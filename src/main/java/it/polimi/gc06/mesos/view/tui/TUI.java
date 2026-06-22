package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.DTOVisitor;
import it.polimi.gc06.mesos.dtos.EndGameDTO;
import it.polimi.gc06.mesos.dtos.EventResolvedDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.View;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.tui.visitors.TuiBuildingEffectVisitor;
import it.polimi.gc06.mesos.view.tui.visitors.TuiEventArtVisitor;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Text-Based User Interface implementation for the Mesos game.
 * It manages the rendering of the board and takes input from the user through the terminal
 */
public class TUI implements View, ModelListener {

    private final SmallModel smallModel;
    private final Client client;
    boolean needsRedraw = true;
    String statusMessage = "";

    private LineReader lineReader;
    private Terminal terminal;
    private TuiBoardRenderer tuiBoardRenderer;

    private final BlockingQueue<EventCard> eventDisplayQueue = new LinkedBlockingQueue<>();
    private EventCard currentlyDisplayingEvent = null;
    private boolean hasGameEnded = false;

    /**
     * Constructs a new TUI.
     *
     * @param smallModel the local state of the game
     * @param client     the client instance handling network communication
     */
    public TUI(SmallModel smallModel, Client client) {
        this.smallModel = smallModel;
        this.client = client;
    }

    /**
     * Entrypoint of the TUI
     * <p>
     * It initializes the terminal and enters the loop to read user input.
     */
    public void start() {
        try {

            // Initialize the terminal
            this.terminal = TerminalBuilder.builder()
                    .system(true)
                    .ffm(true)
                    .build();

            terminal.puts(InfoCmp.Capability.clear_screen);

            // Initialize card completer
            Completer completer = getCompleter();

            // Initialize Line Reader
            this.lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer)
                    .build();


            // Show startup banner for 1.5 seconds
            showBanner(1500);

            this.tuiBoardRenderer = new TuiBoardRenderer(terminal);

            terminal.puts(InfoCmp.Capability.clear_screen);
            terminal.puts(InfoCmp.Capability.cursor_home);

            // Daemon thread to handle events in sequence (In order to show them slightly delayed and one after the other)
            Thread eventDisplayThread = getDisplayThread();
            eventDisplayThread.start();

            // Start listening to the model updates, so we can redraw the board every time something changes
            client.subscribe(this);

            while (true) {
                if (needsRedraw) {
                    render();
                    needsRedraw = false; // Reset the flag after rendering
                }

                try {
                    String input = lineReader.readLine("mesos> ");

                    if (hasGameEnded) {
                        System.exit(0);
                    }

                    if (input == null || input.trim().isEmpty()) {
                        statusMessage = "";
                        continue;
                    }

                    // Tokenize the input by spaces to separate the command from its arguments
                    String[] tokens = input.trim().split("\\s+");
                    String command = tokens[0].toLowerCase();

                    // Based off of most UNIX systems
                    // "exit" is the standard "command" to quit the application
                    if (command.equals("exit")) {
                        System.exit(0);
                    }

                    // If the input wasn't empty, we're definitely gonna need to redraw the whole scene
                    needsRedraw = true;

                    switch (command.toLowerCase()) {
                        case "/place_totem":
                            if (tokens.length < 2) {
                                statusMessage = "Usage: /place_totem <row_index>";

                            } else {
                                String row = tokens[1];
                                try {
                                    client.getServerConnection().placeTotem(smallModel.getPlayer().getNickname(), Integer.parseInt(row));
                                } catch (Exception e) {
                                    statusMessage = Style.RED + e.getMessage() + Style.RESET;
                                }
                            }
                            break;
                        case "/pick_card":
                            if (tokens.length < 3) {
                                statusMessage = "Usage: /pick_card <top/bottom> <card_id>";
                            } else {
                                switch (tokens[1].toLowerCase()) {
                                    case "top":
                                        try {
                                            client.getServerConnection().pickCardFromTop(smallModel.getPlayer().getNickname(), Integer.parseInt(tokens[2]));
                                        } catch (Exception e) {
                                            smallModel.setSystemMessage(Style.RED + "An error occurred while trying to perform this action!" + Style.RESET);
                                        }
                                        smallModel.setSystemMessage("Picking card in slot " + tokens[2] + " from the top row");
                                        break;
                                    case "bottom":
                                        try {
                                            client.getServerConnection().pickCardFromBottom(smallModel.getPlayer().getNickname(), Integer.parseInt(tokens[2]));
                                        } catch (Exception e) {
                                            smallModel.setSystemMessage(Style.RED + "You cannot perform this action right now!" + Style.RESET);
                                        }
                                        statusMessage = "Picking card in slot " + tokens[2] + " from the bottom row";
                                        break;
                                    default:
                                        statusMessage = Style.RED + "Invalid row type: " + tokens[1] + ". Use 'top' or 'bottom'." + Style.RESET;
                                }
                            }
                            break;
                        case "/pick_building":
                            if (tokens.length < 3) {
                                statusMessage = "Usage: /pick_building <top/bottom> <card_id>";
                            } else {
                                switch (tokens[1].toLowerCase()) {
                                    case "top":
                                        try {
                                            client.getServerConnection().pickBuildingFromTop(smallModel.getPlayer().getNickname(), Integer.parseInt(tokens[2]));
                                        } catch (Exception e) {
                                            smallModel.setSystemMessage(Style.RED + "An error occurred while trying to perform this action!" + Style.RESET);
                                        }
                                        smallModel.setSystemMessage("Picking building number " + tokens[2] + " from the top row");
                                        break;
                                    case "bottom":
                                        try {
                                            client.getServerConnection().pickBuildingFromBottom(smallModel.getPlayer().getNickname(), Integer.parseInt(tokens[2]));
                                        } catch (Exception e) {
                                            smallModel.setSystemMessage(Style.RED + "You cannot perform this action right now!" + Style.RESET);
                                        }
                                        statusMessage = "Picking building number " + tokens[2] + " from the bottom row";
                                        break;
                                    default:
                                        statusMessage = Style.RED + "Invalid row type: " + tokens[1] + ". Use 'top' or 'bottom'." + Style.RESET;
                                }
                            }
                            break;

                        case "/end_turn":
                            try {
                                client.getServerConnection().handleSkip(smallModel.getPlayer().getNickname());
                            } catch (Exception e) {
                                smallModel.setSystemMessage(Style.RED + "You cannot perform this action right now!" + Style.RESET);
                            }
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
                                        showCardHelp();
                                    default:
                                        statusMessage = "Available commands: /place_totem, /pick_card, /end_turn, /quit";
                                }
                            }
                            break;
                        case "/board":
                            if (tokens.length > 1) {
                                showPlayerBoard(tokens[1]);
                            } else {
                                showPlayerBoard(smallModel.getPlayer().getNickname());
                            }
                            break;
                        case "/buildings":
                            showBuildings();
                        case "/clear":
                            statusMessage = "";
                            smallModel.setSystemMessage("");
                            break;
                        case "/quit":
                            terminal.writer().println("Closing...");
                            terminal.writer().println("Bye bye!");
                            System.exit(0);
                        default:
                            statusMessage = Style.RED + "Unknown command: " + command + ". Type /help for a list of commands." + Style.RESET;
                    }
                    // Handle Ctrl + C and Ctrl + D shutdown
                } catch (UserInterruptException | EndOfFileException e) {
                    terminal.writer().println("Closing...");
                    terminal.writer().println("Bye bye!");
                    System.exit(0);

                    // Handle unexpected error
                } catch (Exception e) {
                    statusMessage = e.getMessage();
                    needsRedraw = true;
                    break;
                }
            }

            // Critical Error whilst initializing the terminal. Close the application as we cannot proceed any further.
        } catch (IOException e) {
            System.err.println("Error initializing the terminal: " + e.getMessage());
            System.exit(1);
        }
    }

    private Thread getDisplayThread() {
        Thread eventDisplayThread = new Thread(() -> {
            while (true) {
                try {
                    currentlyDisplayingEvent = eventDisplayQueue.take();

                    synchronized (TUI.this) {
                        needsRedraw = true;
                        render();
                    }

                    Thread.sleep(4000);

                    synchronized (TUI.this) {
                        currentlyDisplayingEvent = null;
                        if (eventDisplayQueue.isEmpty()) {
                            needsRedraw = true;
                            render();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        // Close it when closing the app
        eventDisplayThread.setDaemon(true);
        return eventDisplayThread;
    }

    /**
     * Generates the autocompletion rules for the JLine terminal.
     *
     * @return the Completer configuring terminal autocompletion
     */
    private Completer getCompleter() {
        Completer cardCompleter = (reader, line, candidates) -> {
            List<String> words = line.words();
            int wordIndex = line.wordIndex();
            if (!words.isEmpty() && "/pick_card".equals(words.get(0))) {

                if (wordIndex == 1) {
                    if (smallModel.getTopDrawNum() > 0) {
                        candidates.add(new Candidate("top"));
                    }
                    if (smallModel.getBottomDrawNum() > 0) {
                        candidates.add(new Candidate("bottom"));
                    }
                    // The user might have already typed in bottom/top, or it could've just been autocompleted
                } else if (wordIndex == 2 && words.size() >= 2) {
                    String row = words.get(1);
                    if ("top".equals(row)) {
                        for (int i = 0; i < smallModel.getTopRow().size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, String.join(" ", smallModel.getTopRow().get(i).getClass().getSimpleName().split("(?=\\p{Upper})")), null, null, true));
                        }
                    } else if ("bottom".equals(row)) {
                        for (int i = 0; i < smallModel.getBottomRow().size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, String.join(" ", smallModel.getBottomRow().get(i).getClass().getSimpleName().split("(?=\\p{Upper})")), null, null, true));
                        }
                    }
                }
            }
        };

        Completer buildingCompleter = (reader, line, candidates) -> {
            List<String> words = line.words();
            int wordIndex = line.wordIndex();
            if (!words.isEmpty() && "/pick_building".equals(words.get(0))) {
                if (wordIndex == 1) {
                    if (smallModel.getTopDrawNum() > 0) {
                        candidates.add(new Candidate("top"));
                    }
                    if (smallModel.getBottomDrawNum() > 0) {
                        candidates.add(new Candidate("bottom"));
                    }
                    // The user might have already typed in bottom/top, or it could've just been autocompleted
                } else if (wordIndex == 2 && words.size() >= 2) {
                    String row = words.get(1);
                    if ("top".equals(row)) {
                        for (int i = 0; i < smallModel.getTopBuildings().size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, String.join(" ", smallModel.getTopBuildings().get(i).getClass().getSimpleName().split("(?=\\p{Upper})")), null, null, true));
                        }
                    } else if ("bottom".equals(row)) {
                        for (int i = 0; i < smallModel.getBottomBuildings().size(); i++) {
                            candidates.add(new Candidate(String.valueOf(i), String.valueOf(i), null, String.join(" ", smallModel.getBottomBuildings().get(i).getClass().getSimpleName().split("(?=\\p{Upper})")), null, null, true));
                        }
                    }
                }
            }
        };

        Completer totemCompleter = (reader, line, candidates) -> {
            List<String> words = line.words();
            if (!words.isEmpty() && "/place_totem".equals(words.getFirst())) {
                for (int i = 0; i < smallModel.getOfferTrack().size(); i++) {
                    var slot = smallModel.getOfferTrack().get(i);
                    if (slot.isEmpty()) {
                        candidates.add(new Candidate(
                                String.valueOf(i)
                        ));
                    }
                }
            }
        };

        Completer boardCompleter = (reader, line, candidates) -> {
            List<String> words = line.words();
            if (!words.isEmpty() && "/board".equals(words.getFirst())) {
                if (line.wordIndex() == 1) {
                    if (smallModel.getPlayer() != null && smallModel.getPlayer().getNickname() != null) {
                        candidates.add(new Candidate(smallModel.getPlayer().getNickname()));
                    }
                    if (smallModel.getOpponents() != null) {
                        for (PlayerView p : smallModel.getOpponents()) {
                            candidates.add(new Candidate(p.getNickname()));
                        }
                    }
                }
            }
        };

        return new AggregateCompleter(
                new ArgumentCompleter(new StringsCompleter("/pick_card", "/pick_building", "/end_turn", "/quit", "/clear", "/board", "/buildings"), NullCompleter.INSTANCE),
                new ArgumentCompleter(new StringsCompleter("/help"), new StringsCompleter("place_totem", "pick_card", "end_turn", "clear", "cards"), NullCompleter.INSTANCE),
                new ArgumentCompleter(new StringsCompleter("/place_totem"), totemCompleter, NullCompleter.INSTANCE),
                cardCompleter,
                boardCompleter,
                buildingCompleter
        );
    }
    
    /**
     *
     * Displays an interactive help screen containing character cards effects and explanations.
     */
    private void showCardHelp() {
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
            String input = lineReader.readLine("\nPress ENTER or type 'q' to return\n");

            // Input sanitization
            if (input == null) {
                input = "";
            }
            input = input.trim().toLowerCase();

            // Exit conditions
            if (input.equals("q") || input.isEmpty()) {
                break;
            } else {
                terminal.writer().println("Unrecognized command. Press ENTER or 'q' to return to the board.");
                terminal.writer().flush();
            }
        }
    }

    private void showPlayerBoard(String nickname) {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.puts(InfoCmp.Capability.cursor_home);

        PlayerView targetPlayer = null;
        if (smallModel.getPlayer().getNickname().equals(nickname)) {
            targetPlayer = smallModel.getPlayer();
        } else {
            for (PlayerView p : smallModel.getOpponents()) {
                if (p.getNickname().equals(nickname)) {
                    targetPlayer = p;
                    break;
                }
            }
        }

        if (targetPlayer == null) {
            terminal.writer().println("Player " + nickname + " not found.");
            terminal.writer().flush();
        } else {
            String[] title = centerOnScreen(new String[]{"  === Board of: " + nickname + " ===  "}, terminal);
            terminal.writer().println("\n" + title[0]);

            String[] charactersTitle = centerOnScreen(new String[]{"--- CHARACTERS ---"}, terminal);
            terminal.writer().println("\n" + charactersTitle[0] + "\n");
            tuiBoardRenderer.printCardRow(targetPlayer.getCharacters());

            String[] buildingsTitle = centerOnScreen(new String[]{"--- BUILDINGS ---"}, terminal);
            terminal.writer().println("\n" + buildingsTitle[0] + "\n");
            tuiBoardRenderer.printCardRow(targetPlayer.getBuildings());
            terminal.writer().flush();
        }

        while (true) {
            String input = lineReader.readLine("\nPress ENTER or type 'q' to return\n");

            // Input sanitization
            if (input == null) {
                input = "";
            }
            input = input.trim().toLowerCase();

            // Exit conditions
            if (input.equals("q") || input.isEmpty()) {
                break;
            } else {
                terminal.writer().println("Unrecognized command. Press ENTER or 'q' to return to the board.");
                terminal.writer().flush();
            }
        }

    }

    private void showBuildings() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.puts(InfoCmp.Capability.cursor_home);


        String[] buildingsTitle = centerOnScreen(new String[]{"  === TOP BUILDINGS ===  "}, terminal);
        terminal.writer().println("\n" + buildingsTitle[0] + "\n");
        List<Card> topBuildings = smallModel.getTopBuildings();

        TuiBuildingEffectVisitor effectVisitor = new TuiBuildingEffectVisitor();

        for (int i = 0; i < topBuildings.size(); i++) {
            Card b = topBuildings.get(i);
            String name = String.join(" ", b.getClass().getSimpleName().split("(?=\\p{Upper})"));
            String effect = effectVisitor.getEffect(b);

            terminal.writer().println(i + ") " + Style.YELLOW + name + Style.RESET);
            terminal.writer().println("\tEffect: " + effect);
            terminal.writer().println("\tCost: " + effectVisitor.getCost() + " Food");
            terminal.writer().println("\tPrestige: " + effectVisitor.getPrestige() + " Points");

            terminal.writer().println("");
        }

        buildingsTitle = centerOnScreen(new String[]{"  === BOTTOM BUILDINGS ===  "}, terminal);
        terminal.writer().println("\n" + buildingsTitle[0] + "\n");
        List<Card> bottomBuildings = smallModel.getBottomBuildings();

        for (int i = 0; i < bottomBuildings.size(); i++) {
            Card b = bottomBuildings.get(i);
            String name = String.join(" ", b.getClass().getSimpleName().split("(?=\\p{Upper})"));
            String effect = effectVisitor.getEffect(b);

            terminal.writer().println(i + ") " + Style.YELLOW + name + Style.RESET);
            terminal.writer().println("\tEffect: " + effect);
            terminal.writer().println("\tCost: " + effectVisitor.getCost() + " Food");
            terminal.writer().println("\tPrestige: " + effectVisitor.getPrestige() + " Points");


            terminal.writer().println("");
        }

        terminal.writer().flush();


        while (true) {
            String input = lineReader.readLine("\nPress ENTER or type 'q' to return\n");

            // Input sanitization
            if (input == null) {
                input = "";
            }
            input = input.trim().toLowerCase();

            // Exit conditions
            if (input.equals("q") || input.isEmpty()) {
                break;
            } else {
                terminal.writer().println("Unrecognized command. Press ENTER or 'q' to return to the board.");
                terminal.writer().flush();
            }
        }
    }

    /**
     * Clears the screen and displays the final leaderboard.
     */
    public void showLeaderboard(List<Score> finalScores) {

        terminal.puts(InfoCmp.Capability.clear_screen);
        String[] divider = TUI.centerOnScreen(new String[]{"========================================================"}, terminal);
        String[] title = TUI.centerOnScreen(new String[]{"🏆 LEADERBOARD 🏆"}, terminal);

        terminal.writer().println(Style.YELLOW + divider[0] + Style.RESET);
        terminal.writer().println(Style.YELLOW + title[0] + Style.RESET);
        terminal.writer().println(Style.YELLOW + divider[0] + Style.RESET);

        if (finalScores == null || finalScores.isEmpty()) {
            terminal.writer().println("   No scores available to display.");
            terminal.writer().flush();
            return;
        }

        // Make sure the data is sorted
        finalScores.sort((s1, s2) -> Integer.compare(s2.getPrestigeScore(), s1.getPrestigeScore()));

        for (int i = 0; i < finalScores.size(); i++) {
            Score s = finalScores.get(i);
            String name = s.getNickname();
            int prestige = s.getPrestigeScore();

            String prefix;
            String color;
            String suffix;

            if (i == 0) {
                prefix = " 1st Place ";
                color = Style.YELLOW;
                suffix = " 🥇 WINNER!";
            } else if (i == 1) {
                prefix = " 2nd Place ";
                color = Style.SILVER;
                suffix = " 🥈";
            } else if (i == 2) {
                prefix = " 3rd Place ";
                color = Style.ORANGE;
                suffix = " 🥉";
            } else {
                prefix = String.format(" %dth Place ", i + 1);
                color = Style.RESET;
                suffix = "";
            }

            String line = String.format("   %s | %-15s | %3d Points %s", prefix, name, prestige, suffix);

            terminal.writer().println(color + line + Style.RESET);
            terminal.writer().println("");
        }

        terminal.writer().println(Style.YELLOW + divider[0] + Style.RESET);
        terminal.writer().println("\n   " + Style.YELLOW + "Game Over. Thank you for playing Mesos!" + Style.RESET);
        terminal.writer().flush();
        terminal.writer().println("\nPress [ENTER] to exit...\n");

    }

    /**
     * Centers lines of text on the terminal screen horizontally.
     *
     * @param text     the array of strings to be centered
     * @param terminal the current terminal instance to measure width
     * @return an array of strings padded to center alignment
     */
    public static String[] centerOnScreen(String[] text, Terminal terminal) {
        if (text == null || text.length == 0) return new String[0];

        // Get terminal dimensions to compute vertical and horizontal centering
        int terminalWidth = terminal.getWidth();
        int textWidth = text[0].length();
        int horizontalPadding = Math.max(0, (terminalWidth - textWidth) / 2);
        String padString = " ".repeat(horizontalPadding);

        return Arrays.stream(text)
                .map(line -> padString + line)
                .toArray(String[]::new);
    }

    /**
     * Centers lines of text represented as StringBuilders on the terminal screen horizontally.
     *
     * @param text     the array of StringBuilders to be centered
     * @param terminal the current terminal instance to measure width
     * @return an array of strings padded to center alignment
     */
    public static String[] centerOnScreen(StringBuilder[] text, Terminal terminal) {

        if (text == null || text.length == 0) return new String[0];

        // Get terminal dimensions to compute vertical and horizontal centering
        int terminalWidth = terminal.getWidth();
        int textWidth = text[0].length();
        int horizontalPadding = Math.max(0, (terminalWidth - textWidth) / 2);
        String padString = " ".repeat(horizontalPadding);

        return Arrays.stream(text).map(StringBuilder::toString)
                .map(line -> padString + line)
                .toArray(String[]::new);
    }

    /**
     * Visitor class to handle the different types of DTOs that can be received from the server.
     */
    public class TUIDTOVisitor extends DTOVisitor {

        /**
         * If the dto we received signals an event being resolved, we should show a small "animation"
         *
         * @param dto the patch getting visited.
         */
        @Override
        public void visit(EventResolvedDTO dto) {
            eventDisplayQueue.offer(dto.getEventCard());
        }

        @Override
        public void visit(EndGameDTO dto) {
            hasGameEnded = true;
        }

        /**
         * If the dto we received is a generic small model update, we should just flag the interface to be redrawn, so that the new state gets rendered.
         *
         * @param dto the patch getting visited.
         */
        @Override
        public void visit(SmallModelEditor dto) {
            needsRedraw = true;
            render();
        }
    }


    /**
     * Called whenever the model updates from the server.
     * Flags the interface to be redrawn.
     *
     * @param dto the object containing the delta or the entire current state of the small model
     */
    @Override
    public void update(SmallModelEditor dto) {
        dto.accept(new TUIDTOVisitor());
    }


    /**
     * Renders the current state of the board in the terminal.
     */
    private synchronized void render() {
        // The TUI MUST be started before trying to render
        if (this.terminal == null || this.lineReader == null) return;

        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.puts(InfoCmp.Capability.cursor_home);


        if (currentlyDisplayingEvent != null) {
            drawEventAsciiArt(currentlyDisplayingEvent);

            if (lineReader.isReading()) {
                lineReader.callWidget(LineReader.REDRAW_LINE);
                lineReader.callWidget(LineReader.REDISPLAY);
            }
            terminal.writer().flush();
            needsRedraw = false;
            return;
        }

        if (hasGameEnded) {
            showLeaderboard(smallModel.getLeaderboard());
        }

        List<PlayerView> players = new ArrayList<>();
        players.add(smallModel.getPlayer());
        players.addAll(smallModel.getOpponents());

        tuiBoardRenderer.printCardRow(smallModel.getTopRow(), smallModel.getTribeDeckSize());
        tuiBoardRenderer.printOfferTrackAndTurnOrder(smallModel.getTurnOrderTile(), smallModel.getOfferTrack());
        tuiBoardRenderer.printCardRow(smallModel.getBottomRow());
        tuiBoardRenderer.printPlayerInfo(players);

        if (!statusMessage.isEmpty()) {
            terminal.writer().println(statusMessage);
        }

        if (!smallModel.getSystemMessage().isEmpty() || !smallModel.getSystemMessage().isBlank()) {
            terminal.writer().println("\nSystem> " + smallModel.getSystemMessage());
        } else {
            terminal.writer().println("\n");
        }

        if (smallModel.isActive()) {
            terminal.writer().println(Style.GREEN + smallModel.getPhase() + "> It's your Turn!" + Style.RESET);
        }

        if (lineReader.isReading()) {
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }

        terminal.writer().flush();
    }

    private void drawEventAsciiArt(EventCard eventCard) {
        TuiEventArtVisitor artVisitor = new TuiEventArtVisitor();
        eventCard.accept(artVisitor);

        String[] asciiArt = artVisitor.getAsciiArt();
        String color = artVisitor.getColor();
        String title = artVisitor.getTitle();

        int paddingTop = Math.max(0, (terminal.getHeight() - asciiArt.length) / 2);
        for (int i = 0; i < paddingTop; i++) {
            terminal.writer().println();
        }

        String[] centeredAscii = centerOnScreen(asciiArt, terminal);
        for (String line : centeredAscii) {
            terminal.writer().println(color + line + Style.RESET);
        }

        terminal.writer().println("\n");
        String[] subtitle = centerOnScreen(new String[]{"[ EVENT RESOLUTION PHASE - " + title + " ]"}, terminal);
        terminal.writer().println(subtitle[0]);
    }

    /**
     * Displays a colored ASCII banner for the game when starting the TUI.
     *
     * @param duration the time in milliseconds to wait before proceeding with the rest of the program
     */
    private void showBanner(long duration) {
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
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
