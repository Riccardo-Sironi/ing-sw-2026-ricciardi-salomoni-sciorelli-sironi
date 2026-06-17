package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseTotemColorDTOTest {

    private ChooseTotemColorDTO dto;
    private final String testNickname = "PlayerOne";
    private final Color testColor = Color.TURQUOISE;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ChooseTotemColorDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ChooseTotemColorDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new ChooseTotemColorDTO(testNickname, testColor);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(testNickname, dto.getNickname());
        assertEquals(testColor, dto.getColor());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(10);
        assertEquals(10, dto.getSequenceNumber());
    }

    @Test
    void testEditForMainPlayer() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);

        when(mainPlayerMock.getNickname()).thenReturn(testNickname);
        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);

        dto.edit(smallModelMock);

        verify(mainPlayerMock, times(1)).setColor(testColor);
    }

    @Test
    void testEditForOpponent() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);

        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");
        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);

        when(opponentMock.getNickname()).thenReturn(testNickname);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(mainPlayerMock, never()).setColor(any());
        verify(opponentMock, times(1)).setColor(testColor);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}