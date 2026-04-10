package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndGameBuildingCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testPrestigeGain(){
        ToIntFunction<Player> mockEffect = p -> 25;
        EndGameBuildingCard endGameBuildingCard = new EndGameBuildingCard(Era.ERA_III, 5, 2,  mockEffect);

        assertThrows(IllegalArgumentException.class, () -> endGameBuildingCard.getPrestigeGain(null));

        Player mockPlayer = mock(Player.class);
        assertEquals(30, endGameBuildingCard.getPrestigeGain(mockPlayer), "Prestige gain should return 30 (25 + 5)");
    }

    @Test
    void testSetPrestigeEffect(){
        EndGameBuildingCard endGameBuildingCard = new EndGameBuildingCard();

        assertThrows(IllegalArgumentException.class, () -> endGameBuildingCard.setPrestigeEffect(null));
        assertDoesNotThrow(() -> endGameBuildingCard.setPrestigeEffect(EndGameBuildingFunction.COUNT_ARTISTS));
        assertThrows(IllegalStateException.class, () -> endGameBuildingCard.setPrestigeEffect(EndGameBuildingFunction.COUNT_BUILDERS));
    }

    @Test
    void testAccept(){
        CardVisitor visitor = mock(CardVisitor.class);
        EndGameBuildingCard endGameBuildingCard = new EndGameBuildingCard();
        endGameBuildingCard.accept(visitor);

        verify(visitor, times(1)).visit(endGameBuildingCard);
    }

}