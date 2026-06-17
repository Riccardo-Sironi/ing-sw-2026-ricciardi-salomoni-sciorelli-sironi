package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CharactersPresenceVisitorTest {

    private CharactersPresenceVisitor visitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting CharactersPresenceVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending CharactersPresenceVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        visitor = new CharactersPresenceVisitor();
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testInitialState() {
        assertFalse(visitor.areCharactersPresent(), "Initially there should be no characters present");
    }

    @Test
    void testVisitCharacterCardSetsTrue() {
        visitor.visit(mock(CharacterCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitHunterCardSetsTrue() {
        visitor.visit(mock(HunterCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitShamanCardSetsTrue() {
        visitor.visit(mock(ShamanCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitArtistCardSetsTrue() {
        visitor.visit(mock(ArtistCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitBuilderCardSetsTrue() {
        visitor.visit(mock(BuilderCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitInventorCardSetsTrue() {
        visitor.visit(mock(InventorCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitGathererCardSetsTrue() {
        visitor.visit(mock(GathererCard.class));
        assertTrue(visitor.areCharactersPresent());
    }

    @Test
    void testVisitCardKeepsFalse() {
        visitor.visit(mock(Card.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitTribeCardKeepsFalse() {
        visitor.visit(mock(TribeCard.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitEventCardKeepsFalse() {
        visitor.visit(mock(EventCard.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitBuildingCardKeepsFalse() {
        visitor.visit(mock(BuildingCard.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitRitualEventKeepsFalse() {
        visitor.visit(mock(RitualEvent.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitSustenanceEventKeepsFalse() {
        visitor.visit(mock(SustenanceEvent.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitHuntEventKeepsFalse() {
        visitor.visit(mock(HuntEvent.class));
        assertFalse(visitor.areCharactersPresent());
    }

    @Test
    void testVisitPaintingsEventKeepsFalse() {
        visitor.visit(mock(PaintingsEvent.class));
        assertFalse(visitor.areCharactersPresent());
    }
}