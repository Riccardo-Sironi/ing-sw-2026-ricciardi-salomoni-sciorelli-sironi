package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EndOfRoundPhaseTest {

    private EndOfRoundPhase endOfRoundPhase;

    private TurnManager turnManager;
    private GameModel gameModel;
    private Board board;
    private Player player0;
    private Player player1;
    private ModifierBuildingCard specialBuildingCard;
    private CharacterCard characterCardToPick;
    private BuildingCard buildingCardToPick;
    private ArrayList<Player> players;
    private DTONotifier notifier;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting EndOfRoundPhaseTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending EndOfRoundPhaseTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        endOfRoundPhase = new EndOfRoundPhase();

        turnManager = mock(TurnManager.class);
        gameModel = mock(GameModel.class);
        board = mock(Board.class);
        player0 = mock(Player.class);
        player1 = mock(Player.class);
        specialBuildingCard = mock(ModifierBuildingCard.class);
        characterCardToPick = mock(CharacterCard.class);
        buildingCardToPick = mock(BuildingCard.class);
        notifier = mock(DTONotifier.class);

        players = new ArrayList<>(List.of(player0, player1));

        when(turnManager.getPlayersOrder()).thenReturn(players);
        when(turnManager.getGameModel()).thenReturn(gameModel);
        when(gameModel.getBoard()).thenReturn(board);
        when(turnManager.getNotifier()).thenReturn(notifier);
        when(player0.getNickname()).thenReturn("Player0");
        when(player1.getNickname()).thenReturn("Player1");
        when(turnManager.getActivePlayer()).thenReturn(players.getFirst());

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void givenNoSpecialBuilding_whenEndOfRound_thenShiftBoardAndAdvanceRound() throws IllegalPhaseActionException {
        when(turnManager.getPlayersOrder()).thenReturn(players);
        when(turnManager.getPickFromTopCard()).thenReturn(specialBuildingCard); // Nessun cast necessario
        when(turnManager.getGameModel()).thenReturn(gameModel);

        when(player0.getBuildingCards()).thenReturn(new ArrayList<>());
        when(player1.getBuildingCards()).thenReturn(new ArrayList<>());

        when(board.isEndGame()).thenReturn(false);
        when(turnManager.getRound()).thenReturn(1);

        endOfRoundPhase.endOfRound(turnManager, board, gameModel);

        verify(board, times(1)).moveFromTopToBottom();
        verify(board, times(1)).populateTopRow(gameModel);
        verify(turnManager, times(1)).setRound(2);
        verify(turnManager, times(1)).setPhase(any(PlacingTotemPhase.class));
        verify(gameModel, never()).endGame();
    }

    @Test
    void givenNoSpecialBuilding_whenEndOfRound_thenTriggerEndGame() throws IllegalPhaseActionException {
        when(turnManager.getPickFromTopCard()).thenReturn(specialBuildingCard);

        when(player0.getBuildingCards()).thenReturn(new ArrayList<>());
        when(player1.getBuildingCards()).thenReturn(new ArrayList<>());

        when(board.isEndGame()).thenReturn(true);

        endOfRoundPhase.endOfRound(turnManager, board, gameModel);

        verify(board, times(1)).moveFromTopToBottom();
        verify(board, times(1)).populateTopRow(gameModel);
        verify(gameModel, times(1)).endGame();
        verify(turnManager, never()).setPhase(any(PlacingTotemPhase.class));
    }

    @Test
    void givenPlayer0HasSpecialBuilding_whenEndOfRound_thenGrantDrawAndWait() throws IllegalPhaseActionException {
        when(turnManager.getPlayersOrder()).thenReturn(players);
        when(turnManager.getPickFromTopCard()).thenReturn(specialBuildingCard);

        when(player0.getBuildingCards()).thenReturn(new ArrayList<>(List.of(specialBuildingCard)));

        endOfRoundPhase.endOfRound(turnManager, board, gameModel);

        verify(player0, times(1)).setTopDrawNum(1);
        verify(turnManager, never()).setActivePlayerIndex(anyInt());
        verify(board, never()).moveFromTopToBottom();
    }

    @Test
    void givenPlayer1HasSpecialBuilding_whenEndOfRound_thenChangeActivePlayerAndGrantDraw() throws IllegalPhaseActionException {
        when(turnManager.getPlayersOrder()).thenReturn(players);
        when(turnManager.getPickFromTopCard()).thenReturn(specialBuildingCard);

        when(player0.getBuildingCards()).thenReturn(new ArrayList<>());
        when(player1.getBuildingCards()).thenReturn(new ArrayList<>(List.of(specialBuildingCard)));

        endOfRoundPhase.endOfRound(turnManager, board, gameModel);

        verify(player1, times(1)).setTopDrawNum(1);
        verify(turnManager, times(1)).setActivePlayerIndex(1);
        verify(board, never()).moveFromTopToBottom();
    }

    @Test
    void givenPlayerWithDrawLeft_whenPickCharacterCard_thenProcessPickAndFinishRound() throws IllegalPhaseActionException {
        triggerSpecialPickStateForPlayer(player1);

        when(player1.getTopDrawNum()).thenReturn(1);
        when(board.isEndGame()).thenReturn(false);
        when(turnManager.getRound()).thenReturn(1);

        endOfRoundPhase.pickCardFromTop(turnManager, player1, characterCardToPick, board);

        verify(board, times(1)).pickCardFromTopRow(player1, characterCardToPick);
        verify(player1, times(1)).setTopDrawNum(0);
        verify(turnManager, times(1)).setActivePlayerIndex(0);
        verify(board, times(1)).moveFromTopToBottom();
    }

    @Test
    void givenPlayerWithDrawLeft_whenPickBuildingCard_thenProcessBuyAndFinishRound() throws IllegalPhaseActionException, IllegalGameActionException {
        triggerSpecialPickStateForPlayer(player0);

        when(player0.getTopDrawNum()).thenReturn(1);
        when(board.isEndGame()).thenReturn(false);
        when(turnManager.getRound()).thenReturn(1);

        endOfRoundPhase.pickCardFromTop(turnManager, player0, buildingCardToPick, board);

        verify(board, times(1)).buyBuildingFromTopRow(player0, buildingCardToPick);
        verify(player0, times(1)).setTopDrawNum(0);
        verify(turnManager, times(1)).setActivePlayerIndex(0);
        verify(board, times(1)).moveFromTopToBottom();
    }

    @Test
    void givenPhaseJustStarted_whenPickCharacter_thenThrowException() {
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () -> {
            endOfRoundPhase.pickCardFromTop(turnManager, player0, characterCardToPick, board);
        });
        assertEquals("You have to start the end of round phase first!", exception.getMessage());
    }

    @Test
    void givenPhaseJustStarted_whenPickBuilding_thenThrowException() {
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () -> {
            endOfRoundPhase.pickCardFromTop(turnManager, player0, buildingCardToPick, board);
        });
        assertEquals("You have to start the offer resolution phase first!", exception.getMessage());
    }

    @Test
    void givenZeroDraws_whenPickCharacter_thenThrowException() throws IllegalPhaseActionException {
        triggerSpecialPickStateForPlayer(player0);
        when(player0.getTopDrawNum()).thenReturn(0);

        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () -> {
            endOfRoundPhase.pickCardFromTop(turnManager, player0, characterCardToPick, board);
        });
        assertEquals("You can't draw from the top row anymore!", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw Exception if player has 0 draws left and tries to pick Building")
    void givenZeroDraws_whenPickBuilding_thenThrowException() throws IllegalPhaseActionException {
        triggerSpecialPickStateForPlayer(player0);
        when(player0.getTopDrawNum()).thenReturn(0);

        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () -> {
            endOfRoundPhase.pickCardFromTop(turnManager, player0, buildingCardToPick, board);
        });
        assertEquals("You can't draw from the top row anymore!", exception.getMessage());
    }

    private void triggerSpecialPickStateForPlayer(Player playerWithCard) throws IllegalPhaseActionException {
        when(turnManager.getPlayersOrder()).thenReturn(players);
        when(turnManager.getPickFromTopCard()).thenReturn(specialBuildingCard);

        for (Player p : players) {
            if (p == playerWithCard) {
                when(p.getBuildingCards()).thenReturn(new ArrayList<>(List.of(specialBuildingCard)));
            } else {
                when(p.getBuildingCards()).thenReturn(new ArrayList<>());
            }
        }

        endOfRoundPhase.endOfRound(turnManager, board, gameModel);

        reset(board);
        reset(turnManager);
        reset(playerWithCard);
        when(turnManager.getGameModel()).thenReturn(gameModel);
    }
}