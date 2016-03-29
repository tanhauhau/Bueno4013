package com;

import com.server.callback.Callback;
import com.server.server.AtMostOnceServer;
import com.server.server.Server;
import com.server.strategy.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Created by lhtan on 22/3/16.
 */
public class Main {

    private final static String AT_LEAST_ONCE = "AT-LEAST-ONCE";
    private final static String AT_MOST_ONCE = "AT-MOST-ONCE";

    /**
     * @param args
     */
    public static void main(String[] args) {
        //parse arguments from command line
        Namespace ns = parseArgument(args);
        if (ns == null) System.exit(1);

        final String folder = ns.getString("folder");
        final int port = ns.getInt("port");
        final String invocationMethod = ns.getString("invocation");

        Server server = null;
        try {
            Callback callback = new Callback(folder);

            if (invocationMethod.equals(AT_MOST_ONCE)){
                System.out.println("At-Most-Once Server started");
                server = new AtMostOnceServer(port);
            }else{
                System.out.println("At-Least-Once Server start");
                server = new Server(port);
            }

            server.use(0, new PingStrategy())
                  .use(1, new ReadingStrategy(folder))
                  .use(2, new WritingStrategy(folder, callback))
                  .use(3, new RegisterStrategy(folder, callback))
                  .use(4, new SizeStrategy(folder))
                  .use(5, new DoubleStrategy(folder, callback))
                  .use(6, new LastModifiedStrategy(folder))
                  .use(7, new CreateFileStrategy(folder));

            if (ns.getInt("lag") > 0) {
                server.makeItLag(ns.getInt("lag"));
            }
            if (ns.getDouble("gibberish") > 0){
                server.makeItSendGibberish(1 - Math.max(1, ns.getDouble("gibberish")));
            }
            if (ns.getDouble("send") > 0){
                server.makeItPacketLossWhenSending(1 - Math.max(1, ns.getDouble("send")));
            }
            server.start();
        }catch (Exception e){
            System.out.println("Server error");
            e.printStackTrace();
        }finally {
            if (server != null){
                server.stop();
            }
        }
    }

    private static Namespace parseArgument(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Server");
        parser.defaultHelp(true)
                .description("Start a distributed server.");
        parser.addArgument("-p", "--port")
                .type(Integer.class)
                .required(true)
                .help("Specify the port for the UDP Socket");
        parser.addArgument("-iv", "--invocation")
                .choices(AT_LEAST_ONCE, AT_MOST_ONCE)
                .setDefault(AT_LEAST_ONCE)
                .help("Specify the invocation semantics");
        parser.addArgument("-f", "--folder")
                .nargs(1)
                .required(true)
                .help("Folder storing data");
        parser.addArgument("-l","--lag")
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
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            return null;
        }
    }
}
