package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HunterCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting HunterCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending HunterCardTest ---");
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
        Era expectedEra = Era.ERA_I;
        boolean expectedIconPresence = true;
        HunterCard card = new HunterCard(expectedEra, expectedIconPresence);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
        assertEquals(expectedIconPresence, card.hasFoodIcon());
    }

    @Test
    void testAccept() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        HunterFoodIconVisitor visitor = mock(HunterFoodIconVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}