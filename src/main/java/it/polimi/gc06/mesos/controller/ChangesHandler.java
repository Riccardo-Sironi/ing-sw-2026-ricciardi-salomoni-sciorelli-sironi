package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.dtos.*;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChangesHandler {

    private int lastRound;
    private String lastPhase;
    private Era lastEra;
    private final ArrayList<PlayerState> lastPlayersState;
    private final GameModel model;
    private final TurnManager turnManager;
    private final Board board;

    public ChangesHandler(GameModel model) {
        this.model = model;
        this.turnManager = model.getTurnManager();
        this.board = model.getBoard();
        this.lastPhase = null;
        this.lastRound = -1;
        this.lastEra = null;

        this.lastPlayersState = new ArrayList<>();
        for (Player p : model.getPlayers()) {
            lastPlayersState.add(new PlayerState(p));
        }
    }

    /**
     * Returns all changes from last state registration.
     * It compares the current model state with the previously registered one and produces a list of
     * SmallModelEditor updates that represent the deltas.
     *
     * @return all the {@link SmallModelEditor} DTOs summarizing the changes.
     */
    public ArrayList<SmallModelEditor> getChanges() {

        ArrayList<SmallModelEditor> changes = new ArrayList<>();

        //checks last era
        if (lastEra == null || !lastEra.equals(board.getCurrentEra())) {
            changes.add(new EraChangeDTO(board.getCurrentEra()));
        }

        //check last phase
        if (lastPhase == null || !lastPhase.equals(turnManager.getPhase().toString())) {
            // TODO : for now its hard coded this way, we'll do a a refactor in the future to better handle this
            if (turnManager.getPhase().toString().equals(new PlacingTotemPhase().toString())) {
                changes.add(new PhaseChangeDTO(new EventResolutionPhase().toString()));
                changes.add(new PhaseChangeDTO(new EndOfRoundPhase().toString()));
            }
            changes.add(new PhaseChangeDTO(turnManager.getPhase().toString()));
        }

        //check resources & prestige diff
        for (Player p : model.getPlayers()) {
            PlayerState ps = lastPlayersState.stream().filter(s -> s.getPlayer().getNickname().equals(p.getNickname()))
                    .findFirst().orElseThrow(IllegalStateException::new);
            changes.add(new PlayerResourcesChangeDTO(
                    ps.player.getNickname(), p.getTopDrawNum(), p.getBottomDrawNum(),
                    p.getFoodTokens(), p.getPrestigeTokens()
            ));
        }

        //checks if the lastPlayerWhoCouldSkip can still skip
        if (getPlayerWhoCouldSkip() != null) try {
            if (!turnManager.getPhase().checkForRightToSkip(getPlayerWhoCouldSkip(), board)) {
                PlayerStateChangeDTO dto = new PlayerStateChangeDTO(getPlayerWhoCouldSkip().getNickname());
                dto.setCanSkip(false);
                changes.add(dto);
            }
        } catch (IllegalPhaseActionException _) {
            PlayerStateChangeDTO dto = new PlayerStateChangeDTO(getPlayerWhoCouldSkip().getNickname());
            dto.setCanSkip(false);
            changes.add(dto);
        }

        if (lastPhase != null && lastPhase.equals(new OfferResolutionPhase().toString()) && !lastPhase.equals(turnManager.getPhase().toString())) {
            changes.add(new TotemTurnMoveDTO());
        }

        try {
            if (turnManager.getPhase().checkForRightToSkip(turnManager.getActivePlayer(), board)) {

                PlayerState ps = lastPlayersState.stream()
                        .filter(s -> s.getPlayer().getNickname().equals(turnManager.getActivePlayer().getNickname()))
                        .findFirst()
                        .orElse(null);

                if (ps != null && !ps.isCanSkip()) {
                    PlayerStateChangeDTO dto = new PlayerStateChangeDTO(turnManager.getActivePlayer().getNickname());
                    dto.setCanSkip(true);
                    changes.add(dto);

                    ps.setCanSkip(true);
                }
            }
        } catch (IllegalPhaseActionException _) {
        }

        // TODO : handle this (FILLIPPO LOOK HERE)
//        if (board.isEndGame()) {
//            changes.add(new LeaderboardChangeDTO(model.getLeaderboard().getScores()));
//            return changes;
//        }

        //check if the round has changed
        if (lastRound != turnManager.getRound()) {
            changes.add(new RoundChangeDTO(turnManager.getRound()));
            int deckSize = model.getTribeCardsDeck().values().stream().mapToInt(ArrayList::size).sum();

            changes.add(new TopRowRefillDTO(new ArrayList<>(board.getTopRow()), new ArrayList<>(board.getBottomRow()), deckSize));
        }

        // dto for building refill (new era)
        if (lastEra == null || !lastEra.equals(board.getCurrentEra())) {
            int deckSize = model.getTribeCardsDeck().values().stream().mapToInt(ArrayList::size).sum();
            changes.add(new BuildingsRefillDTO(new ArrayList<>(board.getTopBuildings()), new ArrayList<>(board.getBottomBuildings()), deckSize));
        }


        // we always send the active player, not just when it changes, we constantly need this information
        PlayerStateChangeDTO activePlayerDTO = new PlayerStateChangeDTO(turnManager.getActivePlayer().getNickname());
        activePlayerDTO.setIsActive(true);
        changes.add(activePlayerDTO);

        return changes;
    }

    /**
     * Internally saves the current game state to prepare for the next getChanges() call.
     * This snapshot is used to compute deltas.
     */
    public void registerState() {
        lastRound = turnManager.getRound();
        lastPhase = turnManager.getPhase().toString();
        lastEra = board.getCurrentEra();

        //saves players resources & prestige
        for (Player p : model.getPlayers()) {
            PlayerState ps = lastPlayersState.stream().filter(s -> s.getPlayer().getNickname().equals(p.getNickname()))
                    .findFirst().orElseThrow(IllegalStateException::new);
            ps.setBottomDraw(p.getBottomDrawNum());
            ps.setTopDraw(p.getTopDrawNum());
            ps.setFood(p.getFoodTokens());
            ps.setPrestige(p.getPrestigeTokens());
        }

        //saves players flags
        lastPlayersState.forEach(s -> s.setActive(false));
        lastPlayersState.forEach(s -> s.setCanSkip(false));
        try {
            if (turnManager.getPhase().checkForRightToSkip(
                    turnManager.getActivePlayer(), board)) {
                PlayerState ps = lastPlayersState.stream().filter(s -> s.player.getNickname()
                        .equals(turnManager.getActivePlayer().getNickname())).findFirst().orElseThrow(IllegalStateException::new);
                ps.setCanSkip(true);
            }
        } catch (IllegalPhaseActionException _) {
        }

        PlayerState ps = lastPlayersState.stream().filter(s -> s.player.getNickname()
                .equals(turnManager.getActivePlayer().getNickname())).findFirst().orElseThrow(IllegalStateException::new);
        ps.setActive(true);
    }

    /**
     * Retrieves the first player who had the right to skip during the last registered state.
     *
     * @return the Player who could skip, or null if none was found.
     */
    private Player getPlayerWhoCouldSkip() {
        return lastPlayersState.stream().filter(PlayerState::canSkip).map(PlayerState::getPlayer).findFirst().orElse(null);
    }

    /**
     * Retrieves the player who was active during the last registered state.
     *
     * @return the last active Player, or null if none was found.
     */
    private Player getLastActivePlayer() {
        return lastPlayersState.stream().filter(PlayerState::isActive).map(PlayerState::getPlayer).findFirst().orElse(null);
    }

    /**
     * Returns the current game state expressed as a {@link SmallModelEditor}.
     * Should be used only when the game has just started to provide the full initial setup to a client.
     *
     * @param nickname the nickname of the player that will receive this DTO.
     * @return a {@link SmallModelEditor} containing the full initial setup.
     */
    public SmallModelEditor getStartingStateAsDTO(String nickname) {

        Map<String, Integer> foodMap = new HashMap<>();
        Map<String, Color> colorMap = new HashMap<>();
        Map<String, Integer> prestigeMap = new HashMap<>(); // Mappa per i token prestigio iniziale

        int topDrawNum = 0;
        int bottomDrawNum = 0;

        for (Player p : model.getPlayers()) {
            foodMap.put(p.getNickname(), p.getFoodTokens());
            colorMap.put(p.getNickname(), p.getPlayerColor());
            prestigeMap.put(p.getNickname(), p.getPrestigeTokens());

            // Estraiamo i draw_num di partenza specifici del client che si sta connettendo
            if (p.getNickname().equals(nickname)) {
                topDrawNum = p.getTopDrawNum();
                bottomDrawNum = p.getBottomDrawNum();
            }
        }

        boolean isActive = turnManager.getActivePlayer().getNickname().equals(nickname);

        ArrayList<TileEffect> effects = new ArrayList<>(
                board.getOfferTrack().stream().map(TileSlot::getTileEffect).toList()
        );

        int currentDeckSize = 0;
        if (model.getTribeCardsDeck() != null) {
            currentDeckSize = model.getTribeCardsDeck().get(Era.ERA_I).size()
                    + model.getTribeCardsDeck().get(Era.ERA_II).size()
                    + model.getTribeCardsDeck().get(Era.ERA_III).size();
        }

        return new LobbyInitializedDTO(
                nickname,
                new ArrayList<>(turnManager.getPlayersOrder().stream().map(Player::getNickname).toList()),
                colorMap,
                foodMap,
                prestigeMap,
                new ArrayList<>(board.getTopRow()),
                new ArrayList<>(board.getTopBuildings()),
                new ArrayList<>(board.getBottomRow()),
                new ArrayList<>(board.getBottomBuildings()),
                isActive,
                effects,
                topDrawNum,
                bottomDrawNum,
                currentDeckSize
        );
    }


    private static class PlayerState {
        private final Player player;
        private boolean canSkip;
        private boolean isActive;
        private int food;
        private int prestige;
        private int topDraw;
        private int bottomDraw;

        PlayerState(Player p) {
            this.player = p;
            this.canSkip = false;
            this.isActive = false;
            this.food = 0;
            this.prestige = 0;
            this.topDraw = 0;
            this.bottomDraw = 0;
        }

        /**
         * Checks if the player could skip an action.
         *
         * @return true if they can skip, false otherwise.
         */
        public boolean canSkip() {
            return canSkip;
        }

        /**
         * Checks if the player is currently active.
         *
         * @return true if active, false otherwise.
         */
        public boolean isActive() {
            return isActive;
        }

        /**
         * Retrieves the player entity associated with this state.
         *
         * @return the player object.
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Another getter for checking if the player can skip an action.
         *
         * @return true if they can skip, false otherwise.
         */
        public boolean isCanSkip() {
            return canSkip;
        }

        /**
         * Sets whether the player can skip an action.
         *
         * @param canSkip true to allow skipping, false to deny.
         */
        public void setCanSkip(boolean canSkip) {
            this.canSkip = canSkip;
        }

        /**
         * Sets whether the player is currently active.
         *
         * @param active true if the player becomes active, false otherwise.
         */
        public void setActive(boolean active) {
            isActive = active;
        }

        /**
         * Retrieves the player's food tokens in this state.
         *
         * @return the number of food tokens.
         */
        public int getFood() {
            return food;
        }

        /**
         * Sets the player's food tokens in this state.
         *
         * @param food the number of food tokens.
         */
        public void setFood(int food) {
            this.food = food;
        }

        /**
         * Retrieves the player's prestige tokens in this state.
         *
         * @return the number of prestige tokens.
         */
        public int getPrestige() {
            return prestige;
        }

        /**
         * Sets the player's prestige tokens in this state.
         *
         * @param prestige the number of prestige tokens.
         */
        public void setPrestige(int prestige) {
            this.prestige = prestige;
        }

        /**
         * Retrieves the number of available top draws for the player.
         *
         * @return the top draw count.
         */
        public int getTopDraw() {
            return topDraw;
        }

        /**
         * Sets the number of available top draws for the player.
         *
         * @param topDraw the top draw count to set.
         */
        public void setTopDraw(int topDraw) {
            this.topDraw = topDraw;
        }

        /**
         * Retrieves the number of available bottom draws for the player.
         *
         * @return the bottom draw count.
         */
        public int getBottomDraw() {
            return bottomDraw;
        }

        /**
         * Sets the number of available bottom draws for the player.
         *
         * @param bottomDraw the bottom draw count to set.
         */
        public void setBottomDraw(int bottomDraw) {
            this.bottomDraw = bottomDraw;
        }
    }
}