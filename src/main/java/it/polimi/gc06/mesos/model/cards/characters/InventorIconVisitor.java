package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

public class InventorIconVisitor extends CardVisitor {

    private InventionIcon icon;

    public InventorIconVisitor(){
        icon = null;
    }

    /**
     * this method visits an InventorCard and extracts its invention icon.
     *
     * @param card the InventorCard being visited.
     */
    @Override
    public void visit(InventorCard card){
        icon = card.getIcon();
    }


    /**
     * this method retrieves the extracted icon and resets the visitor's state.
     *
     * @return the {@link InventionIcon} extracted from the visited card, or null if no card was visited.
     */
    public InventionIcon getAndClearIcon(){
        InventionIcon oldIcon = icon;
        icon = null;
        return oldIcon;
    }

}
