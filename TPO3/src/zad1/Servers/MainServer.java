package zad1.Servers;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer extends Thread {
    private final Map < String, Integer > jezyki = new HashMap <> ();
    private final String IP;
    private final int port;
    private ServerSocket serverSocket;
    public MainServer(String IP, int port) {
        this.IP = IP;
        this.port = port;
        this.start();
    }

    public void run() {
        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() throws IOException {
        serverSocket = new ServerSocket(port);
        boolean serverRunning = true;
        System.out.println("Server is waiting for requests on port: " + port);
        while (serverRunning) {
            Socket clientSocketLanguage;
            PrintWriter wyjscieDoSerweraJezykowego;
            BufferedReader wejscieOdSerweraJezykowego ;
            for (int i = 12000; i <= 12009; i++) {
                try {
                    clientSocketLanguage = new Socket(IP, i);
                    wyjscieDoSerweraJezykowego = new PrintWriter(clientSocketLanguage.getOutputStream(), true);
                    wejscieOdSerweraJezykowego = new BufferedReader(new InputStreamReader(clientSocketLanguage.getInputStream()));

                    wyjscieDoSerweraJezykowego.println("");
                    String language = wejscieOdSerweraJezykowego.readLine();
                    System.out.println(language + " port " + i);
                    jezyki.put(language,i);
                } catch (IOException ignored) {}
            }

            Socket clientSocket = serverSocket.accept();
            BufferedReader wejscieOdClienta = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter wyjscieDoClienta = new PrintWriter(clientSocket.getOutputStream(), true);

            String dane = wejscieOdClienta.readLine();
            String[] argumenty = dane.split(",");

            if (argumenty.length == 3) {
                String slowo = argumenty[0];
                String jezyk = argumenty[1];
                String clientPort = argumenty[2];

                if (jezyki.containsKey(jezyk)) {
                    int LanguageServerPort = jezyki.get(jezyk);

                    Socket languageSocket = new Socket(IP, LanguageServerPort);
                    PrintWriter wyjscieDoJezyka = new PrintWriter(languageSocket.getOutputStream(), true);

                    try {
                        String wyjscioweArgumenty = slowo + "," + clientPort;
                        System.out.println("Forwarding Message: {" + wyjscioweArgumenty + "}");
                        wyjscieDoJezyka.println(wyjscioweArgumenty);
                        wyjscieDoClienta.println("");
                    } catch (Exception ignored) { }
                    languageSocket.close();
                    wyjscieDoJezyka.close();

                }

            }else{
                wyjscieDoClienta.println("");
            }

            wejscieOdClienta.close();
            wyjscieDoClienta.close();
            clientSocket.close();
        }
    }
}
