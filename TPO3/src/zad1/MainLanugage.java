package zad1;

import zad1.Servers.DictionaryServer;

import java.util.HashMap;
import java.util.Map;

public class MainLanugage {
    public static void main(String[] args) {
        Map< String, String > angielski = new HashMap<>();
        angielski.put("raz", "once");
        angielski.put("jaskinia", "cave");
        DictionaryServer englishServer = new DictionaryServer("ENG", "127.0.0.1", 12000, angielski);


        Map < String, String > francuski = new HashMap <> ();
        francuski.put("kolega", "ami");
        francuski.put("dziewczyna", "fille");
        DictionaryServer frenchServer = new DictionaryServer("FR", "127.0.0.1", 12001, francuski);

        Map < String, String > niemiecki = new HashMap <> ();
        niemiecki.put("auto", "wagen");
        niemiecki.put("pies", "hund");
        DictionaryServer deuchServer = new DictionaryServer("DE", "127.0.0.1", 12002, niemiecki);

    }
}
