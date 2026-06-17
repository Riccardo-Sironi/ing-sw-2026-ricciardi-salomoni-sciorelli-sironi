package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BuildingPresenceVisitorTest {

    private BuildingPresenceVisitor visitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BuildingPresenceVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BuildingPresenceVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        visitor = new BuildingPresenceVisitor();
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testInitialState() {
        assertFalse(visitor.areThereBuildings(), "Initially there should be no buildings present");
    }

    @Test
    void testVisitObserverPairBuildingCard() {
        visitor.visit(mock(ObserverPairBuildingCard.class));
        assertTrue(visitor.areThereBuildings(), "Flag should be true after visiting ObserverPairBuildingCard");
    }

    @Test
    void testVisitObserverSetBuildingCard() {
        visitor.visit(mock(ObserverSetBuildingCard.class));
        assertTrue(visitor.areThereBuildings(), "Flag should be true after visiting ObserverSetBuildingCard");
    }

    @Test
    void testVisitModifierBuildingCard() {
        visitor.visit(mock(ModifierBuildingCard.class));
        assertTrue(visitor.areThereBuildings(), "Flag should be true after visiting ModifierBuildingCard");
    }

    @Test
    void testVisitEndGameBuildingCard() {
        visitor.visit(mock(EndGameBuildingCard.class));
        assertTrue(visitor.areThereBuildings(), "Flag should be true after visiting EndGameBuildingCard");
    }

    @Test
    void testVisitBuildingCard() {
        visitor.visit(mock(BuildingCard.class));
        assertTrue(visitor.areThereBuildings(), "Flag should be true after visiting BuildingCard");
    }
}