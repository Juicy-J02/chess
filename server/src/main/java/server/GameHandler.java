package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.*;
import java.io.IOException;

public class GameHandler {

    private final GameService gameService;
    private final Gson serializer = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listAllGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("Authorization");
        GameListRequest gameListRequest = new GameListRequest(authToken);

        GameListResult result = gameService.getGames(gameListRequest);
        ctx.json(result);
    }

    public void createGame(Context ctx) throws IOException, DataAccessException {
        String authToken = ctx.header("Authorization");
        GameData game = serializer.fromJson(ctx.body(), GameData.class);

        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, game.getGameName());
        CreateGameResult result = gameService.createGame(createGameRequest);

        ctx.json(result);
    }

    public void joinGame(Context ctx) throws IOException, DataAccessException {
        String authToken = ctx.header("Authorization");
        GameData game = serializer.fromJson(ctx.body(), GameData.class);

        JoinGameRequest joinRequest = new JoinGameRequest(authToken, game.getGame().getTeamTurn(), game.getGameID());
        gameService.joinGame(joinRequest);

        ctx.status(200);
    }
}
