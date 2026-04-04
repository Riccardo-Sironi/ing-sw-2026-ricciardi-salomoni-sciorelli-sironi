package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventorCardTest {
    @Test
    void testConstructor() {
        Era expectedEra = Era.ERA_I;
        InventionIcon expectedIcon = InventionIcon.BOAT;
        InventorCard card = new InventorCard(expectedEra, expectedIcon);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
        assertEquals(expectedIcon, card.getIcon());
    }

    @Test
    void testAccept() {
        InventorCard card = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        InventorPairsVisitor visitor = mock(InventorPairsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}