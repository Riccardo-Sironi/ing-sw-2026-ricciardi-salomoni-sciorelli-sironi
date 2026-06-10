package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;

public class LoginController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private ImageView particlesImageView;
    @FXML
    private Region bottomGlow;
    @FXML
    private VBox loginBox;
    @FXML
    private Label promptLabel;
    @FXML
    private TextField nicknameField;
    @FXML
    private Button joinButton;
    @FXML
    private DropShadow buttonShadow;

    private static String nickname = "";
    private boolean transitionStarted = false;

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/ArcadianG.ttf";

    private static final String BTN_STYLE_DEFAULT = "-fx-background-color: transparent; -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String BTN_STYLE_HOVER = "-fx-background-color: rgba(0,0,0,0.1); -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String FIELD_STYLE_VALID = "-fx-background-color: transparent; -fx-border-color: transparent transparent #2B2B2B transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";
    private static final String FIELD_STYLE_ERROR = "-fx-background-color: transparent; -fx-border-color: transparent transparent #8a0303 transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";

    @FXML
    public void initialize() {
        loadImage(backgroundImageView, "/imgs/background/login_background.png");
        loadImage(particlesImageView, "/imgs/effect/fire_particles.gif");

        loadFonts();
        setupDynamicLayout();
        setupGlowAnimation();
        setupButtonInteractions();

        Platform.runLater(nicknameField::requestFocus);
    }

    private void setupDynamicLayout() {
        NumberBinding scale = Bindings.min(
                rootPane.widthProperty().divide(1920.0),
                rootPane.heightProperty().divide(1080.0)
        );

        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        loginBox.scaleXProperty().bind(scale);
        loginBox.scaleYProperty().bind(scale);

        if (particlesImageView != null) {
            particlesImageView.setPreserveRatio(true);
            particlesImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    private void setupGlowAnimation() {
        if (bottomGlow != null) {
            FadeTransition glowPulse = new FadeTransition(Duration.seconds(0.25), bottomGlow);
            glowPulse.setFromValue(0.7);
            glowPulse.setToValue(0.9);
            glowPulse.setCycleCount(Animation.INDEFINITE);
            glowPulse.setAutoReverse(true);
            glowPulse.play();
        }
    }

    private void loadFonts() {
        try {
            setFontIfValid(promptLabel, 50);
            setFontIfValid(nicknameField, 35);
            setFontIfValid(joinButton, 30);
        } catch (Exception e) {
            System.err.println("Font Loading Error: " + e.getMessage());
        }
    }

    private void setFontIfValid(Region node, double size) {
        Font font = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), size);
        if (font == null) return;

        if (node instanceof Label) ((Label) node).setFont(font);
        else if (node instanceof TextField) ((TextField) node).setFont(font);
        else if (node instanceof Button) ((Button) node).setFont(font);
    }

    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        URL url = getClass().getResource(path);
        if (url != null) imageView.setImage(new Image(url.toExternalForm()));
    }

    private void setupButtonInteractions() {
        joinButton.setOnMouseEntered(e -> joinButton.setStyle(BTN_STYLE_HOVER));
        joinButton.setOnMouseExited(e -> joinButton.setStyle(BTN_STYLE_DEFAULT));

        joinButton.setOnMousePressed(e -> applyButtonPressEffect(0.5, 0.5));
        joinButton.setOnMouseReleased(e -> applyButtonPressEffect(0, 2.5));
    }

    private void applyButtonPressEffect(double translateY, double shadowOffsetY) {
        joinButton.setTranslateY(translateY);
        if (buttonShadow != null) buttonShadow.setOffsetY(shadowOffsetY);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        if (transitionStarted) return;

        nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            handleLoginError();
            return;
        }

        nicknameField.setStyle(FIELD_STYLE_VALID);
        lockUIForTransition();

        if (!connectToServer()) {
            unlockUI();
            return;
        }

        System.out.println("Login as: " + nickname);
        performSceneTransition();
    }

    private void handleLoginError() {
        nicknameField.setStyle(FIELD_STYLE_ERROR);
    }

    private void lockUIForTransition() {
        transitionStarted = true;
        rootPane.setDisable(true);
    }

    private void unlockUI() {
        transitionStarted = false;
        rootPane.setDisable(false);
    }

    // TODO Assolutamente da cambiare qusta parte.
    private boolean connectToServer() {
        GUI.client = new Client();
        try {
            GUI.client.connect("RMI", "localhost", 1099, "192.168.1.117", 1102);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
        try {
            if (!GUI.client.getServerConnection().login(nickname)) {
                handleLoginError();
                return false;
            }
        } catch (Exception e) {
            System.err.println("Connection Error");
        }

        GUI.smallModel = new SmallModel(nickname);
        GUI.client.setSmallModel(GUI.smallModel);
        return true;
    }

    private void performSceneTransition() {
        try {
            URL selectGameUrl = getClass().getResource(GameScene.SELECT.getPath());
            Parent selectGameRoot = new FXMLLoader(selectGameUrl).load();

            if (rootPane.getScene() != null) {
                rootPane.getScene().setFill(Color.BLACK);
            }

            FadeTransition fadeOut = new FadeTransition(Duration.millis(800), rootPane);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> swapSceneAndFadeIn(selectGameRoot));
            fadeOut.play();

        } catch (Exception e) {
            System.err.println("Failed to load scene transition: " + e.getMessage());
            //e.printStackTrace();
            unlockUI(); // in case of critical failure, unlock the UI
        }
    }

    private void swapSceneAndFadeIn(Parent newRoot) {
        Stage stage = GUI.primaryStage;

        double currentWidth = stage.getScene().getWidth();
        double currentHeight = stage.getScene().getHeight();

        stage.getScene().setRoot(newRoot);

        if (newRoot instanceof Region) {
            ((Region) newRoot).setPrefSize(currentWidth, currentHeight);
        }

        newRoot.applyCss();
        newRoot.layout();

        newRoot.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), newRoot);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    public static String getNickname() {
        return nickname;
    }
}