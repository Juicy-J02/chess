package ui;

import server.ServerFacade;
import service.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreloginUI {

    ServerFacade server;

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
                    if (params.length < 2) {
                        System.out.println("Please input a username and password");
                    }
                    else if (params.length > 2) {
                        System.out.println("Too many inputs");
                    }
                    else {
                        String username = params[0];
                        String password = params[1];
                        LoginResult loginResult;
                        try {
                            loginResult = server.login(new LoginRequest(username, password));
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        new PostloginUI(this.server).run(loginResult.username(), loginResult.authToken());
                        break label;
                    }
                    break;

                case "register":
                    if (params.length < 3) {
                        System.out.println("Please input a username, password, and email");
                    }
                    else if (params.length > 3) {
                        System.out.println("Too many inputs");
                    }
                    else {
                        String username = params[0];
                        String password = params[1];
                        String email = params[2];
                        RegisterResult registerResult;
                        try {
                            registerResult = server.register(new RegisterRequest(username, password, email));
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        new PostloginUI(this.server).run(registerResult.username(), registerResult.authToken());
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
}
