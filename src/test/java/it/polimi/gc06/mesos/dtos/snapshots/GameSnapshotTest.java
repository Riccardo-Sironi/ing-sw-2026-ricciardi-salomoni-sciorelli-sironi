package it.polimi.gc06.mesos.dtos.snapshots;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSnapshotTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting GameSnapshotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending GameSnapshotTest ---");
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
        @SuppressWarnings("unchecked")
        List<PlayerSnapshot> expectedPlayers = mock(List.class);
        BoardSnapshot expectedBoard = mock(BoardSnapshot.class);
        TurnManagerSnapshot expectedTurnManager = mock(TurnManagerSnapshot.class);
        @SuppressWarnings("unchecked")
        Map expectedTribeDecks = mock(Map.class);
        @SuppressWarnings("unchecked")
        List expectedEvents = mock(List.class);

        GameSnapshot snapshot = new GameSnapshot(
                expectedPlayers,
                expectedBoard,
                expectedTurnManager,
                expectedTribeDecks,
                expectedEvents
        );

        assertEquals(expectedPlayers, snapshot.players());
        assertEquals(expectedBoard, snapshot.board());
        assertEquals(expectedTurnManager, snapshot.turnManager());
        assertEquals(expectedTribeDecks, snapshot.tribeCardsDeck());
        assertEquals(expectedEvents, snapshot.finalEventCards());
    }
}