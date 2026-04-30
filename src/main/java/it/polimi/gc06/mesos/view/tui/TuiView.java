package it.polimi.gc06.mesos.view.tui;

import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;

public class TuiView {


    /**
     * Entrypoint of the TUI
     * <p>
     * It initializes the terminal and enters the loop to read user input.
     */
    public static void start() {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build();

            terminal.puts(InfoCmp.Capability.clear_screen);

            Completer completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("/end_turn", "/help", "quit"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/place_totem"), new StringsCompleter("0", "1", "2", "3", "4"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/pick_card"), new StringsCompleter("top", "bottom"), new StringsCompleter("card1", "card2", "card3"), NullCompleter.INSTANCE)
            );

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .build();
            terminal.writer().println("\u001B[32mStarting Mesos...!\u001B[0m");
            terminal.writer().flush();

            while (true) {
                String input = null;
                try {
                    input = lineReader.readLine("mesos> ");
                    if (input == null || input.trim().isEmpty()) {
                        continue;
                    }

                    // Tokenize the input by spaces to separate the command from its arguments
                    String[] tokens = input.trim().split("\\s+");
                    String command = tokens[0].toLowerCase();

                    if (command.equals("exit") || command.equals("quit")) {
                        terminal.writer().println("Closing...");
                        break;
                    }

                    // TODO: The TuiView should eventually have a reference to the NetworkClient or Controller
                    switch (command) {
                        case "/place_totem":
                            if (tokens.length < 2) {
                                terminal.writer().println("Usage: /place_totem <row_index>");
                            } else {
                                String row = tokens[1];
                                terminal.writer().println("Placing totem on row: " + row);
                                // e.g., networkClient.placeTotem(Integer.parseInt(row));
                            }
                            break;
                        case "/pick_card":
                            if (tokens.length < 3) {
                                terminal.writer().println("Usage: /pick_card <top/bottom> <card_id>");
                            } else {
                                switch (tokens[1]) {
                                    case "top":
                                    case "bottom":
                                        String rowType = tokens[1];
                                        String cardId = tokens[2];
                                        terminal.writer().println("Picking card " + cardId + " from " + rowType + " row");
                                        break;
                                    default:
                                        terminal.writer().println("Invalid row type: " + tokens[1] + ". Use 'top' or 'bottom'.");
                                        continue;
                                }
                            }
                            break;
                        case "/end_turn":
                            terminal.writer().println("Ending turn...");
                            break;
                        case "/help":
                            terminal.writer().println("Available commands: /place_totem, /pick_card, /end_turn, quit");
                            break;
                        default:
                            terminal.writer().println("Unknown command: " + command + ". Type /help for a list of commands.");
                    }
                    // Handle Ctrl + C and Ctrl + D shutdown
                } catch (UserInterruptException | EndOfFileException e) {
                    terminal.writer().println("Quitting...");
                    break;
                    // Handle unexpected error
                } catch (Exception e) {
                    terminal.writer().println(e.getMessage());
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error initializing the terminal: " + e.getMessage());
        }
    }
}
