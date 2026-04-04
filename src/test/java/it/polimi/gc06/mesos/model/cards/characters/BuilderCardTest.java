package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderCardTest {
    @Test
    void testConstructor() {
        Era excpectedEra = Era.ERA_I;
        int expectedPrestige = 3;
        int expectedFoodDiscount = 2;
        BuilderCard card = new BuilderCard(excpectedEra, expectedPrestige, expectedFoodDiscount);
        assertNotNull(card);
        assertEquals(excpectedEra, card.getEra());
        assertEquals(expectedPrestige, card.getPrestige());
        assertEquals(expectedFoodDiscount, card.getFoodDiscount());
    }
}