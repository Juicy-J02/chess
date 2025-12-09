package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTest {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO = new UserDataAccessSQL();
        authDAO = new AuthDataAccessSQL();
        GameDAO gameDAO = new GameDataAccessSQL();

        userDAO.clearUserData();
        authDAO.clearAuthData();
        gameDAO.clearGames();

        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void testRegisterUserSuccessfully() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("john", "password123", "john@email.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result);
        assertEquals("john", result.username());
        assertNotNull(result.authToken());

        UserData storedUser = userDAO.getUserByUsername("john");
        assertNotNull(storedUser);
        assertTrue(BCrypt.checkpw("password123", storedUser.getPassword()));

        AuthData storedAuth = authDAO.getAuthByUsername("john");
        assertNotNull(storedAuth);
    }

    @Test
    public void testRegisterUserDuplicateFails() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("john", "password123", "john@email.com");
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () ->   {
            userService.register(request);
        });
        assertTrue(exception.getMessage().contains("User already exists"));
    }

    @Test
    public void testLoginSuccessfully() throws DataAccessException {
        userService.register(new RegisterRequest("jane", "abc123", "jane@email.com"));
        LoginResult result = userService.login(new LoginRequest("jane", "abc123"));

        assertEquals("jane", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFailsWrongPassword() throws DataAccessException {
        userService.register(new RegisterRequest("sam", "rightpass", "sam@email.com"));

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.login(new LoginRequest("sam", "wrongpass"));
        });
        assertTrue(ex.getMessage().contains("Incorrect password"));
    }

    @Test
    public void testLogoutSuccessfully() throws DataAccessException {
        RegisterResult reg = userService.register(new RegisterRequest("alex", "pass", "alex@email.com"));

        assertDoesNotThrow(() -> userService.logout(new LogoutRequest(reg.authToken())));

        assertNull(authDAO.getAuthByToken(reg.authToken()));
    }

    @Test
    public void testLogoutFailsWithInvalidToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            userService.logout(new LogoutRequest("invalid_token"));
        });
        assertTrue(ex.getMessage().contains("No Auth data"));
    }
}
