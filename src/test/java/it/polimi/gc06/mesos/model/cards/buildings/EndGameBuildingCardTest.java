package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EndGameBuildingCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EndGameBuildingCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EndGameBuildingCardTest ---");
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
    void testConstructorsAndGetters(){
        EndGameBuildingCard card1 = new EndGameBuildingCard(Era.ERA_III, 5, 2, EndGameBuildingFunction.FIXED_25);
        assertEquals(Era.ERA_III, card1.getEra());
        assertEquals(2, card1.getFoodCost());
        assertEquals(EndGameBuildingFunction.FIXED_25, card1.getPrestigeEffect());

        EndGameBuildingCard card2 = new EndGameBuildingCard();
        assertNull(card2.getEra());
        assertNull(card2.getPrestigeEffect());
        assertEquals(-1, card2.getFoodCost());
    }

    @Test
    void testPrestigeGain(){
        EndGameBuildingCard endGameBuildingCard = new EndGameBuildingCard(Era.ERA_III, 5, 2,  EndGameBuildingFunction.FIXED_25);

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

    @Test
    void testEqualsAndHashCode(){
        EndGameBuildingCard card1 = new EndGameBuildingCard(Era.ERA_III, 5, 2, EndGameBuildingFunction.FIXED_25);
        EndGameBuildingCard card2 = new EndGameBuildingCard(Era.ERA_III, 5, 2, EndGameBuildingFunction.FIXED_25);
        EndGameBuildingCard card3 = new EndGameBuildingCard(Era.ERA_III, 5, 2, EndGameBuildingFunction.COUNT_ARTISTS);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertNotEquals(card1, new Object());

        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }
}