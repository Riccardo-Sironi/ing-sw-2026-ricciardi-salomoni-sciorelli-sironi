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
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

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
     *
     * @return all the {@link PropertyChangeEvent} reassuming the changes.
     */
    public ArrayList<SmallModelEditor> getChanges() {

        ArrayList<SmallModelEditor> changes = new ArrayList<>();

        if (board.isEndGame()) {
            changes.add(new GameStateChangeDTO(model.getLeaderboard().getScores()));
            return changes;
        }

        //check resources & prestige diff
        for (Player p : model.getPlayers()) {
            PlayerState ps = lastPlayersState.stream().filter(s -> s.getPlayer().getNickname().equals(p.getNickname()))
                    .findFirst().orElseThrow(IllegalStateException::new);
            changes.add(new PlayerResourcesChangeDTO(
                    ps.player.getNickname(), p.getTopDrawNum() - ps.getTopDraw(), p.getBottomDrawNum() - ps.getBottomDraw(),
                    p.getFoodTokens() - ps.getFood(), p.getPrestigeTokens() - ps.getPrestige()
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

        //check if the round has changed
        if (lastRound != turnManager.getRound()) {
            changes.add(new GameStateChangeDTO(turnManager.getRound()));
        }

        //checks last era
        if (lastEra == null || !lastEra.equals(board.getCurrentEra())) {
            changes.add(new GameStateChangeDTO(board.getCurrentEra()));
            changes.add(new TopRowRefillDTO(new ArrayList<>(board.getTopRow())));
            changes.add(new BuildingsRefillDTO(new ArrayList<>(board.getTopBuildings())));
        }

        //check last phase
        if (lastPhase == null || !lastPhase.equals(turnManager.getPhase().toString())) {
            changes.add(new GameStateChangeDTO(turnManager.getPhase().toString()));
        }

        //check active player change and right to skip of the new player
        if (getLastActivePlayer() == null || !getLastActivePlayer().getNickname().equals(turnManager.getActivePlayer().getNickname())) {
            PlayerStateChangeDTO dto = new PlayerStateChangeDTO(turnManager.getActivePlayer().getNickname());
            dto.setIsActive(true);
            changes.add(dto);
            try {
                if (turnManager.getPhase().checkForRightToSkip(
                        turnManager.getActivePlayer(), board
                )) {
                    dto = new PlayerStateChangeDTO(turnManager.getActivePlayer().getNickname());
                    dto.setCanSkip(true);
                    changes.add(dto);
                }
            } catch (IllegalPhaseActionException _) {
            }
        }
        return changes;
    }

    /**
     * Internally saves the state to prepare next getChanges()
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

    private Player getPlayerWhoCouldSkip() {
        return lastPlayersState.stream().filter(PlayerState::canSkip).map(PlayerState::getPlayer).findFirst().orElse(null);
    }

    private Player getLastActivePlayer() {
        return lastPlayersState.stream().filter(PlayerState::isActive).map(PlayerState::getPlayer).findFirst().orElse(null);
    }

    /**
     * Returns the current game state expressed in {@link SmallModelEditor}
     * Should be used only when the game is just started.
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

        return new GameStartedDTO(
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

        public boolean canSkip() {
            return canSkip;
        }

        public boolean isActive() {
            return isActive;
        }

        public Player getPlayer() {
            return player;
        }

        public boolean isCanSkip() {
            return canSkip;
        }

        public void setCanSkip(boolean canSkip) {
            this.canSkip = canSkip;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public int getFood() {
            return food;
        }

        public void setFood(int food) {
            this.food = food;
        }

        public int getPrestige() {
            return prestige;
        }

        public void setPrestige(int prestige) {
            this.prestige = prestige;
        }

        public int getTopDraw() {
            return topDraw;
        }

        public void setTopDraw(int topDraw) {
            this.topDraw = topDraw;
        }

        public int getBottomDraw() {
            return bottomDraw;
        }

        public void setBottomDraw(int bottomDraw) {
            this.bottomDraw = bottomDraw;
        }
    }
}
