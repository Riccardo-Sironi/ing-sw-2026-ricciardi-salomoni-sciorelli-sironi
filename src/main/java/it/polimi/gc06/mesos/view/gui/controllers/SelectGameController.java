package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.helpers.SoundManager;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectGameController {

    @FXML
    private StackPane rootPane;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private ImageView particlesImageView;
    @FXML
    private Region bottomGlow;

    @FXML
    private VBox mainBox;
    @FXML
    private Label titleLabel;
    @FXML
    private ScrollPane matchesScrollPane;
    @FXML
    private VBox matchesContainer;

    @FXML
    private Button refreshButton;
    @FXML
    private Button createButton;
    @FXML
    private HBox playersSelectionBox;
    @FXML
    private Label playersLabel;

    private Font titleFont;
    private Font itemFont;
    private Font buttonFont;

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf";
    private static final String BTN_STYLE = "-fx-background-color: transparent; -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";
    private static final String HOVER_STYLE = "-fx-background-color: rgba(0,0,0,0.1); -fx-border-color: #2B2B2B; -fx-border-width: 3; -fx-text-fill: #2B2B2B; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

    /**
     * Initializes the view elements, graphics loading, fonts, layout, and immediately queries matches.
     */
    @FXML
    public void initialize() {
        loadImage(backgroundImageView, "/imgs/background/selectgame_background.png");
        loadImage(particlesImageView, "/imgs/effect/fire_particles.gif");

        loadFonts();
        setupDynamicLayout();
        setupAnimations();
        setupButtonsAndScroll();

        Platform.runLater(this::handleRefresh);
    }

    /**
     * Creates window proportion bindings and applies scale parameters uniformly.
     */
    private void setupDynamicLayout() {
        NumberBinding scale = Bindings.min(
                rootPane.widthProperty().divide(1920.0),
                rootPane.heightProperty().divide(1080.0)
        );

        if (particlesImageView != null) {
            particlesImageView.setPreserveRatio(true);
            particlesImageView.fitHeightProperty().bind(rootPane.heightProperty());
        }

        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        mainBox.scaleXProperty().bind(scale);
        mainBox.scaleYProperty().bind(scale);
    }

    /**
     * Initializes any continuous ambient animations over layouts.
     */
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

    /**
     * Assigns styles to buttons and attaches mouse listeners to components including scroll panels.
     */
    private void setupButtonsAndScroll() {
        styleButton(refreshButton);
        styleButton(createButton);
        for (Node n : playersSelectionBox.getChildren()) {
            if (n instanceof Button) styleButton((Button) n);
        }

        matchesScrollPane.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                matchesScrollPane.setVvalue(matchesScrollPane.getVvalue() - event.getDeltaY() * 0.003);
                event.consume();
            }
        });
        createButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());
        refreshButton.setOnMousePressed(e -> SoundManager.getInstance().playClick());
    }

    /**
     * Loads custom TrueType or OTF fonts for rendering textual elements smoothly.
     */
    private void loadFonts() {
        try {
            titleFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 50);
            itemFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 30);
            buttonFont = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 25);

            if (titleFont != null) titleLabel.setFont(titleFont);
            if (itemFont != null) playersLabel.setFont(itemFont);
        } catch (Exception e) {
            System.err.println("Error loading fonts in SelectGameController.");
        }
    }

    /**
     * Safely attempts to load a graphical resource.
     *
     * @param imageView The destination container.
     * @param path The resource path string.
     */
    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        URL url = getClass().getResource(path);
        if (url != null) imageView.setImage(new Image(url.toExternalForm()));
    }

    /**
     * Event listener triggering a fresh fetch of active matches from the server.
     */
    @FXML
    public void handleRefresh() {
        matchesContainer.getChildren().clear();

        try {
            String matchesStr = "";

            matchesStr = GUI.client.getServerConnection().getAvailableMatches();

            if (matchesStr == null || matchesStr.trim().isEmpty()) {
                showEmptyMessage("NO MATCHES AVAILABLE. CREATE ONE!");
                return;
            }

            parseAndDisplayMatches(matchesStr);

        } catch (Exception e) {
            System.err.println("Error connecting to the server: " + e.getMessage());
            showEmptyMessage("ERROR CONNECTING TO SERVER");
        }
    }

    /**
     * Parses the string payload from the network providing match structures and sets the items graphically.
     *
     * @param matchesStr The concatenated textual descriptor array strings detailing games.
     */
    private void parseAndDisplayMatches(String matchesStr) {
        String[] matches = matchesStr.split("[,\\n]+");
        Pattern pattern = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");

        for (String matchObj : matches) {
            if (matchObj.trim().isEmpty()) continue;

            int matchId = extractMatchId(matchObj);
            int currentP = 0;
            int maxP = 0;
            String displayName = matchObj.trim();

            Matcher matcher = pattern.matcher(matchObj);

            if (matcher.find()) {
                currentP = Integer.parseInt(matcher.group(1));
                maxP = Integer.parseInt(matcher.group(2));
                displayName = matchObj.substring(0, matcher.start()).trim();
                if (displayName.endsWith("-")) {
                    displayName = displayName.substring(0, displayName.length() - 1).trim();
                }
            }

            HBox matchItem = createMatchItemUI(displayName, matchId, currentP, maxP);
            matchesContainer.getChildren().add(matchItem);
        }
    }

    /**
     * Event handler to expand the layout and visually unhide the match creation specifics (players).
     */
    @FXML
    public void showCreateOptions() {
        boolean isVisible = playersSelectionBox.isVisible();
        playersSelectionBox.setVisible(!isVisible);
        playersSelectionBox.setManaged(!isVisible);
    }

    /**
     * Executes the CreateMatch remote protocol invocation binding the host onto the new game layout instance.
     *
     * @param event The ActionEvent dispatched by selecting a size count button.
     */
    @FXML
    public void handleCreateMatch(ActionEvent event) {
        rootPane.setDisable(true);
        Button clickedBtn = (Button) event.getSource();
        int numPlayers = Integer.parseInt(clickedBtn.getUserData().toString());

        if (GUI.smallModel != null) GUI.smallModel.setMaxPlayers(numPlayers);

        try {
            GUI.subscribeGUI();
            int currentMatchId = GUI.client.getServerConnection().createMatch(numPlayers, LoginController.getNickname());
            System.out.println("match " + (currentMatchId) + " created");

            proceedToLobby();
        } catch (Exception e) {
            rootPane.setDisable(false);
            System.err.println("Error creating match: " + e.getMessage());
        }
    }

    /**
     * Requests joining an existing match session onto the server backend.
     *
     * @param matchId The unique server assignment identification.
     * @param maxPlayers Maximum players expected on the match for SmallModel mapping constraints.
     */
    private void handleJoin(int matchId, int maxPlayers) {
        rootPane.setDisable(true);

        try {
            GUI.subscribeGUI();
            boolean joined = GUI.client.getServerConnection().joinMatch(matchId, LoginController.getNickname());
            if (joined) {

                if (GUI.smallModel != null) GUI.smallModel.setMaxPlayers(maxPlayers);

                proceedToLobby();
            } else {
                rootPane.setDisable(false);
                handleRefresh();
            }
        } catch (Exception e) {
            rootPane.setDisable(false);
            System.err.println("Error joining match: " + e.getMessage());
        }
    }

    /**
     * Triggers the navigation shift directing rendering to the LobbyScene layout.
     */
    private void proceedToLobby() {
        Platform.runLater(() -> {
            try {
                GUI.changeScene(GameScene.LOBBY.getPath());
            } catch (Exception e) {
                System.err.println("Error proceeding to lobby: " + e.getMessage());
            }
        });
    }

    /**
     * Produces visually assembled list elements summarizing available match lobbies dynamically.
     *
     * @param matchName Label descriptor detailing standard room identifiers.
     * @param matchId Target networking hook parameter.
     * @param currentPlayers Live player registry count within the room.
     * @param maxPlayers The ceiling registry cap per active model constraints.
     * @return Generated HBox row UI fragment to append natively.
     */
    private HBox createMatchItemUI(String matchName, int matchId, int currentPlayers, int maxPlayers) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(20.0);
        row.setStyle("-fx-border-color: #2B2B2B; -fx-border-width: 0 0 2 0; -fx-padding: 10 10 10 10;");

        Label nameLabel = createStyledLabel(matchName, Color.web("#2B2B2B"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isFull = (currentPlayers >= maxPlayers && maxPlayers > 0);
        String playerText = maxPlayers > 0 ? "PLAYERS: " + currentPlayers + "/" + maxPlayers : "";
        Color playerColor = isFull ? Color.web("#8a0303") : Color.web("#2B2B2B");
        Label playersLabel = createStyledLabel(playerText, playerColor);

        Button joinBtn = new Button(isFull ? "FULL" : "JOIN");
        if (buttonFont != null) joinBtn.setFont(buttonFont);
        joinBtn.setPrefWidth(120.0);

        if (isFull) {
            joinBtn.setDisable(true);
            joinBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #8a0303; -fx-border-width: 3; -fx-text-fill: #8a0303; -fx-background-radius: 10; -fx-border-radius: 10;");
        } else {
            styleButton(joinBtn);
            joinBtn.setOnAction(e -> handleJoin(matchId, maxPlayers));
            joinBtn.setOnMousePressed(e -> SoundManager.getInstance().playClick());
        }

        row.getChildren().addAll(nameLabel, spacer, playersLabel, joinBtn);
        return row;
    }

    /**
     * Utilities method formatting and applying DropShadow profiles around text displays uniformly.
     *
     * @param text The string to render.
     * @param textColor Color wrapper descriptor applied across standard properties.
     * @return Fully formatted JavaFX Label element wrapper.
     */
    private Label createStyledLabel(String text, Color textColor) {
        Label label = new Label(text);
        if (itemFont != null) label.setFont(itemFont);
        label.setTextFill(textColor);
        applyShadow(label);
        return label;
    }

    /**
     * Wraps user-friendly error details or empty states when polling query collections are blank.
     *
     * @param msg Formatted diagnostic text string.
     */
    private void showEmptyMessage(String msg) {
        Label emptyLabel = createStyledLabel(msg, Color.web("#2B2B2B"));
        matchesContainer.getChildren().add(emptyLabel);
    }

    /**
     * Appends a subtle underlying projection vector shadow to the graphical label text structure.
     *
     * @param label The graphical element recipient.
     */
    private void applyShadow(Label label) {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.color(1, 1, 1, 0.4));
        ds.setRadius(1.0);
        ds.setSpread(0.8);
        ds.setOffsetY(1.5);
        label.setEffect(ds);
    }

    /**
     * Adjusts the button UI configuration injecting thematic consistency alongside active bounds interactions.
     *
     * @param btn Rendered node layout.
     */
    private void styleButton(Button btn) {
        if (buttonFont != null) btn.setFont(buttonFont);
        btn.setStyle(BTN_STYLE);

        DropShadow textShadow = new DropShadow();
        textShadow.setColor(Color.color(1, 1, 1, 0.4));
        textShadow.setRadius(1.0);
        textShadow.setSpread(0.8);
        textShadow.setOffsetY(2.5);
        btn.setEffect(textShadow);

        btn.setOnMouseEntered(e -> btn.setStyle(HOVER_STYLE));
        btn.setOnMouseExited(e -> btn.setStyle(BTN_STYLE));
        btn.setOnMousePressed(e -> {
            btn.setTranslateY(0.5);
            textShadow.setOffsetY(0.5);
        });
        btn.setOnMouseReleased(e -> {
            btn.setTranslateY(0);
            textShadow.setOffsetY(2.5);
        });
    }

    /**
     * Isolates explicitly declared integer index pointers embedded initially within textual arrays dynamically stringified previously.
     *
     * @param matchString Evaluated text.
     * @return Discovered valid integer matching sequence block fallback to -1 upon formatting parsing traps.
     */
    private int extractMatchId(String matchString) {
        String numberOnly = matchString.replaceAll("[^0-9]", " ").trim().split("\\s+")[0];
        try {
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return -1;
        }
    }
}