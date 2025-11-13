package ui;

import server.ServerFacade;
import service.*;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class PostloginUI {

    ServerFacade server;

    public PostloginUI(ServerFacade server)  {
        this.server = server;
    }

    public void run() {

        System.out.print(BLACK_KING + "Welcome to 240 chess. Type Help to get started." + WHITE_KING + "\n");
        Scanner scanner = new Scanner(System.in);

        label:
        while(true) {

            System.out.print("\n" + "[LOGGED IN]" + " >>> ");

            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            switch (cmd) {
                case "quit":
                    break label;

                case "help":
                    System.out.print("   create <NAME> - a game\n");
                    System.out.print("   list - games\n");
                    System.out.print("   join <ID> [WHITE|BLACK] - a game\n");
                    System.out.print("   observe <ID> - a game\n");
                    System.out.print("   logout - when you are done\n");
                    System.out.print("   quit - playing chess\n");
                    System.out.print("   help - with possible commands\n");
                    break;
            }
        }
    }
}
