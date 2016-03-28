package com.server.strategy;

import com.server.pack.Unpack;

import java.io.IOException;

/**
 * Created by lhtan on 22/3/16.
 */
/*
    Reply to the client with the content it sends
 */

public class PingStrategy extends Strategy {

    private static final String MESSAGE = "message";

    public PingStrategy() {
        super(new Unpack.Builder().setType(MESSAGE, Unpack.TYPE.STRING).build());
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        Unpack.Result result = request.getData();

        String message = result.getString(MESSAGE);
        if (ifAnyNull(message)){
            return replyError(request.getRequestID(), "Corrupted data");
        }else {
            System.out.println(String.format("  PingStrategy >> Received message: %s", message));
            return replySuccess(request.getRequestID(), message);
        }
    }
}
