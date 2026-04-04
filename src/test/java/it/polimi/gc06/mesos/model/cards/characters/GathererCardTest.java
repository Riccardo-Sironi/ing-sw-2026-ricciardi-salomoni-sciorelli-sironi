package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GathererCardTest {
    @Test
    void testConstructor() {
        Era expectedEra = Era.ERA_I;
        GathererCard card = new GathererCard(expectedEra);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
    }

    @Test
    void testAccept() {
        GathererCard card = new GathererCard(Era.ERA_I);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}