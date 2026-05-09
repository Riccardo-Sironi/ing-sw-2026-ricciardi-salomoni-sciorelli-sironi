package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;

public class TuiCardRendererVisitor extends CardVisitor implements TuiRenderer<Card> {

    private String eraStr = "";
    private String topRight = "";
    private String bottomLeft = "";
    private String bottomRight = "";
    private String center = "";

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

    @Override
    public void visit(HunterCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = card.hasFoodIcon() ? "\uD83C\uDF56" : "";
        center = "[H]";
    }

    @Override
    public void visit(ShamanCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = Style.YELLOW + "★: " + card.getStars() + Style.RESET;
        center = "[S]";
    }

    @Override
    public void visit(BuilderCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = "\uD83C\uDF56-" + card.getFoodDiscount();
        bottomLeft = "\uD83E\uDD47" + card.getPrestige();
        center = "[B]";
    }

    @Override
    public void visit(GathererCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[G]";
        topRight = "\uD83E\uDD57-3";
    }

    public void visit(ArtistCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[P]";
    }

    public void visit(InventorCard card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        topRight = getInventorSymbol(card.getIcon());
        center = "[I]";
    }

    public void visit(SustenanceEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[SUST]";
        bottomLeft = "-" + card.getPrestigeLoss();
        bottomRight = "*\uD83D\uDC64";
    }

    public void visit(HuntEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[HUNT]";
        bottomLeft = "\uD83E\uDD47+" + card.getPrestigeGain();
        bottomRight = "\uD83C\uDF56+1";
    }

    public void visit(RitualEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[RITUAL]";
        bottomRight = "\uD83E\uDD47" + "+" + card.getPrestigeGain();
        bottomLeft = "\uD83E\uDD47" + "-" + card.getPrestigeLoss();
    }

    public void visit(PaintingsEvent card) {
        eraStr = card.getEra().name().replace("ERA_", "");
        center = "[PAINT]";
        topRight = card.getMinNumberOfArtists() + "\uD83D\uDC64";
        bottomRight = "\uD83E\uDD47" + "+" + card.getPrestigeGain();
        bottomLeft = "\uD83E\uDD47" + "-" + card.getPrestigeLoss();
    }

    private String getInventorSymbol(InventionIcon icon) {
        return switch (icon) {
            case BOAT -> "⛵";
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
