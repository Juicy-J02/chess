package ui;

import chess.*;
import dataaccess.GameDAO;
import dataaccess.GameDataAccessSQL;
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

    public void run(GameData game, String playerColor, Integer gameNumber,
                    String username, String authToken, String playerType) throws Exception {

        GameDAO gameDAO = new GameDataAccessSQL();

        switch (playerColor) {

            case "White":
                printBoard.printBoard(gameDAO.getGame(game.getGameID()).getGame(), false);
                break;

            case "Black":
                printBoard.printBoard(gameDAO.getGame(game.getGameID()).getGame(), true);
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

                    switch(playerType) {
                        case "Player":
                            server.joinGame(new JoinGameRequest(null, playerColor.toUpperCase(), game.getGameID()), authToken);
                            server.leave(authToken, game.getGameID());

                        case "Observer":
                            server.leave(authToken, game.getGameID());
                    }
                    return;

                case "move":
                    System.out.print("Enter move: ");
                    String moveInput = scanner.nextLine();
                    String[] parts = moveInput.split(" ");


                    if (parts.length < 2) {
                        System.out.println("Invalid move format. Use: startPosition endPosition");
                        break;
                    }

                    try {
                        ChessPosition start = ChessPosition.fromAlgebraic(parts[0]);
                        ChessPosition end = ChessPosition.fromAlgebraic(parts[1]);
                        ChessPiece.PieceType promotion = null;

                        if (parts.length == 3) {
                            promotion = ChessPiece.PieceType.valueOf(parts[2].toUpperCase());
                        }

                        ChessMove move = new ChessMove(start, end, promotion);
                        server.makeMove(authToken, game.getGameID(), move);

                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }

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
