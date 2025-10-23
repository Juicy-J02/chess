package chess;

import java.util.Collection;

public class Bishop implements PieceMoveCalculator {

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {1, 1}, {1, -1}, {-1, 1}
    };

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return DirectionalMove.generateSlidingDirectionalMoves(piece, board, position, DIRECTIONS);
    }
}
