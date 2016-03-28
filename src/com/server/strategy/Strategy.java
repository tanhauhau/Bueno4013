package com.server.strategy;

import com.server.pack.OneByteInt;
import com.server.pack.Pack;
import com.server.pack.Unpack;
import com.server.socket.Socket;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by lhtan on 22/3/16.
 * This Class is an abstract class for all the daughter strategy classes
 * Methods implemented can be used by daughter classes, or overrided
 * Few methods are not implemented and mandatory to be implemented
 * by daughter classes
 */

public abstract class Strategy {

    protected final Unpack unpack;

    protected static final String REQUEST_TYPE = "request";
    public static final String REQUEST_ID = "id";

    /**
     * Class Constructer for Strategy
     *
     * Initialize the object unpack which will be using for unmarshalling messages
     * from client
     *
     * @param unpack
     */

    protected Strategy(Unpack unpack){
        this.unpack = new Unpack.Builder()
                        .setType(REQUEST_TYPE, Unpack.TYPE.ONE_BYTE_INT)
                        .setType(REQUEST_ID, Unpack.TYPE.LONG)
                        .build()
                        .include(unpack);
    }

    protected abstract byte[] handle(Request request) throws IOException;


    public Unpack.Result unpack(byte[] data){
        return unpack.parseByteArray(data);
    }

    public byte[] handle(InetAddress senderAddress, int senderPort, Socket socket, Unpack.Result data) throws IOException{
        return handle(new Request(senderAddress, senderPort, data, socket));
    }

    protected byte[] reply(long requestID, int status, byte[] data) throws IOException {
        return new Pack.Builder()
                .setValue("status", new OneByteInt(status))
                .setValue("id", requestID)
                .setValue("data", data)
                .build()
                .getByteArray();
    }
    protected byte[] replyError(long requestID, String errorMessage) throws IOException {
        return reply(requestID, 1, errorMessage.getBytes());
    }
    protected byte[] replySuccess(long requestID, String replyMessage) throws IOException {
        return reply(requestID, 0, replyMessage.getBytes());
    }
    protected byte[] replySuccess(long requestID, byte[] data) throws IOException {
        return reply(requestID, 0, data);
    }
    protected byte[] replySuccess(long requestID, Pack pack) throws IOException {
        return new Pack.Builder()
                .setValue("status", new OneByteInt(0))
                .setValue("id", requestID)
                .build()
                .include(pack)
                .getByteArray();
    }
    protected boolean ifAnyNull(Object... check) throws IOException{
        for (Object c : check) {
            if (c == null) {
                return true;
            }
        }
        return false;
    }

     protected class Request{
        private InetAddress mAddress;
        private int mPort;
        private Unpack.Result mData;
        private Socket mSocket;
        private long mRequestID;
        private int mRequestType;

        public Request(InetAddress mAddress, int mPort, Unpack.Result mData, Socket socket) {
            this.mAddress = mAddress;
            this.mPort = mPort;
            this.mData = mData;
            this.mSocket = socket;
            this.mRequestType = this.mData.getOneByteInt(REQUEST_TYPE).getValue();
            this.mRequestID = this.mData.getLong(REQUEST_ID);
        }
        public long getRequestID() {
            return mRequestID;
        }
        public int getRequestType() {
            return mRequestType;
        }

        public int getPort() {
             return mPort;
         }

        public InetAddress getAddress() {
             return mAddress;
         }

        public Socket getSocket() {
            return mSocket;
        }
        public Unpack.Result getData() {
            return mData;
        }
    }
}
