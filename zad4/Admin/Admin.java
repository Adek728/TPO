package zad1.Admin;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

public class Admin {
    private final TextField messageArea;
    private final TextField topicArea;
    private final TextField system;
    private final ComboBox comboBox;
    private SocketChannel socketChannel;

    public Admin(TextField messageArea, TextField topicArea, ComboBox comboBox, TextField system) {
        this.messageArea = messageArea;
        this.topicArea = topicArea;
        this.comboBox = comboBox;
        this.system = system;
        reconnect();

    }

    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private CharBuffer charBuffer = buffer.asCharBuffer();
    private final Charset charset = Charset.forName("ISO-8859-2");

    public void getMessage() {
        String message = "";
        if (!socketChannel.isConnected()) {
            return;
        }
        while(true) {
            try {
                buffer.clear();
                int read = socketChannel.read(buffer);
                if (read == 0) {
                    continue;
                }
                if (read == -1) {
                    return;
                } else {
                    buffer.flip();
                    charBuffer = charset.decode(buffer);
                    message = charBuffer.toString();
                    charBuffer.clear();

                    ArrayList<String> messageParts = new ArrayList<>(Arrays.asList(message.split(",")));

                    switch (messageParts.get(0)) {
                        case "tak" -> {
                            return;
                        }
                        case "nie" -> {
                            System.out.println("returning false");
                            return;
                        }
                        case "wezInfo" -> {
                            if (messageParts.get(2).equals("null")) {
                                break;
                            }
                            for (int i = 1; i < messageParts.size(); i++) {
                                comboBox.getItems().add(messageParts.get(i));
                            }
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                reconnect();
            }
            return;
        }
    }

    public void sendNews(String topic, String news) {
        messageArea.setText("");
        system.setText("Wysłałeś informacje na temat tego tematu");
        if(news.equals("")){
            system.setText("Nie podałeś nowej informacji");
        }else{
            sendMessage("wysylanie," + topic + "," + news);
            getMessage();
        }
    }

    public void add(String topic) {
        system.setText("Dodałeś nowy temat: " + topic);
        if(topic.equals("")){
            system.setText("nie podales tematu");
        }else {
            sendMessage("dodaj," + topic);
            comboBox.getItems().add(topic);
            getMessage();
        }
    }

    public void remove(String topic){
        system.setText("Usunales temat: " + topic);
        sendMessage("usun," + topic);
        getMessage();
    }


    public void get(){
        sendMessage("wezInfo");
        getMessage();
    }

    public void sendMessage(String message) {
        ByteBuffer msg = charset.encode(CharBuffer.wrap(message + '\n'));

        while(true) {
            try {
                socketChannel.write(msg);
                return;
            } catch (Exception e) {
                reconnect();
            }
        }
    }

    private void reconnect() {
        while(true) {
            if(socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (Exception ignored) {}
            }

            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress("localhost", 12000));
                while (!socketChannel.finishConnect()) { }
                return;
            } catch (Exception ignored) {}
        }
    }
}