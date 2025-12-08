package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;
    private boolean gameOver;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessBoard tempBoard = board.copy();
            tempBoard.removePiece(startPosition);
            if (move.getPromotionPiece() != null) {
                tempBoard.addPiece(move.getEndPosition(), new ChessPiece(turn, move.getPromotionPiece()));
            } else {
                tempBoard.addPiece(move.getEndPosition(), piece);
            }
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(tempBoard);
            tempGame.setTeamTurn(turn);

            if (!tempGame.isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece");
        }

        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Incorrect team");
        }

        Collection<ChessMove> moves = validMoves(start);
        if (moves == null || !moves.contains(move)) {
            throw new InvalidMoveException("No moves");
        }

        board.removePiece(start);

        if (move.getPromotionPiece() != null) {
            board.addPiece(end, new ChessPiece(turn, move.getPromotionPiece()));
        } else {
            board.addPiece(end, piece);
        }

        if (getTeamTurn() == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
        } else {
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = kingPosition(teamColor);

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && pieceCausingCheck(piece, position, kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceCausingCheck(ChessPiece piece, ChessPosition position, ChessPosition kingPos) {
        for (ChessMove move : piece.pieceMoves(board, position)) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && legalMove(piece, position, teamColor)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (teamPiece(piece, teamColor) && legalMove(piece, position, teamColor)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean teamPiece(ChessPiece piece, TeamColor teamColor) {
        return piece != null && piece.getTeamColor() == teamColor;
    }

    private boolean legalMove(ChessPiece piece, ChessPosition position, TeamColor teamColor) {
        if (piece == null) {
            return false;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, position);

        for (ChessMove move : moves) {
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(board.copy());
            tempGame.setTeamTurn(teamColor);
            try {
                tempGame.makeMove(move);
                if (!tempGame.isInCheck(teamColor)) {
                    return true;
                }
            } catch (InvalidMoveException e) {
                continue;
            }
        }
        return false;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition kingPosition(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
