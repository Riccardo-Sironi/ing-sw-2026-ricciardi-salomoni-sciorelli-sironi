package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerResourcesChangeDTOTest {

    private final String targetPlayer = "TestPlayer";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerResourcesChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerResourcesChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, 1, 2, 30, 40);
        assertEquals(targetPlayer, dto.getPlayer());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, 1, 2, 30, 40);
        dto.setSequenceNumber(99);
        assertEquals(99, dto.getSequenceNumber());
    }

    @Test
    void testEditForMainPlayer() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, 1, 2, 30, 40);
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);

        dto.edit(smallModelMock);

        verify(mainPlayerMock, times(1)).setNumFood(30);
        verify(mainPlayerMock, times(1)).setNumPrestige(40);
        verify(smallModelMock, times(1)).setBottomDrawNum(2);
        verify(smallModelMock, times(1)).setTopDrawNum(1);
    }

    @Test
    void testEditForMainPlayerWithNullValues() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, null, null, null, null);
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);

        dto.edit(smallModelMock);

        verify(mainPlayerMock, never()).setNumFood(anyInt());
        verify(mainPlayerMock, never()).setNumPrestige(anyInt());
        verify(smallModelMock, never()).setBottomDrawNum(anyInt());
        verify(smallModelMock, never()).setTopDrawNum(anyInt());
    }

    @Test
    void testEditForOpponentFullValues() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, null, null, 30, 40);
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");

        when(opponentMock.getNickname()).thenReturn(targetPlayer);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(opponentMock, times(1)).setNumFood(30);
        verify(opponentMock, times(1)).setNumPrestige(40);
    }

    @Test
    void testEditForOpponentOnlyFood() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, null, null, 15, null);
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");

        when(opponentMock.getNickname()).thenReturn(targetPlayer);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(opponentMock, times(1)).setNumFood(15);
        verify(opponentMock, never()).setNumPrestige(anyInt());
    }

    @Test
    void testEditForOpponentOnlyPrestige() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, null, null, null, 50);
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");

        when(opponentMock.getNickname()).thenReturn(targetPlayer);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(opponentMock, never()).setNumFood(anyInt());
        verify(opponentMock, times(1)).setNumPrestige(50);
    }

    @Test
    void testAccept() {
        PlayerResourcesChangeDTO dto = new PlayerResourcesChangeDTO(targetPlayer, 1, 2, 30, 40);
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}