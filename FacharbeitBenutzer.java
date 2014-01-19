import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
 
public class FacharbeitBenutzer {
       
        JFrame benutzerFenster;
        JPanel benutzerPanel;
        JTextArea textBereich_Nachrichten;
        JTextField textFeld_BenutzerNachricht;
        JButton knopf_SendeNachricht;
        JTextField textFeld_Benutzername;
        JScrollPane scrollPanel_Nachrichten;
       
        Socket benutzer;
        PrintWriter schreiber;
        BufferedReader leser;
        
        static String ip;
        
        final int port = 2014;
       
        public static void main(String[] args) {
        	
        	ip=null;
        	System.out.println("Auf welcher IP läuft der Server?");
        	
        	Scanner s = new Scanner(System.in);
        	ip = s.next();
       
        	FacharbeitBenutzer b = new FacharbeitBenutzer();
            b.erstelleOberfläche();
        }
       
        public void erstelleOberfläche() {
                benutzerFenster = new JFrame("Facharbeit-Chat");
                benutzerFenster.setSize(800, 600);
               
                benutzerPanel = new JPanel();
               
                textBereich_Nachrichten = new JTextArea();
                textBereich_Nachrichten.setEditable(false);
               
                textFeld_BenutzerNachricht = new JTextField(38);
                textFeld_BenutzerNachricht.addKeyListener(new EnterVerwalter());
               
                knopf_SendeNachricht = new JButton("Senden");
                knopf_SendeNachricht.addActionListener(new SendenKnopfVerwalter());
               
                textFeld_Benutzername = new JTextField(10);
               
                scrollPanel_Nachrichten = new JScrollPane(textBereich_Nachrichten);
                scrollPanel_Nachrichten.setPreferredSize(new Dimension(700, 500));
                scrollPanel_Nachrichten.setMinimumSize(new Dimension(700, 500));
                scrollPanel_Nachrichten.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPanel_Nachrichten.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);              
               
               
                if(!verbindungZumServer()) {
                        
                }
               
                Thread t = new Thread(new ServerNachrichtenVerwalter());
                t.start();
               
                benutzerPanel.add(scrollPanel_Nachrichten);
                benutzerPanel.add(textFeld_Benutzername);
                benutzerPanel.add(textFeld_BenutzerNachricht);
                benutzerPanel.add(knopf_SendeNachricht);
               
                benutzerFenster.getContentPane().add(BorderLayout.CENTER, benutzerPanel);
               
                benutzerFenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                benutzerFenster.setVisible(true);
        }
       
        public boolean verbindungZumServer() {
                try {
                        benutzer = new Socket(ip, port);
                        leser = new BufferedReader(new InputStreamReader(benutzer.getInputStream()));
                        schreiber = new PrintWriter(benutzer.getOutputStream());
                        fügeTextNachrichtenHinzu("Verbindung zum Server hergestellt!");
                       
                        return true;
                } catch(Exception e) {
                	fügeTextNachrichtenHinzu("Verbindung zum Server konnte nicht hergestellt werden!");
                        e.printStackTrace();
                       
                        return false;
                }
        }
       
        public void sendeNachrichtZumServer() {
                schreiber.println(textFeld_Benutzername.getText() + ": " + textFeld_BenutzerNachricht.getText());
                schreiber.flush();
               
                textFeld_BenutzerNachricht.setText("");
                textFeld_BenutzerNachricht.requestFocus();
        }
       
        public void fügeTextNachrichtenHinzu(String nachricht) {
                textBereich_Nachrichten.append(nachricht + "\n");
        }
       
        // Listener
        public class EnterVerwalter implements KeyListener {
 
                @Override
                public void keyPressed(KeyEvent arg0) {
                        if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                                sendeNachrichtZumServer();
                        }      
                }
 
                @Override
                public void keyReleased(KeyEvent arg0) {}
 
                @Override
                public void keyTyped(KeyEvent arg0) {}
               
        }
       
        public class SendenKnopfVerwalter implements ActionListener {
 
                @Override
                public void actionPerformed(ActionEvent e) {
                        sendeNachrichtZumServer();                 
                }
               
        }
       
        public class ServerNachrichtenVerwalter implements Runnable {
 
                @Override
                public void run() {
                        String nachricht;
                       
                        try {
                                while((nachricht = leser.readLine()) != null) {
                                        fügeTextNachrichtenHinzu(nachricht);
                                        textBereich_Nachrichten.setCaretPosition(textBereich_Nachrichten.getText().length());
                                }
                        } catch (IOException e) {
                                fügeTextNachrichtenHinzu("Nachricht konnte nicht empfangen werden!");
                                e.printStackTrace();
                        }
                }
               
        }
}
