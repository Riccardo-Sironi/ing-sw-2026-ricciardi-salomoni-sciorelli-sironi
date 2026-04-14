package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlacingTotemPhaseTest {

    private PlacingTotemPhase phase;
    private TurnManager turnManagerMock;
    private Player playerMock;
    private TileSlot slotMock;
    private Board boardMock;
    private TurnOrderTile turnOrderTileMock;

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
        turnOrderTileMock = mock(TurnOrderTile.class);
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    @DisplayName("Real situation of the phase")
    void testPlaceTotem() {
        int numPlayers = 5;
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<TileSlot> slots = new ArrayList<>();
        TileSlot slot = new TileSlot();
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player("Player" + (i + 1), Color.values()[i], new ModifierBuildingsRegistry()));
            slots.add(new TileSlot());
            slots.get(i).setPlayer(players.get(i));
        }

        when(turnManagerMock.getPlayersOrder()).thenReturn(new ArrayList<>(players));
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);
        when(turnOrderTileMock.slots()).thenReturn(slots);

        phase.placeTotem(turnManagerMock, players.getFirst(), slot, boardMock);
        assertFalse(slot.isEmpty());
        assertNull(slots.getFirst().getPlayer());

        for (int i = 1; i < numPlayers; i++) {
            TileSlot s = new TileSlot();
            phase.placeTotem(turnManagerMock, players.get(i), s, boardMock);
            assertFalse(s.isEmpty());
            assertNull(slots.get(i).getPlayer());
        }

        verify(turnManagerMock).setPhase(any(OfferResolutionPhase.class));
    }


    @Test
    @DisplayName("placeTotem assigns player to slot and updates queue")
    void placeTotem_ValidSlot_PlacesPlayerAndRemovesFromOrder() throws IllegalPhaseActionException {
        LinkedList<Player> playersQueue = mock(LinkedList.class);
        when(turnManagerMock.getPlayersOrder()).thenReturn(playersQueue);
        when(slotMock.isEmpty()).thenReturn(true);

        ArrayList<TileSlot> tileSlots = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tileSlots.add(slotMock);
        }
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);
        when(turnOrderTileMock.slots()).thenReturn(tileSlots);

        phase.placeTotem(turnManagerMock, playerMock, slotMock, boardMock);

        verify(slotMock).isEmpty();
        verify(turnManagerMock, times(2)).getPlayersOrder();
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
