package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import it.polimi.gc06.mesos.network.socket.infos.SkipRightInfo;
import it.polimi.gc06.mesos.view.PropertyChangeName;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChangesHandler {

    private int lastRound;
    private String lastPhase;
    private String lastActivePlayer;
    private Era lastEra;
    private Player playerWhoCouldSkip;
    private final GameModel model;
    private final TurnManager turnManager;
    private final Board board;
    
    public ChangesHandler(GameModel model){
        this.model = model;
        this.turnManager = model.getTurnManager();
        this.board = model.getBoard();
        this.lastActivePlayer = null;
        this.lastPhase = null;
        this.playerWhoCouldSkip = null;
        this.lastRound = -1;
        this.lastEra = null;
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
        
        //checks if the lastPlayerWhoCouldSkip can still skip
        if(playerWhoCouldSkip != null) try{
            if(!turnManager.getPhase().checkForRightToSkip(playerWhoCouldSkip,board)){
                changes.add(new PropertyChangeEvent(model,PropertyChangeName.PLAYER_CAN_SKIP.name(), null, 
                        new SkipRightInfo(playerWhoCouldSkip.getNickname(),false)));
            }
        }catch (IllegalPhaseActionException _){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.PLAYER_CAN_SKIP.name(), null,
                    new SkipRightInfo(playerWhoCouldSkip.getNickname(),false)));
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
        if(lastActivePlayer == null || !lastActivePlayer.equals(turnManager.getActivePlayer().getNickname())){
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
        lastActivePlayer = turnManager.getActivePlayer().getNickname();
        lastEra = board.getCurrentEra();
        try{
            if(turnManager.getPhase().checkForRightToSkip(
                    turnManager.getActivePlayer(),board)){
                playerWhoCouldSkip = turnManager.getActivePlayer();
            }
        } catch (IllegalPhaseActionException _) {}
    }

}
