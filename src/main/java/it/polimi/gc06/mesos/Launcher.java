package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.tui.LobbyTui;
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
        lobbyTui.askConnectionDetails();
    }
}
