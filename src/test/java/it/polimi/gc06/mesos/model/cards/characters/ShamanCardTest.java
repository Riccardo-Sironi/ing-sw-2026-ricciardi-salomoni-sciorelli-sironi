package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShamanCardTest {
    @Test
    void testConstructor() {
        Era expectedEra = Era.ERA_I;
        int expectedStars = 2;
        ShamanCard card = new ShamanCard(expectedEra, expectedStars);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
        assertEquals(expectedStars, card.getStars());
    }

    @Test
    void testAccept() {
        ShamanCard card = new ShamanCard(Era.ERA_I, 2);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}