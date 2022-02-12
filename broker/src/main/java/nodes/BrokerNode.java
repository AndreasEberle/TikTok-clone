package nodes;

import data.BrokerData;
import index.Index;
import media.Video;
import server.Client;
import server.Server;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BrokerNode extends Node {
    public final TreeMap<String, Index> index = new TreeMap<>(); // username -> Index
    public final TreeMap<String, String> ips = new TreeMap<>(); // username -> ip
    public final TreeMap<String, Integer> ports = new TreeMap<>(); // username -> ports
    public final Server server;
    public static final Set<BrokerData> brokers = new TreeSet<>();

    static {
        String ip = "192.168.1.23";
//        String ip = "127.0.0.1";

        brokers.add(new BrokerData(ip, 55100));
        brokers.add(new BrokerData(ip, 55200));
        brokers.add(new BrokerData(ip, 55300));
    }

    public BrokerNode(String IP, int port) throws IOException {
        super("#BR" +IP +  ":" + port + "#", port);

        server = new Server(this, port);

        System.out.println("************************************************************");
        System.out.println("              BrokerNode ID:" + username);
        System.out.println("");
        System.out.println(" - Listening IP      : " + IP);
        System.out.println(" - Listening ports    : " + port);
        System.out.println("************************************************************");

        print("Initializing ...");

        super.initialize();
    }

    public void startServer() {
        print("Starting server at ports " + ports);
        server.open();
    }

    public void stopServer() throws IOException {
        print("Closing server ...");
        server.close();

        print("Done");
    }

    public void printBrokers() {
        System.out.println("Brokers logged: " + brokers.size());

        for (BrokerData b : brokers) {
            System.out.println(b);
        }
    }

    public void register(String username, String localip, int localport) {
        if (!index.containsKey(username)) {
            index.put(username, new Index(username));
            ips.put(username, localip);
            ports.put(username, localport);

            System.out.println("New user registered with username: " + username + " and address:"  + localip +":" + localport);
        } else {
            System.out.println("User " + username + " has returned" + " with address:"  + localip +":" + localport);
            index.get(username).clear();
            ips.put(username, localip);
            ports.put(username, localport);
        }
    }

    public void printData() {
        for (Map.Entry<String, Index> entry : index.entrySet()) {
            System.out.println("___________________________________");
            System.out.println("User: " + entry.getKey() + " IP: " + ips.get(entry.getKey()) + ":" + ports.get(entry.getKey()));
            System.out.println(entry.getValue());
            System.out.println("___________________________________");
        }

        if (index.isEmpty()) {
            System.out.println("Index is empty. No users have registered");
        }
    }

    public Set<String> getChannels() {
        Set<String > channels = new TreeSet<>();

        for (Map.Entry<String, Index> entry : index.entrySet()) {
            for (Video v : entry.getValue()) {
                channels.add(v.topic);
            }
        }

        return channels;
    }

    synchronized public void push(Video v) {
        System.out.println(v.topic);
        String username = v.topic.split(":")[0];

        index.get(username).add(v);
    }

    synchronized public Set<Video> getVideos(String s) {
        Set<Video > videos = new TreeSet<>();

        for (Map.Entry<String, Index> entry : index.entrySet()) {
            for (Video v : entry.getValue()) {
                if (v.topic.equals(s)) {
                    videos.add(v);
                }
            }
        }

        return videos;
    }

    synchronized public byte[] pull(String channel, String videoName) throws IOException {
        for (Map.Entry<String, Index> entry : index.entrySet()) {
            for (Video v : entry.getValue()) {
                if (v.filename.equals(videoName) && v.topic.equals(channel)) {
                    String ip = ips.get(entry.getKey());
                    int port = ports.get(entry.getKey());

                    Client client = new Client(ip,port);
                    client.open();

                    client.send("pull");
                    client.send(v.id);

                    int x = client.receiveInt();

                    if (x == 1) {

                        byte[] bytes = client.receiveBytes();
                        client.close();
                        return bytes;
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    synchronized public void printChannels() {
        Set<String> channels = getChannels();
        for (String s : channels) {
            System.out.println(s);
        }
    }
}
