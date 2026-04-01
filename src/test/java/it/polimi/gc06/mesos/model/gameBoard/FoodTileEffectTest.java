package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodTileEffectTest {

    private FoodTileEffect effect;
    private Player mockPlayer;
    private ModifierBuildingCard mockFoodBonusCard;
    private ModifierBuildingsRegistry mockRegistry;

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
    void setUp() {
        effect = new FoodTileEffect();
        effect.setNumFood(2);

        mockPlayer = mock(Player.class);
        mockFoodBonusCard = mock(ModifierBuildingCard.class);
        mockRegistry = mock(ModifierBuildingsRegistry.class);

        when(mockRegistry.get(ModifierBuildingRegistryKey.TILE_FOOD_BONUS)).thenReturn(mockFoodBonusCard);
        effect.setRegistry(mockRegistry);
    }

    @Test
    void testExecuteAddsNormalFood() {
        when(mockPlayer.getBuildingCards()).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).addFoodTokens(2);
    }

    @Test
    void testExecuteAddsBonusFood() {
        ArrayList<BuildingCard> deckWithBonus = new ArrayList<>();
        deckWithBonus.add(mockFoodBonusCard);
        when(mockPlayer.getBuildingCards()).thenReturn(deckWithBonus);

        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).addFoodTokens(3);
    }

    @Test
    void testExecuteThrowsExceptionWhenPlayerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> effect.execute(null));
    }

    @Test
    void testAccept() {
        TileEffectVisitor mockVisitor = mock(TileEffectVisitor.class);
        effect.accept(mockVisitor);
        verify(mockVisitor, times(1)).visit(effect);
    }
}