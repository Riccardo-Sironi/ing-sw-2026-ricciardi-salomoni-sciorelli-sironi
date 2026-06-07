package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import org.junit.jupiter.api.*;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CharactersSetsVisitorTest {
    Player player;
    CharactersSetsVisitor visitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting CharactersSetsVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending CharactersSetsVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {

        player = mock(Player.class);
        EnumMap<CharacterType, Integer> sets = new EnumMap<CharacterType, Integer>(CharacterType.class);
        for (CharacterType type : CharacterType.values()) {
            sets.put(type, 0);
        }
        when(player.getCharactersSets()).thenReturn(sets);
        visitor = new CharactersSetsVisitor(player);

        System.out.println("[START] " + testInfo.getDisplayName());
    }


    @Test
    void visitHunterCard() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.HUNTER));
    }

    @Test
    void visitShamanCard() {
        ShamanCard card = new ShamanCard(Era.ERA_I, 2);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.SHAMAN));
    }

    @Test
    void visitArtistCard() {
        ArtistCard card = new ArtistCard(Era.ERA_I);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.ARTIST));
    }

    @Test
    void visitBuilderCard() {
        BuilderCard card = new BuilderCard(Era.ERA_I, 2, 1);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.BUILDER));
    }

    @Test
    void visitInventorCard() {
        InventorCard card = new InventorCard(Era.ERA_I, InventionIcon.ROPE);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.INVENTOR));
    }

    @Test
    void visitGathererCard() {
        GathererCard card = new GathererCard(Era.ERA_I);
        visitor.visit(card);

        assertEquals(1, player.getCharactersSets().get(CharacterType.GATHERER));
    }

    @Test
    void visitRitual() {
        assertDoesNotThrow(() -> visitor.visit(mock(RitualEvent.class)));
    }

    @Test
    void visitSustenance() {
        assertDoesNotThrow(() -> visitor.visit(mock(SustenanceEvent.class)));
    }

    @Test
    void visitHunt() {
        assertDoesNotThrow(() -> visitor.visit(mock(HuntEvent.class)));
    }

    @Test
    void visitPaintings() {
        assertDoesNotThrow(() -> visitor.visit(mock(PaintingsEvent.class)));
    }

    @Test
    void visitTribeCard() {
        assertThrows(IllegalStateException.class, () -> visitor.visit(mock(TribeCard.class)));
    }
}