package dataaccess;

import chess.ChessGame;
import model.GameData;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameDataAccessSQL implements GameDAO {

    public GameDataAccessSQL() {
        try {
            DatabaseManager.createDatabase();
            Connection connection = DatabaseManager.getConnection();
            for (String statement : createStatements) {
                try {
                    var preparedStatement = connection.prepareStatement(statement);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (DataAccessException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)");

                String gameJson = new Gson().toJson(new ChessGame());

                preparedStatement.setString(1, null);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, gameJson);
                return preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?");
                preparedStatement.setInt(1, gameID);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    var ID = resultSet.getInt("gameID");
                    var whiteUsername = resultSet.getString("whiteUsername");
                    var blackUsername = resultSet.getString("blackUsername");
                    var gameName = resultSet.getString("gameName");
                    var chessGameJson = resultSet.getString("chessGame");

                    ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);

                    return new GameData(ID, whiteUsername, blackUsername, gameName, chessGame);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
            return null;
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();

        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    var ID = resultSet.getInt("gameID");
                    var whiteUsername = resultSet.getString("whiteUsername");
                    var blackUsername = resultSet.getString("blackUsername");
                    var gameName = resultSet.getString("gameName");
                    var chessGameJson = resultSet.getString("chessGame");

                    ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);

                    games.add(new GameData(ID, whiteUsername, blackUsername, gameName, chessGame));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
            return games;
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, String userName, String playerColor) throws DataAccessException {
        GameData game = getGame(gameID);

        if (playerColor.equals("WHITE") && game.getWhiteUsername() != null) {
            throw new DataAccessException("White side already taken");
        }
        if (playerColor.equals("BLACK") && game.getBlackUsername() != null) {
            throw new DataAccessException("Black side already taken");
        }

        GameData newGame;

        if (playerColor.equals("WHITE")) {
            newGame = new GameData(game.getGameID(), userName, game.getBlackUsername(), game.getGameName(), game.getGame());
            String gameJson = new Gson().toJson(newGame.getGame());

            try {
                Connection connection = DatabaseManager.getConnection();
                try {
                    var preparedStatement = connection.prepareStatement("UPDATE games SET whiteUsername=?, chessGame=? WHERE gameID=?");
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, gameJson);
                    preparedStatement.setInt(3, gameID);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } catch (DataAccessException e) {
                throw new DataAccessException(e.getMessage());
            }

        } else if (playerColor.equals("BLACK")) {
            newGame = new GameData(game.getGameID(), game.getWhiteUsername(), userName, game.getGameName(), game.getGame());
            String gameJson = new Gson().toJson(newGame.getGame());

            try {
                Connection connection = DatabaseManager.getConnection();
                try {
                    var preparedStatement = connection.prepareStatement("UPDATE games SET blackUsername=?, chessGame=? WHERE gameID=?");
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, gameJson);
                    preparedStatement.setInt(3, gameID);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            } catch (DataAccessException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("TRUNCATE games");
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `chessGame` TEXT,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
