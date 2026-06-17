package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventorCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting InventorCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending InventorCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        System.out.println("[START] " + testInfo.getDisplayName());
    }


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
    void testDefaultConstructorAndSetters() {
        InventorCard card = new InventorCard();
        assertNull(card.getIcon());

        card.setIcon(InventionIcon.ROPE);
        assertEquals(InventionIcon.ROPE, card.getIcon());
    }

    @Test
    void testAccept() {
        InventorCard card = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        InventorPairsVisitor visitor = mock(InventorPairsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }

    @Test
    void testEqualsAndHashCode() {
        InventorCard card1 = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        InventorCard card2 = new InventorCard(Era.ERA_I, InventionIcon.BOAT);
        InventorCard card3 = new InventorCard(Era.ERA_I, InventionIcon.ROPE);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertNotEquals(card1, new Object());

        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }
}