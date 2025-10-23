package chess;

import java.util.Collection;

public class Rook implements PieceMoveCalculator {

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
    };

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return DirectionalMove.generateSlidingDirectionalMoves(piece, board, position, DIRECTIONS);
    }
}
