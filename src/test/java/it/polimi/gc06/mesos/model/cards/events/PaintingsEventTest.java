package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaintingsEventTest {

    private Player player;
    private PaintingsEvent event;
    private ModifierBuildingsRegistry mockRegistry;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PaintingsEventTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PaintingsEventTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        player = mock(Player.class);
        mockRegistry = mock(ModifierBuildingsRegistry.class);
        ModifierBuildingCard foodGainBuilding = mock(ModifierBuildingCard.class);
        when(mockRegistry.get(ModifierBuildingRegistryKey.PAINTING_FOOD_GAIN_CARD)).thenReturn(foodGainBuilding);

        event = new PaintingsEvent(Era.ERA_I, 2, 3, 2) {
            @Override
            protected ModifierBuildingsRegistry getRegistry() {
                return mockRegistry;
            }
        };

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testSettersAndGetters() {
        PaintingsEvent event = new PaintingsEvent();
        assertEquals(-1, event.getPrestigeGain());
        assertEquals(-1, event.getPrestigeLoss());
        assertEquals(-1, event.getMinNumberOfArtists());

        event.setPrestigeGain(3);
        event.setPrestigeLoss(2);
        event.setMinNumberOfArtists(4);

        assertEquals(3, event.getPrestigeGain());
        assertEquals(2, event.getPrestigeLoss());
        assertEquals(4, event.getMinNumberOfArtists());
    }

    @Test
    void testResolveEvent_ArtistsBelowMin_LosesPrestige() {
        when(player.getArtistsCounter()).thenReturn(1);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        event.resolveEvent(player);

        verify(player).getArtistsCounter();
        verify(player).removePrestigeTokens(3);
        verify(player, never()).addPrestigeTokens(anyInt());
        verify(player).addFoodTokens(0);
    }

    @Test
    void testResolveEvent_ArtistsExactMin_GainsPrestige() {
        when(player.getArtistsCounter()).thenReturn(2);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        event.resolveEvent(player);

        verify(player).getArtistsCounter();
        verify(player, never()).removePrestigeTokens(anyInt());
        verify(player).addPrestigeTokens(4); // 2 artists * 2
        verify(player).addFoodTokens(0);
    }

    @Test
    void testResolveEvent_ArtistsAboveMin_GainsPrestige() {
        when(player.getArtistsCounter()).thenReturn(3);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        event.resolveEvent(player);

        verify(player).getArtistsCounter();
        verify(player, never()).removePrestigeTokens(anyInt());
        verify(player).addPrestigeTokens(6); // 3 artists * 2
        verify(player).addFoodTokens(0);
    }

    @Test
    void testResolveEvent_WithPaintingFoodGainBuilding() {
        when(player.getArtistsCounter()).thenReturn(2);
        ArrayList<it.polimi.gc06.mesos.model.cards.buildings.BuildingCard> buildings = new ArrayList<>();
        buildings.add(mockRegistry.get(ModifierBuildingRegistryKey.PAINTING_FOOD_GAIN_CARD));
        when(player.getBuildingCards()).thenReturn(buildings);

        event.resolveEvent(player);

        verify(player).getArtistsCounter();
        verify(player, never()).removePrestigeTokens(anyInt());
        verify(player).addPrestigeTokens(4); // 2 artists * 2
        verify(player).addFoodTokens(2); // Gains 2 food (equal to artists)
    }

    @Test
    void testResolveEvent_ZeroArtists() {
        when(player.getArtistsCounter()).thenReturn(0);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        event.resolveEvent(player);

        verify(player).getArtistsCounter();
        verify(player).removePrestigeTokens(3);
        verify(player, never()).addPrestigeTokens(anyInt());
        verify(player).addFoodTokens(0);
    }

    @Test
    void testAcceptCardVisitor() {
        CardVisitor visitor = mock(CardVisitor.class);
        event.accept(visitor);
        verify(visitor).visit(event);
    }

    @Test
    void testEqualsAndHashCode() {
        PaintingsEvent e1 = new PaintingsEvent(Era.ERA_I, 2, 3, 2);
        PaintingsEvent e2 = new PaintingsEvent(Era.ERA_I, 2, 3, 2);
        PaintingsEvent e3 = new PaintingsEvent(Era.ERA_I, 1, 3, 2);
        PaintingsEvent e4 = new PaintingsEvent(Era.ERA_I, 2, 1, 2);
        PaintingsEvent e5 = new PaintingsEvent(Era.ERA_I, 2, 3, 1);

        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, e4);
        assertNotEquals(e1, e5);
        assertNotEquals(e1, null);
        assertNotEquals(e1, new Object());

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }
}