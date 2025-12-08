package ui;

import chess.*;
import model.GameData;
import model.JoinGameRequest;
import server.ServerFacade;
import java.util.Scanner;

public class GameplayUI {

    ServerFacade server;
    PrintBoard printBoard;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.printBoard = new PrintBoard();
    }

    public void run(GameData game, String playerColor, Integer gameNumber, String username, String authToken) throws Exception {

        switch (playerColor) {

            case "White":
                printBoard.printBoard(game.getGame(), false);
                break;

            case "Black":
                printBoard.printBoard(game.getGame(), true);
                break;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.print("\n" + "[GAMEPLAY]" + " >>> ");

            String line = scanner.nextLine();

            switch (line) {

                case "help":
                    System.out.println("   help - with possible commands");
                    System.out.println("   redraw - to redraw chess board");
                    System.out.println("   leave - to leave the game");
                    System.out.println("   move - input a chess move");
                    System.out.println("   resign - forfeit the game");
                    System.out.println("   highlight - highlights available moves");
                    break;

                case "redraw":
                    switch (playerColor) {

                        case "White":
                            printBoard.printBoard(game.getGame(), false);
                            break;

                        case "Black":
                            printBoard.printBoard(game.getGame(), true);
                            break;
                    }
                    break;

                case "leave":
                    server.joinGame(new JoinGameRequest(null, playerColor.toUpperCase(), game.getGameID()), authToken);
                    server.leave(authToken, game.getGameID());
                    return;

                case "move":
                    server.makeMove(authToken, game.getGameID());
                    break;

                case "resign":
                    server.resign(authToken, game.getGameID());
                    break;

                case "highlight":
                    System.out.println("WIP");
                    break;

                default:
                    System.out.println("Unknown command: " + line);
                    System.out.println("See help for list of commands");
            }
        }
    }
}
