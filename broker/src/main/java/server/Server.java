package server;

import nodes.BrokerNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server extends Thread {
    public final int BACKLOG = 100;
    private BrokerNode brokerNode;
    public final ServerSocket providerSocket;
    public final Set<Socket> sockets = new HashSet<>();
    public boolean closing = false;


    public Server(BrokerNode brokerNode, int port) throws IOException {
        this.brokerNode = brokerNode;
        providerSocket = new ServerSocket(port, BACKLOG);
    }

    public void open() {
        start();
    }

    public void close() throws IOException {
        closing = true;

        for (Socket s : sockets) {
            s.close();
        }
        providerSocket.close();
    }

    public void run() {
        Set<Socket> cons = new HashSet<Socket>();
        try {
            while (true) {
                Socket connection = providerSocket.accept();

                Thread t = new BrokerAcceptActionForClients(brokerNode, connection);
                t.start();

                cons.add(connection);
            }
        } catch (IOException ioException) {
            if (!closing) {
                ioException.printStackTrace();
            }
        }finally {
            for (Socket s : cons) {
                try {
                    s.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
