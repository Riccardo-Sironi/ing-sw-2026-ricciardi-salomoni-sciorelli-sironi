package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.ImageFetcher;
import it.polimi.gc06.mesos.view.gui.helpers.SoundManager;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.gc06.mesos.view.gui.GUI.guidtovisitor;

public class LobbyGuiController {

    @FXML
    public StackPane lobbyRoot;
    @FXML
    public ImageView backgroundImage;
    @FXML
    public VBox mainContent;
    @FXML
    public HBox lobbyContainer;
    @FXML
    public HBox totemSelectionBox;
    @FXML
    public StackPane bowlTray;
    @FXML
    public Pane effectsLayer;

    @FXML
    public ImageView smokeGifView;
    @FXML
    public ImageView fireGifView;

    @FXML
    public Label titleLabel;
    @FXML
    public Label playerCounterLabel;

    private boolean gameStarting = false;
    private boolean firstLoad = true;

    private String cachedFontFamily = "System";
    private DoubleBinding scaleBinding;
    private DoubleBinding spacingBind;

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/ArcadianG.ttf";
    private static final String IMG_BACKGROUND = "/imgs/background/lobby_background.png";
    private static final String IMG_SMOKE = "/imgs/effect/smoke.gif";
    private static final String IMG_FIRE = "/imgs/effect/flames.gif";
    private static final String PATH_TOTEMS = "/imgs/totems/engraved_totem_";
    private static final String PATH_STAINS = "/imgs/totems/stain_";
    private static final String PATH_BOWLS = "/imgs/bowls/";

    private static final double FONT_SIZE_TITLE = 54.0;
    private static final double FONT_SIZE_COUNTER = 40.0;
    private static final double FONT_SIZE_PLAYER_NAME = 28.0;
    private static final double FONT_SIZE_READY = 24.0;
    private static final double FONT_SIZE_SELECTION_MSG = 28.0;

    private static final Color COLOR_TEXT_DEFAULT = Color.WHITE;
    private static final Color COLOR_TEXT_WAITING = Color.web("#a9a9a9");

    private static final double BASE_W = 1280.0;
    private static final double BASE_H = 720.0;

    private static final double SMOKE_FIT_WIDTH = 350.0;
    private static final double FIRE_FIT_WIDTH = 280.0;
    private static final double SMOKE_OPACITY = 0.65;
    private static final double FIRE_OPACITY = 0.85;
    private static final double BLUR_RADIUS = 18.0;
    private static final double SMOKE_BOTTOM_ANCHOR = 110.0;
    private static final double SMOKE_LEFT_ANCHOR = 35.0;
    private static final double FIRE_BOTTOM_ANCHOR = 90.0;
    private static final double FIRE_LEFT_ANCHOR = 45.0;

    /**
     * Executed automatically on scene build initialization. Maps local assets, scales graphics natively,
     * and initializes the underlying state-drawing rendering system.
     */
    @FXML
    public void initialize() {
        initFontCache();
        setupArchitecturalLayout();
        guidtovisitor.setLobbyGuiController(this);
        refreshLobbyUI();
    }

    /**
     * Presets application custom font arrays minimizing multiple memory streams per text injection update loops.
     */
    private void initFontCache() {
        try (InputStream fontStream = getClass().getResourceAsStream(FONT_PATH)) {
            if (fontStream != null) {
                Font f = Font.loadFont(fontStream, 10);
                if (f != null) {
                    cachedFontFamily = f.getFamily();
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
        }
    }

    /**
     * Invokes formatted scaled sizing queries atop globally loaded font resources correctly preserving scaling aesthetics.
     *
     * @param size Double mapped pixel density reference.
     * @return Generated Font context explicitly.
     */
    private Font loadFont(double size) {
        return Font.font(cachedFontFamily, FontWeight.BOLD, size);
    }

    /**
     * Binds mathematical ratios natively scaling embedded layouts, graphics blending mechanisms dynamically alongside stage adjustments structurally.
     */
    private void setupArchitecturalLayout() {
        scaleBinding = Bindings.createDoubleBinding(() -> {
            double wScale = lobbyRoot.getWidth() / BASE_W;
            double hScale = lobbyRoot.getHeight() / BASE_H;
            if (wScale <= 0 || hScale <= 0) return 1.0;
            return Math.min(wScale, hScale);
        }, lobbyRoot.widthProperty(), lobbyRoot.heightProperty());

        loadImage(backgroundImage, IMG_BACKGROUND);
        backgroundImage.fitWidthProperty().bind(lobbyRoot.widthProperty());
        backgroundImage.fitHeightProperty().bind(lobbyRoot.heightProperty());

        effectsLayer.prefWidthProperty().bind(lobbyRoot.widthProperty());
        effectsLayer.prefHeightProperty().bind(lobbyRoot.heightProperty());

        loadImage(smokeGifView, IMG_SMOKE);
        if (smokeGifView.getImage() != null) {
            Image img = smokeGifView.getImage();
            double origRatio = img.getHeight() / img.getWidth();
            smokeGifView.setPreserveRatio(false);
            smokeGifView.setOpacity(SMOKE_OPACITY);
            smokeGifView.setBlendMode(BlendMode.SCREEN);
            smokeGifView.setEffect(new GaussianBlur(BLUR_RADIUS));
            smokeGifView.fitWidthProperty().bind(lobbyRoot.widthProperty().multiply(SMOKE_FIT_WIDTH / BASE_W));
            smokeGifView.fitHeightProperty().bind(lobbyRoot.heightProperty().multiply((SMOKE_FIT_WIDTH * origRatio) / BASE_H));
            smokeGifView.layoutXProperty().bind(lobbyRoot.widthProperty().multiply(SMOKE_LEFT_ANCHOR / BASE_W));
            smokeGifView.layoutYProperty().bind(lobbyRoot.heightProperty().subtract(
                    lobbyRoot.heightProperty().multiply(SMOKE_BOTTOM_ANCHOR / BASE_H)
            ).subtract(smokeGifView.fitHeightProperty()));
        }

        loadImage(fireGifView, IMG_FIRE);
        if (fireGifView.getImage() != null) {
            Image img = fireGifView.getImage();
            double origRatio = img.getHeight() / img.getWidth();
            fireGifView.setPreserveRatio(false);
            fireGifView.setOpacity(FIRE_OPACITY);
            fireGifView.setBlendMode(BlendMode.SCREEN);
            fireGifView.setEffect(new GaussianBlur(BLUR_RADIUS));
            fireGifView.fitWidthProperty().bind(lobbyRoot.widthProperty().multiply(FIRE_FIT_WIDTH / BASE_W));
            fireGifView.fitHeightProperty().bind(lobbyRoot.heightProperty().multiply((FIRE_FIT_WIDTH * origRatio) / BASE_H));
            fireGifView.layoutXProperty().bind(lobbyRoot.widthProperty().multiply(FIRE_LEFT_ANCHOR / BASE_W));
            fireGifView.layoutYProperty().bind(lobbyRoot.heightProperty().subtract(
                    lobbyRoot.heightProperty().multiply(FIRE_BOTTOM_ANCHOR / BASE_H)
            ).subtract(fireGifView.fitHeightProperty()));
        }

        mainContent.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(lobbyRoot.getHeight() * 0.05, lobbyRoot.getWidth() * 0.05, lobbyRoot.getHeight() * 0.03, lobbyRoot.getWidth() * 0.05),
                lobbyRoot.widthProperty(), lobbyRoot.heightProperty()
        ));

        mainContent.spacingProperty().bind(scaleBinding.multiply(BASE_H * 0.04));

        lobbyContainer.maxWidthProperty().bind(lobbyRoot.widthProperty().multiply(0.40));
        lobbyContainer.prefWidthProperty().bind(lobbyContainer.maxWidthProperty());

        spacingBind = scaleBinding.multiply(BASE_W * 0.015);
        lobbyContainer.spacingProperty().bind(spacingBind);

        bowlTray.maxWidthProperty().bind(lobbyRoot.widthProperty().multiply(0.65));
        bowlTray.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(lobbyRoot.getHeight() * 0.02, lobbyRoot.getWidth() * 0.02, lobbyRoot.getHeight() * 0.02, lobbyRoot.getWidth() * 0.02),
                lobbyRoot.widthProperty(), lobbyRoot.heightProperty()
        ));

        totemSelectionBox.spacingProperty().bind(scaleBinding.multiply(BASE_W * 0.03));

        Runnable updateMargin = () -> {
            StackPane.setMargin(playerCounterLabel, new Insets(
                    lobbyRoot.getHeight() * 0.04,
                    lobbyRoot.getWidth() * 0.03,
                    0, 0
            ));
        };
        lobbyRoot.widthProperty().addListener(e -> updateMargin.run());
        lobbyRoot.heightProperty().addListener(e -> updateMargin.run());

        titleLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(Math.max(20, FONT_SIZE_TITLE * scaleBinding.get())),
                scaleBinding
        ));

        playerCounterLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(Math.max(16, FONT_SIZE_COUNTER * scaleBinding.get())),
                scaleBinding
        ));
    }

    /**
     * Executes the procedural visual rebuilding updates required sequentially when pushing models.
     */
    public void refreshLobbyUI() {
        Platform.runLater(() -> {
            updatePlayerCounter();
            drawPlayersAndTotems();
            drawTotemSelectionBox();

            if (firstLoad) {
                firstLoad = false;
            }
        });
    }

    /**
     * Computes numeric updates regarding dynamic match participant constraints.
     */
    private void updatePlayerCounter() {
        if (GUI.smallModel == null) return;
        int currentPlayers = 1 + GUI.smallModel.getOpponents().size();
        int maxPlayers = GUI.smallModel.getMaxPlayers();
        playerCounterLabel.setText(currentPlayers + " / " + maxPlayers);
    }

    /**
     * Formats graphical row outputs tracking internal lobby structures per participant status flags consistently.
     */
    public void drawPlayersAndTotems() {
        lobbyContainer.getChildren().clear();

        if (GUI.smallModel == null) return;

        List<PlayerView> currentPlayers = new ArrayList<>();
        currentPlayers.add(GUI.smallModel.getPlayer());
        currentPlayers.addAll(GUI.smallModel.getOpponents());

        int maxPlayers = GUI.smallModel.getMaxPlayers();

        DoubleBinding slotWidth = lobbyContainer.maxWidthProperty()
                .subtract(spacingBind.multiply(Math.max(0, maxPlayers - 1)))
                .divide(maxPlayers);

        double fontScale = maxPlayers > 3 ? 0.75 : 1.0;
        double nameSize = FONT_SIZE_PLAYER_NAME * fontScale;
        double readySize = FONT_SIZE_READY * fontScale;

        for (int i = 0; i < maxPlayers; i++) {
            VBox slotBox = buildPlayerSlot(i, maxPlayers, currentPlayers, nameSize, readySize, slotWidth);
            lobbyContainer.getChildren().add(slotBox);

            if (firstLoad) {
                animateSlotEntrance(slotBox, i * 150);
            }
        }
    }

    /**
     * Generates a structural participant entity component stack integrating totems alongside user status displays.
     *
     * @param index Slot placement.
     * @param maxPlayers Evaluated ceiling limit.
     * @param currentPlayers Live player objects mapped visually.
     * @param nameSize Configured pixel width scalar referencing dynamically typed elements natively.
     * @param readySize Configured pixel width scalar tracking text properties.
     * @param slotWidth The binding width object constraining elements dynamically.
     * @return Output container mapping user slots perfectly.
     */
    private VBox buildPlayerSlot(int index, int maxPlayers, List<PlayerView> currentPlayers,
                                 double nameSize, double readySize, DoubleBinding slotWidth) {

        VBox slotBox = new VBox();
        slotBox.setAlignment(Pos.CENTER);
        slotBox.spacingProperty().bind(scaleBinding.multiply(BASE_H * 0.015));

        slotBox.minWidthProperty().bind(slotWidth);
        slotBox.maxWidthProperty().bind(slotWidth);

        StackPane graphicsStack = new StackPane();
        graphicsStack.setAlignment(Pos.BOTTOM_CENTER);

        double baseTotemHeight = BASE_H * 0.40;
        double baseStainHeight = baseTotemHeight * 1.15;

        graphicsStack.minHeightProperty().bind(scaleBinding.multiply(baseStainHeight));
        graphicsStack.maxHeightProperty().bind(scaleBinding.multiply(baseStainHeight));

        int totemId = (index % 3) + 1;
        ImageView engravedTotemView = createTotemImageView(totemId);
        engravedTotemView.fitHeightProperty().bind(scaleBinding.multiply(baseTotemHeight));
        engravedTotemView.fitWidthProperty().bind(slotWidth);

        if (index < currentPlayers.size()) {
            PlayerView p = currentPlayers.get(index);
            Label nameLabel = createCustomLabel(p.getNickname(), nameSize, COLOR_TEXT_DEFAULT, true);

            if (p.getColor() != null) {
                Totem chosenTotem = mapColorToTotem(p.getColor());
                int stainIndex = (index % 5) + 1;

                ImageView stainView = createStainImageView(stainIndex, chosenTotem);
                if (stainView != null) {
                    stainView.fitHeightProperty().bind(scaleBinding.multiply(baseStainHeight));
                    stainView.fitWidthProperty().bind(slotWidth);
                    graphicsStack.getChildren().add(stainView);

                    if (!firstLoad) {
                        stainView.setOpacity(0.0);
                        FadeTransition fade = new FadeTransition(Duration.millis(500), stainView);
                        fade.setToValue(1.0);
                        fade.play();
                    }
                }

                graphicsStack.getChildren().add(engravedTotemView);

                Color readyColor = Color.web("rgb(" + chosenTotem.getTotemColorRGB() + ")");
                Label readyLabel = createCustomLabel("READY", readySize, readyColor, true);

                slotBox.getChildren().addAll(graphicsStack, nameLabel, readyLabel);
            } else {
                graphicsStack.getChildren().add(engravedTotemView);
                Label placeholderReady = createCustomLabel("READY", readySize, Color.TRANSPARENT, false);
                slotBox.getChildren().addAll(graphicsStack, nameLabel, placeholderReady);
            }
        } else {
            graphicsStack.getChildren().add(engravedTotemView);
            Label waitingLabel = createCustomLabel("Waiting...", nameSize, COLOR_TEXT_WAITING, false);
            Label placeholderReady = createCustomLabel("READY", readySize, Color.TRANSPARENT, false);
            slotBox.getChildren().addAll(graphicsStack, waitingLabel, placeholderReady);
        }

        return slotBox;
    }

    /**
     * Determines remaining unpicked character variables presenting choice menus directly mapping valid model items natively.
     */
    public void drawTotemSelectionBox() {
        totemSelectionBox.getChildren().clear();

        if (GUI.smallModel == null || GUI.smallModel.getOpponents() == null) return;

        int maxPlayers = GUI.smallModel.getMaxPlayers();
        int currentPlayers = 1 + GUI.smallModel.getOpponents().size();

        if (currentPlayers < maxPlayers) {
            totemSelectionBox.getChildren().add(createCustomLabel("Waiting for other players to join...", FONT_SIZE_SELECTION_MSG, COLOR_TEXT_DEFAULT, true));
            return;
        }

        if (GUI.smallModel.getPlayer().getColor() != null) {
            totemSelectionBox.getChildren().add(createCustomLabel("Waiting for other players to choose...", FONT_SIZE_SELECTION_MSG, COLOR_TEXT_DEFAULT, true));
            return;
        }

        List<Totem> occupiedTotems = new ArrayList<>();
        if (GUI.smallModel.getPlayer().getColor() != null)
            occupiedTotems.add(mapColorToTotem(GUI.smallModel.getPlayer().getColor()));
        for (var opponent : GUI.smallModel.getOpponents()) {
            if (opponent.getColor() != null) occupiedTotems.add(mapColorToTotem(opponent.getColor()));
        }

        for (Totem totem : Totem.values()) {
            if (totem == Totem.NONE || occupiedTotems.contains(totem)) continue;

            HBox bowlBox = buildBowlBox(totem);
            if (bowlBox != null) totemSelectionBox.getChildren().add(bowlBox);
        }
    }

    /**
     * Instantiates visually interactive components referencing model values handling the server payload directly.
     *
     * @param totem Mapped totem model struct.
     * @return Generated HBox context wrapping actions.
     */
    private HBox buildBowlBox(Totem totem) {
        String bowlFileName = totem.name().toLowerCase() + "_bowl.png";
        Image bowlImg = getImage(PATH_BOWLS + bowlFileName);
        if (bowlImg == null) return null;

        HBox bowlBox = new HBox();
        bowlBox.setAlignment(Pos.CENTER);

        bowlBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(lobbyRoot.getHeight() * 0.005), lobbyRoot.heightProperty()
        ));

        bowlBox.setCursor(Cursor.HAND);

        ImageView bowlImage = new ImageView(bowlImg);
        bowlImage.fitHeightProperty().bind(scaleBinding.multiply(BASE_H * 0.10));
        bowlImage.setPreserveRatio(true);
        bowlImage.setEffect(new DropShadow(5, Color.BLACK));

        bowlBox.getChildren().add(bowlImage);
        setupBowlHoverAnimations(bowlBox);

        bowlBox.setOnMouseClicked(e -> {
            SoundManager.getInstance().playClick();
            totemSelectionBox.setDisable(true);
            try {
                GUI.client.getServerConnection().chooseTotemColor(LoginController.getNickname(), mapTotemToColor(totem));
            } catch (Exception ex) {
                System.err.println("Error choosing totem color: " + ex.getMessage());
                totemSelectionBox.setDisable(false);
            }
        });

        return bowlBox;
    }

    /**
     * Constructs and initializes formatted UI labels applying drop-shadows structurally matching custom application bounds natively.
     *
     * @param text The inner literal wrapper value.
     * @param baseFontSize The numerical scale metric representing height.
     * @param textColor Color struct tracking internal hues.
     * @param addShadow Boolean trigger formatting standard visuals natively.
     * @return Finished Label Object.
     */
    private Label createCustomLabel(String text, double baseFontSize, Color textColor, boolean addShadow) {
        Label label = new Label(text);
        label.setTextFill(textColor);

        if (addShadow) {
            label.setEffect(new DropShadow(3, Color.BLACK));
        }

        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(Math.max(10, baseFontSize * scaleBinding.get())),
                scaleBinding
        ));

        return label;
    }

    /**
     * Retrieves static layout imagery directly mapped onto integer indexing parameters universally configured.
     *
     * @param totemId Integer map lookup key sequentially indexed.
     * @return Parsed image wrapper natively bound.
     */
    private ImageView createTotemImageView(int totemId) {
        Image totemImg = getImage(PATH_TOTEMS + totemId + ".png");
        ImageView engravedTotemView = new ImageView(totemImg);
        engravedTotemView.setPreserveRatio(true);
        return engravedTotemView;
    }

    /**
     * Composes an interactive layered map coloring structural overlays matching underlying totem struct parameters graphically.
     *
     * @param stainIndex Numerical sequence identifying the base structure.
     * @param chosenTotem The selected player totem mapping overlay hues.
     * @return Generated ImageView encapsulating lighting adjustments.
     */
    private ImageView createStainImageView(int stainIndex, Totem chosenTotem) {
        Image stainImg = getImage(PATH_STAINS + stainIndex + ".png");
        if (stainImg == null) return null;

        ImageView stainView = new ImageView(stainImg);
        stainView.setPreserveRatio(true);

        Color fxColor = Color.web("rgb(" + chosenTotem.getTotemColorRGB() + ")");
        Lighting lighting = new Lighting(new Light.Distant(45, 45, fxColor));
        lighting.setSurfaceScale(0.0);
        stainView.setEffect(lighting);

        return stainView;
    }

    /**
     * Acquires buffered image contents mapping structural pathways appropriately encapsulating resource failures directly.
     *
     * @param path The direct path parameter.
     * @return Resolved standard FX Image structure natively valid.
     */
    private Image getImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Error: image not found " + path);
            return null;
        }
        return new Image(stream);
    }

    /**
     * Evaluates valid pathways structurally injecting images inside matching containers natively.
     *
     * @param imageView Layout box.
     * @param path Source descriptor matching internal paths natively.
     */
    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        Image img = getImage(path);
        if (img != null) imageView.setImage(img);
    }

    /**
     * Executes fading sequences injecting components natively mapping cascade behaviors matching internal layouts dynamically natively.
     *
     * @param slot Valid component container dynamically typed.
     * @param delayMillis Measured sequential delay pushing updates matching the structural index constraints naturally.
     */
    private void animateSlotEntrance(VBox slot, int delayMillis) {
        slot.setOpacity(0);
        slot.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.millis(400), slot);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), slot);
        slide.setToY(0);
        slide.setDelay(Duration.millis(delayMillis));

        new ParallelTransition(fade, slide).play();
    }

    /**
     * Configures mouse pointer structural hooks wrapping layout animations directly.
     *
     * @param bowlBox The layout container bounding interaction handlers securely natively.
     */
    private void setupBowlHoverAnimations(HBox bowlBox) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.15), bowlBox);
        ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.15), bowlBox);
        TranslateTransition translateIn = new TranslateTransition(Duration.seconds(0.15), bowlBox);
        TranslateTransition translateOut = new TranslateTransition(Duration.seconds(0.15), bowlBox);

        bowlBox.setOnMouseEntered(e -> {
            scaleOut.stop();
            translateOut.stop();
            translateIn.setToY(-15);
            scaleIn.setToX(1.1);
            scaleIn.setToY(1.1);
            translateIn.playFromStart();
            scaleIn.playFromStart();
        });

        bowlBox.setOnMouseExited(e -> {
            translateIn.stop();
            scaleIn.stop();
            translateOut.setToY(0);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            translateOut.playFromStart();
            scaleOut.playFromStart();
        });
    }

    /**
     * Utility converter mapping visual enumerations towards direct network model formats structurally valid.
     *
     * @param totem Local context helper model structure natively formatting types.
     * @return Underlying raw Model Object naturally formatted encapsulating data.
     */
    private it.polimi.gc06.mesos.model.Color mapTotemToColor(Totem totem) {
        return switch (totem.name().toUpperCase()) {
            case "ORANGE" -> it.polimi.gc06.mesos.model.Color.ORANGE;
            case "WHITE" -> it.polimi.gc06.mesos.model.Color.WHITE;
            case "TURQUOISE" -> it.polimi.gc06.mesos.model.Color.TURQUOISE;
            case "YELLOW" -> it.polimi.gc06.mesos.model.Color.YELLOW;
            default -> it.polimi.gc06.mesos.model.Color.PURPLE;
        };
    }

    /**
     * Retrieves valid visual format structs formatting base server model enumerations effectively.
     *
     * @param color Internal standard enum.
     * @return Matching natively structured visual component naturally bounded directly.
     */
    private Totem mapColorToTotem(it.polimi.gc06.mesos.model.Color color) {
        if (color == null) return Totem.NONE;
        return switch (color) {
            case WHITE -> Totem.WHITE;
            case ORANGE -> Totem.ORANGE;
            case TURQUOISE -> Totem.TURQUOISE;
            case YELLOW -> Totem.YELLOW;
            case PURPLE -> Totem.PURPLE;
        };
    }

    /**
     * Triggers the sequence launching background fetching modules proceeding universally into matches natively.
     */
    public void checkAndStartGame() {
        Platform.runLater(() -> {
            if (gameStarting) return;

            if (GUI.smallModel.getPlayer().getColor() == null) return;
            for (var opponent : GUI.smallModel.getOpponents()) {
                if (opponent.getColor() == null) return;
            }
            gameStarting = true;

            try {
                if (GUI.imageFetcher == null) {
                    GUI.imageFetcher = new ImageFetcher(GUI.smallModel.getOpponents().size() + 1);
                }

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event -> startSceneTransition());
                delay.play();

            } catch (Exception e) {
                System.err.println("Error during game start transition: " + e.getMessage());
            }
        });
    }

    /**
     * Initializes smooth overlay swapping components moving players seamlessly inside full game rendering scopes.
     */
    private void startSceneTransition() {
        Region blackOverlay = new Region();
        blackOverlay.setStyle("-fx-background-color: black;");
        blackOverlay.setOpacity(0.0);
        blackOverlay.setMouseTransparent(true);

        blackOverlay.prefWidthProperty().bind(lobbyRoot.widthProperty());
        blackOverlay.prefHeightProperty().bind(lobbyRoot.heightProperty());

        lobbyRoot.getChildren().add(blackOverlay);

        FadeTransition fadeToBlack = new FadeTransition(Duration.seconds(1.5), blackOverlay);
        fadeToBlack.setFromValue(0.0);
        fadeToBlack.setToValue(1.0);

        fadeToBlack.setOnFinished(e -> GUI.changeScene(GameScene.GAME.getPath()));
        fadeToBlack.play();
    }
}