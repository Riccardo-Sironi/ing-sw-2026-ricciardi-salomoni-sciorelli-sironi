package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

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

        manager = new ModelInstancesManager();

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }


    @Test
    void testCreateGame_ValidNumOfPlayers() {
        try (MockedConstruction<Board> mockedBoard = mockConstruction(Board.class)) {

        assertDoesNotThrow(() -> {
            // 2 players
            List<String> num_player_2 = new ArrayList<>(Arrays.asList("p1", "p2"));
            GameModel model_2_players = manager.createGame(num_player_2);
            assertNotNull(model_2_players);

            assertEquals(2, model_2_players.getPlayers().size());

            //3 players
            List<String> num_players_3 = new ArrayList<>(Arrays.asList("p1", "p2", "p3"));
            GameModel model_3_players = manager.createGame(num_players_3);
            assertNotNull(model_3_players);

            assertEquals(3, model_3_players.getPlayers().size());

            //4 players
            List<String> num_players_4 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4"));
            GameModel model_4_players = manager.createGame(num_players_4);
            assertNotNull(model_4_players);

            assertEquals(4, model_4_players.getPlayers().size());

            //5 players
            List<String> num_players_5 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4", "p5"));
            GameModel model_5_players = manager.createGame(num_players_5);
            assertNotNull(model_5_players);

            assertEquals(5, model_5_players.getPlayers().size());

        });
        } catch (Exception e) {
            fail("Exception should not be thrown for valid number of players (2-5): " + e.getMessage());
        }
    }

    @Test
    void testCreateGame_ZeroPlayers_ThrowsException() {
        // numOfPlayers = 0 -> turnOrderTileConfig won't have a key "0" -> new TurnOrderTile(null) -> throws NPE or similar
        List<String> num_player_0 = new ArrayList<>();
        assertThrows(Exception.class, () -> manager.createGame(num_player_0));
    }

    @Test
    void testCreateGame_TooManyPlayers_ThrowsException() {
        // There are exactly 5 colors in the Color enum.
        // If numOfPlayers = 6, it will throw an exception (NoSuchElementException, IndexOutOfBoundsException, or equivalent).
        List<String> num_players_6 = new ArrayList<>(Arrays.asList("p1", "p2", "p3", "p4", "p5"));

        assertThrows(Exception.class, () -> manager.createGame(num_players_6), "6 or more player should throw exception");
    }
}
