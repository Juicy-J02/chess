package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuthData(AuthData authData) throws DataAccessException;

    void deleteAuthData(String authToken) throws DataAccessException;

    AuthData getAuthByToken(String authToken) throws DataAccessException;

    AuthData getAuthByUsername(String username) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
