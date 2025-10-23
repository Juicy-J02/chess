package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
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
            throw new DataAccessException("No Auth data");
        }

        List<GameData> games = gameDAO.getAllGames();
        return new GameListResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthByToken(createGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("No Auth data");
        }
        if (createGameRequest.gameName() == null) {
            throw new DataAccessException("Game name required");
        }

        gameDAO.createGame(createGameRequest.gameName());

        List<GameData> games = gameDAO.getAllGames();
        GameData game = games.getLast();

        return new CreateGameResult(game.getGameID());
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {

        AuthData authData = authDAO.getAuthByToken(joinGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("No Auth data");
        }

        GameData game = gameDAO.getGame(joinGameRequest.gameID());

        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        if (ChessGame.TeamColor.valueOf(String.valueOf(joinGameRequest.teamColor())) == ChessGame.TeamColor.WHITE && game.getWhiteUsername() != null) {
            throw new DataAccessException("White is already taken");
        }
        if (ChessGame.TeamColor.valueOf(String.valueOf(joinGameRequest.teamColor())) == ChessGame.TeamColor.BLACK && game.getBlackUsername() != null) {
            throw new DataAccessException("Black is already taken");
        }

        gameDAO.joinGame(joinGameRequest.gameID(), authData.getUsername(), joinGameRequest.teamColor());
    }
}
