package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;


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
        // 1. Mock's Initialization
        mockThreeStarCard = mock(ModifierBuildingCard.class);
        mockGameInfo = mock(GameInfo.class);
        mockModifierRegistry = mock(ModifierBuildingsRegistry.class);

        when(mockModifierRegistry.get(ModifierBuildingRegistryKey.RITUAL_THREE_STAR_CARD)).thenReturn(mockThreeStarCard);

        // 2. Player's Setup
        player = new Player("TestUser", Color.ORANGE, mockModifierRegistry, new DTONotifier());

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testInitialValuesSuccess() {

        assertEquals("TestUser", player.getNickname(), "NICKNAME should be TestUser");
        assertEquals(Color.ORANGE, player.getPlayerColor(), "COLOR should be RED");

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
    void testGetEnvironmentSuccess() {
        player.setEnvironment(mockGameInfo);

        assertDoesNotThrow(() -> {
            GameInfo retrievedInfo = player.getEnvironment();
            assertEquals(mockGameInfo, retrievedInfo,
                    "getEnvironment should return the mocked GameInfo");
        });
    }

    @Test
    void testGetEnvironmentThrowsExceptionWhenNull() {
        assertThrows(IllegalStateException.class, () -> player.getEnvironment(),
                "getEnvironment should throw IllegalStateException when environment is null");
    }

    @Test
    void testInitMethodsThrowExceptionIfAlreadyInitialized() {
        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverSetBuildingCard.class));
        player.addBuildingCards(mock(ObserverPairBuildingCard.class));

        assertThrows(IllegalStateException.class, () -> player.initCharactersSets());
        assertThrows(IllegalStateException.class, () -> player.initInventorPairs());
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
    void testIncreaseShamanStarsSuccess() {
        assertDoesNotThrow(() -> player.increaseShamanStars(2));
        player.increaseShamanStars(3);

        assertEquals(5, player.getShamanStars(),
                "SHAMAN STARS should be 5");
    }

    @Test
    void testIncreaseShamanStarsThrowsExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> player.increaseShamanStars(-1),
                "NEGATIVE SHAMAN STARS should throw IllegalArgumentException");
    }

    @Test
    void testGetShamanStarsWithThreeStarCardModifier() {
        player.increaseShamanStars(2);
        player.addBuildingCards(mockThreeStarCard);

        assertEquals(5, player.getShamanStars(),
                "SHAMAN STARS should be 5 with THREE STAR CARD modifier");

    }


    // --- BUILDING CARDS TESTS

    @Test
    void testAddNormalBuildingCardSuccess() {
        BuildingCard mockBuildingCard = mock(BuildingCard.class);

        assertDoesNotThrow(() -> player.addBuildingCards(mockBuildingCard));
        assertTrue(player.getBuildingCards().contains(mockBuildingCard));

        assertEquals(1, player.getBuildingCards().size(),
                "BUILDING CARDS DECK should have size 1 after adding one card");
    }

    @Test
    void testAddNormalBuildingCardThrowsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((BuildingCard) null),
                "NULL BUILDING CARD should throw IllegalArgumentException");
    }

    @Test
    void testHasBuildingCardsInitiallyFalse() {
        assertFalse(player.hasSetBuildingCard(), "Initially should not have Set Building Card");
        assertFalse(player.hasPairBuildingCard(), "Initially should not have Pair Building Card");
    }

    @Test
    void testAddObserverSetBuildingCardSuccess() {
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
    void testAddObserverSetBuildingCardThrowsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((ObserverSetBuildingCard) null),
                "NULL OBSERVER SET BUILDING CARD should throw IllegalArgumentException");
    }

    @Test
    void testAddObserverPairBuildingCardSuccess() {
        player.setEnvironment(mockGameInfo);
        ObserverPairBuildingCard mockObserverPairCard = mock(ObserverPairBuildingCard.class);

        assertDoesNotThrow(() -> player.addBuildingCards(mockObserverPairCard));

        assertTrue(player.getBuildingCards().contains(mockObserverPairCard),
                "deck should now contain the mockObserverPairCard");
        assertTrue(player.hasPairBuildingCard(),
                "player should now contain the mockObserverPairCard");

        assertDoesNotThrow(() -> verify(mockGameInfo, times(1)).addObserver(mockObserverPairCard));
    }

    @Test
    void testAddObserverPairBuildingCardThrowsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> player.addBuildingCards((ObserverPairBuildingCard) null),
                "NULL OBSERVER PAIR BUILDING CARD should throw IllegalArgumentException");
    }

    @Test
    void testAddObserverBuildingsTwice() {
        player.setEnvironment(mockGameInfo);

        ObserverSetBuildingCard setCard = mock(ObserverSetBuildingCard.class);
        ObserverPairBuildingCard pairCard = mock(ObserverPairBuildingCard.class);

        player.addBuildingCards(setCard);
        player.addBuildingCards(pairCard);

        assertDoesNotThrow(() -> player.addBuildingCards(setCard));
        assertDoesNotThrow(() -> player.addBuildingCards(pairCard));
    }


    // --- CHARACTER CARDS TESTS

    @Test
    void testAddCharacterCardThrowsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> player.addCharacterCards(null),
                "NULL CHARACTER CARD should throw IllegalArgumentException");
    }

    @Test
    void testAddBuilderCardSuccess() {
        BuilderCard builder1 = new BuilderCard(Era.ERA_I, 2, 1);
        BuilderCard builder2 = new BuilderCard(Era.ERA_II, 3, 2);

        assertDoesNotThrow(() -> player.addCharacterCards(builder1));
        player.addCharacterCards(builder2);

        assertEquals(3, player.getBuildersDiscount(),
                "BUILDER DISCOUNT should be 3 (1 + 2)");
        assertEquals(5, player.getBuildersPrestige(),
                "BUILDER PRESTIGE should be 5 (2 + 3)");
        assertEquals(2, player.getCharacterDeck().get(CharacterType.BUILDER).size(),
                "CHARACTER DECK should have 2 BUILDER cards");
    }

    @Test
    void testAddHunterCardSuccess() {
        HunterCard hunterWithIcon = new HunterCard(Era.ERA_I, true);
        HunterCard hunterWithoutIcon = new HunterCard(Era.ERA_II, false);

        assertDoesNotThrow(() -> player.addCharacterCards(hunterWithIcon));
        player.addCharacterCards(hunterWithoutIcon);

        assertEquals(2, player.getHuntersCounter(),
                "HUNTER COUNTER should be 2");
        assertEquals(2, player.getCharacterDeck().get(CharacterType.HUNTER).size(),
                "CHARACTER DECK should have 2 HUNTER cards");
        assertEquals(1, player.getFoodTokens(),
                "FOOD TOKENS should be 1 after adding a HUNTER card with icon");
    }

    @Test
    void testAddArtistCardSuccess() {
        ArtistCard artist1 = new ArtistCard(Era.ERA_I);
        ArtistCard artist2 = new ArtistCard(Era.ERA_II);

        assertDoesNotThrow(() -> player.addCharacterCards(artist1));
        player.addCharacterCards(artist2);

        assertEquals(2, player.getArtistsCounter(),
                "ARTIST COUNTER should be 2");
        assertEquals(2, player.getCharacterDeck().get(CharacterType.ARTIST).size(),
                "CHARACTER DECK should have 2 ARTIST cards");
    }

    @Test
    void testAddGatherersCardSuccess() {
        GathererCard gatherer1 = new GathererCard(Era.ERA_I);
        GathererCard gatherer2 = new GathererCard(Era.ERA_II);

        assertDoesNotThrow(() -> player.addCharacterCards(gatherer1));
        player.addCharacterCards(gatherer2);

        assertEquals(2, player.getGatherersCounter(),
                "GATHERERS COUNTER should be 2");
        assertEquals(2, player.getCharacterDeck().get(CharacterType.GATHERER).size(),
                "CHARACTER DECK should have 2 GATHERERS cards");
    }

    @Test
    void testAddInventorCardSuccess() {
        InventorCard boatInventor = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        InventorCard arrowheadInventor = new InventorCard(Era.ERA_II, InventionIcon.ARROWHEAD);
        InventorCard hookInventor = new InventorCard(Era.ERA_I, InventionIcon.HOOK);
        InventorCard necklaceInventor = new InventorCard(Era.ERA_II, InventionIcon.NECKLACE);
        InventorCard bowlInventor = new InventorCard(Era.ERA_I, InventionIcon.BOWL);
        InventorCard ropeInventor = new InventorCard(Era.ERA_II, InventionIcon.ROPE);
        InventorCard figureInventor = new InventorCard(Era.ERA_I, InventionIcon.FIGURE);
        InventorCard fluteInventor = new InventorCard(Era.ERA_II, InventionIcon.FLUTE);
        InventorCard hideInventor = new InventorCard(Era.ERA_I, InventionIcon.HIDE);
        InventorCard breadInventor = new InventorCard(Era.ERA_II, InventionIcon.BREAD);

        assertDoesNotThrow(() -> player.addCharacterCards(boatInventor));
        player.addCharacterCards(arrowheadInventor);
        player.addCharacterCards(hookInventor);
        player.addCharacterCards(necklaceInventor);
        player.addCharacterCards(bowlInventor);
        player.addCharacterCards(ropeInventor);
        player.addCharacterCards(figureInventor);
        player.addCharacterCards(fluteInventor);
        player.addCharacterCards(hideInventor);
        player.addCharacterCards(breadInventor);

        assertEquals(10, player.getInventorsCounter(),
                "INVENTORS COUNTER should be 10");
        assertEquals(10, player.getCharacterDeck().get(CharacterType.INVENTOR).size(),
                "CHARACTER DECK should have 10 INVENTORS cards");
    }

    @Test
    void testAddShamanCardSuccess() {
        ShamanCard shaman1 = new ShamanCard(Era.ERA_I, 1);
        ShamanCard shaman2 = new ShamanCard(Era.ERA_II, 2);

        assertDoesNotThrow(() -> player.addCharacterCards(shaman1));
        player.addCharacterCards(shaman2);

        assertEquals(3, player.getShamanStars(),
                "SHAMAN STARS should be 3 (1 + 2)");
        assertEquals(2, player.getCharacterDeck().get(CharacterType.SHAMAN).size(),
                "CHARACTER DECK should have 2 SHAMAN cards");
    }


    // --- SETS AND PAIRS TESTS ---

    @Test
    void testCharactersSetsCompletion() {
        player.setEnvironment(mockGameInfo);
        ObserverSetBuildingCard mockSetCard = mock(ObserverSetBuildingCard.class);
        player.addBuildingCards(mockSetCard);

        assertFalse(player.hasCompletedSet(), "Initially, the player should not have a completed set");

        player.addCharacterCards(new HunterCard(Era.ERA_I, false));
        player.addCharacterCards(new GathererCard(Era.ERA_I));
        player.addCharacterCards(new BuilderCard(Era.ERA_I, 1, 1));
        player.addCharacterCards(new ArtistCard(Era.ERA_I));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));

        assertFalse(player.hasCompletedSet(), "Set should not be complete without a Shaman");

        player.addCharacterCards(new ShamanCard(Era.ERA_I, 3));

        assertTrue(player.hasCompletedSet(), "Set should be complete after adding one character of each type");
    }

    @Test
    void testIncreaseCharactersSetsThrowsExceptionWhenNull() {
        assertThrows(IllegalStateException.class, () -> player.increaseCharactersSets(CharacterType.HUNTER));
    }

    @Test
    void testIncreaseCharactersSetsDirectly() {
        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverSetBuildingCard.class));

        assertDoesNotThrow(() -> player.increaseCharactersSets(CharacterType.HUNTER));
        assertEquals(1, player.getCharactersSets().get(CharacterType.HUNTER));

        assertThrows(IllegalArgumentException.class, () -> player.increaseCharactersSets(null));
    }

    @Test
    void testDecreaseCharactersSets() {
        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverSetBuildingCard.class));
        player.addCharacterCards(new HunterCard(Era.ERA_I, false));
        player.addCharacterCards(new GathererCard(Era.ERA_I));
        player.addCharacterCards(new BuilderCard(Era.ERA_I, 1, 1));
        player.addCharacterCards(new ArtistCard(Era.ERA_I));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));
        player.addCharacterCards(new ShamanCard(Era.ERA_I, 2));

        assertTrue(player.hasCompletedSet());

        assertDoesNotThrow(() -> player.decreaseCharactersSets());

        assertFalse(player.hasCompletedSet(), "Set should not be complete after decreasing");
    }

    @Test
    void testDecreaseAndHasSetThrowsExceptionWhenNull() {
        assertThrows(IllegalStateException.class, () -> player.decreaseCharactersSets());
        assertThrows(IllegalStateException.class, () -> player.hasCompletedSet());
    }

    @Test
    void testGetInventorPairs() {
        assertNull(player.getInventorPairs(), "Initially inventorPairs map should be null");

        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverPairBuildingCard.class));

        assertNotNull(player.getInventorPairs(), "After building, map should not be null");
    }

    @Test
    void testIncreaseInventorPairsDirectly() {
        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverPairBuildingCard.class));

        assertThrows(IllegalArgumentException.class, () -> player.increaseInventorPairs(null));
    }

    @Test
    void testInventorPairsCompletion() {
        player.setEnvironment(mockGameInfo);
        ObserverPairBuildingCard mockPairCard = mock(ObserverPairBuildingCard.class);
        player.addBuildingCards(mockPairCard);

        assertFalse(player.hasCompletedPair(), "Initially, the player should not have a completed pair");

        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));
        assertFalse(player.hasCompletedPair(), "One boat inventor does not make a pair");

        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.ROPE));
        assertFalse(player.hasCompletedPair(), "Two different icons do not make a pair");

        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));

        assertTrue(player.hasCompletedPair(), "Player should have a completed pair of BOATS");
    }

    @Test
    void testDecreaseInventorPair() {
        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverPairBuildingCard.class));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));

        assertTrue(player.hasCompletedPair());

        assertDoesNotThrow(() -> player.decreaseInventorPair());

        assertFalse(player.hasCompletedPair(), "Pair should be removed after calling decreaseInventorPair");
    }

    @Test
    void testInitInventorPairsWithPreExistingCards() {
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.BOAT));
        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.ROPE));

        player.setEnvironment(mockGameInfo);
        player.addBuildingCards(mock(ObserverPairBuildingCard.class));

        assertEquals(0, player.getInventorPairs().get(InventionIcon.BOAT));
        assertEquals(1, player.getInventorPairs().get(InventionIcon.ROPE));
    }

    @Test
    void testPairsMethodsThrowsExceptionWhenNull() {
        assertThrows(IllegalStateException.class, () -> player.decreaseInventorPair());
        assertThrows(IllegalStateException.class, () -> player.hasCompletedPair());
        assertThrows(IllegalStateException.class, () -> player.increaseInventorPairs(InventionIcon.BOAT));
    }

    @Test
    void testGetNumOfIcon() {
        List<CharacterCard> inventorDeck = player.getCharacterDeck().get(CharacterType.INVENTOR);
        inventorDeck.clear();

        inventorDeck.add(mock(CharacterCard.class));
        inventorDeck.add(mock(CharacterCard.class));
        inventorDeck.add(mock(CharacterCard.class));

        try (MockedConstruction<InventorIconVisitor> mockedVisitor = mockConstruction(InventorIconVisitor.class,
                (mock, context) -> {
                    when(mock.getAndClearIcon()).thenReturn(
                            InventionIcon.BOAT,
                            InventionIcon.ARROWHEAD,
                            InventionIcon.BOAT,
                            null
                    );
                })) {
            int numIcons = player.getNumOfIcon();

            assertEquals(2, numIcons, "Inventor should have 2 icons");

            inventorDeck.clear();
            assertEquals(0, player.getNumOfIcon(), "Inventor should have 0 icons after clearing deck");
        }
    }

    @Test
    void testGetNumOfIconAdd() {
        List<CharacterCard> inventorDeck = player.getCharacterDeck().get(CharacterType.INVENTOR);
        inventorDeck.clear();

        CharacterCard cardWithIcon = mock(CharacterCard.class);
        CharacterCard cardWithoutIcon = mock(CharacterCard.class);
        CharacterCard duplicateCard = mock(CharacterCard.class);

        inventorDeck.add(cardWithIcon);
        inventorDeck.add(cardWithoutIcon);
        inventorDeck.add(duplicateCard);

        try (MockedConstruction<InventorIconVisitor> mockedVisitor = mockConstruction(
                InventorIconVisitor.class,
                (mock, context) -> {
                    when(mock.getAndClearIcon())
                            .thenReturn(InventionIcon.BOAT)
                            .thenReturn(null)
                            .thenReturn(InventionIcon.BOAT);
                }
        )) {

            assertEquals(1, player.getNumOfIcon(), "Inventor should have 1 icon BOAT");
        }
    }

    @Test
    void testSetPlayerColor() {
        player.setPlayerColor(Color.TURQUOISE);
        assertEquals(Color.TURQUOISE, player.getPlayerColor(), "Player color should be TURQUOISE");
    }

    @Test
    void testSetNotifier() {
        Player p = new Player("TestUser", Color.ORANGE, mockModifierRegistry, null);
        DTONotifier notifier = mock(DTONotifier.class);
        assertThrows(NullPointerException.class, () -> p.addFoodTokens(1));
        player.setNotifier(notifier);
        assertDoesNotThrow(() -> player.addFoodTokens(1));
    }

    @Test
    @DisplayName("forceState correctly overrides all primitive values, lists, and tracking structures")
    void testForceState() {
        int expectedPrestige = 45;
        int expectedFood = 12;
        int expectedShamanStars = 4;
        int expectedTopDraw = 2;
        int expectedBottomDraw = 1;

        CharacterCard mockHunter = mock(CharacterCard.class);
        CharacterCard mockArtist = mock(CharacterCard.class);
        Map<CharacterType, List<CharacterCard>> savedCharacterDeck = new EnumMap<>(CharacterType.class);
        for (CharacterType type : CharacterType.values()) {
            savedCharacterDeck.put(type, new ArrayList<>());
        }
        savedCharacterDeck.get(CharacterType.HUNTER).add(mockHunter);
        savedCharacterDeck.get(CharacterType.ARTIST).add(mockArtist);

        BuildingCard mockBuilding = mock(BuildingCard.class);
        List<BuildingCard> savedBuildingDeck = new ArrayList<>(List.of(mockBuilding));

        Map<CharacterType, Integer> savedCharactersSets = new EnumMap<>(CharacterType.class);
        savedCharactersSets.put(CharacterType.HUNTER, 2);

        Map<InventionIcon, Integer> savedInventorPairs = new EnumMap<>(InventionIcon.class);
        savedInventorPairs.put(InventionIcon.BOAT, 1);

        player.forceState(
                expectedPrestige, expectedFood, expectedShamanStars,
                expectedTopDraw, expectedBottomDraw,
                savedCharacterDeck, savedBuildingDeck,
                savedCharactersSets, savedInventorPairs
        );

        assertEquals(expectedPrestige, player.getPrestigeTokens());
        assertEquals(expectedFood, player.getFoodTokens());
        assertEquals(expectedShamanStars, player.getShamanStars());
        assertEquals(expectedTopDraw, player.getTopDrawNum());
        assertEquals(expectedBottomDraw, player.getBottomDrawNum());

        assertEquals(1, player.getCharacterDeck().get(CharacterType.HUNTER).size());
        assertEquals(mockHunter, player.getCharacterDeck().get(CharacterType.HUNTER).get(0));
        assertEquals(1, player.getBuildingCards().size());
        assertEquals(mockBuilding, player.getBuildingCards().getFirst());

        assertNotNull(player.getCharactersSets());
        assertEquals(2, player.getCharactersSets().get(CharacterType.HUNTER));
        assertNotNull(player.getInventorPairs());
        assertEquals(1, player.getInventorPairs().get(InventionIcon.BOAT));

        player.forceState(
                expectedPrestige, expectedFood, expectedShamanStars,
                expectedTopDraw, expectedBottomDraw,
                savedCharacterDeck, savedBuildingDeck,
                null, null
        );

        assertNull(player.getCharactersSets(), "charactersSets should remain null if forced with null");
        assertNull(player.getInventorPairs(), "inventorPairs should remain null if forced with null");
    }
}