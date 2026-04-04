package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.*;

class ArtistCardTest {
    @Test
    void testConstructor() {
        Era excpectedEra = Era.ERA_I;
        ArtistCard card = new ArtistCard(excpectedEra);
        assertNotNull(card);
        assertEquals(excpectedEra, card.getEra());
    }

    @Test
    void testAccept() {
        ArtistCard card = new ArtistCard(Era.ERA_I);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}