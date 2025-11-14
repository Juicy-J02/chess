package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import service.*;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpClient;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverDomain;

    public ServerFacade(String url) {
        this.serverDomain = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest, String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", logoutRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameListResult listGames(GameListRequest gameListRequest, String authToken) throws Exception {
        var request = buildRequest("GET", "/game", gameListRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameListResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws Exception {
        var request = buildRequest("POST", "/game", createGameRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws Exception {
        var request = buildRequest("PUT", "/game", joinGameRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverDomain + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Failed to send request: " + ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception{
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String errorMessage = json.get("message").getAsString();
            throw new Exception(errorMessage);
        }

        if (responseClass == null) {
            return null;
        }

        return new Gson().fromJson(response.body(), responseClass);
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
