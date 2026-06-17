package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TileSlotSnapshotTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TileSlotSnapshotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TileSlotSnapshotTest ---");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testRecordState() {
        String expectedNickname = "PlayerOne";
        TileEffect expectedEffect = mock(TileEffect.class);

        TileSlotSnapshot snapshot = new TileSlotSnapshot(expectedNickname, expectedEffect);

        assertEquals(expectedNickname, snapshot.playerNickname());
        assertEquals(expectedEffect, snapshot.tileEffect());
    }
}