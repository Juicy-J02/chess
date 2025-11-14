package ui;

import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginUI {

    ServerFacade server;

    private static final String TOO_MANY_ERROR = "Too many inputs";
    private static final String NOT_ENOUGH_ERROR = "Not enough inputs";

    public PreloginUI(ServerFacade server)  {
        this.server = server;
    }

    public void run() throws Exception {

        System.out.print(BLACK_KING + "Welcome to 240 chess. Type Help to get started." + WHITE_KING + "\n");
        Scanner scanner = new Scanner(System.in);

        label:
        while(true) {

            System.out.print("\n" + "[LOGGED OUT]" + " >>> ");

            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            System.out.println();

            switch (cmd) {
                case "quit":
                    break label;

                case "help":
                    System.out.println("   register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                    System.out.println("   login <USERNAME> <PASSWORD> - to play chess");
                    System.out.println("   quit - playing chess");
                    System.out.println("   help - with possible commands");
                    break;

                case "login":
                    if (login(params)) {
                        break label;
                    }
                    break;

                case "register":
                    if (register(params)) {
                        break label;
                    }
                    break;

                case "clear":
                    server.clear();
                    System.out.println("Cleared database");
                    break;

                default:
                    System.out.println("Unknown command: " + cmd);
                    System.out.println("See help for list of commands");
            }
        }
    }

    private boolean login(String[] params) throws Exception {
        if (params.length < 2) {
            System.out.println(NOT_ENOUGH_ERROR);
            return false;
        }
        if (params.length > 2) {
            System.out.println(TOO_MANY_ERROR);
            return false;
        }
        String username = params[0];
        String password = params[1];
        LoginResult loginResult;
        try {
            loginResult = server.login(new LoginRequest(username, password));
        } catch (Exception ex) {
            if (ex.getMessage().toLowerCase().contains("incorrect")) {
                System.out.println("Password is incorrect for: " + username);
            }
            else if (ex.getMessage().toLowerCase().contains("no user")) {
                System.out.println("No username found for: " + username);
            } else {
                System.out.println("Login failed: " + ex.getMessage());
            }
            return false;
        }
        new PostloginUI(this.server).run(loginResult.username(), loginResult.authToken());
        return true;
    }

    private boolean register(String[] params) throws Exception {
        if (params.length < 3) {
            System.out.println(NOT_ENOUGH_ERROR);
            return false;
        }
        if (params.length > 3) {
            System.out.println(TOO_MANY_ERROR);
            return false;
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];

        RegisterResult registerResult;

        try {
            registerResult = server.register(new RegisterRequest(username, password, email));
        } catch (Exception ex) {
            if (ex.getMessage().toLowerCase().contains("user already")) {
                System.out.println("Username taken");
            } else {
                System.out.println("Registration failed: " + ex.getMessage());
            }
            return false;
        }
        new PostloginUI(this.server).run(registerResult.username(), registerResult.authToken());
        return true;
    }
}
