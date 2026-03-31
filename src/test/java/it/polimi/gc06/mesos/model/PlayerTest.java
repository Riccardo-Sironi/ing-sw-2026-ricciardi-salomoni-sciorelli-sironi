package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;

    //
    private ModifierBuildingCard mockThreeStarCard;
    private GameInfo mockGameInfo;
    private ModifierBuildingsRegistry mockModifierRegistry;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {
        // 1. Inizializzazione dei Mock
        mockThreeStarCard = mock(ModifierBuildingCard.class);
        mockGameInfo = mock(GameInfo.class);
        mockModifierRegistry = mock(ModifierBuildingsRegistry.class);

        when(mockModifierRegistry.get(ModifierBuildingRegistryKey.RITUAL_THREE_STAR_CARD)).thenReturn(mockThreeStarCard);

        // 2. Setup Player
        player = new Player("TestUser", Color.RED, mockModifierRegistry);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testInitialValuesSuccess() {

        assertEquals("TestUser", player.getNickname(), "NICKNAME should be TestUser");
        assertEquals(Color.RED, player.getPlayerColor(), "COLOR should be RED");

        // tests for initial values of tokens and draw numbers equals to 0
        assertEquals(0, player.getFoodTokens(), "initial FOOD should be 0");
        assertEquals(0, player.getPrestigeTokens(), "initial PRESTIGE should be 0");
        assertEquals(0, player.getShamanStars(), "initial SHAMAN STARS should be 0");
        assertEquals(0, player.getTopDrawNum(), "initial TOP DRAW NUM should be 0");
        assertEquals(0, player.getBottomDrawNum(), "initial BOTTOM DRAW NUM should be 0");

        // tests for initial values of the decks not null
        assertNotNull(player.getCharacterDeck(), "character deck SHOULDN'T BE NULL");
        assertNotNull(player.getBuildingCards(), "buildings deck SHOULDN'T BE NULL");
    }


    // --- ENVIRONMENT TESTS

    @Test
    void testGetEnvironmentSuccess(){
        player.setEnvironment(mockGameInfo);

        assertDoesNotThrow(() -> {
            GameInfo retrievedInfo = player.getEnvironment();
            assertEquals(mockGameInfo, retrievedInfo,
                    "getEnvironment should return the mocked GameInfo");
        });
    }

    @Test
    void testGetEnvironmentThrowsExceptionWhenNull(){
        assertThrows(IllegalStateException.class, () -> player.getEnvironment(),
                "getEnvironment should throw IllegalStateException when environment is null");
    }


    // --- FOOD TESTS

    @Test
    void testAddFoodTokensSuccess() {

        assertDoesNotThrow(() -> player.addFoodTokens(5));
        player.addFoodTokens(3);

        assertEquals(8, player.getFoodTokens(),
                "FOOD TOKENS should be 8");
    }

    @Test
    void testAddFoodTokensZero() {
        assertDoesNotThrow(() -> player.addFoodTokens(0));

        assertEquals(0, player.getFoodTokens(),
                "FOOD TOKENS should remain 0 after adding 0");
    }

    @Test
    void testAddFoodTokensThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.addFoodTokens(-3),
                "NEGATIVE FOOD should throw IllegalArgumentException");
    }

    @Test
    void testRemoveFoodTokensSuccess() {
        player.addFoodTokens(5);
        assertDoesNotThrow(() -> player.removeFoodTokens(3));

        assertEquals(2, player.getFoodTokens(),
                "FOOD TOKENS should be 2");
    }

    @Test
    void testRemoveFoodTokensZero() {
        assertDoesNotThrow(() -> player.removeFoodTokens(0));

        assertEquals(0, player.getFoodTokens(),
                "FOOD TOKENS should remain 0 after removing 0");
    }

    @Test
    void testRemoveFoodTokensThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.removeFoodTokens(-3),
                "NEGATIVE FOOD should throw IllegalArgumentException");
    }

    @Test
    void testRemoveFoodTokensThrowsExceptionWhenNotEnough() {
        player.addFoodTokens(2);
        assertThrows(IllegalStateException.class, () -> player.removeFoodTokens(3),
                "NOT ENOUGH FOOD should throw IllegalStateException");
    }


    // --- PRESTIGE TESTS

    @Test
    void testAddPrestigeTokensSuccess() {
        assertDoesNotThrow(() -> player.addPrestigeTokens(10));
        player.addPrestigeTokens(5);

        assertEquals(15, player.getPrestigeTokens(),
                "PRESTIGE TOKENS should be 15");
    }

    @Test
    void testAddPrestigeTokensZero() {
        assertDoesNotThrow(() -> player.addPrestigeTokens(0));

        assertEquals(0, player.getPrestigeTokens(),
                "PRESTIGE TOKENS should remain 0 after adding 0");
    }

    @Test
    void testAddPrestigeTokensThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.addPrestigeTokens(-3),
                "NEGATIVE PRESTIGE TOKENS should throw IllegalArgumentException when added");
    }

    @Test
    void testRemovePrestigeTokensSuccess() {
        player.addPrestigeTokens(10);
        assertDoesNotThrow(() -> player.removePrestigeTokens(15));

        assertEquals(-5, player.getPrestigeTokens(),
                "PRESTIGE TOKENS should be -5");
    }

    @Test
    void testRemovePrestigeTokensZero() {
        assertDoesNotThrow(() -> player.removePrestigeTokens(0));

        assertEquals(0, player.getPrestigeTokens(),
                "PRESTIGE TOKENS should remain 0 after adding 0");
    }

    @Test
    void testRemovePrestigeTokensThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.removePrestigeTokens(-3),
                "NEGATIVE PRESTIGE TOKENS should throw IllegalArgumentException when removed");
    }


    // --- DRAW TESTS

    @Test
    void testSetTopDrawNumSuccess() {
        assertDoesNotThrow(() -> player.setTopDrawNum(3));
        assertEquals(3, player.getTopDrawNum(),
                "TOP DRAW NUM should be 3");
    }

    @Test
    void testSetTopDrawNumThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.setTopDrawNum(-2),
                "NEGATIVE TOP DRAW NUM should throw IllegalArgumentException");
    }

    @Test
    void testSetBottomDrawNumSuccess() {
        assertDoesNotThrow(() -> player.setBottomDrawNum(4));

        assertEquals(4, player.getBottomDrawNum(),
                "BOTTOM DRAW NUM should be 4");
    }

    @Test
    void testSetBottomDrawNumThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.setBottomDrawNum(-1),
                "NEGATIVE BOTTOM DRAW NUM should throw IllegalArgumentException");
    }


    // --- SHAMAN STARS TESTS

    @Test
    void testIncreaseShamanStarsSuccess(){
        assertDoesNotThrow(() -> player.increaseShamanStars(2));
        player.increaseShamanStars(3);

        assertEquals(5, player.getShamanStars(),
                "SHAMAN STARS should be 5");
    }

    @Test
    void testIncreaseShamanStarsThrowsExeptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.increaseShamanStars(-1),
                "NEGATIVE SHAMAN STARS should throw IllegalArgumentException");
    }

    @Test
    void testGetShamanStarsWithThreeStarCardModifier(){
        player.increaseShamanStars(2);
        player.addBuildingCards(mockThreeStarCard);

        assertEquals(5, player.getShamanStars(),
                "SHAMAN STARS should be 5 with THREE STAR CARD modifier");

    }


    // --- BUILDING CARDS TESTS

    @Test
    void testAddNormalBuildingCardSuccess(){
        BuildingCard mockBuildingCard = mock(BuildingCard.class);

        assertDoesNotThrow(() -> player.addBuildingCards(mockBuildingCard));
        assertTrue(player.getBuildingCards().contains(mockBuildingCard));

        assertEquals(1, player.getBuildingCards().size(),
                "BUILDING CARDS DECK should have size 1 after adding one card");
    }

    @Test
    void testAddNormalBuildingCardThrowsExceptionWhenNull(){
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((BuildingCard) null),
                "NULL BUILDING CARD should throw IllegalArgumentException");
    }

    @Test
    void testAddObserverSetBuildingCardSuccess(){
        player.setEnvironment(mockGameInfo);
        ObserverSetBuildingCard mockObserverSetCard = mock(ObserverSetBuildingCard.class);

        assertDoesNotThrow(() -> player.addBuildingCards(mockObserverSetCard));

        assertTrue(player.getBuildingCards().contains(mockObserverSetCard),
                "deck should now contain the mockObserverSetCard");
        assertTrue(player.hasSetBuildingCard(),
                "player should have set building initialized");

        assertDoesNotThrow(() -> verify(mockGameInfo, times(1)).addObserver(mockObserverSetCard));
    }

    @Test
    void testAddObserverSetBuildingCardThrowsExceptionWhenNull(){
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((ObserverSetBuildingCard) null),
                "NULL OBSERVER SET BUILDING CARD should throw IllegalArgumentException");
    }

    @Test
    void testAddObserverPairBuildingCardSuccess(){
        player.setEnvironment(mockGameInfo);
        ObserverSetBuildingCard mockObserverPairCard = mock(ObserverSetBuildingCard.class);

        assertDoesNotThrow(() -> player.addBuildingCards(mockObserverPairCard));

        assertTrue(player.getBuildingCards().contains(mockObserverPairCard),
                "deck should now contain the mockObserverPairCard");
        assertTrue(player.hasSetBuildingCard(),
                "player should now contain the mockObserverPairCard");

        assertDoesNotThrow(() -> verify(mockGameInfo, times(1)).addObserver(mockObserverPairCard));
    }

    @Test
    void testAddObserverPairBuildingCardThrowsExceptionWhenNull(){
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((ObserverPairBuildingCard) null),
                "NULL OBSERVER PAIR BUILDING CARD should throw IllegalArgumentException");
    }
}