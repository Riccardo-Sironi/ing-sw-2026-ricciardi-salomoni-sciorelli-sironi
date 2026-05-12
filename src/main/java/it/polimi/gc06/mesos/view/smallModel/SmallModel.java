package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.network.leaderboard.Score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SmallModel {

    //cards
    private final ArrayList<Card> topRow;
    private final ArrayList<Card> bottomRow;
    private final ArrayList<Card> topBuildings;
    private final ArrayList<Card> bottomBuildings;
    private final ArrayList<PlayerView> turnOrderTile;
    private final ArrayList<TileSlotView> offerTrack;

    //state
    private Era era;
    private String phase;
    private int tribeDeckSize;
    private int round;
    private int topDrawNum;
    private int bottomDrawNum;
    private boolean isActive;
    private boolean canSkip;

    //context
    private PlayerView player;
    private final List<PlayerView> opponents; //is immutable
    private final ArrayList<Score> leaderboard;

    public SmallModel(String nickname) {
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildings = new ArrayList<>();
        this.bottomBuildings = new ArrayList<>();
        this.turnOrderTile = new ArrayList<>();
        this.offerTrack = new ArrayList<>();
        this.era = null;
        this.phase = null;
        this.tribeDeckSize = -1;
        this.round = -1;
        this.topDrawNum = -1;
        this.bottomDrawNum = -1;
        this.player = null;
        this.opponents = new ArrayList<>();
        this.isActive = false;
        this.canSkip = false;
        this.leaderboard = new ArrayList<>();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEndgame() {
        return !leaderboard.isEmpty();
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

    public int getTopDrawNum() {
        return topDrawNum;
    }

    public void setTopDrawNum(int topDrawNum) {
        this.topDrawNum = topDrawNum;
    }

    public int getBottomDrawNum() {
        return bottomDrawNum;
    }

    public void setBottomDrawNum(int bottomDrawNum) {
        this.bottomDrawNum = bottomDrawNum;
    }

    public void setPlayer(String nickname, Color color) {
        this.player = new PlayerView(nickname,color);
    }

    public PlayerView getPlayer(){
        return player;
    }

    public void addOpponent(PlayerView pv){
        opponents.add(pv);
    }

    public List<PlayerView> getOpponents() {
        return opponents;
    }

    public ArrayList<Card> getTopRow() {
        return topRow;
    }

    public ArrayList<Card> getBottomRow() {
        return bottomRow;
    }

    public ArrayList<Card> getTopBuildings() {
        return topBuildings;
    }

    public ArrayList<Card> getBottomBuildings() {
        return bottomBuildings;
    }

    public ArrayList<PlayerView> getTurnOrderTile() {
        return turnOrderTile;
    }

    public ArrayList<TileSlotView> getOfferTrack() {
        return offerTrack;
    }

    public void setLeaderboard(Collection<Score> leaderboard) {
        this.leaderboard.addAll(leaderboard);
    }

    public ArrayList<Score> getLeaderboard() {
        return leaderboard;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

}
