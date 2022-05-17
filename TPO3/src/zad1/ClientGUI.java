package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private static String IP;
    private static int destinationPort;

    public ClientGUI() {
    }

    public ClientGUI(String IP, int destinationPort) {
        this.IP = IP;
        this.destinationPort = destinationPort;
    }

    public static void main(String[] args) {
        ClientGUI client = new ClientGUI("127.0.0.1", 11000);
        Application.launch(ClientGUI.class);
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Do tlumaczenia");

        Label jezyk = new Label("Wybierz jezyk:");
        Label slowo = new Label("Wpisz slowo:");
        Button tlumaczenie = new Button("Przetłumacz");
        Label wynik = new Label("Tlumazcenie:");




        ComboBox comboBoxLanguages = new ComboBox();
        comboBoxLanguages.setMaxSize(500, 150);
        comboBoxLanguages.setPrefHeight(15);
        comboBoxLanguages.getItems().add("ENG");
        comboBoxLanguages.getItems().add("FR");
        comboBoxLanguages.getItems().add("DE");

        TextField textFieldWord = new TextField();

        TextField slowoWynik = new TextField();
        slowoWynik.setPrefSize(300, 150);
        slowoWynik.setPrefHeight(15);

        BorderPane borderPane = new BorderPane();

        HBox hBoxJezyk = new HBox();
        hBoxJezyk.setSpacing(15);
        hBoxJezyk.getChildren().addAll(jezyk, comboBoxLanguages,slowo, textFieldWord, tlumaczenie);
        borderPane.setTop(hBoxJezyk);

        HBox hBoxWynik = new HBox();
        hBoxWynik.setSpacing(15);
        hBoxWynik.getChildren().addAll(wynik,slowoWynik);
        borderPane.setBottom(hBoxWynik);

        tlumaczenie.setOnAction((event) -> {
            slowoWynik.setText("");

            String jezykDoTlumaczenia = (String) comboBoxLanguages.getValue();
            if (jezykDoTlumaczenia == null) {
                slowoWynik.setText("BŁĄD: Nie wybrales jezyka");
                return;
            }

            String word = textFieldWord.getText();

            if (word.equals("")) {
                slowoWynik.setText("BŁĄD: Nie podales slowa");
                return;
            }
            String resultWord = translate(word, jezykDoTlumaczenia);
            textFieldWord.setText("");
            slowoWynik.setText(resultWord);

        });
        Scene scene = new Scene(borderPane, 600, 50);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String translate(String slowoDoTlumaczenia, String jezyk) {
        String wynikTlumaczenia = "";

        try {
            Socket clientSocket = new Socket(IP, destinationPort);
            PrintWriter doSerwera = new PrintWriter(clientSocket.getOutputStream(), true);


            int clientPort = 11501;
            ServerSocket serverSocket = new ServerSocket(clientPort);
            String query = slowoDoTlumaczenia + "," + jezyk + "," + clientPort;
            doSerwera.println(query);

            try{
                Socket resultSocket = serverSocket.accept();
                BufferedReader odSerweraJezykowego = new BufferedReader(new InputStreamReader(resultSocket.getInputStream()));

                wynikTlumaczenia = odSerweraJezykowego.readLine();

                resultSocket.close();
                odSerweraJezykowego.close();
            }catch (Exception ignored){}


            serverSocket.close();
            clientSocket.close();
            doSerwera.close();
        } catch (IOException ignored) {}
        return wynikTlumaczenia;
    }

}