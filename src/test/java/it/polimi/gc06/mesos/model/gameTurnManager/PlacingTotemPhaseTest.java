package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import org.junit.jupiter.api.*;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlacingTotemPhaseTest {

    private PlacingTotemPhase phase;
    private TurnManager turnManagerMock;
    private Player playerMock;
    private TileSlot slotMock;
    private Board boardMock;

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting PlacingTotemPhaseTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending PlacingTotemPhaseTest <<<");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        phase = new PlacingTotemPhase();
        turnManagerMock = mock(TurnManager.class);
        playerMock = mock(Player.class);
        slotMock = mock(TileSlot.class);
        boardMock = mock(Board.class);
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    @DisplayName("placeTotem assigns player to slot and updates queue")
    void placeTotem_ValidSlot_PlacesPlayerAndRemovesFromOrder() throws IllegalPhaseActionException {
        LinkedList<Player> playersQueue = mock(LinkedList.class);
        when(turnManagerMock.getPlayersOrder()).thenReturn(playersQueue);
        when(slotMock.isEmpty()).thenReturn(true);

        phase.placeTotem(turnManagerMock, playerMock, slotMock, boardMock);

        verify(slotMock).isEmpty();
        verify(turnManagerMock).getPlayersOrder();
        verify(playersQueue).removeFirst();
        verify(slotMock).setPlayer(playerMock);
    }

    @Test
    @DisplayName("placeTotem fails if slot is not empty")
    void placeTotem_SlotNotEmpty_ThrowsIllegalPhaseActionException() {
        when(slotMock.isEmpty()).thenReturn(false);

        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () -> {
            phase.placeTotem(turnManagerMock, playerMock, slotMock, boardMock);
        });

        assertEquals("The slot is not empty!", exception.getMessage());
        verify(turnManagerMock, never()).getPlayersOrder();
        verify(slotMock, never()).setPlayer(any());
    }

    @Test
    @DisplayName("Trying to perform illegal actions in PlacingTotemPhaseTest throws IllegalPhaseActionException")
    void illegalActions_ThrowIllegalPhaseActionException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, mock(Player.class), mock());
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(CharacterCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(EventCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(BuildingCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.resolveEvent(turnManagerMock, mock(Board.class));
        });
    }
}
