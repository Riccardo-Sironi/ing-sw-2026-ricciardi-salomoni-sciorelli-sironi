package it.polimi.gc06.mesos.Model;

import java.awt.*;
import java.util.ArrayList;

public class Player {

    private final String nickname;
    private int prestigeTokens;
    private int foodTokens;

    private final ArrayList<CharacterCard> characterDeck;
    private final ArrayList<BuildingCard> buildingDeck;

    private final Color color;

    private int shamanStars;
    private int huntersCounter;
    private int artistsCounter;
    private int gatherersCounter;

    private int topDrawNum;
    private int bottomDrawNum;

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

        this.topDrawNum = 0;
        this.bottomDrawNum = 0;
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
        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.characterDeck.add(card);
    }

    //BUILDING CARDS
    protected ArrayList<BuildingCard> getBuildingCards(){
        return this.buildingDeck;
    }

    protected void addBuildingCards(BuildingCard card){
        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
    }

    public int getTopDrawNum() {
        return topDrawNum;
    }

    public void setTopDrawNum(int topDrawNum) throws IllegalArgumentException{
        if(topDrawNum < 0) throw new IllegalArgumentException();
        this.topDrawNum = topDrawNum;
    }

    public int getBottomDrawNum() {
        return bottomDrawNum;
    }

    public void setBottomDrawNum(int bottomDrawNum) throws IllegalArgumentException{
        if(bottomDrawNum < 0) throw new IllegalArgumentException();
        this.bottomDrawNum = bottomDrawNum;
    }

    //FOOD TOKEN CARDS
    protected int getFoodTokens(){
        return this.foodTokens;
    }

    protected void addFoodTokens(int amount){
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.foodTokens += amount;
    }

    /*remove food tokens only if the player has them. otherwise is thrown an exception*/
    protected void removeFoodTokens(int amount) throws IllegalStateException{
        if(amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        if ((this.foodTokens - amount) >= 0) {
            this.foodTokens -= amount;
        } else {
            throw new IllegalStateException();
        }
    }

    //PRESTIGE TOKEN CARDS
    protected int getPrestigeTokens(){
        return this.prestigeTokens;
    }

    protected void addPrestigeTokens(int amount){
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.prestigeTokens += amount;
    }

    protected void removePrestigeTokens(int amount){
        if(amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
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
