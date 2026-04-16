package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TileSlotTest {

    private TileSlot tileSlot;
    private Player player;
    private TileEffect tileEffect;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TileSlotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TileSlotTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {

        tileSlot = new TileSlot();
        player = mock(Player.class);
        tileEffect = mock(TileEffect.class);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testInitialState() {
        assertNull(tileSlot.getPlayer(), "player should be null initially");
        assertNull(tileSlot.getTileEffect(), "tile effect should be null initially");
        assertTrue(tileSlot.isEmpty(), "tile slot should be empty initially");
    }

    @Test
    void testSetPlayerSuccess() {
        tileSlot.setPlayer(player);

        assertEquals(player, tileSlot.getPlayer(), "player should be returned");
        assertFalse(tileSlot.isEmpty(), "tile slot should not be empty after setting a player");
    }

    @Test
    void testSetPlayerThrowsExceptionWhenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            tileSlot.setPlayer(null);
        }, "should throw IllegalArgumentException when player is null");
    }

    @Test
    void testSetPlayerThrowsExceptionWhenAlreadyOccupied() {
        tileSlot.setPlayer(player);
        Player anotherPlayer = mock(Player.class);

        assertThrows(IllegalStateException.class, () -> {
            tileSlot.setPlayer(anotherPlayer);
        }, "should throw IllegalStateException when tile slot is already occupied");
    }

    @Test
    void testSetAndGetTileEffect(){
        tileSlot.setTileEffect(tileEffect);

        assertEquals(tileEffect, tileSlot.getTileEffect(), "tile effect should be returned");
    }

    @Test
    void testApplyEffectSuccess(){
        tileSlot.setPlayer(player);
        tileSlot.setTileEffect(tileEffect);

        tileSlot.applyEffect();

        verify(tileEffect, times(1)).execute(player);
    }

    @Test
    void testApplyNullEffect() {
        tileSlot.setPlayer(player);
        tileSlot.setTileEffect(null);

        assertDoesNotThrow(() -> {
            tileSlot.applyEffect();
        }, "should not apply any effect when tile effect is null");

        verifyNoInteractions(tileEffect);
    }

    @Test
    void testApplyEffectThrowsExceptionWhenEmptySlot() {
        assertThrows(IllegalStateException.class, () -> {
            tileSlot.applyEffect();
        }, "should throw IllegalStateException when empty slot");
    }

    @Test
    void testRemovePlayerSuccess(){
        tileSlot.setPlayer(player);

        Player removedPlayer = tileSlot.removePlayer();

        assertEquals(player, removedPlayer, "should return the player removed from the tile");
        assertTrue(tileSlot.isEmpty(), "tile slot should be empty after removing the player");
        assertNull(tileSlot.getPlayer(), "player should be null initially");
    }

    @Test
    void testRemovePlayerThrowsExceptionWhenEmptySlot() {
        assertThrows(IllegalStateException.class, () -> {
            tileSlot.removePlayer();
        }, "should throw IllegalStateException when trying to remove a player in an empty slot");
    }
}