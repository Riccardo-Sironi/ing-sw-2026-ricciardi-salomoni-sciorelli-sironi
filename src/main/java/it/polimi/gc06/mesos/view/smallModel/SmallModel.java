package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SmallModel {

    //cards
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> bottomRow;
    private final ArrayList<Card> topBuilding;
    private final ArrayList<Card> bottomBuilding;
    private final ArrayList<PlayerView> turnOrderTile;
    private final ArrayList<TileSlotView> offerTrack;

    //state
    private Era era;
    private int tribeDeckSize;
    private int round;
    private boolean isActive;
    private boolean isEndgame;
    private boolean canSkip;

    //context
    private final PlayerView player;
    private final List<PlayerView> opponents; //is immutable

    public SmallModel(String nickname, Color color, Map<Color, String> opponentsMap) {
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuilding = new ArrayList<>();
        this.bottomBuilding = new ArrayList<>();
        this.turnOrderTile = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.era = null;
        this.tribeDeckSize = -1;
        this.round = -1;
        this.isActive = false;
        this.isEndgame = false;
        this.canSkip = false;
        this.player = new PlayerView(nickname, color);
        this.opponents = opponentsMap.entrySet().stream().map(
                x -> new PlayerView(x.getValue(), x.getKey())
        ).collect(Collectors.toList());
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEndgame() {
        return isEndgame;
    }

    public void setEndgame(boolean endgame) {
        isEndgame = endgame;
    }

    public boolean isCanSkip() {
        return canSkip;
    }

    public void setCanSkip(boolean canSkip) {
        this.canSkip = canSkip;
    }

    public int getTribeDeckSize() {
        return tribeDeckSize;
    }

    public void setTribeDeckSize(int tribeDeckSize) {
        this.tribeDeckSize = tribeDeckSize;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Era getEra() {
        return era;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public PlayerView getPlayer() {
        return player;
    }

    /**
     * Opponents getter.
     *
     * @return an immutable list of the opponents.
     */
    public List<PlayerView> getOpponents() {
        return opponents;
    }

    public ArrayList<Card> getTopRow() {
        return topRow;
    }

    public ArrayList<Card> getBottomRow() {
        return bottomRow;
    }

    public ArrayList<Card> getTopBuilding() {
        return topBuilding;
    }

    public ArrayList<Card> getBottomBuilding() {
        return bottomBuilding;
    }

    public ArrayList<PlayerView> getTurnOrderTile() {
        return turnOrderTile;
    }

    public ArrayList<TileSlotView> getOfferTrack() {
        return offerTrack;
    }
}
