package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventResolvedDTOTest {

    private EventResolvedDTO dto;
    private EventCard eventCardMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EventResolvedDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EventResolvedDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        eventCardMock = mock(EventCard.class);
        dto = new EventResolvedDTO(eventCardMock);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(eventCardMock, dto.getEventCard());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(11);
        assertEquals(11, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);

        assertDoesNotThrow(() -> dto.edit(smallModelMock));

        verifyNoInteractions(smallModelMock);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}