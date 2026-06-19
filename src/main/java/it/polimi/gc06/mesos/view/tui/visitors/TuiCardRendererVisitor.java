package it.polimi.gc06.mesos.view.tui.visitors;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import it.polimi.gc06.mesos.view.tui.Style;
import it.polimi.gc06.mesos.view.tui.TuiRenderer;

/**
 * A visitor that generates an ASCII representation for different types of cards
 * to be rendered in the Text-based User Interface.
 */
public class TuiCardRendererVisitor extends CardVisitor implements TuiRenderer<Card> {

    private String eraStr = "";
    private String topRight = "";
    private String bottomLeft = "";
    private String bottomRight = "";
    private String center = "";

    /**
     * Entrypoint for rendering a Generic Card string array view.
     * This method resets the formatting properties, triggers the visitor
     * on the specific Card class to populate fields, builds the final
     * ASCII box layout, and returns it.
     *
     * @param card the generic card object to render
     * @return an array of strings representing the 7 lines of the card box
     */
    @Override
    public String[] render(Card card) {

        // A card is composed of seven strings (rows)
        String[] renderedCard = new String[7];

        card.accept(this);

        renderedCard[0] = "┌─────────┐";
        // %-4s aligns left (4 char max), %4s aligns right (4 char max)
        renderedCard[1] = String.format("│%-4s %4s│", eraStr, topRight);
        renderedCard[2] = "│         │";
        // Center the card name, within 9 characters (11 - 2 for the borders)
        renderedCard[3] = "│" + centerText(center, 9) + "│";
        renderedCard[4] = "│         │";
        renderedCard[5] = String.format("│%-4s %4s│", bottomLeft, bottomRight);
        renderedCard[6] = "└─────────┘";

        // Clear corners
        eraStr = "";
        topRight = "";
        bottomLeft = "";
        bottomRight = "";
        center = "";

        return renderedCard;
    }

    /**
     * Configures the layout properties for a Hunter card.
     * Sets the era, the food icon if applicable, and the [H] tag.
     *
     * @param card the HunterCard to be visited
     */
    @Override
    public void visit(HunterCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = card.hasFoodIcon() ? "\uD83C\uDF56" : "";
        center = "[H]";
    }

    /**
     * Configures the layout properties for a Shaman card.
     * Sets the era, the stars amount, and the [S] tag.
     *
     * @param card the ShamanCard to be visited
     */
    @Override
    public void visit(ShamanCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = Style.YELLOW + "★: " + card.getnStars() + Style.RESET;
        center = "[S]";
    }

    /**
     * Configures the layout properties for a Builder card.
     * Sets the era, food discount, prestige points, and the [B] tag.
     *
     * @param card the BuilderCard to be visited
     */
    @Override
    public void visit(BuilderCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = "\uD83C\uDF56-" + card.getFoodDiscount();
        bottomLeft = "\uD83E\uDD47" + card.getPrestige();
        center = "[B]";
    }

    /**
     * Configures the layout properties for a Gatherer card.
     * Sets the era, a fixed food discount text, and the [G] tag.
     *
     * @param card the GathererCard to be visited
     */
    @Override
    public void visit(GathererCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[G]";
        topRight = "\uD83E\uDD57-3";
    }

    /**
     * Configures the layout properties for an Artist card.
     * Sets the era and the [A] tag.
     *
     * @param card the ArtistCard to be visited
     */
    public void visit(ArtistCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[A]";
    }

    /**
     * Configures the layout properties for an Inventor card.
     * Sets the era, the specific invention icon emoji, and the [I] tag.
     *
     * @param card the InventorCard to be visited
     */
    public void visit(InventorCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = getInventorSymbol(card.getIcon());
        center = "[I]";
    }

    /**
     * Configures the layout properties for a Sustenance event card.
     * Sets the era, prestige loss format, worker token dependency icon, and the [SUST] tag.
     *
     * @param card the SustenanceEvent to be visited
     */
    public void visit(SustenanceEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[SUST]";
        bottomLeft = "-" + card.getPrestigeLoss();
        bottomRight = "*\uD83D\uDC64";
    }

    /**
     * Configures the layout properties for a Hunt event card.
     * Sets the era, prestige gain, food token gain, and the [HUNT] tag.
     *
     * @param card the HuntEvent to be visited
     */
    public void visit(HuntEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[HUNT]";
        bottomLeft = "\uD83E\uDD47+" + card.getPrestigeGain();
        bottomRight = "\uD83C\uDF56+1";
    }

    /**
     * Configures the layout properties for a Ritual event card.
     * Sets the era, prestige gain, prestige loss, and the [RITUAL] tag.
     *
     * @param card the RitualEvent to be visited
     */
    public void visit(RitualEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[RITUAL]";
        bottomRight = "\uD83E\uDD47" + card.getPrestigeGain();
        bottomLeft = "\uD83E\uDD47" + "-" + card.getPrestigeLoss();
    }

    /**
     * Configures the layout properties for a Paintings event card.
     * Sets the era, prestige gain, prestige loss, minimum artist requirement, and the [PAINT] tag.
     *
     * @param card the PaintingsEvent to be visited
     */
    public void visit(PaintingsEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[PAINT]";
        topRight = card.getMinNumberOfArtists() + "\uD83D\uDC64";
        bottomRight = "\uD83E\uDD47" + card.getPrestigeGain();
        bottomLeft = "\uD83E\uDD47" + "-" + card.getPrestigeLoss();
    }

    /**
     * Maps the internal enum of Invention Icons to Unicode Emojis
     * representing the kind of invention the inventor provides.
     *
     * @param icon the invention icon enum to map
     * @return the string containing the appropriate emoji symbol
     */
    private String getInventorSymbol(InventionIcon icon) {
        return switch (icon) {
            case BOAT -> "\u26F5\uFE0E";
            case ARROWHEAD -> "\uD83C\uDFF9";
            case HOOK -> "\uD83E\uDE9D";
            case NECKLACE -> "\uD83D\uDCFF";
            case BOWL -> "\uD83E\uDD63";
            case ROPE -> "\uD83E\uDEA2";
            case FIGURE -> "\uD83D\uDDFF";
            case FLUTE -> "\uD83E\uDE88";
            case HIDE -> "\uD83D\uDCDC";
            case BREAD -> "\uD83E\uDD56";
        };
    }
}
