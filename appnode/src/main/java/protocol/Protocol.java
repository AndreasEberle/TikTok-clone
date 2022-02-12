package protocol;

import data.BrokerData;
import media.Video;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Protocol {
    private static int PACKET_SIZE = 10*1024;

    public static final void sendString(ObjectOutputStream os, String msg) throws IOException {
        os.writeUTF(msg);
        os.flush();
    }

    public static final String receiveString(ObjectInputStream is) throws IOException {
        String msg = is.readUTF();
        return msg;
    }


    public static final void sendBrokerData(ObjectOutputStream os, BrokerData data) throws IOException {
        os.writeObject(data);
        os.flush();
    }

    public static final BrokerData receiveBrokerData(ObjectInputStream os) throws IOException, ClassNotFoundException {
        BrokerData data = (BrokerData) os.readObject();
        return data;
    }


    public static final void sendInteger(ObjectOutputStream os, int msg) throws IOException {
        os.writeInt(msg);
        os.flush();
    }

    public static final int receiveInteger(ObjectInputStream is) throws IOException {
        int msg = is.readInt();
        return msg;
    }

    public static void sendVideo(ObjectOutputStream os, Video data) throws IOException {
        os.writeObject(data);
        os.flush();
    }

    public static final Video receiveVideo(ObjectInputStream os) throws IOException, ClassNotFoundException {
        Video data = (Video) os.readObject();
        return data;
    }

    public static final void sendBytes(ObjectOutputStream os, byte[] bytes) throws IOException {
        sendInteger(os, bytes.length);

        int n = 0;

        while (n < bytes.length) {
            int nextPacketSize;
            if (bytes.length - n >= PACKET_SIZE) {
                nextPacketSize = PACKET_SIZE;
            } else {
                nextPacketSize = bytes.length - n;
            }

            os.write(bytes, n, nextPacketSize);

            n = n + nextPacketSize;

            System.out.println("Progress " + n + " of " + bytes.length + " sent ");
        }
        os.flush();
    }

    public static final byte[] receiveBytes(ObjectInputStream is) throws IOException {
        int len = receiveInteger(is);

        byte[] bytes = new byte[len];


        int n = 0;

        while (n < bytes.length) {
            int nextPacketSize;
            if (bytes.length - n >= PACKET_SIZE) {
                nextPacketSize = PACKET_SIZE;
            } else {
                nextPacketSize = bytes.length - n;
            }

            n += is.read(bytes, n, nextPacketSize);

            System.out.println("Progress " + n + " of " + bytes.length + " received ");
        }


        return bytes;
    }
}
