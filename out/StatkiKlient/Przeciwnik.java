package StatkiKlient;

import java.util.Random;

/**
 * Klasa odpowiedzialna za obsluge przeciwnika w trybie singleplayer
 */
public class Przeciwnik extends Thread{

    /** Referencja na plansze komputera*/
    Plansza planszaKomputera;
    /** Referencja na plansze gracza*/
    Plansza planszaGracza;
    /** Referencja na obiekt klasy RysujPlansze*/
    RysujPlansze plansza;

    Random generator;

    /**
     * Metoda przypisujaca poczatkowe wartosci obiektowi
     * @param planszaKomputera
     * @param planszaGracza
     * @param plansza
     */
    Przeciwnik(Plansza planszaKomputera, Plansza planszaGracza,RysujPlansze plansza){
        this.planszaKomputera = planszaKomputera;
        this.planszaGracza = planszaGracza;
        generator = new Random();
        this.plansza = plansza;
    }


    /**
     * Metoda run klasy, kontroluje logike komputera
     */
    @Override
    public void run() {
        if(planszaKomputera.iloscStatkow !=0){
            for(int i = 0;i < 4 ;){
                planszaKomputera.dodajStatek(i + 1, generator.nextInt(10), generator.nextInt(10), generator.nextBoolean());
                if(planszaKomputera.listyStatkow[i].size()==0)
                    i++;
            }

            plansza.pseudoLogDwa = plansza.pseudoLog;
            plansza.pseudoLog = "Komputer rozstawil Statki";
            plansza.repaint();

            while(planszaKomputera.iloscStatkow != 10){
                if(planszaKomputera.ruch == 2)
                    plansza.przeciwnikOstatniRuch = planszaKomputera.atak(generator.nextInt(10), generator.nextInt(10),planszaGracza);
                if(planszaGracza.iloscZatopionych == 10) {
                    plansza.graczWygral();
                    plansza.pseudoLogDwa = plansza.pseudoLog;
                    plansza.pseudoLog = "Wygrales z komputerem";
                    plansza.repaint();
                    break;
                }
                if(planszaKomputera.iloscZatopionych == 10){
                    plansza.przeciwnikWygral();
                    plansza.pseudoLogDwa = plansza.pseudoLog;
                    plansza.pseudoLog = "Przegrales z komputerem";
                    plansza.repaint();
                    break;
                }
            }

        }

        plansza.repaint();

    }
}
