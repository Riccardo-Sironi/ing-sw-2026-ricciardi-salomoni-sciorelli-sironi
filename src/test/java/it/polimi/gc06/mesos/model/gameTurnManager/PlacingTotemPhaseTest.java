package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.DTONotifier;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlacingTotemPhaseTest {

    private PlacingTotemPhase phase;
    private TurnManager turnManagerMock;
    private Player playerMock;
    private TileSlot slotMock;
    private Board boardMock;
    private TurnOrderTile turnOrderTileMock;
    private DTONotifier notifierMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlacingTotemPhaseTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlacingTotemPhaseTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {

        phase = new PlacingTotemPhase();
        turnManagerMock = mock(TurnManager.class);
        playerMock = mock(Player.class);
        slotMock = mock(TileSlot.class);
        boardMock = mock(Board.class);
        turnOrderTileMock = mock(TurnOrderTile.class);
        notifierMock = mock(DTONotifier.class);

        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);
        when(turnManagerMock.getNotifier()).thenReturn(notifierMock);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testPlaceTotem() throws IllegalPhaseActionException {

        int numPlayers = 5;
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<TileSlot> orderSlots = new ArrayList<>();
        ArrayList<TileSlot> offerSlots = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player("Player" + (i + 1), Color.values()[i], new ModifierBuildingsRegistry(), new DTONotifier()));

            TileSlot orderSlot = new TileSlot();
            orderSlot.setPlayer(players.get(i));
            orderSlots.add(orderSlot);

            offerSlots.add(new TileSlot());
        }

        LinkedList<Player> playersOrder = new LinkedList<>(players);

        when(turnManagerMock.getPlayersOrder()).thenReturn(playersOrder);
        when(turnOrderTileMock.slots()).thenReturn(orderSlots);
        when(boardMock.getOfferTrack()).thenReturn(offerSlots);
        when(boardMock.getOfferTrackPlayerSlot(any(Player.class))).thenReturn(offerSlots.getFirst());
        when(turnManagerMock.getActivePlayer()).thenReturn(players.getFirst());

        Phase offerPhaseMock = mock(OfferResolutionPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(offerPhaseMock);

        for (int i = 0; i < numPlayers - 1; i++) {
            phase.placeTotem(turnManagerMock, players.get(i), offerSlots.get(i), boardMock);

            assertFalse(offerSlots.get(i).isEmpty(), "offer slot should contain the player");
            assertNotNull(offerSlots.get(i).getPlayer(), "player in offer slot shouldn't be null");
            assertNull(orderSlots.get(i).getPlayer(), "player should be removed from the turn order tile");

            verify(turnManagerMock, never()).setPhase(any(OfferResolutionPhase.class));
        }

        phase.placeTotem(turnManagerMock, players.get(numPlayers - 1), offerSlots.get(numPlayers - 1), boardMock);

        verify(turnManagerMock, times(1)).setPhase(any(OfferResolutionPhase.class));

        verify(offerPhaseMock, times(1)).startPlayerOfferResolution(eq(turnManagerMock), any(Player.class), any(TileSlot.class));
    }

    @Test
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
    void illegalActions_ThrowIllegalPhaseActionException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, mock(Player.class), mock(TileSlot.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(CharacterCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(BuildingCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(EventCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(BuildingCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(CharacterCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.resolveEvent(turnManagerMock, mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.endOfRound(turnManagerMock, mock(Board.class), mock());
        });
    }

    @Test
    void placeTotem_RemovesPlayerFromCorrectSlot_BranchCoverage() throws IllegalPhaseActionException {
        LinkedList<Player> playersOrder = new LinkedList<>(List.of(playerMock, mock(Player.class)));
        when(turnManagerMock.getPlayersOrder()).thenReturn(playersOrder);
        when(turnManagerMock.getActivePlayer()).thenReturn(playersOrder.getFirst());
        when(slotMock.isEmpty()).thenReturn(true);

        TileSlot emptySlot = mock(TileSlot.class);
        when(emptySlot.getPlayer()).thenReturn(null);

        TileSlot wrongPlayerSlot = mock(TileSlot.class);
        when(wrongPlayerSlot.getPlayer()).thenReturn(mock(Player.class));

        TileSlot correctSlot = mock(TileSlot.class);
        when(correctSlot.getPlayer()).thenReturn(playerMock);

        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        when(turnOrderTileMock.slots()).thenReturn(new ArrayList<>(List.of(emptySlot, wrongPlayerSlot, correctSlot)));

        phase.placeTotem(turnManagerMock, playerMock, slotMock, boardMock);

        verify(emptySlot, never()).removePlayer();
        verify(wrongPlayerSlot, never()).removePlayer();
        verify(correctSlot, times(1)).removePlayer();
    }

    @Test
    void placeTotem_TransitionWithEmptyOfferTrack_ThrowsIllegalStateException() throws IllegalPhaseActionException {
        LinkedList<Player> playersOrder = new LinkedList<>(List.of(playerMock));
        when(turnManagerMock.getPlayersOrder()).thenReturn(playersOrder);
        when(slotMock.isEmpty()).thenReturn(true);

        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);
        when(turnOrderTileMock.slots()).thenReturn(new ArrayList<>());

        TileSlot emptyOfferSlot = mock(TileSlot.class);
        when(emptyOfferSlot.getPlayer()).thenReturn(null);

        when(boardMock.getOfferTrack()).thenReturn(new ArrayList<>(List.of(emptyOfferSlot)));

        when(turnManagerMock.getPhase()).thenReturn(mock(OfferResolutionPhase.class));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            phase.placeTotem(turnManagerMock, playerMock, slotMock, boardMock);
        });

        assertEquals("test", exception.getMessage());
    }
}