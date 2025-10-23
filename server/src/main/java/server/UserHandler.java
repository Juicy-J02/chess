package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

public class UserHandler {

    private final UserService userService;
    private final Gson serializer = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(Context ctx) {
        try {
            RegisterRequest registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);

            if (registerRequest.username() == null || registerRequest.password() == null) {
                ctx.status(400).json(new Message("Error: bad request"));
                return;
            }

            RegisterResult result = userService.register(registerRequest);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(403).json(new Message(e.getMessage()));
        }
    }

    public void loginUser(Context ctx) {
        try {
            LoginRequest loginrequest = serializer.fromJson(ctx.body(), LoginRequest.class);

            if (loginrequest.username() == null || loginrequest.password() == null) {
                ctx.status(400).json(new Message("Error: bad request"));
                return;
            }

            LoginResult result = userService.login(loginrequest);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(401).json(new Message(e.getMessage()));
        }
    }

    public void logoutUser(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            userService.logout(logoutRequest);
            ctx.status(200);

        } catch (DataAccessException e) {
            ctx.status(401).json(new Message(e.getMessage()));
        }
    }

    private static class Message {
        public final String message;
        public Message(String message) {
            this.message = message;
        }
    }
}
