package it.polimi.gc06.mesos.view.gui.elements;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Duration;

public class PopupEffectsManager {
    public static void playPopupIn(Popup popup) {
        if (popup.getContent().isEmpty()) return;
        HBox content = (HBox) popup.getContent().get(0);
        content.setOpacity(0);
        content.setTranslateY(6);

        FadeTransition fade = new FadeTransition(Duration.millis(100), content);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.millis(100), content);
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
}
