package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import org.jline.terminal.Terminal;

import java.util.Arrays;
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
        if (rowCards == null || rowCards.isEmpty()) {
            String centeredEmpty = Arrays.toString(TUI.centerOnScreen(new String[]{"[Empty Row]"}, terminal));
            terminal.writer().println(centeredEmpty);
            return;
        }

        // StringBuilder is a mutable sequence of characters
        // This allows us to append the cards one after another without creating a new string every time.
        StringBuilder[] rowLines = new StringBuilder[CARD_HEIGHT];
        for (int i = 0; i < CARD_HEIGHT; i++) {
            rowLines[i] = new StringBuilder();
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

        String[] centeredText = centerOnScreen(rowLines, terminal);

        for (String line : centeredText) {
            terminal.writer().println(line);
        }


    }

    public void printOfferTrack(List<TileSlotView> tiles) {
        if (tiles == null || tiles.isEmpty()) {
            terminal.writer().println("[Empty Row]");
            return;
        }

        // StringBuilder is a mutable sequence of characters
        // This allows us to append the cards one after another without creating a new string every time.
        StringBuilder[] rowLines = new StringBuilder[5];
        for (int i = 0; i < 5; i++) {
            rowLines[i] = new StringBuilder();
        }
        // We only need one visitor, and call visit every time we want to render a new card
        TuiTileSlotRendererVisitor renderer = new TuiTileSlotRendererVisitor();

        for (TileSlotView tile : tiles) {
            String[] renderedCard = renderer.render(tile);

            // Append the card, and put a separator at the end
            for (int i = 0; i < 5; i++) {
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
            terminal.writer().println("[Empty Row]");
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


}
