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

    //CONSTRUCTOR
    public Player(String nickname, Color color) {
        this.nickname = nickname;
        this.prestigeTokens = 0;
        this.foodTokens = 0;
        this.characterDeck = new ArrayList<>();
        this.buildingDeck = new ArrayList<>();
        this.color = (color != null) ? color : Color.BLACK; //DA CAPIRE
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
}
