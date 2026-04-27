package it.polimi.gc06.mesos.network.leaderboard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard implements Comparable<Leaderboard>{

    private Timestamp timestamp;
    private int numOfPlayers;
    private int ID;
    private final List<Score> scores;

    public Leaderboard(){
        timestamp = null;
        numOfPlayers = -1;
        ID = -1;
        scores = new ArrayList<>();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Adds a score to the current leaderboard.
     *
     * @param score the score object for the player
     */
    public void addScore(Score score){
        scores.add(score);
    }

    /**
     * Scores getter.
     *
     * @return an immutable list that represent the scores.
     */
    public List<Score> getScores(){
        return Collections.unmodifiableList(scores);
    }

    @Override
    public int compareTo(Leaderboard l) {
        return l.getTimestamp().compareTo(this.timestamp);
    }
}
