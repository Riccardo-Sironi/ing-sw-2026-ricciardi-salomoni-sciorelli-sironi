package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CharactersPresenceVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

public class OfferResolutionPhase extends Phase {

    private boolean isStarted = false;


    /**
     * This method starts the offer resolution phase for the specified player.
     * Verifies that the player is the active one, applies the effects of the chosen tile slot,
     * and checks whether the player has immediately finished their resolution.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player starting their offer resolution.
     * @param tileSlot the tile slot chosen by the player on the offer track.
     * @throws IllegalPhaseActionException if it is not the specified player's turn.
     * @throws IllegalArgumentException if the provided tile slot is null.
     */
    @Override
    public void startPlayerOfferResolution(TurnManager turnManager, Player player, TileSlot tileSlot) throws IllegalPhaseActionException {
        if (turnManager.getActivePlayer() != player) {
            throw new IllegalPhaseActionException("It's not your turn yet!");
        }
        if (tileSlot == null) {
            throw new IllegalArgumentException("Tile slot is null!");
        }

        tileSlot.applyEffect();

        isStarted = true;

        checkIfPlayerIsFinished(turnManager, player, turnManager.getGameModel().getBoard());
    }

    /**
     * This method allows the active player to pick a CharacterCard from the top row.
     * Decrements the player's available top draws and checks if their turn is finished.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player performing the action.
     * @param card the CharacterCard being picked.
     * @param board the game board containing the cards.
     * @throws IllegalPhaseActionException if the phase hasn't started or the player has no top draws left.
     */
    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.pickCardFromTopRow(player, card);
        player.setTopDrawNum(player.getTopDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    /**
     * This method allows the active player to pick a CharacterCard from the bottom row.
     * Decrements the player's available bottom draws and checks if their turn is finished.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player performing the action.
     * @param card the CharacterCard being picked.
     * @param board the game board containing the cards.
     * @throws IllegalPhaseActionException if the phase hasn't started or the player has no bottom draws left.
     */
    @Override
    public void pickCardFromBottom(TurnManager turnManager, Player player, CharacterCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the bottom row anymore!");
        }

        board.pickCardFromBottomRow(player, card);
        player.setBottomDrawNum(player.getBottomDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    /**
     * This method allows the active player to pick a BuildingCard from the top row.
     * Decrements the player's available top draws and checks if their turn is finished.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player performing the action.
     * @param card the BuildingCard being picked.
     * @param board the game board containing the cards.
     * @throws IllegalPhaseActionException if the phase hasn't started or the player has no top draws left.
     */
    @Override
    public void pickCardFromTop(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException, IllegalGameActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getTopDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the top row anymore!");
        }

        board.buyBuildingFromTopRow(player, card);
        player.setTopDrawNum(player.getTopDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    /**
     * This method allows the active player to pick a BuildingCard from the bottom row.
     * Decrements the player's available bottom draws and checks if their turn is finished.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player performing the action.
     * @param card the BuildingCard being picked.
     * @param board the game board containing the cards.
     * @throws IllegalPhaseActionException if the phase hasn't started or the player has no bottom draws left.
     */
    @Override
    public void pickCardFromBottom(TurnManager turnManager, Player player, BuildingCard card, Board board) throws IllegalPhaseActionException, IllegalArgumentException, IllegalGameActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() <= 0) {
            throw new IllegalPhaseActionException("You can't draw from the bottom row anymore!");
        }

        board.buyBuildingFromBottomRow(player, card);
        player.setBottomDrawNum(player.getBottomDrawNum() - 1);
        checkIfPlayerIsFinished(turnManager, player, board);
    }

    /**
     * This method evaluates whether the active player has completed their offer resolution phase.
     * If the player has no remaining draws (top or bottom), they are moved from the offer track
     * to the turn order tile, and the next player on the offer track begins their resolution.
     * If the offer track is completely empty, the game transitions to the {@link EventResolutionPhase}
     * and automatically triggers the event resolution.
     *
     * @param turnManager the turn manager controlling the flow of the game.
     * @param player the player currently resolving their offer.
     * @param board the game board.
     * @throws IllegalPhaseActionException if the offer resolution phase has not been started.
     */
    public void checkIfPlayerIsFinished(TurnManager turnManager, Player player, Board board) throws IllegalPhaseActionException {

        if (!isStarted) {
            throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");
        }

        if (player.getBottomDrawNum() == 0 && player.getTopDrawNum() == 0) {
            TileSlot playerSlot = board.getOfferTrackPlayerSlot(player);
            playerSlot.removePlayer();

            turnManager.getPlayersOrder().remove(player);

            for (TileSlot orderTile : board.getTurnOrderTile().slots()) {
                if (orderTile.getPlayer() == null) {
                    orderTile.setPlayer(player);
                    break;
                }
            }

            if (!board.isOfferTrackEmpty()) {
                Player nextPlayer = null;

                for (TileSlot offerTrackTile : board.getOfferTrack()) {
                    if (offerTrackTile.getPlayer() != null) {
                        nextPlayer = offerTrackTile.getPlayer();
                        break;
                    }
                }

                turnManager.setPhase(new OfferResolutionPhase());
                turnManager.getPhase().startPlayerOfferResolution(turnManager, nextPlayer, board.getOfferTrackPlayerSlot(nextPlayer));
            }
        }


        // if the offer track is empty then we can move on with the next phase
        if (board.isOfferTrackEmpty()) {
            turnManager.setPhase(new EventResolutionPhase());

            turnManager.getPlayersOrder().clear();

            // refill the players order list with the new order of the players based on the turn order tile
            for (TileSlot orderTile : board.getTurnOrderTile().slots()) {
                if (orderTile.getPlayer() != null) {
                    turnManager.getPlayersOrder().addLast(orderTile.getPlayer());
                }
            }

            // we call the method, we don't wait for no request from no client
            turnManager.getPhase().resolveEvent(turnManager, board);
        }
    }

    /**
     * This method checks if the player has the right to skip picking from the top row.
     * A player can only skip if there are no CharacterCards remaining in the top row.
     *
     * @param player the player requesting to skip.
     * @param board  the game board containing the top row cards.
     * @return {@code true} if the player is allowed to skip, {@code false} otherwise.
     * @throws IllegalPhaseActionException if an invalid state occurs during the check.
     */
    private boolean checkForRightToSkipTop(Player player, Board board) throws IllegalPhaseActionException {
        if (player.getTopDrawNum() > 0) {
            CharactersPresenceVisitor charactersPresenceVisitor = new CharactersPresenceVisitor();
            board.getTopRow().forEach(card -> card.accept(charactersPresenceVisitor));
            return !charactersPresenceVisitor.areCharactersPresent();
        } else {
            return true;
        }
    }

    /**
     * This method checks if the player has the right to skip picking from the bottom row.
     * A player can only skip if there are no CharacterCards remaining in the bottom row.
     *
     * @param player the player requesting to skip.
     * @param board  the game board containing the top row cards.
     * @return {@code true} if the player is allowed to skip, {@code false} otherwise.
     * @throws IllegalPhaseActionException if an invalid state occurs during the check.
     */
    private boolean checkForRightToSkipBottom(Player player, Board board) throws IllegalPhaseActionException {
        if (player.getBottomDrawNum() > 0) {
            CharactersPresenceVisitor charactersPresenceVisitor = new CharactersPresenceVisitor();
            board.getBottomRow().forEach(card -> card.accept(charactersPresenceVisitor));
            return !charactersPresenceVisitor.areCharactersPresent();
        } else {
            return true;
        }
    }

    /**
     * /**
     * This method allows to skip the player picking phase if needed. An example is if there are no characters card to
     * pick or if the rows are empty.
     *
     * @param turnManager the model turn manager reference.
     * @param player      current player that wants to skip the picking phase.
     * @throws IllegalPhaseActionException if the player can't skip the phase or if the offer resolution phase for that player hasn't started yet.
     */
    @Override
    public void skipPick(TurnManager turnManager, Player player) throws IllegalPhaseActionException {
        if (!isStarted) throw new IllegalPhaseActionException("You have to start the offer resolution phase first!");

        if (checkForRightToSkipTop(player, turnManager.getGameModel().getBoard()) && checkForRightToSkipBottom(player, turnManager.getGameModel().getBoard())) {
            player.setTopDrawNum(0);
            player.setBottomDrawNum(0);

            checkIfPlayerIsFinished(turnManager, player, turnManager.getGameModel().getBoard());
        } else {
            throw new IllegalPhaseActionException("You can't skip the offer resolution phase!");
        }
    }

    /**
     * This method validates if the specified player has the right to skip their picks entirely.
     * Combines the checks for both the top and bottom rows.
     *
     * @param player the player requesting to skip.
     * @param board the game board.
     * @return {@code true} if the player can skip their remaining picks, {@code false} otherwise.
     */
    @Override
    public boolean checkForRightToSkip(Player player, Board board) {
        return checkForRightToSkipTop(player, board) && checkForRightToSkipBottom(player, board);
    }

    /**
     * This method returns the string representation of this phase.
     *
     * @return the string "offer_resolution".
     */
    @Override
    public String toString() {
        return "offer_resolution";
    }
}
