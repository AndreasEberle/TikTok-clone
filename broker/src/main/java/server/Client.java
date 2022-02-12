package server;

import data.BrokerData;
import media.Video;
import protocol.Protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Client {
    public final String ip;
    public final int port;

    public ObjectOutputStream out = null;
    public ObjectInputStream in = null;

    public Socket requestSocket;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    public void open() throws IOException {
        requestSocket = new Socket(ip, port);
        out = new ObjectOutputStream(requestSocket.getOutputStream());
        in = new ObjectInputStream(requestSocket.getInputStream());
    }


    public void close() {
        try {
            in.close();
            out.close();
            requestSocket.close();
        } catch (IOException ioException) {

        }
    }

    public void send(String msg) throws IOException {
        Protocol.sendString(out, msg);
    }

    public Set<BrokerData> receiveBrokerData() throws IOException, ClassNotFoundException {
        int n = Protocol.receiveInteger(in);

        Set<BrokerData> brokers = new TreeSet<>();

        for (int i=0;i<n;i++) {
            BrokerData data = Protocol.receiveBrokerData(in);
            brokers.add(data);
        }
        return brokers;
    }

    public Set<String> receiveChannels() throws IOException {
        int n = Protocol.receiveInteger(in);

        Set<String> channels = new TreeSet<>();

        for (int i=0;i<n;i++) {
            String channel  = Protocol.receiveString(in);
            channels.add(channel);
        }
        return channels;
    }

    public void sendVideo(Video v) throws IOException {
        Protocol.sendVideo(out, v);
    }

    public Set<Video> receiveVideos() throws IOException, ClassNotFoundException {
        int n = Protocol.receiveInteger(in);

        Set<Video> videos = new TreeSet<>();

        for (int i=0;i<n;i++) {
            Video v  = Protocol.receiveVideo(in);
            videos.add(v);
        }
        return videos;
    }

    public void send(int value) throws IOException {
        Protocol.sendInteger(out, value);
    }


    public byte[] receiveBytes() throws IOException {
        return Protocol.receiveBytes(in);
    }

    public void sendBytes(byte [] bytes) throws IOException {
        Protocol.sendBytes(out,bytes);
    }

    public int receiveInt() throws IOException {
        return Protocol.receiveInteger(in);
    }
}