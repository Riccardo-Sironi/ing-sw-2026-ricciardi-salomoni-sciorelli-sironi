package it.polimi.gc06.mesos.controller;

import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.InstancesManager.ModelInstancesManager;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

// TODO: dobbiamo fare il testing del controller, ma solo dopo aver terminato tutti quelli del model (linear)
class GameControllerTest {
    GameController controller;
    GameModel model;

    @Test
    void test_fullGame() {
        ModelInstancesManager manager = new ModelInstancesManager();
        List<String> playersNames = new ArrayList<>(Arrays.asList("player1", "player2", "player3", "player4", "player5"));


        assertDoesNotThrow(() -> {
            model = manager.createGame(playersNames);
        });

        controller = new GameController(model);

        controller.getModel().startGame();

        while (true) {
            playRound();
            if (controller.isGameFinished()) {
                System.out.println("Game finished");

                for (Player p : model.getPlayers()) {
                    System.out.println(p.getNickname() + " prestigeTokens: " + p.getPrestigeTokens() + " | foodTokens: " + p.getFoodTokens());
                }

                break;
            }
        }

    }

    private void playRound() {
        System.out.print("\n__________________________ ROUND " + controller.getModel().getTurnManager().getRound() + " _________________________________\n");

        int nPlayers = controller.getModel().getTurnManager().getPlayersOrder().size();

        ArrayList<Integer> choiceList = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));

        Collections.shuffle(choiceList);

        for (int i = 0; i < nPlayers; i++) {
            String movingPlayer = model.getBoard().getTurnOrderTile().slots().get(i).getPlayer().getNickname();

            System.out.println("____________________________________________________________");
            printTurnOrderTile();
            printOfferTrack();

            System.out.println("\n  [  Moving " + movingPlayer + " from turn order tile " + i + " to offer track tile " + choiceList.get(i) + "  ]\n");

            controller.handleTotemOfferTilePlacement(movingPlayer, choiceList.get(i));

            printTurnOrderTile();
            printOfferTrack();
            System.out.println("____________________________________________________________");
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        int offerTrackSize = controller.getModel().getBoard().getOfferTrack().size();

        for (int i = 0; i < offerTrackSize; i++) {
            Player pickingPlayer = controller.getModel().getBoard().getOfferTrack().get(i).getPlayer();

            if (pickingPlayer != null) {
                String pickingPlayerNickname = pickingPlayer.getNickname();

                CharacterCard pickedCard;
                int pickedCardIndex;

                while (pickingPlayer.getTopDrawNum() > 0) {
                    pickedCard = controller.getModel().getBoard().getTopRow().stream().filter(card -> card instanceof CharacterCard).map(card -> (CharacterCard) card).findFirst().orElse(null);
                    if (pickedCard == null) {
                        pickingPlayer.setTopDrawNum(0);
                        controller.getModel().getTurnManager().getPhase().skipPickingPLayer(controller.getModel().getTurnManager());
                        break;
                    }
                    pickedCardIndex = controller.getModel().getBoard().getTopRow().indexOf(pickedCard);
                    topSituation(pickingPlayerNickname, pickedCard, pickedCardIndex);
                }
                while (pickingPlayer.getBottomDrawNum() > 0) {
                    pickedCard = controller.getModel().getBoard().getBottomRow().stream().filter(card -> card instanceof CharacterCard).map(card -> (CharacterCard) card).findFirst().orElse(null);
                    if (pickedCard == null) {
                        pickingPlayer.setBottomDrawNum(0);
                        controller.getModel().getTurnManager().getPhase().skipPickingPLayer(controller.getModel().getTurnManager());
                        break;
                    }
                    pickedCardIndex = controller.getModel().getBoard().getBottomRow().indexOf(pickedCard);
                    bottomSituation(pickingPlayerNickname, pickedCard, pickedCardIndex);
                }
            }
        }


        System.out.println();
        printPlayersOrder();
    }

    private void topSituation(String pickingPlayerNickname, CharacterCard pickedCard, int pickedCardIndex) {
        System.out.println("____________________________________________________________");
        printPlayersOrder();
        printTurnOrderTile();
        printOfferTrack();
        printTopRow();

        System.out.println("\n  [" + pickingPlayerNickname + " is picking card " + pickedCard.getClass().getSimpleName() + " from the top row  ]\n");

        controller.handleCardPickTopRow(pickingPlayerNickname, pickedCardIndex);

        printPlayersOrder();
        printTurnOrderTile();
        printOfferTrack();
        printTopRow();
        System.out.println("____________________________________________________________");
    }

    private void bottomSituation(String pickingPlayerNickname, CharacterCard pickedCard, int pickedCardIndex) {
        System.out.println("____________________________________________________________");
        printPlayersOrder();
        printTurnOrderTile();
        printOfferTrack();
        printBottomRow();

        System.out.println("\n  [" + pickingPlayerNickname + " is picking card " + pickedCard.getClass().getSimpleName() + " from the bottom row  ]\n");

        controller.handleCardPickBottomRow(pickingPlayerNickname, pickedCardIndex);

        printPlayersOrder();
        printTurnOrderTile();
        printOfferTrack();
        printBottomRow();
        System.out.println("____________________________________________________________");
    }

    private void printOfferTrack() {
        ArrayList<TileSlot> offerTrack = (ArrayList<TileSlot>) controller.getModel().getBoard().getOfferTrack();
        System.out.print("offerTrack ");
        for (TileSlot tileSlot : offerTrack) {
            if (tileSlot.getPlayer() == null) {
                System.out.print("| - |");
            } else {
                System.out.print("| " + tileSlot.getPlayer().getNickname() + " |");
            }
        }
        System.out.println();
    }

    private void printTurnOrderTile() {
        TurnOrderTile turnOrderTile = controller.getModel().getBoard().getTurnOrderTile();
        System.out.print("turnOrderTile ");
        for (TileSlot tileSlot : turnOrderTile.slots()) {
            if (tileSlot.getPlayer() == null) {
                System.out.print("| - |");
            } else {
                System.out.print("| " + tileSlot.getPlayer().getNickname() + " |");
            }
        }
        System.out.println();
    }

    private void printTopRow() {
        ArrayList<TribeCard> topRow = controller.getModel().getBoard().getTopRow();
        System.out.print("topRow ");
        for (TribeCard tribeCard : topRow) {
            String cardTitle = tribeCard.getClass().getSimpleName();
            System.out.print("// " + cardTitle + " //");
        }
        System.out.println();
    }

    private void printBottomRow() {
        ArrayList<TribeCard> bottomRow = controller.getModel().getBoard().getBottomRow();
        System.out.print("bottomRow ");
        for (TribeCard tribeCard : bottomRow) {
            String cardTitle = tribeCard.getClass().getSimpleName();
            System.out.print("// " + cardTitle + " //");
        }
        System.out.println();
    }

    void printPlayersOrder() {
        ArrayList<Player> players = (ArrayList<Player>) controller.getModel().getTurnManager().getPlayersOrder();
        System.out.print("playersOrder ");

        for (Player player : players) {
            System.out.print("| " + player.getNickname() + " |");
        }
        System.out.println();
    }
}
