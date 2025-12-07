package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.CompletionStage;
import com.google.gson.Gson;

public class WebsocketCommunicator {

    private WebSocket webSocket;
    private final Gson gson = new Gson();

    public WebsocketCommunicator(String serverUrl) {
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(serverUrl.replace("http", "ws") + "/ws"), new Listener() {

                    @Override
                    public void onOpen(WebSocket ws) {
                        System.out.println("Connected to WebSocket server!");
                        WebsocketCommunicator.this.webSocket = ws;
                        Listener.super.onOpen(ws);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
                        System.out.println("Received message: " + data);
                        return Listener.super.onText(ws, data, last);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
                        System.out.println("WebSocket closed: " + statusCode + " - " + reason);
                        return Listener.super.onClose(ws, statusCode, reason);
                    }

                    @Override
                    public void onError(WebSocket ws, Throwable error) {
                        System.err.println("WebSocket error: " + error.getMessage());
                        Listener.super.onError(ws, error);
                    }
                });
    }

    public void sendCommand(Object command) {
        if (webSocket != null) {
            String json = gson.toJson(command);
            webSocket.sendText(json, true);
        }
    }
}
