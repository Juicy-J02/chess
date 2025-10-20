package chess;

import java.util.ArrayList;
import java.util.Collection;

public class King implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        directionalMove(piece, board, position, -1, -1, moves);
        directionalMove(piece, board, position, 1, 1, moves);
        directionalMove(piece, board, position, 1, -1, moves);
        directionalMove(piece, board, position, -1, 1, moves);
        directionalMove(piece, board, position, -1, 0, moves);
        directionalMove(piece, board, position, 1, 0, moves);
        directionalMove(piece, board, position, 0, -1, moves);
        directionalMove(piece, board, position, 0, 1, moves);

        return moves;
    }

    private void directionalMove(ChessPiece piece, ChessBoard board, ChessPosition position,
                                 int x, int y, Collection<ChessMove> moves) {

        int row = position.getRow();
        int col = position.getColumn();

        row += x;
        col += y;

        ChessPosition move = new ChessPosition(row, col);

        if (board.validPosition(move) && (board.emptyPosition(move) || board.isEnemy(move, piece))) {
            moves.add(new ChessMove(position, move, null));
        }
    }
}
