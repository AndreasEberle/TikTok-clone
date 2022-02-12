package index;

import com.google.common.io.Files;
import media.Video;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Index extends TreeSet<Video> {

    public static final List<String> validExtensions = Arrays.asList("mp4", "avi", "mkv");
    public static int id = 0;

    public final String username;

    public Index(String username) {
        this.username = username;
    }

    public void loadFromDisk(String uploadDirectory) throws IOException {
        File root = new File(uploadDirectory);

        for (File channelRoot : root.listFiles()) {
            if (channelRoot.isDirectory()) {
                for (File file : channelRoot.listFiles()) {
                    String s = Files.getFileExtension(file.getName());

                    if (validExtensions.contains(Files.getFileExtension(file.getName()))) {
                        String topic = username +":" + channelRoot.getName();
                        System.out.println("Indexing ... " + file.getCanonicalPath() + " on topic:"  + topic);


                        Video video = new Video(++id, topic, file.getName(), file.getCanonicalPath());
                        this.add(video);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String buffer = "";
        for (Video v : this) {
            buffer += v.toString() + "\n";
        }
        return buffer;
    }
}
