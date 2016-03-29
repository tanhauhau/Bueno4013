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
 * This Class is an abstract class for all the daughter strategy classes
 * Methods implemented can be used by daughter classes, or overrided
 * Few methods are not implemented and mandatory to be implemented
 * by daughter classes
 */

public abstract class Strategy {

    private static final String STATUS = "status";
    protected static final String REQUEST_ID = "id";
    private final Unpack unpack;

    /**
     * Class Constructor for Strategy
     * <p/>
     * Initialize the object unpack which will be using for unmarshalling messages
     * from client
     *
     * @param unpack Unmarshalling object
     */
    protected Strategy(Unpack unpack) {
        this.unpack = new Unpack.Builder()
                .setType(STATUS, Unpack.TYPE.ONE_BYTE_INT)
                .setType(REQUEST_ID, Unpack.TYPE.LONG)
                .build()
                .include(unpack);
    }

    /**
     * Return the hashmap containing the component of the byte array
     * after parsing
     *
     * @param data bytearray for UDP communication
     * @return hashmap
     */
    final protected Unpack.Result unpack(byte[] data) {
        return this.unpack.parseByteArray(data);
    }

    /**
     * Check the status of the result
     *
     * @param result Result from the byte array
     * @return boolean true or false
     */
    final protected boolean isStatusOK(Unpack.Result result) {
        OneByteInt status = result.getOneByteInt(STATUS);
        if (status != null) {
            return status.getValue() == 0;
        }
        return false;
    }

    /**
     * This method check whether the Request ID is equal
     *
     * @param result Result from the byte array
     * @param id     Request ID
     * @return boolean true or false
     */
    final protected boolean isRequestIdEqual(Unpack.Result result, long id) {
        Long requestId = result.getLong(REQUEST_ID);
        if (requestId != null) {
            return requestId == id;
        }
        return false;
    }

    /**
     * This method will resend request to server, if
     * there is no reply from server after a pre-defined
     * timeout
     *
     * @param client    Client object
     * @param pack      Marshalling object
     * @param requestID Request ID
     * @return
     * @throws IOException
     */
    final protected Unpack.Result keepTryingUntilReceive(Client client, Pack pack, long requestID) throws IOException {
        while (true) {
            try {
                DatagramPacket packet = client.receive();
                Unpack.Result result = unpack(packet.getData());
                if (isRequestIdEqual(result, requestID)) {
                    return result;
                }
            } catch (SocketTimeoutException e) {
                Console.info("  Strategy >> Timeout");
                Console.info("  Strategy >> Resend");
                client.send(pack);
            }
        }
    }

    /**
     * Compulsory method to be implemented by the daughter classes
     * Main method of the daughter classes
     * Services provided by the client side
     *
     * @param scanner Console Scanner
     * @param client  Client object
     * @throws IOException
     */
    public abstract void serviceUser(Console scanner, Client client) throws IOException;

    public abstract String getTitle();
}
