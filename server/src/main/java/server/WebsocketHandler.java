package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
        try {
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> connect(userGameCommand, ctx);
                case LEAVE -> leave(userGameCommand, ctx);
                case RESIGN -> resign(userGameCommand);
                case MAKE_MOVE -> makeMove(userGameCommand);
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

    private void makeMove(UserGameCommand userGameCommand) throws DataAccessException, IOException {
        String message = userGameCommand.getUsername() + " made move";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        String json = new Gson().toJson(notificationMessage);
        connections.broadcast(userGameCommand.getGameID(), json);
    }

    private void error(UserGameCommand userGameCommand, String errorMessage) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        String json = new Gson().toJson(error);
        connections.broadcast(userGameCommand.getGameID(), json);
    }

}