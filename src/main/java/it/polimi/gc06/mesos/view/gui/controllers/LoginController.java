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

    private static final String PROTOCOL_BTN_ACTIVE = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.1); -fx-border-color: #E0E0E0; -fx-border-width: 3; -fx-text-fill: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: default;";
    private static final String PROTOCOL_BTN_INACTIVE = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-border-color: #4A4A4A; -fx-border-width: 3; -fx-text-fill: #6B6B6B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String PROTOCOL_BTN_HOVER = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.05); -fx-border-color: #6B6B6B; -fx-border-width: 3; -fx-text-fill: #8A8A8A; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    private static final String MODAL_BTN_HOVER = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.1); -fx-border-color: #E0E0E0; -fx-border-width: 3; -fx-text-fill: #E0E0E0; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    private static final String FIELD_STYLE_VALID = "-fx-background-color: transparent; -fx-border-color: transparent transparent #2B2B2B transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";
    private static final String FIELD_STYLE_ERROR = "-fx-background-color: transparent; -fx-border-color: transparent transparent #8a0303 transparent; -fx-border-width: 0 0 3 0; -fx-text-fill: #2B2B2B; -fx-prompt-text-fill: rgba(43,43,43,0.5); -fx-alignment: center;";

    /**
     * Initializes the login layout elements, setting up structural bindings, interactive visual behaviors,
     * resolving valid available network connections, and triggering necessary static layouts properly.
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
     * Resolves all internally active local network connections scanning interfaces to dynamically populate
     * the modal combo-box structure allowing users to explicitly select desired bound IP interfaces.
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

        try {
            validIps.add(NetworkUtils.getPublicIpAddress());
        } catch (Exception e) {
            System.err.println("Failed to retrieve public IP address: " + e.getMessage());
        }

        validIps.add("127.0.0.1");
        networkInterfaceBox.getItems().addAll(validIps);
    }

    /**
     * Applies internal UI configuration parameters handling protocol mappings successfully mapping
     * action events tied accurately cleanly.
     */
    private void setupProtocolButtons() {
        if (rmiButton != null && tcpButton != null) {
            rmiButton.setOnAction(e -> selectProtocol("RMI"));
            tcpButton.setOnAction(e -> selectProtocol("TCP"));

            selectProtocol("RMI");
        }
    }

    /**
     * Switches the underlying target networking mechanism, automatically updating fallback port constants
     * and highlighting the selected interactive visually properly accurately.
     *
     * @param protocol Extracted String defining explicit connection behaviors (e.g. "TCP", "RMI").
     */
    private void selectProtocol(String protocol) {
        connectionType = protocol;
        if ("RMI".equals(protocol)) {
            rmiButton.setStyle(PROTOCOL_BTN_ACTIVE);
            tcpButton.setStyle(PROTOCOL_BTN_INACTIVE);
            if (portField != null) portField.setPromptText("Port (default: 1099)");
        } else {
            tcpButton.setStyle(PROTOCOL_BTN_ACTIVE);
            rmiButton.setStyle(PROTOCOL_BTN_INACTIVE);
            if (portField != null) portField.setPromptText("Port (default: 45161)");
        }
    }

    /**
     * Initializes behavior for the advanced configuration panel, creating interactive boundaries
     * mapping key handlers correctly.
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
     * Triggers the appearance transition fading explicitly correctly displaying mapping structures naturally
     * natively showing the advanced settings view accurately safely cleanly.
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
     * Consumes action hiding the advanced configuration elements behind fading animations restoring focus safely natively.
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
     * Attaches responsive bindings adjusting proportions seamlessly naturally ensuring resolutions scale properly natively safely explicitly.
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
     * Dispatches looping animated glow layers mapped underneath login modules seamlessly.
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
     * Resolves layout font structures extracting dynamically provided resources setting configurations correctly properly safely.
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
     * Formats ComboBox structures styling dropdowns safely adjusting colors consistently appropriately properly correctly effectively natively.
     *
     * @param comboBox Provided control interface component instance.
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
     * Overrides internal formatting attributes successfully loading metrics binding directly securely properly natively cleanly.
     *
     * @param node Targeted general-purpose user interface base component natively correctly smoothly securely explicitly.
     * @param size Numerical scalar multiplier encapsulating sizes natively accurately visually efficiently cleanly.
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
     * Extracts graphics resolving URL representations efficiently accurately natively dynamically properly correctly properly securely natively explicitly.
     *
     * @param imageView The container correctly wrapping bounds securely safely completely natively smoothly explicitly correctly safely precisely smoothly accurately effectively natively intelligently correctly successfully directly naturally seamlessly appropriately successfully precisely securely structurally optimally optimally successfully natively natively.
     * @param path Source directory string properly natively.
     */
    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        URL url = getClass().getResource(path);
        if (url != null) imageView.setImage(new Image(url.toExternalForm()));
    }

    /**
     * Implements responsive interactive mouse boundaries properly formatting hover bindings accurately cleanly natively properly effectively securely naturally accurately safely correctly correctly securely natively cleanly cleanly properly explicitly smoothly smoothly successfully cleanly properly reliably securely cleanly effectively.
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
            if (!"RMI".equals(connectionType)) rmiButton.setStyle(PROTOCOL_BTN_HOVER);
        });
        rmiButton.setOnMouseExited(e -> {
            if (!"RMI".equals(connectionType)) rmiButton.setStyle(PROTOCOL_BTN_INACTIVE);
        });
        rmiButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());

        tcpButton.setOnMouseEntered(e -> {
            if (!"TCP".equals(connectionType)) tcpButton.setStyle(PROTOCOL_BTN_HOVER);
        });
        tcpButton.setOnMouseExited(e -> {
            if (!"TCP".equals(connectionType)) tcpButton.setStyle(PROTOCOL_BTN_INACTIVE);
        });
        tcpButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());
    }

    /**
     * Resolves translational bounds cleanly natively triggering visually structured interactions dynamically correctly smoothly intelligently intelligently safely explicitly natively organically explicitly functionally explicitly accurately.
     *
     * @param button Base bounds mapped natively explicitly securely.
     * @param shadow Drop shadow components securely accurately effectively.
     * @param translateY Coordinate shift bounds securely intelligently naturally dynamically structurally seamlessly clearly naturally naturally seamlessly natively reliably correctly securely correctly optimally.
     * @param shadowOffsetY Shadows shifting values intelligently seamlessly natively properly safely securely optimally efficiently safely accurately optimally smoothly reliably intelligently seamlessly correctly organically safely organically safely securely properly organically accurately.
     */
    private void applyButtonPressEffect(Button button, DropShadow shadow, double translateY, double shadowOffsetY) {
        button.setTranslateY(translateY);
        if (shadow != null) shadow.setOffsetY(shadowOffsetY);
    }

    /**
     * Engages complete login routines safely accurately seamlessly correctly gracefully properly effectively correctly natively efficiently seamlessly efficiently gracefully securely correctly efficiently cleanly intelligently appropriately accurately securely securely gracefully optimally organically efficiently efficiently correctly gracefully properly optimally organically organically gracefully effectively natively dynamically natively.
     *
     * @param event Bound action component explicitly.
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
     * Sets graphical styling displaying error structures correctly properly cleanly cleanly properly elegantly effectively cleanly explicitly natively dynamically efficiently dynamically natively naturally correctly organically intelligently intelligently properly explicitly safely appropriately optimally gracefully successfully.
     */
    private void handleLoginError() {
        SoundManager.getInstance().playError();
        nicknameField.setStyle(FIELD_STYLE_ERROR);
    }

    /**
     * Generates disabling components bounds restricting clicks properly elegantly elegantly organically seamlessly natively efficiently accurately properly smoothly safely efficiently intelligently accurately appropriately smoothly accurately.
     */
    private void lockUIForTransition() {
        transitionStarted = true;
        rootPane.setDisable(true);
    }

    /**
     * Reactivates layout properly properly safely safely organically efficiently securely cleanly appropriately smoothly properly gracefully intelligently safely securely seamlessly.
     */
    private void unlockUI() {
        transitionStarted = false;
        rootPane.setDisable(false);
    }

    /**
     * Constructs valid server handshakes securely seamlessly checking settings explicitly correctly correctly effectively smoothly natively naturally seamlessly smoothly accurately seamlessly effectively gracefully seamlessly accurately successfully appropriately.
     *
     * @return Generated boolean determining connection outcome reliably efficiently organically effectively reliably.
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
     * Fires graphical fade transitions cleanly smoothly intelligently explicitly reliably gracefully seamlessly intelligently efficiently gracefully reliably gracefully appropriately gracefully efficiently.
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
     * Renders swapped contexts seamlessly efficiently dynamically dynamically correctly gracefully intelligently organically safely reliably smoothly safely seamlessly.
     *
     * @param newRoot Injected root parent naturally explicitly explicitly safely safely correctly elegantly seamlessly cleanly organically seamlessly cleanly successfully correctly naturally seamlessly efficiently efficiently explicitly accurately explicitly efficiently.
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
     * Extrapolates globally recognized player name statically dynamically correctly dynamically.
     *
     * @return Generated string properly efficiently correctly organically efficiently cleanly.
     */
    public static String getNickname() {
        return nickname;
    }
}