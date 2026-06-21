package it.polimi.gc06.mesos.model.gameBoard;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

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

        turnOrderTile.addSlot(1, newSlot);

        assertEquals(3, turnOrderTile.slots().size(),
                "should return 3 slots (slot1, slot2 and newSlot)");

        assertEquals(newSlot, turnOrderTile.slots().get(1), "slot must be added at the specified index");
    }
}