package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
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

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerTest ---");
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

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }


    @Test
    @DisplayName("resolveEvent correctly and change phase")
    void resolveEvent_NormalExecution_ChangesPhase() throws Exception {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        List<Player> players = List.of(p1, p2);

        when(turnManagerMock.getPlayersOrder()).thenReturn(players);

        EventCard event1 = mock(EventCard.class);
        EventCard event2 = mock(EventCard.class);
        ArrayList<EventCard> events = new ArrayList<>(List.of(event1, event2));

        when(boardMock.cleanBottomRow()).thenReturn(events);

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(boardMock, times(1)).cleanBottomRow();
        verify(turnManagerMock, times(2)).getPlayersOrder(); // Events list has size 2, called per element

        verify(event1).resolveEvent(p1);
        verify(event1).resolveEvent(p2);
        verify(event2).resolveEvent(p1);
        verify(event2).resolveEvent(p2);

        verify(turnManagerMock).setPhase(any(EndOfRoundPhase.class));
    }

    @Test
    @DisplayName("resolveEvent does not fail if there are no events in the board")
    void resolveEvent_NoEvents_EmptyList() throws Exception {
        when(turnManagerMock.getPlayersOrder()).thenReturn(List.of(mock(Player.class)));
        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>()); // No events

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(boardMock, times(1)).cleanBottomRow();
        verify(turnManagerMock, never()).getPlayersOrder();
        verify(turnManagerMock).setPhase(any(EndOfRoundPhase.class));
    }

    @Test
    @DisplayName("resolveEvent does not fail if there are no players")
    void resolveEvent_NoPlayers_EmptyList() throws Exception {
        EventCard event1 = mock(EventCard.class);
        when(turnManagerMock.getPlayersOrder()).thenReturn(new ArrayList<>());
        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>(List.of(event1)));

        phase.resolveEvent(turnManagerMock, boardMock);

        verify(event1, never()).resolveEvent(any());
        verify(turnManagerMock).setPhase(any(EndOfRoundPhase.class));
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
    @DisplayName("Trying to perform illegal actions in EventResolutionPhase throws IllegalPhaseActionException")
    void illegalActions_ThrowIllegalPhaseActionException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, mock(Player.class), mock());
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, mock(Player.class), mock(CharacterCard.class), boardMock);
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(EventCard.class), boardMock);
        });

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, mock(Player.class), mock(BuildingCard.class), mock(Board.class));
        });
    }

}

