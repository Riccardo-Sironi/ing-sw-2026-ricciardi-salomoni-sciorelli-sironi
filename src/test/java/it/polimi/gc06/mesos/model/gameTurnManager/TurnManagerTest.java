package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnManagerTest {

    private TurnManager turnManager;

    private final ModifierBuildingsRegistry registryMock = mock(ModifierBuildingsRegistry.class);
    private final ModifierBuildingCard modifierCardMock = mock(ModifierBuildingCard.class);
    private final Player player1 = mock(Player.class);
    private final Player player2 = mock(Player.class);
    private final GameModel gameModelMock = mock(GameModel.class);
    private final Phase phaseMock = mock(Phase.class);

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting TurnManagerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending TurnManagerTest ---");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        when(registryMock.get(ModifierBuildingRegistryKey.PICK_FROM_TOP)).thenReturn(modifierCardMock);

        List<Player> players = new ArrayList<>(List.of(player1, player2));
        turnManager = new TurnManager(players, registryMock);

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    // --- INITIALIZATION ---

    @Test
    @DisplayName("Constructor initializes TurnManager with correct default values and shuffles players")
    void constructor_InitializesCorrectly() {
        assertNotNull(turnManager.getPhase());
        assertInstanceOf(PlacingTotemPhase.class, turnManager.getPhase());

        assertEquals(0, turnManager.getRound());
        assertNull(turnManager.getGameModel());
        assertEquals(modifierCardMock, turnManager.getPickFromTopCard());

        assertEquals(2, turnManager.getPlayersOrder().size());
        assertTrue(turnManager.getPlayersOrder().contains(player1));
        assertTrue(turnManager.getPlayersOrder().contains(player2));

        assertEquals(turnManager.getPlayersOrder().getFirst(), turnManager.getActivePlayer());
    }

    // --- GETTERS AND SETTERS ---

    @Test
    @DisplayName("setPhase and getPhase work correctly")
    void setAndGetPhase() {
        turnManager.setPhase(phaseMock);
        assertEquals(phaseMock, turnManager.getPhase());
    }

    @Test
    @DisplayName("setRound and getRound work correctly")
    void setAndGetRound() {
        turnManager.setRound(5);
        assertEquals(5, turnManager.getRound());
    }

    @Test
    @DisplayName("setActivePlayerIndex and getActivePlayer work correctly")
    void setAndGetActivePlayer() {
        Player expectedPlayer = turnManager.getPlayersOrder().get(1);

        turnManager.setActivePlayerIndex(1);

        assertEquals(expectedPlayer, turnManager.getActivePlayer());
    }

    @Test
    @DisplayName("setGameModel and getGameModel work correctly")
    void setAndGetGameModel() {
        turnManager.setGameModel(gameModelMock);
        assertEquals(gameModelMock, turnManager.getGameModel());
    }
}