package nodes;

import com.google.common.io.Files;
import com.sun.corba.se.pept.broker.Broker;
import data.BrokerData;
import hashing.HashingCalculator;
import index.Index;
import media.Video;
import protocol.Protocol;
import server.Client;
import server.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AppNode extends Node {
    public final Index index;
    public final Server server;
    public final Set<BrokerData> brokers = new TreeSet<>();
    public final HashMap<BrokerData, Client> clients = new HashMap<>();

    public AppNode(String username, int port) throws IOException {
        super(username, port);

        server = new Server(this, port);
        index = new Index(username);

        System.out.println("************************************************************");
        System.out.println("              AppNode - Username: " + username);
        System.out.println("");
        System.out.println(" - Working  directory: " + uploadDirectory);
        System.out.println(" - Download directory: " + downloadDirectory);
        System.out.println(" - Listening port    : " + port);
        System.out.println("************************************************************");

        print("Initializing ...");

        super.initialize();
    }


    public void loadIndex() throws IOException {
        System.out.println("Reading files from uploadDirectory: " + uploadDirectory);

        index.loadFromDisk(uploadDirectory);
    }

    public void printIndex() {
        System.out.println("Index contains: ");

        System.out.println(index.toString());
    }

    public void startServer() {
        print("Starting server at port " + port);
        server.open();
    }


    public void stopServer() throws IOException {
        print("Closing server ...");
        server.close();

        print("Done");


        for (Map.Entry<BrokerData, Client> entry : clients.entrySet()) {
            entry.getValue().close();
        }
    }

    public void printBrokers() {
        System.out.println("Brokers logged: " + brokers.size());

        for (BrokerData b : brokers) {
            boolean connectionEstablished = clients.containsKey(b);
            if (connectionEstablished) {
                System.out.println(b + "\t\tCONNECTION ESTABLISED");
            }
        }
    }

    public void register(String gatewayIP, int gatewayPort, String localip, int localport) throws IOException, ClassNotFoundException {
        print("Registering through gateway: " + gatewayIP + ":" + gatewayPort + " ...");
        print("Connecting ...");

        Client client = new Client(gatewayIP, gatewayPort);
        client.open();


        print("Registering ...");
        client.send("register");
        client.send(username);
        client.send(localip);
        client.send(localport);


        print("Asking for brokers ...");
        client.send("brokers");

        Set<BrokerData> brokerData = client.receiveBrokerData();

        for (BrokerData data : brokerData) {
            this.brokers.add(data);
        }

        client.close();

    }

    public void connectToAll(String gatewayIP, int gatewayPort, String localip, int localport) throws IOException {
        for (BrokerData data : brokers) {
            if (!data.ip.equals(gatewayIP) || data.port != gatewayPort) {
                Client client = new Client(data.ip, data.port);
                client.open();
                client.send("register");
                client.send(username);
                client.send(localip);
                client.send(localport);
                client.close();
            }
        }
        for (BrokerData data : brokers) {
            Client client = new Client(data.ip, data.port);
            clients.put(data, client);

            print("connecting to: " + client.ip + ":" + client.port);
            client.open();
            print("Connection established");

            client.open();
        }
    }


    public void printChannels() throws IOException {

        for (Map.Entry<BrokerData, Client> entry : clients.entrySet()) {
            Client client= entry.getValue();
            client.send("channels");

            Set<String> channels = client.receiveChannels();

            for (String s : channels) {
                System.out.println("- " + s);
            }
        }
    }

    public void pushAll() throws IOException {
        for (Video v : index) {
            String topic = v.topic;
            BrokerData data =  HashingCalculator.getBrokerData(brokers, topic);

            System.out.println("Video: #" + v.id + " " + v.filename + " at '" + v.topic + "' will be sent to: " + data);

            Client client = clients.get(data);
            client.send("push");
            client.sendVideo(v);
        }
    }

    public void push(Video v) throws IOException {
        String topic = v.topic;
        BrokerData data =  HashingCalculator.getBrokerData(brokers, topic);

        System.out.println("Video: #" + v.id + " " + v.filename + " at '" + v.topic + "' will be sent to: " + data);

        Client client = clients.get(data);
        client.send("push");
        client.sendVideo(v);
    }

    public void browse(String channel) throws IOException, ClassNotFoundException {
        String topic = channel;
        BrokerData data =  HashingCalculator.getBrokerData(brokers, topic);

        System.out.println("Request for " + channel + " will be sent to : " + data);

        Client client = clients.get(data);
        client.send("browse");
        client.send(channel);

        Set<Video> videos = client.receiveVideos();

        System.out.println(" Results: ");
        System.out.println("----------");
        for (Video v : videos) {
            System.out.println(v);
        }
    }

    public void pull(String channel, String videoName) throws IOException {
        String topic = channel;
        BrokerData data =  HashingCalculator.getBrokerData(brokers, topic);

        System.out.println("Request for " + videoName + " will be sent to : " + data);

        Client client = clients.get(data);
        client.send("pull");
        client.send(channel);
        client.send(videoName);

        System.out.println("Waiting for ack (0 or 1) ...");
        int x = client.receiveInt();

        if (x == 0) {
            System.out.println("file not found");
        } else {
            System.out.println("file found, transfering ...");

            byte[] bytes = client.receiveBytes();

            File f = new File(downloadDirectory + "/" + videoName);

            Files.write(bytes, f);

            System.out.println("Transfer complete.");
        }
    }

    public byte[] readFile(int id) throws IOException {
        for (Video v : index) {
            if (v.id == id) {
                File f = new File(v.path);
                byte[] bytes = Files.toByteArray(f);
                return bytes;
            }
        }

        return null;
    }


}
