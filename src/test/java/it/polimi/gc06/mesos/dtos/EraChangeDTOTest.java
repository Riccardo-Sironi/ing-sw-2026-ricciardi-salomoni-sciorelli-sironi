package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EraChangeDTOTest {

    private EraChangeDTO dto;
    private final Era testEra = Era.ERA_II;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EraChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EraChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        dto = new EraChangeDTO(testEra);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(testEra, dto.getEra());
        assertNull(dto.getSequenceNumber());
    }

    @Test
    void testSequenceNumber() {
        dto.setSequenceNumber(5);
        assertEquals(5, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);

        dto.edit(smallModelMock);

        verify(smallModelMock, times(1)).setEra(testEra);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}