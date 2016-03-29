package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.cache.Cache;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

/**
 * Created by lhtan on 23/3/16.
 * This class enable Client to register themselves
 * into callback of files they desired for
 * a certain timeout.
 * During the monitoring interval, any changes made to
 * the file will be informed to the Clients.
 * During this monitoring interval, registered clients
 * are not able to write anything to the file
 */
public class RegisterStrategy extends Strategy {

    private final static String STATUS = "datastatus";
    private final static String STATUS2 = "datastatus2";
    private final static String DATA = "data";
    private final Cache cache;

    /**
     * Class Constructor for RegisterStrategy Class
     * @param cache     Cache object on Client's side
     */
    public RegisterStrategy(Cache cache) {
        super(new Unpack.Builder()
                .setType(STATUS, Unpack.TYPE.ONE_BYTE_INT)
                .setType(DATA, Unpack.TYPE.STRING)
                .setType(STATUS2, Unpack.TYPE.ONE_BYTE_INT)
                .build());
        this.cache = cache;
    }

    /*

     */

    /**
     * This method registers the client for a callback of a
     * certain file for a certain duration
     * during the callback duration, client can monitor the file,
     * if there is any changes in the file, the changes will be
     * shown to the client until timeout
     *
     * @param scanner       Console Scanner
     * @param client        Client object
     * @throws IOException
     */
    @Override
    public void serviceUser(Console scanner, Client client) throws IOException {
        String filename = scanner.askForString("Callback for which file?");
        int timeout = scanner.askForInteger("Durations(seconds)?");

        long requestID = client.getMessageId();
        Pack request = new Pack.Builder()
                .setValue("request", new OneByteInt(Client.REGISTER_REQUEST))
                .setValue("id", requestID)
                .setValue("filename", filename)
                .setValue("timeout", timeout)
                .build();

        client.send(request);

        Unpack.Result registerResult = keepTryingUntilReceive(client, request, requestID);
        if (!isStatusOK(registerResult)){
            Console.println("  RegisterStrategy >> No Such File");
            return;
        }else{
            Console.println("  RegisterStrategy >> Start observing file change");
        }

        long timeStart = System.currentTimeMillis();        /* Calculate the time for timeout */
        long timeEnd = timeStart + timeout * 1000; //timeout * 1000ms

        while (System.currentTimeMillis() < timeEnd) {      /* Monitoring the file during the interval of callback */
            try {
                client.setTimeout((int) (timeEnd - System.currentTimeMillis()));

                DatagramPacket packet = client.receive();
                Unpack.Result result = unpack(packet.getData());

                if (!isStatusOK(result)) continue;
                if (!isRequestIdEqual(result, requestID)) continue;

                OneByteInt status = result.getOneByteInt(STATUS);
                String data = result.getString(DATA);
                OneByteInt status2 = result.getOneByteInt(STATUS2);

                if (!checkValidity(status, status2)) continue;
                if (data == null)   continue;

                Console.println(String.format("  RegisterStrategy >> DataChanged: '%s'", data)); /* Inform the registered client about the changes made */
                cache.writeToCache(filename, data);
            }catch (SocketTimeoutException e){
                Console.info("  RegisterStrategy >> Timeout");
            }
        }

        client.resetTimeout();
    }

    private boolean checkValidity(OneByteInt s1, OneByteInt s2){
        return s1 != null && s2 != null && (s1.getValue() & 0XFF) == 0b01010101 && (s2.getValue() & 0XFF) == 0b10101010;
    }

    @Override
    public String getTitle() {
        return "Register for File Callback";
    }
}
