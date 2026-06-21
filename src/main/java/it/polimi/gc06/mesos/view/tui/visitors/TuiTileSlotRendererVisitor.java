package it.polimi.gc06.mesos.view.tui.visitors;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.gameBoard.*;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import it.polimi.gc06.mesos.view.tui.Style;
import it.polimi.gc06.mesos.view.tui.TuiRenderer;

/**
 * A visitor that generates an ASCII representation for different types of tile slots
 * to be rendered in the Text-based User Interface.
 */
public class TuiTileSlotRendererVisitor implements TuiRenderer<TileSlotView>, TileEffectVisitor {

    private String effectString = "";


    /**
     * Configures the layout properties for a ChooseCardTileEffect.
     * Sets the effect string format for selecting cards from top or bottom layers.
     *
     * @param effect the ChooseCardTileEffect to be visited
     */
    @Override
    public void visit(ChooseCardTileEffect effect) {
        effectString = effect.getNumOfTopCards() + "T " + effect.getNumOfBottomCards() + "B";
    }

    /**
     * Configures the layout properties for a FoodTileEffect.
     * Sets the effect string to display positive food generation.
     *
     * @param effect the FoodTileEffect to be visited
     */
    @Override
    public void visit(FoodTileEffect effect) {
        effectString = "+" + effect.getNumFood() + " Food";
    }

    /**
     * Configures the layout properties for a RemoveFoodTileEffect.
     * Sets the effect string to represent the loss of food.
     *
     * @param effect the RemoveFoodTileEffect to be visited
     */
    @Override
    public void visit(RemoveFoodTileEffect effect) {
        effectString = "-1 Food";
    }

    /**
     * Fallback configuration for any other unspecified TileEffect.
     * Leaves the printed string mostly blank.
     *
     * @param effect the generic TileEffect to be visited
     */
    @Override
    public void visit(TileEffect effect) {
        // Fallback
        effectString = " ";
    }

    /**
     * Render the Tile Slot Box based on the provided generic slot parameter.
     * Includes information on player standing, its totem presence, and specific effect formatting.
     *
     * @param slot the tile slot's parameters representing the board section to display
     * @return an array of strings providing structural ASCII layout lines
     */
    public String[] render(TileSlotView slot) {
        String[] card = new String[7];

        slot.getTileEffect().accept(this);

        card[0] = "┌─────────┐";
        // Center the player totem placeholder, if present, within 9 characters (11 - 2 for the borders)
        card[1] = "│         │";
        if (!slot.isEmpty()) {
            String playerString = toString(slot.getPlayer().getColor()) + centerText("P", 9) + Style.RESET;
            card[2] = "│" + playerString + "│";
        } else {
            card[2] = "│         │";
        }
        // Center the effect string, within 9 characters (11 - 2 for the borders)
        card[5] = "│" + centerText(effectString, 9) + "│";
        card[3] = "│         │";
        card[4] = "│         │";
        card[6] = "└─────────┘";

        // Clear strings
        effectString = "";

        return card;
    }

    /**
     * Converts a player's model Color object to its string representing its color attribute via ANSI constants.
     *
     * @param color player's unique color
     * @return an equivalent ANSI styling escape representation indicating layout hue
     */
    public String toString(Color color) {
        return switch (color) {
            case Color.WHITE -> Style.WHITE;
            case Color.ORANGE -> Style.ORANGE;
            case Color.TURQUOISE -> Style.CYAN;
            case Color.YELLOW -> Style.YELLOW;
            case Color.PURPLE -> Style.PURPLE;
        };
    }

}
