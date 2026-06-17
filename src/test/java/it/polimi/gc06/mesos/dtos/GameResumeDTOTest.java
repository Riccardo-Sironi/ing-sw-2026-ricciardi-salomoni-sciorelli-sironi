package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameResumeDTOTest {

    private GameResumeDTO dto;
    private SmallModel resumeReferenceMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting GameResumeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending GameResumeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        resumeReferenceMock = mock(SmallModel.class);
        dto = new GameResumeDTO(resumeReferenceMock);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumberIsIgnored() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(5);
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        dto.edit(smallModelMock);
        verify(smallModelMock, times(1)).copy(resumeReferenceMock);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }

    @Test
    void testToString() {
        String expectedString = "ResumeReferenceTestString";
        when(resumeReferenceMock.toString()).thenReturn(expectedString);

        assertEquals(expectedString, dto.toString());
    }
}