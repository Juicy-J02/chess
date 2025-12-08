package server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.PrintBoard;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class WebsocketCommunicator extends Endpoint {

    Session session;

    public WebsocketCommunicator(String url) {
        try {
            url = url.replace("http", "ws");
            URI uri = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.println("Websocket error" + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void handleMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            System.out.print("\r\033[2K");
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            new PrintBoard().printBoard(loadGameMessage.getGame(), true);
            System.out.print("\n" + "[GAMEPLAY] >>> ");
        }
        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            System.out.print("\r\033[2K");
            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
            System.out.println(errorMessage.getError());
            System.out.print("\n" + "[GAMEPLAY] >>> ");
        }
        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            System.out.print("\r\033[2K");
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println(notificationMessage.getNotification());
            System.out.print("\n" + "[GAMEPLAY] >>> ");
        }
    }

    public void sendMessage(String json) {
        this.session.getAsyncRemote().sendText(json);
    }
}
