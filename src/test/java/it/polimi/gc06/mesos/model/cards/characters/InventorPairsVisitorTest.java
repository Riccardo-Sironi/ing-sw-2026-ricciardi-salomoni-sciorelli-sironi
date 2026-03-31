package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.model.cards.events.*;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventorPairsVisitorTest {
    Player player;
    InventorPairsVisitor visitor;

    @BeforeEach
    void setUp() {
        GameModel modelMock = mock(GameModel.class);
        player = new Player("TestPlayer", Color.RED, null);
        player.setEnvironment(mock(GameModel.class));
        visitor = new InventorPairsVisitor(player);
    }

    @Test
    void visitInventorCard() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        observerPairBuildingCard.setFoodCost(0);

        Board board = new Board(null,null);
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