package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.GameDataAccessSQL;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;

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
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        MakeMoveCommand makeMoveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
        try {
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand, ctx);
                case LEAVE -> leave(userGameCommand, ctx);
                case RESIGN -> resign(userGameCommand);
                case MAKE_MOVE -> makeMove(userGameCommand, makeMoveCommand);
            }
        } catch (Exception e) {
            error(userGameCommand, e.getMessage());
        }
    }

    private void connect(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        connections.add(userGameCommand.getGameID(), ctx);

        String message = userGameCommand.getUsername() + " has joined the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        String json = new Gson().toJson(notificationMessage);
        connections.broadcast(userGameCommand.getGameID(), json);
    }

    private void leave(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        String message = userGameCommand.getUsername() + " has left the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        String json = new Gson().toJson(notificationMessage);
        connections.broadcast(userGameCommand.getGameID(), json);

        connections.remove(userGameCommand.getGameID(), ctx);
    }

    private void resign(UserGameCommand userGameCommand) throws DataAccessException, IOException {
        String message = userGameCommand.getUsername() + " has resigned";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        String json = new Gson().toJson(notificationMessage);
        connections.broadcast(userGameCommand.getGameID(), json);
    }

    private void makeMove(UserGameCommand userGameCommand, MakeMoveCommand makeMoveCommand) throws DataAccessException, IOException {
        GameDAO gameDataAccess = new GameDataAccessSQL();

        GameData gameData = gameDataAccess.getGame(makeMoveCommand.getGameID());

        try {
            gameData.getGame().makeMove(makeMoveCommand.getMove());

            gameDataAccess.updateGame(gameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
            String json = new Gson().toJson(loadGameMessage);
            connections.broadcast(userGameCommand.getGameID(), json);

        } catch (InvalidMoveException e) {
            error(makeMoveCommand, "Invalid move: " + e.getMessage());
        }
    }

    private void error(UserGameCommand userGameCommand, String errorMessage) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        String json = new Gson().toJson(error);
        connections.broadcast(userGameCommand.getGameID(), json);
    }

}