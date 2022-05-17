package zad1.Serwer;


import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class Server {
    private static final int PORT = 12000;
    private Selector selector;
    private final HashMap<String, List<News>> topics = new HashMap<>();
    private final Map<SocketChannel, List<String>> clients = new HashMap<>();
    private ServerSocketChannel serverSocketChannel;

    public Server() {
        topics.put("sport", new ArrayList<>());
        topics.get("sport").add(new News("pływanie lol"));
        topics.put("polityka", new ArrayList<>());
        topics.get("polityka").add(new News("nie fajny temat"));
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server started");
        run();
    }

    private void run(){
        boolean run = true;
        while (run) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        SocketChannel cc = serverSocketChannel.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                        clients.put(cc, new ArrayList<>());
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel cc = (SocketChannel) key.channel();
                        serviceRequest(cc);
                        continue;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private static final Charset CHARSET = Charset.forName("ISO-8859-2");
    private static final int BUFFER_SIZE = 1024;

    private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private StringBuffer stringBuffer = new StringBuffer();

    private void serviceRequest(SocketChannel client) {
        if(!client.isOpen()) return;

        stringBuffer.setLength(0);
        byteBuffer.clear();

        try{
            readLoop:
            while(true){
                int read = client.read(byteBuffer);
                if(read > 0){
                    byteBuffer.flip();
                    CharBuffer charBuffer = CHARSET.decode(byteBuffer);
                    while (charBuffer.hasRemaining()) {
                        char c = charBuffer.get();
                        if (c == '\n' || c == '\r') break readLoop;
                        stringBuffer.append(c);
                    }
                    break;
                }
            }

            ArrayList<String> zapytanie = new ArrayList<>(Arrays.asList(stringBuffer.toString().split(",")));
            String text = zapytanie.get(0);
            String temat;

            switch (text){
                case "wysylanie":
                    String topicText = zapytanie.get(1);
                    String newsText = zapytanie.get(2);
                    topics.putIfAbsent(topicText, new ArrayList<>());
                    topics.get(topicText).add(new News(newsText));
                    sendMessage(client, "tak");
                    break;
                case "dodaj":
                    String topicName = zapytanie.get(1);
                    topics.putIfAbsent(topicName, new ArrayList<>());
                    topics.get(topicName).add(new News("Brak informacji"));
                    sendMessage(client, "tak");
                    break;
                case "usun":
                    String topicNameToDelete = zapytanie.get(1);
                    topics.remove(topicNameToDelete);
                    sendMessage(client, "tak");
                    break;
                case "wezInfo":
                    String messageToSend = "wezInfo,";
                    if(topics.size() == 0)
                        messageToSend = "null";
                    else{
                        for(int i = 0; i < topics.size(); i++){
                            if(i == topics.size() - 1)
                                messageToSend += topics.keySet().toArray()[i];
                            else
                                messageToSend += topics.keySet().toArray()[i] + ",";

                        }
                    }
                    sendMessage(client, messageToSend);
                    break;
                case "sub":
                    temat = zapytanie.get(1);
                    if (topics.containsKey(temat)) {
                        clients.get(client).add(temat);
                        sendMessage(client, "tak");
                    }else
                        sendMessage(client, "nie");

                    break;
                case "unsub":
                    temat = zapytanie.get(1);
                    if (topics.containsKey(temat)) {
                        if(clients.get(client).remove(zapytanie.get(1)))
                            sendMessage(client, "tak");
                        else
                            sendMessage(client, "nie");

                    }else
                        sendMessage(client, "nie");

                    break;
                case "info":
                    String topicNews = zapytanie.get(1);
                    if(!topics.containsKey(topicNews)){
                        sendMessage(client, "info," + topicNews + ",usuniete");
                        break;
                    }
                    for (String t: topics.keySet()){
                        if(t.equals(topicNews)) {
                            clients.get(client).add(topicNews);
                            topics.get(topicNews).stream().max(Comparator.comparing(News::getId)).ifPresent(news -> sendMessage(client,
                                    "info," + topicNews + "," + news.getData()));
                            break;
                        }
                    }
                    break;
            }

        }catch (IOException e) {
            try {
                client.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void sendMessage(SocketChannel client, String message){
        ByteBuffer buffer = CHARSET.encode(CharBuffer.wrap(message));
        try {
            client.write(buffer);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
