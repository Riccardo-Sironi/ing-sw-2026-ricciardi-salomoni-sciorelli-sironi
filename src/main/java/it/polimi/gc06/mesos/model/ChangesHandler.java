package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import it.polimi.gc06.mesos.network.socket.infos.ResourcesInfo;
import it.polimi.gc06.mesos.network.socket.infos.SkipRightInfo;
import it.polimi.gc06.mesos.view.PropertyChangeName;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ChangesHandler {

    private int lastRound;
    private String lastPhase;
    private Era lastEra;
    private final ArrayList<PlayerState> lastPlayersState;
    private final GameModel model;
    private final TurnManager turnManager;
    private final Board board;
    
    public ChangesHandler(GameModel model){
        this.model = model;
        this.turnManager = model.getTurnManager();
        this.board = model.getBoard();
        this.lastPhase = null;
        this.lastRound = -1;
        this.lastEra = null;
        
        this.lastPlayersState = new ArrayList<>();
        for(Player p : model.getPlayers()) {
            lastPlayersState.add(new PlayerState(p));
        }
    }

    /**
     * Returns all changes from last state registration.
     *
     * @return all the {@link PropertyChangeEvent} reassuming the changes.
     */
    public ArrayList<PropertyChangeEvent> getChanges(){

        ArrayList<PropertyChangeEvent> changes = new ArrayList<>();

        if(board.isEndGame()){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.IS_END_GAME.name(), null, model.getLeaderboard()));
            return changes;
        }

        //check resources & prestige diff
        for(Player p : model.getPlayers()) {
            PlayerState ps = lastPlayersState.stream().filter(s -> s.getPlayer().getNickname().equals(p.getNickname()))
                    .findFirst().orElseThrow(IllegalStateException::new);
            int diff = p.getFoodTokens() - ps.getFood();
            if(diff != 0) changes.add(new PropertyChangeEvent(model,PropertyChangeName.FOOD_CHANGED.name(),
                    0, diff));
            diff = p.getTopDrawNum() - ps.getTopDraw();
            if(diff != 0) changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_NUM_DRAW_CHANGED.name(),
                    0, diff));
            diff = p.getBottomDrawNum() - ps.getBottomDraw();
            if(diff != 0) changes.add(new PropertyChangeEvent(model,PropertyChangeName.BOTTOM_NUM_DRAW_CHANGED.name(),
                    0, diff));
            diff = p.getPrestigeTokens() - ps.getPrestige();
            if(diff != 0) changes.add(new PropertyChangeEvent(model,PropertyChangeName.PRESTIGE_CHANGED.name(),
                    0, diff));
        }

        //checks if the lastPlayerWhoCouldSkip can still skip
        if(getPlayerWhoCouldSkip() != null) try{
            if(!turnManager.getPhase().checkForRightToSkip(getPlayerWhoCouldSkip(),board)){
                changes.add(new PropertyChangeEvent(model,PropertyChangeName.PLAYER_CAN_SKIP.name(), null,
                        new SkipRightInfo(getPlayerWhoCouldSkip().getNickname(),false)));
            }
        }catch (IllegalPhaseActionException _){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.PLAYER_CAN_SKIP.name(), null,
                    new SkipRightInfo(getPlayerWhoCouldSkip().getNickname(),false)));
        }
        
        //check if the round has changed
        if(lastRound != turnManager.getRound()){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ROUND_CHANGED.name(),
                    0,turnManager.getRound()));
        }
        
        //checks last era
        if(lastEra == null || !lastEra.equals(board.getCurrentEra())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ERA_CHANGED.name(),
                    null,board.getCurrentEra()));
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_ROW_REFILL.name(),
                    null,board.getTopRow()));
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_BUILDINGS_REFILL.name(),
                    null,board.getTopBuildings()));
        }
        
        //check last phase
        if(lastPhase == null ||!lastPhase.equals(turnManager.getPhase().toString())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.PHASE_CHANGED.name(),
                    null,turnManager.getPhase().toString()));
        }
        
        //check active player change and right to skip of the new player
        if(getLastActivePlayer() == null || !getLastActivePlayer().getNickname().equals(turnManager.getActivePlayer().getNickname())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ACTIVE_PLAYER_CHANGED.name(),
                    null,turnManager.getActivePlayer().getNickname()));
            try {
                if(turnManager.getPhase().checkForRightToSkip(
                        turnManager.getActivePlayer(),board
                )){
                    changes.add(new PropertyChangeEvent(model, PropertyChangeName.PLAYER_CAN_SKIP.name(), null,
                            new SkipRightInfo(turnManager.getActivePlayer().getNickname(), true)));
                }
            }catch (IllegalPhaseActionException _){}
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

        //saves players last states
        //saves players resources & prestige
        for(Player p : model.getPlayers()){
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
        try{
            if(turnManager.getPhase().checkForRightToSkip(
                    turnManager.getActivePlayer(),board)){
                PlayerState ps = lastPlayersState.stream().filter(s -> s.player.getNickname()
                        .equals(turnManager.getActivePlayer().getNickname())).findFirst().orElseThrow(IllegalStateException::new);
                ps.setCanSkip(true);
            }
        } catch (IllegalPhaseActionException _) {}

        PlayerState ps = lastPlayersState.stream().filter(s -> s.player.getNickname()
                .equals(turnManager.getActivePlayer().getNickname())).findFirst().orElseThrow(IllegalStateException::new);
        ps.setActive(true);
    }

    private Player getPlayerWhoCouldSkip(){
        return lastPlayersState.stream().filter(PlayerState::canSkip).map(PlayerState::getPlayer).findFirst().orElse(null);
    }
    
    private Player getLastActivePlayer(){
        return lastPlayersState.stream().filter(PlayerState::isActive).map(PlayerState::getPlayer).findFirst().orElse(null);
    }
    
    /**
     * Returns the current game state expressed in {@link PropertyChangeEvent}
     * Should be used only when tha game is just started.
     *
     * @return an {@link ArrayList} containing the changes from an empty model.
     */
    public ArrayList<PropertyChangeEvent> getStartingStateAsChanges(){
        ArrayList<PropertyChangeEvent> changes = new ArrayList<>();

        changes.add(new PropertyChangeEvent(model,PropertyChangeName.ERA_CHANGED.name(),
                null,board.getCurrentEra()));
        changes.add(new PropertyChangeEvent(model,PropertyChangeName.ROUND_CHANGED.name(),
                0,turnManager.getRound()));
        changes.add(new PropertyChangeEvent(model,PropertyChangeName.PHASE_CHANGED.name(),
                null,turnManager.getPhase().toString()));
        changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_ROW_REFILL.name(),
                null,board.getTopRow()));
        changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_BUILDINGS_REFILL.name(),
                null,board.getTopBuildings()));

        //each player notification
        for(Player p : model.getPlayers()){
            changes.add(new PropertyChangeEvent(model, PropertyChangeName.FOOD_CHANGED.name(), null,
                    new ResourcesInfo(p.getNickname(),p.getFoodTokens())));
            changes.add(new PropertyChangeEvent(model, PropertyChangeName.TOTEM_PLACEMENT_TURN.name(), null,
                    turnManager.getPlayersOrder().indexOf(p)));
        }

        return changes;
    }

    
    private static class PlayerState{
        private final Player player;
        private boolean canSkip;
        private boolean isActive;
        private int food;
        private int prestige;
        private int topDraw;
        private int bottomDraw;

        PlayerState(Player p){
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
