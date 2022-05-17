/**
 *
 *  @author Krzywnicki Adrian S22887
 *
 */

package zad1;


import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Service {


    private Locale iso;
    private final String code;

    public Service(String kraj) {
        this.iso = getCountryIso(kraj);
        this.code = getCountryCode(kraj);
    }
    public Locale getCountryIso(String kraj) {
        Locale.setDefault(Locale.ENGLISH);
        for (Locale locale : Locale.getAvailableLocales())
            if (kraj.equals(locale.getDisplayCountry()))
                this.iso = locale;
        return this.iso;
    }

    public String getCountryCode(String kraj){
        Locale.setDefault(Locale.ENGLISH);
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            if (locale.getDisplayCountry().equals(kraj))
                return iso;
        }
        return null;
    }

    public double getRateFor(String kurs) {
            double result = 0;
            String currency = Currency.getInstance(iso).toString();
            try{
                String x = "https://api.exchangerate.host/convert?from=" + kurs + "&to=" + currency;
                JsonElement jsonElement = connect(x);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                String res = jsonObject.get("result").getAsString();

                result = Double.parseDouble(res);
            }catch(Exception ignored){ }
            return result;
    }

    public String getWeather(String kraj) {
        String result = "";
        String key = "2867373a2d746742247a6ad1755c8376";
        String x = ("https://api.openweathermap.org/data/2.5/weather?q="+kraj+ "," + code +"&appid=" + key + "&units=metric");
        try{
            JsonElement jsonElement = connect(x);
            result = jsonElement.toString();
        }catch (Exception ignored){}
        return result;
    }

    public double getNBPRate() {
        String currency = Currency.getInstance(iso).toString();
        String result = "";

        if(!currency.equals("PLN")){
            String x = "";
            try {
                x = "http://api.nbp.pl/api/exchangerates/tables/a/";
                JsonElement jsonElement = connect(x);
                result = jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("rates").toString();
                if(result.contains(currency))
                    x = "http://api.nbp.pl/api/exchangerates/rates/a/" + currency + "/";
                else
                    x = "http://api.nbp.pl/api/exchangerates/rates/b/" + currency + "/";

                System.out.println(x);
                jsonElement = connect(x);
                result = jsonElement.getAsJsonObject().get("rates").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString();
                return Double.parseDouble(result);
            }catch (Exception ignored){}
        }
        return 1.0;
    }

    public JsonElement connect(String string) throws Exception{

        URL base = new URL(string);
        HttpURLConnection request = (HttpURLConnection)base.openConnection();
        request.connect();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(new InputStreamReader(request.getInputStream()));
        return jsonElement;
    }
    public Locale getCountryLocale() {
        return iso;
    }
}
