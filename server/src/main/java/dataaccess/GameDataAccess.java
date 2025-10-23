package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataAccess implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameID = 1;

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Game name cannot be empty");
        }
        int id = gameID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);

        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void joinGame(int gameID, String userName, ChessGame.TeamColor playerColor) throws DataAccessException {
        GameData game = getGame(gameID);

        if (playerColor == ChessGame.TeamColor.WHITE && game.getWhiteUsername() != null) {
            throw new DataAccessException("White side already taken");
        }
        if (playerColor == ChessGame.TeamColor.BLACK && game.getBlackUsername() != null) {
            throw new DataAccessException("Black side already taken");
        }

        GameData newGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            newGame = new GameData(game.getGameID(), userName, game.getBlackUsername(), game.getGameName(), game.getGame());
        } else {
            newGame = new GameData(game.getGameID(), game.getWhiteUsername(), userName, game.getGameName(), game.getGame());
        }

        games.put(gameID, newGame);
    }

    @Override
    public void clearGames() {
        games.clear();
        gameID = 1;
    }
}
