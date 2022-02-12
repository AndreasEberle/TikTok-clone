package media;

import java.io.Serializable;

public class Video implements Comparable<Video>, Serializable {
    public final int id;
    public final String filename;
    public final String path;
    public final String topic;

    public Video(int id, String filename, String path, String topic) {
        this.id = id;
        this.filename = filename;
        this.path = path;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return String.format("%5d %20s %15s %20s", id, topic, filename, path);
    }

    @Override
    public int compareTo(Video o) {
        return Integer.compare(this.id, o.id);
    }
}
