package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TotemOfferMoveDTOTest {

    private TotemOfferMoveDTO dto;
    private final String targetPlayer = "TotemPlayer";
    private final int testIndex = 2;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TotemOfferMoveDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TotemOfferMoveDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new TotemOfferMoveDTO(targetPlayer, testIndex);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(targetPlayer, dto.getPlayer());
        assertEquals(testIndex, dto.getIndex());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(11);
        assertEquals(11, dto.getSequenceNumber());
    }

    @Test
    void testEditSuccessPlayerFoundInTurnOrderTile() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView wrongPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);
        when(wrongPlayerMock.getNickname()).thenReturn("WrongPlayer");

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        turnOrderTile.add(null);
        turnOrderTile.add(wrongPlayerMock);
        turnOrderTile.add(mainPlayerMock);
        when(smallModelMock.getTurnOrderTile()).thenReturn(turnOrderTile);

        TileSlotView targetSlotMock = mock(TileSlotView.class);
        ArrayList<TileSlotView> offerTrack = new ArrayList<>();
        offerTrack.add(mock(TileSlotView.class));
        offerTrack.add(mock(TileSlotView.class));
        offerTrack.add(targetSlotMock);
        when(smallModelMock.getOfferTrack()).thenReturn(offerTrack);

        dto.edit(smallModelMock);

        assertNotNull(turnOrderTile.get(1)); // Wrong player non toccato
        assertNull(turnOrderTile.get(2)); // Target player rimosso
        verify(targetSlotMock, times(1)).setPlayer(mainPlayerMock);
    }

    @Test
    void testEditSuccessPlayerNotFoundInTurnOrderTile() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);

        ArrayList<PlayerView> turnOrderTile = new ArrayList<>();
        turnOrderTile.add(null);
        when(smallModelMock.getTurnOrderTile()).thenReturn(turnOrderTile);

        TileSlotView targetSlotMock = mock(TileSlotView.class);
        ArrayList<TileSlotView> offerTrack = new ArrayList<>();
        offerTrack.add(mock(TileSlotView.class));
        offerTrack.add(mock(TileSlotView.class));
        offerTrack.add(targetSlotMock);
        when(smallModelMock.getOfferTrack()).thenReturn(offerTrack);

        assertDoesNotThrow(() -> dto.edit(smallModelMock));
        verify(targetSlotMock, times(1)).setPlayer(mainPlayerMock);
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