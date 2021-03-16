package StatkiKlient;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static javax.swing.JOptionPane.*;

/**
 * Klasa odpowiedzialna za rysowanie okna gry zapewniajaca logiczna obsluge
 * gry i jego interfejsu graficznego
 */
public class RysujPlansze extends JPanel implements MouseListener , ActionListener {
    /** Referencja na obiekt planszy Gracza*/
    Plansza planszaGracz;
    /** Referencja na obiekt planszy Gracza*/
    Plansza planszaPrzeciwnik;

    /** Przycisk, ktory w fazie ustawiania zapewnia mozliwosc stawiania statkow poziomo, lub pionowo w zaleznosci
     * od preferencji uzytkownika*/
    JButton poziomoButton;
    /** Przycisk, ktory w fazie ustawiania wybiera do ustawienia statek jedno masztowy*/
    JButton wybierzJedenMaszt;
    /** Przycisk, ktory w fazie ustawiania wybiera do ustawienia statek dwu masztowy*/
    JButton wybierzDwaMaszt;
    /** Przycisk, ktory w fazie ustawiania wybiera do ustawienia statek trzy masztowy*/
    JButton wybierzTrzyMaszt;
    /** Przycisk, ktory w fazie ustawiania wybiera do ustawienia statek cztero masztowy*/
    JButton wybierzCzteryMaszt;

    /** Przycisk, ktory zapewnia mozliwosc resetu rozgrywki*/
    JButton reset;
    /** Przycisk, ktory zapewnia mozliwosc gry z komputerem jesli statki zostaly rozstawione*/
    JButton single;
    /** Przycisk, ktory zapewnia mozliwosc gry z innym graczem jesli statki zostaly rozstawione*/
    JButton multi;
    /** Przycisk, ktory zapewnia mozliwosc wyjscia z aplikacji*/
    JButton wyjdz;
    /** Przycisk, ktory zapewnia mozliwosc przestania szukania klienta i zamkniecie socket servera, gdy zaden klient sie
     * polaczyl*/
    JButton rozlacz;

    /** Obszar tekstu zawierajacy informacje o ruchach gracza*/
    JTextArea gracz;
    /** Obszar tekstu zawierajacy informacje o ruchach przeciwnika*/
    JTextArea przeciwnik;
    JTextArea tutorial;
    /** Obszar tekstu zawierajacy informacje o dzialaniu gry*/
    JTextArea faza;

    /** Watek obslugujacy ruchy przeciwnika w grze singleplayer*/
    Przeciwnik przeciwnikT;
    /** Watek obslugujacy ruchy przeciwnika w grze multiplayer*/
    Klient klientT;

    /** Zmienna pomocnicza okreslajaca wybrany rozmiar statku*/
    int wybranyRozmiar;

    /** Zmienna pomocnicza porzadane polozenie statku*/
    boolean poziomo;
    /** Zmienna pomocnicza okreslajaca faze rozgrywki, true - gra jest w fazie stawiania*/
    boolean stawianie;
    /** Zmienna pomocnicza tryb rozgrywki*/
    boolean singlePlayer;
    /** Zmienna pomocnicza tryb rozgrywki*/
    boolean multiplayer;
    /** Zmienna pomocnicza okreslajaca typ polaczenia, true - hostuje*/
    boolean serwer;

    /** Referencja do socketu*/
    Socket socket;
    /** Referencja do serversocketu (gdy nie hostuje, wskazuje na null)*/
    ServerSocket server;

    /** String zawierajacy informacje o ostatnim ruchu gracza*/
    String graczOstatniRuch;
    /** String zawierajacy informacje o ostatnim ruchu przeciwnika*/
    String przeciwnikOstatniRuch;
    String pseudoLog;
    String pseudoLogDwa;
    String host;

    /**
     * Kosntruktor , ustawiajacy wartosci poczatkowe Gry
     */
    RysujPlansze(){

        planszaGracz = new Plansza();
        planszaPrzeciwnik = new Plansza();

        wybranyRozmiar = 1;

        poziomo = false;
        singlePlayer = false;
        stawianie = true;
        multiplayer = false;
        serwer = false;

        addMouseListener(this);
        setLayout(null);

        poziomoButton = new JButton("Poziomo");
        add(poziomoButton);
        poziomoButton.addActionListener(this);

        wybierzJedenMaszt = new JButton("Jeden");
        add(wybierzJedenMaszt);
        wybierzJedenMaszt.addActionListener(this);

        wybierzDwaMaszt = new JButton("Dwa");
        add(wybierzDwaMaszt);
        wybierzDwaMaszt.addActionListener(this);

        wybierzTrzyMaszt = new JButton("Trzy");
        add(wybierzTrzyMaszt);
        wybierzTrzyMaszt.addActionListener(this);

        wybierzCzteryMaszt = new JButton("Cztery");
        add(wybierzCzteryMaszt);
        wybierzCzteryMaszt.addActionListener(this);


        single = new JButton("SinglePlayer");
        add(single);
        single.addActionListener(this);

        multi = new JButton("Multiplayer");
        add(multi);
        multi.addActionListener(this);

        reset = new JButton("Reset");
        add(reset);
        reset.addActionListener(this);

        wyjdz = new JButton("Wyjdz");
        add(wyjdz);
        wyjdz.addActionListener(this);

        rozlacz = new JButton("Rozlacz");
        add(rozlacz);
        rozlacz.addActionListener(this);

        Border border = BorderFactory.createBevelBorder(1,Color.lightGray,Color.blue);

        gracz = new JTextArea("Gracz zatopil "+planszaGracz.iloscZatopionych+" Statkow");
        gracz.setBorder(border);
        add(gracz);

        przeciwnik = new JTextArea("Przeciwnik zatopil "+planszaPrzeciwnik.iloscZatopionych+" Statkow");
        przeciwnik.setBorder(border);
        add(przeciwnik);

        faza = new JTextArea("");
        faza.setBorder(border);
        add(faza);

        graczOstatniRuch = "Gracz jeszcze sie nie ruszyl";
        przeciwnikOstatniRuch = "Przeciwnik jeszcze sie nie ruszyl";
        pseudoLogDwa="Zacznij rozstawianie statkow";
        pseudoLog = "";

        host = "localhost";
    }

    /**
     * Przeslonieta metoda paintComponent
     * Odpowiedzialna za rysowanie okna w trakcie gry
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        setBackground(Color.white);

        rysujPlanczeGracza(g);
        rysujPlanszePrzeciwnika(g);
        ustawPozycje();

        if(stawianie){
            if(poziomo)
                poziomoButton.setText("Poziomo");
            else
                poziomoButton.setText("Pionowo");
        }
        int graczSkutecznosc = (int)((float)planszaGracz.iloscTrafien / (float)planszaGracz.iloscStrzalow * 100);
        int przeciwnikSkutecznosc = (int)((float)planszaPrzeciwnik.iloscTrafien / (float)planszaPrzeciwnik.iloscStrzalow * 100);

        gracz.setText("Gracz zatopil "+planszaGracz.iloscZatopionych+" Statkow\nSkutecznosc: " + planszaGracz.iloscTrafien + "/"  + planszaGracz.iloscStrzalow + " " + graczSkutecznosc + "%\n"
        +graczOstatniRuch);
        przeciwnik.setText("Przeciwnik zatopil "+planszaPrzeciwnik.iloscZatopionych+" Statkow\nSkutecznosc: " + planszaPrzeciwnik.iloscTrafien + "/"  + planszaPrzeciwnik.iloscStrzalow + " " + przeciwnikSkutecznosc + "%\n"
        +przeciwnikOstatniRuch);

        wybierzJedenMaszt.setText("Jeden: "+ planszaGracz.listyStatkow[0].size());
        wybierzDwaMaszt.setText("Dwa: "+ planszaGracz.listyStatkow[1].size());
        wybierzTrzyMaszt.setText("Trzy: "+ planszaGracz.listyStatkow[2].size());
        wybierzCzteryMaszt.setText("Cztery: "+ planszaGracz.listyStatkow[3].size());

        faza.setText(pseudoLogDwa+ "\n" +pseudoLog);
    }

    /**
     * Metoda ustawiajaca pozycje poszczegolnych elementow w oknie
     */
    public void ustawPozycje(){
        poziomoButton.setBounds(0, getHeight() - getHeight()/10, getWidth()/5, getHeight()/10);
        wybierzJedenMaszt.setBounds(getWidth() - 4 * getWidth()/5, getHeight() - getHeight()/10, getWidth()/5, getHeight()/10);
        wybierzDwaMaszt.setBounds(getWidth() - 3 * getWidth()/5, getHeight() - getHeight()/10, getWidth()/5, getHeight()/10);
        wybierzTrzyMaszt.setBounds(getWidth() - 2 * getWidth()/5, getHeight() - getHeight()/10, getWidth()/5, getHeight()/10);
        wybierzCzteryMaszt.setBounds(getWidth() - getWidth()/5, getHeight() - getHeight()/10, getWidth()/5, getHeight()/10);

        single.setBounds(getWidth()/2 - 75, 10, 150, 20);
        multi.setBounds(getWidth()/2 - 75, 40, 150, 20);
        reset.setBounds(getWidth()/2 - 75, 70, 150, 20);
        wyjdz.setBounds(getWidth()/2 - 75, 160, 150, 20);
        rozlacz.setBounds(getWidth()/2 - 75, 130, 150, 20);



        gracz.setBounds(0, getHeight()/2 + 5, getHeight()/2 - 5, 60);
        przeciwnik.setBounds(getWidth() - (getHeight()/20) * 10, getHeight()/2 + 5, getHeight()/2 - 5, 60);
        faza.setBounds(0, getHeight()/2 + 65, getWidth(), 60);
    }

    /**
     * Gdy gracz zatopil 10 statkow, wygral i ma mozliwosc grania dalej poprzez restartowanie
     * rozgrywki, lub wyjscie z aplikacji
     */
    public void graczWygral(){
        int result = showConfirmDialog(this,
                "Wygrales czy chcesz grac dalej?",null, YES_NO_OPTION);
        if(result == YES_OPTION) {
            reset();
        }
        else
            System.exit(0);
        remove(result);

    }
    /**
     * Gdy Przeciwnik zatopil 10 statkow, gracz przegral i ma mozliwosc grania dalej poprzez restartowanie
     * rozgrywki, lub wyjscie z aplikacji
     */
    public void przeciwnikWygral(){

        int result = showConfirmDialog(null,
                "Przegrales czy chcesz grac dalej?",null, YES_NO_OPTION);
        if(result == YES_OPTION) {
            reset();
        }
        else
            System.exit(0);
        remove(result);
    }

    /**
     * Metoda pytajaca sie uzytkownika jak chce sie polaczyc z przeciwnikiem,
     * jesli na odpowiedz hostujesz odpowiada "tak", znaczy to ze aplikacja ma dzialac jako serwer
     * jesli odpowiada "nie", znaczy to ze uzytkownik chce szukac dostepnych serwerow
     * @return - zwraca true jesli odpowiedz "tak", false jesli "nie"
     */
    public boolean metodaPolaczenia(){
        int result = showConfirmDialog(this,
                "Hostujesz?","Metoda polaczenia", YES_NO_OPTION);
        if(result == YES_OPTION) {
            return true;
        }
        else
            return false;

    }

    /**
     * Metoda odpowiedzialna za rysowanie planszy gracza w lewym gornym rogu
     * @param g
     */
    public void rysujPlanczeGracza(Graphics g){
        if(planszaGracz.iloscStatkow==0)
            stawianie = false;
        planszaGracz.sprawdz();
        int dim;
        Graphics2D g2 = (Graphics2D) g;
        if(getHeight() <= getWidth())
            dim = getHeight()/20;
        else
            dim = getWidth()/20;
        planszaGracz.sprawdz();
        for(int i = 0;i<10;i++){
            for(int j = 0;j<10;j++){
                if(planszaGracz.planszaStatkow[i][j]!=null){
                    if(planszaGracz.planszaStatkow[i][j].life!=0)
                        g.setColor(Color.red);
                    else
                        g.setColor(Color.gray);
                }
                else if(stawianie && planszaGracz.moznaPostawicPoziomo[j][i] >= wybranyRozmiar && poziomo)
                    g.setColor(Color.green);
                else if(stawianie && planszaGracz.moznaPostawicPionowo[j][i] >= wybranyRozmiar && !poziomo)
                    g.setColor(Color.green);
                else
                    g.setColor(Color.white);
                g.fillRect( i*dim,j*dim,dim,dim);
                if(planszaGracz.czyPoleZaatakowane[i][j]) {
                    g.setColor(Color.black);
                    Line2D lin = new Line2D.Float( dim* i, j*dim, dim* (i+1), (j+1)*dim);
                    g2.draw(lin);
                    Line2D lin2 = new Line2D.Float( dim* i, (j+1)*dim,  dim* (i+1), j*dim);
                    g2.draw(lin2);
                }
            }

        }
        for(int i = 0; i <= 10;i++){
            g.setColor(Color.black);
            Line2D lin = new Line2D.Float(0, i*dim, 10*dim, i*dim);
            g2.draw(lin);
            Line2D lin2 = new Line2D.Float(i*dim, 0, i*dim, 10*dim);
            g2.draw(lin2);
        }

    }
    /**
     * Metoda odpowiedzialna za rysowanie planszy przeciwnika w prawym gornym rogu
     * @param g
     */
    public void rysujPlanszePrzeciwnika(Graphics g){

        int dim;
        Graphics2D g2 = (Graphics2D) g;

        if(getHeight() <= getWidth())
            dim = getHeight()/20;
        else
            dim = getWidth()/20;
        int start = getWidth() - dim * 10;

        for(int i = 0;i<10;i++){
            for(int j = 0;j<10;j++){


                if(planszaPrzeciwnik.planszaStatkow[i][j]!=null && planszaPrzeciwnik.czyPoleZaatakowane[i][j])
                    if(planszaPrzeciwnik.planszaStatkow[i][j].life!=0)
                        g.setColor(Color.red);
                    else
                        g.setColor(Color.gray);
                else
                    g.setColor(Color.white);
                g.fillRect( start + i*dim,j*dim,dim,dim);
                if(planszaPrzeciwnik.czyPoleZaatakowane[i][j]) {
                    g.setColor(Color.black);
                    Line2D lin = new Line2D.Float(start + dim* i, j*dim, start + dim* (i+1), (j+1)*dim);
                    g2.draw(lin);
                    Line2D lin2 = new Line2D.Float(start + dim* i, (j+1)*dim, start + dim* (i+1), j*dim);
                    g2.draw(lin2);
                }
            }

        }
        for(int i = 0; i <= 10;i++){
            g.setColor(Color.black);
            Line2D lin = new Line2D.Float(getWidth() - 10 * dim, i*dim, getWidth(), i*dim);
            g2.draw(lin);
            Line2D lin2 = new Line2D.Float(getWidth() - i*dim, 0, getWidth() - i*dim, 10*dim);
            g2.draw(lin2);
        }
    }

    /**
     * Przeslonieta metoda
     * zapewnia mozliwosc ustawiania statkow i atakowania planszy przeciwnika
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(stawianie)
            postaw(e);
        else
            if(planszaGracz.ruch == 1)
                zaatakuj(e);
        repaint();
    }

    /**
     * Metoda obslugujaca stawianie statkow na plansze gracza
     * oblicza wcisniete pole i wywoluje dla tego pola metode DodajStatek() dla planszy gracza
     * @param e
     */
    void postaw(MouseEvent e){
        int dim;
        int x = e.getX();
        int y = e.getY();

        if(getHeight() <= getWidth())
            dim = getHeight()/20;
        else
            dim = getWidth()/20;
        for(int i = 0;i<10;i++){
            for(int j=0;j<10;j++){
                if(( x >= dim * i &&  x <= dim * (i +1) ) && ( y >= dim * j && y <= dim * (j +1))){
                    if(planszaGracz.planszaStatkow[i][j]!=null)
                        System.out.println("Statek");
                    pseudoLogDwa = pseudoLog;
                    pseudoLog = planszaGracz.dodajStatek(wybranyRozmiar,i,j,poziomo);
                }
            }
        }
    }

    /**
     * Metoda obslugujaca atakowanie planszy przeciwnika
     * oblicza wcisniete pole i wywoluje dla tego pola metode atak() planszy gracza na plansze przeciwnika
     * , jesli gra w trybie multiplayer wysyla informacje o ataku do przeciwnika
     * @param e
     */
    void zaatakuj(MouseEvent e){
        int dim;
        int x = e.getX();
        int y = e.getY();

        if(getHeight() <= getWidth())
            dim = getHeight()/20;
        else
            dim = getWidth()/20;
        int start = getWidth() - dim * 10;
        for(int i = 0;i < 10;i++){
            for(int j = 0 ;j < 10;j++){
                if(( x >= start + dim * i &&  x <= start + dim * (i +1) ) && ( y >= dim * j && y <= dim * (j +1))){
                    String temp = "brak";
                    if(planszaGracz.ruch==1)
                        temp = planszaGracz.atak(i,j,planszaPrzeciwnik);
                    if(!temp.equals("brak") && multiplayer){
                        wyslij(i,j);
                    }
                    graczOstatniRuch = temp;
                }
            }
        }
    }

    /**
     * wyslanie informacji o ataku do polaczonego przeciwnika
     * @param x - numer kolumny atakowanej
     * @param y - numer wiersza atakowanego
     */
    void wyslij(int x, int y){
        DataOutputStream out = null;
        try {
           out = new DataOutputStream(socket.getOutputStream());
            x = x*10;
            x = x + y;
            System.out.println(x);
            out.writeInt(x);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Metoda resetujaca gre
     */
    void reset()  {
        planszaGracz = new Plansza();
        planszaPrzeciwnik = new Plansza();
        wybranyRozmiar = 1;
        host = "localhost";

        if(multiplayer){
            try {
                if(!serwer)
                    socket.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        graczOstatniRuch = "Gracz jeszcze sie nie ruszyl";
        przeciwnikOstatniRuch = "Przeciwnik jeszcze sie nie ruszyl";

        poziomo = false;
        singlePlayer = false;
        stawianie = true;
        multiplayer = false;

        pseudoLogDwa = pseudoLog;
        pseudoLog = "Zrestartowano Gre";

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    /**
     * Przeslonieta metoda zapewniajaca obsluge przyciskow
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if(source == poziomoButton)
        {
            poziomo = !poziomo;
            if(poziomo)
                poziomoButton.setText("Poziomo");
            else
                poziomoButton.setText("Pionowo");
        }
        if(source == wybierzJedenMaszt)
            wybranyRozmiar = 1;

        if(source == wybierzDwaMaszt)
            wybranyRozmiar = 2;

        if(source == wybierzTrzyMaszt)
            wybranyRozmiar = 3;

        if(source == wybierzCzteryMaszt)
            wybranyRozmiar = 4;
        if(source == reset)
            reset();
        if(source == single && !singlePlayer){
            System.out.println("asd");
            if(planszaGracz.iloscStatkow != 0)
                System.out.println("Ustaw statki");
            else{
                singlePlayer = true;
                przeciwnikT = new Przeciwnik(planszaPrzeciwnik,planszaGracz,this);
                przeciwnikT.start();

                pseudoLogDwa = pseudoLog;
                pseudoLog = "Rozpoczales gre z komputerem";
            }
        }
        if(source == wyjdz)
            System.exit(0);

        if(source == multi && !multiplayer){

            if(planszaGracz.iloscStatkow != 0)
                System.out.println("Ustaw statki");
            else{

                serwer = metodaPolaczenia();

                multiplayer = true;
                if(serwer)
                    serwer();
                else
                    klient();

                    klientT = new Klient(planszaPrzeciwnik,planszaGracz,this);
                    klientT.start();

            }
        }
        if(source  == rozlacz) {
            multiplayer = false;
            singlePlayer = false;
            host = "localhost";
            try {
                server.close();
                socket.close();
            } catch (IOException | NullPointerException e) {
            }
        }

        System.out.println(wybranyRozmiar);
        repaint();
    }

    /**
     * Metoda wywolywana, gdy uzytkownik chce postawic serwer
     */
    void serwer(){
        try {
            server = new ServerSocket(5000);
            InetAddress adres = InetAddress.getLocalHost();
            String hostName = adres.getHostName();
            showMessageDialog(this, "Podziel sie swoja nazwa z klientem\nTwoja Nazwa: "+hostName,"Nazwa",2);

        } catch (IOException | NullPointerException e) {
        }

    }

    /**
     * Metoda wywolywana, gdy uzytkownik chce polaczyc sie z serwerem
     */
    void klient(){
        String temp = JOptionPane.showInputDialog(this,"Podaj nazwe hosta: ");
        if(temp != null)
            host = temp;
    }
}