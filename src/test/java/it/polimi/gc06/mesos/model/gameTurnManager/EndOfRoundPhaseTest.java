package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EndOfRoundPhaseTest {

    private EndOfRoundPhase endOfRoundPhase;
    private TurnManager turnManager;
    private Player player;
    private Board board;
    private GameModel gameModel;

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting EndOfRoundPhaseTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending EndOfRoundPhaseTest <<<");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {
        endOfRoundPhase = new EndOfRoundPhase();
        turnManager = mock(TurnManager.class);
        player = mock(Player.class);
        board = mock(Board.class);
        gameModel = mock(GameModel.class);
        ModifierBuildingCard mockPickCard = mock(ModifierBuildingCard.class);
        when(turnManager.getPickFromTopCard()).thenReturn(mockPickCard);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    void illegalActions_ThrowIllegalPhaseActionException() {
        CharacterCard card = mock(CharacterCard.class);
        assertThrows(IllegalPhaseActionException.class, () -> endOfRoundPhase.pickCardFromTop(turnManager, player, card, board));
    }

    @Test
    void pickCard_With_ZeroTopDrawNum_ThrowsException() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        assertDoesNotThrow(() -> endOfRoundPhase.checkForEndOfRoundPick(turnManager));

        when(player.getTopDrawNum()).thenReturn(0);
        CharacterCard card = mock(CharacterCard.class);

        assertThrows(IllegalPhaseActionException.class, () -> endOfRoundPhase.pickCardFromTop(turnManager, player, card, board));
    }

    @Test
    void pickCardFromTop_NormalExecution() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        assertDoesNotThrow(() -> endOfRoundPhase.checkForEndOfRoundPick(turnManager));

        when(player.getTopDrawNum()).thenReturn(1);
        CharacterCard card = mock(CharacterCard.class);

        assertDoesNotThrow(() -> endOfRoundPhase.pickCardFromTop(turnManager, player, card, board));

        verify(board).pickCardFromTopRow(player, card);
        verify(player, times(2)).setTopDrawNum(0);
    }

    @Test
    void picCardFromTop_Building_NormalExecution() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        assertDoesNotThrow(() -> endOfRoundPhase.checkForEndOfRoundPick(turnManager));

        when(player.getTopDrawNum()).thenReturn(1);
        BuildingCard card = mock(BuildingCard.class);

        assertDoesNotThrow(() -> endOfRoundPhase.pickCardFromTop(turnManager, player, card, board));

        assertDoesNotThrow(() -> verify(board).buyBuildingFromTopRow(player, card));
        verify(player, times(2)).setTopDrawNum(0);
    }

    @Test
    void EndOfRoundLogic() {
        when(turnManager.getRound()).thenReturn(1);
        when(board.isEndGame()).thenReturn(false);

        assertDoesNotThrow(() -> endOfRoundPhase.endOfRound(turnManager, board, gameModel));

        verify(board).moveFromTopToBottom();
        verify(board).populateTopRow(gameModel);
        verify(turnManager).setRound(2);
    }
}
