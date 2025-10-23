package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight implements PieceMoveCalculator {

    private static final int[][] directions = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        return DirectionalMove.generateStaticDirectionalMove(piece, board, position, directions);
    }
}
