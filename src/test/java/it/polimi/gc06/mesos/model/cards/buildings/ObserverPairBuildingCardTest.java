package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

class ObserverPairBuildingCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ObserverPairBuildingCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ObserverPairBuildingCardTest ---");
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
    void testUpdateWhenPairCompleted() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        Player mockPlayer = mock(Player.class);
        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(observerPairBuildingCard);

        when(mockPlayer.getBuildingCards()).thenReturn(buildingCards);

        when(mockPlayer.hasCompletedPair()).thenReturn(true);
        observerPairBuildingCard.update(mockPlayer);

        verify(mockPlayer, times(1)).addFoodTokens(3);
        verify(mockPlayer, times(1)).decreaseInventorPair();
    }

    @Test
    void testUpdateWhenPairNotCompleted() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        Player mockPlayer = mock(Player.class);

        when(mockPlayer.hasCompletedPair()).thenReturn(false);
        observerPairBuildingCard.update(mockPlayer);

        verify(mockPlayer, never()).addFoodTokens(anyInt());
        verify(mockPlayer, never()).decreaseInventorPair();
    }

    @Test
    void testAccept() {
        CardVisitor visitor = mock(CardVisitor.class);
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        observerPairBuildingCard.accept(visitor);

        verify(visitor, times(1)).visit(observerPairBuildingCard);
    }
}