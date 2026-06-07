package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class GathererCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting GathererCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending GathererCardTest ---");
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