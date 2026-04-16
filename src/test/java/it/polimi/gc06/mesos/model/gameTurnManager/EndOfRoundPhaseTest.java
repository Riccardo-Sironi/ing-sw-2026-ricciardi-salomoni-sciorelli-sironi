package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.HunterCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.RemoveFoodTileEffect;
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
    void setUp(TestInfo testInfo) {

        endOfRoundPhase = new EndOfRoundPhase();
        turnManager = mock(TurnManager.class);
        player = mock(Player.class);
        board = mock(Board.class);
        gameModel = mock(GameModel.class);
//        ModifierBuildingCard mockPickCard = mock(ModifierBuildingCard.class);
//        when(turnManager.getPickFromTopCard()).thenReturn(mockPickCard);
//        when(player.getBuildingCards()).thenReturn(new ArrayList<>());
        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    @DisplayName("resolveEvent with players without the pick building")
    void test_resolveEvent_NoPickCard() {
        EndOfRoundPhase phase = new EndOfRoundPhase();

        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player);
        when(turnManager.getPlayersOrder()).thenReturn(players);

        phase.endOfRound(turnManager, board, gameModel);
    }

    @Test
    @DisplayName("resolveEvent with players with the pick building")
    void test_resolveEvent_PickCard() {
        EndOfRoundPhase phase = new EndOfRoundPhase();
        ModifierBuildingCard pickCard = mock(ModifierBuildingCard.class);


        when(turnManager.getPickFromTopCard()).thenReturn(pickCard);


        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        Player playerWithPickCard = new Player("playerWithPickCard", Color.BLUE, new ModifierBuildingsRegistry());
        playerWithPickCard.addBuildingCards(pickCard);
        players.add(playerWithPickCard);
        when(turnManager.getPlayersOrder()).thenReturn(players);

        phase.endOfRound(turnManager, board, gameModel);
    }

    @Test
    void test_pickCardFromTop() {
        EndOfRoundPhase phase = new EndOfRoundPhase();
        ModifierBuildingCard pickCard = mock(ModifierBuildingCard.class);

        Player player = new Player("player", Color.BLUE, new ModifierBuildingsRegistry());
        player.addBuildingCards(pickCard);
        ArrayList<Player> players = new ArrayList<>();

        when(turnManager.getPickFromTopCard()).thenReturn(pickCard);

        players.add(player);
        when(turnManager.getPlayersOrder()).thenReturn(players);

        phase.endOfRound(turnManager, board, gameModel);

        HunterCard card = new HunterCard(Era.ERA_I, true);
        ArrayList<TribeCard> deck = new ArrayList<>();
        deck.add(card);
        when(board.getTopRow()).thenReturn(deck);

        phase.endOfRound(turnManager, board, gameModel);

        phase.pickCardFromTop(turnManager, player, card, board);
    }
}
