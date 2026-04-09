package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class CardVisitorTest {


    // DEFAULT CLASSES

    @Test
    void testCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};

        assertDoesNotThrow(() -> visitor.visit(mock(Card.class)),
                "No exception should be thrown when visiting a Card");
    }

    @Test
    void testTribeCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};

        assertDoesNotThrow(() -> visitor.visit(mock(TribeCard.class)),
                "No exception should be thrown when visiting a TribeCard");
    }

    @Test
    void testEventCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};

        assertDoesNotThrow(() -> visitor.visit(mock(EventCard.class)),
                "No exception should be thrown when visiting a EventCard");
    }

    @Test
    void testCharacterCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};

        assertDoesNotThrow(() -> visitor.visit(mock(CharacterCard.class)),
                "No exception should be thrown when visiting a CharacterCard");
    }

    @Test
    void testBuildingCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(BuildingCard.class)),
                "No exception should be thrown when visiting a BuildingCard");
    }


    // EVENTS

    @Test
    void testRitualEventVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(RitualEvent.class)),
                "No exception should be thrown when visiting a RitualEvent");
    }

    @Test
    void testSustenanceEventVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(SustenanceEvent.class)),
                "No exception should be thrown when visiting a SustenanceEvent");
    }

    @Test
    void testHuntEventVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(HuntEvent.class)),
                "No exception should be thrown when visiting a HuntEvent");
    }

    @Test
    void testPaintingsEventVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(PaintingsEvent.class)),
                "No exception should be thrown when visiting a PaintingsEvent");
    }


    // CHARACTERS

    @Test
    void testHunterCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(HunterCard.class)),
                "No exception should be thrown when visiting a HunterCard");
    }

    @Test
    void testShamanCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(ShamanCard.class)),
                "No exception should be thrown when visiting a ShamanCard");
    }

    @Test
    void testArtistCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(ArtistCard.class)),
                "No exception should be thrown when visiting a ArtistCard");
    }

    @Test
    void testBuilderCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(BuilderCard.class)),
                "No exception should be thrown when visiting a BuilderCard");
    }

    @Test
    void testInvetorCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(InventorCard.class)),
                "No exception should be thrown when visiting a InventorCard");
    }

    @Test
    void testGathererCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(GathererCard.class)),
                "No exception should be thrown when visiting a GathererCard");
    }


    // BUILDINGS

    @Test
    void testEndGameBuildingCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(EndGameBuildingCard.class)),
                "No exception should be thrown when visiting a EndGameBuildingCard");
    }

    @Test
    void testModifierBuildingCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(ModifierBuildingCard.class)),
                "No exception should be thrown when visiting a ModifierBuildingCard");
    }

    @Test
    void testObserverSetBuildingCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(ObserverSetBuildingCard.class)),
                "No exception should be thrown when visiting a ObserverSetBuildingCard");
    }

    @Test
    void testObserverPairBuildingCardVisitor() {
        CardVisitor visitor = new CardVisitor() {};
        assertDoesNotThrow(() -> visitor.visit(mock(ObserverPairBuildingCard.class)),
                "No exception should be thrown when visiting a ObserverPairBuildingCard");
    }
}