package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (userDAO.getUserByUsername(registerRequest.username()) != null) {
            throw new DataAccessException("Error: User already exists");
        }

        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        userDAO.createUserData(user);

        AuthData authData = new AuthData(registerRequest.username());
        authDAO.createAuthData(authData);

        return new RegisterResult(user.getUsername(), authData.getAuthToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData user = userDAO.getUserByUsername(loginRequest.username());

        if (user == null) {
            throw new DataAccessException("No user found");
        }

        if (!BCrypt.checkpw(loginRequest.password(), user.getPassword())) {
            throw new DataAccessException("incorrect password");
        }

        AuthData authData = new AuthData(loginRequest.username());
        authDAO.createAuthData(authData);

        return new LoginResult(user.getUsername(), authData.getAuthToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthByToken(logoutRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("Error: No Auth data");
        }
        authDAO.deleteAuthData(authData.getAuthToken());
    }
}
