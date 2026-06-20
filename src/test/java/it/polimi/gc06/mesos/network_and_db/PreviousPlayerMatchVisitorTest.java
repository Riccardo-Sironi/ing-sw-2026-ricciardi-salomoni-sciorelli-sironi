package it.polimi.gc06.mesos.network_and_db;

import it.polimi.gc06.mesos.network.server.matches.Match;
import it.polimi.gc06.mesos.network.server.matches.PreviousPlayerMatchVisitor;
import it.polimi.gc06.mesos.network.server.matches.RestoredMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PreviousPlayerMatchVisitorTest {

    @Test
    @DisplayName("Standard match allows any player to enter")
    void testVisitStandardMatch() {
        PreviousPlayerMatchVisitor visitor = new PreviousPlayerMatchVisitor("Alice");
        Match match = mock(Match.class);

        visitor.visit(match);

        assertTrue(visitor.canEnter());
    }

    @Test
    @DisplayName("Restored match allows previous player to enter")
    void testVisitRestoredMatchAllowed() {
        PreviousPlayerMatchVisitor visitor = new PreviousPlayerMatchVisitor("Bob");
        RestoredMatch match = mock(RestoredMatch.class);
        when(match.getPreviousPlayers()).thenReturn((Map<String, Boolean>) Map.of("Bob", true, "Charlie", true));

        visitor.visit(match);

        assertTrue(visitor.canEnter());
    }

    @Test
    @DisplayName("Restored match denies entry to non-previous player")
    void testVisitRestoredMatchDenied() {
        PreviousPlayerMatchVisitor visitor = new PreviousPlayerMatchVisitor("Eve");
        RestoredMatch match = mock(RestoredMatch.class);
        when(match.getPreviousPlayers()).thenReturn((Map<String, Boolean>) Map.of("Bob", false, "Charlie", false));

        visitor.visit(match);

        assertFalse(visitor.canEnter());
    }
}