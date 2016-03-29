package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 23/3/16.
 * This class is an example of non-idempotent request
 * This class enable Client to duplicate the entire content of
 * a certain file
 */
public class DoubleStrategy extends Strategy {

    public DoubleStrategy() {
        super(null);
    }

    /*
        This is an example of non-idempotent request

     */

    /**
     * When client select this options towards a certain file
     * the server will duplicate the entire content of the file,
     * and double the size of the file
     *
     * @param scanner       Console Scanner
     * @param client        Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Filename");
        long messageId = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.DOUBLE_REQUEST))
                .setValue("id", messageId)
                .setValue("filename", filename)
                .build();

        client.send(request);

        Unpack.Result result = keepTryingUntilReceive(client, request, messageId);

        if (isStatusOK(result)) {
            Console.println("  DoubleStrategy >> Success");
        }else{
            Console.println("  DoubleStrategy >> Failed");
        }
    }

    @Override
    public String getTitle() {
        return "Non-idempotent, double the excitement!";
    }
}
