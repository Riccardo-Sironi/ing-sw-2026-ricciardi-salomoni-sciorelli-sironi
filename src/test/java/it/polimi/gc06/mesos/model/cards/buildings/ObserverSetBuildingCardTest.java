package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

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

}