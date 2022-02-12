package server;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ActionForClients extends Thread {
    public final ObjectInputStream in;
    public final ObjectOutputStream out;

    public ActionForClients(Socket connection) throws IOException {
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
    }

    public void run() {
        try {
            processConnection();
        } catch (IOException e) {
            if (! (e instanceof EOFException)){
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    abstract public  void processConnection() throws IOException, ClassNotFoundException;
}

