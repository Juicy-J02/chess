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
    public Integer createGame(String gameName) {
        int id = gameID++;
        GameData game = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, game);

        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public void joinGame(int gameID, String userName, String playerColor) throws DataAccessException {
        GameData game = getGame(gameID);

        if (playerColor.equals("WHITE") && game.getWhiteUsername() != null) {
            throw new DataAccessException("Error: White side already taken");
        }
        if (playerColor.equals("BLACK") && game.getBlackUsername() != null) {
            throw new DataAccessException("Error: Black side already taken");
        }

        GameData newGame = null;

        if (playerColor.equals("WHITE")) {
            newGame = new GameData(game.getGameID(), userName, game.getBlackUsername(), game.getGameName(), game.getGame());
        } else if (playerColor.equals("BLACK")) {
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
