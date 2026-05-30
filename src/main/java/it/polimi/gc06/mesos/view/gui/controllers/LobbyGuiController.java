package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.GameScene;
import it.polimi.gc06.mesos.view.gui.ImageFetcher;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;
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

    // =========================================================================
    // NODI FXML
    // =========================================================================
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
    public AnchorPane effectsLayer;

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

    private static final double LOBBY_MAX_WIDTH = 700.0;
    private static final double SPACING_BOWLS = 20.0;

    private static final double SMOKE_FIT_WIDTH = 350.0;
    private static final double FIRE_FIT_WIDTH = 280.0;
    private static final double SMOKE_OPACITY = 0.65;
    private static final double FIRE_OPACITY = 0.85;
    private static final double BLUR_RADIUS = 18.0;
    private static final double SMOKE_BOTTOM_ANCHOR = 110.0;
    private static final double SMOKE_LEFT_ANCHOR = 55.0;
    private static final double FIRE_BOTTOM_ANCHOR = 90.0;
    private static final double FIRE_LEFT_ANCHOR = 65.0;

    @FXML
    public void initialize() {
        loadHeaderFonts();
        setupArchitecturalLayout();
        guidtovisitor.setLobbyGuiController(this);
        refreshLobbyUI();
    }

    private void loadHeaderFonts() {
        titleLabel.setFont(loadFont(FONT_SIZE_TITLE));
        playerCounterLabel.setFont(loadFont(FONT_SIZE_COUNTER));
    }

    private void setupArchitecturalLayout() {
        loadImage(backgroundImage, IMG_BACKGROUND);
        backgroundImage.fitWidthProperty().bind(lobbyRoot.widthProperty());
        backgroundImage.fitHeightProperty().bind(lobbyRoot.heightProperty());

        mainContent.prefWidthProperty().bind(lobbyRoot.widthProperty());
        mainContent.prefHeightProperty().bind(lobbyRoot.heightProperty());

        effectsLayer.prefWidthProperty().bind(lobbyRoot.widthProperty());
        effectsLayer.prefHeightProperty().bind(lobbyRoot.heightProperty());

        loadImage(smokeGifView, IMG_SMOKE);
        if (smokeGifView.getImage() != null) {
            smokeGifView.setFitWidth(SMOKE_FIT_WIDTH);
            smokeGifView.setPreserveRatio(true);
            smokeGifView.setOpacity(SMOKE_OPACITY);
            smokeGifView.setBlendMode(BlendMode.SCREEN);
            smokeGifView.setEffect(new GaussianBlur(BLUR_RADIUS));
            AnchorPane.setBottomAnchor(smokeGifView, SMOKE_BOTTOM_ANCHOR);
            AnchorPane.setLeftAnchor(smokeGifView, SMOKE_LEFT_ANCHOR);
        }

        loadImage(fireGifView, IMG_FIRE);
        if (fireGifView.getImage() != null) {
            fireGifView.setFitWidth(FIRE_FIT_WIDTH);
            fireGifView.setPreserveRatio(true);
            fireGifView.setOpacity(FIRE_OPACITY);
            fireGifView.setBlendMode(BlendMode.SCREEN);
            fireGifView.setEffect(new GaussianBlur(BLUR_RADIUS));
            AnchorPane.setBottomAnchor(fireGifView, FIRE_BOTTOM_ANCHOR);
            AnchorPane.setLeftAnchor(fireGifView, FIRE_LEFT_ANCHOR);
        }

        lobbyContainer.setMaxWidth(LOBBY_MAX_WIDTH);
        lobbyContainer.setAlignment(Pos.CENTER);

        VBox.setMargin(lobbyContainer, new Insets(60, 0, 0, 0));

        lobbyContainer.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.6));
        totemSelectionBox.prefHeightProperty().bind(mainContent.heightProperty().multiply(0.3));
        VBox.setVgrow(lobbyContainer, Priority.ALWAYS);
    }

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

    private void updatePlayerCounter() {
        if (GUI.smallModel == null) return;
        int currentPlayers = 1 + GUI.smallModel.getOpponents().size();
        int maxPlayers = GUI.smallModel.getMaxPlayers();
        playerCounterLabel.setText(currentPlayers + " / " + maxPlayers);
    }

    public void drawPlayersAndTotems() {
        lobbyContainer.getChildren().clear();

        if (GUI.smallModel == null) return;

        List<PlayerView> currentPlayers = new ArrayList<>();
        currentPlayers.add(GUI.smallModel.getPlayer());
        currentPlayers.addAll(GUI.smallModel.getOpponents());

        int maxPlayers = GUI.smallModel.getMaxPlayers();

        double dynamicSpacing = (LOBBY_MAX_WIDTH / maxPlayers) * 0.15;
        lobbyContainer.setSpacing(dynamicSpacing);

        double dynamicTotemHeight = 330.0 - (maxPlayers * 30.0);
        double dynamicStainHeight = dynamicTotemHeight * 1.15;

        double fontScale = maxPlayers > 3 ? 0.75 : 1.0;
        double nameSize = FONT_SIZE_PLAYER_NAME * fontScale;
        double readySize = FONT_SIZE_READY * fontScale;

        for (int i = 0; i < maxPlayers; i++) {
            VBox slotBox = buildPlayerSlot(i, maxPlayers, currentPlayers, dynamicTotemHeight, dynamicStainHeight, nameSize, readySize);
            lobbyContainer.getChildren().add(slotBox);

            if (firstLoad) {
                animateSlotEntrance(slotBox, i * 150);
            }
        }
    }

    private VBox buildPlayerSlot(int index, int maxPlayers, List<PlayerView> currentPlayers,
                                 double totemHeight, double stainHeight, double nameSize, double readySize) {

        VBox slotBox = new VBox(5);
        slotBox.setAlignment(Pos.CENTER);
        slotBox.setPrefWidth(LOBBY_MAX_WIDTH / maxPlayers);

        StackPane graphicsStack = new StackPane();
        graphicsStack.setAlignment(Pos.CENTER);

        int totemId = (index % 3) + 1;
        ImageView engravedTotemView = createTotemImageView(totemId, totemHeight);

        if (index < currentPlayers.size()) {
            PlayerView p = currentPlayers.get(index);
            Label nameLabel = createCustomLabel(p.getNickname(), nameSize, COLOR_TEXT_DEFAULT, true);

            if (p.getColor() != null) {
                Totem chosenTotem = mapColorToTotem(p.getColor());
                int stainIndex = (index % 5) + 1;

                ImageView stainView = createStainImageView(stainIndex, chosenTotem, stainHeight);
                if (stainView != null) {
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
                slotBox.getChildren().addAll(graphicsStack, nameLabel);
            }
        } else {
            graphicsStack.getChildren().add(engravedTotemView);
            Label emptyLabel = createCustomLabel("Waiting...", readySize, COLOR_TEXT_WAITING, false);
            slotBox.getChildren().addAll(graphicsStack, emptyLabel);
        }

        return slotBox;
    }

    public void drawTotemSelectionBox() {
        totemSelectionBox.getChildren().clear();
        totemSelectionBox.setSpacing(SPACING_BOWLS);

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

    private HBox buildBowlBox(Totem totem) {
        String bowlFileName = totem.name().toLowerCase() + "_bowl.png";
        Image bowlImg = getImage(PATH_BOWLS + bowlFileName);
        if (bowlImg == null) return null;

        HBox bowlBox = new HBox();
        bowlBox.setAlignment(Pos.CENTER);
        bowlBox.setPadding(new Insets(5));
        bowlBox.setCursor(Cursor.HAND);

        ImageView bowlImage = new ImageView(bowlImg);
        bowlImage.fitHeightProperty().bind(totemSelectionBox.heightProperty().multiply(0.8));
        bowlImage.setPreserveRatio(true);
        bowlImage.setEffect(new DropShadow(5, Color.BLACK));

        bowlBox.getChildren().add(bowlImage);
        setupBowlHoverAnimations(bowlBox);

        bowlBox.setOnMouseClicked(e -> {
            totemSelectionBox.setDisable(true);
            try {
                GUI.client.getServerConnection().chooseTotemColor(LoginController.getNickname(), mapTotemToColor(totem));
            } catch (Exception ex) {
                ex.printStackTrace();
                totemSelectionBox.setDisable(false);
            }
        });

        return bowlBox;
    }

    private Label createCustomLabel(String text, double fontSize, Color textColor, boolean addShadow) {
        Label label = new Label(text);
        label.setFont(loadFont(fontSize));
        label.setTextFill(textColor);

        if (addShadow) {
            DropShadow shadow = new DropShadow(3, Color.BLACK);
            label.setEffect(shadow);
        }
        return label;
    }

    private ImageView createTotemImageView(int totemId, double height) {
        Image totemImg = getImage(PATH_TOTEMS + totemId + ".png");
        ImageView engravedTotemView = new ImageView(totemImg);
        engravedTotemView.setFitHeight(height);
        engravedTotemView.setPreserveRatio(true);
        return engravedTotemView;
    }

    private ImageView createStainImageView(int stainIndex, Totem chosenTotem, double height) {
        Image stainImg = getImage(PATH_STAINS + stainIndex + ".png");
        if (stainImg == null) return null;

        ImageView stainView = new ImageView(stainImg);
        stainView.setFitHeight(height);
        stainView.setPreserveRatio(true);

        Color fxColor = Color.web("rgb(" + chosenTotem.getTotemColorRGB() + ")");
        Lighting lighting = new Lighting(new Light.Distant(45, 45, fxColor));
        lighting.setSurfaceScale(0.0);
        stainView.setEffect(lighting);

        return stainView;
    }

    private Font loadFont(double size) {
        InputStream fontStream = getClass().getResourceAsStream(FONT_PATH);
        if (fontStream != null) {
            Font f = Font.loadFont(fontStream, size);
            if (f != null) return f;
        }
        return Font.font("System", FontWeight.BOLD, size);
    }

    private Image getImage(String path) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("Error: image not found " + path);
            return null;
        }
        return new Image(stream);
    }

    private void loadImage(ImageView imageView, String path) {
        if (imageView == null) return;
        Image img = getImage(path);
        if (img != null) imageView.setImage(img);
    }


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


    private it.polimi.gc06.mesos.model.Color mapTotemToColor(Totem totem) {
        return switch (totem.name().toUpperCase()) {
            case "ORANGE" -> it.polimi.gc06.mesos.model.Color.ORANGE;
            case "WHITE" -> it.polimi.gc06.mesos.model.Color.WHITE;
            case "TURQUOISE" -> it.polimi.gc06.mesos.model.Color.TURQUOISE;
            case "YELLOW" -> it.polimi.gc06.mesos.model.Color.YELLOW;
            default -> it.polimi.gc06.mesos.model.Color.PURPLE;
        };
    }

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
                e.printStackTrace();
            }
        });
    }

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