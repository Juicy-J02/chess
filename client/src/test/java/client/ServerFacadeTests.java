package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost" + port);
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void register() throws Exception {
        var authData = facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(Exception.class, () -> {
            facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        });
    }

    @Test
    void login() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        assertEquals("player1", loginResult.username());
    }

    @Test
    void loginFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        assertThrows(Exception.class, () -> {
            facade.login(new LoginRequest("player1", "passwor"));
        });
    }

    @Test
    void logout() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        assertDoesNotThrow(() -> {
            facade.logout(new LogoutRequest(loginResult.authToken()), loginResult.authToken());
        });
    }

    @Test
    void logoutFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p1@email.com"));
        facade.login(new LoginRequest("player1", "password"));
        String authToken = "badAuth";
        assertThrows(Exception.class, () -> {
            facade.logout(new LogoutRequest(authToken), authToken);
        });
    }

    @Test
    void listGames() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        String authToken = loginResult.authToken();

        var gamesBefore = facade.listGames(new GameListRequest(authToken), authToken);
        assertNotNull(gamesBefore);
        assertEquals(0, gamesBefore.games().size());

        var createResult = facade.createGame(new CreateGameRequest("TestGame"), authToken);
        assertTrue(createResult.gameID() > 0, "Game ID should be positive");

        var gamesAfter = facade.listGames(new GameListRequest(authToken), authToken);
        assertEquals(1, gamesAfter.games().size(), "There should be one game after creation");

        var game = gamesAfter.games().getFirst();
        assertEquals("TestGame", game.getGameName(), "The game name should match");
    }

    @Test
    void listGamesFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        facade.login(new LoginRequest("player1", "password"));
        String authToken = "badAuth";

        assertThrows(Exception.class, () -> {
            facade.listGames(new GameListRequest(authToken), authToken);
        });
    }

    @Test
    void createGame() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        String authToken = loginResult.authToken();

        var createResult = facade.createGame(new CreateGameRequest("TestGame"), authToken);
        assertTrue(createResult.gameID() > 0, "Game ID should be positive");
    }

    @Test
    void createGameFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        facade.login(new LoginRequest("player1", "password"));
        String authToken = "badAuth";

        assertThrows(Exception.class, () -> {
            facade.createGame(new CreateGameRequest("TestGame"), authToken);
        });
    }

    @Test
    void joinGame() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        String authToken = loginResult.authToken();

        var createResult = facade.createGame(new CreateGameRequest("TestGame"), authToken);
        assertTrue(createResult.gameID() > 0, "Game ID should be positive");

        facade.joinGame(new JoinGameRequest("WHITE", 1), authToken);
        var games = facade.listGames(new GameListRequest(authToken), authToken);

        var joinedGame = games.games().getFirst();
        assertEquals("player1", joinedGame.getWhiteUsername());
    }

    @Test
    void joinGameFail() throws Exception {
        facade.register(new RegisterRequest("player1", "password", "p21email.com"));
        var loginResult = facade.login(new LoginRequest("player1", "password"));
        String authToken = loginResult.authToken();

        var createResult = facade.createGame(new CreateGameRequest("TestGame"), authToken);
        assertTrue(createResult.gameID() > 0, "Game ID should be positive");

        facade.joinGame(new JoinGameRequest("WHITE", 1), authToken);

        assertThrows(Exception.class, () -> {
            facade.joinGame(new JoinGameRequest("WHITE", 1), authToken);
        });
    }
}
