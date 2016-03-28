package com.server.strategy;

import java.io.IOException;

/**
 * Created by lhtan on 22/3/16.
 */
public class ErrorStrategy extends Strategy {
    public ErrorStrategy() {
        super(null);
    }

    @Override
    protected byte[] handle(Request request) throws IOException {
        System.out.println(String.format("  ErrorStrategy >> Received unknown request %d", request.getRequestType()));
        return replyError(request.getRequestID(), "Cibai what you trying to do??");
    }
}
