package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;
import java.util.Date;

/**
 * Created by lhtan on 23/3/16.
 */
public class LastModifiedStrategy extends Strategy {

    private final static String TIME = "time";

    public LastModifiedStrategy() {
        super(new Unpack.Builder()
                .setType(TIME, Unpack.TYPE.LONG)
                .build());
    }
    /*
        This is an example of idempotent request
        User can check the the last time where the file was previously edited
        provided there is no other users access the file during this process
     */

    public long lastUpdate(Client client, String filename) throws IOException{
        long messageId = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.LAST_MODIFIED_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (!isStatusOK(result)) {
            return -1;
        }
        return result.getLong(TIME);
    }

    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Name of that file");
        long time = lastUpdate(client, filename);
        if (time == -1){
            Console.println("  LastModifiedStrategy >> Server error occured");
        }else {
            Date date = new Date(time);
            Console.println(String.format("  LastModifiedStrategy >> Last modified time for %s is %s", filename, date.toString()));
        }
    }

    @Override
    public String getTitle() {
        return "The last time you modified the file";
    }
}
