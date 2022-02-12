package server;

import data.BrokerData;
import media.Video;
import nodes.BrokerNode;
import protocol.Protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;

public class BrokerAcceptActionForClients extends ActionForClients {

    private final BrokerNode brokerNode;

    public BrokerAcceptActionForClients(BrokerNode brokerNode, Socket connection) throws IOException {
        super(connection);
        this.brokerNode = brokerNode;
    }


    @Override
    public void processConnection() throws IOException, ClassNotFoundException { // process the connection of one client
        while (true) {
            String order = Protocol.receiveString(in);

            if (order.equals("register")) {
                register();
            }

            if (order.equals("brokers")) {
                brokers();
            }

            if (order.equals("channels")) {
                channels();
            }

            if (order.equals("browse")) {
                browse();
            }

            if (order.equals("push")) {
                push();
            }

            if (order.equals("pull")) {
                pull();
            }
        }
    }

    private void register() throws IOException {
        System.out.println("REGISTER REQUESTED");

        String username = Protocol.receiveString(in);
        String localip = Protocol.receiveString(in);
        int localport  = Protocol.receiveInteger(in);
        brokerNode.register(username, localip, localport);
    }

    private void brokers() throws IOException {
        System.out.println("BROKERS REQUESTED");

        Set<BrokerData> brokers = BrokerNode.brokers;

        int len = brokers.size();

        Protocol.sendInteger(out, len);

        for (BrokerData data : brokers) {
            Protocol.sendBrokerData(out, data);
        }
    }

    private void channels() throws IOException {
        System.out.println("CHANNELS REQUESTED");

        Set<String> channels = brokerNode.getChannels();

        int len = channels.size();

        Protocol.sendInteger(out, len);

        for (String data : channels) {
            Protocol.sendString(out, data);
        }
    }



    private void pull() throws IOException {
        System.out.println("PULL REQUESTED");

        String channel = Protocol.receiveString(in);
        String videoName = Protocol.receiveString(in);

        byte[] bytes = brokerNode.pull(channel, videoName);

        if (bytes != null) {
            System.out.println("File found");
            Protocol.sendInteger(out, 1);
            Protocol.sendBytes(out, bytes);
        } else {
            System.out.println("File not found");
            Protocol.sendInteger(out, 0);
        }
    }


    private void push() throws IOException, ClassNotFoundException {
        System.out.println("PUSH REQUESTED");

        Video v = Protocol.receiveVideo(in);

        brokerNode.push(v);

    }

    private void browse() throws IOException {
        System.out.println("browse REQUESTED");

        String s = Protocol.receiveString(in);

        Set<Video> videos = brokerNode.getVideos(s);

        int len = videos.size();

        Protocol.sendInteger(out, len);

        for (Video v : videos) {
            Protocol.sendVideo(out, v);
        }
    }





}
