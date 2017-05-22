/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package niti;

import domen.Clan;
import domen.Mesto;
import domen.Trener;
import domen.Trening;
import greske.SQLObjekatPostojiException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kontroler.Kontroler;
import request.RequestObject;
import response.ResponseObject;
import status.EnumResponseStatus;
import util.Akcije;

/**
 *
 * @author Milan
 */
public class KlijentNit implements Runnable {

    private final Socket socket;

    public KlijentNit(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            obradiKlijenta(socket);

        } catch (IOException ex) {
            Logger.getLogger(KlijentNit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KlijentNit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void obradiKlijenta(Socket socket) throws IOException, ClassNotFoundException {
        while (true) {
            try {
                System.out.println("Cekam zahtev od klijenta");
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Object obj = in.readObject();
                RequestObject request = (RequestObject) obj;

                ResponseObject response = obradiZahtev(request);

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(response);
                out.flush();
            } catch (SocketException se) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private ResponseObject obradiZahtev(RequestObject request) {
        ResponseObject response = new ResponseObject();

        int akcija = request.getAction();
        Clan clan;
        Trener trener;
        Trening trening;
        switch (akcija) {
            case Akcije.VRATI_SVA_MESTA:
                List<Mesto> svaMesta = new ArrayList<>();

                try {
                    Kontroler.getInstance().vratiMesta(svaMesta);
                    response.setObject(svaMesta);
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;
            case Akcije.UBACI_CLANA:
                clan = (Clan) request.getObject();
                try {
                    Kontroler.getInstance().ubaciClana(clan);
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                } catch (SQLObjekatPostojiException ex) {
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;
            case Akcije.OBRISI_CLANA:
                clan = (Clan) request.getObject();
                try {
                    Kontroler.getInstance().obrisi(clan);
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;
            case Akcije.VRATI_SVE_CLANOVE:
                try {
                    response.setObject(Kontroler.getInstance().vratiSveClanove());
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;
            case Akcije.DODAJ_CLANA_NA_TRENING:
                Object obj[] = (Object[]) request.getObject();
                clan = (Clan) obj[0];
                trening = (Trening) obj[1];
                try {
                    Kontroler.getInstance().ubaciNaTrening(clan, trening);
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.VRATI_MAX_ID:
                try {
                    response.setObject(Kontroler.getInstance().vratiMaxId());
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.USPOSTAVI_KONEKCIJU_NA_BAZU:
                try {
                    Kontroler.getInstance().uspostaviKonekcijuNaBazu();
                    response.setStatus(EnumResponseStatus.OK);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.PROMENI_CLANOVE:
                try {
                    if (request.getObject() instanceof List) {
                        List<Clan> clanovi = (List<Clan>) request.getObject();
                        response.setObject(Kontroler.getInstance().promeni(clanovi));
                        response.setStatus(EnumResponseStatus.OK);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.VRATI_SVE_TRENERE_ZA_TRENING:
                try {
                    trening = (Trening) request.getObject();
                    response.setObject(Kontroler.getInstance().vratiSveTrenere(trening));
                    response.setStatus(EnumResponseStatus.OK);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.VRATI_SVE_CLANOVE_ZA_TRENING:
                try {
                    trening = (Trening) request.getObject();
                    response.setObject(Kontroler.getInstance().vratiSveClanove(trening));
                    response.setStatus(EnumResponseStatus.OK);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;
            case Akcije.VRATI_DATUME:
                try {
                    response.setObject(Kontroler.getInstance().vratiSveDatume());
                    response.setStatus(EnumResponseStatus.OK);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

            case Akcije.VRATI_VREMENA:
                try {
                    LocalDate datum = (LocalDate) request.getObject();
                    response.setObject(Kontroler.getInstance().vratiSvaVremena(datum));
                    response.setStatus(EnumResponseStatus.OK);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(EnumResponseStatus.ERROR);
                    response.setMessage(ex.getMessage());
                }
                return response;

//            case Akcije.UBACI_TRENERA:
//                trener = (Trener) request.getObject();
//                try {
//                    Kontroler.getInstance().u;
//                    response.setStatus(EnumResponseStatus.OK);
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    response.setStatus(EnumResponseStatus.ERROR);
//                    response.setMessage(ex.getMessage());
//                } catch (SQLObjekatPostojiException ex) {
//                    response.setStatus(EnumResponseStatus.ERROR);
//                    response.setMessage(ex.getMessage());
//                }
//                return response;                
            default:
                response.setMessage("Nije implementirana akcija.");
                response.setStatus(EnumResponseStatus.ERROR);
                return response;
        }
    }

}