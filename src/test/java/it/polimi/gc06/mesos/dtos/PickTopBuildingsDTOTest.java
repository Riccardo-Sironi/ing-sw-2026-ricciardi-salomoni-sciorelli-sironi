package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PickTopBuildingsDTOTest {

    private PickTopBuildingsDTO dto;
    private final String targetPlayer = "PlayerTop";
    private final int testIndex = 3;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PickTopBuildingsDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PickTopBuildingsDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new PickTopBuildingsDTO(targetPlayer, testIndex);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(targetPlayer, dto.getPlayer());
        assertEquals(testIndex, dto.getCardIndex());
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(42);
        assertEquals(42, dto.getSequenceNumber());
    }

    @Test
    void testEditForMainPlayer() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        Card removedCardMock = mock(Card.class);

        @SuppressWarnings("unchecked")
        ArrayList<Card> topBuildingsMock = mock(ArrayList.class);
        @SuppressWarnings("unchecked")
        ArrayList<Card> playerBuildingsMock = mock(ArrayList.class);

        when(smallModelMock.getTopBuildings()).thenReturn(topBuildingsMock);
        when(topBuildingsMock.remove(testIndex)).thenReturn(removedCardMock);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);
        when(mainPlayerMock.getBuildings()).thenReturn(playerBuildingsMock);

        dto.edit(smallModelMock);

        verify(playerBuildingsMock, times(1)).add(removedCardMock);
    }

    @Test
    void testEditForOpponent() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);
        Card removedCardMock = mock(Card.class);

        @SuppressWarnings("unchecked")
        ArrayList<Card> topBuildingsMock = mock(ArrayList.class);
        @SuppressWarnings("unchecked")
        ArrayList<Card> opponentBuildingsMock = mock(ArrayList.class);

        when(smallModelMock.getTopBuildings()).thenReturn(topBuildingsMock);
        when(topBuildingsMock.remove(testIndex)).thenReturn(removedCardMock);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");

        when(opponentMock.getNickname()).thenReturn(targetPlayer);
        when(opponentMock.getBuildings()).thenReturn(opponentBuildingsMock);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(opponentBuildingsMock, times(1)).add(removedCardMock);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}