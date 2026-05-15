package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.network.ConnectionDetails;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.tui.LobbyTui;
import it.polimi.gc06.mesos.view.tui.TUI;
import javafx.application.Application;

import java.util.Scanner;

/**
 * Main entry point for the Mesos Client application.
 */
public class Launcher {

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("--cli")) {
                startTUI();
            } else if (args[0].equals("--gui")) {
                Application.launch(GUI.class, args);
            } else {
                System.out.println("Invalid argument. use --cli or --gui");
            }
        } else {
            showInteractiveMenu(args);
        }
    }

    /**
     * Shows an interactive menu to let the user choose the interface mode.
     */
    private static void showInteractiveMenu(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose Game mode:");
        System.out.println("Type 1 for TUI or 2 for GUI");
        System.out.print("insert choice: ");

        int choice = 0;
        while (choice != 1 && choice != 2) {
            try {
                choice = Integer.parseInt(scanner.nextLine());

                if (choice != 1 && choice != 2) {
                    System.out.print("Invalid choice. Please insert 1 or 2: ");
                }

            } catch (NumberFormatException e) {
                System.out.print("Invalid format. Please insert 1 or 2: ");
            }
        }

        if (choice == 1) {
            startTUI();
        } else {
            System.out.println("\nStarting GUI...");
            Application.launch(GUI.class, args);
        }
    }

    /**
     * Initializes and starts the Text User Interface.
     */
    private static void startTUI() {
        LobbyTui lobbyTui = new LobbyTui();
        ConnectionDetails det = lobbyTui.askConnectionDetails();

        SmallModel smallModel = new SmallModel(det.nickname());

        try {
            Client client = new Client(smallModel);
            client.connect(det.tech(), det.ip(), det.port());

            String nickname = det.nickname();

            while (!client.getServerConnection().login(nickname)) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Nickname already in use. Please choose a different nickname and try again.");
                System.out.print("Insert your nickname: ");
                nickname = scanner.nextLine().trim();
            }

            lobbyTui.start(client, nickname);

            // If the match hasn't started yet, the model will be empty.
            // We can just wait for the model to change (we'll get the notification from the server when the match is ready)
            String[] frames = {".  ", ".. ", "..."}; // 3 frames
            int count = 0;

            int matchID = client.getServerConnection().getPlayersMatchId(nickname);

            while (smallModel.getPhase() == null) {
                try {
                    System.out.print("\rThere are " + client.getServerConnection().getMatchInfo(matchID) + " players in Match " + matchID + frames[count % frames.length]);
                    count++;

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

            TUI tui = new TUI(smallModel, client);
            tui.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
