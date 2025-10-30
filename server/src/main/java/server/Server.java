package server;

import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {

        UserDAO userDataAccess = new UserDataAccessSQL();
        GameDAO gameDataAccess = new GameDataAccess();
        AuthDAO authDataAccess = new AuthDataAccess();

        UserService userService = new UserService(userDataAccess, authDataAccess);
        GameService gameService = new GameService(authDataAccess, gameDataAccess);
        ClearService clearService = new ClearService(userDataAccess, authDataAccess, gameDataAccess);

        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
        javalin.delete("/session", userHandler::logoutUser);

        javalin.get("/game", gameHandler::listAllGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.delete("/db", clearHandler::clearDatabase);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
