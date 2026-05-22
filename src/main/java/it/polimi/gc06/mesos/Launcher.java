package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.network.ConnectionDetails;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.tui.LobbyTui;
import it.polimi.gc06.mesos.view.tui.Style;
import it.polimi.gc06.mesos.view.tui.TUI;
import javafx.application.Application;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.List;
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

        try {
            Client client = new Client();
            client.connect(det.tech(), det.ip(), det.port());

            String nickname = det.nickname();

            while (!client.getServerConnection().login(nickname)) {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Nickname already in use. Please choose a different nickname and try again.");
                System.out.print("Insert your nickname: ");
                nickname = scanner.nextLine().trim();
            }

            SmallModel smallModel = new SmallModel(det.nickname());
            client.setSmallModel(smallModel);

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

            try (Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build()) {

                terminal.puts(InfoCmp.Capability.clear_screen);

                Completer completer = (reader, line, candidates) -> {
                    List<String> words = line.words();
                    if (!words.isEmpty() && "/set_color".equals(words.get(0))) {
                        if (words.size() == 2) {
                            List<String> allColors = java.util.Arrays.stream(it.polimi.gc06.mesos.model.Color.values())
                                    .map(Enum::toString)
                                    .toList();

                            java.util.Set<String> takenColors = smallModel.getOpponents().stream()
                                    .filter(o -> o.getColor() != null)
                                    .map(o -> o.getColor().toString())
                                    .collect(java.util.stream.Collectors.toSet());

                            for (String color : allColors) {
                                if (!takenColors.contains(color)) {
                                    candidates.add(new Candidate(color));
                                }
                            }
                        }
                    }
                };

                LineReader lineReader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(completer)
                        .build();


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
                            terminal.writer().println("Usage: /set_color <color> - Available colors: ORANGE, WHITE, TURQUOISE, YELLOW, PURPLE");
                        } else {
                            String color = tokens[1];
                            try {
                                client.getServerConnection().chooseTotemColor(smallModel.getPlayer().getNickname(), mapTotemToColor(color));
                                break;
                            } catch (Exception e) {
                                terminal.writer().println(Style.RED + "The color you chose is already in use: " + e.getMessage() + Style.RESET);
                            }
                        }
                    }
                }

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
                System.out.println("Sorry, we encountered an error while setting up the terminal");
            }
            TUI tui = new TUI(smallModel, client);
            tui.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static it.polimi.gc06.mesos.model.Color mapTotemToColor(String totem) {
        return switch (totem.toUpperCase()) {
            case "ORANGE" -> it.polimi.gc06.mesos.model.Color.ORANGE;
            case "WHITE" -> it.polimi.gc06.mesos.model.Color.WHITE;
            case "TURQUOISE" -> it.polimi.gc06.mesos.model.Color.TORQUISE;
            case "YELLOW" -> it.polimi.gc06.mesos.model.Color.YELLOW;
            default -> it.polimi.gc06.mesos.model.Color.PURPLE;
        };
    }


}
