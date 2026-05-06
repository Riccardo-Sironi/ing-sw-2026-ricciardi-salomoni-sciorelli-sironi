package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.gameBoard.*;

public class TuiTileSlotRendererVisitor implements TileEffectVisitor {

    private String effectString = "";
    private String playerString = "";

    public String getEffectString() {
        return effectString;
    }

    @Override
    public void visit(ChooseCardTileEffect effect) {
        effectString = effect.getNumOfTopCards() + "T " + effect.getNumOfBottomCards() + "B";
    }

    @Override
    public void visit(FoodTileEffect effect) {
        effectString = "+" + effect.getNumFood() + " Food";
    }

    @Override
    public void visit(RemoveFoodTileEffect effect) {
        effectString = "-1 Food";
    }

    @Override
    public void visit(TileEffect effect) {
        // Fallback
        effectString = " ";
    }

    public String[] render(TileSlot slot) {
        String[] card = new String[7];

        slot.getTileEffect().accept(this);

        if (!slot.isEmpty() && slot.getPlayer() != null) {
            playerString = "P:" + toString(slot.getPlayer().getPlayerColor());
        }

        card[0] = "┌─────────┐";
        // Center the effect string, within 9 characters (11 - 2 for the borders)
        card[1] = "│" + centerText(playerString, 9) + "│";
        card[2] = "│         │";
        // Center the effect string, within 9 characters (11 - 2 for the borders)
        card[3] = "│" + centerText(effectString, 9) + "│";
        card[4] = "│         │";
        card[5] = "│         │";
        card[6] = "└─────────┘";

        // Clear strings
        playerString = "";
        effectString = "";

        return card;
    }

    // Helper method to center text in a fixed width
    private String centerText(String text, int width) {
        // If no text was input, set it to an empty string
        if (text == null) text = "";
        // if the text is larger than the card width, clamp it to the expected width
        if (text.length() >= width) return text.substring(0, width);

        // Calculate the padding from the left, and padding from the right
        int padLeft = (width - text.length()) / 2;
        int padRight = width - text.length() - padLeft;
        // Return the centered text
        return " ".repeat(padLeft) + text + " ".repeat(padRight);
    }

    public String toString(Color color) {
        return switch (color) {
            case Color.WHITE -> Style.WHITE;
            case Color.RED -> Style.RED;
            case Color.BLUE -> Style.BLUE;
            case Color.YELLOW -> Style.YELLOW;
            case Color.PURPLE -> Style.PURPLE;
        };
    }

}
