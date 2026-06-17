package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HuntEventTest {

    private HuntEvent huntEvent;
    private ModifierBuildingCard mockBuildingCard;
    private Player player;
    private ModifierBuildingsRegistry mockRegistry;
    private final int prestigeGain = 2;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting HuntEventTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending HuntEventTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {

        mockBuildingCard = mock(ModifierBuildingCard.class);
        mockRegistry = mock(ModifierBuildingsRegistry.class);
        when(mockRegistry.get(ModifierBuildingRegistryKey.HUNT_PRESTIGE_AND_FOOD_GAIN_CARD)).thenReturn(mockBuildingCard);

        huntEvent = new HuntEvent(Era.ERA_I, prestigeGain) {
            @Override
            protected ModifierBuildingsRegistry getRegistry() {
                return mockRegistry;
            }
        };
        player = mock(Player.class);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testSettersAndGetters() {
        HuntEvent event = new HuntEvent();
        assertEquals(-1, event.getPrestigeGain());
        event.setPrestigeGain(5);
        assertEquals(5, event.getPrestigeGain());
    }

    @Test
    void testIsLastToBeResolvedIsFalse() {
        assertFalse(huntEvent.isLastToBeResolved());
    }

    @Test
    void testAcceptCallsVisitor() {
        CardVisitor visitor = mock(CardVisitor.class);
        huntEvent.accept(visitor);
        verify(visitor).visit(huntEvent);
    }

    @Test
    void testResolveEvent_NoHunters_NoTokensAdded() {
        when(player.getHuntersCounter()).thenReturn(0);

        huntEvent.resolveEvent(player);

        verify(player, never()).addFoodTokens(anyInt());
        verify(player, never()).addPrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_NegativeHunters_NoTokensAdded() {
        when(player.getHuntersCounter()).thenReturn(-1);

        huntEvent.resolveEvent(player);

        verify(player, never()).addFoodTokens(anyInt());
        verify(player, never()).addPrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_WithHunters_HasOtherBuildingCards() {
        int huntersCount = 2;
        when(player.getHuntersCounter()).thenReturn(huntersCount);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mock(ModifierBuildingCard.class)); // Adding a different mock
        when(player.getBuildingCards()).thenReturn(buildingCards);

        huntEvent.resolveEvent(player);

        verify(player).addFoodTokens(huntersCount);
        verify(player).addPrestigeTokens(huntersCount * prestigeGain);
    }

    @Test
    void testResolveEvent_NullModifierCardInConstructor() {
        HuntEvent nullModifierEvent = new HuntEvent(Era.ERA_II, prestigeGain) {
            @Override
            protected ModifierBuildingsRegistry getRegistry() {
                return mockRegistry;
            }
        };
        int huntersCount = 1;
        when(player.getHuntersCounter()).thenReturn(huntersCount);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        // should handle gracefully returning false on contains(null)
        nullModifierEvent.resolveEvent(player);

        verify(player).addFoodTokens(huntersCount);
        verify(player).addPrestigeTokens(huntersCount * prestigeGain);
    }

    @Test
    void testResolveEvent_ZeroPrestigeGain() {
        HuntEvent zeroPrestigeEvent = new HuntEvent(Era.ERA_I, 0) {
            @Override
            protected ModifierBuildingsRegistry getRegistry() {
                return mockRegistry;
            }
        };
        int huntersCount = 3;
        when(player.getHuntersCounter()).thenReturn(huntersCount);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        zeroPrestigeEvent.resolveEvent(player);

        verify(player).addFoodTokens(huntersCount);
        verify(player).addPrestigeTokens(0); // huntersCount * 0
    }

    @Test
    void testResolveEvent_WithHunters_WithoutBuildingCard() {
        int huntersCount = 3;
        when(player.getHuntersCounter()).thenReturn(huntersCount);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>()); // does not contain mockBuildingCard

        huntEvent.resolveEvent(player);

        verify(player).addFoodTokens(huntersCount);
        verify(player).addPrestigeTokens(huntersCount * prestigeGain);
    }

    @Test
    void testResolveEvent_WithHunters_WithBuildingCard() {
        int huntersCount = 3;
        when(player.getHuntersCounter()).thenReturn(huntersCount);
        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockBuildingCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        huntEvent.resolveEvent(player);

        verify(player).addFoodTokens(huntersCount * 2);
        verify(player).addPrestigeTokens(huntersCount * (prestigeGain + 1));
    }

    @Test
    void testAccept_With_NullVisitor() {
        assertThrows(NullPointerException.class, () -> {
            huntEvent.accept(null);
        });
    }

    @Test
    void testEqualsAndHashCode() {
        HuntEvent e1 = new HuntEvent(Era.ERA_I, 2);
        HuntEvent e2 = new HuntEvent(Era.ERA_I, 2);
        HuntEvent e3 = new HuntEvent(Era.ERA_I, 3);

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, new Object());

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }
}