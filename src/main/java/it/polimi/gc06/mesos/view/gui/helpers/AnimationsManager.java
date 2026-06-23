package it.polimi.gc06.mesos.view.gui.helpers;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.elements.*;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class AnimationsManager {

    /**
     * This method applies a smooth animation to a card when it's picked from the top/bottom rows or the buildings rows and moved to the player's inventory.
     * * The card will visually move from its original position to the player's inventory area, creating a more engaging user experience.
     * * It is called before redrawing the personal decks of the target player, based on the small model data.
     *
     * @param card            the {@link CardView} object representing the card to be animated
     * @param targetContainer the {@link HBox} container representing the player's inventory area where the card will be moved to
     * @param mainRoot        the {@link HBox} representing the main root of the scene
     * @param onEndAction     the {@link Runnable} action to be executed after the animation ends
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

        SoundManager.getInstance().playPickCard();

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
     * @param tile            the {@link OfferTileView} object representing the target offer tile where the totem piece will be placed
     * @param owner           the nickname of the player who owns the totem piece being moved
     * @param turnOrderTile   the {@link TurnOrderTileView} object representing the player's turn order tile containing the totem piece
     * @param offerTrackTiles the list of {@link OfferTileView} objects representing all offer tiles in the game
     * @param mainRoot        the {@link HBox} representing the main root of the scene
     * @param onEndAction     the {@link Runnable} action to be executed after the animation ends
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

            // scale the ghost to the correct dimension (so 25% of the tile target)
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

            SoundManager.getInstance().playTotem();

            transition.setOnFinished(ev -> {
                root.getChildren().remove(overlayPane);
                if (onEndAction != null) onEndAction.run();
            });

            transition.play();
        } else {
            if (onEndAction != null) onEndAction.run();
        }
    }

    /**
     * This method orchestrate the refill animation for the cards rows, first it clears the target containers,
     * then it animates the bottom row and then the top row.
     *
     * @param top           the {@link List} of {@link Card} of the top row
     * @param bottom        the {@link List} of {@link Card} of the bottom row
     * @param deckContainer the {@link HBox} where the deck is displayed
     * @param topTarget     the {@link HBox} where the top row of cards will be displayed
     * @param bottomTarget  the {@link HBox} where the bottom row of cards will be displayed
     * @param onEndAction   the {@link Runnable} to be executed when the animation is complete
     */
    public static void refillCardsRowAnimation(List<Card> top, List<Card> bottom, HBox deckContainer, HBox topTarget, HBox bottomTarget, Runnable onEndAction) {
        bottomTarget.getChildren().clear();

        // first we drop move the top cards in the bottom row (after discarding the old bottom cards)
        animateCardListMove(0, bottom, topTarget, bottomTarget, () -> {
            topTarget.getChildren().clear();

            // then we draw the new cards from deck and put them in the top row
            animateCardListWithFly(0, top, deckContainer, topTarget, () -> {
                if (onEndAction != null) onEndAction.run();
            });
        });
    }

    /**
     * This method animates the movement of a list of cards from a source container to a target container.
     *
     * @param index     the index of the card currently animating.
     * @param cards     the {@link List} of cards yet to be animated.
     * @param sourceBox the {@link HBox} initially containing the cards.
     * @param targetBox the {@link HBox} where the cards will be moved.
     * @param onListEnd the action ({@link Runnable}) to perform when the list animation is complete.
     */
    private static void animateCardListMove(int index, List<Card> cards, HBox sourceBox, HBox targetBox, Runnable onListEnd) {
        if (index >= cards.size()) {
            if (onListEnd != null) onListEnd.run();
            return;
        }

        Card card = cards.get(index);
        if (card == null) {
            animateCardListMove(index + 1, cards, sourceBox, targetBox, onListEnd);
            return;
        }

        animateCardMove(card, index, sourceBox, targetBox, () -> {
            animateCardListMove(index + 1, cards, sourceBox, targetBox, onListEnd);
        });
    }

    /**
     * This method animates the movement of a single card from a source container to a target container. The movement is a
     * smooth transition that visually represents the card being moved from one location to another.
     *
     * @param card        the {@link Card} to animate.
     * @param index       the index of the card currently animating.
     * @param sourceBox   the {@link HBox} containing the source cards.
     * @param targetBox   the {@link HBox} where the cards will be moved.
     * @param onEndAction the action ({@link Runnable}) to perform when the animation is complete.
     */
    private static void animateCardMove(Card card, int index, HBox sourceBox, HBox targetBox, Runnable onEndAction) {
        Pane root = (Pane) sourceBox.getScene().getRoot();
        Pane overlayPane = new Pane();
        overlayPane.setMouseTransparent(true);
        overlayPane.prefWidthProperty().bind(root.widthProperty());
        overlayPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(overlayPane);

        CardView droppingCard = new CardView(imageFetcher.fetch(card));
        droppingCard.setPreserveRatio(true);
        droppingCard.fitHeightProperty().bind(targetBox.heightProperty().multiply(0.85));

        Bounds sourceScreen = sourceBox.localToScreen(sourceBox.getBoundsInLocal());
        Bounds targetScreen = targetBox.localToScreen(targetBox.getBoundsInLocal());
        Bounds rootScreen = root.localToScreen(root.getBoundsInLocal());

        if (sourceScreen == null || targetScreen == null || rootScreen == null) {
            root.getChildren().remove(overlayPane);
            targetBox.getChildren().add(droppingCard);
            if (onEndAction != null) onEndAction.run();
            return;
        }

        // we find the original card and we hide it
        if (index < sourceBox.getChildren().size()) {

            Node sourceNode = sourceBox.getChildren().get(index);
            Bounds nodeScreen = sourceNode.localToScreen(sourceNode.getBoundsInLocal());

            if (nodeScreen != null) {
                sourceNode.setVisible(false);
            }
        }

        double startX = sourceScreen.getMinX() - rootScreen.getMinX() + (sourceScreen.getWidth() / 2);
        double startY = sourceScreen.getMinY() - rootScreen.getMinY();

        double cardHeightEstim = targetScreen.getHeight() * 0.85;
        double imageRatio = droppingCard.getImage().getWidth() / droppingCard.getImage().getHeight();
        double cardWidthEstim = cardHeightEstim * imageRatio;

        double targetX = targetScreen.getMinX() - rootScreen.getMinX() + (targetScreen.getWidth() / 2) - (cardWidthEstim / 2);
        double targetY = targetScreen.getMinY() - rootScreen.getMinY() + (targetScreen.getHeight() / 2) - (cardHeightEstim / 2);

        droppingCard.relocate(startX, startY);
        overlayPane.getChildren().add(droppingCard);

        double deltaX = targetX - startX;
        double deltaY = targetY - startY;

        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(400), droppingCard);
        moveTransition.setByX(deltaX);
        moveTransition.setByY(deltaY);
        moveTransition.setInterpolator(Interpolator.EASE_OUT);

        SoundManager.getInstance().playPickCard();

        moveTransition.setOnFinished(ev -> {
            overlayPane.getChildren().remove(droppingCard);
            droppingCard.setTranslateX(0);
            droppingCard.setTranslateY(0);
            root.getChildren().remove(overlayPane);

            targetBox.getChildren().add(droppingCard);

            if (onEndAction != null) onEndAction.run();
        });

        moveTransition.play();
    }

    /**
     * This method animates the displaying of a list of cards from the deck to the target container.
     *
     * @param index         the index of the card to animate.
     * @param cards         the {@link List} of cards yet to be animated.
     * @param deckContainer the {@link HBox} initially containing the cards.
     * @param targetBox     the {@link HBox} where the cards will be moved.
     * @param onListEnd     the action ({@link Runnable}) to perform when the list animation is complete.
     */
    private static void animateCardListWithFly(int index, List<Card> cards, HBox deckContainer, HBox targetBox, Runnable onListEnd) {
        if (index >= cards.size()) {
            if (onListEnd != null) onListEnd.run();
            return;
        }

        Card card = cards.get(index);
        if (card == null) {
            animateCardListWithFly(index + 1, cards, deckContainer, targetBox, onListEnd);
            return;
        }

        animateCardFlyFromDeck(card, deckContainer, targetBox, () -> {
            animateCardListWithFly(index + 1, cards, deckContainer, targetBox, onListEnd);
        });
    }

    /**
     * This method animates a card flying from the deck to the target container. The card will visually "fly"" from the
     * deck to the target container, creating a more engaging user experience. The animation includes a flip effect to
     * reveal the card's face as it reaches the target.
     *
     * @param cardToDraw    the {@link Card} to animate.
     * @param deckContainer the {@link HBox} initially containing the card.
     * @param targetBox     the {@link HBox} where the card will be moved.
     * @param onEndAction   the action ({@link Runnable}) to perform when the animation is complete.
     */
    private static void animateCardFlyFromDeck(Card cardToDraw, HBox deckContainer, HBox targetBox, Runnable onEndAction) {
        Pane root = (Pane) deckContainer.getScene().getRoot();
        Pane overlayPane = new Pane();

        overlayPane.setMouseTransparent(true);

        overlayPane.prefWidthProperty().bind(root.widthProperty());
        overlayPane.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().add(overlayPane);

        CardView flyingCard = new CardView(imageFetcher.getDeckBackImage(smallModel.getEra()));
        flyingCard.setPreserveRatio(true);
        flyingCard.fitHeightProperty().bind(targetBox.heightProperty().multiply(0.85));

        Bounds deckScreen = deckContainer.localToScreen(deckContainer.getBoundsInLocal());
        Bounds targetScreen = targetBox.localToScreen(targetBox.getBoundsInLocal());
        Bounds rootScreen = root.localToScreen(root.getBoundsInLocal());

        if (deckScreen == null || targetScreen == null || rootScreen == null) {
            root.getChildren().remove(overlayPane);
            if (onEndAction != null) onEndAction.run();
            return;
        }

        double startX = deckScreen.getMinX() - rootScreen.getMinX() + (deckScreen.getWidth() / 2);
        double startY = deckScreen.getMinY() - rootScreen.getMinY();

        double cardHeightEstim = targetScreen.getHeight() * 0.85;
        double imageRatio = flyingCard.getImage().getWidth() / flyingCard.getImage().getHeight();
        double cardWidthEstim = cardHeightEstim * imageRatio;

        // we let the card fly to the center of the container
        double targetX = targetScreen.getMinX() - rootScreen.getMinX() + (targetScreen.getWidth() / 2) - (cardWidthEstim / 2);
        double targetY = targetScreen.getMinY() - rootScreen.getMinY() + (targetScreen.getHeight() / 2) - (cardHeightEstim / 2);

        flyingCard.relocate(startX, startY);
        overlayPane.getChildren().add(flyingCard);

        double deltaX = targetX - startX;
        double deltaY = targetY - startY;

        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(500), flyingCard);
        moveTransition.setByX(deltaX);
        moveTransition.setByY(deltaY);
        moveTransition.setInterpolator(Interpolator.EASE_OUT);

        // we make the card flip from the back to the front
        RotateTransition flipTransition = new RotateTransition(Duration.millis(500), flyingCard);
        flipTransition.setAxis(Rotate.Y_AXIS);
        flipTransition.setFromAngle(0);
        flipTransition.setToAngle(180);

        // when the card is 50% through the flip we set the image to the card which has been drawn to create the illusion of a flip
        flipTransition.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (newTime.toMillis() >= 250 && flyingCard.getScaleX() > 0) {
                flyingCard.setImage(imageFetcher.fetch(cardToDraw));
                flyingCard.setScaleX(-1);
            }
        });

        ParallelTransition flyAndFlip = new ParallelTransition(moveTransition, flipTransition);

        SoundManager.getInstance().playCardFlip();

        flyAndFlip.setOnFinished(ev -> {
            overlayPane.getChildren().remove(flyingCard);
            flyingCard.setTranslateX(0);
            flyingCard.setTranslateY(0);
            root.getChildren().remove(overlayPane);

            targetBox.getChildren().add(flyingCard);

            if (onEndAction != null) onEndAction.run();
        });

        flyAndFlip.play();
    }

    /**
     * This method orchestrate the refill animations for the buildings rows, first it clears the target containers,
     * then it animates the top row and then the bottom row.
     *
     * @param top          the {@link List} of cards for the top row.
     * @param bottom       the {@link List} of cards for the bottom row.
     * @param topTarget    the {@link HBox} for the top row.
     * @param bottomTarget the {@link HBox} for the bottom row.
     * @param onEndAction  the action ({@link Runnable}) to perform when the animation is complete.
     */
    public static void refillBuildingsRowAnimation(List<Card> top, List<Card> bottom, HBox topTarget, HBox bottomTarget, Runnable onEndAction) {
        topTarget.getChildren().clear();
        bottomTarget.getChildren().clear();

        animateBuildingsList(0, top, topTarget, () -> {
            animateBuildingsList(0, bottom, bottomTarget, () -> {
                if (onEndAction != null) onEndAction.run();
            });
        });
    }

    private static void animateBuildingsList(int index, List<Card> cards, HBox targetBox, Runnable onListEnd) {
        if (index >= cards.size()) {
            if (onListEnd != null) onListEnd.run();
            return;
        }

        Card card = cards.get(index);
        if (card == null) {
            animateBuildingsList(index + 1, cards, targetBox, onListEnd);
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
        parallel.setOnFinished(ev -> animateBuildingsList(index + 1, cards, targetBox, onListEnd));

        SoundManager.getInstance().playPickCard();
        parallel.play();
    }

    /**
     * This method animates the resolution of an event card, displaying it in the center of the screen with a
     * fade-in and scale-up effect, followed by a slight scale-down before disappearing.
     *
     * @param eventCard      the {@link Card} to animate.
     * @param eventContainer the {@link HBox} containing the event card.
     * @param onEndAction    the action ({@link Runnable}) to perform when the animation is complete.
     */
    public static void eventResolutionAnimation(Card eventCard, HBox eventContainer, Runnable onEndAction) {
        Pane root = (Pane) eventContainer.getScene().getRoot();

        StackPane overlayPane = new StackPane();

        overlayPane.setMouseTransparent(false);

        overlayPane.prefWidthProperty().bind(root.widthProperty());
        overlayPane.prefHeightProperty().bind(root.heightProperty());

        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        root.getChildren().add(overlayPane);

        CardView cv = new CardView(imageFetcher.fetch(eventCard));
        cv.fitHeightProperty().bind(overlayPane.heightProperty().multiply(0.5));
        cv.setPreserveRatio(true);
        cv.setOpacity(0);

        overlayPane.getChildren().add(cv);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), cv);
        fadeIn.setToValue(1);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(1000), cv);
        scaleIn.setToX(1.25);
        scaleIn.setToY(1.25);
        scaleIn.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition parallelIn = new ParallelTransition(fadeIn, scaleIn);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(1000), cv);
        scaleOut.setToX(1.1);
        scaleOut.setToY(1.1);
        scaleOut.setInterpolator(Interpolator.EASE_BOTH);

        PauseTransition pause1 = new PauseTransition(Duration.millis(250));
        PauseTransition pause2 = new PauseTransition(Duration.millis(350));

        pause1.setOnFinished(ev -> {
            root.getChildren().remove(overlayPane);
            overlayPane.getChildren().remove(cv);
        });

        SequentialTransition sequential = new SequentialTransition(parallelIn, scaleOut, pause1, pause2);

        sequential.setOnFinished(ev -> {
            if (onEndAction != null) onEndAction.run();
        });

        sequential.play();
    }
}