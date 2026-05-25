package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.CardTypifiedVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A lightweight representation of a player's state used by the client-side UI.
 * It contains the nickname, assigned color, current resources, and owned cards.
 * Provides helper methods to compute aggregate stats like characters held, discounts, and points.
 */
public class PlayerView {

    //state
    private String nickname;
    private Color color;
    private int numFood;
    private int numPrestige;
    private final ArrayList<Card> characters;
    private final ArrayList<Card> buildings;

    /**
     * Constructs a new PlayerView instance with initials properties.
     *
     * @param nickname the player's network nickname
     * @param color    the player's assigned color
     */
    public PlayerView(String nickname, Color color) {
        this.nickname = nickname;
        this.color = color;
        this.numFood = 0;
        this.numPrestige = 0;
        this.characters = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    /**
     * Retrieves the network nickname of the player.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the network nickname of the player.
     *
     * @param nickname the player's nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Retrieves the color assigned to the player.
     *
     * @return the player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color assigned to the player.
     *
     * @param color the player's color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Retrieves the current amount of food tokens held by the player.
     *
     * @return the number of food tokens
     */
    public int getNumFood() {
        return numFood;
    }

    /**
     * Sets the current amount of food tokens held by the player.
     *
     * @param numFood the number of food tokens
     */
    public void setNumFood(int numFood) {
        this.numFood = numFood;
    }

    /**
     * Retrieves the current amount of prestige points the player has acquired.
     *
     * @return the number of prestige points
     */
    public int getNumPrestige() {
        return numPrestige;
    }

    /**
     * Sets the current amount of prestige points the player has acquired.
     *
     * @param numPrestige the number of prestige points
     */
    public void setNumPrestige(int numPrestige) {
        this.numPrestige = numPrestige;
    }

    /**
     * Retrieves the list of character cards currently owned by the player.
     *
     * @return the list of character cards
     */
    public ArrayList<Card> getCharacters() {
        return characters;
    }

    /**
     * Retrieves the list of building cards currently owned by the player.
     *
     * @return the list of building cards
     */
    public ArrayList<Card> getBuildings() {
        return buildings;
    }

    /**
     * Calculates the total sum of stars provided by all Shaman cards owned by the player.
     *
     * @return the total number of Shaman stars
     */
    public int getNumShamanStar() {

        CardTypifiedVisitor<Integer> shamanVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(ShamanCard card) {
                setResult(getResult() + card.getStars());
            }
        };
        shamanVisitor.setResult(0);
        characters.forEach(c -> c.accept(shamanVisitor));

        return shamanVisitor.getResult();
    }

    /**
     * Calculates the total number of Gatherer cards owned by the player.
     *
     * @return the count of Gatherers
     */
    public int getNumGatherer() {
        CardTypifiedVisitor<Integer> gatherVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(GathererCard card) {
                setResult(getResult() + 1);
            }
        };
        gatherVisitor.setResult(0);
        characters.forEach(c -> c.accept(gatherVisitor));

        return gatherVisitor.getResult();
    }

    /**
     * Calculates the total number of Shaman cards owned by the player.
     *
     * @return the count of Shamans
     */
    public int getNumShaman() {
        CardTypifiedVisitor<Integer> shamanVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(ShamanCard card) {
                setResult(getResult() + 1);
            }
        };
        shamanVisitor.setResult(0);
        characters.forEach(c -> c.accept(shamanVisitor));

        return shamanVisitor.getResult();
    }

    /**
     * Calculates the total number of Inventor cards owned by the player.
     *
     * @return the count of Inventors
     */
    public int getNumInventor() {
        CardTypifiedVisitor<Integer> shamanVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(InventorCard card) {
                setResult(getResult() + 1);
            }
        };
        shamanVisitor.setResult(0);
        characters.forEach(c -> c.accept(shamanVisitor));

        return shamanVisitor.getResult();
    }

    /**
     * Calculates the total number of Hunter cards owned by the player.
     *
     * @return the count of Hunters
     */
    public int getNumHunter() {
        CardTypifiedVisitor<Integer> hunterVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(HunterCard card) {
                setResult(getResult() + 1);
            }
        };
        hunterVisitor.setResult(0);
        characters.forEach(c -> c.accept(hunterVisitor));

        return hunterVisitor.getResult();
    }

    /**
     * Calculates the total number of Artist cards owned by the player.
     *
     * @return the count of Artists
     */
    public int getNumArtist() {
        CardTypifiedVisitor<Integer> artistVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(ArtistCard card) {
                setResult(getResult() + 1);
            }
        };
        artistVisitor.setResult(0);
        characters.forEach(c -> c.accept(artistVisitor));

        return artistVisitor.getResult();
    }

    /**
     * Calculates the total number of Builder cards owned by the player.
     *
     * @return the count of Builders
     */
    public int getNumBuilders() {
        CardTypifiedVisitor<Integer> builderVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(BuilderCard card) {
                setResult(getResult() + 1);
            }
        };
        builderVisitor.setResult(0);
        characters.forEach(c -> c.accept(builderVisitor));

        return builderVisitor.getResult();
    }

    /**
     * Calculates the total discount on food requirements provided by all owned Builder cards.
     *
     * @return the accumulated food discount from builders
     */
    public int getBuildersDiscount() {
        CardTypifiedVisitor<Integer> builderVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(BuilderCard card) {
                setResult(getResult() + card.getFoodDiscount());
            }
        };
        builderVisitor.setResult(0);
        characters.forEach(c -> c.accept(builderVisitor));

        return builderVisitor.getResult();
    }

    /**
     * Calculates the total prestige points provided statically by all owned Builder cards.
     *
     * @return the accumulated prestige points from builders
     */
    public int getBuildersPrestige() {
        CardTypifiedVisitor<Integer> builderVisitor = new CardTypifiedVisitor<>() {
            @Override
            public void visit(BuilderCard card) {
                setResult(getResult() + card.getPrestige());
            }
        };
        builderVisitor.setResult(0);
        characters.forEach(c -> c.accept(builderVisitor));

        return builderVisitor.getResult();
    }

    /**
     * Collects all the distinct Invention icons present on the Inventor cards owned by the player.
     *
     * @return a Set containing all unique collected invention icons
     */
    public Set<InventionIcon> getCollectedIcons() {
        CardTypifiedVisitor<Set<InventionIcon>> inventorVisitor = new CardTypifiedVisitor<>() {
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
