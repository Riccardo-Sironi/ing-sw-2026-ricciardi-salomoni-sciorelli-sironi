package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoundChangeDTOTest {

    private RoundChangeDTO dto;
    private final int testRound = 3;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting RoundChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending RoundChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new RoundChangeDTO(testRound);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(15);
        assertEquals(15, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        dto.edit(smallModelMock);
        verify(smallModelMock, times(1)).setRound(testRound);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}