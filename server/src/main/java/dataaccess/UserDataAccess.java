package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDataAccess implements UserDAO {

    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUserData(UserData user) throws DataAccessException {
        if (users.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.getUsername(), user);
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clearUserData() throws DataAccessException {
        users.clear();
    }
}
