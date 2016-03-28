package com.server.strategy;

import com.server.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 22/3/16.
 * PingStrategy class handles client's request to ping
 * by returning the content client sent
 */


public class PingStrategy extends Strategy {

    private static final String MESSAGE = "message";

    /**
     * Class Constructor for PingStrategy
     */
    public PingStrategy() {
        super(new Unpack.Builder().setType(MESSAGE, Unpack.TYPE.STRING).build());
    }

    /**
     * Handle method inherited from Parent Strategy class
     * This class return the content client sent,
     * back to client, such that the server received the
     * content and resend back as a ping
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();

        String message = result.getString(MESSAGE);     /* Acquire the message where client sent */
        if (ifAnyNull(message)){
            return replyError(request.getRequestID(), "Corrupted data");    /* Return error message if the message is NULL */
        }else {
            System.out.println(String.format("  PingStrategy >> Received message: %s", message)); /* Return content with success message */
            return replySuccess(request.getRequestID(), message);
        }
    }
}
