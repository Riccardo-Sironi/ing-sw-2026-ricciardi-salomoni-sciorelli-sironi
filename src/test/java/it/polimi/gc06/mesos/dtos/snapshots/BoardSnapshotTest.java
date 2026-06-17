package it.polimi.gc06.mesos.dtos.snapshots;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardSnapshotTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BoardSnapshotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BoardSnapshotTest ---");
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
        boolean expectedEndGame = false;

        @SuppressWarnings("unchecked")
        List topRow = mock(List.class);
        @SuppressWarnings("unchecked")
        List bottomRow = mock(List.class);
        @SuppressWarnings("unchecked")
        List topBuildings = mock(List.class);
        @SuppressWarnings("unchecked")
        List bottomBuildings = mock(List.class);
        @SuppressWarnings("unchecked")
        Map buildingsDecks = mock(Map.class);
        @SuppressWarnings("unchecked")
        List turnOrderSlots = mock(List.class);
        @SuppressWarnings("unchecked")
        List offerTrack = mock(List.class);

        BoardSnapshot snapshot = new BoardSnapshot(
                null,
                expectedEndGame,
                topRow,
                bottomRow,
                topBuildings,
                bottomBuildings,
                buildingsDecks,
                turnOrderSlots,
                offerTrack
        );

        assertNull(snapshot.currentEra());
        assertEquals(expectedEndGame, snapshot.isEndGame());
        assertEquals(topRow, snapshot.topRow());
        assertEquals(bottomRow, snapshot.bottomRow());
        assertEquals(topBuildings, snapshot.topBuildings());
        assertEquals(bottomBuildings, snapshot.bottomBuildings());
        assertEquals(buildingsDecks, snapshot.buildingsDecks());
        assertEquals(turnOrderSlots, snapshot.turnOrderTileSlots());
        assertEquals(offerTrack, snapshot.offerTrack());
    }
}