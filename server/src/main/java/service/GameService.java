package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.*;

import java.util.List;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameListResult getGames(GameListRequest gameListRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthByToken(gameListRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: No Auth data");
        }

        List<GameData> games = gameDAO.getAllGames();
        return new GameListResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuthByToken(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: No Auth data");
        }

        if (createGameRequest.gameName() == null) {
            throw new DataAccessException("Game name required");
        }

        GameListResult games = getGames(new GameListRequest(authToken));

        for (GameData game : games.games()) {
            if (createGameRequest.gameName().equals(game.getGameName())) {
                throw new DataAccessException("Game already exists");
            }
        }

        gameDAO.createGame(createGameRequest.gameName());

        List<GameData>  updatedGames = gameDAO.getAllGames();
        if (updatedGames.isEmpty()) {
            throw new DataAccessException("Error: Game creation failed");
        }

        GameData game = updatedGames.getLast();
        return new CreateGameResult(game.getGameID());
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws DataAccessException {
        GameData game = gameDAO.getGame(joinGameRequest.gameID());

        if (game == null) {
            throw new DataAccessException("Error: Game not found");
        }

        if (authToken.equals("LEAVE")) {
            gameDAO.joinGame(joinGameRequest.gameID(), null, joinGameRequest.playerColor());
            return;
        }

        AuthData authData = authDAO.getAuthByToken(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: No Auth data");
        }

        if (joinGameRequest.playerColor().equals("WHITE") && game.getWhiteUsername() != null) {
            throw new DataAccessException("Error: White is already taken");
        }
        if (joinGameRequest.playerColor().equals("BLACK") && game.getBlackUsername() != null) {
            throw new DataAccessException("Error: Black is already taken");
        }

        gameDAO.joinGame(joinGameRequest.gameID(), authData.getUsername(), joinGameRequest.playerColor());
    }
}
