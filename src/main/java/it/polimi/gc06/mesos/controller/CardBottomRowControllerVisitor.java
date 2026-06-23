package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

/**
 * A visitor that handles the logic for picking a card from the bottom row.
 * It safely routes character cards to the turn manager while rejecting event cards,
 * which cannot be manually picked by players.
 */
public class CardBottomRowControllerVisitor extends CardVisitor {
    private final TurnManager turnManager;
    private final Player activePlayer;
    private final GameModel model;

    /**
     * Constructs a new CardBottomRowControllerVisitor.
     *
     * @param turnManager The game's turn manager.
     * @param activePlayer The player attempting to pick the card.
     * @param model The main game model.
     */
    public CardBottomRowControllerVisitor(TurnManager turnManager, Player activePlayer, GameModel model) {
        this.turnManager = turnManager;
        this.activePlayer = activePlayer;
        this.model = model;
    }

    /**
     * This method visits a CharacterCard and triggers the logic to pick it from the bottom row.
     *
     * @param card The CharacterCard being visited.
     */
    @Override
    public void visit(CharacterCard card) {
        turnManager.getPhase().pickCardFromBottom(turnManager, activePlayer, card, model.getBoard());
    }

    /**
     * This method visits an EventCard.
     * Since EventCards cannot be directly picked by players, this method throws an exception.
     *
     * @param card The EventCard being visited.
     * @throws IllegalGameActionException Always, as event cards cannot be picked.
     */
    @Override
    public void visit(EventCard card) {
        throw new IllegalGameActionException("Event cards cannot be picked!");
    }
}