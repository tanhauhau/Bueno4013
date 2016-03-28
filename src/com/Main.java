package com;

//putang ina mo
import com.server.callback.Callback;
import com.server.server.AtMostOnceServer;
import com.server.server.Server;
import com.server.strategy.*;

/**
 * Created by lhtan on 22/3/16.
 */
public class Main {

    public static void main(String[] args) {
        String folder = "/Users/lhtan/IdeaProjects/Server/data";
        Server server = null;
        try {
            System.out.println("Server start");

            Callback callback = new Callback(folder);

            server = new AtMostOnceServer(6789) //new Server(6789)
//                         .makeItLag(6000)
//                         .useNormalSocket()
                         .use(0, new PingStrategy())
                         .use(1, new ReadingStrategy(folder))
                         .use(2, new WritingStrategy(folder, callback))
                         .use(3, new RegisterStrategy(folder, callback))
                         .use(4, new SizeStrategy(folder))
                         .use(5, new DoubleStrategy(folder, callback))
                         .use(6, new LastModifiedStrategy(folder));

            server.start();
        }catch (Exception e){
            System.out.println("Fuck why server got error!!! CiBaI");
            e.printStackTrace();
        }finally {
            if (server != null){
                server.stop();
            }
        }
    }
}
