import nodes.BrokerNode;

import java.util.Scanner;

public class Main {
    private static void printHelp() {
        System.out.println("Instructions: ");
        System.out.println("-------------------");
        System.out.println(" > brokers: brokers (b)");
        System.out.println(" > data: data (d)");
        System.out.println(" > channels: channels (c)");
        System.out.println(" > help: help (h)");
    }

    public static void main(String args[]) {
        if (args.length != 2) {
            System.out.println("Invalid command line arguments");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);

            BrokerNode node = new BrokerNode(ip, port);

            System.out.println("_____________________________________________________");
            node.printBrokers();
            System.out.println("_____________________________________________________");

            node.startServer();



            printHelp();

            while (true) {
                System.out.println("Broker: " + node.username + " - Type your command (type help for help)");
                String cmd = scanner.nextLine().toLowerCase();

                if (cmd.equals("help") || cmd.equals("h")) {
                   printHelp();
                }


                if (cmd.equals("data") || cmd.equals("d")) {
                    node.printData();

                }
                if (cmd.equals("channels") || cmd.equals("c")) {
                    node.printChannels();

                }

                if (cmd.equals("b") || cmd.equals("b")) {
                    System.out.println("_____________________________________________________");
                    node.printBrokers();
                    System.out.println("_____________________________________________________");
                }

                if (cmd.equals("list") || cmd.equals("l")) {

                }

                if (cmd.equals("0") || cmd.equals("exit") || cmd.equals("e")) {
                    break;
                }
            }

            node.stopServer();

            System.out.println("Node exit successful");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
