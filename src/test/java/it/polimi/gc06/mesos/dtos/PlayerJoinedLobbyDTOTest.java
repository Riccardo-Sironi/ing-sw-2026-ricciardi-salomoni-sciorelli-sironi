package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerJoinedLobbyDTOTest {

    private PlayerJoinedLobbyDTO dto;
    private ArrayList<String> playersList;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerJoinedLobbyDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerJoinedLobbyDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        playersList = new ArrayList<>(List.of("LocalPlayer", "Alice", "Bob"));
        dto = new PlayerJoinedLobbyDTO(playersList);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(55);
        assertEquals(55, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView localPlayerMock = mock(PlayerView.class);

        @SuppressWarnings("unchecked")
        ArrayList<PlayerView> opponentsListMock = mock(ArrayList.class);

        when(smallModelMock.getOpponents()).thenReturn(opponentsListMock);
        when(smallModelMock.getPlayer()).thenReturn(localPlayerMock);
        when(localPlayerMock.getNickname()).thenReturn("LocalPlayer");

        dto.edit(smallModelMock);

        verify(opponentsListMock, times(1)).clear();

        ArgumentCaptor<PlayerView> opponentCaptor = ArgumentCaptor.forClass(PlayerView.class);
        verify(smallModelMock, times(2)).addOpponent(opponentCaptor.capture());

        List<PlayerView> capturedOpponents = opponentCaptor.getAllValues();
        assertEquals("Alice", capturedOpponents.get(0).getNickname());
        assertEquals("Bob", capturedOpponents.get(1).getNickname());
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}