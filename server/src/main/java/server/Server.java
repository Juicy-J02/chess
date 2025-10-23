package server;

import dataaccess.*;
import io.javalin.Javalin;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDataAccess;
    private final GameDAO gameDataAccess;
    private final AuthDAO authDataAccess;

    public Server() {
        userDataAccess = new UserDataAccess();
        gameDataAccess = new GameDataAccess();
        authDataAccess = new AuthDataAccess();

        UserService userService = new UserService(userDataAccess, authDataAccess);
        GameService gameService = new GameService(authDataAccess, gameDataAccess);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
        javalin.delete("/session", userHandler::logoutUser);

        javalin.get("/game", gameHandler::listAllGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.delete("/db", ctx -> {
            userDataAccess.clearUserData();
            gameDataAccess.clearGames();;
            authDataAccess.clearAuthData();

            ctx.status(200).json(new Object() {
                public final String message = "Database cleared";
            });
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
