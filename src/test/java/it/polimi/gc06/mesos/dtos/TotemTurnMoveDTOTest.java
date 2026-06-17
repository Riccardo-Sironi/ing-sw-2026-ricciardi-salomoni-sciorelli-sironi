package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TotemTurnMoveDTOTest {

    private TotemTurnMoveDTO dto;
    private final String targetPlayer = "TurnPlayer";
    private final int testIndex = 0;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TotemTurnMoveDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TotemTurnMoveDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new TotemTurnMoveDTO(targetPlayer, testIndex);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(77);
        assertEquals(77, dto.getSequenceNumber());
    }

    @Test
    void testEditSuccessPlayerFoundInOfferTrack() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);

        TileSlotView wrongSlotMock = mock(TileSlotView.class);
        PlayerView wrongPlayerMock = mock(PlayerView.class);
        when(wrongPlayerMock.getNickname()).thenReturn("WrongPlayer");
        when(wrongSlotMock.getPlayer()).thenReturn(wrongPlayerMock);

        TileSlotView targetSlotMock = mock(TileSlotView.class);
        when(targetSlotMock.getPlayer()).thenReturn(mainPlayerMock);

        ArrayList<TileSlotView> offerTrack = new ArrayList<>();
        offerTrack.add(mock(TileSlotView.class)); // Slot null
        offerTrack.add(wrongSlotMock); // Slot con giocatore sbagliato
        offerTrack.add(targetSlotMock); // Slot con giocatore giusto
        when(smallModelMock.getOfferTrack()).thenReturn(offerTrack);

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        turnOrderTile.add(null);
        when(smallModelMock.getTurnOrderTile()).thenReturn(turnOrderTile);

        dto.edit(smallModelMock);

        verify(wrongSlotMock, never()).removePlayer();
        verify(targetSlotMock, times(1)).removePlayer();
        assertEquals(mainPlayerMock, turnOrderTile.get(testIndex));
    }

    @Test
    void testEditSuccessPlayerNotFoundInOfferTrack() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);

        ArrayList<TileSlotView> offerTrack = new ArrayList<>();
        offerTrack.add(mock(TileSlotView.class)); // Nessun giocatore nell'offer track
        when(smallModelMock.getOfferTrack()).thenReturn(offerTrack);

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        turnOrderTile.add(null);
        when(smallModelMock.getTurnOrderTile()).thenReturn(turnOrderTile);

        assertDoesNotThrow(() -> dto.edit(smallModelMock));
        assertEquals(mainPlayerMock, turnOrderTile.get(testIndex));
    }

    @Test
    void testEditOpponentNotFoundThrowsException() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("OtherPlayer");
        when(smallModelMock.getOpponents()).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> dto.edit(smallModelMock));
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}