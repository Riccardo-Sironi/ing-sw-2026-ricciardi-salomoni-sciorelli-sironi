package it.polimi.gc06.mesos.Model;

import java.awt.*;
import java.util.ArrayList;

public class Player {

    private String nickname;
    private int prestigeTokens;
    private int foodTokens;

    private ArrayList<CharacterCard> characterDeck;
    private ArrayList<BuildingCard> buildingDeck;

    private Color color;

    private int shamanStars;
    private int huntersCounter;
    private int artistsCounter;
    private int gatherersCounter;

    //CONSTRUCTOR
    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.prestigeTokens = 0;
        this.foodTokens = 0;
        this.characterDeck = new ArrayList<>();
        this.buildingDeck = new ArrayList<>();
        this.color = (color != null) ? color : Color.BLACK; //DA CAPIRE

        this.shamanStars = 0;
        this.huntersCounter = 0;
        this.artistsCounter = 0;
        this.gatherersCounter = 0;
    }

    //NICKNAME
    protected String getNickname(){
        return this.nickname;
    }

    //CHARACTER CARDS
    protected ArrayList<CharacterCard> getCharacterDeck(){
        return this.characterDeck;
    }

    protected void addCharacterCards(CharacterCard card){
        this.characterDeck.add(card);
    }

    //BUILDING CARDS
    protected ArrayList<BuildingCard> getBuildingCards(){
        return this.buildingDeck;
    }

    protected void addBuildingCards(BuildingCard card){
        this.buildingDeck.add(card);
    }

    //FOOD TOKEN CARDS
    protected int getFoodTokens(){
        return this.foodTokens;
    }

    protected void addFoodTokens(int amount){
        this.foodTokens += amount;
    }

    /*remove food tokens only if the player has them. otherwise is thrown an excception*/
    protected void removeFoodTokens(int amount) throws IllegalStateException{
        if ((this.foodTokens - amount) >= 0) this.foodTokens -= amount;
        else throw new IllegalStateException();
    }

    //PRESTIGE TOKEN CARDS
    protected int getPrestigeTokens(){
        return this.prestigeTokens;
    }

    protected void addPrestigeTokens(int amount){
        this.prestigeTokens += amount;
    }

    protected void removePrestigeTokens(int amount){
        this.prestigeTokens -= amount;
    }

    //COLOR
    protected Color getColor(){
        return this.color;
    }

    protected int getShamanStars() {
        return this.shamanStars;
    }

     protected int getHuntersCounter() {
        return this.huntersCounter;
    }

    protected int getArtistsCounter() {
        return this.artistsCounter;
    }

    protected int getGatherersCounter() {
        return this.gatherersCounter;
    }

    protected void increaseShamanStars(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.shamanStars  += amount;
    }

    protected void increaseHuntersCounter(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.huntersCounter += amount;
    }

    protected void increaseArtistsCounter(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.artistsCounter += amount;
    }

    protected void increaseGatherersCounter(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.gatherersCounter += amount;
    }
}
