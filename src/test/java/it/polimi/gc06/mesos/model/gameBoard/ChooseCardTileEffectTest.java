package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChooseCardTileEffectTest {

    private Player mockPlayer;

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
    void setUp() {
        mockPlayer = mock(Player.class);
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
}