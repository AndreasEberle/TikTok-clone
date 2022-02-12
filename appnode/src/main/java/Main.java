import nodes.AppNode;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static void printHelp() {
        System.out.println("Instructions: ");
        System.out.println("-------------------");
        System.out.println(" > brokers: brokers (b)");
        System.out.println(" > data: data (d) - display local data");
        System.out.println(" > channels: channels (c) - display remote data for channels");
        System.out.println(" > browse: browse (r) - browse data of a channel");
        System.out.println(" > push: push (p) - push data to remote");
        System.out.println(" > pull: pull (u) - pull data from remote");
        System.out.println(" > auto: auto (a) - test pull data from remote");
        System.out.println(" > help: help (h) - display help");
    }
    public static void main(String args[]) {
        if (args.length != 5) {
            System.out.println("Invalid command line arguments: username localip localport gatewayip gatewayport");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        String username = args[0];
        String localip = args[1];
        int localport = Integer.parseInt(args[2]);

        String gatewayIP = args[3];
        int gatewayPort = Integer.parseInt(args[4]);

        AppNode node = null;


        try {
            node = new AppNode(username, localport);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {

            node.loadIndex();

            node.printIndex();

            node.startServer();

            node.register(gatewayIP, gatewayPort, localip, localport);

            node.printBrokers();

            node.connectToAll(gatewayIP, gatewayPort, localip, localport);

            node.pushAll();

            printHelp();

            while (true) {
                System.out.print("Currently known brokers: " + node.brokers.size() + " - Type your command (type help for help): ");
                String cmd = scanner.nextLine().toLowerCase();

                if (cmd.equals("help") || cmd.equals("h")) {
                   printHelp();
                }

                if (cmd.equals("data") || cmd.equals("d")) {
                    node.printIndex();
                }

                if (cmd.equals("b") || cmd.equals("b")) {
                    node.printBrokers();
                }

                if (cmd.equals("channels") || cmd.equals("c")) {
                    node.printChannels();
                }

                if (cmd.equals("browse") || cmd.equals("r")) {
                    System.out.print("Channel name? ");
                    String channel = scanner.nextLine();
                    node.browse(channel);
                }


//                if (cmd.equals("push") || cmd.equals("p")) {
//                    System.out.println("Video name? ");
//                    String videoname = scanner.nextLine();
//                    node.push(videoname);
//                }

                if (cmd.equals("pull") || cmd.equals("u")) {
                    System.out.print("Channel name? ");
                    String channel = scanner.nextLine();

                    System.out.println("Video name?") ;
                    String videoName = scanner.nextLine();

                    node.pull(channel, videoName);
                }

                if (cmd.equals("auto") || cmd.equals("a")) {
                    String channel = "Han:channel1";
                    String videoName = "han_a.mp4";

                    System.out.println("Pulling: " + channel + " => " + videoName);

                    node.pull(channel, videoName);
                }

                if (cmd.equals("0") || cmd.equals("exit") || cmd.equals("e")) {
                    break;
                }
            }




            System.out.println("Node exit successful");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
            try {
                node.stopServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
