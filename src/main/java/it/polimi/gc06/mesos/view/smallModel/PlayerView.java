package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;

import java.util.*;

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

    //recap info
    private final Map<CharacterType, Integer> tribeRecap;
    private int buildersDiscount;
    private int shamanStar;
    private final Set<InventionIcon> collectedIcons;

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
        this.tribeRecap = new HashMap<>();
        Arrays.asList(CharacterType.values()).forEach(t -> tribeRecap.put(t, 0)); //sets all characters recap to zero
        this.collectedIcons = new HashSet<>();
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
     * Calculates the total number of Artist cards owned by the player.
     *
     * @return the count of Artists
     */
    public int getNumArtist() {
        return tribeRecap.get(CharacterType.ARTIST);
    }

    public void setArtistNumber(int artistNumber) {
        tribeRecap.put(CharacterType.ARTIST, artistNumber);
    }

    public int getNumBuilders() {
        return tribeRecap.get(CharacterType.BUILDER);
    }

    public void setBuilderNumber(int builderNumber) {
        tribeRecap.put(CharacterType.BUILDER, builderNumber);
    }

    public int getBuildersDiscount() {
        return buildersDiscount;
    }

    public void setBuildersDiscount(int buildersDiscount) {
        this.buildersDiscount = buildersDiscount;
    }

    /**
     * Calculates the total number of Gatherer cards owned by the player.
     *
     * @return the count of Gatherers
     */
    public int getNumGatherer() {
        return tribeRecap.get(CharacterType.GATHERER);
    }

    public void setGathererNumber(int gathererNumber) {
        tribeRecap.put(CharacterType.GATHERER, gathererNumber);
    }

    /**
     * Calculates the total number of Hunter cards owned by the player.
     *
     * @return the count of Hunters
     */
    public int getNumHunter() {
        return tribeRecap.get(CharacterType.HUNTER);
    }

    public void setHunterNumber(int hunterNumber) {
        tribeRecap.put(CharacterType.HUNTER, hunterNumber);
    }

    /**
     * Calculates the total number of Inventor cards owned by the player.
     *
     * @return the count of Inventors
     */
    public int getNumInventor() {
        return tribeRecap.get(CharacterType.INVENTOR);
    }

    public void setInventorNumber(int inventorNumber) {
        tribeRecap.put(CharacterType.INVENTOR, inventorNumber);
    }

    /**
     * Collects all the distinct Invention icons present on the Inventor cards owned by the player.
     *
     * @return a Set containing all unique collected invention icons
     */
    public Set<InventionIcon> getCollectedIcons() {
        return collectedIcons;
    }

    public void setCollectedIcons(Set<InventionIcon> collectedIcons) {
        this.collectedIcons.clear();
        this.collectedIcons.addAll(collectedIcons);
    }

    /**
     * Calculates the total number of Shaman cards owned by the player.
     *
     * @return the count of Shamans
     */
    public int getNumShaman() {
        return tribeRecap.get(CharacterType.SHAMAN);
    }

    public void setShamanNumber(int shamanNumber) {
        tribeRecap.put(CharacterType.SHAMAN, shamanNumber);
    }

    /**
     * Calculates the total sum of stars provided by all Shaman cards owned by the player.
     *
     * @return the total number of Shaman stars
     */
    public int getNumShamanStar() {
        return shamanStar;
    }

    public void setShamanStar(int shamanStar) {
        this.shamanStar = shamanStar;
    }
}
