package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BuildingCardTest {

    private BuildingCard emptyCard;
    private BuildingCard completeCard;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BuildingCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BuildingCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        emptyCard = new BuildingCard() {
            @Override
            public void accept(CardVisitor visitor) {
            }
        };

        completeCard = new BuildingCard(Era.ERA_I, 10, 5) {
            @Override
            public void accept(CardVisitor visitor) {}
        };

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testConstructorAndGetters() {
        assertNull(emptyCard.getEra());
        assertEquals(-1, emptyCard.getPrestigeGain(mock(Player.class)));
        assertEquals(-1, emptyCard.getBasePrestigeGain());
        assertEquals(-1, emptyCard.getFoodCost());

        assertEquals(Era.ERA_I, completeCard.getEra());
        assertEquals(10, completeCard.getPrestigeGain(mock(Player.class)));
        assertEquals(10, completeCard.getBasePrestigeGain());
        assertEquals(5, completeCard.getFoodCost());
    }

    @Test
    void testEraSetter() {
        assertDoesNotThrow(() -> emptyCard.setEra(Era.ERA_II));
        assertEquals(Era.ERA_II, emptyCard.getEra(), "Era in emptyCard should be ERA_II");

        assertThrows(IllegalArgumentException.class, () -> emptyCard.setEra(null));
        assertThrows(IllegalStateException.class, () -> emptyCard.setEra(Era.ERA_III));
    }

    @Test
    void testFoodCostSetter(){
        assertDoesNotThrow(() -> emptyCard.setFoodCost(3));
        assertEquals(3, emptyCard.getFoodCost());

        assertThrows(IllegalArgumentException.class, () -> emptyCard.setFoodCost(-1));
        assertThrows(IllegalStateException.class, () -> emptyCard.setFoodCost(4));
    }

    @Test
    void testPrestigeSetter(){
        assertDoesNotThrow(() -> emptyCard.setPrestigeGain(3));
        assertEquals(3, emptyCard.getPrestigeGain(mock(Player.class)));

        assertThrows(IllegalArgumentException.class, () -> emptyCard.setPrestigeGain(-1));
        assertThrows(IllegalStateException.class, () -> emptyCard.setPrestigeGain(9));
    }
}