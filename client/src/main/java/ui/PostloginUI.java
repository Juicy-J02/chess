package ui;

import model.GameData;
import server.ServerFacade;
import service.*;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class PostloginUI {

    ServerFacade server;
    Map<Integer, GameData> gameNumberMap = new HashMap<>();


    public PostloginUI(ServerFacade server)  {
        this.server = server;
    }

    public void run(String username, String authToken) throws Exception {

        System.out.print(BLACK_KING + "Logged in as " + username + WHITE_KING + "\n");

        Scanner scanner = new Scanner(System.in);

        label:
        while(true) {

            System.out.print("\n" + "[LOGGED IN]" + " >>> ");

            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            System.out.println();

            switch (cmd) {
                case "quit":
                    break label;

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
                    break label;

                case "list":
                    try {
                        int gameNumber = 0;
                        GameListResult gameListResult = server.listGames(new GameListRequest(authToken), authToken);
                        for (GameData game : gameListResult.games()) {
                            String gameName = game.getGameName();
                            String whiteUsername = game.getWhiteUsername();
                            String blackUsername = game.getBlackUsername();
                            gameNumber += 1;

                            gameNumberMap.put(gameNumber, game);

                            System.out.println(gameNumber + ": " + gameName);
                            System.out.println("White player: " + whiteUsername);
                            System.out.println("Black player: " + blackUsername);
                            System.out.println(game.getGame());
                            if (gameNumber < gameListResult.games().toArray().length) {
                                System.out.println();
                            }
                        }
                        break;
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }

                case "create":
                    if (params.length < 1) {
                        System.out.println("Please input a game name");
                    }
                    else if (params.length > 1) {
                        System.out.println("Too many inputs");
                    }
                    else {
                        String gameName = params[0];
                        CreateGameResult createGameResult;
                        try {
                            createGameResult = server.createGame(new CreateGameRequest(gameName), authToken);
                            System.out.println("Created game " + gameName + " ID: " + createGameResult.gameID());
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        break;
                    }
                    break;

                case "join":
                    if (params.length < 2) {
                        System.out.println("Please input a game ID and Player Color");
                    }
                    else if (params.length > 2) {
                        System.out.println("Too many inputs");
                    }
                    else {
                        GameData game;
                        String playerColor;
                        int gameNumber;

                        try {
                            gameNumber = Integer.parseInt(params[0]);
                            playerColor = params[1].toUpperCase();
                            game = gameNumberMap.get(gameNumber);

                            if (game == null) {
                                System.out.println("See game numbers from list");
                                break;
                            }
                        } catch (Exception ex) {
                            System.out.println("Join a game with <ID> [WHITE|BLACK]");
                            break;
                        }

                        try {
                            server.joinGame(new JoinGameRequest(playerColor, game.getGameID()), authToken);
                            System.out.println("Joined Game " + gameNumber + " as " + playerColor);
                            if (playerColor.equals("WHITE")) {
                                new GameplayUI(this.server).run(game, "White");
                            } else {
                                new GameplayUI(this.server).run(game, "Black");
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        break;
                    }
                    break;

                case "observe":
                    if (params.length < 1) {
                        System.out.println("Please input a game number");
                    }
                    else if (params.length > 1) {
                        System.out.println("Too many inputs");
                    }
                    else {
                        GameData game;
                        int gameNumber;

                        try {
                            gameNumber = Integer.parseInt(params[0]);
                            game = gameNumberMap.get(gameNumber);

                            if (game == null) {
                                System.out.println("See game numbers from list");
                                break;
                            }

                            new GameplayUI(this.server).run(game, "White");
                        } catch (Exception ex) {
                            System.out.println("Observe a game with <ID>");
                            break;
                        }
                    }
                    break;

                default:
                    System.out.println("Unknown command: " + cmd);
                    System.out.println("See help for list of commands");
            }
        }
    }
}
