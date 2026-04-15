package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddToBuildingsVisitorTest {

    private Player mockPlayer;
    private AddToBuildingsVisitor visitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting AddToBuildingsVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending AddToBuildingsVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        mockPlayer = mock(Player.class);
        visitor = new AddToBuildingsVisitor(mockPlayer);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testVisitEndGameBuildingCard(){
        EndGameBuildingCard endGameBuildingCard = mock(EndGameBuildingCard.class);
        visitor.visit(endGameBuildingCard);

        verify(mockPlayer, times(1)).addBuildingCards(endGameBuildingCard);
    }

    @Test
    void testVisitModifierBuildingCard(){
        ModifierBuildingCard modifierBuildingCard = mock(ModifierBuildingCard.class);
        visitor.visit(modifierBuildingCard);

        verify(mockPlayer, times(1)).addBuildingCards(modifierBuildingCard);
    }

    @Test
    void testVisitObserverSetBuildingCard(){
        ObserverSetBuildingCard observerSetBuildingCard = mock(ObserverSetBuildingCard.class);
        visitor.visit(observerSetBuildingCard);

        verify(mockPlayer, times(1)).addBuildingCards(observerSetBuildingCard);
    }

    @Test
    void testVisitObserverPairBuildingCard(){
        ObserverPairBuildingCard observerPairBuildingCard = mock(ObserverPairBuildingCard.class);
        visitor.visit(observerPairBuildingCard);

        verify(mockPlayer, times(1)).addBuildingCards(observerPairBuildingCard);
    }

    @Test
    void testVisitGenericBuildingCardThrowsException(){
        BuildingCard buildingCard = mock(BuildingCard.class);
        IllegalGameActionException exception = assertThrows(IllegalGameActionException.class, () -> visitor.visit(buildingCard));

        assertEquals("Test.", exception.getMessage());
    }
}