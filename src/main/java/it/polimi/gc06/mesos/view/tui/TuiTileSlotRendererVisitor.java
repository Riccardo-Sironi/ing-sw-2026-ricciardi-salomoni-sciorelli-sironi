package it.polimi.gc06.mesos.view.tui;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.gameBoard.*;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;

public class TuiTileSlotRendererVisitor implements TuiRenderer<TileSlotView>, TileEffectVisitor {

    private String effectString = "";

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

    public String[] render(TileSlotView slot) {
        String[] card = new String[5];

        slot.getTileEffect().accept(this);

        card[0] = "┌─────────┐";
        // Center the player totem placeholder, if present, within 9 characters (11 - 2 for the borders)
        if (!slot.isEmpty()) {
            String playerString = toString(slot.getPlayer().getColor()) + centerText("P", 9) + Style.RESET;
            card[1] = "│" + playerString + "│";
        } else {
            card[1] = "│         │";
        }
        //card[2] = "│         │";
        // Center the effect string, within 9 characters (11 - 2 for the borders)
        //card[4] = "│         │";
        card[2] = "│         │";
        card[3] = "│" + centerText(effectString, 9) + "│";
        card[4] = "└─────────┘";

        // Clear strings
        effectString = "";

        return card;
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
