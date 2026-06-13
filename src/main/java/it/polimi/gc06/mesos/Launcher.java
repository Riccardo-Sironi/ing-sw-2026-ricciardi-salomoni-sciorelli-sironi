package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.tui.LobbyTui;
import javafx.application.Application;

import java.util.Scanner;

/**
 * Main entry point for the Mesos Client application.
 */
public class Launcher {

    private static int clientPort = 1101;

    static void main(String[] args) {

        int cliOrGui = 0;

        try {
            for (String arg : args) {
                if (arg.startsWith("--port=")) {
                    clientPort = Integer.parseInt(arg.split("=")[1]);
                } else if (arg.startsWith("--gui")) {
                    cliOrGui = 2;
                } else if (arg.startsWith("--cli") || arg.startsWith("--tui")) {
                    cliOrGui = 1;
                }
            }
        } catch (NumberFormatException _) {
            System.err.println("Invalid port number provided. Falling back to default port " + clientPort);
        }

        if (cliOrGui == 1) {
            startTUI(clientPort);
        } else if (cliOrGui == 2) {
            Application.launch(GUI.class, args);
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
            startTUI(clientPort);
        } else {
            startGUI(args);
        }
    }

    /**
     * Initializes and starts the Text User Interface.
     */
    private static void startTUI(int clientPort) {
        LobbyTui lobbyTui = new LobbyTui(clientPort);

        try {
            lobbyTui.start();
        } catch (Exception ex) {
            System.err.println("Failed to start the lobby TUI: " + ex.getMessage());
            System.exit(1);
        }

    }

    private static void startGUI(String[] args) {
        System.out.println("\nStarting GUI...");
        Application.launch(GUI.class, args);
    }
}
