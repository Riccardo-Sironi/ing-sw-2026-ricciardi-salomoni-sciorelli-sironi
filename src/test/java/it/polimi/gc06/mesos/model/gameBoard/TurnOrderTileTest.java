package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnOrderTileTest {

    private TurnOrderTile turnOrderTile;
    private ArrayList<TileSlot> slots;
    private TileSlot slot1;
    private TileSlot slot2;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TurnOrderTileTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TurnOrderTileTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {

        slots = new ArrayList<>();
        slot1 = mock(TileSlot.class);
        slot2 = mock(TileSlot.class);
        slots.add(slot1);
        slots.add(slot2);
        turnOrderTile = new TurnOrderTile(slots);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testConstructorAndAccessor() {

        assertThrows(IllegalArgumentException.class, () -> new TurnOrderTile(null)
        , "should throw IllegalArgumentException if slots is null");

        assertEquals(slots, turnOrderTile.slots(), "record's accessor should return the slot's list");
    }

    @Test
    void testAddSlots() {
        TileSlot newSlot = mock(TileSlot.class);

        turnOrderTile.addSlot(1,  newSlot);

        assertEquals(3, turnOrderTile.slots().size(),
                "should return 3 slots (slot1, slot2 and newSlot)");

        assertEquals(newSlot, turnOrderTile.slots().get(1), "slot must be added at the specified index");
    }

    @Test
    void testGetPlayerOnNthTileSuccess() {
        Player mockPlayer = mock(Player.class);
        when(slot2.getPlayer()).thenReturn(mockPlayer);

        Player retrieved = turnOrderTile.getPlayerOnNthTile(1);

        assertEquals(mockPlayer, retrieved,
                "should return the correct player in the slot");

        verify(slot2, times(1)).getPlayer();
    }

    @Test
    void testGetPlayerOnNthTileOutOfBounds() {

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.getPlayerOnNthTile(-1));

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.getPlayerOnNthTile(2));
    }

    @Test
    void testSetPlayerOnNthTileSuccess() {
        Player mockPlayer = mock(Player.class);

        turnOrderTile.setPlayerOnNthTile(mockPlayer, 0);

        verify(slot1, times(1)).setPlayer(mockPlayer);
    }

    @Test
    void testSetPlayerOnNthTileExceptions() {
        Player mockPlayer = mock(Player.class);

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.setPlayerOnNthTile(mockPlayer, -1),
                "should throw IllegalArgumentException for negative index");

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.setPlayerOnNthTile(null, 0),
                "should throw IllegalArgumentException for null player");

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.setPlayerOnNthTile(mockPlayer, 5),
                "should throw IllegalArgumentException index > size of slots");

        assertThrows(IllegalArgumentException.class, () -> turnOrderTile.setPlayerOnNthTile(mockPlayer, 2),
                "should throw IllegalArgumentException index > size of slots");
    }
}