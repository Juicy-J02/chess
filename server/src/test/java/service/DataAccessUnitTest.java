package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessUnitTest {

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
        AuthData found = authDAO.getAuthByToken(authData.getAuthToken());
        authDAO.deleteAuthData(authData.getAuthToken());

        assertNull(authDAO.getAuthByToken(found.getAuthToken()), "User should not be found in the database");
    }

    @Test
    public void testDatabaseStartsEmpty() throws DataAccessException {
        assertNull(userDAO.getUserByUsername("nobody"), "Database should be empty at start");
        assertNull(authDAO.getAuthByToken("nobody"), "Database should be empty at start");
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
    public void testJoinGameSuccessfully() throws DataAccessException {
        gameDAO.createGame("biggest_game");
        gameDAO.joinGame(1, "john", "WHITE");
        GameData found = gameDAO.getGame(1);
        assertEquals("john", found.getWhiteUsername());
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

    @Test
    public void testGetAllGamesReturnsCopyNotReference() throws DataAccessException {
        gameDAO.createGame("solo_game");
        List<GameData> games = gameDAO.getAllGames();


        games.clear();

        List<GameData> gamesAgain = gameDAO.getAllGames();
        assertEquals(1, gamesAgain.size(), "DAO internal list should not be affected by external modification");
    }
}
