package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDataAccess implements AuthDAO {

    private final Map<String, AuthData> authDatas = new HashMap<>();

    @Override
    public void createAuthData(AuthData authData) throws DataAccessException {
        if (authDatas.containsKey(authData.getAuthToken())) {
            throw new DataAccessException("AuthToken already exists");
        }
        authDatas.put(authData.getAuthToken(), authData);
    }

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {
        if (!authDatas.containsKey(authToken)) {
            throw new DataAccessException("No Auth Token to Delete");
        }
        authDatas.remove(authToken);
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        AuthData authData = authDatas.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Auth token not found");
        }
        return authData;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        for (AuthData authData : authDatas.values()) {
            if (authData.getUsername().equals(username)) {
                return authData;
            }
        }
        throw new DataAccessException("No Auth Token found for username");
    }

    @Override
    public void clearAuthData() {
        authDatas.clear();
    }
}
