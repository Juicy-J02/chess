package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDataAccessSQL implements UserDAO {

    public UserDataAccessSQL() {
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
    public void createUserData(UserData user) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)");
                preparedStatement.setString(1, user.getUsername());
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("SELECT username, password, email FROM users WHERE username=?");
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    var user = resultSet.getString("username");
                    var password = resultSet.getString("password");
                    var email = resultSet.getString("email");
                    return new UserData(user, password, email);
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
    public void clearUserData() throws DataAccessException {
        try {
            Connection connection = DatabaseManager.getConnection();
            try {
                var preparedStatement = connection.prepareStatement("TRUNCATE users");
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
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
