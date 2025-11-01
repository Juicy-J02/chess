package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessSQLUnitTest {

    private static UserDAO userDAO;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new UserDataAccessSQL();
        userDAO.clearUserData();
        authDAO = new AuthDataAccessSQL();
        authDAO.clearAuthData();
        gameDAO = new GameDataAccessSQL();
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
    public void testAddUserUnsuccessfully() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.createUserData(user);

        UserData duplicateUser = new UserData("john_doe", "newpass456", "john2@example.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUserData(duplicateUser),
                "Should throw exception when trying to add duplicate username");
    }

    @Test
    public void testGetUserByUsernameFound() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.createUserData(user);

        UserData found = userDAO.getUserByUsername("john_doe");

        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
    }

    @Test
    public void testGetUserByUsernameNotFound() throws DataAccessException {
        UserData found = userDAO.getUserByUsername("nonexistent");
        assertNull(found, "Expected null for non-existent username");
    }

    @Test
    public void testClearUserDataSuccessfully() throws DataAccessException {
        UserData user1 = new UserData("john_doe", "pass", "john@example.com");
        UserData user2 = new UserData("jane_doe", "pass", "jane@example.com");
        userDAO.createUserData(user1);
        userDAO.createUserData(user2);

        userDAO.clearUserData();
        assertNull(userDAO.getUserByUsername("john_doe"));
        assertNull(userDAO.getUserByUsername("jane_doe"));
    }

    @Test public void testClearEmptyUserData() throws DataAccessException {
        assertNull(userDAO.getUserByUsername("anyone"));
    }

    @Test
    public void testAddAuthSuccessfully() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");
        authDAO.createAuthData(authData);

        AuthData found = authDAO.getAuthByUsername("john_doe");
        assertNotNull(found);
        assertEquals("john_doe", found.getUsername());
        assertEquals(authData.getAuthToken(), found.getAuthToken());
    }


    @Test
    public void testGetAuthByUsernameFound() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");
        authDAO.createAuthData(authData);

        AuthData found = authDAO.getAuthByUsername(authData.getUsername());
        assertNotNull(found);
        assertEquals("john_doe", found.getUsername());
    }


    @Test
    public void testGetAuthByUsernameNotFound() throws DataAccessException {
        AuthData found = authDAO.getAuthByUsername("nonexistent");
        assertNull(found);
    }

    @Test
    public void testGetAuthByTokenFound() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");
        authDAO.createAuthData(authData);

        AuthData found = authDAO.getAuthByToken(authData.getAuthToken());
        assertNotNull(found);
        assertEquals("john_doe", found.getUsername());
        assertEquals(authData.getAuthToken(), found.getAuthToken());
    }


    @Test
    public void testGetAuthByTokenNotFound() throws DataAccessException {
        AuthData found = authDAO.getAuthByToken("invalid_token");
        assertNull(found);
    }

    @Test
    public void testDeleteAuthSuccessfully() throws DataAccessException {
        AuthData authData = new AuthData("john_doe");

        authDAO.createAuthData(authData);
        AuthData found = authDAO.getAuthByToken(authData.getAuthToken());
        authDAO.deleteAuthData(authData.getAuthToken());

        assertNull(authDAO.getAuthByToken(found.getAuthToken()), "User should not be found in the database");
    }

    @Test
    public void testDeleteAuthUnsuccessfully() throws DataAccessException {
        String token = "fake_token";

        authDAO.deleteAuthData(token);

        assertNull(authDAO.getAuthByToken(token), "Nonexistent auth token should not be found in the database");
    }


    @Test
    public void testClearAuthDataSuccessfully() throws DataAccessException {
        authDAO.createAuthData(new AuthData("user1"));
        authDAO.createAuthData(new AuthData("user2"));
        authDAO.clearAuthData();

        assertNull(authDAO.getAuthByUsername("user1"));
        assertNull(authDAO.getAuthByUsername("user2"));
    }

    @Test public void testClearEmptyAuthData() throws DataAccessException {
        assertNull(authDAO.getAuthByUsername("anyone"));
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

    @Test
    public void testAddGameUnsuccessfully() throws DataAccessException {
        gameDAO.createGame("biggest_game");

        assertThrows(DataAccessException.class, () -> gameDAO.createGame("biggest_game"),
                "Should throw exception when adding duplicate game name");


        List<GameData> games = gameDAO.getAllGames();
        assertEquals(1, games.size(), "Only one game entry should exist");
    }


    @Test
    public void testGetGameSuccessfully() throws DataAccessException {
        Integer gameID = gameDAO.createGame("biggest_game");
        GameData found = gameDAO.getGame(gameID);

        assertNotNull(found);
        assertEquals(gameID, found.getGameID());
        assertEquals("biggest_game", found.getGameName());
        assertNull(found.getWhiteUsername());
        assertNull(found.getBlackUsername());
    }

    @Test
    public void testGetGameUnsuccessfully() throws DataAccessException {
        GameData found = gameDAO.getGame(999999);

        assertNull(found, "Expected null when fetching a non-existent game");
    }

    @Test
    public void testJoinGameSuccessfully() throws DataAccessException {
        gameDAO.createGame("biggest_game");
        gameDAO.joinGame(1, "john", "WHITE");
        GameData found = gameDAO.getGame(1);
        assertEquals("john", found.getWhiteUsername());
    }

    @Test
    public void testJoinGameUnsuccessfully() throws DataAccessException {
        gameDAO.createGame("biggest_game");
        gameDAO.joinGame(1, "john", "WHITE");
        assertThrows(DataAccessException.class, () -> {
            gameDAO.joinGame(1, "mary", "WHITE");
        }, "Should throw exception when joining a color that's already taken");
    }

    @Test
    public void testGetAllGamesReturnsAll() throws DataAccessException {
        gameDAO.createGame("game_one");
        gameDAO.createGame("game_two");
        gameDAO.createGame("game_three");

        List<GameData> games = gameDAO.getAllGames();

        assertEquals(3, games.size(), "Should return all created games");
        assertTrue(
                games.stream().anyMatch(g -> g.getGameName().equals("game_one")),
                "Returned list should include 'game_one'"
        );
        assertTrue(
                games.stream().anyMatch(g -> g.getGameName().equals("game_two")),
                "Returned list should include 'game_two'"
        );
        assertTrue(
                games.stream().anyMatch(g -> g.getGameName().equals("game_three")),
                "Returned list should include 'game_three'"
        );
    }

    @Test
    public void testGetAllGamesWhenEmpty() throws DataAccessException {
        List<GameData> games = gameDAO.getAllGames();
        assertTrue(games.isEmpty(), "Should return an empty list when no games exist");
    }

    @Test public void testClearEmptyGameData() throws DataAccessException {
        assertNull(gameDAO.getGame(1));
    }

}
