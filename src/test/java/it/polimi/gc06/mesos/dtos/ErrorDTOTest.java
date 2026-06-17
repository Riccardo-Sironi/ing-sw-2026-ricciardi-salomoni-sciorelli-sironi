package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ErrorDTOTest {

    private ErrorDTO dto;
    private final String errorMessage = "Connection Lost";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ErrorDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ErrorDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new ErrorDTO(errorMessage);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(1);
        assertEquals(1, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);

        dto.edit(smallModelMock);

        verify(smallModelMock, times(1)).setSystemMessage(errorMessage);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}