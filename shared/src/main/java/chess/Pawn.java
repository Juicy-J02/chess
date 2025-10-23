package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn implements PieceMoveCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();
        int direction;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        ChessPosition endPosition = new ChessPosition(row + direction, col);

        if (board.validPosition(endPosition) && board.emptyPosition(endPosition)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, endPosition, ChessPiece.PieceType.ROOK));
            }
            else {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }

        ChessPosition endPosition2 = new ChessPosition(row + (direction * 2), col);

        if (board.validPosition(endPosition2) && board.emptyPosition(endPosition)
                && board.emptyPosition(endPosition2) &&
                ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                        (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7))) {
            moves.add(new ChessMove(position, endPosition2, null));
        }

        ChessPosition endPosition3 = new ChessPosition(row + direction, col + 1);

        if (board.validPosition(endPosition3) && !board.emptyPosition(endPosition3)
                && board.isEnemy(endPosition3, piece)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, endPosition3, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, endPosition3, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, endPosition3, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, endPosition3, ChessPiece.PieceType.ROOK));
            } else {
                moves.add(new ChessMove(position, endPosition3, null));
            }
        }

        ChessPosition endPosition4 = new ChessPosition(row + direction, col - 1);

        if (board.validPosition(endPosition4) && !board.emptyPosition(endPosition4)
                && board.isEnemy(endPosition4, piece)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, endPosition4, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, endPosition4, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, endPosition4, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, endPosition4, ChessPiece.PieceType.ROOK));
            } else {
                moves.add(new ChessMove(position, endPosition4, null));
            }
        }

        return moves;
    }
}
