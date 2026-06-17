package it.polimi.gc06.mesos.dtos;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DTOVisitorTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting DTOVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending DTOVisitorTest ---");
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testBaseVisitMethodsDoNotThrow() {
        DTOVisitor defaultVisitor = new DTOVisitor() {};

        assertDoesNotThrow(() -> defaultVisitor.visit(mock(SmallModelEditor.class)));
        assertDoesNotThrow(() -> defaultVisitor.visit(mock(PlayerJoinedLobbyDTO.class)));
    }

    @Test
    void testDelegationToSmallModelEditorVisit() {
        class TrackingVisitor extends DTOVisitor {
            int delegationCount = 0;

            @Override
            public void visit(SmallModelEditor dto) {
                delegationCount++;
            }
        }

        TrackingVisitor visitor = new TrackingVisitor();

        visitor.visit(mock(LeaderboardChangeDTO.class));
        visitor.visit(mock(BuildingsRefillDTO.class));
        visitor.visit(mock(LobbyInitializedDTO.class));
        visitor.visit(mock(PickBottomRowDTO.class));
        visitor.visit(mock(PickBottomBuildingsDTO.class));
        visitor.visit(mock(PickTopRowDTO.class));
        visitor.visit(mock(PickTopBuildingsDTO.class));
        visitor.visit(mock(PlayerResourcesChangeDTO.class));
        visitor.visit(mock(PlayerStateChangeDTO.class));
        visitor.visit(mock(TopRowRefillDTO.class));
        visitor.visit(mock(TotemOfferMoveDTO.class));
        visitor.visit(mock(PhaseChangeDTO.class));
        visitor.visit(mock(EraChangeDTO.class));
        visitor.visit(mock(RoundChangeDTO.class));
        visitor.visit(mock(TotemTurnMoveDTO.class));
        visitor.visit(mock(EventResolvedDTO.class));
        visitor.visit(mock(ChooseTotemColorDTO.class));
        visitor.visit(mock(MesosStartedDTO.class));
        visitor.visit(mock(PlayerRecapDTO.class));
        visitor.visit(mock(EndGameDTO.class));
        visitor.visit(mock(GameResumeDTO.class));

        assertEquals(21, visitor.delegationCount);

        visitor.visit(mock(PlayerJoinedLobbyDTO.class));
        assertEquals(21, visitor.delegationCount);
    }
}