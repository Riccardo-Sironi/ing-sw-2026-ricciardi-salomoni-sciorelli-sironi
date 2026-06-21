package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.network.NetworkUtils;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.helpers.SoundManager;
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
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

    @FXML
    private Label advancedSettingsLabel;

    // --- OVERLAY ELEMENTS ---
    @FXML
    private StackPane advancedSettingsOverlay;
    @FXML
    private VBox advancedSettingsModal;
    @FXML
    private Label advancedSettingsTitle;
    @FXML
    private Button rmiButton;
    @FXML
    private Button tcpButton;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private ComboBox<String> networkInterfaceBox;
    @FXML
    private Button okButton;

    private String connectionType = "RMI";
    private static String nickname = "";
    private boolean transitionStarted = false;

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/ArcadianG.ttf";

    private static final String BTN_STYLE_DEFAULT = "-fx-background-color: transparent; -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String BTN_STYLE_HOVER = "-fx-background-color: rgba(0,0,0,0.1); -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    private static final String PROTOCOL_BTN_ACTIVE = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #2B2B2B; -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    private static final String MODAL_BTN_DEFAULT = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-border-color: #E0E0E0; -fx-border-width: 3; -fx-text-fill: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String MODAL_BTN_HOVER = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.1); -fx-border-color: #E0E0E0; -fx-border-width: 3; -fx-text-fill: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    private static final String FIELD_STYLE_VALID = "-fx-background-color: transparent; -fx-border-color: transparent transparent #2B2B2B transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";
    private static final String FIELD_STYLE_ERROR = "-fx-background-color: transparent; -fx-border-color: transparent transparent #8a0303 transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";

    /**
     * Initializes the view elements, configures dynamic bindings, and sets up input handlers.
     */
    @FXML
    public void initialize() {
        loadImage(backgroundImageView, "/imgs/background/login_background.png");
        loadImage(particlesImageView, "/imgs/effect/fire_particles.gif");

        setupProtocolButtons();
        setupAdvancedSettingsModal();
        loadFonts();
        setupDynamicLayout();
        setupGlowAnimation();
        setupButtonInteractions();
        populateNetworkInterfaces();

        Platform.runLater(nicknameField::requestFocus);
    }

    /**
     * Reads all available network interfaces on the local machine to populate the combo box.
     */
    private void populateNetworkInterfaces() {
        List<String> validIps = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    byte[] ipBytes = addr.getAddress();

                    if (ipBytes.length == 4 && !addr.isLinkLocalAddress()) {
                        validIps.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error reading the local network interfaces: " + e.getMessage());
        }


        // Try adding the public IP address to the list
        try {
            validIps.add(NetworkUtils.getPublicIpAddress());
        } catch (Exception e) {
            System.err.println("Failed to retrieve public IP address: " + e.getMessage());
        }

        validIps.add("127.0.0.1");
        networkInterfaceBox.getItems().addAll(validIps);
    }

    /**
     * Configures the protocol selection buttons.
     */
    private void setupProtocolButtons() {
        if (rmiButton != null && tcpButton != null) {
            rmiButton.setOnAction(e -> selectProtocol("RMI"));
            tcpButton.setOnAction(e -> selectProtocol("TCP"));

            selectProtocol("RMI");
        }
    }

    /**
     * Sets the active protocol to connect to the server and updates UI styles.
     *
     * @param protocol The selected protocol string.
     */
    private void selectProtocol(String protocol) {
        connectionType = protocol;
        if ("RMI".equals(protocol)) {
            rmiButton.setStyle(PROTOCOL_BTN_ACTIVE);
            tcpButton.setStyle(MODAL_BTN_DEFAULT);
            if (portField != null) portField.setPromptText("Port (default: 1099)");
        } else {
            tcpButton.setStyle(PROTOCOL_BTN_ACTIVE);
            rmiButton.setStyle(MODAL_BTN_DEFAULT);
            if (portField != null) portField.setPromptText("Port (default: 45161)");
        }
    }

    /**
     * Configures behavior and layout of the advanced settings modal.
     */
    private void setupAdvancedSettingsModal() {
        if (advancedSettingsOverlay != null) {
            advancedSettingsOverlay.setVisible(false);
            advancedSettingsOverlay.setManaged(false);

            advancedSettingsOverlay.setOnMouseClicked(e -> closeAdvancedSettings());

            if (advancedSettingsModal != null) {
                advancedSettingsModal.setOnMouseClicked(javafx.event.Event::consume);

                advancedSettingsModal.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ENTER) {
                        closeAdvancedSettings();
                        e.consume();
                    }
                });
            }

            if (advancedSettingsLabel != null) {
                advancedSettingsLabel.setOnMouseClicked(e -> {
                    openAdvancedSettings();
                    SoundManager.getInstance().playClick();
                });

                advancedSettingsLabel.setOnMouseEntered(e -> {
                    advancedSettingsLabel.setTextFill(Color.WHITE);
                    DropShadow shadow = (DropShadow) advancedSettingsLabel.getEffect();
                    if (shadow != null) {
                        shadow.setRadius(15.0);
                        shadow.setSpread(0.6);
                    }
                });

                advancedSettingsLabel.setOnMouseExited(e -> {
                    advancedSettingsLabel.setTextFill(Color.web("#2B2B2B"));
                    DropShadow shadow = (DropShadow) advancedSettingsLabel.getEffect();
                    if (shadow != null) {
                        shadow.setRadius(1.0);
                        shadow.setSpread(0.8);
                    }
                });
            }
        }
    }

    /**
     * Opens the advanced settings modal overlay with a fade-in animation.
     */
    private void openAdvancedSettings() {
        joinButton.setDefaultButton(false);

        advancedSettingsOverlay.setVisible(true);
        advancedSettingsOverlay.setManaged(true);

        Platform.runLater(() -> ipField.requestFocus());

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), advancedSettingsOverlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Closes the advanced settings modal overlay with a fade-out animation.
     */
    @FXML
    private void closeAdvancedSettings() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), advancedSettingsOverlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            advancedSettingsOverlay.setVisible(false);
            advancedSettingsOverlay.setManaged(false);

            joinButton.setDefaultButton(true);

            Platform.runLater(() -> {
                nicknameField.requestFocus();
                nicknameField.deselect();
                nicknameField.positionCaret(nicknameField.getText().length());
            });
        });
        fadeOut.play();
    }

    /**
     * Establishes scale bindings to keep the layout responsive relative to window size.
     */
    private void setupDynamicLayout() {
        NumberBinding scale = Bindings.min(
                rootPane.widthProperty().divide(1920.0),
                rootPane.heightProperty().divide(1080.0)
        );

        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        loginBox.scaleXProperty().bind(scale);
        loginBox.scaleYProperty().bind(scale);

        if (advancedSettingsModal != null) {
            advancedSettingsModal.scaleXProperty().bind(scale);
            advancedSettingsModal.scaleYProperty().bind(scale);
        }

        if (particlesImageView != null) {
            particlesImageView.setPreserveRatio(true);
            particlesImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }
    }

    /**
     * Prepares and starts a cyclic animation for bottom glow visual effect.
     */
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

    /**
     * Attempts to load external font files and sets them onto UI components.
     */
    private void loadFonts() {
        try {
            setFontIfValid(promptLabel, 50);
            setFontIfValid(nicknameField, 35);
            setFontIfValid(joinButton, 30);
            setFontIfValid(advancedSettingsLabel, 20);
            setFontIfValid(advancedSettingsTitle, 30);

            styleComboBox(networkInterfaceBox);

        } catch (Exception e) {
            System.err.println("Font Loading Error: " + e.getMessage());
        }
    }

    /**
     * Applies styling rules to customize the appearance of the ComboBox interface items.
     *
     * @param comboBox The ComboBox to style.
     */
    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(comboBox.getPromptText());
                    setTextFill(Color.web("rgba(224,224,224,0.6)"));
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                }
                setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
            }
        });

        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #2B2B2B;");
                    setOnMouseEntered(null);
                    setOnMouseExited(null);
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                    setStyle("-fx-background-color: #2B2B2B; -fx-padding: 8 10 8 10; -fx-font-size: 14px;");

                    setOnMouseEntered(e -> setStyle("-fx-background-color: #444444; -fx-padding: 8 10 8 10; -fx-font-size: 14px; -fx-cursor: hand;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #2B2B2B; -fx-padding: 8 10 8 10; -fx-font-size: 14px;"));
                }
            }
        });
    }

    /**
     * Sets a custom font to the given region element if the font file is valid.
     *
     * @param node The UI component.
     * @param size The font size point.
     */
    private void setFontIfValid(Region node, double size) {
        if (node == null) return;
        Font font = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), size);
        if (font == null) return;

        switch (node) {
            case Label label -> label.setFont(font);
            case TextField textField -> textField.setFont(font);
            case Button button -> button.setFont(font);
            default -> {
            }
        }
    }

    /**
     * Loads an image to a targeted ImageView from the specified resource string.
     *
     * @param imageView The container for the graphic.
     * @param path      The resource path.
     */
    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        URL url = getClass().getResource(path);
        if (url != null) imageView.setImage(new Image(url.toExternalForm()));
    }

    /**
     * Assigns stylistic effects and interactive transition behaviors to UI buttons.
     */
    private void setupButtonInteractions() {
        joinButton.setOnMouseEntered(e -> joinButton.setStyle(BTN_STYLE_HOVER));
        joinButton.setOnMouseExited(e -> joinButton.setStyle(BTN_STYLE_DEFAULT));
        joinButton.setOnMousePressed(e -> {
            applyButtonPressEffect(joinButton, buttonShadow, 0.5, 0.5);
            SoundManager.getInstance().playClick();
        });
        joinButton.setOnMouseReleased(e -> applyButtonPressEffect(joinButton, buttonShadow, 0, 2.5));

        okButton.setOnMouseEntered(e -> okButton.setStyle(MODAL_BTN_HOVER + " -fx-font-size: 18px; -fx-font-weight: bold;"));
        okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: transparent; -fx-border-color: #E0E0E0; -fx-border-width: 3; -fx-text-fill: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand; -fx-font-size: 18px; -fx-font-weight: bold;"));
        okButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());

        rmiButton.setOnMouseEntered(e -> {
            if (!"RMI".equals(connectionType)) rmiButton.setStyle(MODAL_BTN_HOVER);
        });
        rmiButton.setOnMouseExited(e -> {
            if (!"RMI".equals(connectionType)) rmiButton.setStyle(MODAL_BTN_DEFAULT);
        });
        rmiButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());

        tcpButton.setOnMouseEntered(e -> {
            if (!"TCP".equals(connectionType)) tcpButton.setStyle(MODAL_BTN_HOVER);
        });
        tcpButton.setOnMouseExited(e -> {
            if (!"TCP".equals(connectionType)) tcpButton.setStyle(MODAL_BTN_DEFAULT);
        });
        tcpButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());
    }

    /**
     * Applies displacement effects visually mimicking a physical button press.
     *
     * @param button        The button to animate.
     * @param shadow        The button's shadow effect structure to be modified.
     * @param translateY    The Y-axis translation value.
     * @param shadowOffsetY The Y-axis shadow offset.
     */
    private void applyButtonPressEffect(Button button, DropShadow shadow, double translateY, double shadowOffsetY) {
        button.setTranslateY(translateY);
        if (shadow != null) shadow.setOffsetY(shadowOffsetY);
    }

    /**
     * Event handler to execute the server connection sequence upon pressing "Join".
     *
     * @param event The triggered ActionEvent.
     */
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

    /**
     * Activates visual indicators to signal an error during the login attempt.
     */
    private void handleLoginError() {
        SoundManager.getInstance().playError();
        nicknameField.setStyle(FIELD_STYLE_ERROR);
    }

    /**
     * Disables the main pane interactions to prevent multiple attempts while transitioning.
     */
    private void lockUIForTransition() {
        transitionStarted = true;
        rootPane.setDisable(true);
    }

    /**
     * Re-enables the UI layout interactions, effectively aborting a transition lock.
     */
    private void unlockUI() {
        transitionStarted = false;
        rootPane.setDisable(false);
    }

    /**
     * Initiates the connection phase with the specified server parameters and attempts a user login.
     *
     * @return True if the connection and login phase executed successfully, otherwise false.
     */
    private boolean connectToServer() {
        GUI.client = new Client();

        String ip = (ipField != null && !ipField.getText().trim().isEmpty()) ? ipField.getText().trim() : "localhost";
        String networkTech = "TCP".equals(connectionType) ? "SOCKET" : connectionType;

        int defaultPort = "RMI".equals(connectionType) ? 1099 : 45161;
        int port = defaultPort;

        if (portField != null && !portField.getText().trim().isEmpty()) {
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid port provided, falling back to default: " + defaultPort);
                port = defaultPort;
            }
        }

        String selectedLocalIp = networkInterfaceBox.getValue();
        if (selectedLocalIp == null) {
            selectedLocalIp = networkInterfaceBox.getItems().isEmpty() ? "127.0.0.1" : networkInterfaceBox.getItems().getFirst();
        }

        if ("RMI".equals(connectionType)) {
            System.setProperty("java.rmi.server.hostname", selectedLocalIp);
        }

        try {
            GUI.client.connect(networkTech, ip, port, 0);
            GUI.subscribeGUI();
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }

        try {
            if (!GUI.client.getServerConnection().login(nickname)) {
                handleLoginError();
                return false;
            }
        } catch (Exception e) {
            System.err.println("Connection Error: " + e.getMessage());
            return false;
        }

        GUI.smallModel = new SmallModel(nickname);
        GUI.client.setSmallModel(GUI.smallModel);
        return true;
    }

    /**
     * Loads the "Select Game" scene layout and fades it into the application stage.
     */
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
            unlockUI();
        }
    }

    /**
     * Sets the specified layout as the active scene root and applies fade-in properties.
     *
     * @param newRoot The Parent layout to present.
     */
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

    /**
     * Returns the finalized valid user nickname instance.
     *
     * @return The active player's nickname.
     */
    public static String getNickname() {
        return nickname;
    }
}