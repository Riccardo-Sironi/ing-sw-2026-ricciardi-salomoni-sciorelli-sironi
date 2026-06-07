package it.polimi.gc06.mesos.network.leaderboard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard implements Comparable<Leaderboard> {

    private Timestamp timestamp;
    private final List<Score> scores;

    public Leaderboard() {
        timestamp = null;
        scores = new ArrayList<>();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getNumOfPlayers() {
        return scores.size();
    }

    /**
     * Adds a score to the current leaderboard.
     *
     * @param score the score object for the player
     */
    public void addScore(Score score) {
        scores.add(score);
    }

    /**
     * Scores getter, they are already ordered.
     *
     * @return an immutable list that represent the scores.
     */
    public List<Score> getScores() {
        scores.sort((Score s1, Score s2) -> s2.getPrestigeScore() - s1.getPrestigeScore() == 0 ?
                s2.getFoodScore() - s1.getFoodScore() : s2.getPrestigeScore() - s1.getPrestigeScore());
        return Collections.unmodifiableList(scores);
    }

    @Override
    public int compareTo(Leaderboard l) {
        return l.getTimestamp().compareTo(this.timestamp);
    }
}
