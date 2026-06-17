package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuildingsRefillDTOTest {

    private BuildingsRefillDTO dto;
    private List<Card> topCards;
    private List<Card> bottomCards;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BuildingsRefillDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BuildingsRefillDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        topCards = List.of(mock(Card.class), mock(Card.class));
        bottomCards = List.of(mock(Card.class));
        dto = new BuildingsRefillDTO(topCards, bottomCards);
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
        dto.setSequenceNumber(42);
        assertEquals(42, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        ArrayList<Card> smallModelTop = new ArrayList<>();
        ArrayList<Card> smallModelBottom = new ArrayList<>();

        when(smallModelMock.getTopBuildings()).thenReturn(smallModelTop);
        when(smallModelMock.getBottomBuildings()).thenReturn(smallModelBottom);

        dto.edit(smallModelMock);

        assertEquals(2, smallModelTop.size());
        assertEquals(1, smallModelBottom.size());
        verify(smallModelMock, times(2)).getTopBuildings();
        verify(smallModelMock, times(2)).getBottomBuildings();
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}