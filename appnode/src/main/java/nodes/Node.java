package nodes;

import java.io.File;

public class Node {
    public final String username;
    public final String uploadDirectory;
    public final String downloadDirectory;
    public final int port;

    public Node(String username, int port) {
        this.username = username;
        this.port = port;
        this.uploadDirectory =  "data" + "/" + username.toLowerCase() + "/" + "upload";
        this.downloadDirectory = "data" + "/" + username.toLowerCase() + "/" + "download";
    }

    public void print(String data) {
        System.out.println("[" + username + "]" + ": "  + data);
    }

    public void error(String data) {
        System.out.println("[" + username + "]" + ":  CRITICAL ERROR: "  + data);
        System.exit(1);
    }

    protected void initialize() {
        print("Checking upload directory ...");
        File file= new File(uploadDirectory);
        if (file.exists() && !file.isDirectory()) {
            error("Home directory already used as file");
        }
        if (!file.exists()) {
            file.mkdir();
        }

        print("Checking download directory ...");

        File file2= new File(uploadDirectory);
        if (file2.exists() && !file2.isDirectory()) {
            error("Home directory already used as file");
        }
        if (!file2.exists()) {
            file2.mkdir();
        }
    }
}
