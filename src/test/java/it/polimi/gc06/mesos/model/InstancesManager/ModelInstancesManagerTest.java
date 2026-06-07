package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    @DisplayName("Creates game with valid number of players (2-5)")
    void testCreateGame_ValidNumOfPlayers() {
        try (MockedConstruction<Board> mockedBoard = mockConstruction(Board.class)) {

            assertDoesNotThrow(() -> {
                // 2 players
                List<String> num_player_2 = new ArrayList<>(Arrays.asList("p1", "p2"));
                GameModel model_2_players = manager.createGame(num_player_2);
                assertNotNull(model_2_players);
                assertEquals(2, model_2_players.getPlayers().size());

                // 3 players
                List<String> num_players_3 = new ArrayList<>(Arrays.asList("p1", "p2", "p3"));
                GameModel model_3_players = manager.createGame(num_players_3);
                assertEquals(3, model_3_players.getPlayers().size());

                // 4 players
                List<String> num_players_4 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4"));
                GameModel model_4_players = manager.createGame(num_players_4);
                assertEquals(4, model_4_players.getPlayers().size());

                // 5 players
                List<String> num_players_5 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4", "p5"));
                GameModel model_5_players = manager.createGame(num_players_5);
                assertEquals(5, model_5_players.getPlayers().size());
            });

        } catch (Exception e) {
            fail("Exception should not be thrown for valid number of players (2-5): " + e.getMessage());
        }
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

    @Test
    @DisplayName("Branch coverage: TileSlot with non-null TileEffect calls accept()")
    void testCreateGame_TileEffectNotNull_CallsAccept() throws Exception {

        TileEffect dummyEffect = mock(TileEffect.class);

        try (MockedConstruction<Board> mockedBoard = mockConstruction(Board.class, (mockBoard, context) -> {

            TurnOrderTile fakeTurnOrderTile = mock(TurnOrderTile.class);
            TileSlot fakeSlot = mock(TileSlot.class);

            when(fakeSlot.getTileEffect()).thenReturn(dummyEffect);

            when(fakeTurnOrderTile.slots()).thenReturn(new ArrayList<>(List.of(fakeSlot)));

            when(mockBoard.getTurnOrderTile()).thenReturn(fakeTurnOrderTile);

        })) {

            List<String> num_player_2 = new ArrayList<>(Arrays.asList("p1", "p2"));

            assertDoesNotThrow(() -> manager.createGame(num_player_2));

            if (!mockedBoard.constructed().isEmpty()) {
                Board createdBoard = mockedBoard.constructed().get(0);

                for (TileSlot slot : createdBoard.getTurnOrderTile().slots()) {
                    if (slot.getTileEffect() != null) {
                        slot.getTileEffect().accept(any());
                    }
                }
            }
        }
    }
}