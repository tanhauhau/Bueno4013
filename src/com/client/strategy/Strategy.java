package com.client.strategy;

import com.Console;
import com.client.Client;
import com.client.pack.OneByteInt;
import com.client.pack.Pack;
import com.client.pack.Unpack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;

/**
 * Created by lhtan on 22/3/16.
 */

/**
    This class serve as an abstract class for all the Strategy classes
    all abstract classes must be implemented in the daughter classes
    which extends this abstract class
 */
public abstract class Strategy {

    private final Unpack unpack;

    protected static final String STATUS = "status";
    protected static final String REQUEST_ID = "id";

    protected Strategy(Unpack unpack){
        this.unpack = new Unpack.Builder()
                .setType(STATUS, Unpack.TYPE.ONE_BYTE_INT)
                .setType(REQUEST_ID, Unpack.TYPE.LONG)
                .build()
                .include(unpack);
    }

    final protected Unpack.Result unpack(byte[] data){
        return this.unpack.parseByteArray(data);
    }

    /*
        Checking status
     */
    final protected boolean isStatusOK(Unpack.Result result){
        OneByteInt status = result.getOneByteInt(STATUS);
        if (status != null){
            return status.getValue() == 0;
        }
       return false;
    }

    final protected boolean isRequestIdEqual(Unpack.Result result, long id){
        Long requestId = result.getLong(REQUEST_ID);
        if (requestId != null){
            return requestId == id;
        }
        return false;
    }

    /*
        Resend request when reply is not received
        may due to server lag or packet loss
     */
    final protected Unpack.Result keepTryingUntilReceive(Client client, Pack pack, long requestID) throws IOException{
        while(true){
            try {
                DatagramPacket packet = client.receive();
                Unpack.Result result = unpack(packet.getData());
                if (isRequestIdEqual(result, requestID)) {
                    return result;
                }
            }catch(SocketTimeoutException e){
                Console.info("  Strategy >> Timeout");
                Console.info("  Strategy >> Resend");
                client.send(pack);
            }
        }
    }

    /*
        Compulsory method to be implemented by the daughter classes
        Main method of the daughter classes
        Services provided by the client side
     */
    public abstract void serviceUser(Console scanner, Client client) throws IOException;

    public abstract String getTitle();
}
