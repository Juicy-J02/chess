package dataaccess;

import model.GameData;
import java.util.List;

public interface GameDAO {

    Integer createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> getAllGames() throws DataAccessException;

    void joinGame(int gameID, String userName, String playerColor) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clearGames() throws DataAccessException;
}
