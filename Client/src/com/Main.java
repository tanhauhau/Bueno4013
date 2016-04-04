package com;

import com.client.Client;
import com.client.cache.Cache;
import com.client.cache.FileCache;
import com.client.strategy.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Scanner;

/**
 * Created by lhtan on 22/3/16.
 */
public class Main {
    public static void main(String[] args) {
        //parse arguments from command line
        Namespace ns = parseArgument(args);
        if (ns == null) System.exit(1);

        final String socketIPAddress = ns.getString("ip");
        final int socketPort = ns.getInt("port");
        final int socketTimeout = ns.getInt("timeout");
        final String cacheFolder = ns.getString("folder");
        final int cacheFreshness = ns.getInt("cachefresh");

        //show or hide info
        Console.showInfo = ns.getBoolean("info");
        Console console = new Console(new Scanner(System.in));

        Client client = null;
        Cache cache = new FileCache(cacheFolder, cacheFreshness);
        try {
            client = new Client(socketIPAddress, socketPort, socketTimeout * 1000)
                    .use(1, new PingStrategy())
                    .use(2, new ReadingStrategy())
                    .use(3, new WritingStrategy())
                    .use(4, new RegisterStrategy(cache))
                    .use(5, new SizeStrategy())
                    .use(6, new DoubleStrategy())
                    .use(7, new LastModifiedStrategy())
                    .use(8, new CacheStrategy(cache))
                    .use(9, new CacheReadingStrategy(cache))
                    .use(10, new CacheWritingStrategy(cache))
                    .use(11, new CacheDetailStrategy(cache))
                    .use(12, new CreateFileStrategy(cache));

            if (ns.getInt("lag") > 0) {
                client.makeItLag(ns.getInt("lag"));
            }
            if (ns.getDouble("gibberish") > 0) {
                client.makeItSendGibberish(1 - Math.min(1, ns.getDouble("gibberish")));
            }
            if (ns.getDouble("send") > 0) {
                client.makeItPacketLossWhenSending(1 - Math.min(1, ns.getDouble("send")));
            }
            if (ns.getDouble("receive") > 0) {
                client.makeItPacketLossWhenReceiving(1 - Math.min(1, ns.getDouble("receive")));
            }
            while (true) {
                int option = printMenu(console, client);
                if (option == 99) {
                    break;
                }
                //execute
                client.execute(option, console);
                //
                System.out.println();
            }

        } catch (Exception e) {
            Console.info("Hey why there is an error?!");
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.stop();
            }
        }
    }

    private static int printMenu(Console scanner, Client client) {
        int option = -1;
        while (option <= 0 || option > 99) {
            Console.println("=== This is the main menu of Services ===");
            Console.println("Choose any of the following option");
            client.printMenu();
            Console.println("99. Exit");
            option = scanner.askForInteger();
        }
        return option;
    }

    private static Namespace parseArgument(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Client");
        parser.defaultHelp(true)
                .description("Start a distributed client.");
        parser.addArgument("-ip")
                .type(String.class)
                .required(true)
                .help("Specify the ip address for the UDP Server");
        parser.addArgument("-p", "--port")
                .type(Integer.class)
                .required(true)
                .help("Specify the port for the UDP Server");
        parser.addArgument("-t", "--timeout")
                .type(Integer.class)
                .required(true)
                .help("Specify the timeout for the UDP Socket in second");
        parser.addArgument("-f", "--folder")
                .required(true)
                .help("Folder storing cached data");
        parser.addArgument("-cf", "--cachefresh")
                .type(Integer.class)
                .required(true)
                .help("Freshness of cache in second");
        parser.addArgument("-l", "--lag")
                .type(Integer.class)
                .required(false)
                .setDefault(0)
                .help("Make the server lag for specified seconds");
        parser.addArgument("-g", "--gibberish")
                .type(Double.class)
                .required(false)
                .setDefault(0.0)
                .help("Specify the probability of replying gibberish message to the client");
        parser.addArgument("-s", "--send")
                .type(Double.class)
                .required(false)
                .setDefault(0.0)
                .help("Specify the probability of packet loss when sending packets");
        parser.addArgument("-r", "--receive")
                .type(Double.class)
                .required(false)
                .setDefault(0.0)
                .help("Specify the probability of packet loss when receiving packets");
        parser.addArgument("-i", "--info")
                .type(Boolean.class)
                .required(false)
                .setDefault(false)
                .help("Show debug message");
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            return null;
        }
    }
}
