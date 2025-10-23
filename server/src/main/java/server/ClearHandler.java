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
            ctx.status(500).json(new Message("Error: " + e.getMessage()));
        }
    }

    private static class Message {
        public final String message;
        public Message(String message) {
            this.message = message;
        }
    }
}
