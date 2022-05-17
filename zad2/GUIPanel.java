package zad1;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.util.Currency;

public class GUIPanel extends Application{

    private Service service;
    private String country;
    private String city;

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Weather for you");

        BorderPane borderPane = new BorderPane();
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        webEngine.load("https://en.wikipedia.org/wiki/Warsaw");


        Font font = new Font(15);

        Label label = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        label.setFont(font);
        label1.setFont(font);
        label2.setFont(font);




        TextField countryField = new TextField();
        countryField.setPrefSize(75,10);
        Label kraj = new Label("Country");
        countryField.setOnAction(actionEvent -> {
            this.country = countryField.getText();
            service = new Service(country);
        });


        TextField cityField = new TextField();
        cityField.setPrefSize(75,10);
        Label miasto = new Label("City");
        cityField.setOnAction(actionEvent -> {
            this.city = cityField.getText();
            webEngine.load("https://en.wikipedia.org/wiki/"+city);

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(service.getWeather(city));
            String temp = jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsString();
            String sky = jsonElement.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
            String pressure = jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("pressure").getAsString();
            String humidity = jsonElement.getAsJsonObject().get("main").getAsJsonObject().get("humidity").getAsString();
            String speed = jsonElement.getAsJsonObject().get("wind").getAsJsonObject().get("speed").getAsString();

            label.setText(city + ":  Temp: " + temp +"\u2103  Sky: " + sky + "  Pressure: " + pressure + "  Humidity: " + humidity + "  Wind Speed: " + speed);
            label1.setText("1 " + Currency.getInstance(service.getCountryLocale()) + " to PLN = " +service.getNBPRate());
        });


        TextField kursField = new TextField();
        kursField.setPrefSize(50,10);
        Label kurs = new Label("Currency");
        kursField.setOnAction(actionEvent -> {
            label2.setText("1 " + kursField.getText() + " to " + Currency.getInstance(service.getCountryLocale())+
                    " is " + service.getRateFor(kursField.getText()));
        });

        HBox hboxtext = new HBox();
        hboxtext.setSpacing(10);
        hboxtext.getChildren().addAll(
                kraj,countryField,
                miasto,cityField,
                kurs, kursField,
                label, label1, label2
        );
        borderPane.setCenter(browser);
        borderPane.setTop(hboxtext);

        Scene scene = new Scene(borderPane,1400,920);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}