package it.polimi.gc06.mesos.Model;

import it.polimi.gc06.mesos.Model.Cards.Buildings.BuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Buildings.ObserverSetBuildingCard;
import it.polimi.gc06.mesos.Model.Cards.Characters.*;

import java.util.ArrayList;
import java.util.EnumMap;

public class Player {

    private final String nickname;
    private int prestigeTokens;
    private int foodTokens;

    private int shamanStars;

    private transient GameInfo gameInfo; // GameInfo must be transient in order to avoid serialization issues.

    private final EnumMap<CharacterType, ArrayList<CharacterCard>> characterDeck;
    private final ArrayList<BuildingCard> buildingDeck;

    private EnumMap<CharacterType, Integer> charactersSets;
    private EnumMap<InventionIcon, Integer> inventorPairs;

    private final ModifierBuildingCard threeStarCard;

    private final DeckCardVisitor deckCardVisitor;
    private final CharactersSetsVisitor charactersSetsVisitor;
    private final InventorPairsVisitor inventorPairsVisitor;
    private final ShamanVisitor shamanVisitor;

    private final Color color;

    // TODO Attenzione a non duplicare nel controller - vedrei se si può completamente spostare
    private int topDrawNum;
    private int bottomDrawNum;

    public Player(String nickname, Color color, ModifierBuildingCard threeStarCard) {
        this.nickname = nickname;
        this.threeStarCard = threeStarCard;

        this.characterDeck = new EnumMap<>(CharacterType.class);
        java.util.Arrays.stream(CharacterType.values()).forEach(cType -> characterDeck.put(cType, new ArrayList<>()));

        this.buildingDeck = new ArrayList<>();

        this.color = color;

        this.charactersSets = null;
        this.inventorPairs = null;

        this.prestigeTokens = 0;
        this.foodTokens = 0;

        this.shamanStars = 0;

        this.topDrawNum = 0;
        this.bottomDrawNum = 0;

        deckCardVisitor = new DeckCardVisitor(this);
        charactersSetsVisitor = new CharactersSetsVisitor(this);
        inventorPairsVisitor = new InventorPairsVisitor(this);
        shamanVisitor = new ShamanVisitor(this);
    }

    /**
     * Returns the nickname of the player.
     *
     * @return the string representing the player's nickname
     */
    protected String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the character deck of the player, which is a map that associates each character type
     * with a list of character cards of that type.
     *
     * @return the enum map of the player's character deck arrays
     */
    public EnumMap<CharacterType, ArrayList<CharacterCard>> getCharacterDeck() {
        return this.characterDeck;
    }

    /**
     * Adds a character card to the player's character deck. The card is added to the list corresponding to its character
     * type by using the visitor pattern. It handles the increment of the shaman stars and the update of the character
     * sets and inventor pairs if the player has the corresponding building cards in their building deck.
     *
     * @param card the character card to be added to the player's character deck
     * @throws IllegalArgumentException if the card is null
     */
    protected void addCharacterCards(CharacterCard card) throws IllegalArgumentException {
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

    /**
     * Returns the building deck of the player.
     *
     * @return the array list of the player's building deck
     */
    public ArrayList<BuildingCard> getBuildingCards() {
        return this.buildingDeck;
    }

    /**
     * Adds a building card to the player's building deck.
     *
     * @param card the building card to be added to the player's building deck
     * @throws IllegalArgumentException if the card is null
     */
    protected void addBuildingCards(BuildingCard card) throws IllegalArgumentException {
        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
    }

    /**
     * Adds a set building card to the player's building deck. The overloaded method is called when the player picks the
     * specific building card which takes tracks of character sets completed by the player.
     *
     * @param card the set building card to be added to the player's building deck
     * @throws IllegalArgumentException if the card is null
     */
    protected void addBuildingCards(ObserverSetBuildingCard card) throws IllegalArgumentException {

        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
        if (charactersSets == null) {
            initCharactersSets();
        }
    }

    /**
     * Adds a pair building card to the player's building deck. The overloaded method is called when the player picks the
     * specific building card which takes tracks of inventor pairs completed by the player.
     *
     * @param card the pair building card to be added to the player's building deck
     * @throws IllegalArgumentException if the card is null
     */
    protected void addBuildingCards(ObserverPairBuildingCard card) throws IllegalArgumentException {

        if (card == null) throw new IllegalArgumentException("Card cannot be null");
        this.buildingDeck.add(card);
        if (inventorPairs == null) {
            initInventorPairs();
        }
    }

    /**
     * This method checks if the player has the character sets map initialized, which means that the player has picked
     * the specific building card which takes tracks of character sets completed by the player.
     *
     * @return true if the player has the character sets map initialized, false otherwise
     */
    public boolean hasSetBuildingCard() {
        return charactersSets != null;
    }

    /**
     * This method checks if the player has the inventor pairs map initialized, which means that the player has picked
     * the specific building card which takes tracks of inventor pairs completed by the player.
     *
     * @return true if the player has the inventor pairs map initialized, false otherwise
     */
    public boolean hasPairBuildingCard() {
        return inventorPairs != null;
    }

    /**
     * Returns the number of food tokens the player has.
     *
     * @return the integer representing the number of food tokens the player has
     */
    public int getFoodTokens() {
        return this.foodTokens;
    }

    /**
     * Adds food tokens to the player
     *
     * @param amount the integer value of food tokens to be added to the player
     * @throws IllegalArgumentException if the amount of food tokens to be added is negative
     */
    public void addFoodTokens(int amount) throws IllegalArgumentException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.foodTokens += amount;
    }

    /**
     * Removes food tokens from the player if the player has enough food tokens.
     *
     * @param amount the integer value of food tokens to be removed from the player
     * @throws IllegalArgumentException if the amount of food tokens to be removed is negative
     * @throws IllegalStateException    if the player does not have enough food tokens to be removed
     */
    public void removeFoodTokens(int amount) throws IllegalStateException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        if ((this.foodTokens - amount) >= 0) {
            this.foodTokens -= amount;
        } else {
            throw new IllegalStateException();
        }
    }


    /**
     * Returns the number of prestige tokens the player has.
     *
     * @return the integer representing the number of prestige tokens the player has
     */
    protected int getPrestigeTokens() {
        return this.prestigeTokens;
    }

    /**
     * Adds prestige tokens to the player.
     *
     * @param amount the integer value of prestige tokens to be added to the player
     * @throws IllegalArgumentException if the amount of prestige tokens to be added is negative
     */
    public void addPrestigeTokens(int amount) throws IllegalArgumentException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.prestigeTokens += amount;
    }

    /**
     * Removes prestige tokens from the player if the player has enough prestige tokens.
     *
     * @param amount the integer value of prestige tokens to be removed from the player
     * @throws IllegalArgumentException if the amount of prestige tokens to be removed is negative
     * @throws IllegalStateException    if the player does not have enough prestige tokens to be removed
     */
    public void removePrestigeTokens(int amount) throws IllegalStateException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.prestigeTokens -= amount;
    }

    /**
     * Returns the color of the player.
     *
     * @return the enum value of the color of the player
     */
    protected Color getPlayerColor() {
        return this.color;
    }

    /**
     * Returns the number of shaman stars the player has, which is the sum of the shaman stars from the shaman cards
     * in the player's character deck and the shaman stars from the three star building card if the player has it in
     * their building deck.
     *
     * @return the integer representing the number of shaman stars the player has
     */
    public int getShamanStars() {
        return this.shamanStars + (buildingDeck.contains(threeStarCard) ? 3 : 0);
    }

    /**
     * Increases the number of shaman stars the player has by a certain amount.
     *
     * @param amount the integer value of shaman stars to be added to the player
     * @throws IllegalArgumentException if the amount of shaman stars to be added is negative
     */
    public void increaseShamanStars(int amount) throws IllegalArgumentException {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.shamanStars += amount;
    }

    /**
     * Returns the number of hunter cards the player has in their character deck.
     *
     * @return the integer representing the number of hunter cards the player has in their character deck
     */
    public int getHuntersCounter() {
        return this.characterDeck.get(CharacterType.HUNTER).size();
    }

    /**
     * Returns the number of artist cards the player has in their character deck.
     *
     * @return the integer representing the number of artist cards the player has in their character deck
     */
    public int getArtistsCounter() {
        return this.characterDeck.get(CharacterType.ARTIST).size();
    }

    /**
     * Returns the number of gatherer cards the player has in their character deck.
     *
     * @return the integer representing the number of gatherer cards the player has in their character deck
     */
    public int getGatherersCounter() {
        return this.characterDeck.get(CharacterType.GATHERER).size();
    }

    /**
     * Returns the number of inventor cards the player has in their character deck.
     *
     * @return the integer representing the number of inventor cards the player has in their character deck
     */
    protected int getTopDrawNum() {
        return topDrawNum;
    }

    // TODO : DA CAPIRE
    public void setTopDrawNum(int topDrawNum) throws IllegalArgumentException {
        if (topDrawNum < 0) throw new IllegalArgumentException();
        this.topDrawNum = topDrawNum;
    }

    // TODO : DA CAPIRE
    protected int getBottomDrawNum() {
        return bottomDrawNum;
    }

    // TODO : DA CAPIRE
    public void setBottomDrawNum(int bottomDrawNum) throws IllegalArgumentException {
        if (bottomDrawNum < 0) throw new IllegalArgumentException();
        this.bottomDrawNum = bottomDrawNum;
    }

    // TODO : SERVE?
    public void setEnvironment(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * Returns the game environment of the player, which is the object that contains all the information about the game
     * state.
     *
     * @return the GameInfo object representing the game environment of the player
     * @throws IllegalStateException if the game environment is not available for the player
     */
    public GameInfo getEnvironment() throws IllegalStateException {
        if (this.gameInfo == null) {
            throw new IllegalStateException("Game Environment not available for player " + this.nickname);
        }
        return this.gameInfo;
    }

    /**
     * This method initializes the character sets map of the player, which is a map that associates each character type
     * with the number of complete sets of that character type the player has in their character deck. The method is called
     * when the player picks the specific building card which takes tracks of character sets completed by the player.
     *
     * @throws IllegalStateException if the character sets map has already been initialized
     */
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
