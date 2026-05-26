package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

public class CardTopRowControllerVisitor extends CardVisitor {
    private final TurnManager turnManager;
    private final Player activePlayer;
    private final GameModel model;


    public CardTopRowControllerVisitor(TurnManager turnManager, Player activePlayer, GameModel model) {
        this.turnManager = turnManager;
        this.activePlayer = activePlayer;
        this.model = model;
    }

    /**
     * This method visits a CharacterCard and triggers the logic to pick it from the top row.
     *
     * @param card the CharacterCard being visited.
     */
    @Override
    public void visit(CharacterCard card) {
        turnManager.getPhase().pickCardFromTop(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * This method visits an EventCard.
     * Since EventCards cannot be directly picked by players, this method throws an exception.
     *
     * @param card the EventCard being visited.
     * @throws IllegalGameActionException ALWAYS, as event cards cannot be picked.
     */
    @Override
    public void visit(EventCard card) {
        throw new IllegalGameActionException("Event cards cannot be picked!");
    }
}
