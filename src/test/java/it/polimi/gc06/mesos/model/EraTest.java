package it.polimi.gc06.mesos.model;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EraTest {
    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EraTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EraTest ---");
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
    void testERAI() {
        assertEquals(Era.ERA_II, Era.ERA_I.nextEra(), "ERA_II is the next of ERA_I");
    }

    @Test
    void testERAII() {
        assertEquals(Era.ERA_III, Era.ERA_II.nextEra(), "ERA_III is the next of ERA_II");
    }

    @Test
    void testERAIII() {
        assertThrows(IllegalStateException.class, () -> Era.ERA_III.nextEra());
    }
}