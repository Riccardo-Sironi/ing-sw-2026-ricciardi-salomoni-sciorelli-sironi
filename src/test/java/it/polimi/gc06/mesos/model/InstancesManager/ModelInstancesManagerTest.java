package it.polimi.gc06.mesos.model.InstancesManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ModelInstancesManagerTest {

    private ModelInstancesManager manager;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ModelInstancesManagerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ModelInstancesManagerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        manager = new ModelInstancesManager(new DTONotifier());
        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    // --- STANDARD VALIDATION TESTS ---

    @Test
    @DisplayName("Creates game with valid number of players (2-5)")
    void testCreateGame_ValidNumOfPlayers() {
        assertDoesNotThrow(() -> {
            List<String> num_player_2 = new ArrayList<>(Arrays.asList("p1", "p2"));
            GameModel model_2_players = manager.createGame(num_player_2);
            assertNotNull(model_2_players);
            assertEquals(2, model_2_players.getPlayers().size());

            List<String> num_players_3 = new ArrayList<>(Arrays.asList("p1", "p2", "p3"));
            GameModel model_3_players = manager.createGame(num_players_3);
            assertEquals(3, model_3_players.getPlayers().size());

            List<String> num_players_4 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4"));
            GameModel model_4_players = manager.createGame(num_players_4);
            assertEquals(4, model_4_players.getPlayers().size());

            List<String> num_players_5 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4", "p5"));
            GameModel model_5_players = manager.createGame(num_players_5);
            assertEquals(5, model_5_players.getPlayers().size());
        });
    }

    @Test
    @DisplayName("Throws exception if nicknames list is empty")
    void testCreateGame_ZeroPlayers_ThrowsException() {
        List<String> num_player_0 = new ArrayList<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> manager.createGame(num_player_0));
        assertEquals("Nicknames list is empty", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception if nicknames list has more than 5 players")
    void testCreateGame_TooManyPlayers_ThrowsException() {
        List<String> num_players_6 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4", "p5", "p6"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> manager.createGame(num_players_6));
        assertEquals("Nicknames list size should be between 2 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception if nicknames list is null")
    void testCreateGame_NullNicknames_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> manager.createGame(null));
        assertEquals("Nicknames list is null", exception.getMessage());
    }

    @Test
    @DisplayName("Throws exception if nicknames list has less than 2 players")
    void testCreateGame_OnePlayer_ThrowsException() {
        List<String> num_players_1 = new ArrayList<>(List.of("p1"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> manager.createGame(num_players_1));
        assertEquals("Nicknames list size should be between 2 and 5", exception.getMessage());
    }

    // --- BRANCH COVERAGE TESTS ---

    @Test
    @DisplayName("Branch coverage: TileSlot with both non-null and null TileEffect in offer track")
    void testCreateGame_OfferTrack_BranchCoverage() throws Exception {
        Map<String, List<Card>> cardMap = new HashMap<>();
        cardMap.put("2", new ArrayList<>());

        List<ModifierBuildingCard> modifierCards = new ArrayList<>();

        Map<String, ArrayList<TileSlot>> turnOrderTileConfig = new HashMap<>();
        TileSlot turnOrderSlot = mock(TileSlot.class);
        when(turnOrderSlot.getTileEffect()).thenReturn(mock(TileEffect.class));
        turnOrderTileConfig.put("2", new ArrayList<>(List.of(turnOrderSlot)));

        Map<String, ArrayList<TileSlot>> offerTrackConfig = new HashMap<>();
        TileSlot offerSlotWithEffect = mock(TileSlot.class);
        when(offerSlotWithEffect.getTileEffect()).thenReturn(mock(TileEffect.class));

        TileSlot offerSlotWithoutEffect = mock(TileSlot.class);
        when(offerSlotWithoutEffect.getTileEffect()).thenReturn(null);

        offerTrackConfig.put("2", new ArrayList<>(Arrays.asList(offerSlotWithEffect, offerSlotWithoutEffect)));

        try (MockedConstruction<ObjectMapper> mockedMapper = mockConstruction(ObjectMapper.class, (mock, context) -> {
            when(mock.readValue(any(InputStream.class), any(TypeReference.class)))
                    .thenReturn(cardMap)
                    .thenReturn(modifierCards)
                    .thenReturn(turnOrderTileConfig)
                    .thenReturn(offerTrackConfig);
        })) {
            List<String> nicknames = new ArrayList<>(Arrays.asList("p1", "p2"));
            GameModel model = manager.createGame(nicknames);

            assertNotNull(model);
            verify(offerSlotWithEffect, times(2)).getTileEffect();
            verify(offerSlotWithoutEffect, times(1)).getTileEffect();
        }
    }
}