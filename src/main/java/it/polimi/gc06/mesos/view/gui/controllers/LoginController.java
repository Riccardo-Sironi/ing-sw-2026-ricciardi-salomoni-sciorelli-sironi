package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.ImageFetcher;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/ArcadianG.ttf";
    private static final String SUBMIT_BUTTON_STYLE = "-fx-background-color: transparent; -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String HOVER_BUTTON_STYLE = "-fx-background-color: rgba(0,0,0,0.1); -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String FIELD_DEFAULT_STYLE = "-fx-background-color: transparent; -fx-border-color: transparent transparent #2B2B2B transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";
    private static final String FIELD_ERROR_STYLE = "-fx-background-color: transparent; -fx-border-color: transparent transparent #8a0303 transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";

    @FXML
    public void initialize() {
        loadImage(backgroundImageView, "/login_background.png");
        loadImage(particlesImageView, "/fire_particles.gif");
        loadFonts();

        setupDynamicLayout();
        setupAnimations();
        setupButtonAnimations();

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

    private void setupAnimations() {
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
            Font titleFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 50);
            Font fieldFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 35);
            Font buttonFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 30);

            if (titleFont != null) promptLabel.setFont(titleFont);
            if (fieldFont != null) nicknameField.setFont(fieldFont);
            if (buttonFont != null) joinButton.setFont(buttonFont);
        } catch (Exception e) {
            System.err.println("Font Loading Error: " + e.getMessage());
        }
    }

    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        URL url = getClass().getResource(path);
        if (url != null) {
            imageView.setImage(new Image(url.toExternalForm()));
        }
    }

    private void setupButtonAnimations() {
        joinButton.setOnMouseEntered(e -> joinButton.setStyle(HOVER_BUTTON_STYLE));
        joinButton.setOnMouseExited(e -> joinButton.setStyle(SUBMIT_BUTTON_STYLE));

        joinButton.setOnMousePressed(e -> {
            joinButton.setTranslateY(0.5);
            if (buttonShadow != null) buttonShadow.setOffsetY(0.5);
        });

        joinButton.setOnMouseReleased(e -> {
            joinButton.setTranslateY(0);
            if (buttonShadow != null) buttonShadow.setOffsetY(2.5);
        });
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            nicknameField.setStyle(FIELD_ERROR_STYLE);
            return;
        }

        nicknameField.setStyle(FIELD_DEFAULT_STYLE);

        GUI.client = new Client();
        GUI.client.connect("RMI", "localhost", 1099);

        try {
            if (!GUI.client.getServerConnection().login(nickname)) {
                nicknameField.setStyle(FIELD_ERROR_STYLE);
                return;
            }
        } catch (Exception e) {
        }

        GUI.smallModel = new SmallModel(nickname);
        GUI.client.setSmallModel(GUI.smallModel);

        // TODO : match selection process ... for now we force to join the first match of the list

        try {
            GUI.client.getServerConnection().joinMatch(0, nickname);
        } catch (Exception e) {
        }

        GUI.subscribeGUI();

        while (GUI.smallModel.getPhase() == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        try {
            GUI.imageFetcher = new ImageFetcher(GUI.smallModel.getOpponents().size() + 1);
        } catch (Exception e) {
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/gc06/mesos/fxml/Lobby.fxml"));
            loader.load();
            GUI.guidtovisitor.setLobbyGuiController(loader.getController());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // TODO : THIS SHOULD BE DONE IN THE LOBBY ONCE WE HAVE IT

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/polimi/gc06/mesos/fxml/Mesos.fxml"));
            Parent root = loader.load();

            GameViewController gameViewController = loader.getController();

            GUI.guidtovisitor.setGameViewController(gameViewController);
            GUI.guidtovisitor.setBoardController(gameViewController.getBoardController());

            Scene scene = new Scene(root, GUI.WIDTH, GUI.HEIGHT);

            scene.getStylesheets().add(java.util.Objects.requireNonNull(
                    GUI.class.getResource("/it/polimi/gc06/mesos/css/board_style.css")
            ).toExternalForm());

            javafx.application.Platform.runLater(() -> GUI.primaryStage.setMaximized(true));
            GUI.primaryStage.setTitle("Mesos");
            GUI.primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Login as: " + nickname);
    }


    public static String getNickname() {
        return nickname;
    }
}