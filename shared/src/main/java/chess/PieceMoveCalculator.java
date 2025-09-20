package chess;

import java.util.Collection;

public interface PieceMoveCalculator {

    Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition pos);
}
