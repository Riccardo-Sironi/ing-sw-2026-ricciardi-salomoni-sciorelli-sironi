package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArtistCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ArtistCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ArtistCardTest ---");
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
        Era excpectedEra = Era.ERA_I;
        ArtistCard card = new ArtistCard(excpectedEra);
        assertNotNull(card);
        assertEquals(excpectedEra, card.getEra());
    }

    @Test
    void testDefaultConstructor() {
        ArtistCard card = new ArtistCard();
        assertNotNull(card);
    }

    @Test
    void testAccept() {
        ArtistCard card = new ArtistCard(Era.ERA_I);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }

    @Test
    void testEquals() {
        ArtistCard card1 = new ArtistCard(Era.ERA_I);
        ArtistCard card2 = new ArtistCard(Era.ERA_II);
        GathererCard otherCard = new GathererCard(Era.ERA_I);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, null);
        assertNotEquals(card1, otherCard);
    }
}