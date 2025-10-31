package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class AuthErrorBlock {

    public AuthErrorBlock(String msg, Context ctx, DataAccessException e) {
        if (msg.contains("no auth")) {
            ctx.status(401).json(new Message(e.getMessage()));
        } else {
            ctx.status(500).json(new Message(e.getMessage()));
        }
    }

    private record Message(String message) {
    }
}
