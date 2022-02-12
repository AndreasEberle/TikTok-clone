package server;

import nodes.AppNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server extends Thread {
    public final int BACKLOG = 100;
    private AppNode node;
    public final ServerSocket providerSocket;
    public final Set<Socket> sockets = new HashSet<>();
    public boolean closing = false;

    public Server(AppNode node, int port) throws IOException {
        this.node = node;
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
        try {
            while (true) {
                Socket connection = providerSocket.accept();

                Thread t = new AppNodeAcceptActionForClients(node, connection);
                t.start();
            }
        } catch (IOException ioException) {
            if (!closing) {
                ioException.printStackTrace();
            }
        }
    }
}
