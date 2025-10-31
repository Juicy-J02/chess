package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

public class GameHandler {

    private final GameService gameService;
    private final Gson serializer = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listAllGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            GameListRequest gameListRequest = new GameListRequest(authToken);
            GameListResult result = gameService.getGames(gameListRequest);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(500).json(new Message(e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            CreateGameRequest createGameRequest = serializer.fromJson(ctx.body(), CreateGameRequest.class);
            if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                ctx.status(400).json(new Message("Error: game name required"));
                return;
            }

            CreateGameResult result = gameService.createGame(createGameRequest, authToken);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(500).json(new Message(e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            JoinGameRequest joinGameRequest = serializer.fromJson(ctx.body(), JoinGameRequest.class);

            if (joinGameRequest.gameID() == null || joinGameRequest.gameID() <= 0 ||
                    joinGameRequest.playerColor() == null ||
                    (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK"))) {
                ctx.status(400).json(new Message("Error: bad request"));
                return;
            }

            gameService.joinGame(joinGameRequest, authToken);
            ctx.status(200);

        } catch (DataAccessException e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("already taken")) {
                ctx.status(403).json(new Message(e.getMessage()));
            } else if (msg.contains("not found")) {
                ctx.status(400).json(new Message(e.getMessage()));
            } else if (msg.contains("no auth")) {
                ctx.status(401).json(new Message(e.getMessage()));
            } else {
                ctx.status(500).json(new Message(e.getMessage()));
            }
        }
    }

    private static class Message {
        public final String message;
        public Message(String message) {
            this.message = message;
        }
    }
}
