package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.*;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.model.cards.events.*;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class InventorPairsVisitorTest {
    Player player;
    InventorPairsVisitor visitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting InventorPairsVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending InventorPairsVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        GameModel modelMock = mock(GameModel.class);
        player = new Player("TestPlayer", Color.ORANGE, new ModifierBuildingsRegistry(), new DTONotifier());
        player.setEnvironment(mock(GameModel.class));
        visitor = new InventorPairsVisitor(player);

        System.out.println("[START] " + testInfo.getDisplayName());
    }


    @Test
    void visitInventorCard() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        observerPairBuildingCard.setFoodCost(0);

        Board board = new Board(null, null, new DTONotifier());
        board.getTopBuildings().add(observerPairBuildingCard);
        board.buyBuildingFromTopRow(player, observerPairBuildingCard);

        assertNotNull(player.getInventorPairs());

        InventorCard card = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        assertDoesNotThrow(() -> visitor.visit(card));
        assertNotNull(player.getInventorPairs());
        assertEquals(1, player.getInventorPairs().get(InventionIcon.BOAT));
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
    void visitHunterCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(HunterCard.class)));
    }

    @Test
    void visitShamanCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(ShamanCard.class)));
    }

    @Test
    void ArtistCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(ArtistCard.class)));
    }

    @Test
    void visitBuilderCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(BuilderCard.class)));
    }

    @Test
    void visitGathererCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(GathererCard.class)));
    }

    @Test
    void visitTribeCard() {
        assertDoesNotThrow(() -> visitor.visit(mock(TribeCard.class)));
    }
}