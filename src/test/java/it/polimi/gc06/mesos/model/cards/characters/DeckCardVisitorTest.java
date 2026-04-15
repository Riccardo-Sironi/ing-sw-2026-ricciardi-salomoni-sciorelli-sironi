package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.*;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeckCardVisitorTest {
    Player player;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting DeckCardVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending DeckCardVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        player = new Player("test", Color.RED, new ModifierBuildingsRegistry());

        System.out.println("[START] " + testInfo.getDisplayName());
    }


    @Test
    void visitHunterCard() {
        HunterCard card = new HunterCard(Era.ERA_I, false);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.HUNTER).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.HUNTER).size());
    }

    @Test
    void visitShamanCard() {
        ShamanCard card = new ShamanCard(Era.ERA_I, 3);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.SHAMAN).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.SHAMAN).size());
    }

    @Test
    void visitArtistCard() {
        ArtistCard card = new ArtistCard(Era.ERA_I);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.ARTIST).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.ARTIST).size());
    }

    @Test
    void visitBuilderCard() {
        BuilderCard card = new BuilderCard(Era.ERA_I, 1, 1);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.BUILDER).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.BUILDER).size());
    }

    @Test
    void visitInventorCard() {
        InventorCard card = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.INVENTOR).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.INVENTOR).size());
    }

    @Test
    void visitGathererCard() {
        GathererCard card = new GathererCard(Era.ERA_I);
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(card));
        assertTrue(player.getCharacterDeck().get(CharacterType.GATHERER).contains(card));
        assertEquals(1, player.getCharacterDeck().get(CharacterType.GATHERER).size());
    }

    @Test
    void visitRitual() {
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(mock(RitualEvent.class)));
    }

    @Test
    void visitSustenance() {
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(mock(SustenanceEvent.class)));
    }

    @Test
    void visitHunt() {
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(mock(HuntEvent.class)));
    }

    @Test
    void visitPaintings() {
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(mock(PaintingsEvent.class)));
    }

    @Test
    void visitTribeCard() {
        assertDoesNotThrow(() -> new DeckCardVisitor(player).visit(mock(TribeCard.class)));
    }
}