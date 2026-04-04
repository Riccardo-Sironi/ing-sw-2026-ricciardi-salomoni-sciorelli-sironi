package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;

public class HunterFoodIconVisitor extends CardVisitor {

    private final Player player;

    public HunterFoodIconVisitor(Player player) {
        this.player = player;
    }

    @Override
    public void visit(HunterCard card) {
        if (card.hasFoodIcon()) {
            player.addFoodTokens(player.getCharacterDeck().get(CharacterType.HUNTER).size());
        }
    }

}
