package it.polimi.gc06.mesos.model.cards.buildings;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ModifierBuildingsRegistryTest {

    private ModifierBuildingsRegistry registry;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        registry = new ModifierBuildingsRegistry();

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testRegisterAndGet(){
        ModifierBuildingCard card = new ModifierBuildingCard();
        card.setCardKey(ModifierBuildingRegistryKey.RITUAL_NO_LOSS_CARD);

        registry.register(card);

        assertEquals(card, registry.get(ModifierBuildingRegistryKey.RITUAL_NO_LOSS_CARD));
        assertNull(registry.get(ModifierBuildingRegistryKey.TILE_FOOD_BONUS));
    }
}