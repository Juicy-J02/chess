package server;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    public void add(Session session) {
        connections.put(session, session);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(String message) {
        for (Session session : connections.values()) {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(new Gson().toJson(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
