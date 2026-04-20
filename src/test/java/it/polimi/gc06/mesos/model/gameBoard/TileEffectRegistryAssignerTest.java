package it.polimi.gc06.mesos.model.gameBoard;


import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class TileEffectRegistryAssignerTest {

    private ModifierBuildingsRegistry mockRegistry;
    private TileEffectRegistryAssigner assigner;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TileEffectRegistryAssignerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TileEffectRegistryAssignerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {

        mockRegistry = mock(ModifierBuildingsRegistry.class);
        assigner = new TileEffectRegistryAssigner(mockRegistry);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testVisitFoodTileEffect() {
        FoodTileEffect foodTileEffect = mock(FoodTileEffect.class);
        assigner.visit(foodTileEffect);

        verify(foodTileEffect, times(1)).setRegistry(mockRegistry);
    }

    @Test
    void testVisitRemoveFoodTileEffect() {
        RemoveFoodTileEffect removeFoodTileEffect = mock(RemoveFoodTileEffect.class);

        assertDoesNotThrow(() -> assigner.visit(removeFoodTileEffect));
        verifyNoInteractions(removeFoodTileEffect);
    }

    @Test
    void testVisitChooseCardTileEffect() {
        ChooseCardTileEffect chooseCardTileEffect = mock(ChooseCardTileEffect.class);

        assertDoesNotThrow(() -> assigner.visit(chooseCardTileEffect));
        verifyNoMoreInteractions(chooseCardTileEffect);
    }

    @Test
    void testVisitGenericTileEffect() {
        TileEffect genericTileEffect = mock(TileEffect.class);

        assertDoesNotThrow(() -> assigner.visit(genericTileEffect));
        verifyNoInteractions(genericTileEffect);
    }
}