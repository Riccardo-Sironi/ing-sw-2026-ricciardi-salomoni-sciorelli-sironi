package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameInfo;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObserverSetBuildingCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ObserverSetBuildingCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ObserverSetBuildingCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testUpdateWhenSetCompleted() {
        ObserverSetBuildingCard observerPairBuildingCard = new ObserverSetBuildingCard();
        Player mockPlayer = mock(Player.class);
        mockPlayer.addBuildingCards(observerPairBuildingCard);
        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(observerPairBuildingCard);

        when(mockPlayer.getBuildingCards()).thenReturn(buildingCards);

        when(mockPlayer.hasCompletedSet()).thenReturn(true);
        observerPairBuildingCard.update(mockPlayer);

        verify(mockPlayer, times(1)).addFoodTokens(5);
        verify(mockPlayer, times(1)).decreaseCharactersSets();
    }

    @Test
    void testUpdateWhenSetNotCompleted() {
        ObserverSetBuildingCard observerPairBuildingCard = new ObserverSetBuildingCard();
        Player mockPlayer = mock(Player.class);

        when(mockPlayer.hasCompletedSet()).thenReturn(false);
        observerPairBuildingCard.update(mockPlayer);

        verify(mockPlayer, never()).addFoodTokens(anyInt());
        verify(mockPlayer, never()).decreaseCharactersSets();
    }

    @Test
    void testAccept() {
        CardVisitor visitor = mock(CardVisitor.class);
        ObserverSetBuildingCard observerSetBuildingCard = new ObserverSetBuildingCard();
        observerSetBuildingCard.accept(visitor);

        verify(visitor, times(1)).visit(observerSetBuildingCard);
    }

    @Test
    void testAcquiringFullSetTriggersFoodReward() {
        GameInfo mockGameInfo = mock(GameInfo.class);
        ModifierBuildingsRegistry mockRegistry = mock(ModifierBuildingsRegistry.class);
        DTONotifier mockNotifier = mock(DTONotifier.class);

        Player player = new Player("TestPlayer", null, mockRegistry, mockNotifier);
        player.setEnvironment(mockGameInfo);

        int initialFood = player.getFoodTokens();

        ObserverSetBuildingCard buildingCard = new ObserverSetBuildingCard();
        player.addBuildingCards(buildingCard);

        ArtistCard artist = mock(ArtistCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(artist);
            return null;
        }).when(artist).accept(any());

        BuilderCard builder = mock(BuilderCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(builder);
            return null;
        }).when(builder).accept(any());

        GathererCard gatherer = mock(GathererCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(gatherer);
            return null;
        }).when(gatherer).accept(any());

        HunterCard hunter = mock(HunterCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(hunter);
            return null;
        }).when(hunter).accept(any());

        ShamanCard shaman = mock(ShamanCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(shaman);
            return null;
        }).when(shaman).accept(any());

        InventorCard inventor = mock(InventorCard.class);
        doAnswer(i -> {
            ((CardVisitor) i.getArgument(0)).visit(inventor);
            return null;
        }).when(inventor).accept(any());

        player.addCharacterCards(artist);
        player.addCharacterCards(builder);
        player.addCharacterCards(gatherer);
        player.addCharacterCards(hunter);
        player.addCharacterCards(shaman);

        buildingCard.update(player);

        assertEquals(initialFood, player.getFoodTokens(),
                "Food tokens should not increase with an incomplete set (5/6 characters)");

        player.addCharacterCards(inventor);

        buildingCard.update(player);

        assertEquals(initialFood + 5, player.getFoodTokens(),
                "Food tokens should increase by 5 after completing a full character set");

        assertFalse(player.hasCompletedSet(),
                "The completed set flag should be reset after the reward is claimed");
    }

    @Test
    void testEquals() {
        ObserverSetBuildingCard card1 = new ObserverSetBuildingCard();
        ObserverSetBuildingCard card2 = new ObserverSetBuildingCard();
        ObserverPairBuildingCard differentCard = new ObserverPairBuildingCard();

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, differentCard);
        assertNotEquals(card1, null);
    }
}