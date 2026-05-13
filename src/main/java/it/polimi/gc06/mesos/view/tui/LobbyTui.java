package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.network.ConnectionDetails;
import it.polimi.gc06.mesos.view.LobbyView;

import java.util.Scanner;

public class LobbyTui implements LobbyView {

    private final Scanner scanner;

    public LobbyTui() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public ConnectionDetails askConnectionDetails() {
        System.out.println("-- Connection Settings --");

        System.out.print("Type 1 for SOCKET or 2 for RMI: ");
        String tech = scanner.nextLine().equals("2") ? "RMI" : "SOCKET";

        System.out.print("Server IP: ");
        String ip = scanner.nextLine();

        System.out.print("Server Port: ");
        int port;
        try {
            port = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            port = tech.equals("RMI") ? 1099 : 45161; // default fallback
        }

        String nickname = null;
        while (nickname == null || nickname.isBlank()) {
            System.out.print("Insert your nickname: ");
            nickname = scanner.nextLine().trim();
        }
        
        return new ConnectionDetails(tech, ip, port, nickname);

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
