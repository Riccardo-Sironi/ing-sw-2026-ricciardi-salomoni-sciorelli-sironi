package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.ConnectionDetails;
import it.polimi.gc06.mesos.network.NetworkUtils;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.LobbyView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Text-based User Interface for the game lobby phase.
 * It handles gathering connection credentials from the user and allows
 * creating or joining a match through an interactive terminal session.
 */
public class LobbyTui implements LobbyView {

    private final Scanner scanner;
    private int clientPort = 1101;
    private static Client client;
    private static String nickname;

    private static final String[] frames = {".  ", ".. ", "..."}; // 3 frames

    // TODO Forse switchare con Terminal di Jline?

    /**
     * Constructs a new LobbyTui instance
     */
    public LobbyTui(int clientPort) {
        this.scanner = new Scanner(System.in);
        this.clientPort = clientPort;
    }

    /**
     * Prompts the user interactively via standard output and input to enter connection details
     * such as network technology (RMI/SOCKET), IP address, port, and nickname.
     *
     * @return the structured {@link ConnectionDetails connection details} provided by the user
     */
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

    /**
     * Displays a success message upon successful connection to the server.
     *
     * @param message the success message to be shown
     */
    @Override
    public void showConnectionSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    /**
     * Displays an error message when the connection to the server fails.
     *
     * @param errorMessage the error message to be shown
     */
    @Override
    public void showConnectionError(String errorMessage) {
        System.out.println("[ERROR] " + errorMessage);
    }

    /**
     * Starts the interactive lobby terminal shell.
     * It allows the user to refresh the list of available matches, create a new match,
     * or join an existing one using an auto-completing command-line interface.
     *
     * @throws IOException if there is an issue initializing or interacting with the terminal
     */
    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            client = new Client();
            ConnectionDetails det = askConnectionDetails();

            try {
                setupClientConnection(client, det);
                nickname = performLogin(client, det.nickname(), scanner);
                break;

            } catch (IllegalStateException ise) {
                System.err.println("\nNetwork Error: " + ise.getMessage());
            } catch (ConnectException ce) {
                System.err.println("\nNetwork Error: " + ce.getMessage());
                System.out.println("Check your server connection and try again\n");
            } catch (Exception e) {
                System.err.println("\nLogin Error: " + e.getMessage());
                System.out.println("Please try logging in again\n");
            }
        }

        SmallModel smallModel = new SmallModel(nickname);
        client.setSmallModel(smallModel);

        gameSelectionLobby();

        // If the match hasn't started yet, the model will be empty.
        // We can just wait for the model to change (we'll get the notification from the server when the match is ready)
        int count = 0;

        int matchID;
        try {
            matchID = client.getServerConnection().getPlayersMatchId(nickname);
        } catch (Exception e) {
            System.err.println("Failed to retrieve match information.");
            matchID = -1;
        }
        String matchInfo;
        while (smallModel.getPhase() == null) {
            try {

                try {
                    matchInfo = client.getServerConnection().getMatchInfo(matchID);
                } catch (Exception e) {
                    matchInfo = "N/A";
                }

                System.out.print("\rThere are " + matchInfo + " players in Match " + matchID + frames[count % frames.length]);
                count++;

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        colorLobby(smallModel);

        //If we left the color selection screen, it means we're ready to start the game
        TUI tui = new TUI(smallModel, client);
        tui.start();
    }


    private static void gameSelectionLobby() {
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

    private static void colorLobby(SmallModel smallModel) {
        try (Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .ffm(true)
                .build()) {

            terminal.puts(InfoCmp.Capability.clear_screen);

            Completer colorCompleter = getCompleter(smallModel);

            Completer completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("/set_color"), colorCompleter, NullCompleter.INSTANCE)
            );

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .build();

            String coloredAvailableColors = Arrays.stream(Color.values())
                    .map(c -> Style.getAnsiFromColorName(c.toString()) + c + Style.RESET)
                    .collect(Collectors.joining(", "));

            while (smallModel.getPlayer().getColor() == null) {
                terminal.writer().println("Please choose your totem color using the command: /set_color <color>");
                String input = lineReader.readLine("mesos> ");
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }

                // Tokenize the input by spaces to separate the command from its arguments
                String[] tokens = input.trim().split("\\s+");
                String command = tokens[0].toLowerCase();


                if (command.equals("/set_color")) {
                    if (tokens.length < 2) {
                        terminal.writer().println("Usage: /set_color <color> - Available colors: " + coloredAvailableColors);
                    } else {
                        String color = tokens[1].trim().toUpperCase();
                        try {
                            client.getServerConnection().chooseTotemColor(smallModel.getPlayer().getNickname(), mapTotemToColor(color));
                            break;
                        } catch (Exception e) {
                            terminal.writer().println(Style.RED + "The color you chose is already in use or is not valid: " + e.getMessage() + Style.RESET);
                        }
                    }
                }
            }

            int count = 0;
            while (smallModel.getOpponents().stream().anyMatch(o -> o.getColor() == null)) {
                try {
                    terminal.writer().print("\rWaiting for other players to choose their colors" + frames[count % frames.length]);
                    count++;
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Sorry, we encountered a critical error while setting up the terminal");
            System.exit(1);
        } catch (UserInterruptException | EndOfFileException e) {
            System.exit(0);
        }
    }

    private static Completer getCompleter(SmallModel smallModel) {
        Completer colorCompleter = (reader, line, candidates) -> {
            List<String> words = line.words();
            if (!words.isEmpty() && "/set_color".equals(words.get(0))) {
                if (words.size() == 2) {
                    List<String> allColors = Arrays.stream(Color.values())
                            .map(Enum::toString)
                            .toList();

                    Set<String> takenColors = smallModel.getOpponents().stream()
                            .filter(o -> o.getColor() != null)
                            .map(o -> o.getColor().toString())
                            .collect(Collectors.toSet());

                    for (String color : allColors) {
                        if (!takenColors.contains(color)) {
                            candidates.add(new Candidate(color, Style.getAnsiFromColorName(color) + color + Style.RESET, null, null, null, null, true));
                        }
                    }
                }
            }
        };

        return new AggregateCompleter(
                new ArgumentCompleter(new StringsCompleter("/set_color"), colorCompleter, NullCompleter.INSTANCE)
        );
    }

    private void setupClientConnection(Client client, ConnectionDetails det) throws IllegalStateException, ConnectException {
        //String playerIp = getIpAddress();
        String playerIp = "127.0.0.1";

        if ("RMI".equals(det.tech())) {
            System.out.println("\n--- NETWORK CONFIGURATION (RMI) ---");
            System.out.println("1) LAN or VPN (ZeroTier, Hamachi, Hotspot - Recommended)");
            System.out.println("2) WAN / Direct Internet (ADVANCED - Requires Port 1101 Forwarding)");
            System.out.print("Select your network mode (1/2): ");

            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim();

            if ("2".equals(choice)) {
                System.out.print("Fetching Public IP... ");
                try {
                    playerIp = NetworkUtils.getPublicIpAddress();
                    System.out.println("Success: " + playerIp);
                } catch (Exception e) {
                    throw new ConnectException("Could not retrieve Public IP. Check your internet connection.");
                }
            } else {
                playerIp = NetworkUtils.getLocalIpAddress();
                System.out.println("Local interface selected: " + playerIp);
            }
            // Set the RMI Hostname for RMI Callbacks
            System.setProperty("java.rmi.server.hostname", playerIp);
        }
        client.connect(det.tech(), det.ip(), det.port(), this.clientPort);
    }

    private String performLogin(Client client, String initialNickname, Scanner scanner) throws Exception {
        String currentNickname = initialNickname;

        while (true) {
            while (currentNickname == null || currentNickname.trim().isEmpty()) {
                System.out.print("Nickname cannot be empty. Insert your nickname: ");
                currentNickname = scanner.nextLine().trim();
            }

            if (client.getServerConnection().login(currentNickname)) {
                System.out.println("Logged in as: " + currentNickname);
                return currentNickname;
            }

            System.out.println("Nickname " + currentNickname + "is already being used. Please choose another one");
            System.out.print("Insert your nickname: ");
            currentNickname = scanner.nextLine().trim();
        }
    }

    private static Color mapTotemToColor(String totem) {
        return switch (totem.toUpperCase()) {
            case "ORANGE" -> Color.ORANGE;
            case "WHITE" -> Color.WHITE;
            case "TURQUOISE" -> Color.TURQUOISE;
            case "YELLOW" -> Color.YELLOW;
            case "PURPLE" -> Color.PURPLE;
            default -> throw new IllegalStateException("Unexpected value: " + totem.toUpperCase());
        };
    }
}
