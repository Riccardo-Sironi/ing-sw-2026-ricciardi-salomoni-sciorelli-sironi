package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerSnapshotTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerSnapshotTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerSnapshotTest ---");
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
        String expectedNickname = "SnapshotPlayer";
        int expectedPrestige = 10;
        int expectedFood = 5;
        int expectedShamanStars = 2;
        int expectedTopDraw = 1;
        int expectedBottomDraw = 3;

        @SuppressWarnings("unchecked")
        Map expectedCharacterDeck = mock(Map.class);
        @SuppressWarnings("unchecked")
        List<BuildingCard> expectedBuildingDeck = mock(List.class);
        @SuppressWarnings("unchecked")
        Map expectedCharacterSets = mock(Map.class);
        @SuppressWarnings("unchecked")
        Map expectedInventorPairs = mock(Map.class);

        PlayerSnapshot snapshot = new PlayerSnapshot(
                expectedNickname,
                null,
                expectedPrestige,
                expectedFood,
                expectedShamanStars,
                expectedTopDraw,
                expectedBottomDraw,
                expectedCharacterDeck,
                expectedBuildingDeck,
                expectedCharacterSets,
                expectedInventorPairs
        );

        assertEquals(expectedNickname, snapshot.nickname());
        assertNull(snapshot.color());
        assertEquals(expectedPrestige, snapshot.prestigeTokens());
        assertEquals(expectedFood, snapshot.foodTokens());
        assertEquals(expectedShamanStars, snapshot.shamanStars());
        assertEquals(expectedTopDraw, snapshot.topDrawNum());
        assertEquals(expectedBottomDraw, snapshot.bottomDrawNum());
        assertEquals(expectedCharacterDeck, snapshot.characterDeck());
        assertEquals(expectedBuildingDeck, snapshot.buildingDeck());
        assertEquals(expectedCharacterSets, snapshot.charactersSets());
        assertEquals(expectedInventorPairs, snapshot.inventorPairs());
    }
}