package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BuilderCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BuilderCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BuilderCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        System.out.println("[START] " + testInfo.getDisplayName());
    }

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

    @Test
    void testDefaultConstructorAndSetters() {
        BuilderCard card = new BuilderCard();
        assertEquals(-1, card.getPrestige());
        assertEquals(-1, card.getFoodDiscount());

        card.setPrestige(5);
        card.setFoodDiscount(3);

        assertEquals(5, card.getPrestige());
        assertEquals(3, card.getFoodDiscount());
    }

    @Test
    void testAccept() {
        BuilderCard card = new BuilderCard(Era.ERA_I, 3, 2);
        CharactersSetsVisitor visitor = Mockito.mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }

    @Test
    void testEqualsAndHashCode() {
        BuilderCard card1 = new BuilderCard(Era.ERA_I, 3, 2);
        BuilderCard card2 = new BuilderCard(Era.ERA_I, 3, 2);
        BuilderCard card3 = new BuilderCard(Era.ERA_I, 5, 2);
        BuilderCard card4 = new BuilderCard(Era.ERA_I, 3, 1);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, card4);
        assertNotEquals(card1, null);
        assertNotEquals(card1, new Object());

        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }
}