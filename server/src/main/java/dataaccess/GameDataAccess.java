package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataAccess implements GameDAO {

    private final Map<Integer, GameData> games = new HashMap<>();
    private int gameID = 0;

    @Override
    public void createGame(String gameName) throws DataAccessException {
        int id = gameID++;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(id, game);

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
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void joinGame(int gameID, String userName, ChessGame.TeamColor playerColor) throws DataAccessException {
        GameData game = getGame(gameID);

        GameData newGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            newGame = new GameData(game.getGameID(), userName, game.getBlackUsername(), game.getGameName(), game.getGame());
        } else {
            newGame = new GameData(game.getGameID(), game.getWhiteUsername(), userName, game.getGameName(), game.getGame());
        }

        games.put(gameID, newGame);
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
        gameID = 0;
    }
}
