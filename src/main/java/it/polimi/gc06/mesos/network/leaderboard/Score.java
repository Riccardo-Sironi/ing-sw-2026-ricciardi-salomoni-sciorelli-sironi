package it.polimi.gc06.mesos.network.leaderboard;

import java.io.Serializable;

public class Score implements Serializable{

    private String nickname;
    private int prestigeScore;
    private int foodScore;

    public Score(){
        nickname = null;
        prestigeScore = -1;
        foodScore = -1;
    }

    public Score(String nickname, int prestigeScore, int foodScore){
        this.nickname = nickname;
        this.prestigeScore = prestigeScore;
        this.foodScore = foodScore;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPrestigeScore() {
        return prestigeScore;
    }

    public void setPrestigeScore(int prestigeScore) {
        this.prestigeScore = prestigeScore;
    }

    public int getFoodScore() {
        return foodScore;
    }

    public void setFoodScore(int foodScore) {
        this.foodScore = foodScore;
    }
}
