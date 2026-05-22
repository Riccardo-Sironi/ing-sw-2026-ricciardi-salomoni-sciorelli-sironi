package it.polimi.gc06.mesos.view.gui.helpers;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.elements.*;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;

public class AnimationsManager {
    /**
     * This method applies a smooth animation to a card when it's picked from the top/bottom rows or the buildings rows and moved to the player's inventory.
     * * The card will visually move from its original position to the player's inventory area, creating a more engaging user experience.
     * * It is called before redrawing the personal decks of the target player, based on the small model data.
     *
     * @param card
     * @param targetContainer
     * @param mainRoot
     * @param onEndAction
     */
    public static void cardPickAnimation(CardView card, HBox targetContainer, HBox mainRoot, Runnable onEndAction) {
        card.setOnMouseClicked(null);

        Bounds cardScreen = card.localToScreen(card.getBoundsInLocal());
        if (cardScreen == null) return;

        OpponentBox parentOpponentBox = null;
        Node current = targetContainer;

        // we look for the parent opponent box of the target container, if it exists, to adjust the animation accordingly
        // in case the opponent box is closed
        while (current != null) {
            boolean isOpponentBox = current.getProperties().get("isOpponentBox") != null;

            if (isOpponentBox) {
                parentOpponentBox = (OpponentBox) current;
                break;
            }
            current = current.getParent();
        }

        Bounds targetScreen = null;
        boolean isOpponentBoxClosed = false;

        if (parentOpponentBox != null) {
            if (!parentOpponentBox.getDetailsContainer().isVisible() || parentOpponentBox.getDetailsContainer().getHeight() <= 0) {
                isOpponentBoxClosed = true;
                targetScreen = parentOpponentBox.localToScreen(parentOpponentBox.getBoundsInLocal());
            }
        }

        if (targetScreen == null) {
            targetScreen = targetContainer.localToScreen(targetContainer.getBoundsInLocal());
        }

        if (targetScreen == null) return;

        Pane overlayPane = new Pane();
        Pane root = (Pane) mainRoot.getScene().getRoot();
        Bounds rootScreen = root.localToScreen(root.getBoundsInLocal());

        overlayPane.prefWidthProperty().bind(root.widthProperty());
        overlayPane.prefHeightProperty().bind(root.heightProperty());
        overlayPane.setMouseTransparent(true);
        root.getChildren().add(overlayPane);

        double cardX = cardScreen.getMinX() - rootScreen.getMinX();
        double cardY = cardScreen.getMinY() - rootScreen.getMinY();
        double targetX = targetScreen.getMinX() - rootScreen.getMinX();
        double targetY = targetScreen.getMinY() - rootScreen.getMinY();

        if (isOpponentBoxClosed) {
            targetX += (targetScreen.getWidth() - cardScreen.getWidth()) / 2;
            targetY += (targetScreen.getHeight() - cardScreen.getHeight()) / 2;
        }

        Pane originalParent = (Pane) card.getParent();
        if (originalParent != null) {
            originalParent.getChildren().remove(card);
        }

        card.relocate(cardX, cardY);
        card.setTranslateX(0);
        card.setTranslateY(0);
        overlayPane.getChildren().add(card);

        // adjust the speed of the animation based on the distance to travel, with a minimum and maximum duration
        double deltaX = targetX - cardX;
        double deltaY = targetY - cardY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double speed = 0.9;
        long durationMs = (long) (distance / speed);
        durationMs = Math.clamp(durationMs, 300, 600);

        TranslateTransition transition = EffectsManager.createCardMoveTransition(card, deltaX, deltaY, durationMs);
        transition.setInterpolator(Interpolator.EASE_OUT);

        transition.setOnFinished(e -> {
            overlayPane.getChildren().remove(card);
            root.getChildren().remove(overlayPane);
            card.setTranslateX(0);
            card.setTranslateY(0);
            // the bind below ensure that the card is the same size of the others visually, it matters just during the
            // animation, then we re draw the inventory, and it will be draw like the others
            card.fitHeightProperty().bind(targetContainer.heightProperty().multiply(1));
            EffectsManager.normalCard(card);
            card.setOnMouseEntered(null);

            if (onEndAction != null) onEndAction.run();
        });

        transition.play();
    }

    /**
     * This method applies a smooth animation to a totem piece when the player places it on an offer tile.
     * The totem piece will visually move from the player's turn order tile to the target offer tile, creating a more
     * engaging user experience.
     *
     * @param tile
     * @param turnOrderTile
     * @param offerTrackTiles
     * @param mainRoot
     * @param onEndAction
     */
    public static void totemSetAnimation(OfferTileView tile, String owner, TurnOrderTileView turnOrderTile, ArrayList<OfferTileView> offerTrackTiles, HBox mainRoot, Runnable onEndAction) {
        TotemPieceView totemMoved = turnOrderTile.getTotemPieces().stream()
                .filter(tp -> tp.getPlayer() != null && tp.getPlayer().getNickname().equals(owner))
                .findFirst()
                .orElse(null);

        if (totemMoved != null) {
            // prevent the player from click others tiles during animation
            for (OfferTileView t : offerTrackTiles) {
                t.setOnMouseClicked(null);
                EffectsManager.defaultTile(t);
            }
            tile.setOnMouseClicked(null);

            Bounds totemScreen = totemMoved.localToScreen(totemMoved.getBoundsInLocal());
            Bounds tileScreen = tile.localToScreen(tile.getBoundsInLocal());

            // if the bounds are null, it means that the node is not currently rendered on the screen,
            // so we skip the animation and directly send the network request
            if (totemScreen == null || tileScreen == null) {
                if (onEndAction != null) onEndAction.run();
                return;
            }

            Pane overlayPane = new Pane();
            Pane root = (Pane) mainRoot.getScene().getRoot();
            Bounds rootScreen = root.localToScreen(root.getBoundsInLocal());

            overlayPane.prefWidthProperty().bind(root.widthProperty());
            overlayPane.prefHeightProperty().bind(root.heightProperty());
            overlayPane.setMouseTransparent(true);
            root.getChildren().add(overlayPane);

            // we correctly scale the ghost totem to the dimension of real totems
            double tileTrueHeight = tileScreen.getHeight();

            double targetTotemHeight = tileTrueHeight * 0.25;

            ImageView ghostTotem = new ImageView(totemMoved.getImage());
            ghostTotem.setPreserveRatio(true);
            ghostTotem.setFitHeight(targetTotemHeight);


            double imageRatio = totemMoved.getImage().getWidth() / totemMoved.getImage().getHeight();
            double targetTotemWidth = targetTotemHeight * imageRatio;


            double startX = totemScreen.getMinX() - rootScreen.getMinX() + (totemScreen.getWidth() - targetTotemWidth) / 2;
            double startY = totemScreen.getMinY() - rootScreen.getMinY() + (totemScreen.getHeight() - targetTotemHeight) / 2;

            double targetCenterX = tileScreen.getMinX() + (tileScreen.getWidth() * 0.5) - rootScreen.getMinX();
            double targetCenterY = tileScreen.getMinY() + (tileScreen.getHeight() * 0.2) - rootScreen.getMinY();

            double targetX = targetCenterX - (targetTotemWidth / 2);
            double targetY = targetCenterY - (targetTotemHeight / 2);

            ghostTotem.relocate(startX, startY);
            overlayPane.getChildren().add(ghostTotem);

            // hide the totem piece in the turn order tile during the animation
            totemMoved.setVisible(false);

            double deltaX = targetX - startX;
            double deltaY = targetY - startY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            long durationMs = (long) (distance / 0.9);
            durationMs = Math.clamp(durationMs, 300, 650);

            TranslateTransition transition = new TranslateTransition(Duration.millis(durationMs), ghostTotem);
            transition.setToX(deltaX);
            transition.setToY(deltaY);
            transition.setInterpolator(Interpolator.EASE_OUT);

            transition.setOnFinished(ev -> {
                root.getChildren().remove(overlayPane);
                if (onEndAction != null) onEndAction.run();
            });

            transition.play();
        } else {
            if (onEndAction != null) onEndAction.run();
        }
    }

    public static void refillCardsRowAnimation(List<Card> top, List<Card> bottom, HBox topTarget, HBox bottomTarget, Runnable onEndAction) {
        topTarget.getChildren().clear();
        bottomTarget.getChildren().clear();

        animateCardList(0, top, topTarget, () -> {
            animateCardList(0, bottom, bottomTarget, () -> {
                if (onEndAction != null) onEndAction.run();
            });
        });
    }

    private static void animateCardList(int index, List<Card> cards, HBox targetBox, Runnable onListEnd) {
        if (index >= cards.size()) {
            if (onListEnd != null) onListEnd.run();
            return;
        }

        Card card = cards.get(index);
        if (card == null) {
            animateCardList(index + 1, cards, targetBox, onListEnd);
            return;
        }

        CardView cv = new CardView(imageFetcher.fetch(card));
        cv.setPreserveRatio(true);
        cv.fitHeightProperty().bind(targetBox.heightProperty().multiply(0.85));

        cv.setOpacity(0);
        cv.setTranslateY(-50);
        targetBox.getChildren().add(cv);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), cv);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition land = new TranslateTransition(Duration.millis(250), cv);
        land.setFromY(-50);
        land.setToY(0);
        land.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition parallel = new ParallelTransition(fadeIn, land);
        parallel.setOnFinished(ev -> animateCardList(index + 1, cards, targetBox, onListEnd));
        
        parallel.play();
    }
}
