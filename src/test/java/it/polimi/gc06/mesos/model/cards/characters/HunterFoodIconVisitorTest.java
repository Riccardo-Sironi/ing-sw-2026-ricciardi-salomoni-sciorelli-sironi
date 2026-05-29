package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class HunterFoodIconVisitorTest {
    Player player;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting HunterFoodIconVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending HunterFoodIconVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        player = new Player("test", Color.ORANGE, new ModifierBuildingsRegistry(), new DTONotifier());

        System.out.println("[START] " + testInfo.getDisplayName());
    }



    @Test
    void visitHunterWithIcon() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        HunterFoodIconVisitor visitor = new HunterFoodIconVisitor(player);
        player.getCharacterDeck().get(CharacterType.HUNTER).addLast(card);

        assertDoesNotThrow(() -> visitor.visit(card));
        assertEquals(player.getCharacterDeck().get(CharacterType.HUNTER).size(), player.getFoodTokens());
    }

    @Test
    void visitHunterWithoutIcon() {
        HunterCard card = new HunterCard(Era.ERA_I, false);
        HunterFoodIconVisitor visitor = new HunterFoodIconVisitor(player);
        player.getCharacterDeck().get(CharacterType.HUNTER).addLast(card);

        assertDoesNotThrow(() -> visitor.visit(card));
        assertEquals(0, player.getFoodTokens());
    }

    @Test
    void visitHunterWithIconWhitHuuntersInDecks() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        HunterFoodIconVisitor visitor = new HunterFoodIconVisitor(player);
        player.getCharacterDeck().get(CharacterType.HUNTER).addLast(card);
        player.getCharacterDeck().get(CharacterType.HUNTER).addLast(new HunterCard(Era.ERA_I, false));
        player.getCharacterDeck().get(CharacterType.HUNTER).addLast(new HunterCard(Era.ERA_I, true));

        assertDoesNotThrow(() -> visitor.visit(card));
        assertEquals(3, player.getFoodTokens());
    }

    @Test
    void visitRitual() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(RitualEvent.class)));
    }

    @Test
    void visitSustenance() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(SustenanceEvent.class)));
    }

    @Test
    void visitHunt() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(HuntEvent.class)));
    }

    @Test
    void visitPaintings() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(PaintingsEvent.class)));
    }

    @Test
    void visitShaman() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(ShamanCard.class)));
    }

    @Test
    void visitArtist() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(ArtistCard.class)));
    }

    @Test
    void visitBuilder() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(BuilderCard.class)));
    }

    @Test
    void visitInventor() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(InventorCard.class)));
    }

    @Test
    void visitGatherer() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(GathererCard.class)));
    }

    @Test
    void visitTribeCard() {
        assertDoesNotThrow(() -> new HunterFoodIconVisitor(player).visit(mock(TribeCard.class)));
    }
}