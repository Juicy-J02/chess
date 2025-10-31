package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class ClearHandler {

    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clearDatabase(Context ctx) {
        try {
            clearService.clearDB();
            ctx.status(200).json(new Message("Data cleared successfully"));
        } catch (DataAccessException e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("no auth")) {
                ctx.status(401).json(new Message(e.getMessage()));
            } else {
                ctx.status(500).json(new Message(e.getMessage()));
            }
        }
    }

    private record Message(String message) {
    }
}
