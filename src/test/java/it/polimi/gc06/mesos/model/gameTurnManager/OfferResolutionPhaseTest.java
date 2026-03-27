package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OfferResolutionPhaseTest {

    private OfferResolutionPhase phase;
    private TurnManager turnManagerMock;
    private Player playerMock;
    private TileSlot tileSlotMock;
    private Board boardMock;
    private CharacterCard cardMock;
    private BuildingCard buildingCardMock;

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting OfferResolutionPhaseTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending OfferResolutionPhaseTest <<<");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        phase = new OfferResolutionPhase();
        turnManagerMock = mock(TurnManager.class);
        playerMock = mock(Player.class);
        tileSlotMock = mock(TileSlot.class);
        boardMock = mock(Board.class);
        cardMock = mock(CharacterCard.class);
        buildingCardMock = mock(BuildingCard.class);
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");

    }

    @Test
    @DisplayName("startPlayerOfferResolution throws an exception if it's not that player's turn")
    void startPlayerOfferResolution_WrongPlayer_ThrowsException() {
        Player wrongPlayer = mock(Player.class);
        when(turnManagerMock.getActivePlayer()).thenReturn(wrongPlayer);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        });

        verify(tileSlotMock, never()).applyEffect();
    }

    @Test
    @DisplayName("startPlayerOfferResolution applies the effect and draws the card")
    void startPlayerOfferResolution_CorrectPlayer_StartsPhase() {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);

        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        verify(tileSlotMock, times(1)).applyEffect();

        assertDoesNotThrow(() -> {
            when(playerMock.getBottomDrawNum()).thenReturn(1);
            phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom throws if phase is not started")
    void pickCardFromBottom_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromTop throws if phase is not started")
    void pickCardFromTop_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom throws if no draws left")
    void pickCardFromBottom_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getBottomDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromTop throws if no draws left")
    void pickCardFromTop_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom (BuildingCard) succeeds")
    void pickCardFromBottom_BuildingCard_Success() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getBottomDrawNum()).thenReturn(1).thenReturn(0);
        when(playerMock.getTopDrawNum()).thenReturn(1); // Not finishing yet

        phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock);

        verify(boardMock).buyBuildingFromBottomRow(playerMock, buildingCardMock);
        // Does not trigger finish logic since getTopDrawNum() == 1
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
    }

    @Test
    @DisplayName("pickCardFromTop (CharacterCard) succeeds and triggers player finish")
    void pickCardFromTop_Character_SuccessAndFinish() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        // getTopDrawNum is called: 
        // 1. in the <= 0 check (returns 1)
        // 2. in setTopDrawNum(getTopDrawNum() - 1) (returns 1, sets 0)
        // 3. in checkIfPlayerIsFinished (returns 0)
        when(playerMock.getTopDrawNum()).thenReturn(1, 1, 0);
        when(playerMock.getBottomDrawNum()).thenReturn(0); // already zero

        TurnOrderTile turnOrderTileMock = mock(TurnOrderTile.class);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot playerSlotMock = mock(TileSlot.class);
        when(boardMock.getOfferTrackPlayerSlot(playerMock)).thenReturn(playerSlotMock);

        TileSlot turnOrderSlotMock = mock(TileSlot.class);
        when(turnOrderSlotMock.getPlayer()).thenReturn(null);

        ArrayList<TileSlot> dummySlots = new ArrayList<>();
        dummySlots.add(turnOrderSlotMock);
        when(turnOrderTileMock.slots()).thenReturn(dummySlots);

        List playerListMock = mock(List.class);
        when(turnManagerMock.getPlayersOrder()).thenReturn(playerListMock);

        when(boardMock.isOfferTrackEmpty()).thenReturn(true);

        phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);

        verify(boardMock).pickCardFromTopRow(playerMock, cardMock);
        verify(playerMock).setTopDrawNum(0);

        // Verifies player finish logic
        verify(playerSlotMock).removePlayer();
        verify(turnOrderSlotMock).setPlayer(playerMock);
        verify(playerListMock).addLast(playerMock);
        verify(turnManagerMock).setPhase(any(EventResolutionPhase.class)); // Phase changes
    }

    @Test
    @DisplayName("pickCardFromTop does not transition phase if OfferTrack is NOT empty")
    void pickCardFromTop_DoesNotTransitionPhase_IfOfferTrackNotEmpty() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1, 1, 0);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        TurnOrderTile turnOrderTileMock = mock(TurnOrderTile.class);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot playerSlotMock = mock(TileSlot.class);
        when(boardMock.getOfferTrackPlayerSlot(playerMock)).thenReturn(playerSlotMock);

        TileSlot turnOrderSlotMock = mock(TileSlot.class);
        when(turnOrderSlotMock.getPlayer()).thenReturn(null);

        ArrayList<TileSlot> dummySlots = new ArrayList<>();
        dummySlots.add(turnOrderSlotMock);
        when(turnOrderTileMock.slots()).thenReturn(dummySlots);

        // The OfferTrack is not empty, so even if the player finishes, they should not be removed from the track and phase should not change
        List playerListMock = mock(List.class);
        when(turnManagerMock.getPlayersOrder()).thenReturn(playerListMock);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);

        verify(playerMock).setTopDrawNum(0);

        // There is no player finish logic since OfferTrack is not empty, so the player remains on the track and phase does not change
        verify(turnManagerMock, never()).setPhase(any(EventResolutionPhase.class));
    }

    @Test
    @DisplayName("pickCardFromTop with EventCard throws exception as inherited by Phase class")
    void pickCardFromTop_EventCard_ThrowsException() {
        EventCard eventCardMock = mock(EventCard.class);
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, eventCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom with EventCard throws exception as inherited by Phase class")
    void pickCardFromBottom_EventCard_ThrowsException() {
        EventCard eventCardMock = mock(EventCard.class);
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, playerMock, eventCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromTop (Building) propagates IllegalGameActionException from board and does NOT consume draw")
    void pickCardFromTop_BuildingCard_PropagatesException_DoesNotConsumeDraw() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        // Simulate that the player DOES have a draw left, but not enough food to buy a building
        when(playerMock.getTopDrawNum()).thenReturn(1);

        doThrow(new IllegalGameActionException("Not enough food")).when(boardMock).buyBuildingFromTopRow(playerMock, buildingCardMock);

        assertThrows(IllegalGameActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock);
        });

        // Since we threw an error, the draw MUST not have been consumed
        verify(playerMock, never()).setTopDrawNum(anyInt());
    }
}