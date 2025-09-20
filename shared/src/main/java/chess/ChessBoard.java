package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] spaces = new ChessPiece[8][8]; // create 2d array (board)

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        spaces[position.getRow() - 1][position.getColumn() - 1] = piece; // Assign piece a position
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return spaces[position.getRow() - 1][position.getColumn() - 1];
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            spaces[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            spaces[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        spaces[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        spaces[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        spaces[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        spaces[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        spaces[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        spaces[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        spaces[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        spaces[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        spaces[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        spaces[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        spaces[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        spaces[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        spaces[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        spaces[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        spaces[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        spaces[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);



        // throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(spaces, that.spaces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(spaces);
    }
}
