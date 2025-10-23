package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class AuthDataAccess implements AuthDAO {

    private final Map<String, AuthData> authDatas = new HashMap<>();

    @Override
    public void createAuthData(AuthData authData) throws DataAccessException {
        if (authDatas.containsKey(authData.getUsername())) {
            throw new DataAccessException("AuthToken already exists");
        }
        authDatas.put(authData.getUsername(), authData);
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        if (!authDatas.containsKey(username)) {
            throw new DataAccessException("No Auth Token to Delete");
        }
        authDatas.remove(username);
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return authDatas.get(authToken);
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
       return authDatas.get(username);
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        authDatas.clear();
    }
}
