import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
 
public class FacharbeitServer {
 
        ServerSocket server;
        ArrayList<PrintWriter> liste_benutzerSchreiber;
       
        final int rot = 1;
        final int schwarz = 0;
        
        final int port = 2014;
 
        public static void main(String[] args) {
        	FacharbeitServer s = new FacharbeitServer();
                if (s.läuftServer()) {
                        s.höreBenutzernZu();
                } else {
                        // Do nothing
                }
        }
       
        public class BenutzerVerwalter implements Runnable {
 
                Socket benutzer;
                BufferedReader leser;
               
                public BenutzerVerwalter(Socket benutzer) {
                        try {
                                this.benutzer = benutzer;
                                leser = new BufferedReader(new InputStreamReader(benutzer.getInputStream()));
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
               
                @Override
                public void run() {
                        String nachricht;
                       
                        try {
                                while((nachricht = leser.readLine()) != null) {
                                        textZurKonsole("Vom Benutzer: \n" + nachricht, schwarz);
                                        sendeZuAllenBenutzern(nachricht);
                                }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
       
        public void höreBenutzernZu() {
                while(true) {
                        try {
                                Socket benutzer = server.accept();
                               
                                PrintWriter schreiber = new PrintWriter(benutzer.getOutputStream());
                                liste_benutzerSchreiber.add(schreiber);
                               
                                Thread benutzerThread = new Thread(new BenutzerVerwalter(benutzer));
                                benutzerThread.start();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }              
                }
        }
 
        public boolean läuftServer() {
                try {
                        server = new ServerSocket(port);
                        textZurKonsole("Server wurde gestartet auf der IP " +InetAddress.getLocalHost().getHostAddress()+":"+ port + "!", rot);
                       
                        liste_benutzerSchreiber = new ArrayList<PrintWriter>();
                        return true;
                } catch (IOException e) {
                        try {
							textZurKonsole("Server konnte nicht auf der IP " +InetAddress.getLocalHost().getHostAddress()+":"+ port + " gestartet werden!", rot);
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
                        e.printStackTrace();
                        return false;
                }
        }
       
        public void textZurKonsole(String nachricht, int farbe) {
                if(farbe == rot) {
                        System.err.println(nachricht + "\n");
                } else {
                        System.out.println(nachricht + "\n");
                }
        }
       
        public void sendeZuAllenBenutzern(String nachricht) {
                Iterator it = liste_benutzerSchreiber.iterator();
               
                while(it.hasNext()) {
                        PrintWriter schreiber = (PrintWriter) it.next();
                        schreiber.println(nachricht);
                        schreiber.flush();
                }
        }
}
