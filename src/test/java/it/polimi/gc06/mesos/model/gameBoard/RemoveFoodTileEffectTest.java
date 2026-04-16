package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Player;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveFoodTileEffectTest {

    private RemoveFoodTileEffect effect;
    private Player mockPlayer;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting RemoveFoodTileEffectTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending RemoveFoodTileEffectTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        effect = new RemoveFoodTileEffect();
        mockPlayer = mock(Player.class);

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testExecuteRemovesFoodSuccessfully() {
        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).removeFoodTokens(1);
        verify(mockPlayer, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testExecuteRemovesPrestigeWhenNoFood() {
        doThrow(new IllegalStateException("Not enough food")).when(mockPlayer).removeFoodTokens(1);

        assertDoesNotThrow(() -> effect.execute(mockPlayer));

        verify(mockPlayer, times(1)).removePrestigeTokens(2);
    }

    @Test
    void testExecuteThrowsExceptionWhenPlayerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> effect.execute(null));
    }

    @Test
    void testAccept() {
        TileEffectVisitor mockVisitor = mock(TileEffectVisitor.class);
        effect.accept(mockVisitor);
        verify(mockVisitor, times(1)).visit(effect);
    }
}