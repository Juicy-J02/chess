package server;

import io.javalin.websocket.WsContext;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<String, WsContext> connections = new ConcurrentHashMap<>();

    public void add(String authToken, WsContext ctx) {
        connections.put(authToken, ctx);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String authToken, String message) {
        WsContext ctx = connections.get(authToken);
        if (ctx != null && ctx.session.isOpen()) {
            ctx.send(message);
        }
    }
}
