package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
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
    void testDefaultConstructor() {
        GathererCard card = new GathererCard();
        assertNotNull(card);
    }

    @Test
    void testAccept() {
        GathererCard card = new GathererCard(Era.ERA_I);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }

    @Test
    void testEquals() {
        GathererCard card1 = new GathererCard(Era.ERA_I);
        GathererCard card2 = new GathererCard(Era.ERA_II);
        ArtistCard otherCard = new ArtistCard(Era.ERA_I);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, null);
        assertNotEquals(card1, otherCard);
    }
}