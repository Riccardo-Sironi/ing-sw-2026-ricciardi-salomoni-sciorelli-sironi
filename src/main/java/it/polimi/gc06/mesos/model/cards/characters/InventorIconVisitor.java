package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.cards.CardVisitor;

public class InventorIconVisitor extends CardVisitor {

    private InventionIcon icon;

    public InventorIconVisitor(){
        icon = null;
    }

    @Override
    public void visit(InventorCard card){
        icon = card.getIcon();
    }

    public InventionIcon getAndClearIcon(){
        InventionIcon oldIcon = icon;
        icon = null;
        return oldIcon;
    }

}
