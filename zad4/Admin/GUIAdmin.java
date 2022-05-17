package zad1.Admin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class GUIAdmin extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Admin");
        ComboBox comboBox = new ComboBox();
        comboBox.setMaxSize(500,500);

        Label toplicLabel = new Label("Temat");

        TextField topicText = new TextField();

        Label messLabel = new Label("Informacja do klienta");

        TextField messText = new TextField();

        Button Send = new Button("Wyslij");
        Send.setMaxSize(500,500);

        Button Add = new Button("Dodaj temat");
        Add.setMaxSize(500,500);

        Button Remove = new Button("Usuń temat");
        Remove.setMaxSize(500,500);

        TextField system = new TextField();

        VBox vBox = new VBox(comboBox, toplicLabel, topicText, messLabel, messText, Send, Add, Remove, system);

        Admin adminLogicHandler = new Admin(messText, topicText, comboBox, system);

        Send.setOnAction((event) ->
        {
            String news = messText.getText();
            messText.setText("");
            String topic = (String) comboBox.getValue();
            adminLogicHandler.sendNews(topic, news);
        });

        Add.setOnAction((event) ->
        {
            String topic = topicText.getText();
            topicText.setText("");
            adminLogicHandler.add(topic);
        });
        Remove.setOnAction(e -> {
            topicText.setText("");
            String topic = comboBox.getValue().toString();
            comboBox.getItems().remove(topic);
            adminLogicHandler.remove(topic);
        });

        adminLogicHandler.get();

        Scene scene = new Scene(vBox, 300,210);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
