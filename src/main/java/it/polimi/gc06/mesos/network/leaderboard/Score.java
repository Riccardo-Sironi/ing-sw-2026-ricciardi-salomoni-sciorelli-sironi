package it.polimi.gc06.mesos.network.leaderboard;

public class Score {

    private String nickname;
    private int score;

    public Score(){
        nickname = null;
        score = 0;
    }

    public Score(String nickname, int score){
        this.nickname = nickname;
        this.score = score;
    }

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
}
