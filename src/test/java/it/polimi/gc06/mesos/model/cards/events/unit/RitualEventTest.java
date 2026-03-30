package it.polimi.gc06.mesos.model.cards.events.unit;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameInfo;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class RitualEventTest {

    private RitualEvent ritualEvent;
    private ModifierBuildingCard mockNoLossCard;
    private ModifierBuildingCard mockDoubleWinCard;
    private Player player;
    private GameInfo gameInfo;

    private final int numPrestigeGained = 3;
    private final int numPrestigeLost = 2;

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting RitualEventTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending RitualEventTest <<<");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        mockNoLossCard = mock(ModifierBuildingCard.class);
        mockDoubleWinCard = mock(ModifierBuildingCard.class);
        ritualEvent = new RitualEvent(Era.ERA_II, numPrestigeGained, numPrestigeLost, mockNoLossCard, mockDoubleWinCard);

        player = mock(Player.class);
        gameInfo = mock(GameInfo.class);
        when(player.getEnvironment()).thenReturn(gameInfo);

        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    void testIsLastToBeResolvedIsFalse() {
        assertFalse(ritualEvent.isLastToBeResolved());
    }

    @Test
    void testAcceptCallsVisitor() {
        TribeCardVisitor visitor = mock(TribeCardVisitor.class);
        ritualEvent.accept(visitor);
        verify(visitor).visit(ritualEvent);
    }

    @Test
    void testAccept_With_NullVisitor() {
        assertThrows(NullPointerException.class, () -> {
            ritualEvent.accept(null);
        });
    }

    @Test
    void testResolveEvent_PlayerNotMinNotMax_NoTokensChanged() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(5);

        ritualEvent.resolveEvent(player);

        verify(player, never()).addPrestigeTokens(anyInt());
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_PlayerIsMax_NotSoloMax_NoBuilding_AddsNormalPrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(10);
        // Not the only player with the highest amount of stars
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(2);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        verify(player).addPrestigeTokens(numPrestigeGained);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_PlayerIsMax_SoloMax_NoBuilding_AddsNormalPrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(10);
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(1); // solo max

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        // Does not have doubleWinCard, so normal gain
        verify(player).addPrestigeTokens(numPrestigeGained);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_PlayerIsMax_SoloMax_WithDoubleWinBuilding_AddsDoublePrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(10);
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(1); // solo max

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockDoubleWinCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        verify(player).addPrestigeTokens(numPrestigeGained * 2);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_PlayerIsMax_NotSoloMax_WithDoubleWinBuilding_AddsNormalPrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(10);
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(3); // multiple players tied for max

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockDoubleWinCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        // Tied for max, so ignores doubleWinCard effect
        verify(player).addPrestigeTokens(numPrestigeGained);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_PlayerIsMin_NoNoLossBuilding_RemovesPrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(2);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        verify(player, never()).addPrestigeTokens(anyInt());
        verify(player).removePrestigeTokens(numPrestigeLost);
    }

    @Test
    void testResolveEvent_PlayerIsMin_WithNoLossBuilding_RemovesZeroPrestige() {
        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(2);
        when(player.getShamanStars()).thenReturn(2);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockNoLossCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        verify(player, never()).addPrestigeTokens(anyInt());
        // verify parameter 0 was passed since they have the noLossCard
        verify(player).removePrestigeTokens(0);
    }

    @Test
    void testResolveEvent_PlayerIsBothMinAndMax_Solo_NoBuildings_EdgeCase() {
        when(gameInfo.getMaxStars()).thenReturn(5);
        when(gameInfo.getMinStars()).thenReturn(5);
        when(player.getShamanStars()).thenReturn(5);
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(1); // 1 player game edge case

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        when(player.getBuildingCards()).thenReturn(buildingCards);

        ritualEvent.resolveEvent(player);

        verify(player).addPrestigeTokens(numPrestigeGained);
        verify(player).removePrestigeTokens(numPrestigeLost);
    }

    @Test
    void testResolveEvent_NullModifierCardsInConstructor_DoesNotThrowWhenNoCards() {
        RitualEvent nullModifierEvent = new RitualEvent(Era.ERA_I, 4, 3, null, null);

        when(gameInfo.getMaxStars()).thenReturn(10);
        when(gameInfo.getMinStars()).thenReturn(0);
        when(player.getShamanStars()).thenReturn(10);
        when(gameInfo.getNumPlayerMaxStars()).thenReturn(1);

        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        // This should not throw NullPointerException because List.contains(null) politely returns false
        nullModifierEvent.resolveEvent(player);

        verify(player).addPrestigeTokens(4);
        verify(player, never()).removePrestigeTokens(anyInt());
    }
}
