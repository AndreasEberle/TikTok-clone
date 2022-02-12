package data;

import hashing.HashingCalculator;

import java.io.Serializable;

public class BrokerData implements Comparable<BrokerData>, Serializable {

    public final String ip;
    public final int port;
    public final String hash;
    public final byte[] bytes;


    public BrokerData(String ip, int port) {
        this.ip = ip;
        this.port = port;

        hash = HashingCalculator.hash(ip + ":" + port);
        bytes = HashingCalculator.rawhash(ip + ":" + port);
    }

    @Override
    public String toString() {
        return String.format("Broker: %s:%d:%s", ip, port, hash);
    }


    @Override
    public int compareTo(BrokerData o) {
        return hash.compareTo(o.hash);
    }
}
