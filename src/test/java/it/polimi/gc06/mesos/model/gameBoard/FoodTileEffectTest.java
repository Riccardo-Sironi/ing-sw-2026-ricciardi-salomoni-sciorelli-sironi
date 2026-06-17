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
        System.out.println("--- Starting FoodTileEffectTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending FoodTileEffectTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        effect = new FoodTileEffect();
        effect.setNumFood(2);

        mockPlayer = mock(Player.class);
        mockFoodBonusCard = mock(ModifierBuildingCard.class);
        mockRegistry = mock(ModifierBuildingsRegistry.class);

        when(mockRegistry.get(ModifierBuildingRegistryKey.TILE_FOOD_BONUS)).thenReturn(mockFoodBonusCard);
        effect.setRegistry(mockRegistry);

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testGettersAndSetters() {
        FoodTileEffect e1 = new FoodTileEffect(5);
        assertEquals(5, e1.getNumFood());

        FoodTileEffect e2 = new FoodTileEffect();
        e2.setNumFood(3);
        assertEquals(3, e2.getNumFood());
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

    @Test
    void testEqualsAndHashCode() {
        FoodTileEffect e1 = new FoodTileEffect(2);
        FoodTileEffect e2 = new FoodTileEffect(2);
        FoodTileEffect e3 = new FoodTileEffect(3);

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, new Object());

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }
}