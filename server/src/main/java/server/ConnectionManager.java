package server;

import io.javalin.websocket.WsContext;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<WsContext, WsContext> connections = new ConcurrentHashMap<>();

    public void add(WsContext ctx) {
        connections.put(ctx, ctx);
    }

    public void remove(WsContext ctx) {
        connections.remove(ctx);
    }

    public void broadcast(String message) throws IOException {
        for (WsContext ctx : connections.values()) {
            if (ctx.session.isOpen()) {
                ctx.session.getRemote().sendString(message);
            }
        }
    }
}
