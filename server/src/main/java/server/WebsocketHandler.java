package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final Gson gson = new Gson();

    public WebsocketHandler(ConnectionManager connections) {
        this.connections = connections;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        UserGameCommand userGameCommand = gson.fromJson(ctx.message(), UserGameCommand.class);

        try {
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand, ctx);
                case LEAVE -> leave(userGameCommand, ctx);
                case RESIGN -> resign(userGameCommand, ctx);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(userGameCommand, makeMoveCommand, ctx);
                }
            }
        } catch (Exception e) {
            error(userGameCommand, e.getMessage(), ctx);
        }
    }

    private void connect(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        connections.add(userGameCommand.getGameID(), ctx);

        GameDAO gameDAO = new GameDataAccessSQL();
        AuthDAO authDAO = new AuthDataAccessSQL();
        GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

        if (gameData == null) {
            ErrorMessage error = new ErrorMessage("Invalid gameID");
            ctx.send(gson.toJson(error));
            return;
        }

        if (authDAO.getAuthByToken(userGameCommand.getAuthToken()) == null) {
            ErrorMessage error = new ErrorMessage("Unauthorized");
            ctx.send(gson.toJson(error));
            return;
        }

        LoadGameMessage loadMessage = new LoadGameMessage(gameData.getGame());
        ctx.send(gson.toJson(loadMessage));

        String message = getUsername(userGameCommand.getAuthToken()) + " has joined the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);
        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(notificationMessage), ctx);
    }

    private void leave(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        GameDAO gameDAO = new GameDataAccessSQL();
        GameDataAccessSQL gameDataAccessSQL = new GameDataAccessSQL();
        GameData gameData = gameDataAccessSQL.getGame(userGameCommand.getGameID());
        if (getUsername(userGameCommand.getAuthToken()).equals(gameData.getWhiteUsername())) {
            gameDAO.joinGame(userGameCommand.getGameID(), null, "WHITE");
        } else if (getUsername(userGameCommand.getAuthToken()).equals(gameData.getBlackUsername())){
            gameDAO.joinGame(userGameCommand.getGameID(), null, "BLACK");
        } else {
            gameDAO.joinGame(userGameCommand.getGameID(), null, "OBSERVER");
        }

        String message = getUsername(userGameCommand.getAuthToken()) + " has left the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(notificationMessage), ctx);
        connections.remove(userGameCommand.getGameID(), ctx);
    }

    private void resign(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        GameDAO gameDAO = new GameDataAccessSQL();
        GameData gameData = gameDAO.getGame(userGameCommand.getGameID());

        if (gameData.getGame().getGameOver()) {
            ErrorMessage error = new ErrorMessage("Game is already over");
            ctx.send(gson.toJson(error));
            return;
        }

        boolean isWhite = getUsername(userGameCommand.getAuthToken()).equals(gameData.getWhiteUsername());
        boolean isBlack = getUsername(userGameCommand.getAuthToken()).equals(gameData.getBlackUsername());

        if (!isWhite && !isBlack) {
            ErrorMessage error = new ErrorMessage("Observers cannot resign");
            ctx.send(gson.toJson(error));
            return;
        }

        gameData.getGame().setGameOver(true);
        gameDAO.updateGame(gameData);


        String message = getUsername(userGameCommand.getAuthToken()) + " has resigned";
        NotificationMessage notificationMessage = new NotificationMessage(message);
        connections.broadcast(gameData.getGameID(), gson.toJson(notificationMessage));
    }

    private void makeMove(UserGameCommand userGameCommand, MakeMoveCommand makeMoveCommand, WsContext ctx)
            throws DataAccessException, IOException {

        GameDAO gameDAO = new GameDataAccessSQL();
        AuthDAO authDAO = new AuthDataAccessSQL();
        GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());

        if (authDAO.getAuthByToken(userGameCommand.getAuthToken()) == null) {
            ErrorMessage error = new ErrorMessage("Unauthorized");
            ctx.send(gson.toJson(error));
            return;
        }

        if (gameData.getGame().getGameOver()) {
            ErrorMessage error = new ErrorMessage("Game is over");
            ctx.send(gson.toJson(error));
            return;
        }

        boolean isWhite = getUsername(makeMoveCommand.getAuthToken()).equals(gameData.getWhiteUsername());
        boolean isBlack = getUsername(makeMoveCommand.getAuthToken()).equals(gameData.getBlackUsername());

        if (!isWhite && !isBlack) {
            ErrorMessage error = new ErrorMessage("Observers cannot make moves");
            ctx.send(gson.toJson(error));
            return;
        }

        boolean whiteTurn = gameData.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE;
        if ((isWhite && !whiteTurn) || (isBlack && whiteTurn)) {
            ErrorMessage error = new ErrorMessage("Not your turn");
            ctx.send(gson.toJson(error));
            return;
        }

        try {
            gameData.getGame().makeMove(makeMoveCommand.getMove());
        } catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage("Invalid move");
            ctx.send(gson.toJson(error));
            return;
        }

        gameDAO.updateGame(gameData);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
        connections.broadcast(gameData.getGameID(), gson.toJson(loadGameMessage));

        String notificationText = getUsername(makeMoveCommand.getAuthToken()) + " moved " + makeMoveCommand.getMove();
        NotificationMessage notificationMessage = new NotificationMessage(notificationText);
        connections.broadcastExcept(gameData.getGameID(), gson.toJson(notificationMessage), ctx);

        ChessGame game = gameData.getGame();
        ChessGame.TeamColor nextTurn = game.getTeamTurn();

        if (game.isInCheckmate(nextTurn)) {
            game.setGameOver(true);
            gameDAO.updateGame(gameData);
            String winner = (nextTurn == ChessGame.TeamColor.WHITE) ? "BLACK" : "WHITE";
            NotificationMessage endMsg = new NotificationMessage("Checkmate! " + winner + " wins!");
            connections.broadcast(gameData.getGameID(), gson.toJson(endMsg));
            return;
        }
        if (game.isInStalemate(nextTurn)) {
            game.setGameOver(true);
            gameDAO.updateGame(gameData);
            NotificationMessage endMsg = new NotificationMessage("Stalemate! The game is a draw.");
            connections.broadcast(gameData.getGameID(), gson.toJson(endMsg));
            return;
        }
        if (game.isInCheck(nextTurn)) {
            NotificationMessage checkMsg = new NotificationMessage("Check against " + nextTurn + "!");
            connections.broadcast(gameData.getGameID(), gson.toJson(checkMsg));
        }
    }

    private void error(UserGameCommand userGameCommand, String errorMessage, WsContext ctx) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(error), ctx);
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthDataAccessSQL authDataAccessSQL = new AuthDataAccessSQL();
        AuthData authData = authDataAccessSQL.getAuthByToken(authToken);
        return authData.getUsername();
    }
}
