package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import websocket.commands.UserGameCommand;

public class ServerFacade {

    private final HttpCommunicator httpCommunicator;
    private final WebsocketCommunicator websocketCommunicator;

    public ServerFacade(String url) {
        this.httpCommunicator = new HttpCommunicator(url);
        this.websocketCommunicator = new WebsocketCommunicator(url);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        return httpCommunicator.register(registerRequest);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        return httpCommunicator.login(loginRequest);
    }

    public void logout(LogoutRequest logoutRequest, String authToken) throws Exception {
        httpCommunicator.logout(logoutRequest, authToken);
    }

    public GameListResult listGames(GameListRequest gameListRequest, String authToken) throws Exception {
        return httpCommunicator.listGames(gameListRequest, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws Exception {
        return httpCommunicator.createGame(createGameRequest, authToken);
    }

    public void joinGame(JoinGameRequest joinGameRequest, String authToken) throws Exception {
        httpCommunicator.joinGame(joinGameRequest, authToken);
    }

    public void clear() throws Exception {
        httpCommunicator.clear();
    }

    public void sendCommand(UserGameCommand userGameCommand) {
        String message = new Gson().toJson(userGameCommand);
        websocketCommunicator.sendCommand(message);
    }
}
