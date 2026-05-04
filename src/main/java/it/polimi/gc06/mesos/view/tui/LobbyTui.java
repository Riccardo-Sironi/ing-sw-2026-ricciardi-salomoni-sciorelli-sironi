package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.view.LobbyView;
import java.util.Scanner;

public class LobbyTui implements LobbyView{

    private final Scanner scanner;

    public LobbyTui() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void askConnectionDetails() {
        System.out.println("-- Connection Settings --");

        System.out.print("Server IP: ");
        String ip = scanner.nextLine();

        System.out.print("Server Port: ");
        int port;
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            port = 1234; // default fallback
        }

        System.out.print("Type 1 for SOCKET or 2 for RMI: ");
        String tech = scanner.nextLine().equals("2") ? "RMI" : "SOCKET";

        System.out.print("Insert your nickname: ");
        String nickname = scanner.nextLine();

        System.out.println("\n[MOCK SYSTEM] connection to server " + ip + ", port " + port + " via " + tech + "...");
        System.out.println("[MOCK SYSTEM] registration as " + nickname);

        showConnectionSuccess("Connected");
    }

    @Override
    public void showConnectionSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    @Override
    public void showConnectionError(String errorMessage) {
        System.out.println("[ERROR] " + errorMessage);
    }
}
