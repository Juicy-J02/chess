package server;

import io.javalin.websocket.WsContext;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, Set<WsContext>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, WsContext ctx) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(ctx);
    }

    public void remove(Integer gameID, WsContext ctx) {
        Set<WsContext> gameClients = connections.get(gameID);
        if (gameClients != null) {
            gameClients.remove(ctx);
            if (gameClients.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(Integer gameID, String message) throws IOException {
        Set<WsContext> gameClients = connections.get(gameID);
        if (gameClients != null) {
            for (WsContext ctx : gameClients) {
                if (ctx.session.isOpen()) {
                    ctx.session.getRemote().sendString(message);
                }
            }
        }
    }

    public void broadcastExcept(int gameID, String message, WsContext exclude) throws IOException {
        Set<WsContext> gameClients = connections.get(gameID);
        if (gameClients != null) {
            for (WsContext ctx : gameClients) {
                if (!ctx.equals(exclude) && ctx.session.isOpen()) {
                    ctx.session.getRemote().sendString(message);
                }
            }
        }
    }
}
