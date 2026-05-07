package it.polimi.gc06.mesos.view.gui.elements;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.util.Duration;

public class EffectsManager {

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
        card.setDisable(true);
    }

    /**
     * Sets the active effect on the card, which consist in a bluish shadow which suggest that an action can be performed.
     *
     * @param card Card on which the effect get applied.
     */
    public static void activeCard(CardView card) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(1);
        dropShadow.setOffsetY(1);
        dropShadow.setSpread(0.5);
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(Color.web("#348ceb", 0.2));
        card.setEffect(dropShadow);
        card.setStyle("-fx-cursor: hand");
    }
}
