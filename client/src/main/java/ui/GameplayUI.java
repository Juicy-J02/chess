package ui;

import chess.*;
import com.google.gson.Gson;
import model.GameData;
import server.ServerFacade;
import server.WebsocketCommunicator;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayUI {

    ServerFacade server;

    public GameplayUI(ServerFacade server)  {
        this.server = server;
    }

    public void run(GameData game, String boardView, Integer gameNumber, String username, String authToken) throws Exception {

        switch (boardView) {

            case "White":
                printBoard(game.getGame(), false);
                break;

            case "Black":
                printBoard(game.getGame(), true);
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
                            printBoard(game.getGame(), false);
                            break;

                        case "Black":
                            printBoard(game.getGame(), true);
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

    private void printBoard(ChessGame game, boolean flip) {
        columnHeader(flip);

        for (int i = 8; i >= 1; i--) {
            int row = flip ? 9 - i : i;
            System.out.print(" " + row + " ");

            for (int j = 1; j <= 8; j++) {
                int col = flip ? 9 - j : j;
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = game.getBoard().getPiece(pos);

                boolean isLight = (row + col) % 2 == 0;
                String bg = isLight ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                String symbol = piece != null ? getPieceSymbol(piece) : EscapeSequences.EMPTY;

                System.out.print(bg + symbol + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(" " + row);
        }

        columnHeader(flip);
    }

    private void columnHeader(boolean flip) {
        System.out.print("   ");

        for (int j = 1; j <= 8; j++) {
            int col = flip ? 9 - j : j;
            System.out.print((char)('a' + col - 1) + "   ");
        }
        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_KING: EscapeSequences.WHITE_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_QUEEN : EscapeSequences.WHITE_QUEEN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_ROOK : EscapeSequences.WHITE_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_BISHOP : EscapeSequences.WHITE_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_KNIGHT : EscapeSequences.WHITE_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.BLACK_PAWN : EscapeSequences.WHITE_PAWN;
        };
    }

    private void handleServerMessage(String json) {
        Gson gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(json, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {

            case LOAD_GAME -> {
                LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);

                printBoard(msg.getGame(), msg.getGame().getTeamTurn().equals(ChessGame.TeamColor.BLACK));
            }

            case NOTIFICATION -> {
                NotificationMessage msg = gson.fromJson(json, NotificationMessage.class);
                System.out.println("[SERVER NOTICE] " + msg.getNotification());
            }

            case ERROR -> {
                ErrorMessage msg = gson.fromJson(json, ErrorMessage.class);
                System.out.println("[SERVER ERROR] " + msg.getError());
            }
        }
    }
}
