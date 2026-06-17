package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseCardTileEffectTest {

    private Player mockPlayer;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ChooseCardTileEffectTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ChooseCardTileEffectTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        mockPlayer = mock(Player.class);
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testGetters() {
        ChooseCardTileEffect effect = new ChooseCardTileEffect(2, 3);
        assertEquals(2, effect.getNumOfTopCards());
        assertEquals(3, effect.getNumOfBottomCards());
    }

    @Test
    void testExecuteWithConstructedValues() {
        ChooseCardTileEffect effect = new ChooseCardTileEffect(2, 1);

        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).setTopDrawNum(2);
        verify(mockPlayer, times(1)).setBottomDrawNum(1);
    }

    @Test
    void testExecuteWithSetters() {
        ChooseCardTileEffect effect = new ChooseCardTileEffect();
        effect.setNumOfTopCards(0);
        effect.setNumOfBottomCards(3);

        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).setTopDrawNum(0);
        verify(mockPlayer, times(1)).setBottomDrawNum(3);
    }

    @Test
    void testExecuteThrowsExceptionWhenPlayerIsNull() {
        ChooseCardTileEffect effect = new ChooseCardTileEffect(1, 1);
        assertThrows(IllegalArgumentException.class, () -> effect.execute(null));
    }

    @Test
    void testAccept() {
        ChooseCardTileEffect effect = new ChooseCardTileEffect();
        TileEffectVisitor mockVisitor = mock(TileEffectVisitor.class);
        effect.accept(mockVisitor);
        verify(mockVisitor, times(1)).visit(effect);
    }

    @Test
    void testEqualsAndHashCode() {
        ChooseCardTileEffect e1 = new ChooseCardTileEffect(2, 1);
        ChooseCardTileEffect e2 = new ChooseCardTileEffect(2, 1);

        ChooseCardTileEffect e3 = new ChooseCardTileEffect(1, 1);
        ChooseCardTileEffect e4 = new ChooseCardTileEffect(2, 5);

        assertEquals(e1, e1);
        assertEquals(e1, e2);

        assertNotEquals(e1, e3);
        assertNotEquals(e1, e4);

        assertNotEquals(e1, null);
        assertNotEquals(e1, new Object());

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
        assertNotEquals(e1.hashCode(), e4.hashCode());
    }
}