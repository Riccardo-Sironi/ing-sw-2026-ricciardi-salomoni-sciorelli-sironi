package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.AddToBuildingsVisitor;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.*;

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
    void setUp(TestInfo testInfo)  {
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
    void testAccept() {
        ArtistCard card = new ArtistCard(Era.ERA_I);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}