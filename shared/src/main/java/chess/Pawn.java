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

        ChessPosition end_position = new ChessPosition(row + direction, col);

        if (board.validPosition(end_position) && board.emptyPosition(end_position)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, end_position, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, end_position, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, end_position, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, end_position, ChessPiece.PieceType.ROOK));
            }
            else {
                moves.add(new ChessMove(position, end_position, null));
            }
        }

        ChessPosition end_position_2 = new ChessPosition(row + (direction * 2), col);

        if (board.validPosition(end_position_2) && board.emptyPosition(end_position) && board.emptyPosition(end_position_2) &&
                ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2) ||
                        (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7))) {
            moves.add(new ChessMove(position, end_position_2, null));
        }

        ChessPosition end_position_3 = new ChessPosition(row + direction, col + 1);

        if (board.validPosition(end_position_3) && !board.emptyPosition(end_position_3) && board.isEnemy(end_position_3, piece)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, end_position_3, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, end_position_3, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, end_position_3, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, end_position_3, ChessPiece.PieceType.ROOK));
            } else {
                moves.add(new ChessMove(position, end_position_3, null));
            }
        }

        ChessPosition end_position_4 = new ChessPosition(row + direction, col - 1);

        if (board.validPosition(end_position_4) && !board.emptyPosition(end_position_4) && board.isEnemy(end_position_4, piece)) {
            if ((piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 7) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 2)) {
                moves.add(new ChessMove(position, end_position_4, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, end_position_4, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(position, end_position_4, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, end_position_4, ChessPiece.PieceType.ROOK));
            } else {
                moves.add(new ChessMove(position, end_position_4, null));
            }
        }


//        if (row < 8 && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            ChessPosition end_position = new ChessPosition(row + 1, col);
//            if (board.spaces[row + 1][col] != null){
//                moves.add(new ChessMove(position, end_position, null));
//            }
//            ChessPosition end_position_2 = new ChessPosition(row + 2, col);
//            if (row == 2 && board.spaces[row + 1][col] != null) {
//                moves.add(new ChessMove(position, end_position_2, null));
//            }
//        }
//        else if (row > 1 && piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
//            ChessPosition end_position = new ChessPosition(row + 1, col);
//            moves.add(new ChessMove(position, end_position, null));
//
//            ChessPosition end_position_2 = new ChessPosition(row + 2, col);
//            if (row == 7) {
//                moves.add(new ChessMove(position, end_position_2, null));
//            }
//        }

        return moves;
    }
}
