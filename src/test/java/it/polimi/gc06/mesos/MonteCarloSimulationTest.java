package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameTurnManager.EndOfRoundPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class MonteCarloSimulationTest {

    private static final int SESSIONS = 3;
    private static final int ITERATIONS_PER_SESSION = 1000;
    private static final boolean VERBOSE = false;

    // Immutable records to encapsulate results (Functional Programming approach)
    record GameResult(int totalPrestige, int totalFood, int maxPrestige, int minPrestige) {
    }

    record SessionResult(double meanPrestige, double meanFood, int maxPrestige, int minPrestige) {
    }

    @Test
    @DisplayName("Monte Carlo Simulation: " + SESSIONS + " sessions of " + ITERATIONS_PER_SESSION + " matches each")
    void runMonteCarloSimulation() {
        long startTime = System.currentTimeMillis();

        // Run all sessions and map them into a list of results
        List<SessionResult> sessionResults = IntStream.range(0, SESSIONS)
                .mapToObj(i -> {
                    System.out.println("-> Starting Session " + (i + 1) + " of " + SESSIONS + "...");
                    return runSession(ITERATIONS_PER_SESSION);
                })
                .toList();

        // Calculate global means across all sessions
        double globalMeanPrestige = sessionResults.stream().mapToDouble(SessionResult::meanPrestige).average().orElse(0.0);
        double globalMeanFood = sessionResults.stream().mapToDouble(SessionResult::meanFood).average().orElse(0.0);

        // Calculate Population Variance
        double variancePrestige = sessionResults.stream()
                .mapToDouble(s -> Math.pow(s.meanPrestige() - globalMeanPrestige, 2))
                .average().orElse(0.0);

        double varianceFood = sessionResults.stream()
                .mapToDouble(s -> Math.pow(s.meanFood() - globalMeanFood, 2))
                .average().orElse(0.0);

        int absoluteMax = sessionResults.stream().mapToInt(SessionResult::maxPrestige).max().orElse(0);
        int absoluteMin = sessionResults.stream().mapToInt(SessionResult::minPrestige).min().orElse(0);

        long endTime = System.currentTimeMillis();

        System.out.println("\n====== MONTE CARLO RESULTS (" + SESSIONS + " Sessions of " + ITERATIONS_PER_SESSION + " Matches) ======");
        System.out.println("Total time elapsed: " + (endTime - startTime) + " ms");
        System.out.printf("Prestige Points: Global Mean = %.3f | Variance = %.3f%n", globalMeanPrestige, variancePrestige);
        System.out.printf("Food Tokens:     Global Mean = %.3f | Variance = %.3f%n", globalMeanFood, varianceFood);
        System.out.println("Highest Absolute Score: " + absoluteMax);
        System.out.println("Lowest Absolute Score:  " + absoluteMin);
        System.out.println("=====================================================================================");
    }

    /**
     * Aggregates the results of a single iterative session using IntSummaryStatistics.
     */
    private SessionResult runSession(int iterations) {
        List<GameResult> games = IntStream.range(0, iterations)
                .mapToObj(this::simulateSingleGame)
                .toList();

        IntSummaryStatistics prestigeStats = games.stream().mapToInt(GameResult::totalPrestige).summaryStatistics();
        IntSummaryStatistics foodStats = games.stream().mapToInt(GameResult::totalFood).summaryStatistics();

        // Divide by the number of players to get the average per single player (Assuming 3 players)
        double meanPrestigePerPlayer = prestigeStats.getAverage() / 3.0;
        double meanFoodPerPlayer = foodStats.getAverage() / 3.0;

        int max = games.stream().mapToInt(GameResult::maxPrestige).max().orElse(0);
        int min = games.stream().mapToInt(GameResult::minPrestige).min().orElse(0);

        return new SessionResult(meanPrestigePerPlayer, meanFoodPerPlayer, max, min);
    }

    /**
     * Initialization, execution, and measurement of a single isolated match.
     */
    private GameResult simulateSingleGame(int gameIndex) {
        if (VERBOSE) System.out.println("\n================ MATCH " + gameIndex + " ================");

        GameController controller = setupSimulatedGame();
        GameModel model = controller.getModel();

        Color[] colors = Color.values();
        int cIndex = 0;
        for (Player p : model.getPlayers()) {
            controller.handleChooseTotemColor(p.getNickname(), colors[cIndex++]);
        }
        model.startGame();
        if (VERBOSE) System.out.println("Started match");

        int turnSafetyLimit = 2000;
        int turnCount = 0;

        while (!controller.isGameFinished() && turnCount < turnSafetyLimit) {
            turnCount++;
            Player activePlayer = model.getTurnManager().getActivePlayer();
            String nick = activePlayer.getNickname();
            String phaseName = model.getTurnManager().getPhase().getClass().getSimpleName();

            if (VERBOSE && turnCount % 10 == 0) {
                System.out.println("--- Turn " + turnCount + " | Round: " + model.getTurnManager().getRound() + " | Phase: " + phaseName + " | It's : " + nick + "'s turn ---");
            }

            if (phaseName.equals(PlacingTotemPhase.class.getSimpleName())) {
                executeRandomTotemPlacement(controller, model, nick);
            } else if (phaseName.equals(OfferResolutionPhase.class.getSimpleName())) {
                executeRandomPick(controller, model, nick);
            } else if (phaseName.equals(EndOfRoundPhase.class.getSimpleName())) {
                executeRandomEndOfRoundPick(controller, model, nick);
            }
        }

        assertTrue(controller.isGameFinished(), "Test failed! The game got stuck on turn " + turnCount + " in phase " + model.getTurnManager().getPhase().getClass().getSimpleName());

        if (VERBOSE) System.out.println("================ GAME ENDED " + gameIndex + " ================\n");

        int sumPrestige = 0, sumFood = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (Player p : model.getPlayers()) {
            int score = p.getPrestigeTokens();
            int food = p.getFoodTokens();
            sumPrestige += score;
            sumFood += food;
            if (score > max) max = score;
            if (score < min) min = score;
        }

        return new GameResult(sumPrestige, sumFood, max, min);
    }

    private void executeRandomEndOfRoundPick(GameController controller, GameModel model, String nick) {
        Board board = model.getBoard();
        Random rand = new Random();

        List<Integer> actions = Arrays.asList(0, 1, 2);
        Collections.shuffle(actions);

        for (int action : actions) {
            try {
                if (action == 0 && !board.getTopRow().isEmpty()) {
                    int id = rand.nextInt(board.getTopRow().size());
                    controller.handleCardPickTopRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " bought a Tribe card from the top row (Index " + id + ")");
                    return;
                } else if (action == 1 && !board.getTopBuildings().isEmpty()) {
                    int id = rand.nextInt(board.getTopBuildings().size());
                    controller.handleBuildingPickTopRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " bought a Building card from the top row (Index " + id + ")");
                    return;
                } else if (action == 2) {
                    controller.handlePickSkip(nick);
                    if (VERBOSE)
                        System.out.println(nick + " has skipped the special draw at the end of the round.");
                    return;
                }
            } catch (IllegalGameActionException | IllegalArgumentException |
                     IndexOutOfBoundsException ignored) {
                // The rules of the game blocked this action. The bot will try another one.
            } catch (Exception e) {
                System.err.println("\nGame crashed during EndOfRound!");
                e.printStackTrace();
                throw new RuntimeException("Game crashed during EndOfRound!", e);
            }
        }
    }

    private void executeRandomTotemPlacement(GameController controller, GameModel model, String nick) {
        Random rand = new Random();
        int trackSize = model.getBoard().getOfferTrack().size();

        for (int i = 0; i < 50; i++) {
            int randomSlot = rand.nextInt(trackSize);
            try {
                controller.handleTotemOfferTilePlacement(nick, randomSlot);
                if (VERBOSE)
                    System.out.println(nick + " placed a totem in slot number " + randomSlot);
                return;
            } catch (IllegalGameActionException | IllegalArgumentException |
                     IndexOutOfBoundsException ignored) {
                // The slot was occupied or invalid. The bot will try another one.
            } catch (Exception e) {
                System.err.println("\nGame crashed during totem placement!");
                e.printStackTrace();
                throw new RuntimeException("Game crashed during totem placement", e);
            }
        }
    }

    private void executeRandomPick(GameController controller, GameModel model, String nick) {
        Board board = model.getBoard();
        Random rand = new Random();

        List<Integer> actions = Arrays.asList(0, 1, 2, 3, 4);
        Collections.shuffle(actions);

        for (int action : actions) {
            try {
                if (action == 0 && !board.getBottomRow().isEmpty()) {
                    int id = rand.nextInt(board.getBottomRow().size());
                    controller.handleCardPickBottomRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " bought a Tribe card from the bottom row (Index " + id + ")");
                    return;
                } else if (action == 1 && !board.getTopRow().isEmpty()) {
                    int id = rand.nextInt(board.getTopRow().size());
                    controller.handleCardPickTopRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " bought a Tribe card from the top row (Index " + id + ")");
                    return;
                } else if (action == 2 && !board.getBottomBuildings().isEmpty()) {
                    int id = rand.nextInt(board.getBottomBuildings().size());
                    controller.handleBuildingPickBottomRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " has bought a Building from the bottom row (Index " + id + ")");
                    return;
                } else if (action == 3 && !board.getTopBuildings().isEmpty()) {
                    int id = rand.nextInt(board.getTopBuildings().size());
                    controller.handleBuildingPickTopRow(nick, id);
                    if (VERBOSE)
                        System.out.println(nick + " has bought a Building from the top row (Index " + id + ")");
                    return;
                } else if (action == 4) {
                    controller.handlePickSkip(nick);
                    if (VERBOSE) System.out.println(nick + " has skipped the purchase turn.");
                    return;
                }
            } catch (IllegalGameActionException | IllegalArgumentException |
                     IndexOutOfBoundsException ignored) {
                // Not enough food or requirements not met. The bot will try the next random action.
            } catch (Exception e) {
                System.err.println("\nGame crashed during card pick!");
                e.printStackTrace();
                throw new RuntimeException("Game crashed during card pick!", e);
            }
        }
    }

    private GameController setupSimulatedGame() {
        DTONotifier dummyNotifier = mock(DTONotifier.class);
        ModelInstancesManager manager = new ModelInstancesManager(dummyNotifier);
        List<String> nicknames = new ArrayList<>(List.of("Bot_Alpha", "Bot_Beta", "Bot_Gamma"));

        try {
            GameModel model = manager.createGame(nicknames);
            return new GameController(model, dummyNotifier);
        } catch (Exception e) {
            fail("Failed whilst initializing the game: " + e.getMessage());
            return null;
        }
    }
}