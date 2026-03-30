package it.polimi.gc06.mesos.model.cards.events.unit;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.events.HuntEvent;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class HuntEventTest {

    private HuntEvent huntEvent;
    private ModifierBuildingCard mockBuildingCard;
    private Player player;
    private final int prestigeGain = 2;

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting HuntEventTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending HuntEventTest <<<");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        mockBuildingCard = mock(ModifierBuildingCard.class);
        huntEvent = new HuntEvent(Era.ERA_I, prestigeGain, mockBuildingCard);
        player = mock(Player.class);
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    void testIsLastToBeResolvedIsFalse() {
        assertFalse(huntEvent.isLastToBeResolved());
    }

    @Test
    void testAcceptCallsVisitor() {
        TribeCardVisitor visitor = mock(TribeCardVisitor.class);
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
        HuntEvent nullModifierEvent = new HuntEvent(Era.ERA_II, prestigeGain, null);
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
        HuntEvent zeroPrestigeEvent = new HuntEvent(Era.ERA_I, 0, mockBuildingCard);
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
        // The visitor should not be null, thus, a NullPointerException is expected when trying to accept a null visitor
        // In order to enforce this, the parameter is marked as @NotNull, throwing a compile warning if a null visitor is passed.
        assertThrows(NullPointerException.class, () -> {
            huntEvent.accept(null);
        });
    }
}
