package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameInfo;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;
import it.polimi.gc06.mesos.model.cards.characters.InventorCard;
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

    @Test
    void testAcquiringSecondInventorTriggersFoodReward() {
        GameInfo mockGameInfo = mock(GameInfo.class);
        ModifierBuildingsRegistry mockRegistry = mock(ModifierBuildingsRegistry.class);
        DTONotifier mockNotifier = mock(DTONotifier.class);

        Player player = new Player("TestPlayer", null, mockRegistry, mockNotifier);
        player.setEnvironment(mockGameInfo);

        int initialFood = player.getFoodTokens();

        ObserverPairBuildingCard buildingCard = new ObserverPairBuildingCard();
        player.addBuildingCards(buildingCard);

        InventorCard firstInventor = new InventorCard(Era.ERA_I, InventionIcon.ROPE);
        InventorCard secondInventor = new InventorCard(Era.ERA_I, InventionIcon.ROPE);

        player.addCharacterCards(firstInventor);
        buildingCard.update(player); // force this that should be done by the board

        Assertions.assertEquals(initialFood, player.getFoodTokens(),
                "Food tokens should not increase with only one inventor");

        player.addCharacterCards(secondInventor);
        buildingCard.update(player); // force this that should be done by the board

        Assertions.assertEquals(initialFood + 3, player.getFoodTokens(),
                "Food tokens should increase by 3 after completing an inventor pair");

        Assertions.assertFalse(player.hasCompletedPair(),
                "The completed pair flag should be reset after the reward is claimed");

        player.addCharacterCards(new InventorCard(Era.ERA_I, InventionIcon.FIGURE));
        buildingCard.update(player);

        Assertions.assertFalse(player.hasCompletedPair(),
                "Adding a different inventor should not trigger the pair completion");
    }
}