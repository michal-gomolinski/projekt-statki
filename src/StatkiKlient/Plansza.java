package StatkiKlient;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Klasa plansza zawiera informacje o planszach graczy
 * i zapewnia metody pozwalajace dzialanie na tych planszach
 * i modyfikacje informacji o planszach
 */
public class Plansza implements Serializable {

    /** Tablica obiektow klasy Statek o rozmiarach odpowiadajacej planszy*/
    Statek planszaStatkow[][];
    /** Tablica boolean zawierajaca informacje o tym czy pole zostalo zaatakowane o rozmiarach odpowiadajacej planszy*/
    boolean czyPoleZaatakowane[][];

    /** Tablica zawierajaca informacje pomocnicze o tym czy na
     * tym polu mozna ustawic Statek pionowo i o jakich rozmiarach mozna postawic*/
    int moznaPostawicPionowo[][];
    /** Tablica zawierajaca informacje pomocnicze o tym czy na
     * tym polu mozna ustawic Statek poziomo i o jakich rozmiarach mozna postawic*/
    int moznaPostawicPoziomo[][];
    /** Lista statkow, ktore jeszcze nie zostaly ustawione na planszy*/
    LinkedList<Statek> listyStatkow[];

    /** ilosc nie ustawionych statkow*/
    int iloscStatkow;
    /** ilosc zatopionych statkow*/
    int iloscZatopionych;

    /** ilosc wykonanych atakow na plansze przeciwnika*/
    int iloscStrzalow;
    /** ilosc trafionych statkow przeciwnika */
    int iloscTrafien;

    /** flaga okreslajaca ruch gracza*/
    volatile int ruch;

    /**
     * Konstruktor ustawiajacy wartosci poczatkowe
     */
    Plansza(){
        reset();
    }

    void reset(){
        ruch = 1;
        iloscStatkow = 10;
        planszaStatkow = new Statek[10][10];
        czyPoleZaatakowane = new boolean[10][10];
        listyStatkow = new LinkedList[5];
        moznaPostawicPionowo = new int[10][10];
        moznaPostawicPoziomo = new int[10][10];
        iloscZatopionych = 0;
        iloscStrzalow = 0;
        iloscTrafien = 0;

        for(int i = 0;i < 4 ;i++){
            listyStatkow[i] = new LinkedList<Statek>();
            for (int j = 0;j < 4 - i;j++){
                listyStatkow[i].add(new Statek(i + 1));
            }
        }

        for(int i = 0;i<10;i++)
        {
            for(int j = 0;j<10;j++)
            {
                planszaStatkow[i][j] = null;
                czyPoleZaatakowane[i][j] = false;
            }
        }
    }

    /**
     * Poniewaz tworzac watki przekazujemy referencje na obiekty klasy Plansza
     * aby skopiowac inny obiekt tej samej klasy zachowujac ta sama referencje
     * pomiedzy watkami uzywamy tej metody
     * @param doKopiowania referencja na obiekt ktory chcemy skopiowac
     */
    void hardCopy(Plansza doKopiowania){
        planszaStatkow = doKopiowania.planszaStatkow;
        czyPoleZaatakowane = doKopiowania.czyPoleZaatakowane;

        moznaPostawicPionowo = doKopiowania.moznaPostawicPionowo;
        moznaPostawicPoziomo = doKopiowania.moznaPostawicPoziomo;
        listyStatkow = doKopiowania.listyStatkow;

        iloscStatkow = doKopiowania.iloscStatkow;
        iloscZatopionych = doKopiowania.iloscZatopionych;

        iloscStrzalow = doKopiowania.iloscStrzalow;
        iloscTrafien = doKopiowania.iloscTrafien;

        ruch = doKopiowania.ruch;
    }

    /**
     * Metoda, ktora umozliwia ustawienie statku na planszy
     * @param rozmiar - rozmiar statku, ktory chcemy ustawic
     * @param x - numer kolumny, do ktorej chcemy wstawic statek
     * @param y - numer wierszu, do ktorego chcemy wstawic statek
     * @param poziomo - true - statek chcemy ustawic poziomo, false - statek chcemy ustawic pionowo
     * @return - zwracamy rezultat wykonania metody w postaci Stringa
     */
    String dodajStatek(int rozmiar, int x, int y, boolean poziomo){
        sprawdz();

        char litera = (char) (x + 65);
        int liczba = y + 1;

        if(listyStatkow[rozmiar - 1].size() == 0)
            return "Nie masz wiecej takich statkow";

        Statek statek = listyStatkow[rozmiar - 1].get(0);

        if(statek.life <= this.moznaPostawicPoziomo[y][x] && poziomo){
            for(int i = 0;i < statek.rozmiar;i ++)
                planszaStatkow[x + i][y] = statek;
            listyStatkow[rozmiar - 1].pop();
            iloscStatkow--;
        }

        if(statek.life <= this.moznaPostawicPionowo[y][x] && !poziomo){
            for(int i = 0;i < statek.rozmiar;i ++)
                planszaStatkow[x][y + i] = statek;
            listyStatkow[rozmiar - 1].pop();
            iloscStatkow--;
        }

        else
            return "Nie mozesz tu tego postawic";

        sprawdz();
        return "Postawiles na: " + litera + " " + liczba + " statek z: " + rozmiar +" masztami";
    }

    /**
     * Metoda ustawiajaca wartosci dla tabel moznaPostawicPionowo, moznaPostawicPoziomo
     */
    void sprawdz() {
        for(int i = 0;i<10;i++)
        {
            for(int j = 0;j<10;j++)
            {
                moznaPostawicPionowo[i][j] = czyMoznaPostawic(i,j);
                moznaPostawicPoziomo[i][j] = czyMoznaPostawicPoziomo(i,j);
            }
        }

    }

    /**
     * Metoda umozliwiajaca atakowanie planszy przeciwnika
     * @param x - numer kolumny, do ktorej chcemy wstawic statek
     * @param y - numer wierszu, do ktorego chcemy wstawic statek
     * @param plansza - referencja na atakowana plansze
     * @return - zwracamy rezultat wykonania metody w postaci Stringa
     */
    String atak(int x, int y,Plansza plansza){

        char litera = (char) (x + 65);
        int liczba = y + 1;

        if(plansza.czyPoleZaatakowane[x][y])
            return("brak");
        if(this.ruch == 1){
            this.ruch = 2;
            plansza.ruch = 2;
        }
        else if(this.ruch == 2){
            this.ruch = 1;
            plansza.ruch = 1;
        }

        iloscStrzalow ++;

        plansza.czyPoleZaatakowane[x][y] = true;
        if(plansza.planszaStatkow[x][y]!= null){
            if(plansza.planszaStatkow[x][y].life == 1){
                System.out.println(litera + " " + liczba + " Trafiony zatopiony");
                plansza.planszaStatkow[x][y].life--;
                iloscZatopionych ++;
                iloscTrafien ++;
                return(litera + " " + liczba + " Trafiony zatopiony");

            }
            else if(plansza.planszaStatkow[x][y].life > 1){
                System.out.println(litera + " " + liczba + " Trafiony");
                plansza.planszaStatkow[x][y].life--;
                iloscTrafien ++;

                return(litera + " " + liczba + " Trafiony");
            }
        }
        else{
            System.out.println(litera + " " + liczba + " Chybiony");
            return(litera + " " + liczba + " Chybiony");
        }
        return("brak");
    }

    /**
     * Metoda sprawdzajaca jakiego rozmiaru statek mozna postawic pionowo na danym polu
     * @param x - numer sprawdzanek kolumny
     * @param y - numer sprawdzanej wierszu
     * @return - rozmiar statku, ktorego mozna postawic, gdy 0 nie mozna postawic
     */
    int czyMoznaPostawic(int x, int y){
        int i , z;
        int ret = -1;
        boolean mozna = true;

        if(x == 0){
            i = 0;
        }
        else
            i = x - 1;
        if(y == 0){
            z = 0;
        }
        else
            z = y - 1;
        for(;i < 10;i++){
            for(int j = z ; j < y + 2;j++){
                if(j == 10)
                    continue;
                if(planszaStatkow[j][i]!=null && mozna)
                {
                    ret --;
                    mozna = false;
                }

            }
            if(!mozna || ret == 4){
                break;
            }

            ret++;
        }
        return ret;
    }
    /**
     * Metoda sprawdzajaca jakiego rozmiaru statek mozna postawic poziomo na danym polu
     * @param x - numer sprawdzanek kolumny
     * @param y - numer sprawdzanej wierszu
     * @return - rozmiar statku, ktorego mozna postawic, gdy 0 nie mozna postawic
     */
    int czyMoznaPostawicPoziomo(int x, int y){
        int j , z;
        int ret = -1;
        boolean mozna = true;

        if(x == 0){
            z = 0;
        }
        else
            z = x - 1;
        if(y == 0){
            j= 0;
        }
        else
            j = y - 1;
        for(; j < 10;j++){
            for(int i = z;i < x + 2;i++){
                if(i == 10)
                    continue;
                if(planszaStatkow[j][i]!=null && mozna)
                {
                    ret --;
                    mozna = false;
                }

            }
            if(!mozna || ret == 4){
                break;
            }

            ret++;
        }
        return ret;
    }



}
