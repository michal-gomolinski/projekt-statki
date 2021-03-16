package StatkiKlient;

import java.io.Serializable;

/**
 * Klasa tworzaca obiekty, ktore chcemy ustawic na plansze
 */
public class Statek implements Serializable {
    /** zycia statku*/
    int life;
    /** rozmiar statku*/
    int rozmiar;

    /**
     * konstruktor ustawiający wartości obiektu
     * @param rozmiar - rozmiar statku
     */
    Statek(int rozmiar){

        this.life = rozmiar;
        this.rozmiar = rozmiar;
    }
}
