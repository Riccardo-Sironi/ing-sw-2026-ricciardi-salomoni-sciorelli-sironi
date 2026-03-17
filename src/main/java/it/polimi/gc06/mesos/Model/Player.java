package it.polimi.gc06.mesos.Model;

import it.polimi.gc06.mesos.Model.Cards.Buildings.BuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ObserverSetBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Characters.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;

public class Player {

    private final String nickname;
    private int prestigeTokens;
    private int foodTokens;

    private int shamanStars;

    // Just like in real life, the player is able to know the state of the game.
    // GameInfo must be transient in order to avoid serialization issues.
    private transient GameInfo gameInfo;

    // TODO Vedere se riusciamo ad aggirare
    private final EnumMap<CharacterType, ArrayList<CharacterCard>> characterDeck;
    private final ArrayList<BuildingCard> buildingDeck;

    private EnumMap<CharacterType, Integer> charactersSets;
    private EnumMap<InventionIcon, Integer> inventorPairs;

    private final ModifierBuildingCard threeStarCard;

    private final DeckCardVisitor deckCardVisitor;
    private final CharactersSetsVisitor charactersSetsVisitor;
    private final InventorPairsVisitor inventorPairsVisitor;
    private final ShamanVisitor shamanVisitor;

    // TODO Rimuovere colore e gestire esternamente - Usiamo ENUM
    private final Color color;

    // TODO Attenzione a non duplicare nel controller - vedrei se si può completamente spostare
    private int topDrawNum;
    private int bottomDrawNum;

    //CONSTRUCTOR
    public Player(String nickname, Color color, ModifierBuildingCard threeStarCard) {
        this.nickname = nickname;
        this.threeStarCard = threeStarCard;
        this.prestigeTokens = 0;
        this.foodTokens = 0;
        this.characterDeck = new EnumMap<>(CharacterType.class);
        this.buildingDeck = new ArrayList<>();
        this.color = (color != null) ? color : Color.BLACK; //DA CAPIRE

        for (CharacterType cType : CharacterType.values()) {
            characterDeck.put(cType, new ArrayList<>());
        }

        this.charactersSets = null;
        this.inventorPairs = null;

        this.shamanStars = 0;

        this.topDrawNum = 0;
        this.bottomDrawNum = 0;

        deckCardVisitor = new DeckCardVisitor(this);
        charactersSetsVisitor = new CharactersSetsVisitor(this);
        inventorPairsVisitor = new InventorPairsVisitor(this);
        shamanVisitor = new ShamanVisitor(this);
    }

    //NICKNAME
    protected String getNickname() {
        return this.nickname;
    }

    //CHARACTER CARDS
    public EnumMap<CharacterType, ArrayList<CharacterCard>> getCharacterDeck() {
        return this.characterDeck;
    }

    protected void addCharacterCards(CharacterCard card) {
        if (card == null) throw new IllegalArgumentException("Card cannot be null");

        card.accept(deckCardVisitor);

        card.accept(shamanVisitor);

        if (charactersSets != null) {
            card.accept(charactersSetsVisitor);
        }
        if (inventorPairs != null) {
            card.accept(inventorPairsVisitor);
        }
    }

    //BUILDING CARDS
    public ArrayList<BuildingCard> getBuildingCards() {
        return this.buildingDeck;
    }

    protected void addBuildingCards(BuildingCard card) {
        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
    }

    protected void addBuildingCards(ObserverSetBuildingCard card) {

        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
        if (charactersSets == null) {

            initCharactersSets();
        }
    }

    protected void addBuildingCards(ObserverPairBuildingCard card) {

        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
        if (inventorPairs == null) {
            initInventorPairs();
        }
    }

    public boolean hasSetBuildingCard() {
        return charactersSets != null;
    }

    public boolean hasPairBuildingCard() {
        return inventorPairs != null;
    }

    //FOOD TOKEN CARDS
    public int getFoodTokens() {
        return this.foodTokens;
    }

    public void addFoodTokens(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.foodTokens += amount;
    }

    /*remove food tokens only if the player has them. otherwise is thrown an exception*/
    public void removeFoodTokens(int amount) throws IllegalStateException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        if ((this.foodTokens - amount) >= 0) {
            this.foodTokens -= amount;
        } else {
            throw new IllegalStateException();
        }
    }

    //PRESTIGE TOKEN CARDS
    protected int getPrestigeTokens() {
        return this.prestigeTokens;
    }

    public void addPrestigeTokens(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.prestigeTokens += amount;
    }

    public void removePrestigeTokens(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.prestigeTokens -= amount;
    }


    //COLOR
    protected Color getColor() {
        return this.color;
    }

    //COUNTERS

    //shaman stars
    public int getShamanStars() {
        return this.shamanStars + (buildingDeck.contains(threeStarCard) ? 3 : 0);
    }

    public void increaseShamanStars(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.shamanStars += amount;
    }


    //hunters

    public int getHuntersCounter() {
        return this.characterDeck.get(CharacterType.HUNTER).size();
    }

    //artists
    public int getArtistsCounter() {
        return this.characterDeck.get(CharacterType.ARTIST).size();
    }

    //gatherer
    public int getGatherersCounter() {
        return this.characterDeck.get(CharacterType.GATHERER).size();
    }

    //TOP DRAW NUM
    protected int getTopDrawNum() {
        return topDrawNum;
    }

    public void setTopDrawNum(int topDrawNum) throws IllegalArgumentException {
        if (topDrawNum < 0) throw new IllegalArgumentException();
        this.topDrawNum = topDrawNum;
    }

    //BOTTOM DRAW NUM
    protected int getBottomDrawNum() {
        return bottomDrawNum;
    }

    public void setBottomDrawNum(int bottomDrawNum) throws IllegalArgumentException {
        if (bottomDrawNum < 0) throw new IllegalArgumentException();
        this.bottomDrawNum = bottomDrawNum;
    }

    public void setEnvironment(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public GameInfo getEnvironment() {
        if (this.gameInfo == null) {
            throw new IllegalStateException("Game Environment not available for player " + this.nickname);
        }
        return this.gameInfo;
    }

    // CHARACTER SETS
    protected void initCharactersSets() throws IllegalStateException {
        if (charactersSets != null) throw new IllegalStateException("Character sets have already been initialized");

        charactersSets = new EnumMap<>(CharacterType.class);

        // get the number of sets of cards already completed by the player, which is the minimum number of cards in each character type deck
        int min = characterDeck.values().stream().mapToInt(ArrayList::size).min().orElse(0);

        // for each character type we put the cards which are in excess of the minimum
        // (so if the player has 3 hunters, 2 artists and 1 gatherer, the minimum is 1 and the map will contain 2 for hunters,
        // 1 for artists and 0 for gatherers)
        for (CharacterType cType : CharacterType.values()) {
            charactersSets.put(cType, characterDeck.get(cType).size() - min);
        }
    }

    public EnumMap<CharacterType, Integer> getCharactersSets() {
        return charactersSets;
    }

    protected void increaseCharactersSets(CharacterType cType) throws IllegalArgumentException, IllegalStateException {
        if (cType == null) throw new IllegalArgumentException("Character type cannot be null");
        if (charactersSets == null) throw new IllegalStateException("Character sets have not been initialized");
        charactersSets.put(cType, charactersSets.get(cType) + 1);
    }

    public void decreaseCharactersSets() throws IllegalArgumentException, IllegalStateException {
        if (charactersSets == null) throw new IllegalStateException("Character sets have not been initialized");
        charactersSets.replaceAll((cType, count) -> count - 1);
    }

    public boolean hasCompletedSet() {
        if (charactersSets == null) throw new IllegalStateException("Character sets have not been initialized");
        return charactersSets.values().stream().min(Integer::compareTo).orElse(0).equals(1);
    }

    // INVENTOR PAIRS
    protected void initInventorPairs() throws IllegalStateException {
        if (inventorPairs != null) throw new IllegalStateException("Inventor pairs have already been initialized");

        inventorPairs = new EnumMap<>(InventionIcon.class);

        for (InventionIcon icon : InventionIcon.values()) {
            for (CharacterCard card : characterDeck.get(CharacterType.INVENTOR)) {
                InventorCard temp = (InventorCard) card;

                // it checks if the icon is already in the map, if it is it sum 1 to the value, otherwise it put 1 as value
                inventorPairs.merge(icon, 1, Integer::sum);
            }
        }
        // it replaces the value of each icon with the remainder of the division by 2,
        // so if the player has an even number of pairs of that icon, the value will be 0 (no spare icon cards),
        // otherwise it will be 1 (so we have a spare icon card that can be used to complete a pair)
        inventorPairs.replaceAll((icon, count) -> count % 2);
    }

    public void increaseInventorPairs(InventionIcon icon) throws IllegalArgumentException {
        if (icon == null) throw new IllegalArgumentException("Icon cannot be null");
        if (inventorPairs == null) throw new IllegalStateException("Inventor pairs have not been initialized");
        inventorPairs.put(icon, inventorPairs.get(icon) + 1);
    }

    public void decreaseInventorPair() throws IllegalArgumentException {
        if (inventorPairs == null) throw new IllegalStateException("Inventor pairs have not been initialized");
        inventorPairs.replaceAll((icon, count) -> count == 2 ? 0 : count);
    }

    public boolean hasCompletedPair() throws IllegalArgumentException, IllegalStateException {
        if (inventorPairs == null) throw new IllegalStateException("Inventor pairs have not been initialized");
        return inventorPairs.values().stream().anyMatch(count -> count == 2);
    }
}
