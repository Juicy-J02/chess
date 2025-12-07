package server;

import com.google.gson.Gson;
import jakarta.websocket.*;

import java.net.URI;

@ClientEndpoint
public class WebsocketCommunicator {

    private Session session;
    private WebsocketMessageListener listener;
    private final Gson gson = new Gson();

    public WebsocketCommunicator(String serverUrl) {
        try {
            URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[WebSocket] Connected.");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        if (listener != null) {
            listener.onMessage(message);
        }
    }

    public void setListener(WebsocketMessageListener listener) {
        this.listener = listener;
    }

    public void sendCommand(Object command) {
        try {
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface WebsocketMessageListener {
        void onMessage(String message);
    }
}
