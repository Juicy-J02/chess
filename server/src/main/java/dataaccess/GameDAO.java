package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.List;

public interface GameDAO {

    void createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> getAllGames() throws DataAccessException;

    void joinGame(int gameID, String userName, ChessGame.TeamColor playerColor) throws DataAccessException;

    void clearGames() throws DataAccessException;

}
