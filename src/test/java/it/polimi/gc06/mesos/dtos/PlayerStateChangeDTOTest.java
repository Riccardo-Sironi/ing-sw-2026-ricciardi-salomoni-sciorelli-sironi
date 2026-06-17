package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerStateChangeDTOTest {

    private PlayerStateChangeDTO dto;
    private final String targetPlayer = "ActivePlayer";
    private SmallModel smallModelMock;
    private PlayerView playerViewMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerStateChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerStateChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new PlayerStateChangeDTO(targetPlayer);
        smallModelMock = mock(SmallModel.class);
        playerViewMock = mock(PlayerView.class);
        when(smallModelMock.getPlayer()).thenReturn(playerViewMock);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(targetPlayer, dto.getPlayer());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(99);
        assertEquals(99, dto.getSequenceNumber());
    }

    @Test
    void testEditDifferentPlayer() {
        when(playerViewMock.getNickname()).thenReturn("OtherPlayer");

        dto.edit(smallModelMock);

        verify(smallModelMock, times(1)).setActive(false);
        verify(smallModelMock, times(1)).setCanSkip(false);
    }

    @Test
    void testEditSamePlayerNoChanges() {
        when(playerViewMock.getNickname()).thenReturn(targetPlayer);

        dto.edit(smallModelMock);

        verify(smallModelMock, never()).setActive(anyBoolean());
        verify(smallModelMock, never()).setCanSkip(anyBoolean());
    }

    @Test
    void testEditSamePlayerWithTrueChanges() {
        when(playerViewMock.getNickname()).thenReturn(targetPlayer);

        dto.setIsActive(true);
        dto.setCanSkip(true);

        dto.edit(smallModelMock);

        verify(smallModelMock, times(1)).setActive(true);
        verify(smallModelMock, times(1)).setCanSkip(true);
    }

    @Test
    void testEditSamePlayerWithFalseChanges() {
        when(playerViewMock.getNickname()).thenReturn(targetPlayer);

        dto.setIsActive(false);
        dto.setCanSkip(false);

        dto.edit(smallModelMock);

        verify(smallModelMock, times(1)).setActive(false);
        verify(smallModelMock, times(1)).setCanSkip(false);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}