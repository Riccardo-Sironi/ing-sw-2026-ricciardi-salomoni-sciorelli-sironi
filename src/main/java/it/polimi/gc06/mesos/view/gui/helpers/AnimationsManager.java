package it.polimi.gc06.mesos.view.gui.helpers;

import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.OfferTileView;
import it.polimi.gc06.mesos.view.gui.elements.TotemPieceView;
import it.polimi.gc06.mesos.view.gui.elements.TurnOrderTileView;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;

import static it.polimi.gc06.mesos.view.gui.GUI.*;
import static it.polimi.gc06.mesos.view.gui.GUI.client;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

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
        Bounds targetScreen = targetContainer.localToScreen(targetContainer.getBoundsInLocal());
        if (cardScreen == null || targetScreen == null) return;

        // we need this overlay pane to make the card appear above all other elements during the animation
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

        Pane originalParent = (Pane) card.getParent();
        originalParent.getChildren().remove(card); // TODO : we could do this and then redraw the original container

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

        TranslateTransition transition = EffectsManager.createCardMoveTransition(card, targetX - cardX, targetY - cardY, durationMs);
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
            targetContainer.getChildren().add(card); // TODO : we could do this and then redraw the original container

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

            double totemWidth = totemScreen.getWidth();
            double totemHeight = totemScreen.getHeight();

            double startX = totemScreen.getMinX() - rootScreen.getMinX();
            double startY = totemScreen.getMinY() - rootScreen.getMinY();

            // we use exact location of the totem after redraw
            double targetCenterX = tileScreen.getMinX() + (tile.getWidth() * 0.5) - rootScreen.getMinX();
            double targetCenterY = tileScreen.getMinY() + (tile.getHeight() * 0.2) - rootScreen.getMinY();

            double targetX = targetCenterX - (totemWidth / 2);
            double targetY = targetCenterY - (totemHeight / 2);

            // create ghost totem
            ImageView ghostTotem = new ImageView(totemMoved.getImage());
            ghostTotem.setFitWidth(totemWidth);
            ghostTotem.setFitHeight(totemHeight);
            ghostTotem.setPreserveRatio(true);

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
}
