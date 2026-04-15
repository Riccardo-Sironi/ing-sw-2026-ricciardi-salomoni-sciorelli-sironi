package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShamanVisitorTest {
    Player player;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ShamanVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ShamanVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        player = new Player("TestPlayer", Color.RED, new ModifierBuildingsRegistry());

        System.out.println("[START] " + testInfo.getDisplayName());
    }


    @Test
    void visitShamanCard() {
        player = new Player("TestPlayer", Color.RED, new ModifierBuildingsRegistry());
        player.increaseShamanStars(5);
        ShamanCard card = new ShamanCard(Era.ERA_I, 5);
        ShamanVisitor visitor = new ShamanVisitor(player);

        assertDoesNotThrow(() -> visitor.visit(card));

        assertEquals(10, player.getShamanStars());
    }

    @Test
    void visitRitual() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(RitualEvent.class)));
    }

    @Test
    void visitSustenance() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(SustenanceEvent.class)));
    }

    @Test
    void visitHunt() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(HuntEvent.class)));
    }

    @Test
    void visitPaintings() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(PaintingsEvent.class)));
    }

    @Test
    void visitHunterCard() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(HunterCard.class)));
    }

    @Test
    void visitArtistCard() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(ArtistCard.class)));
    }

    @Test
    void visitBuilderCard() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(BuilderCard.class)));
    }

    @Test
    void visitInventorCard() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(InventorCard.class)));
    }

    @Test
    void vistiGathererCard() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(GathererCard.class)));
    }

    @Test
    void visitTribe() {
        assertDoesNotThrow(() -> new ShamanVisitor(player).visit(mock(TribeCard.class)));
    }
}