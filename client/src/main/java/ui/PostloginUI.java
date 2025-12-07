package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostloginUI {

    ServerFacade server;
    Map<Integer, GameData> gameNumberMap = new HashMap<>();

    private static final String TOO_MANY_ERROR = "Too many inputs";
    private static final String NOT_ENOUGH_ERROR = "Not enough inputs";
    private static final String GAME_NUMBER_ERROR = "See game numbers from list";

    public PostloginUI(ServerFacade server)  {
        this.server = server;
    }

    public void run(String username, String authToken) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while(true) {

            System.out.print("\n" + "[LOGGED IN]" + " >>> ");

            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            System.out.println();

            switch (cmd) {
                case "quit":
                    return;

                case "help":
                    System.out.println("   create <NAME> - a game");
                    System.out.println("   list - games");
                    System.out.println("   join <ID> [WHITE|BLACK] - a game");
                    System.out.println("   observe <ID> - a game");
                    System.out.println("   logout - when you are done");
                    System.out.println("   quit - playing chess");
                    System.out.println("   help - with possible commands");
                    break;

                case "logout":
                    try {
                        server.logout(new LogoutRequest(authToken), authToken);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                    new PreloginUI(this.server).run();
                    return;

                case "list":
                    list(authToken);
                    break;

                case "create":
                    create(params, authToken);
                    break;

                case "join":
                    join(params, authToken, username);
                    break;

                case "observe":
                    observe(params, username, authToken);
                    break;

                default:
                    System.out.println("Unknown command: " + cmd);
                    System.out.println("See help for list of commands");
            }
        }
    }

    private void list(String authToken) {
        try {
            int gameNumber = 0;
            GameListResult gameListResult = server.listGames(new GameListRequest(authToken), authToken);

            if (gameListResult.games().isEmpty()) {
                System.out.println("No games created");
            }

            for (GameData game : gameListResult.games()) {
                String gameName = game.getGameName();
                String whiteUsername = game.getWhiteUsername();
                if (whiteUsername == null) {
                    whiteUsername = "";
                }
                String blackUsername = game.getBlackUsername();
                if (blackUsername == null) {
                    blackUsername = "";
                }
                gameNumber += 1;

                gameNumberMap.put(gameNumber, game);

                System.out.println(gameNumber + ": " + gameName);
                System.out.println("White player: " + whiteUsername);
                System.out.println("Black player: " + blackUsername);
                if (gameNumber < gameListResult.games().toArray().length) {
                    System.out.println();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void create(String[] params, String authToken) {
        if (params.length < 1) {
            System.out.println(NOT_ENOUGH_ERROR);
            return;
        }
        if (params.length > 1) {
            System.out.println(TOO_MANY_ERROR);
            return;
        }

        String gameName = params[0];
        try {
            server.createGame(new CreateGameRequest(gameName), authToken);
            System.out.println("Created game: " + gameName);
        } catch (Exception ex) {
            if (ex.getMessage().toLowerCase().contains("game already")) {
                System.out.println("Game already exists: " + gameName);
            }
        }
    }

    private void join(String[] params, String authToken, String username) {
        if (params.length < 2) {
            System.out.println(NOT_ENOUGH_ERROR);
            return;
        }
        if (params.length > 2) {
            System.out.println(TOO_MANY_ERROR);
            return;
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (Exception ex) {
            System.out.println("Join a game with <ID> [WHITE|BLACK]");
            return;
        }

        String playerColor;
        try {
            playerColor = params[1].toUpperCase();
        } catch (Exception ex) {
            System.out.println("Join a game with <ID> [WHITE|BLACK]");
            return;
        }

        GameData game = gameNumberMap.get(gameNumber);
        if (game == null) {
            System.out.println(GAME_NUMBER_ERROR);
            return;
        }

        try {
            server.joinGame(new JoinGameRequest(playerColor, game.getGameID()), authToken);
            System.out.println("Joined Game " + gameNumber + " as " + playerColor);

            server.connectWebsocket();
            server.connect(authToken, game.getGameID());

            new GameplayUI(this.server).run(game, playerColor.equals("WHITE") ? "White" : "Black", gameNumber,
                    username, authToken);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void observe(String[] params, String username, String authToken) {
        if (params.length < 1) {
            System.out.println(NOT_ENOUGH_ERROR);
            return;
        }
        if (params.length > 1) {
            System.out.println(TOO_MANY_ERROR);
            return;
        }

        GameData game;
        int gameNumber;

        try {
            gameNumber = Integer.parseInt(params[0]);
            game = gameNumberMap.get(gameNumber);

            if (game == null) {
                System.out.println(GAME_NUMBER_ERROR);
                return;
            }

            server.connectWebsocket();
            server.connect(authToken, game.getGameID());

            new GameplayUI(this.server).run(game, "White", gameNumber, username, authToken);
        } catch (Exception ex) {
            System.out.println("Observe a game with <ID>");
        }
    }
}
