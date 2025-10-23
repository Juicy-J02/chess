package chess;

import java.util.ArrayList;
import java.util.Collection;

public class DirectionalMove {

    /**
     * Adds all possible moves in given directions for line-moving pieces
     * (e.g., Rook, Bishop, Queen).
     */
    public static Collection<ChessMove> generateSlidingDirectionalMoves(ChessPiece piece, ChessBoard board,
                                                                        ChessPosition position, int[][] directions) {

        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] dir : directions) {
            int row = position.getRow() + dir[0];
            int col = position.getColumn() + dir[1];

            while (true) {
                ChessPosition move = new ChessPosition(row, col);

                if (!board.validPosition(move)) {
                    break;
                }

                if (board.emptyPosition(move)) {
                    moves.add(new ChessMove(position, move, null));
                } else if (board.isEnemy(move, piece)) {
                    moves.add(new ChessMove(position, move, null));
                    break;
                } else {
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }

        return moves;
    }

    public static Collection<ChessMove> generateStaticDirectionalMove(
            ChessPiece piece, ChessBoard board, ChessPosition position,
            int[][] directions) {

        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] dir : directions) {
            int row = position.getRow() + dir[0];
            int col = position.getColumn() + dir[1];

            ChessPosition move = new ChessPosition(row, col);

            if (board.validPosition(move) &&
                    (board.emptyPosition(move) || board.isEnemy(move, piece))) {
                moves.add(new ChessMove(position, move, null));
            }
        }

        return moves;
    }
}
