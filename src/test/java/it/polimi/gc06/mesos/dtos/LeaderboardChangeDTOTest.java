package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaderboardChangeDTOTest {

    private LeaderboardChangeDTO dto;
    private List<Score> leaderboardMock;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting LeaderboardChangeDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending LeaderboardChangeDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        leaderboardMock = mock(List.class);
        dto = new LeaderboardChangeDTO(leaderboardMock);
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
        verify(smallModelMock, times(1)).setLeaderboard(leaderboardMock);
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}