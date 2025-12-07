package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class PrintBoard {

    public void printBoard(ChessGame game, boolean flip) {
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

}
