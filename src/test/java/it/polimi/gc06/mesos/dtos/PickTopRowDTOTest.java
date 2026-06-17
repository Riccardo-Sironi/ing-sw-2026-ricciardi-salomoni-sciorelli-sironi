package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PickTopRowDTOTest {

    private PickTopRowDTO dto;
    private final String targetPlayer = "PlayerTopRow";
    private final int testIndex = 1;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PickTopRowDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PickTopRowDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new PickTopRowDTO(targetPlayer, testIndex);
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
        dto.setSequenceNumber(84);
        assertEquals(84, dto.getSequenceNumber());
    }

    @Test
    void testEditForMainPlayer() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        Card removedCardMock = mock(Card.class);

        @SuppressWarnings("unchecked")
        ArrayList<Card> topRowMock = mock(ArrayList.class);
        @SuppressWarnings("unchecked")
        ArrayList<Card> playerCharactersMock = mock(ArrayList.class);

        when(smallModelMock.getTopRow()).thenReturn(topRowMock);
        when(topRowMock.remove(testIndex)).thenReturn(removedCardMock);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn(targetPlayer);
        when(mainPlayerMock.getCharacters()).thenReturn(playerCharactersMock);

        dto.edit(smallModelMock);

        verify(playerCharactersMock, times(1)).add(removedCardMock);
    }

    @Test
    void testEditForOpponent() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView mainPlayerMock = mock(PlayerView.class);
        PlayerView opponentMock = mock(PlayerView.class);
        Card removedCardMock = mock(Card.class);

        @SuppressWarnings("unchecked")
        ArrayList<Card> topRowMock = mock(ArrayList.class);
        @SuppressWarnings("unchecked")
        ArrayList<Card> opponentCharactersMock = mock(ArrayList.class);

        when(smallModelMock.getTopRow()).thenReturn(topRowMock);
        when(topRowMock.remove(testIndex)).thenReturn(removedCardMock);

        when(smallModelMock.getPlayer()).thenReturn(mainPlayerMock);
        when(mainPlayerMock.getNickname()).thenReturn("AnotherPlayer");

        when(opponentMock.getNickname()).thenReturn(targetPlayer);
        when(opponentMock.getCharacters()).thenReturn(opponentCharactersMock);
        when(smallModelMock.getOpponents()).thenReturn(List.of(opponentMock));

        dto.edit(smallModelMock);

        verify(opponentCharactersMock, times(1)).add(removedCardMock);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}