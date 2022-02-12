package server;

import nodes.AppNode;
import protocol.Protocol;

import java.io.IOException;
import java.net.Socket;

public class AppNodeAcceptActionForClients extends ActionForClients {

    private AppNode node;

    public AppNodeAcceptActionForClients(AppNode node, Socket connection) throws IOException {
        super(connection);
        this.node = node;
    }


    @Override
    public void processConnection() throws IOException {
        String order = Protocol.receiveString(in);

        if (order.equals("pull")) {
            pull();
        }
    }

    private void pull() throws IOException {
        System.out.println("PULL REQUESTED");

        int id = Protocol.receiveInteger(in);

        System.out.println("Request for video: #" + id);

        byte[] bytes = node.readFile(id);

        if (bytes != null) {
            Protocol.sendInteger(out,1);
            Protocol.sendBytes(out, bytes);
        } else {
            Protocol.sendInteger(out,0);
        }
    }
}
