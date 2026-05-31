package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EventResolutionPhaseTest {

    private EventResolutionPhase phase;
    private TurnManager turnManagerMock;
    private Board boardMock;
    private GameModel gameModelMock;
    private DTONotifier notifierMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EventResolutionPhaseTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EventResolutionPhaseTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {

        phase = new EventResolutionPhase();
        turnManagerMock = mock(TurnManager.class);
        boardMock = mock(Board.class);
        gameModelMock = mock(GameModel.class);
        notifierMock = mock(DTONotifier.class);

        when(gameModelMock.getTurnManager()).thenReturn(turnManagerMock);
        when(gameModelMock.getBoard()).thenReturn(boardMock);
        when(turnManagerMock.getNotifier()).thenReturn(notifierMock);
        when(turnManagerMock.getGameModel()).thenReturn(gameModelMock);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("resolveEvent correctly applies events to players and advances to EndOfRoundPhase")
    void resolveEvent_NormalExecution_ChangesPhase() throws Exception {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        List<Player> players = List.of(p1, p2);

        when(turnManagerMock.getPlayersOrder()).thenReturn(players);

        EventCard event1 = mock(EventCard.class);
        EventCard event2 = mock(EventCard.class);
        ArrayList<EventCard> events = new ArrayList<>(List.of(event1, event2));

        when(boardMock.cleanBottomRow()).thenReturn(events);

        Phase endOfRoundPhaseMock = mock(EndOfRoundPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(endOfRoundPhaseMock);

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(boardMock, times(1)).cleanBottomRow();
        verify(turnManagerMock, times(2)).getPlayersOrder();

        verify(event1, times(1)).resolveEvent(p1);
        verify(event1, times(1)).resolveEvent(p2);
        verify(event2, times(1)).resolveEvent(p1);
        verify(event2, times(1)).resolveEvent(p2);

        verify(turnManagerMock, times(1)).setPhase(any(EndOfRoundPhase.class));
        verify(endOfRoundPhaseMock, times(1)).endOfRound(turnManagerMock, boardMock, gameModelMock);
    }

    @Test
    void resolveEvent_NoEvents_EmptyList() throws Exception {
        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>());

        Phase endOfRoundPhaseMock = mock(EndOfRoundPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(endOfRoundPhaseMock);

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(boardMock, times(1)).cleanBottomRow();
        verify(turnManagerMock, never()).getPlayersOrder();

        verify(turnManagerMock, times(1)).setPhase(any(EndOfRoundPhase.class));
        verify(endOfRoundPhaseMock, times(1)).endOfRound(turnManagerMock, boardMock, gameModelMock);
    }

    @Test
    void resolveEvent_NoPlayers_EmptyList() throws Exception {
        EventCard event1 = mock(EventCard.class);

        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>(List.of(event1)));
        when(turnManagerMock.getPlayersOrder()).thenReturn(new ArrayList<>());

        Phase endOfRoundPhaseMock = mock(EndOfRoundPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(endOfRoundPhaseMock);

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(event1, never()).resolveEvent(any());
        verify(turnManagerMock, times(1)).setPhase(any(EndOfRoundPhase.class));
        verify(endOfRoundPhaseMock, times(1)).endOfRound(turnManagerMock, boardMock, gameModelMock);
    }

    @Test
    @DisplayName("resolveEvent with null events throws Exception")
    void resolveEvent_NullEvents_ThrowsNPE() {
        when(boardMock.cleanBottomRow()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {
            phase.resolveEvent(turnManagerMock, boardMock);
        });

        verify(turnManagerMock, never()).setPhase(any());
    }

    @Test
    void illegalActions_ThrowIllegalPhaseActionException() {

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, mock(Player.class), mock(it.polimi.gc06.mesos.model.gameBoard.TileSlot.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(CharacterCard.class), boardMock);
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(BuildingCard.class), boardMock);
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(EventCard.class), boardMock);
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(BuildingCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(CharacterCard.class), mock(Board.class));
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.endOfRound(turnManagerMock, boardMock, gameModelMock);
        });
    }
}