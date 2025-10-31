package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLDataAccessBase {

    public SQLDataAccessBase(String[] createStatements) {
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
            System.err.println("Data Access Error:" + e.getMessage());
        }
    }
}
