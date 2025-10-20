package dataaccess;

import model.UserData;

public interface UserDAO {

    void createUserData(UserData user) throws DataAccessException;

    UserData getUserByUsername(String username) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}


