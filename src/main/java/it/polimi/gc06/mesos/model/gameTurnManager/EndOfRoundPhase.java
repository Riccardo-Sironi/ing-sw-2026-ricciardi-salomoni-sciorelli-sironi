package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CharactersPresenceVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;

import java.util.ArrayList;

public class EndOfRoundPhase extends Phase {

    private boolean justStarted;
    private boolean isEndOfRoundPickPlayerPresent;
    private int endOfRoundPickPlayerIndex;

    public EndOfRoundPhase() {
        this.justStarted = true;
        this.isEndOfRoundPickPlayerPresent = false;
        this.endOfRoundPickPlayerIndex = 0;
    }

    public void checkForEndOfRoundPick(TurnManager turnManager) throws IllegalPhaseActionException {
        ArrayList<Player> players = (ArrayList<Player>) turnManager.getPlayersOrder();

        for (Player player : players) {
            if (player.getBuildingCards().contains(turnManager.getPickFromTopCard())) {
                player.setTopDrawNum(1);
                isEndOfRoundPickPlayerPresent = true;

                if (players.getFirst() != player) {
                    endOfRoundPickPlayerIndex = players.indexOf(player);
                    turnManager.setActivePlayerIndex(endOfRoundPickPlayerIndex);
                }

                break; // Once we find the player, there is no point in looking further.
            }
        }

        justStarted = false;
    }

    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException {

        if (justStarted) {
            throw new IllegalPhaseActionException("You have to start the end of round phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.pickCardFromTopRow(player, card);
        player.setTopDrawNum(0);
        isEndOfRoundPickPlayerPresent = false;
        turnManager.setActivePlayerIndex(0);
        endOfRound(turnManager, board, turnManager.getGameModel());
    }

    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException, IllegalGameActionException {

        if (justStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.buyBuildingFromTopRow(player, card);
        player.setTopDrawNum(0);
        isEndOfRoundPickPlayerPresent = false;
        turnManager.setActivePlayerIndex(0);
        endOfRound(turnManager, board, turnManager.getGameModel());
    }

    /**
     * This method allow to skip the end of round pick if needed.
     *
     * @param turnManager
     * @param player      current player that wants to skip the end of round pick.
     * @throws IllegalPhaseActionException if the player tries to skip the end of round pick when he still has to pick
     *                                     a character card or if the end of round phase has just started.
     */
    @Override
    public void skipPick(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        if (justStarted) throw new IllegalPhaseActionException("You have to start the end of round phase first!");

        if (checkForRightToSkip(player, turnManager.getGameModel().getBoard())) {
            turnManager.getActivePlayer().setTopDrawNum(0);
            isEndOfRoundPickPlayerPresent = false;
            turnManager.setActivePlayerIndex(0);
            endOfRound(turnManager, turnManager.getGameModel().getBoard(), turnManager.getGameModel());
        } else {
            throw new IllegalPhaseActionException("You can't skip top pick in this phase!");
        }
    }

    private boolean checkForRightToSkip(Player player, Board board) throws IllegalPhaseActionException {
        if (player.getTopDrawNum() > 0) {
            CharactersPresenceVisitor charactersPresenceVisitor = new CharactersPresenceVisitor();
            board.getTopRow().forEach(card -> card.accept(charactersPresenceVisitor));

            return !charactersPresenceVisitor.areCharactersPresent();
        } else {
            return true;
        }
    }

    /**
     * this method executes the end of round actions:
     * resolves all event cards left in the bottom row for every player,
     * moves the top row cards to the bottom row and repopulate the top row
     *
     * @param turnManager the turn manager controlling the flow of the game.
     */
    @Override
    public void endOfRound(TurnManager turnManager, Board board, GameModel gameModel) throws IllegalPhaseActionException {
        // if this method is called from the previous phase then we have to check if a player
        // could pick a card from the top row before ending the round (so if someone has that specific building card)
        if (justStarted) {
            checkForEndOfRoundPick(turnManager);
        }

        if (!isEndOfRoundPickPlayerPresent) {
            board.moveFromTopToBottom();

            board.populateTopRow(gameModel);

            turnManager.setRound(turnManager.getRound() + 1);

            // TODO Check whether game is over
            if (board.isEndGame()) {

                // end of game routine:
                board.moveFromTopToBottom();

                ArrayList<EventCard> events = board.cleanBottomRow();

                events.forEach(card -> {
                    turnManager.getPlayersOrder().forEach(card::resolveEvent);
                });

                // TODO Go to EndGame Phase
                gameModel.endGame();
                return;
            }

            // if the game is not over we move on with the next round
            turnManager.setPhase(new PlacingTotemPhase());
        }
    }
}
