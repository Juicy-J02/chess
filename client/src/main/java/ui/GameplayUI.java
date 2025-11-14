package ui;

import server.ServerFacade;
import service.*;

import java.awt.*;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.BLACK_KING;
import static ui.EscapeSequences.WHITE_KING;

public class GameplayUI {

    ServerFacade server;

    public GameplayUI(ServerFacade server)  {
        this.server = server;
    }

    public void run(String username, String authToken) throws Exception {

        System.out.print(BLACK_KING + "Logged in as " + username + WHITE_KING + "\n");
        Scanner scanner = new Scanner(System.in);

        label:
        while(true) {

            System.out.print("\n" + "[LOGGED IN]" + " >>> ");

            String line = scanner.nextLine();
            String[] tokens = line.toLowerCase().split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            switch (cmd) {

            }
        }
    }
}
