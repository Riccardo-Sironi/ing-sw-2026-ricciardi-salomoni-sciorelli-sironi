package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhaseChangeDTOTest {

    private PhaseChangeDTO dto;
    private final String testPhase = "ACTION_PHASE";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PhaseChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PhaseChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new PhaseChangeDTO(testPhase);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(testPhase, dto.getPhase());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(12);
        assertEquals(12, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        dto.edit(smallModelMock);
        verify(smallModelMock, times(1)).setPhase(testPhase);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}