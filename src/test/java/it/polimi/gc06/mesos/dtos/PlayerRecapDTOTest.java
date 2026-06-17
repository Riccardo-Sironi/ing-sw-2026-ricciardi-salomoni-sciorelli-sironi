package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerRecapDTOTest {

    private SmallModel smallModelMock;
    private PlayerView playerViewMock;
    private final String targetNickname = "TestPlayer";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerRecapDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerRecapDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        smallModelMock = mock(SmallModel.class);
        playerViewMock = mock(PlayerView.class);
        when(playerViewMock.getNickname()).thenReturn(targetNickname);
        when(smallModelMock.getPlayer()).thenReturn(playerViewMock);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testFirstConstructorAndGetters() {
        @SuppressWarnings("unchecked")
        Set<InventionIcon> icons = mock(Set.class);
        PlayerRecapDTO dto = new PlayerRecapDTO("TestPlayer", 1, 2, 3, 4, 5, 6, 7, 8, icons);

        assertEquals("TestPlayer", dto.getPlayer());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSecondConstructor() {
        PlayerRecapDTO dto = new PlayerRecapDTO("TestPlayer", 8, 7, 6, 5, 4, 3);
        assertEquals("TestPlayer", dto.getPlayer());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, 8, 7, 6, 5, 4, 3);
        dto.setSequenceNumber(100);
        assertEquals(100, dto.getSequenceNumber());
    }

    @Test
    void testEditPlayerFoundAsMain() {
        @SuppressWarnings("unchecked")
        Set<InventionIcon> icons = mock(Set.class);
        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, 1, 2, 3, 4, 5, 6, 7, 8, icons);

        dto.edit(smallModelMock);

        verify(playerViewMock, times(1)).setBuildersDiscount(1);
        verify(playerViewMock, times(1)).setShamanStar(2);
        verify(playerViewMock, times(1)).setArtistNumber(3);
        verify(playerViewMock, times(1)).setBuilderNumber(4);
        verify(playerViewMock, times(1)).setGathererNumber(5);
        verify(playerViewMock, times(1)).setHunterNumber(6);
        verify(playerViewMock, times(1)).setInventorNumber(7);
        verify(playerViewMock, times(1)).setShamanNumber(8);
        verify(playerViewMock, times(1)).setCollectedIcons(icons);
    }

    @Test
    void testEditPlayerFoundAsOpponent() {
        PlayerView mainPlayerMock = mock(PlayerView.class);
        when(mainPlayerMock.getNickname()).thenReturn("OtherPlayer");
        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(smallModelMock.getOpponents()).thenReturn(List.of(playerViewMock));

        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, 8, 7, 6, 5, 4, 3);
        dto.edit(smallModelMock);

        verify(playerViewMock, times(1)).setShamanNumber(8);
        verify(playerViewMock, times(1)).setInventorNumber(7);
        verify(playerViewMock, times(1)).setHunterNumber(6);
        verify(playerViewMock, times(1)).setGathererNumber(5);
        verify(playerViewMock, times(1)).setBuilderNumber(4);
        verify(playerViewMock, times(1)).setArtistNumber(3);
        verify(playerViewMock, never()).setBuildersDiscount(anyInt());
    }

    @Test
    void testEditPlayerWithAllNullValues() {
        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, null, null, null, null, null, null, null, null, null);

        dto.edit(smallModelMock);

        verify(playerViewMock, never()).setShamanNumber(anyInt());
        verify(playerViewMock, never()).setInventorNumber(anyInt());
        verify(playerViewMock, never()).setHunterNumber(anyInt());
        verify(playerViewMock, never()).setGathererNumber(anyInt());
        verify(playerViewMock, never()).setBuilderNumber(anyInt());
        verify(playerViewMock, never()).setArtistNumber(anyInt());
        verify(playerViewMock, never()).setCollectedIcons(any());
        verify(playerViewMock, never()).setBuildersDiscount(anyInt());
        verify(playerViewMock, never()).setShamanStar(anyInt());
    }

    @Test
    void testEditPlayerNotFoundThrowsException() {
        PlayerView mainPlayerMock = mock(PlayerView.class);
        when(mainPlayerMock.getNickname()).thenReturn("OtherPlayer");
        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(smallModelMock.getOpponents()).thenReturn(List.of());

        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, 8, 7, 6, 5, 4, 3);

        assertThrows(IllegalStateException.class, () -> dto.edit(smallModelMock));
    }

    @Test
    void testAccept() {
        PlayerRecapDTO dto = new PlayerRecapDTO(targetNickname, 8, 7, 6, 5, 4, 3);
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}