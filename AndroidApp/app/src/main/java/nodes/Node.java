package nodes;

import android.os.Environment;

import java.io.File;

public class Node {
    public final String APPNAME = "APPNODE";
    public final String username;
    public final String uploadDirectory;
    public final String downloadDirectory;
    public final int port;
    public final File ROOT;

    public Node(String username, int port) {
        ROOT = Environment.getExternalStoragePublicDirectory(APPNAME);

        this.username = username;
        this.port = port;
        this.uploadDirectory =  ROOT + File.separator + username.toLowerCase() + File.separator  + "upload";
        this.downloadDirectory = ROOT + File.separator + username.toLowerCase() + File.separator  + "download";
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
        if (!ROOT.exists()) {
            if (!ROOT.mkdirs()) {
                throw new IllegalArgumentException("Storage I/O Error! could not create ROOT directory");
            }
        }

        File file= new File(uploadDirectory);
        if (file.exists() && !file.isDirectory()) {
            error("Home directory already used as file");
        }
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalArgumentException("Storage I/O Error! could not create data directory");
            }
        }

        print("Checking download directory ...");

        File file2= new File(uploadDirectory);
        if (file2.exists() && !file2.isDirectory()) {
            error("Home directory already used as file");
        }
        if (!file2.exists()) {
            if (!file2.mkdirs()) {
                throw new IllegalArgumentException("Storage I/O Error! could not create data directory");
            }
        }
    }
}
