package zad1.Servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class DictionaryServer extends Thread {
    private Map < String, String > tlumacz;
    private String jezyk;
    private String IP;
    private int port;
    ServerSocket serverSocket;

    public DictionaryServer(String jezyk, String IP, int port, Map <String, String> tlumacz) {
        this.jezyk = jezyk;
        this.IP = IP;
        this.port = port;
        this.tlumacz = tlumacz;
        this.start();
    }



    public void run() {
        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening() throws IOException {
        serverSocket = new ServerSocket(port);
        boolean serverRunning = true;
        System.out.println("Language Server(" + jezyk + ") is waiting for requests on port: " + port);
        while (serverRunning) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader wejscieOdSerwera = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter wyjscieDoSerwera = new PrintWriter(clientSocket.getOutputStream(), true);

            String dane = wejscieOdSerwera .readLine();
            String[] argumenty = dane.split(",");


            if (argumenty.length == 2) {
                String polSlowo = argumenty[0];
                int portClienta = Integer.parseInt(argumenty[1]);

                Socket languageSocket = new Socket(IP, portClienta);
                PrintWriter wyjscieDoClienta = new PrintWriter(languageSocket.getOutputStream(), true);

                if (tlumacz.containsKey(polSlowo)) {
                    wyjscieDoClienta.println(tlumacz.get(polSlowo));
                } else {
                    wyjscieDoClienta.println("BŁĄD: Nie znaleziono słowa w słowniku");
                }

                wyjscieDoClienta.close();
                languageSocket.close();
            }else{
                wyjscieDoSerwera.println(this.jezyk);
            }

            wejscieOdSerwera .close();
            wyjscieDoSerwera.close();
            clientSocket.close();
        }
    }
}
