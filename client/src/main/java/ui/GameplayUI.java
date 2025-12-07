package ui;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import server.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayUI {

    ServerFacade server;
    PrintBoard printBoard;

    public GameplayUI(ServerFacade server)  {
        this.server = server;
    }

    public void run(GameData game, String boardView, Integer gameNumber, String username, String authToken) throws Exception {

        switch (boardView) {

            case "White":
                printBoard.printBoard(game.getGame(), false);
                break;

            case "Black":
                printBoard.printBoard(game.getGame(), true);
                break;
        }

        Scanner scanner = new Scanner(System.in);

        label:
        while(true) {

            System.out.print("\n" + "[GAMEPLAY OF " + gameNumber + "]" + " >>> ");

            String line = scanner.nextLine();

            System.out.println();

            switch (line) {

                case "help":
                    System.out.println("   help - with possible commands");
                    System.out.println("   redraw chess board - to redraw chess board");
                    System.out.println("   leave - to leave the game");
                    System.out.println("   make move - input a chess move");
                    System.out.println("   resign - forfeit the game");
                    System.out.println("   highlight legal moves - highlights available moves");
                    break;

                case "redraw chess board":
                    switch (boardView) {

                        case "White":
                            printBoard.printBoard(game.getGame(), false);
                            break;

                        case "Black":
                            printBoard.printBoard(game.getGame(), true);
                            break;
                    }
                    break;

                case "leave":
                    new PostloginUI(this.server).run(username, authToken);
                    break label;

                case "make move":
                    System.out.println("WIP");
                    break;

                case "resign":
                    System.out.println("WIP");
                    break;

                case "highlight legal moves":
                    System.out.println("WIP");
                    break;

                default:
                    System.out.println("Unknown command: " + line);
                    System.out.println("See help for list of commands");
            }
        }
    }
}
