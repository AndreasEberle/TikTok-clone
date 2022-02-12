package hashing;

import data.BrokerData;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class HashingCalculator {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static byte[] digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return result;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hash(String input) {
        byte[] md5InBytes = digest(input.getBytes(UTF_8));
        return bytesToHex(md5InBytes);
    }

    public static byte[] rawhash(String input) {
        byte[] md5InBytes = digest(input.getBytes(UTF_8));
        return md5InBytes;
    }

    public static BrokerData getBrokerData(Set<BrokerData> data, String topic) {
        String h = hash(topic);

        for (BrokerData d : data) {
            if (h.compareTo(d.hash) == -1) {
                return d;
            }
        }

        int m = data.size();

        BigInteger bi = new BigInteger(h, 16).mod(new BigInteger(""+m,10));

        int n = bi.intValue();


        for (BrokerData d : data) {
            if (n == 0) {
                return d;
            } else {
                n--;
            }
        }

        return null;
    }
}

