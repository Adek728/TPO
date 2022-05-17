package zad1.Client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import zad1.Client.Client;

import java.util.ArrayList;
import java.util.HashMap;


public class GUIClient extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client");
        ComboBox comboBox = new ComboBox();
        comboBox.setMaxSize(500,500);

        Label toplicLabel = new Label("Temat");

        TextField topicText = new TextField();

        Label messLabel = new Label("Informacje");

        TextField messText = new TextField();

        Button Sub = new Button("Sub");
        Sub.setMaxSize(500,500);

        Button UnSub = new Button("UnSub");
        UnSub.setMaxSize(500,500);

        Button News = new Button("Informacje");
        News.setMaxSize(500,500);

        TextField system = new TextField();

        VBox vBox = new VBox(comboBox, toplicLabel, topicText, messLabel, messText, Sub, UnSub, News,system);

        HashMap<String, String> topicAndLatestNews = new HashMap<>();
        ArrayList<String> topics = new ArrayList<>();
        Client clientLogicHandler = new Client(messText, topicAndLatestNews, topics, comboBox,system);

        Sub.setOnAction((event) ->
        {
            String topic = topicText.getText();
            if(!topics.contains(topic))
                clientLogicHandler.sub(topic);
            topicText.setText("");
        });

        UnSub.setOnAction((event) ->
        {
            String topic = (String) comboBox.getValue();
            if(topics.contains(topic))
                clientLogicHandler.unsub(topic);

        });
        News.setOnAction(e -> {
            String topic = (String) comboBox.getValue();
            clientLogicHandler.info(topic);
        });


        Scene scene = new Scene(vBox, 300,210);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}

