package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private DTONotifier notifierMock;
    private GameModel gameModelMock;
    private TurnOrderTile turnOrderTileMock;

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
        notifierMock = mock(DTONotifier.class);
        gameModelMock = mock(GameModel.class);
        turnOrderTileMock = mock(TurnOrderTile.class);

        when(turnManagerMock.getActivePlayer()).thenReturn(playerMock);
        when(turnManagerMock.getPhase()).thenReturn(phase);
        when(turnManagerMock.getNotifier()).thenReturn(notifierMock);
        when(turnManagerMock.getGameModel()).thenReturn(gameModelMock);
        when(turnManagerMock.getGameModel().getBoard()).thenReturn(boardMock);
        when(boardMock.getOfferTrackPlayerSlot(any())).thenReturn(tileSlotMock);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }


    @Test
    @DisplayName("startPlayerOfferResolution throws an exception if it's not that player's turn")
    void startPlayerOfferResolution_WrongPlayer_ThrowsException() {
        Player wrongPlayer = mock(Player.class);

        assertThrows(IllegalPhaseActionException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, wrongPlayer, tileSlotMock);
        });
        verify(tileSlotMock, never()).applyEffect();
    }

    @Test
    @DisplayName("startPlayerOfferResolution throws exception if offerTrack tile is null")
    void startPlayerOfferResolution_NullTile_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            phase.startPlayerOfferResolution(turnManagerMock, playerMock, null);
        });
        verify(tileSlotMock, never()).applyEffect();
    }

    @Test
    @DisplayName("startPlayerOfferResolution applies the effect")
    void startPlayerOfferResolution_CorrectPlayer_StartsPhase() throws IllegalPhaseActionException {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        verify(tileSlotMock, times(1)).applyEffect();
    }

    // --- EXCEPTIONS WHEN PHASE NOT STARTED ---

    @Test
    @DisplayName("pickCardFromTop throws if phase is not started")
    void pickCardFromTop_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock));
    }

    @Test
    @DisplayName("pickCardFromBottom throws if phase is not started")
    void pickCardFromBottom_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock));
    }

    @Test
    @DisplayName("pickBuildingFromTop throws if phase is not started")
    void pickBuildingFromTop_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock));
    }

    @Test
    @DisplayName("pickBuildingFromBottom throws if phase is not started")
    void pickBuildingFromBottom_NotStarted_ThrowsException() {
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock));
    }

    // --- EXCEPTIONS WHEN DRAWS ARE 0 ---

    @Test
    @DisplayName("pickCardFromTop throws if no draws left")
    void pickCardFromTop_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        when(playerMock.getTopDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock));
    }

    @Test
    @DisplayName("pickCardFromBottom throws if no draws left")
    void pickCardFromBottom_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock));
    }

    @Test
    @DisplayName("pickBuildingFromTop throws if no draws left")
    void pickBuildingFromTop_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        when(playerMock.getTopDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock));
    }

    @Test
    @DisplayName("pickBuildingFromBottom throws if no draws left")
    void pickBuildingFromBottom_NoDrawsLeft_ThrowsException() throws IllegalPhaseActionException {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock));
    }

    // --- SUCCESSFUL PICKS (WITHOUT ENDING PHASE) ---

    @Test
    @DisplayName("pickCardFromTop (BuildingCard) succeeds")
    void pickCardFromTop_BuildingCard_Success() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock);

        verify(boardMock).buyBuildingFromTopRow(playerMock, buildingCardMock);
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setTopDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromBottom (BuildingCard) succeeds")
    void pickCardFromBottom_BuildingCard_Success() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.pickCardFromBottom(turnManagerMock, playerMock, buildingCardMock, boardMock);

        verify(boardMock).buyBuildingFromBottomRow(playerMock, buildingCardMock);
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setBottomDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromTop (Character) succeeds")
    void pickCardFromTop_CharacterCard_Success() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.pickCardFromTop(turnManagerMock, playerMock, cardMock, boardMock);

        verify(boardMock).pickCardFromTopRow(playerMock, cardMock);
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setTopDrawNum(0);
    }

    @Test
    @DisplayName("pickCardFromBottom (Character) succeeds")
    void pickCardFromBottom_Character_Success() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(playerMock.getBottomDrawNum()).thenReturn(1);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.pickCardFromBottom(turnManagerMock, playerMock, cardMock, boardMock);

        verify(boardMock).pickCardFromBottomRow(playerMock, cardMock);
        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
        verify(playerMock).setBottomDrawNum(0);
    }

    // --- ERROR PROPAGATION ---

    @Test
    @DisplayName("pickCardFromTop (Building) propagates IllegalGameActionException from board and does NOT consume draw")
    void pickCardFromTop_BuildingCard_PropagatesException_DoesNotConsumeDraw() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(1);
        doThrow(new IllegalGameActionException("Not enough food")).when(boardMock).buyBuildingFromTopRow(playerMock, buildingCardMock);

        assertThrows(IllegalGameActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, buildingCardMock, boardMock));
        verify(playerMock, never()).setTopDrawNum(anyInt());
    }

    @Test
    @DisplayName("pickCardFromTop with EventCard throws exception as inherited by Phase class")
    void pickCardFromTop_EventCard_ThrowsException() {
        EventCard eventCardMock = mock(EventCard.class);
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromTop(turnManagerMock, playerMock, eventCardMock, boardMock));
    }

    @Test
    @DisplayName("pickCardFromBottom with EventCard throws exception as inherited by Phase class")
    void pickCardFromBottom_EventCard_ThrowsException() {
        EventCard eventCardMock = mock(EventCard.class);
        assertThrows(IllegalPhaseActionException.class, () -> phase.pickCardFromBottom(turnManagerMock, playerMock, eventCardMock, boardMock));
    }

    @Test
    @DisplayName("test checkIfPlayerIsFinished throws assertion if player hasn't started")
    void test_checkIfPlayerIsFinished_hasNotEvenStarted_ThrowsAssertion() {
        OfferResolutionPhase unstartedPhase = new OfferResolutionPhase();
        assertThrows(IllegalPhaseActionException.class, () -> unstartedPhase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock));
    }

    @Test
    @DisplayName("Player finishes: is placed on TurnOrderTile, and next player is triggered from OfferTrack")
    void test_checkIfPlayerIsFinished_NextPlayerInOfferTrack() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(0);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        TileSlot playerSlotMock = mock(TileSlot.class);
        when(boardMock.getOfferTrackPlayerSlot(playerMock)).thenReturn(playerSlotMock);

        LinkedList<Player> turnQueue = new LinkedList<>();
        turnQueue.add(playerMock);
        when(turnManagerMock.getPlayersOrder()).thenReturn(turnQueue);

        TurnOrderTile turnOrderTileMock = mock(TurnOrderTile.class);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot occupiedOrderSlot = mock(TileSlot.class);
        when(occupiedOrderSlot.getPlayer()).thenReturn(mock(Player.class));

        TileSlot emptyOrderSlot = mock(TileSlot.class);
        when(emptyOrderSlot.getPlayer()).thenReturn(null);

        ArrayList<TileSlot> orderSlots = new ArrayList<>(List.of(occupiedOrderSlot, emptyOrderSlot));
        when(turnOrderTileMock.slots()).thenReturn(orderSlots);

        TileSlot emptyOfferSlot = mock(TileSlot.class);
        when(emptyOfferSlot.getPlayer()).thenReturn(null);

        TileSlot nextPlayerOfferSlot = mock(TileSlot.class);
        Player nextPlayerMock = mock(Player.class);
        when(nextPlayerOfferSlot.getPlayer()).thenReturn(nextPlayerMock);

        ArrayList<TileSlot> offerSlots = new ArrayList<>(List.of(emptyOfferSlot, nextPlayerOfferSlot));
        when(boardMock.getOfferTrack()).thenReturn(offerSlots);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        when(boardMock.getOfferTrackPlayerSlot(nextPlayerMock)).thenReturn(nextPlayerOfferSlot);

        Phase newPhaseMock = mock(OfferResolutionPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(newPhaseMock);

        phase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock);

        verify(playerSlotMock, times(1)).removePlayer();
        verify(emptyOrderSlot, times(1)).setPlayer(playerMock);

        assertEquals(0, turnQueue.size());

        verify(turnManagerMock, times(1)).setPhase(any(OfferResolutionPhase.class));
        verify(newPhaseMock, times(1)).startPlayerOfferResolution(eq(turnManagerMock), eq(nextPlayerMock), any(TileSlot.class));
    }

    @Test
    @DisplayName("Player finishes: is placed on TurnOrderTile, OfferTrack is empty so triggers EventResolution")
    void test_checkIfPlayerIsFinished_OfferTrackEmpty_TriggersEventPhase() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getTopDrawNum()).thenReturn(0);
        when(playerMock.getBottomDrawNum()).thenReturn(0);

        TileSlot playerSlotMock = mock(TileSlot.class);
        when(boardMock.getOfferTrackPlayerSlot(playerMock)).thenReturn(playerSlotMock);

        LinkedList<Player> turnQueue = new LinkedList<>();
        when(turnManagerMock.getPlayersOrder()).thenReturn(turnQueue);

        TurnOrderTile turnOrderTileMock = mock(TurnOrderTile.class);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot emptyOrderSlot = mock(TileSlot.class);
        when(emptyOrderSlot.getPlayer()).thenReturn(null);
        ArrayList<TileSlot> orderSlots = new ArrayList<>(List.of(emptyOrderSlot));
        when(turnOrderTileMock.slots()).thenReturn(orderSlots);

        when(boardMock.getOfferTrack()).thenReturn(new ArrayList<>());
        when(boardMock.isOfferTrackEmpty()).thenReturn(true);

        Phase eventPhaseMock = mock(EventResolutionPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(eventPhaseMock);

        phase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock);

        verify(playerSlotMock, times(1)).removePlayer();
        verify(emptyOrderSlot, times(1)).setPlayer(playerMock);

        verify(turnManagerMock, times(1)).setPhase(any(EventResolutionPhase.class));
        verify(eventPhaseMock, times(1)).resolveEvent(turnManagerMock, boardMock);
    }


    @Test
    @DisplayName("Branch coverage: Bottom is 0 but Top is 1 -> skips finishing logic")
    void test_checkIfPlayerIsFinished_Bottom0_Top1() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getBottomDrawNum()).thenReturn(0);
        when(playerMock.getTopDrawNum()).thenReturn(1);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);

        phase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock);

        verify(boardMock, never()).getOfferTrackPlayerSlot(any());
    }

    @Test
    @DisplayName("Branch coverage: orderTile loop never finds null, offerTrack has null players")
    void test_checkIfPlayerIsFinished_TurnOrderFull_OfferTrackHasNull() throws Exception {
        phase.startPlayerOfferResolution(turnManagerMock, playerMock, tileSlotMock);

        when(playerMock.getBottomDrawNum()).thenReturn(0);
        when(playerMock.getTopDrawNum()).thenReturn(0);

        TileSlot playerSlotMock = mock(TileSlot.class);
        when(boardMock.getOfferTrackPlayerSlot(playerMock)).thenReturn(playerSlotMock);
        LinkedList<Player> turnQueue = new LinkedList<>();
        when(turnManagerMock.getPlayersOrder()).thenReturn(turnQueue);

        TurnOrderTile turnOrderTileMock = mock(TurnOrderTile.class);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot occupiedOrderSlot1 = mock(TileSlot.class);
        when(occupiedOrderSlot1.getPlayer()).thenReturn(mock(Player.class));
        TileSlot occupiedOrderSlot2 = mock(TileSlot.class);
        when(occupiedOrderSlot2.getPlayer()).thenReturn(mock(Player.class));

        when(turnOrderTileMock.slots()).thenReturn(new ArrayList<>(List.of(occupiedOrderSlot1, occupiedOrderSlot2)));

        TileSlot emptyOfferSlot = mock(TileSlot.class);
        when(emptyOfferSlot.getPlayer()).thenReturn(null);

        TileSlot nextPlayerOfferSlot = mock(TileSlot.class);
        Player nextPlayerMock = mock(Player.class);
        when(nextPlayerOfferSlot.getPlayer()).thenReturn(nextPlayerMock);

        ArrayList<TileSlot> offerSlots = new ArrayList<>(List.of(emptyOfferSlot, nextPlayerOfferSlot));
        when(boardMock.getOfferTrack()).thenReturn(offerSlots);
        when(boardMock.isOfferTrackEmpty()).thenReturn(false);
        when(boardMock.getOfferTrackPlayerSlot(nextPlayerMock)).thenReturn(nextPlayerOfferSlot);

        Phase newPhaseMock = mock(OfferResolutionPhase.class);
        when(turnManagerMock.getPhase()).thenReturn(newPhaseMock);

        phase.checkIfPlayerIsFinished(turnManagerMock, playerMock, boardMock);

        verify(playerSlotMock, times(1)).removePlayer();

        verify(occupiedOrderSlot1, never()).setPlayer(any());
        verify(occupiedOrderSlot2, never()).setPlayer(any());

        verify(turnManagerMock, times(1)).setPhase(any(OfferResolutionPhase.class));
    }
}