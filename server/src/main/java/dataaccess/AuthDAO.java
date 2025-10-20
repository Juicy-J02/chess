package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuthData(AuthData user) throws DataAccessException;

    void deleteAuthData(String authToken) throws DataAccessException;

    AuthData getAuthByToken(String authToken) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
