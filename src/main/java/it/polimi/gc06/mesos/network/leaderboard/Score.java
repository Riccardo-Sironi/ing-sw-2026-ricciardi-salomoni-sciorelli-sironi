package it.polimi.gc06.mesos.network.leaderboard;

public class Score {

    private String nickname;
    private int score;
    private int LeaderboardID;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLeaderboardID() {
        return LeaderboardID;
    }

    public void setLeaderboardID(int leaderboardID) {
        LeaderboardID = leaderboardID;
    }
}
