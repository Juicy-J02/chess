package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import service.*;

public class UserHandler {

    private final UserService userService;
    private final Gson serializer = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(Context ctx) throws DataAccessException {
        UserData user = serializer.fromJson(ctx.body(), UserData.class);
        RegisterRequest registerRequest = new RegisterRequest(user.getUsername(), user.getPassword(), user.getEmail());

        RegisterResult result = userService.register(registerRequest);
        ctx.json(result);
    }

    public void loginUser(Context ctx) throws DataAccessException {
        UserData user = serializer.fromJson(ctx.body(), UserData.class);
        LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());

        LoginResult result = userService.login(loginRequest);
        ctx.json(result);
    }

    public void logoutUser(Context ctx) throws DataAccessException {
        String authToken = ctx.header("Authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);

        userService.logout(logoutRequest);
        ctx.status(200);
    }
}
