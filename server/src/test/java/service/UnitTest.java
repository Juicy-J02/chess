package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new UserDataAccess();
        userDAO.clearUserData();
        authDAO = new AuthDataAccess();
        authDAO.clearAuthData();
        gameDAO = new GameDataAccess();
        gameDAO.clearGames();
    }

    @Test
    public void testAddUserSuccessfully() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");

        userDAO.createUserData(user);
        UserData found = userDAO.getUserByUsername("john_doe");

        assertNotNull(found, "User should be found in the database");
        assertEquals("john_doe", found.getUsername());
        assertEquals("john@example.com", found.getEmail());
    }

    @Test
    public void testAddDuplicateUserFails() throws DataAccessException {
        UserData user = new UserData("jane_doe", "password123", "jane@example.com");
        userDAO.createUserData(user);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUserData(user);
        }, "Inserting duplicate username should throw exception");
    }

    @Test
    public void testAddAuthSuccessfully() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");

        authDAO.createAuthData(authData);
        String authToken = authData.getAuthToken();
        AuthData found = authDAO.getAuthByUsername("john_doe");

        assertNotNull(found, "User should be found in the database");
        assertEquals("john_doe", found.getUsername());
        assertEquals(authToken, found.getAuthToken());
    }

    @Test
    public void testDeleteAuthSuccessfully() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");

        authDAO.createAuthData(authData);
        AuthData found = authDAO.getAuthByUsername("john_doe");
        authDAO.deleteAuthData(authData.getUsername());

        assertNull(authDAO.getAuthByUsername(found.getUsername()), "User should not be found in the database");
    }

    @Test
    public void testAddDuplicateAuthFails() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");

        authDAO.createAuthData(authData);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuthData(authData);
        }, "Inserting duplicate username should throw exception");
    }

    @Test
    public void testDatabaseStartsEmpty() throws DataAccessException {
        assertNull(userDAO.getUserByUsername("nobody"), "Database should be empty at start");
        assertNull(authDAO.getAuthByUsername("nobody"), "Database should be empty at start");
    }

    @Test
    public void clearUserDatabase() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.createUserData(user);
        UserData user1 = new UserData("jim_rock", "password123", "jim@example.com");
        userDAO.createUserData(user1);
        UserData user2 = new UserData("peepaw", "password123", "peep@example.com");
        userDAO.createUserData(user2);

        userDAO.clearUserData();

        assertNull(userDAO.getUserByUsername("john_doe"), "Database should be empty");
        assertNull(userDAO.getUserByUsername("jim_rock"), "Database should be empty");
        assertNull(userDAO.getUserByUsername("peepaw"), "Database should be empty");
    }

    @Test
    public void testAddGameSuccessfully() throws DataAccessException {
        gameDAO.createGame("biggest_game");
        gameDAO.createGame("second_biggest_game");
        gameDAO.createGame("third_biggest_game");
        GameData found = gameDAO.getGame(2);

        assertNotNull(found, "Game should be found in the database");
        assertEquals("second_biggest_game", found.getGameName());
        assertNull(found.getBlackUsername());
        assertNull(found.getWhiteUsername());
    }

}
