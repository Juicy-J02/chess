package ui;

import chess.*;
import model.GameData;
import model.JoinGameRequest;

import server.ServerFacade;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class GameplayUI {

    ServerFacade server;
    PrintBoard printBoard;

    public GameplayUI(ServerFacade server) {
        this.server = server;
        this.printBoard = new PrintBoard();
    }

    public void run(GameData game, String playerColor, String authToken, String playerType) throws Exception {

        Scanner scanner = new Scanner(System.in);

        while (true) {

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
                    printBoard.printBoard(game.getGame(), playerColor);
                    break;

                case "leave":

                    switch(playerType) {
                        case "Player":
                            server.joinGame(new JoinGameRequest(playerColor.toUpperCase(),
                                    game.getGameID()), "LEAVE");
                            server.leave(authToken, game.getGameID());
                            break;

                        case "Observer":
                            server.leave(authToken, game.getGameID());
                            break;
                    }
                    return;

                case "move":
                    move(game, authToken, scanner);
                    break;

                case "resign":
                    server.resign(authToken, game.getGameID());
                    break;

                case "highlight":
                    highlight(game, playerColor, scanner);
                    break;

                default:
                    System.out.println("Unknown command: " + line);
                    System.out.println("See help for list of commands");
            }
            System.out.print("\n" + "[GAMEPLAY]" + " >>> ");
        }
    }

    private void move(GameData game, String authToken, Scanner scanner) {
        System.out.println("Give a start position and end position");
        System.out.print("Enter move: ");
        String moveInput = scanner.nextLine();
        String[] parts = moveInput.split(" ");

        if (parts.length < 2) {
            System.out.println("Give a start position and end position");
            return;
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
    }

    private void highlight(GameData game, String playerColor, Scanner scanner) {
        System.out.print("Enter piece: ");
        String inputPos = scanner.nextLine();

        try {
            ChessPosition pos = ChessPosition.fromAlgebraic(inputPos);
            ChessPiece piece = game.getGame().getBoard().getPiece(pos);

            if (piece == null) {
                System.out.println("No piece at that position.");
                return;
            }

            if (!piece.getTeamColor().toString().equalsIgnoreCase(playerColor)) {
                System.out.println("You can only highlight your own pieces.");
                return;
            }

            Collection<ChessMove> legalMoves = game.getGame().validMoves(pos);

            if (legalMoves == null || legalMoves.isEmpty()) {
                System.out.println("No legal moves for this piece.");
            } else {
                List<ChessPosition> highlightMoves =
                        new java.util.ArrayList<>(legalMoves.stream()
                                .map(ChessMove::getEndPosition)
                                .toList());
                highlightMoves.add(pos);
                printBoard.printBoard(game.getGame(), playerColor, highlightMoves);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
