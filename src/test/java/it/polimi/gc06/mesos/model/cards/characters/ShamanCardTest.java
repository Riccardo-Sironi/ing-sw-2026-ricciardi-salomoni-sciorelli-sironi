package it.polimi.gc06.mesos.model.cards.characters;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShamanCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ShamanCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ShamanCardTest ---");
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
        int expectedStars = 2;
        ShamanCard card = new ShamanCard(expectedEra, expectedStars);
        assertNotNull(card);
        assertEquals(expectedEra, card.getEra());
        assertEquals(expectedStars, card.getStars());
    }

    @Test
    void testAccept() {
        ShamanCard card = new ShamanCard(Era.ERA_I, 2);
        CharactersSetsVisitor visitor = mock(CharactersSetsVisitor.class);
        card.accept(visitor);
        verify(visitor, times(1)).visit(card);
    }
}