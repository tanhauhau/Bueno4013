package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 */
public class SizeStrategy extends Strategy {

    private final static String SIZE = "size";

    public SizeStrategy() {
        super(new Unpack.Builder()
                .setType(SIZE, Unpack.TYPE.LONG)
                .build());
    }

    /*
        This is an example of idempotent request
        where client request to check the size of a certain file,
        provided during this period, there is no other users accessing the file
     */

    public long getFileSize(Client client, String filename) throws IOException{
        long messageId = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.SIZE_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (isStatusOK(result)) {
            return result.getLong(SIZE);
        }else{
            return -1;
        }
    }

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of the file");
        
        long size = getFileSize(client, filename);
        if (size == -1){
            Console.println("  SizeStrategy >> Not available");
        }else {
            Console.println(String.format("  SizeStrategy >> Size: %s bytes", size));
        }
    }

    @Override
    public String getTitle() {
        return "Size of the file";
    }
}
