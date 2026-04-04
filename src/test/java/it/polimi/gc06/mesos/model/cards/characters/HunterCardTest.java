package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HunterCardTest {
    @Test
    void testConstructor() {
        Era expectedEra = Era.ERA_I;
        boolean expectedIconPresence = true;
        HunterCard card = new HunterCard(expectedEra, expectedIconPresence);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
        assertEquals(expectedIconPresence, card.hasFoodIcon());
    }
}