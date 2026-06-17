package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MesosStartedDTOTest {

    private MesosStartedDTO dto;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting MesosStartedDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending MesosStartedDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new MesosStartedDTO();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(100);
        assertEquals(100, dto.getSequenceNumber());
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