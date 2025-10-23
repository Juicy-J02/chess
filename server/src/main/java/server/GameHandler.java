package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
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
            ctx.status(403).json(new Message(e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            GameData game = serializer.fromJson(ctx.body(), GameData.class);
            if (game.getGameName() == null || game.getGameName().isEmpty()) {
                ctx.status(400).json(new Message("Error: game name required"));
                return;
            }

            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, game.getGameName());
            CreateGameResult result = gameService.createGame(createGameRequest);
            ctx.status(200).json(result);

        } catch (DataAccessException e) {
            ctx.status(403).json(new Message(e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null) {
                ctx.status(401).json(new Message("Error: unauthorized"));
                return;
            }

            GameData game = serializer.fromJson(ctx.body(), GameData.class);
            if (game.getGameID() == 0 || game.getGame() == null) {
                ctx.status(400).json(new Message("Error: bad request"));
                return;
            }

            JoinGameRequest joinRequest = new JoinGameRequest(authToken, game.getGame().getTeamTurn(), game.getGameID());
            gameService.joinGame(joinRequest);
            ctx.status(200);

        } catch (DataAccessException e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("already taken")) {
                ctx.status(403).json(new Message(e.getMessage()));
            } else if (msg.contains("not found")) {
                ctx.status(400).json(new Message(e.getMessage()));
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
