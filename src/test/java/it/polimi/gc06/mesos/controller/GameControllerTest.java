package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameControllerTest {

    private GameController controller;

    @Mock
    private GameModel modelMock;
    @Mock
    private TurnManager turnManagerMock;
    @Mock
    private Phase phaseMock;
    @Mock
    private Board boardMock;
    @Mock
    private Player activePlayerMock;

    @Mock
    private TileSlot tileSlotMock;
    @Mock
    private CharacterCard characterCardMock;
    @Mock
    private BuildingCard buildingCardMock;

    private final String activeNickname = "ActivePlayer";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting GameControllerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending GameControllerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        when(modelMock.getTurnManager()).thenReturn(turnManagerMock);
        when(modelMock.getBoard()).thenReturn(boardMock);
        when(turnManagerMock.getActivePlayer()).thenReturn(activePlayerMock);
        when(turnManagerMock.getPhase()).thenReturn(phaseMock);
        when(activePlayerMock.getNickname()).thenReturn(activeNickname);

        controller = new GameController(modelMock, new DTONotifier());

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testGetModel() {
        assertEquals(modelMock, controller.getModel());
    }

    @Test
    void testIsGameFinished() {
        when(modelMock.isFinished()).thenReturn(true);
        assertTrue(controller.isGameFinished());

        when(modelMock.isFinished()).thenReturn(false);
        assertFalse(controller.isGameFinished());
    }

    @Test
    void handleTotemOfferTilePlacement_WrongPlayer_ThrowsException() {
        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleTotemOfferTilePlacement("WrongPlayer", 0)
        );
        assertEquals("It is not WrongPlayer turn !", ex.getMessage());
    }

    @Test
    void handleTotemOfferTilePlacement_IndexOutOfBounds_ThrowsException() {
        List<TileSlot> offerTrack = new ArrayList<>(List.of(tileSlotMock));
        when(boardMock.getOfferTrack()).thenReturn(offerTrack);

        assertThrows(IllegalPhaseActionException.class, () -> controller.handleTotemOfferTilePlacement(activeNickname, -1));
        assertThrows(IllegalPhaseActionException.class, () -> controller.handleTotemOfferTilePlacement(activeNickname, 5));
    }

    @Test
    void handleTotemOfferTilePlacement_TileOccupied_ThrowsException() {
        List<TileSlot> offerTrack = new ArrayList<>(List.of(tileSlotMock));
        when(boardMock.getOfferTrack()).thenReturn(offerTrack);
        when(tileSlotMock.getPlayer()).thenReturn(mock(Player.class));

        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleTotemOfferTilePlacement(activeNickname, 0)
        );
        assertEquals("The tile is already occupied !", ex.getMessage());
    }

    @Test
    void handleTotemOfferTilePlacement_Success() throws IllegalPhaseActionException {
        List<TileSlot> offerTrack = new ArrayList<>(List.of(tileSlotMock));
        when(boardMock.getOfferTrack()).thenReturn(offerTrack);
        when(tileSlotMock.getPlayer()).thenReturn(null);

        controller.handleTotemOfferTilePlacement(activeNickname, 0);

        verify(phaseMock).placeTotem(turnManagerMock, activePlayerMock, tileSlotMock, boardMock);
    }

    @Test
    void handleCardPickBottomRow_WrongPlayer_ThrowsException() {
        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleCardPickBottomRow("WrongPlayer", 0)
        );
        assertEquals("It's not WrongPlayer's turn !", ex.getMessage());
    }

    @Test
    void handleCardPickBottomRow_Success() throws IllegalGameActionException {
        when(boardMock.getBottomCardFromIndex(0)).thenReturn(characterCardMock);

        controller.handleCardPickBottomRow(activeNickname, 0);

        verify(characterCardMock).accept(any(CardBottomRowControllerVisitor.class));
    }

    @Test
    void handleCardPickTopRow_WrongPlayer_ThrowsException() {
        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleCardPickTopRow("WrongPlayer", 0)
        );
        assertEquals("It's not WrongPlayer's turn !", ex.getMessage());
    }

    @Test
    void handleCardPickTopRow_Success() throws IllegalGameActionException {
        when(boardMock.getTopCardFromIndex(0)).thenReturn(characterCardMock);

        controller.handleCardPickTopRow(activeNickname, 0);

        verify(characterCardMock).accept(any(CardTopRowControllerVisitor.class));
    }

    @Test
    void handleBuildingPickBottomRow_WrongPlayer_ThrowsException() {
        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleBuildingPickBottomRow("WrongPlayer", 0)
        );
        assertEquals("It's not WrongPlayer's turn !", ex.getMessage());
    }

    @Test
    void handleBuildingPickBottomRow_Success() throws IllegalGameActionException {
        when(boardMock.getBottomBuildingFromIndex(0)).thenReturn(buildingCardMock);

        controller.handleBuildingPickBottomRow(activeNickname, 0);

        verify(phaseMock).pickCardFromBottom(turnManagerMock, activePlayerMock, buildingCardMock, boardMock);
    }

    @Test
    void handleBuildingPickTopRow_WrongPlayer_ThrowsException() {
        IllegalPhaseActionException ex = assertThrows(IllegalPhaseActionException.class, () ->
                controller.handleBuildingPickTopRow("WrongPlayer", 0)
        );
        assertEquals("It's not WrongPlayer's turn !", ex.getMessage());
    }

    @Test
    void handleBuildingPickTopRow_Success() throws IllegalGameActionException {
        when(boardMock.getTopBuildingFromIndex(0)).thenReturn(buildingCardMock);

        controller.handleBuildingPickTopRow(activeNickname, 0);

        verify(phaseMock).pickCardFromTop(turnManagerMock, activePlayerMock, buildingCardMock, boardMock);
    }
}