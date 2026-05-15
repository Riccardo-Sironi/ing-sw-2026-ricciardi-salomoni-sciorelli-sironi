package it.polimi.gc06.mesos.view.gui.helpers;

import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.gui.controllers.GameViewController;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.OfferTileView;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.effect.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class EffectsManager {

    private static final Font mesosFont = Font.loadFont(
            GameViewController.class.getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ), 20
    );

    public static void playPopupIn(Popup popup) {
        if (popup.getContent().isEmpty()) return;
        HBox content = (HBox) popup.getContent().get(0);
        content.setOpacity(0);
        content.setTranslateY(6);

        FadeTransition fade = new FadeTransition(Duration.millis(100), content);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(
                Duration.millis(100),
                content
        );
        slide.setFromY(6);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();
    }

    public static void playPopupOut(Popup popup) {
        if (popup.getContent().isEmpty()) return;
        if (!popup.isShowing()) return;
        HBox content = (HBox) popup.getContent().get(0);

        FadeTransition fade = new FadeTransition(Duration.millis(100), content);
        fade.setFromValue(content.getOpacity());
        fade.setToValue(0);
        fade.setInterpolator(Interpolator.EASE_IN);
        fade.setOnFinished(e -> popup.hide());

        fade.play();
    }

    /**
     * Sets the disabled effect on the card, which consist in adjust the color to a gray scale which suggest that the
     * player cant performa an action on it.
     *
     * @param card Card on which the effect get applied.
     */
    public static void disableCard(CardView card) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0);
        colorAdjust.setSaturation(-1);
        colorAdjust.setBrightness(0);
        colorAdjust.setContrast(0);
        card.setEffect(colorAdjust);
        card.setStyle("-fx-cursor: default; -fx-focus-traversable: false");
        card.setMouseTransparent(true);
    }

    /**
     * Sets the active effect on the card, which consist in a bluish shadow which suggest that an action can be performed.
     *
     * @param card Card on which the effect get applied.
     */
    public static void activeCard(CardView card) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.web("#348ceb", 1));
        dropShadow.setRadius(6);
        dropShadow.setSpread(0.65);
        card.setEffect(dropShadow);
        card.setCursor(Cursor.HAND);

        Timeline in = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(dropShadow.radiusProperty(), 10, Interpolator.EASE_OUT),
                        new KeyValue(dropShadow.spreadProperty(), 0.65, Interpolator.EASE_OUT)
                )
        );

        Timeline out = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(dropShadow.radiusProperty(), 6, Interpolator.EASE_OUT),
                        new KeyValue(dropShadow.spreadProperty(), 0.65, Interpolator.EASE_OUT)
                )
        );

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), card);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        scaleOut.setInterpolator(Interpolator.EASE_OUT);

        card.setOnMouseEntered(e -> {
            out.stop();
            in.play();
            scaleOut.stop();
            scaleIn.play();
        });
        card.setOnMouseExited(e -> {
            in.stop();
            out.play();
            scaleIn.stop();
            scaleOut.play();
        });

        card.setOnMouseClicked(e -> {
            // TODO :
        });
    }

    public static void normalCard(CardView card) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(4);
        dropShadow.setOffsetY(4);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        card.setEffect(dropShadow);
    }

    public static TranslateTransition createCardMoveTransition(CardView card, double toX, double toY, double duration) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(duration), card);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        return transition;
    }

    public static void setTileEffect(OfferTileView tile) {

        if (!smallModel.isActive()) return;
        if (!smallModel.getPhase().equals(new PlacingTotemPhase().toString())) return;

        // occupied tile slot effect
        InnerShadow occupiedEffect = new InnerShadow();
        occupiedEffect.setColor(Color.rgb(200, 0, 0, 1));
        occupiedEffect.setRadius(20);
        occupiedEffect.setChoke(0.1);
        occupiedEffect.setBlurType(BlurType.GAUSSIAN);

        Timeline blockIn = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(occupiedEffect.colorProperty(), Color.rgb(200, 0, 0, 1))
                )
        );

        Timeline blockOut = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(occupiedEffect.colorProperty(), Color.rgb(0, 0, 0, 0))
                )
        );

        // free tile slot effect

        InnerShadow freeEffect = new InnerShadow();
        freeEffect.setColor(Color.web("#009DFFFF"));
        freeEffect.setRadius(20);
        freeEffect.setChoke(0.5);
        freeEffect.setBlurType(BlurType.GAUSSIAN);

        if (tile.getTotem().getTotemType() == Totem.NONE) {
            tile.setEffect(freeEffect);
        }

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);

        Timeline hoverIn = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(colorAdjust.brightnessProperty(), 0.4)
                )
        );

        Timeline hoverOut = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(colorAdjust.brightnessProperty(), 0.0)
                )
        );

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), tile);
        scaleIn.setToX(1.02);
        scaleIn.setToY(1.02);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), tile);
        scaleOut.setToX(1);
        scaleOut.setToY(1);

        tile.setOnMouseEntered(e -> {
            if ((tile.getTotem().getTotemType() == Totem.NONE)) {
                colorAdjust.setInput(freeEffect);
                tile.setEffect(colorAdjust);
                hoverIn.play();
                scaleOut.stop();
                scaleIn.play();
                tile.setCursor(Cursor.HAND);
            } else {
                tile.setEffect(occupiedEffect);
                blockOut.stop();
                blockIn.play();
            }
        });

        tile.setOnMouseExited(e -> {
            if ((tile.getTotem().getTotemType() == Totem.NONE)) {
                hoverOut.play();
                scaleIn.stop();
                scaleOut.play();
                tile.setEffect(freeEffect);
            } else {
                blockOut.play();
                blockIn.stop();
                tile.setEffect(null);
            }
        });
    }

    public static Popup createTotemPopup(Totem totemType, String playerName) {
        Popup popup = new Popup();
        popup.setAutoFix(true);

        HBox popupContent = new HBox();
        popupContent.setStyle(
                "-fx-background-color: " + totemType.getTotemColorHex() + ";" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-padding: 8px 15px;"
        );
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMouseTransparent(true);

        Text playerNameText = new Text(playerName);
        playerNameText.setFill(Color.WHITE);
        if (mesosFont != null) {
            playerNameText.setFont(mesosFont);
        }
        playerNameText.setMouseTransparent(true);

        popupContent.setOpacity(0);
        popupContent.getChildren().add(playerNameText);
        popup.getContent().add(popupContent);

        return popup;
    }
}
