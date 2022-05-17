package zad1.Client;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
    private final TextField textArea;
    private final TextField system;
    private final ComboBox comboBox;
    private final HashMap<String, String> topicsAndLastNews;
    private final ArrayList<String> topics;
    private SocketChannel socketChannel;

    public Client(TextField textArea, HashMap<String, String> topicsAndLastNews, ArrayList<String> topics, ComboBox comboBox, TextField system) {
        this.textArea = textArea;
        this.topicsAndLastNews = topicsAndLastNews;
        this.topics = topics;
        this.comboBox = comboBox;
        this.system = system;
        reconnect();
    }

    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private CharBuffer charBuffer = buffer.asCharBuffer();
    private final Charset charset = Charset.forName("ISO-8859-2");

    public boolean getMessage() {
        String message = "";

        if (!socketChannel.isConnected())
            return false;

        while(true) {
            try {
                buffer.clear();
                int read = socketChannel.read(buffer);
                if (read == 0)
                    continue;
                else if (read == -1)
                    break;
                else {
                    buffer.flip();
                    charBuffer = charset.decode(buffer);
                    message = charBuffer.toString();
                    charBuffer.clear();

                    ArrayList<String> messageParts = new ArrayList<>(Arrays.asList(message.split(",")));

                    switch (messageParts.get(0)) {
                        case "tak" -> {
                            return true;
                        }
                        case "nie" -> {
                            return false;
                        }
                        case "info" -> {
                            if (messageParts.get(2).equals("usuniete")) {
                                comboBox.getItems().remove(messageParts.get(1));
                                system.setText("Temat został usunięty");
                                textArea.setText("");
                                return false;
                            }
                            topicsAndLastNews.put(messageParts.get(1), messageParts.get(2));
                            textArea.setText(messageParts.get(1) + ": " + messageParts.get(2));
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                reconnect();
            }
        }
        return false;
    }

    public void sub(String topic) {
        sendMessage("sub," + topic);
        textArea.setText("");
        if (!getMessage())
            system.setText("Nie udało się zasubskrybować");
        else{
            topics.add(topic);
            comboBox.getItems().add(topic);
            system.setText("Sub zrealizowany");
        }
    }

    public void unsub(String topic) {
        sendMessage("unsub," + topic);
        textArea.setText("");
        system.setText("UnSub zrealizowany");
        if (getMessage()) {
            topics.remove(topic);
            comboBox.getItems().remove(topic);
        }
    }

    public void info(String topic){
        sendMessage("info," + topic);
        system.setText("");
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