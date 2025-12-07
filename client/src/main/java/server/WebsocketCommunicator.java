package server;

import com.google.gson.Gson;
import jakarta.websocket.*;

import java.net.URI;

@ClientEndpoint
public class WebsocketCommunicator {

    ServerFacade serverFacade;
    private Session session;
    private final Gson gson = new Gson();

    public WebsocketCommunicator(ServerFacade serverFacade, String serverUrl) {
        try {
            URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
    }


    public void handleMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                String json = gson.toJson(message);
                session.getBasicRemote().sendText(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface WebsocketMessageListener {
        void onMessage(String message);
    }
}
