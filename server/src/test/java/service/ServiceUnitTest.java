package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTest {

    private UserService userService;
    private GameService gameService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() {
        userDAO = new UserDataAccess();
        authDAO = new AuthDataAccess();
        gameDAO = new GameDataAccess();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
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
        assertEquals("password123", storedUser.getPassword());

        AuthData storedAuth = authDAO.getAuthByUsername("john");
        assertNotNull(storedAuth);
    }

    @Test
    public void testRegisterUserDuplicateFails() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("john", "password123", "john@email.com");
        userService.register(request);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
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
        assertTrue(ex.getMessage().contains("Incorrect username or password"));
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
