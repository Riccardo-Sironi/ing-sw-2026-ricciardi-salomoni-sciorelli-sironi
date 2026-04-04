package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;

public class ModelInstancesManagerTest {

    private ModelInstancesManager manager;

    @BeforeEach
    void setUp() {
        manager = new ModelInstancesManager();
    }

    @Test
    void testCreateGame_ValidNumOfPlayers() {
        try (MockedConstruction<Board> mockedBoard = mockConstruction(Board.class)) {
            GameModel model1 = manager.createGame(2);
            assertNotNull(model1);
            assertEquals(2, model1.getPlayers().size());

            GameModel model2 = manager.createGame(3);
            assertNotNull(model2);
            assertEquals(3, model2.getPlayers().size());

            GameModel model3 = manager.createGame(4);
            assertNotNull(model3);
            assertEquals(4, model3.getPlayers().size());

            GameModel model4 = manager.createGame(5);
            assertNotNull(model4);
            assertEquals(5, model4.getPlayers().size());
        } catch (Exception e) {
            fail("Exception should not be thrown for valid number of players (2-5): " + e.getMessage());
        }
    }

    @Test
    void testCreateGame_ZeroPlayers_ThrowsException() {
        // numOfPlayers = 0 -> turnOrderTileConfig won't have a key "0" -> new TurnOrderTile(null) -> throws NPE or similar
        Exception exception = assertThrows(Exception.class, () -> {
            manager.createGame(0);
        });
        // We know it probably throws NPE or JsonMappingException/IllegalArgumentException deep down
        assertNotNull(exception);
    }

    @Test
    void testCreateGame_NegativePlayers_ThrowsException() {
        assertThrows(Exception.class, () -> {
            manager.createGame(-1);
        });
    }

    @Test
    void testCreateGame_TooManyPlayers_ThrowsException() {
        // There are exactly 5 colors in the Color enum.
        // If numOfPlayers = 6, it will throw an exception (NoSuchElementException, IndexOutOfBoundsException, or equivalent).
        assertThrows(Exception.class, () -> manager.createGame(6));
    }
}
