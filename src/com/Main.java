package com;

import com.client.Client;
import com.client.cache.FileCache;
import com.client.cache.Cache;
import com.client.strategy.*;

import java.util.Scanner;

/**
 * Created by lhtan on 22/3/16.
 */
public class Main {
    public static void main(String[] args) {
        //hide those mahuan stuff
        Console.showInfo = true;

        Client client = null;
        Scanner reader = new Scanner(System.in);
        Console console = new Console(reader);
        String cacheFolder = "/Users/bert1/Downloads/Distributed/Client/data";


        Cache cache = new FileCache(cacheFolder, 30);
        try {
            client = new Client("10.27.124.59", 6789, 5000) //30000)  //30 second timeout
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
                        //.makeItKisiao(.2)
//                        .makeItNoobInReceiving(.3)
//                        .makeItNoobInSending(.8)
//                        .makeItLag(1000)
                        ;
            mainLoop: while(true) {
                int option = printMenu(reader, client);
                if (option == 99){
                    break mainLoop;
                }
                //execute
                client.execute(option, console);
                //
                System.out.println();
            }

        }catch (Exception e){
            Console.info("Fuck why got error!!! CiBaI");
            e.printStackTrace();
        }finally {
            if (client != null){
                client.stop();
            }
        }
    }
    public static int printMenu(Scanner scanner, Client client){
        int option = -1;
        while(option <= 0 || option > 99) {
            Console.println("=== This is the main menu of Bobo ===");
            Console.println("Choose any of the following option");
            client.printMenu();
            Console.println("99. Exit");
            try {
                option = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
            }
        }
        return option;
    }
}
