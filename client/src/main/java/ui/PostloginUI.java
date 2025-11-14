package ui;

import server.ServerFacade;
import service.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class PostloginUI {

    ServerFacade server;

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

            switch (cmd) {
                case "quit":
                    break label;

                case "help":
                    System.out.print("   create <NAME> - a game\n");
                    System.out.print("   list - games\n");
                    System.out.print("   join <ID> [WHITE|BLACK] - a game\n");
                    System.out.print("   observe <ID> - a game\n");
                    System.out.print("   logout - when you are done\n");
                    System.out.print("   quit - playing chess\n");
                    System.out.print("   help - with possible commands\n");
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
                    GameListResult gameListResult;
                    try {
                        gameListResult = server.listGames(new GameListRequest(authToken), authToken);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        break;
                    }
                    System.out.println(gameListResult.games());
                    break;

                case "create":
                    if (params.length < 1) {
                        System.out.print("please input a game name\n");
                    }
                    else if (params.length > 1) {
                        System.out.print("too many inputs\n");
                    }
                    else {
                        String gameName = params[0];
                        CreateGameResult createGameResult;
                        try {
                            createGameResult = server.createGame(new CreateGameRequest(gameName), authToken);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        System.out.println("Created game " + createGameResult.gameID());
                        break;
                    }
                    break;

                case "join":
                    if (params.length < 2) {
                        System.out.print("please input a game ID and Player Color\n");
                    }
                    else if (params.length > 2) {
                        System.out.print("too many inputs\n");
                    }
                    else {
                        int gameID = 0;
                        String playerColor = "";

                        try {
                            gameID = Integer.parseInt(params[0]);
                            playerColor = params[1].toUpperCase();
                        } catch (Exception ex) {
                            System.out.println();
                        }
                        try {
                            server.joinGame(new JoinGameRequest(playerColor, gameID), authToken);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        System.out.println("Joined Game " + gameID + " as " + playerColor);
                        break;
                    }
                    break;
            }
        }
    }
}
