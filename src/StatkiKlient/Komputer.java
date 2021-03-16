package StatkiKlient;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Klasa odpowiedzialna za obsluge przeciwnika w trybie singleplayer
 */
public class Komputer extends Thread{

    /** Referencja na plansze komputera*/
    Plansza planszaKomputera;
    /** Referencja na plansze gracza*/
    Plansza planszaGracza;
    /** Referencja na obiekt klasy RysujPlansze*/
    RysujPlansze plansza;
    Semaphore sem;

    int id;

    Random generator;

    /**
     * Metoda przypisujaca poczatkowe wartosci obiektowi
     * @param planszaKomputera
     * @param planszaGracza
     * @param plansza
     */
    Komputer(Plansza planszaKomputera, Plansza planszaGracza,RysujPlansze plansza,Semaphore sem, int id){
        this.planszaKomputera = planszaKomputera;
        this.planszaGracza = planszaGracza;
        generator = new Random();
        this.plansza = plansza;
        this.sem = sem;
        this.id =id;
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
                try {
                    sem.acquire();
                } catch (InterruptedException e) {
                }
                int ctrl = planszaKomputera.iloscStrzalow;
                while(ctrl == planszaKomputera.iloscStrzalow && planszaGracza.iloscStatkow != 10) {
                    if(id == 1)
                    plansza.przeciwnikOstatniRuch = planszaKomputera.atak(generator.nextInt(10), generator.nextInt(10), planszaGracza);
                    else
                        plansza.graczOstatniRuch = planszaKomputera.atak(generator.nextInt(10), generator.nextInt(10), planszaGracza);
                }
                if(planszaGracza.iloscZatopionych == 10) {
                    plansza.pseudoLogDwa = plansza.pseudoLog;
                    plansza.pseudoLog = "Komputer "+ id + " wygrał";
                    plansza.reset();
                    //break;
                }
                if(planszaKomputera.iloscZatopionych == 10){
                    plansza.pseudoLogDwa = plansza.pseudoLog;
                    plansza.pseudoLog = "Komputer "+ id + " przegrał";
                    plansza.reset();
                    //break;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sem.release();

                plansza.repaint();
            }
            System.out.println("koniec");
        }
        plansza.repaint();


    }
}
