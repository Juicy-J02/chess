package server;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
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
                case RESIGN -> resign(userGameCommand);
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
            ErrorMessage error = new ErrorMessage("Invalid game ID");
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

        String message = userGameCommand.getUsername() + " has joined the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);
        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(notificationMessage), ctx);
    }

    private void leave(UserGameCommand userGameCommand, WsContext ctx) throws DataAccessException, IOException {
        String message = userGameCommand.getUsername() + " has left the game";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        connections.broadcast(userGameCommand.getGameID(), gson.toJson(notificationMessage));
        connections.remove(userGameCommand.getGameID(), ctx);
    }

    private void resign(UserGameCommand userGameCommand) throws DataAccessException, IOException {
        String message = userGameCommand.getUsername() + " has resigned";
        NotificationMessage notificationMessage = new NotificationMessage(message);

        connections.broadcast(userGameCommand.getGameID(), gson.toJson(notificationMessage));
    }

    private void makeMove(UserGameCommand userGameCommand, MakeMoveCommand makeMoveCommand, WsContext ctx) throws DataAccessException, IOException, InvalidMoveException {
        GameDAO gameDAO = new GameDataAccessSQL();
        GameData gameData = gameDAO.getGame(makeMoveCommand.getGameID());

        gameData.getGame().makeMove(makeMoveCommand.getMove());
        gameDAO.updateGame(gameData);

        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.getGame());
        connections.broadcast(userGameCommand.getGameID(), gson.toJson(loadGameMessage));

        String notificationText = userGameCommand.getUsername() + " moved " + makeMoveCommand.getMove();
        NotificationMessage notificationMessage = new NotificationMessage(notificationText);
        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(notificationMessage), ctx);
    }

    private void error(UserGameCommand userGameCommand, String errorMessage, WsContext ctx) throws IOException {
        ErrorMessage error = new ErrorMessage(errorMessage);
        connections.broadcastExcept(userGameCommand.getGameID(), gson.toJson(error), ctx);
    }
}
