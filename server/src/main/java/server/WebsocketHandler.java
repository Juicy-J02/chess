package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> {
                System.out.println("CONNECT");
                 connect(userGameCommand, ctx.session);
            }
            case LEAVE -> {
                System.out.println("LEAVE");
                leave(userGameCommand, ctx.session);
            }
            case MAKE_MOVE -> {
                System.out.println("MAKE_MOVE");
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand userGameCommand, Session session) throws DataAccessException {
        connections.add(session);
        var message = String.format(userGameCommand.getUsername() + "entered game " + userGameCommand.getGameID());
        connections.broadcast(message);
    }

    private void leave(UserGameCommand userGameCommand, Session session) throws DataAccessException {
        connections.remove(session);
        var message = String.format(userGameCommand.getUsername() + "left game " + userGameCommand.getGameID());
        connections.broadcast(message);
    }
}