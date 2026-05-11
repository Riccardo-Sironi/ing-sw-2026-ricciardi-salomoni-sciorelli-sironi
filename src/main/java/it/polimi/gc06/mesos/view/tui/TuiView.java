package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.model.gameBoard.ChooseCardTileEffect;
import it.polimi.gc06.mesos.model.gameBoard.FoodTileEffect;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
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

            List<Card> topCards = new ArrayList<>();
            topCards.add(new HunterCard(Era.ERA_I, true));
            topCards.add(new ShamanCard(Era.ERA_II, 3));
            topCards.add(new BuilderCard(Era.ERA_III, 2, 3));
            topCards.add(new InventorCard(Era.ERA_III, InventionIcon.BREAD));
            topCards.add(new RitualEvent(Era.ERA_II, 2, 1));
            topCards.add(new PaintingsEvent(Era.ERA_I, 3, 4, 2));

            List<Card> bottomCards = new ArrayList<>();
            bottomCards.add(new BuilderCard(Era.ERA_III, 2, 3));
            bottomCards.add(new InventorCard(Era.ERA_III, InventionIcon.BREAD));
            bottomCards.add(new GathererCard(Era.ERA_II));
            bottomCards.add(new ArtistCard(Era.ERA_I));
            bottomCards.add(new HuntEvent(Era.ERA_II, 2));
            bottomCards.add(new SustenanceEvent(Era.ERA_I, 3));

            Completer cardCompleter = (reader, line, candidates) -> {
                List<String> words = line.words();
                if (words.size() >= 2 && "/pick_card".equals(words.get(0))) {
                    String row = words.get(1);
                    if ("top".equals(row)) {
                        for (int i = 0; i < topCards.size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, topCards.get(i).getClass().getSimpleName(), null, null, true));
                        }
                    } else if ("bottom".equals(row)) {
                        for (int i = 0; i < bottomCards.size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, bottomCards.get(i).getClass().getSimpleName(), null, null, true));
                        }
                    }
                }
            };

            Completer completer = new AggregateCompleter(
                    new ArgumentCompleter(new StringsCompleter("/end_turn", "/help", "quit"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/place_totem"), new StringsCompleter("0", "1", "2", "3", "4"), NullCompleter.INSTANCE),
                    new ArgumentCompleter(new StringsCompleter("/pick_card"), new StringsCompleter("top", "bottom"), cardCompleter, NullCompleter.INSTANCE)
            );

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .build();

            TuiBoardRenderer tuiBoardRenderer = new TuiBoardRenderer(terminal);

            List<PlayerView> players = new ArrayList<>();

            PlayerView playerview1 = new PlayerView("pippo", Color.BLUE);
            PlayerView playerview2 = new PlayerView("player2", Color.RED);
            PlayerView playerview3 = new PlayerView("player3", Color.WHITE);
            PlayerView playerview4 = new PlayerView("player4", Color.YELLOW);
            PlayerView playerview5 = new PlayerView("player5", Color.PURPLE);


            List<TileSlotView> offerTrack = new ArrayList<>();
            TileSlotView firstSlot = new TileSlotView();
            firstSlot.setTileEffect(new FoodTileEffect(3));
            firstSlot.setPlayer(playerview1);

            TileSlotView secondSlot = new TileSlotView();
            secondSlot.setTileEffect(new ChooseCardTileEffect(2, 1));
            secondSlot.setPlayer(playerview2);

            offerTrack.add(firstSlot);
            offerTrack.add(secondSlot);


            String statusMessage = "";


            players.add(playerview1);
            players.add(playerview2);
            players.add(playerview3);
            players.add(playerview4);
            players.add(playerview5);


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
                    tuiBoardRenderer.printPlayerInfo(players);

                    if (!statusMessage.isEmpty()) {
                        terminal.writer().println("\nSystem> " + statusMessage);
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
                                switch (tokens[1].toLowerCase()) {
                                    case "top":
                                    case "bottom":
                                        String rowType = tokens[1];
                                        String cardId = tokens[2];
                                        statusMessage = "Picking card " + cardId + " from " + rowType + " row";
                                        break;
                                    default:
                                        statusMessage = Style.RED + "Invalid row type: " + tokens[1] + ". Use 'top' or 'bottom'." + Style.RESET;
                                }
                            }
                            break;
                        case "/end_turn":
                            statusMessage = "Ending turn...";
                            break;
                        case "/help":
                            if (tokens.length < 2) {
                                statusMessage = "Available commands: /place_totem, /pick_card, /end_turn, quit";
                            } else {
                                switch (tokens[1].toLowerCase()) {
                                    case "place_totem":
                                        statusMessage = "Usage: /place_totem <row_index> - Place a totem on the specified tile in the offer track (0-based index). Example: /place_totem 1";
                                        break;
                                    case "pick_card":
                                        statusMessage = "Usage: /pick_card <top/bottom> <card_id> - Pick a card from the specified row (0-based index). Example: /pick_card top 2";
                                        break;
                                    case "end_turn":
                                        statusMessage = "Ends the current turn, if possible (A turn cannot be ended unless all mandatory actions have been performed, such as placing a totem or picking a card).";
                                        break;
                                    case "clear":
                                        statusMessage = "Clears the System output.";
                                        break;
                                    case "cards":
                                        showCardHelp(terminal, lineReader);
                                    default:
                                        statusMessage = "Available commands: /place_totem, /pick_card, /end_turn, quit";
                                }
                            }
                            break;
                        case "/clear":
                            statusMessage = "";
                        default:
                            statusMessage = Style.RED + "Unknown command: " + command + ". Type /help for a list of commands." + Style.RESET;
                    }
                    // Handle Ctrl + C and Ctrl + D shutdown
                } catch (UserInterruptException | EndOfFileException e) {
                    terminal.writer().println("Quitting...");
                    terminal.writer().println("Bye bye!");
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

    private static void showCardHelp(Terminal terminal, LineReader lineReader) {
        terminal.writer().println("Available cards:");
        terminal.writer().println("- Hunter > [H]:  " +
                "Whenever you add a Hunter without a \uD83C\uDF56 icon to your tribe, you get nothing. Whenever you add a Hunter with an icon to your tribe, immediately " +
                "take 1 Food token for each Hunter in your tribe (with or without a icon).\n" +
                " During the Hunt Event, you take Food and gain " +
                "Prestige Points based on the number of Hunters " +
                "in your tribe\n");
        terminal.writer().println("- Shaman > [S]: Shamans can show from 1 to 3 ★ icons.\n" +
                " During the Shamanic Ritual Event, having the " +
                "majority of these icons provides Prestige Points; " +
                "having the minority, on the other hand, results in " +
                "losing Prestige Points\n");
        terminal.writer().println("- Builder > [B]: During the game, each Builder reduces the Food\n" +
                "cost of every Building card you take by the amount " +
                "indicated in top right corner. " +
                "At the end of the game, each Builder provides the " +
                "Prestige Points indicated in the lower left corner of the card.");
        terminal.writer().println("- Inventor > [I]: At the end of the game, Inventors  provide a " +
                "number of Prestige Points equal to the number" +
                "of Inventors in your tribe multiplied by the " +
                "number of different Invention icons you have.\n " +
                "There are 10 different Invention icons.\n" +
                " ⛵ \uD83C\uDFF9 \uD83E\uDE9D \uD83D\uDCFF \uD83E\uDD63 \uD83E\uDEA2 \uD83D\uDDFF \uD83E\uDE88 \uD83D\uDCDC \uD83E\uDD56 \n");
        terminal.writer().println("- Artist > [A]: During the Cave Paintings Event, you can gain " +
                "or lose Prestige Points based on the number of " +
                "Artists in your tribe\n" +
                " At the end of the game, you gain 10 Prestige points for every 2 artists in your tribe\n");
        terminal.writer().println("- Gatherer > [G] : During the Sustenance Event they provide a discount of 3 Food tokens on the total you would have to pay.\n");
        terminal.writer().println("- HuntEvent: An event card that triggers a hunt.");
        terminal.writer().println("- SustenanceEvent: An event card that provides sustenance.");
        terminal.writer().println("- RitualEvent: An event card that triggers a ritual.");
        terminal.writer().println("- PaintingsEvent: An event card that allows you to create paintings.");
        terminal.writer().flush();

        while (true) {
            String input = lineReader.readLine("\nPress ENTER or type 'q' or '/board' to return\n");

            // Input sanitization
            if (input == null) {
                input = "";
            }
            input = input.trim().toLowerCase();

            // Exit conditions
            if (input.equals("q") || input.equals("/board") || input.equals("/b") || input.isEmpty()) {
                break;
            } else {
                terminal.writer().println("Unrecognized command. Press ENTER to return to the board.");
                terminal.writer().flush();
            }
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
