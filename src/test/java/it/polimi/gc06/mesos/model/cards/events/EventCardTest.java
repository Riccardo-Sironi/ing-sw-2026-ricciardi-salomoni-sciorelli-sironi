package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EventCardTest {

    private static class DummyEventCard extends EventCard {

        private boolean isResolved = false;

        public DummyEventCard(Era era, boolean lastToBeResolved) {
            super(era, lastToBeResolved);
        }

        @Override
        public void resolveEvent(Player player) {
            this.isResolved = true;
        }

        public boolean isResolved() {
            return isResolved;
        }

        @Override
        public void accept(CardVisitor visitor) {
            // empty
        }
    }

    private DummyEventCard eventCard;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EventCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EventCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {

        eventCard = new DummyEventCard(Era.ERA_I, true);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testIsLastToBeResolved() {

        assertTrue(eventCard.isLastToBeResolved(), "should return true if we pass true to the constructor");

        DummyEventCard notLastCard = new DummyEventCard(Era.ERA_II, false);
        assertFalse(notLastCard.isLastToBeResolved(), "should return false if we pass false to the constructor");
    }

    @Test
    void testResolveEvent() {
        Player player = mock(Player.class);

        assertFalse(eventCard.isResolved(), "initially shouldn't be resolved");

        eventCard.resolveEvent(player);

        assertTrue(eventCard.isResolved(), "should be resolved after calling resolveEvent");
    }

    @Test
    void testSetRegistryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            eventCard.setRegistry(null);
        }, "should throw IllegalArgumentException");
    }

    @Test
    void testSetAndGetRegistry() {
        ModifierBuildingsRegistry modifierBuildingsRegistry = mock(ModifierBuildingsRegistry.class);

        eventCard.setRegistry(modifierBuildingsRegistry);

        assertSame(modifierBuildingsRegistry, eventCard.getRegistry(), "the returned registry should be the same");
    }

}