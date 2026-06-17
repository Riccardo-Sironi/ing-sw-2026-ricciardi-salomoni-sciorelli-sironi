package it.polimi.gc06.mesos.dtos.snapshots;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerSnapshotTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TurnManagerSnapshotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TurnManagerSnapshotTest ---");
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
        int expectedRound = 3;
        int expectedIndex = 1;
        List<String> expectedPlayers = List.of("Player1", "Player2");

        TurnManagerSnapshot snapshot = new TurnManagerSnapshot(expectedRound, expectedIndex, expectedPlayers);

        assertEquals(expectedRound, snapshot.round());
        assertEquals(expectedIndex, snapshot.activePlayerIndex());
        assertEquals(expectedPlayers, snapshot.playersOrderNicknames());
    }
}