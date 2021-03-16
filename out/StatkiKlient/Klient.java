package StatkiKlient;

import java.net.*;
import java.io.*;

/**
 * Klasa obslugujaca przeciwnika w trybie multiplayer
 */
public class Klient extends Thread{
    /** Referencja na plansze przeciwnika*/
    Plansza planszaPrzeciwnika;
    /** Referencja na plansze gracza*/
    Plansza planszaGracza;
    /** Referencja na obiekt klasy RysujPlansze*/
    RysujPlansze plansza;
    DataInputStream in;

    /**
     * Konstruktor klasy ustawiajacy poczatkowe wartosci obiektu
     * @param planszaKomputera
     * @param planszaGracza
     * @param plansza
     */
    Klient(Plansza planszaKomputera, Plansza planszaGracza,RysujPlansze plansza){
        this.planszaPrzeciwnika = planszaKomputera;
        this.planszaGracza = planszaGracza;
        this.plansza = plansza;
    }

    /**
     * Metoda zapewniajaca polaczenie aplikacji z klientem
     */
    void serwer(){
        try {
            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Rozpoczeto probe hostowania";
            plansza.repaint();

            plansza.socket = plansza.server.accept();

            OutputStream out = plansza.socket.getOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(planszaGracza);

            objOut.flush();

            InputStream in = plansza.socket.getInputStream();
            ObjectInputStream objIn = new ObjectInputStream(in);

            planszaPrzeciwnika.hardCopy((Plansza) objIn.readObject()) ;

            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Polaczono z klientem, nastapila wymiana informacji";
            plansza.repaint();

        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Hostowanie nieudane";
            plansza.repaint();
        }
    }
    /**
     * Metoda zapewniajaca polaczenie aplikacji z serwerem
     */
    void klient(){
        try {
            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Szukam serwera";
            plansza.repaint();

            plansza.socket = new Socket(plansza.host,5000);
            InputStream in = plansza.socket.getInputStream();
            ObjectInputStream objIn = new ObjectInputStream(in);

            planszaPrzeciwnika.hardCopy((Plansza) objIn.readObject());

            OutputStream out = plansza.socket.getOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(planszaGracza);
            objOut.flush();

            planszaGracza.ruch = 2;

            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Polaczono z serwerem";
            plansza.repaint();

        } catch (IOException | ClassNotFoundException e) {
            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Polaczenie nieudane";
            plansza.repaint();
        }
    }

    /**
     * Metoda obslugujaca komunikacje gracza z przeciwnikiem
     */
    @Override
    public void run() {
        if(plansza.serwer)
            serwer();
        else
            klient();

        while(planszaPrzeciwnika.iloscStatkow != 10) {
                try {
                    in = new DataInputStream(plansza.socket.getInputStream());
                    int cord = in.readInt();
                    System.out.println(cord);
                    System.out.println((cord/10)+" "+(cord%10));
                    plansza.przeciwnikOstatniRuch = planszaPrzeciwnika.atak(cord/10,cord%10,planszaGracza);


                    plansza.repaint();
                } catch (IOException e) {
                }
            if(planszaGracza.iloscZatopionych == 10) {
                plansza.graczWygral();
                plansza.pseudoLogDwa = plansza.pseudoLog;
                plansza.pseudoLog = "Wygrales";
                plansza.repaint();

                break;
            }
            if(planszaPrzeciwnika.iloscZatopionych == 10){
                plansza.przeciwnikWygral();
                plansza.pseudoLogDwa = plansza.pseudoLog;
                plansza.pseudoLog = "Przegrales";
                plansza.repaint();

                break;
            }
                    planszaGracza.ruch = 1;
        }
        plansza.multiplayer = false;

        plansza.pseudoLogDwa = plansza.pseudoLog;
        plansza.pseudoLog = "Koncze polaczenie";
        plansza.repaint();
    }

}
