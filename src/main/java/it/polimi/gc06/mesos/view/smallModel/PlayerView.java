package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardTypifiedVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlayerView {

    //state
    private String nickname;
    private Color color;
    private int numFood;
    private int numPrestige;
    private final ArrayList<Card> characters;
    private final ArrayList<Card> buildings;

    public PlayerView(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.numFood = 0;
        this.numPrestige = 0;
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumFood() {
        return numFood;
    }

    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }

    public int getNumPrestige() {
        return numPrestige;
    }

    public void setNumPrestige(int numPrestige) {
        this.numPrestige = numPrestige;
    }

    public ArrayList<Card> getCharacters() {
        return characters;
    }

    public ArrayList<Card> getBuildings() {
        return buildings;
    }

    public int getNumShamanStar() {

        CardTypifiedVisitor<Integer> shamanVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(ShamanCard card) {
                setResult(getResult() + card.getStars());
            }
        };
        shamanVisitor.setResult(0);
        characters.forEach(c -> c.accept(shamanVisitor));

        return shamanVisitor.getResult();
    }

    public int getNumGatherer() {
        CardTypifiedVisitor<Integer> gatherVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(GathererCard card) {
                setResult(getResult() + 1);
            }
        };
        gatherVisitor.setResult(0);
        characters.forEach(c -> c.accept(gatherVisitor));

        return gatherVisitor.getResult();
    }

    public int getNumHunter() {
        CardTypifiedVisitor<Integer> hunterVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(HunterCard card) {
                setResult(getResult() + 1);
            }
        };
        hunterVisitor.setResult(0);
        characters.forEach(c -> c.accept(hunterVisitor));

        return hunterVisitor.getResult();
    }

    public int getNumArtist() {
        CardTypifiedVisitor<Integer> artistVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(ArtistCard card) {
                setResult(getResult() + 1);
            }
        };
        artistVisitor.setResult(0);
        characters.forEach(c -> c.accept(artistVisitor));

        return artistVisitor.getResult();
    }

    public int getBuildersDiscount() {
        CardTypifiedVisitor<Integer> builderVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(BuilderCard card) {
                setResult(getResult() + card.getFoodDiscount());
            }
        };
        builderVisitor.setResult(0);
        characters.forEach(c -> c.accept(builderVisitor));

        return builderVisitor.getResult();
    }

    public int getBuildersPrestige() {
        CardTypifiedVisitor<Integer> builderVisitor = new CardTypifiedVisitor<Integer>(){
            @Override
            public void visit(BuilderCard card) {
                setResult(getResult() + card.getPrestige());
            }
        };
        builderVisitor.setResult(0);
        characters.forEach(c -> c.accept(builderVisitor));

        return builderVisitor.getResult();
    }

    public Set<InventionIcon> getCollectedIcons() {
        CardTypifiedVisitor<Set<InventionIcon>> inventorVisitor = new CardTypifiedVisitor<Set<InventionIcon>>(){
            @Override
            public void visit(InventorCard card) {
                getResult().add(card.getIcon());
            }
        };
        inventorVisitor.setResult(new HashSet<>());
        characters.forEach(c -> c.accept(inventorVisitor));

        return inventorVisitor.getResult();
    }
}
