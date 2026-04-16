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

import static org.junit.jupiter.api.Assertions.*;
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
        System.out.println("--- Starting OfferResolutionPhaseTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending OfferResolutionPhaseTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
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

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }


    @Test
    @DisplayName("startPlayerOfferResolution throws an exception if it's not that player's turn")
    void startPlayerOfferResolution_WrongPlayer_ThrowsException() {
        Player wrongPlayer = mock(Player.class);
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, wrongPlayer, tileSlotMock);
        });

        verify(tileSlotMock, never()).applyEffect();
    }

    @Test
    @DisplayName("startPlayerOfferResolution throws exception if offerTrack tile is null")
    void startPlayerOfferResolution_NullTile_ThrowsException() {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        assertThrows(IllegalArgumentException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, playerMock, null);
        });
        verify(tileSlotMock, never()).applyEffect();
    }

    @Test
    @DisplayName("startPlayerOfferResolution applies the effect")
    void startPlayerOfferResolution_CorrectPlayer_StartsPhase() {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);

        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        verify(tileSlotMock, times(1)).applyEffect();
    }

    @Test
    @DisplayName("pickCardFromTop throws if phase is not started")
    void pickCardFromTop_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);
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
    @DisplayName("pickCardFromTop throws if phase is not started")
    void pickBuildingFromTop_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom throws if phase is not started")
    void pickBuildingFromBottom_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromTop throws if no draws left")
    void pickBuildingFromTop_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromBottom throws if no draws left")
    void pickBuildingFromBottom_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getBottomDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock);
        });
    }

    @Test
    @DisplayName("pickCardFromTop (BuildingCard) succeeds")
    void pickCardFromTop_BuildingCard_Success() {
        when(turnManagerMock.getPhase()).thenReturn(phase);
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);

        phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock);
        verify(boardMock).buyBuildingFromTopRow(playerMock, buildingCardMock);
        // Does not trigger finish logic since getTopDrawNum() == 1
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setTopDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromBottom (BuildingCard) succeeds")
    void pickCardFromBottom_BuildingCard_Success() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1); // Not finishing yet
        when(playerMock.getBottomDrawNum()).thenReturn(1);

        phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock);

        verify(boardMock).buyBuildingFromBottomRow(playerMock, buildingCardMock);
        // Does not trigger finish logic since getTopDrawNum() == 1
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setBottomDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromTop (Character) succeeds")
    void pickCardFromTop_CharacterCard_Success() {
        when(turnManagerMock.getPhase()).thenReturn(phase);
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);

        phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);
        verify(boardMock).pickCardFromTopRow(playerMock, cardMock);
        // Does not trigger finish logic since getTopDrawNum() == 1
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setTopDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromBottom (Character) succeeds")
    void pickCardFromBottom_Character_Success() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1); // Not finishing yet
        when(playerMock.getBottomDrawNum()).thenReturn(1);

        phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock);

        verify(boardMock).pickCardFromBottomRow(playerMock, cardMock);
        // Does not trigger finish logic since getTopDrawNum() == 1
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setBottomDrawNum(0);
    }


    @Test
    @DisplayName("Player picks card from top but doesn't get remove from offer track because still has to pick")
    void test_isPlayerFinished_doesNotMovePlayerWhoIsNotFinished() throws Exception {
        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        when(playerMock.getTopDrawNum()).thenReturn(2);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

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

        verify(playerMock).setTopDrawNum(1);

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

    @Test
    @DisplayName("test checkIfPlayerIsFinished does throw assertion if player hasn't started ")
    void test_checkIfPlayerIsFinished_hasNotEvenStarted_ThrowsAssertion() {
        OfferResolutionPhase offerResolutionPhase = new OfferResolutionPhase();
        assertThrows(IllegalPhaseActionException.class, () -> {
            offerResolutionPhase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock);
        });
    }
}