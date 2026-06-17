package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndGameDTOTest {

    private EndGameDTO dto;
    private ArrayList<Card> bottomCards;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EndGameDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EndGameDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        bottomCards = new ArrayList<>(List.of(mock(Card.class), mock(Card.class)));
        dto = new EndGameDTO(bottomCards);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(99);
        assertEquals(99, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        ArrayList<Card> smallModelBottomRow = new ArrayList<>();

        when(smallModelMock.getBottomRow()).thenReturn(smallModelBottomRow);

        dto.edit(smallModelMock);

        assertEquals(2, smallModelBottomRow.size());
        verify(smallModelMock, times(2)).getBottomRow();
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}