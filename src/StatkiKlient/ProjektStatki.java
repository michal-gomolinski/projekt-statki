package StatkiKlient;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Klasa zawierajaca metode main() rowniez odpowiedzialna za UserInterface
 * i menu glowne programu
 * @Autor Michal Gomolinski
 */
public class ProjektStatki extends JFrame implements ActionListener {
    /** Przycisk odpowiedzialny za uruchamianie gry*/
    JButton graj;
    /** Przycisk wyjscia z aplikacji*/
    JButton wylacz;
    /** Przycisk umozliwiajacy zmiane rozdzielczosci*/
    JButton rozdzielczosc;
    /** Przycisk wyswietlajacy informacje o autorze*/
    JButton credits;

    /** Przycisk powrotu do menu glownego*/
    JButton menu;

    /** Obszar zawierajacy tytul programu*/
    JTextPane tytul;

    /** Panel gry */
    JPanel plansza;

    /** Tablica zawierajaca stringi dostepnych rozdzielczosci*/
    final String dostepneRozdzielczoscStr[] = new String[]{"800x600", "1000x750", "1200x900"};
    /** Tablica wymiary dostepnych rozdzielczosci*/
    final Dimension dostepneRozdzielczoscDim[] = new Dimension[]{new Dimension(800,600),new Dimension(1000,750),new Dimension(1200,900)};
    /** Zmienna pomocnicza okreslajaca wybrana rozdzielczosc*/
    int wybranaRozdzielczosc = 0;

    /** Metoda odpowiedzialna za wyjscie z menu glownego do gry*/
    void zacznijGre(){
        remove(graj);
        remove(wylacz);
        remove(rozdzielczosc);
        remove(credits);
        remove(tytul);

        add(menu);

        setBackground(Color.white);

        plansza = new RysujPlansze();
        pack();
        plansza.setSize(this.getContentPane().getSize());
        add(plansza);

        repaint();
    }

    /**
     * Metoda odpowiedzialna za zmiane rozdzielczosci
     */
    void ustawRozdzielczosc(){
        setPreferredSize(dostepneRozdzielczoscDim[wybranaRozdzielczosc]);
        pack();
        Border border = BorderFactory.createLineBorder(Color.darkGray);

        tytul.setBounds(10,10,getContentPane().getWidth() - 20,getContentPane().getHeight()/4);

        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(attribs,getContentPane().getHeight()/7);

        tytul.setEditable(false);
        tytul.setParagraphAttributes(attribs, true);
        tytul.setBorder(border);
        tytul.setBackground(new Color(220,241,246));

        graj.setBounds(10, getContentPane().getHeight()/3, getContentPane().getWidth() - 20, getContentPane().getHeight()/6);
        rozdzielczosc.setBounds(10, getContentPane().getHeight()/2, getContentPane().getWidth() - 20, getContentPane().getHeight()/6);
        credits.setBounds(10, getContentPane().getHeight()/6*4, getContentPane().getWidth() - 20, getContentPane().getHeight()/6);
        wylacz.setBounds(10, getContentPane().getHeight()/6 * 5, getContentPane().getWidth() - 20, getContentPane().getHeight()/6);
        menu.setBounds(getContentPane().getWidth()/2 - 75, 100, 150, 20);

    }
    /**
     * Metoda odpowiedzialna za rysowanie menu glownego
     */
    void menuGlowne(){
        pack();

        tytul.setText("PROJEKT STATKI");
        add(tytul);

        add(graj);
        add(rozdzielczosc);
        add(credits);
        add(wylacz);

    }
    /**
     * Metoda odpowiedzialna za powrot z gry do menu glownego
     */
    void powrot(){
        remove(menu);
        remove(plansza);


        menuGlowne();
        repaint();
    }
    /**
     * Konstruktor odpowiedzialny za wstepne ustawienie okna aplikacji
     */
    public ProjektStatki() {
        setTitle("Statki");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        tytul = new JTextPane();
        graj = new JButton("Graj");
        rozdzielczosc = new JButton("Opcje");
        credits = new JButton("Credits");
        wylacz = new JButton("Wylacz");
        menu = new JButton("Menu");


        graj.addActionListener(this);
        rozdzielczosc.addActionListener(this);
        credits.addActionListener(this);
        wylacz.addActionListener(this);
        menu.addActionListener(this);

        menuGlowne();
        ustawRozdzielczosc();

        setVisible(true);
    }

    /**
     * Przeslonieta metoda zapewniajaca obsluge przyciskow
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == graj)
            zacznijGre();
        if(source == wylacz)
            System.exit(0);
        if(source == credits)
            JOptionPane.showMessageDialog(this, "Wykonal:\nMichal Gomolinski\nWCY18IY1S1");
        if(source == menu)
            powrot();
        if(source == rozdzielczosc){
            String rozd = (String)JOptionPane.showInputDialog(this,"Wybierz Rozdzielczosc:","Rozdzielczosc",JOptionPane.QUESTION_MESSAGE,null,dostepneRozdzielczoscStr,"");
            for(int i = 0;i < 3;i++){
                if(rozd.equals(dostepneRozdzielczoscStr[i]))
                    wybranaRozdzielczosc = i;
                ustawRozdzielczosc();
            }

        }

    }

    /**
     * Metoda main() aplikacji odpowiedzialna za wywolanie okna
     * @param args
     */
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ProjektStatki();
            }
        });
    }
}
