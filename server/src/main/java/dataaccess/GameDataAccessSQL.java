package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDataAccessSQL extends SQLDataAccessBase implements GameDAO {

    public GameDataAccessSQL() {
        super(new String[] {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameId` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL UNIQUE,
              `chessGame` TEXT,
              PRIMARY KEY (`gameId`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        });
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        Connection connection = DatabaseManager.getConnection();
        try {
            var preparedStatement = connection.prepareStatement(
                    "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)"
            );

            String gameJson = new Gson().toJson(new ChessGame());

            preparedStatement.setString(1, null);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, gameName);
            preparedStatement.setString(4, gameJson);
            return preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement(
                    "SELECT gameId, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameId=?"
            );
            preparedStatement.setInt(1, gameId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return parseGame(resultSet);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("SELECT gameId, whiteUsername, blackUsername, gameName, chessGame FROM games");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                GameData game = parseGame(resultSet);
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
        return games;
    }

    @Override
    public void joinGame(int gameId, String userName, String playerColor) throws DataAccessException {
        GameData game = getGame(gameId);
        takenSideError(playerColor, game);
        GameData newGame;

        if (playerColor.equals("WHITE")) {
            newGame = new GameData(game.getGameID(), userName, game.getBlackUsername(), game.getGameName(), game.getGame());
            String gameJson = new Gson().toJson(newGame.getGame());

            try {
                Connection connection = DatabaseManager.getConnection();
                var preparedStatement = connection.prepareStatement("UPDATE games SET whiteUsername=?, chessGame=? WHERE gameId=?");
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, gameJson);
                preparedStatement.setInt(3, gameId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Data Access Error:" + e.getMessage());
            }

        } else if (playerColor.equals("BLACK")) {
            newGame = new GameData(game.getGameID(), game.getWhiteUsername(), userName, game.getGameName(), game.getGame());
            String gameJson = new Gson().toJson(newGame.getGame());

            try {
                Connection connection = DatabaseManager.getConnection();
                var preparedStatement = connection.prepareStatement("UPDATE games SET blackUsername=?, chessGame=? WHERE gameId=?");
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, gameJson);
                preparedStatement.setInt(3, gameId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Data Access Error:" + e.getMessage());
            }
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("TRUNCATE games");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
    }

    private GameData parseGame(ResultSet resultSet) throws SQLException {
        var gameId = resultSet.getInt("gameId");
        var whiteUsername = resultSet.getString("whiteUsername");
        var blackUsername = resultSet.getString("blackUsername");
        var gameName = resultSet.getString("gameName");
        var chessGameJson = resultSet.getString("chessGame");

        ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);

        return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
    }

    public void takenSideError(String playerColor, GameData game) throws DataAccessException {
        if (playerColor.equals("WHITE") && game.getWhiteUsername() != null) {
            throw new DataAccessException("White side already taken");
        }
        if (playerColor.equals("BLACK") && game.getBlackUsername() != null) {
            throw new DataAccessException("Black side already taken");
        }
    }
}
