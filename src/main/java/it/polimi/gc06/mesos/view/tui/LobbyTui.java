package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.network.ConnectionDetails;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.LobbyView;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Scanner;

public class LobbyTui implements LobbyView {

    private final Scanner scanner;

    public LobbyTui() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public ConnectionDetails askConnectionDetails() {
        System.out.println("-- Connection Settings --");

        System.out.print("Type 1 for SOCKET or 2 for RMI - (Will default to Socket): ");
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

    public void start(Client client, String nickname) throws IOException {
        try {

            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build();

            Completer completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("/refresh"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/create"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/join"), NullCompleter.INSTANCE)
            );

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .build();

            boolean inMatch = false;
            // TODO Aggiornare con un bel ART
            terminal.writer().println("\n=== LOBBY ===");

            while (!inMatch) {
                String line;
                try {
                    line = reader.readLine("Lobby> ");
                    if (line == null || line.isBlank()) continue;
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }

                String[] tokens = line.trim().split("\\s+");
                String command = tokens[0];

                switch (command) {
                    case "/refresh":
                        String matches = client.getServerConnection().getAvailableMatches();
                        if (matches == null || matches.isEmpty()) {
                            terminal.writer().println("No matches available");
                        } else {
                            terminal.writer().println("Match available: " + matches);
                        }
                        break;

                    case "/create":
                        if (tokens.length < 2) {
                            terminal.writer().println("Usage: /create <number of players (2-5)>");
                        } else {
                            try {
                                int num = Integer.parseInt(tokens[1]);
                                int currentMatchId = client.getServerConnection().createMatch(num, nickname);
                                terminal.writer().println("Match " + Style.YELLOW + currentMatchId + Style.RESET + " created! Waiting for other players to join...");
                                inMatch = true;
                            } catch (NumberFormatException e) {
                                terminal.writer().println("Player count must be a number between 2 and 5.");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                    case "/join":
                        if (tokens.length < 2) {
                            terminal.writer().println("Usage: /join <id_match>");
                        } else {
                            try {
                                int id = Integer.parseInt(tokens[1]);
                                boolean joined = client.getServerConnection().joinMatch(id, nickname);
                                if (joined) {
                                    terminal.writer().println("Match joined! Waiting for the game to start...");
                                    inMatch = true;
                                } else {
                                    terminal.writer().println("Error joining match. It may be full, already started, or the ID may be incorrect.");
                                }
                            } catch (NumberFormatException e) {
                                terminal.writer().println("Invalid ID");
                            }
                        }
                        break;

                    default:
                        terminal.writer().println("Unknown Command");
                        break;
                }
            }

            terminal.close();

        } catch (IOException e) {
            System.out.println("Error initializing the terminal: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
