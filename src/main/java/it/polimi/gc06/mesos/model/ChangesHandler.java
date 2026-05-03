package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.view.PropertyChangeName;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ChangesHandler {

    private int lastRound;
    private String lastPhase;
    private String lastActivePlayer;
    private Era lastEra;
    private final GameModel model;

    public ChangesHandler(GameModel model){
        this.model = model;
        this.lastActivePlayer = null;
        this.lastPhase = null;
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

        if(model.getBoard().isEndGame()){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.IS_END_GAME.name(), null, model.getLeaderboard()));
            return changes;
        }

        if(lastRound != model.getTurnManager().getRound()){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ROUND_CHANGED.name(),
                    0,model.getTurnManager().getRound()));
        }
        if(!lastEra.equals(model.getBoard().getCurrentEra())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ERA_CHANGED.name(),
                    null,model.getBoard().getCurrentEra()));
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_ROW_REFILL.name(),
                    null,model.getBoard().getTopRow()));
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.TOP_BUILDINGS_REFILL.name(),
                    null,model.getBoard().getTopBuildings()));
        }
        if(!lastPhase.equals(model.getTurnManager().getPhase().toString())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.PHASE_CHANGED.name(),
                    null,model.getTurnManager().getPhase().toString()));
        }
        if(!lastActivePlayer.equals(model.getTurnManager().getActivePlayer().getNickname())){
            changes.add(new PropertyChangeEvent(model,PropertyChangeName.ACTIVE_PLAYER_CHANGED.name(),
                    null,model.getTurnManager().getActivePlayer().getNickname()));
        }
        return changes;
    }

    /**
     * Internally saves the state to prepare next getChanges()
     */
    public void registerState() {
        lastRound = model.getTurnManager().getRound();
        lastPhase = model.getTurnManager().getPhase().toString();
        lastActivePlayer = model.getTurnManager().getActivePlayer().getNickname();
        lastEra = model.getBoard().getCurrentEra();
    }

}
