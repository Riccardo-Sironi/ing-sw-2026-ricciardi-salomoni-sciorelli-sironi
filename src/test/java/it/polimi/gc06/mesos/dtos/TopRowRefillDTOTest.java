package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TopRowRefillDTOTest {

    private TopRowRefillDTO dto;
    private List<Card> topCards;
    private List<Card> bottomCards;
    private final int testDeckSize = 42;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TopRowRefillDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TopRowRefillDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        topCards = List.of(mock(Card.class), mock(Card.class));
        bottomCards = List.of(mock(Card.class));
        dto = new TopRowRefillDTO(topCards, bottomCards, testDeckSize);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(2, dto.getTop().size());
        assertEquals(1, dto.getBottom().size());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(8);
        assertEquals(8, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        ArrayList<Card> smallModelTop = new ArrayList<>();
        ArrayList<Card> smallModelBottom = new ArrayList<>();

        when(smallModelMock.getTopRow()).thenReturn(smallModelTop);
        when(smallModelMock.getBottomRow()).thenReturn(smallModelBottom);

        dto.edit(smallModelMock);

        assertEquals(2, smallModelTop.size());
        assertEquals(1, smallModelBottom.size());
        verify(smallModelMock, times(1)).setTribeDeckSize(testDeckSize);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}