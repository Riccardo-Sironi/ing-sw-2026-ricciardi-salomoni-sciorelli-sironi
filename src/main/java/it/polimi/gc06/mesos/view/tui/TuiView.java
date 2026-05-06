package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.model.gameBoard.ChooseCardTileEffect;
import it.polimi.gc06.mesos.model.gameBoard.FoodTileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            TuiBoardRenderer tuiBoardRenderer = new TuiBoardRenderer(terminal);

            List<Card> topCards = new ArrayList<>();
            topCards.add(new HunterCard(Era.ERA_I, true));
            topCards.add(new ShamanCard(Era.ERA_II, 3));
            topCards.add(new BuilderCard(Era.ERA_III, 2, 3));
            topCards.add(new InventorCard(Era.ERA_III, InventionIcon.BREAD));
            topCards.add(new RitualEvent(Era.ERA_II, 2, 1));
            topCards.add(new PaintingsEvent(Era.ERA_I, 3, 4, 2));

            List<TileSlot> offerTrack = new ArrayList<>();
            TileSlot firstSlot = new TileSlot();
            firstSlot.setTileEffect(new FoodTileEffect(3));
            //  firstSlot.setPlayer(new Player("Player1", Color.RED, new ModifierBuildingsRegistry()));

            TileSlot secondSlot = new TileSlot();
            secondSlot.setTileEffect(new ChooseCardTileEffect(2, 1));
            // firstSlot.setPlayer(new Player("Player1", Color.PURPLE, new ModifierBuildingsRegistry()));

            offerTrack.add(firstSlot);
            offerTrack.add(secondSlot);

            List<Card> bottomCards = new ArrayList<>();
            bottomCards.add(new BuilderCard(Era.ERA_III, 2, 3));
            bottomCards.add(new InventorCard(Era.ERA_III, InventionIcon.BREAD));
            bottomCards.add(new GathererCard(Era.ERA_II));
            bottomCards.add(new ArtistCard(Era.ERA_I));
            bottomCards.add(new HuntEvent(Era.ERA_II, 2));
            bottomCards.add(new SustenanceEvent(Era.ERA_I, 3));

            String statusMessage = "";

            String[] titleAscii = {
                    " ██████   ██████ ██████████  █████████     ███████     █████████ ",
                    "░░██████ ██████ ░░███░░░░░█ ███░░░░░███  ███░░░░░███  ███░░░░░███",
                    " ░███░█████░███  ░███  █ ░ ░███    ░░░  ███     ░░███░███    ░░░ ",
                    " ░███░░███ ░███  ░██████   ░░█████████ ░███      ░███░░█████████ ",
                    " ░███ ░░░  ░███  ░███░░█    ░░░░░░░░███░███      ░███ ░░░░░░░░███",
                    " ░███      ░███  ░███ ░   █ ███    ░███░░███     ███  ███    ░███",
                    " █████     █████ ██████████░░█████████  ░░░███████░  ░░█████████ ",
                    "░░░░░     ░░░░░ ░░░░░░░░░░  ░░░░░░░░░     ░░░░░░░     ░░░░░░░░░  ",
                    "                                                                 "
            };

            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.puts(InfoCmp.Capability.cursor_home);

            // Get terminal dimensions to compute vertical and horizontal centering
            int terminalHeight = terminal.getHeight();

            int bannerHeight = titleAscii.length;

            // Calculate padding
            int verticalPadding = Math.max(0, (terminalHeight - bannerHeight) / 2);

            // Print top padding
            for (int i = 0; i < verticalPadding; i++) {
                terminal.writer().println();
            }


            String[] centeredTitle = centerOnScreen(titleAscii, terminal);

            // Print the entire array with a color gradient from Red to Yellow, using ANSI escape codes for true color
            for (int i = 0; i < bannerHeight; i++) {
                float ratio = (float) i / (bannerHeight - 1);

                // Interpolate from Red (255, 0, 0) to Yellow (255, 255, 0)
                // The only value that changes is the green component, which goes from 0 to 255 as we go down the banner
                // Thus, the index goes from 0 to 255 with a ration proportional to the banner height
                int r = 255;
                int g = (int) (ratio * 255);
                int b = 0;

                terminal.writer().println(Style.COLOR(r, g, b) + centeredTitle[i] + Style.RESET);
            }
            terminal.writer().flush();

            try {
                // Wait for 1.5 seconds so the user can see the banner
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.puts(InfoCmp.Capability.cursor_home);
            boolean needsRedraw = true;

            while (true) {
                if (needsRedraw) {
                    terminal.puts(InfoCmp.Capability.clear_screen);
                    terminal.puts(InfoCmp.Capability.cursor_home);
                    terminal.writer().flush();

                    tuiBoardRenderer.printCardRow(topCards);
                    tuiBoardRenderer.printOfferTrack(offerTrack);
                    tuiBoardRenderer.printCardRow(bottomCards);

                    if (!statusMessage.isEmpty()) {
                        terminal.writer().println("\nSystem: " + statusMessage);
                    } else {
                        terminal.writer().println();
                    }
                    terminal.writer().flush();

                    needsRedraw = false; // Reset the flag after rendering
                }

                String input = null;
                try {
                    input = lineReader.readLine("\nmesos> ");
                    if (input == null || input.trim().isEmpty()) {
                        statusMessage = "";
                        continue;
                    }

                    // Tokenize the input by spaces to separate the command from its arguments
                    String[] tokens = input.trim().split("\\s+");
                    String command = tokens[0].toLowerCase();

                    if (command.equals("exit") || command.equals("quit")) {
                        terminal.writer().println("Closing...");
                        System.exit(0);
                    }

                    // If the input wasn't empty, we're definitely gonna need to redraw the whole scene
                    needsRedraw = true;

                    // TODO: The TuiView should eventually have a reference to the NetworkClient or Controller
                    switch (command) {
                        case "/place_totem":
                            if (tokens.length < 2) {
                                statusMessage = "Usage: /place_totem <row_index>";
                            } else {
                                String row = tokens[1];
                                statusMessage = "Placing totem on row: " + row;
                            }
                            break;
                        case "/pick_card":
                            if (tokens.length < 3) {
                                statusMessage = "Usage: /pick_card <top/bottom> <card_id>";
                            } else {
                                switch (tokens[1]) {
                                    case "top":
                                    case "bottom":
                                        String rowType = tokens[1];
                                        String cardId = tokens[2];
                                        statusMessage = "Picking card " + cardId + " from " + rowType + " row";
                                        break;
                                    default:
                                        statusMessage = "Invalid row type: " + tokens[1] + ". Use 'top' or 'bottom'.";
                                }
                            }
                            break;
                        case "/end_turn":
                            statusMessage = "Ending turn...";
                            break;
                        case "/help":
                            statusMessage = "Available commands: /place_totem, /pick_card, /end_turn, quit";
                            break;
                        default:
                            statusMessage = "Unknown command: " + command + ". Type /help for a list of commands.";
                    }
                    // Handle Ctrl + C and Ctrl + D shutdown
                } catch (UserInterruptException | EndOfFileException e) {
                    terminal.writer().println("Quitting...");
                    System.exit(0);
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

    public static String[] centerOnScreen(String[] text, Terminal terminal) {
        // Get terminal dimensions to compute vertical and horizontal centering
        int terminalWidth = terminal.getWidth();

        int textWidth = text[0].length();
        int textHeight = text.length;

        // Calculate padding
        int horizontalPadding = Math.max(0, (terminalWidth - textWidth) / 2);

        String[] centeredText = new String[textHeight];
        String padString = " ".repeat(horizontalPadding);
        for (int i = 0; i < textHeight; i++) {
            centeredText[i] = padString + text[i];
        }
        return centeredText;
    }

    public static String[] centerOnScreen(StringBuilder[] text, Terminal terminal) {

        // Get terminal dimensions to compute vertical and horizontal centering
        int terminalWidth = terminal.getWidth();

        int textWidth = text[0].length();
        int textHeight = text.length;

        // Calculate padding
        int horizontalPadding = Math.max(0, (terminalWidth - textWidth) / 2);

        String[] centeredText = new String[textHeight];
        String padString = " ".repeat(horizontalPadding);
        for (int i = 0; i < textHeight; i++) {
            // The method is exactly the same, we just need to make sure to convert the StringBuilder to a String before concatenating it with the padding
            centeredText[i] = padString + text[i].toString();
        }
        return centeredText;
    }
}
