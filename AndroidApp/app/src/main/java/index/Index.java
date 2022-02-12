package index;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import media.Video;

public class Index extends TreeSet<Video> {

    public static final List<String> validExtensions = Arrays.asList("mp4", "avi", "mkv");
    public static int id = 0;

    public final String username;

    public Index(String username) {
        this.username = username;
    }

    public Video loadFromDisk(String uploadDirectory) throws IOException {
        File root = new File(uploadDirectory);

        Video latestvideo = null;

        for (File channelRoot : root.listFiles()) {
            if (channelRoot.isDirectory()) {
                for (File file : channelRoot.listFiles()) {
                    String s = Files.getFileExtension(file.getName());

                    if (validExtensions.contains(Files.getFileExtension(file.getName()))) {
                        String topic = username +":" + channelRoot.getName();
                        System.out.println("Indexing ... " + file.getCanonicalPath() + " on topic:"  + topic);

                        boolean duplicate = false;

                        for (Video v : this) {
                            if (v.path.equals(file.getCanonicalPath())) {
                                duplicate = true;
                                break;
                            }
                        }

                        if (!duplicate) {
                            Video video = new Video(++id, file.getName(), file.getCanonicalPath(), topic);
                            this.add(video);
                            latestvideo = video;
                        }
                    }
                }
            }
        }

        return latestvideo;
    }

    @Override
    public String toString() {
        String buffer = "";
        for (Video v : this) {
            buffer += v.toString() + "\n";
        }
        return buffer;
    }

    public Video findByPosition(int id) {
        return (Video) this.toArray()[id];
    }

    public void loadFromDownloadsDisk(String downloadDirectory) throws IOException {
        File root = new File(downloadDirectory);

        for (File usernameRoot : root.listFiles()) {
            for (File channelRoot : usernameRoot.listFiles()) {
                if (channelRoot.isDirectory()) {
                    for (File file : channelRoot.listFiles()) {
                        String username = usernameRoot.getName();

                        if (validExtensions.contains(Files.getFileExtension(file.getName()))) {
                            String topic = username + ":" + channelRoot.getName();
                            System.out.println("Indexing ... " + file.getCanonicalPath() + " on topic:" + topic);

                            boolean duplicate = false;

                            for (Video v : this) {
                                if (v.path.equals(file.getCanonicalPath())) {
                                    duplicate = true;
                                    break;
                                }
                            }

                            if (!duplicate) {
                                Video video = new Video(++id, file.getName(), file.getCanonicalPath(), topic);
                                this.add(video);
                            }
                        }
                    }
                }
            }
        }
    }
}

