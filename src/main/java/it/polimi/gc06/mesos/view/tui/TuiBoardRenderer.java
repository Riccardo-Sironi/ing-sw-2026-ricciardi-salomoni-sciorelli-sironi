package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import it.polimi.gc06.mesos.view.tui.visitors.TuiCardRendererVisitor;
import it.polimi.gc06.mesos.view.tui.visitors.TuiTileSlotRendererVisitor;
import org.jline.terminal.Terminal;

import java.util.List;

import static it.polimi.gc06.mesos.view.tui.TUI.centerOnScreen;

public class TuiBoardRenderer {
    private static final int CARD_HEIGHT = 7;
    private static final String GAP_SEPARATOR = " ";
    private final Terminal terminal;

    public TuiBoardRenderer(Terminal terminal) {
        this.terminal = terminal;
    }

    public void printCardRow(List<Card> rowCards) {
        printCardRow(rowCards, null);
    }

    // We use Integer instead of int, in order to allow null values, which indicate that we don't want to render the deck card
    public void printCardRow(List<Card> rowCards, Integer deckSize) {
        if (rowCards == null || rowCards.isEmpty()) {
            String[] centeredEmpty = TUI.centerOnScreen(new String[]{"[Empty Row]"}, terminal);
            for (String line : centeredEmpty) {
                terminal.writer().println(line);
            }
            return;
        }


        // StringBuilder is a mutable sequence of characters
        // This allows us to append the cards one after another without creating a new string every time.
        StringBuilder[] rowLines = new StringBuilder[CARD_HEIGHT];
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i] = new StringBuilder();
        }

        if (deckSize != null && deckSize >= 0) {
            String[] dCard = new String[CARD_HEIGHT];
            dCard[0] = "┌─────────┐";
            dCard[1] = "│ DECK    │";
            dCard[2] = "│         │";
            dCard[3] = "│   " + String.format("%2d", deckSize) + "    │";
            dCard[4] = "│         │";
            dCard[5] = "│         │";
            dCard[6] = "└─────────┘";

            String extraSpace = "   ";
            for (int i = 0; i < CARD_HEIGHT; i++) {
                rowLines[i].append(extraSpace).append(dCard[i]);
            }
        }

        // We only need one visitor, and call visit every time we want to render a new card
        TuiCardRendererVisitor renderer = new TuiCardRendererVisitor();

        for (Card card : rowCards) {

            String[] renderedCard = renderer.render(card);

            // Append the card, and put a separator at the end
            for (int i = 0; i < CARD_HEIGHT; i++) {
                rowLines[i].append(renderedCard[i]).append(GAP_SEPARATOR);
            }
        }

        String[] buildingsCard = new String[CARD_HEIGHT];
        buildingsCard[0] = "┌─────────┐";
        buildingsCard[1] = "│         │";
        buildingsCard[2] = "│         │";
        buildingsCard[3] = "│BUILDINGS│";
        buildingsCard[4] = "│         │";
        buildingsCard[5] = "│         │";
        buildingsCard[6] = "└─────────┘";

        String extraSpace = "   ";
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i].append(extraSpace).append(buildingsCard[i]);
        }

        String[] centeredText = centerOnScreen(rowLines, terminal);

        for (String line : centeredText) {
            terminal.writer().println(line);
        }


    }

    public void printOfferTrackAndTurnOrder(List<PlayerView> turnOrderTile, List<TileSlotView> tiles) {
        if (tiles == null || tiles.isEmpty()) {
            String[] centeredEmpty = TUI.centerOnScreen(new String[]{"[Empty Row]"}, terminal);
            for (String line : centeredEmpty) {
                terminal.writer().println(line);
            }
            return;
        }


        // StringBuilder is a mutable sequence of characters
        // This allows us to append the cards one after another without creating a new string every time.
        StringBuilder[] rowLines = new StringBuilder[CARD_HEIGHT];
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i] = new StringBuilder();
        }

        String[] offerTrack = buildTurnOrderBox(turnOrderTile);
        String extraSpace = "   ";
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i].append(extraSpace).append(offerTrack[i]);
        }

        // We only need one visitor, and call visit every time we want to render a new card
        TuiTileSlotRendererVisitor renderer = new TuiTileSlotRendererVisitor();

        for (TileSlotView tile : tiles) {
            String[] renderedCard = renderer.render(tile);

            // Append the card, and put a separator at the end
            for (int i = 0; i < CARD_HEIGHT; i++) {
                rowLines[i].append(renderedCard[i]).append(GAP_SEPARATOR);
            }
        }


        String[] centeredText = centerOnScreen(rowLines, terminal);

        for (String line : centeredText) {
            terminal.writer().println(line);
        }

    }

    public void printPlayerInfo(List<PlayerView> players) {
        if (players == null || players.isEmpty()) {
            String[] centeredEmpty = TUI.centerOnScreen(new String[]{"[Empty Row]"}, terminal);
            for (String line : centeredEmpty) {
                terminal.writer().println(line);
            }
            return;
        }

        // StringBuilder is a mutable sequence of characters
        // This allows us to append the cards one after another without creating a new string every time.
        StringBuilder[] rowLines = new StringBuilder[CARD_HEIGHT];
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i] = new StringBuilder();
        }
        // We only need one visitor, and call visit every time we want to render a new card
        TuiPlayerInfoRenderer renderer = new TuiPlayerInfoRenderer();

        for (PlayerView player : players) {
            String[] renderedCard = renderer.render(player);

            // Append the card, and put a separator at the end
            for (int i = 0; i < CARD_HEIGHT; i++) {
                rowLines[i].append(renderedCard[i]).append(GAP_SEPARATOR);
            }
        }


        String[] centeredText = centerOnScreen(rowLines, terminal);

        for (String line : centeredText) {
            terminal.writer().println(line);
        }

    }

    public String[] buildTurnOrderBox(List<PlayerView> turnOrder) {
        String[] box = new String[7];
        box[0] = "┌─────────┐";
        box[6] = "└─────────┘";

        TuiTileSlotRendererVisitor colorMapper = new TuiTileSlotRendererVisitor();

        for (int i = 0; i < 5; i++) {
            int totalPlayers = (turnOrder != null) ? turnOrder.size() : 0;
            if (i < totalPlayers) {
                PlayerView p = turnOrder.get(i);
                if (p != null) {
                    String color = colorMapper.toString(p.getColor());
                    String player = color + "[P]" + Style.RESET;
                    box[i + 1] = "│   " + player + "   │";
                } else {
                    box[i + 1] = "│   [ ]   │";
                }
            } else {
                box[i + 1] = "│         │";
            }
        }
        return box;
    }

}
