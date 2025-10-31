package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDataAccessSQL extends SQLDataAccessBase implements AuthDAO {

    public AuthDataAccessSQL() {
        super(new String[] {
            """
            CREATE TABLE IF NOT EXISTS authDatas (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL UNIQUE,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        });
    }

    @Override
    public void createAuthData(AuthData authData) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("INSERT INTO authDatas (authToken, username) VALUES (?, ?)");
            preparedStatement.setString(1, authData.getAuthToken());
            preparedStatement.setString(2, authData.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
    }

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("DELETE FROM authDatas WHERE authToken=?");
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("SELECT authToken, username FROM authDatas WHERE authToken=?");
            preparedStatement.setString(1, authToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var auth = resultSet.getString("authToken");
                var username = resultSet.getString("username");
                return new AuthData(auth, username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("SELECT authToken, username FROM authDatas WHERE username=?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var user = resultSet.getString("username");
                var authToken = resultSet.getString("authToken");
                return new AuthData(authToken, user);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            var preparedStatement = connection.prepareStatement("TRUNCATE authDatas");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Error:" + e.getMessage());
        }
    }

}
