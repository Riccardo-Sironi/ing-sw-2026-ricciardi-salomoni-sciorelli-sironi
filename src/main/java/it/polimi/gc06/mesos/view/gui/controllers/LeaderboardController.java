package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.net.URL;
import java.util.*;

public class LeaderboardController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView steleBackground;

    @FXML
    private ImageView fireGifView;

    @FXML
    private ImageView smokeGifView;

    @FXML
    private AnchorPane totemsContainer;

    private DoubleBinding scaleBinding;
    private String cachedFontFamily = "System";
    private String cachedTitleFontFamily = "System";

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf";
    private static final String FONT_PATH_TITLE = "/it/polimi/gc06/mesos/fonts/ArcadianG.ttf";
    private static final String IMG_STELE = "/imgs/background/lobby_background.png";
    private static final String IMG_FIRE = "/imgs/effect/flames.gif";
    private static final String IMG_SMOKE = "/imgs/effect/smoke.gif";

    private static final String[] TOTEM_ASSETS = {
            "/imgs/totems/engraved_totem_1.png",
            "/imgs/totems/engraved_totem_2.png",
            "/imgs/totems/engraved_totem_3.png"
    };

    private static final String[] STAIN_ASSETS = {
            "/imgs/totems/stain_1.png",
            "/imgs/totems/stain_2.png",
            "/imgs/totems/stain_3.png",
            "/imgs/totems/stain_4.png",
            "/imgs/totems/stain_5.png"
    };

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

    private static final double DISTANCE_FROM_BOTTOM = 80.0;
    private static final double MAX_BAR_HEIGHT = 250.0;

    private static final double FONT_SIZE_TITLE = 54.0;
    private static final double FONT_SIZE_NAME = 24.0;
    private static final double FONT_SIZE_SCORE = 36.0;

    private static final double POINTS_PER_SECOND = 10.0;

    private final Random random = new Random();

    /**
     * Called entirely on scene start triggering data-driven structural bindings properly mapping constraints.
     */
    @FXML
    public void initialize() {
        initFontCache();

        scaleBinding = Bindings.createDoubleBinding(() -> {
            double wScale = rootPane.getWidth() / BASE_W;
            double hScale = rootPane.getHeight() / BASE_H;
            if (wScale <= 0 || hScale <= 0) return 1.0;
            return Math.min(wScale, hScale);
        }, rootPane.widthProperty(), rootPane.heightProperty());

        loadImage(steleBackground, IMG_STELE);
        steleBackground.fitWidthProperty().bind(rootPane.widthProperty());
        steleBackground.fitHeightProperty().bind(rootPane.heightProperty());

        setupAtmosphere();
        setupTitle();

        Platform.runLater(() -> {
            if (GUI.smallModel != null && GUI.smallModel.getLeaderboard() != null && !GUI.smallModel.getLeaderboard().isEmpty()) {
                setupStaticLeaderboard(GUI.smallModel.getLeaderboard());
            } else {
                List<Score> mockScores = new ArrayList<>();
                mockScores.add(new Score("A", 47, 10));
                mockScores.add(new Score("B", 25, 8));
                mockScores.add(new Score("C", 35, 12));
                mockScores.add(new Score("D", 56, 3));
                mockScores.add(new Score("E", 70, 2));

                setupStaticLeaderboard(mockScores);
            }
        });
    }

    /**
     * Instantiates caching wrappers mitigating layout delays natively rendering text effectively across constraints dynamically.
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
            e.printStackTrace();
        }

        try (InputStream titleFontStream = getClass().getResourceAsStream(FONT_PATH_TITLE)) {
            if (titleFontStream != null) {
                Font f = Font.loadFont(titleFontStream, 10);
                if (f != null) {
                    cachedTitleFontFamily = f.getFamily();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolves structural scaled metrics into direct font formatting mappings securely.
     *
     * @param size Font descriptor size explicitly applied natively.
     * @return Fully loaded JavaFX mapping explicitly typed correctly.
     */
    private Font loadFont(double size) {
        return Font.font(cachedFontFamily, FontWeight.BOLD, size);
    }

    /**
     * Composes fixed positioning bounding boxes encapsulating title strings correctly applying dropshadow profiles accurately.
     */
    private void setupTitle() {
        Label titleLabel = new Label("LEADERBOARD");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(3, Color.BLACK));

        titleLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font(cachedTitleFontFamily, FontWeight.BOLD, Math.max(20, FONT_SIZE_TITLE * scaleBinding.get())),
                scaleBinding
        ));

        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        titleLabel.translateYProperty().bind(scaleBinding.multiply(BASE_H * 0.08));

        rootPane.getChildren().add(titleLabel);
    }

    /**
     * Formats external ambient graphic layers injecting visual blending rules dynamically mapping offsets.
     */
    private void setupAtmosphere() {
        loadImage(smokeGifView, IMG_SMOKE);
        if (smokeGifView.getImage() != null) {
            Image img = smokeGifView.getImage();
            double origRatio = img.getHeight() / img.getWidth();
            smokeGifView.setPreserveRatio(false);
            smokeGifView.setOpacity(SMOKE_OPACITY);
            smokeGifView.setBlendMode(BlendMode.SCREEN);
            smokeGifView.setEffect(new GaussianBlur(BLUR_RADIUS));
            smokeGifView.setManaged(false);

            smokeGifView.fitWidthProperty().bind(rootPane.widthProperty().multiply(SMOKE_FIT_WIDTH / BASE_W));
            smokeGifView.fitHeightProperty().bind(rootPane.heightProperty().multiply((SMOKE_FIT_WIDTH * origRatio) / BASE_H));
            smokeGifView.layoutXProperty().bind(rootPane.widthProperty().multiply(SMOKE_LEFT_ANCHOR / BASE_W));
            smokeGifView.layoutYProperty().bind(rootPane.heightProperty().subtract(
                    rootPane.heightProperty().multiply(SMOKE_BOTTOM_ANCHOR / BASE_H)
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
            fireGifView.setManaged(false);

            fireGifView.fitWidthProperty().bind(rootPane.widthProperty().multiply(FIRE_FIT_WIDTH / BASE_W));
            fireGifView.fitHeightProperty().bind(rootPane.heightProperty().multiply((FIRE_FIT_WIDTH * origRatio) / BASE_H));
            fireGifView.layoutXProperty().bind(rootPane.widthProperty().multiply(FIRE_LEFT_ANCHOR / BASE_W));
            fireGifView.layoutYProperty().bind(rootPane.heightProperty().subtract(
                    rootPane.heightProperty().multiply(FIRE_BOTTOM_ANCHOR / BASE_H)
            ).subtract(fireGifView.fitHeightProperty()));
        }
    }

    /**
     * Rebuilds visually driven layout components populating final stats tracking metrics consistently matching player names securely.
     *
     * @param finalScores Fully collected array holding the resolved player performances sequentially.
     */
    public void setupStaticLeaderboard(List<Score> finalScores) {
        totemsContainer.getChildren().clear();

        List<Score> displayScores = new ArrayList<>();

        if (GUI.smallModel != null && GUI.smallModel.getPlayer() != null) {
            String localNickname = GUI.smallModel.getPlayer().getNickname();

            finalScores.stream()
                    .filter(s -> s.getNickname().equals(localNickname))
                    .findFirst()
                    .ifPresent(displayScores::add);

            for (PlayerView opp : GUI.smallModel.getOpponents()) {
                finalScores.stream()
                        .filter(s -> s.getNickname().equals(opp.getNickname()))
                        .findFirst()
                        .ifPresent(displayScores::add);
            }
        } else {
            displayScores.addAll(finalScores);
            Collections.shuffle(displayScores, random);
        }

        int maxPlayers = displayScores.size();

        int maxScore = displayScores.stream()
                .mapToInt(s -> Math.max(0, s.getPrestigeScore() + s.getFoodScore()))
                .max()
                .orElse(1);

        if (maxScore == 0) maxScore = 1;

        StackPane wrapperPane = new StackPane();
        AnchorPane.setTopAnchor(wrapperPane, 0.0);
        AnchorPane.setBottomAnchor(wrapperPane, 0.0);
        AnchorPane.setLeftAnchor(wrapperPane, 0.0);
        AnchorPane.setRightAnchor(wrapperPane, 0.0);

        HBox playersRow = new HBox();
        playersRow.setAlignment(Pos.BOTTOM_CENTER);

        DoubleBinding containerMaxWidth = scaleBinding.multiply(BASE_W * 0.40);
        playersRow.maxWidthProperty().bind(containerMaxWidth);
        playersRow.prefWidthProperty().bind(containerMaxWidth);

        playersRow.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(0, 0, rootPane.getHeight() * (DISTANCE_FROM_BOTTOM / BASE_H), 0),
                rootPane.heightProperty()
        ));

        DoubleBinding spacingBind = scaleBinding.multiply(BASE_W * 0.015);
        playersRow.spacingProperty().bind(spacingBind);

        int FIXED_SLOTS = 5;
        DoubleBinding slotWidth = containerMaxWidth
                .subtract(spacingBind.multiply(FIXED_SLOTS - 1))
                .divide(FIXED_SLOTS);

        double baseTotemHeight = BASE_H * 0.35;
        double baseStainHeight = baseTotemHeight * 1.25;
        double fontScale = 0.75;

        List<String> availableStains = new ArrayList<>(Arrays.asList(STAIN_ASSETS));
        Collections.shuffle(availableStains, random);

        for (int i = 0; i < maxPlayers; i++) {
            Score score = displayScores.get(i);
            String assignedStain = availableStains.get(i % availableStains.size());

            VBox playerSlot = createPlayerSlot(score, assignedStain, i + 1, baseTotemHeight, baseStainHeight, fontScale, slotWidth, maxScore);
            playersRow.getChildren().add(playerSlot);
        }

        wrapperPane.getChildren().add(playersRow);
        totemsContainer.getChildren().add(wrapperPane);
    }

    /**
     * Compiles structurally constrained elements driving dynamic animations visualizing points naturally spanning intervals precisely.
     *
     * @param score Valid wrapper maintaining user score metrics.
     * @param stainAssetPath Asset string path.
     * @param rank Calculated performance index defining visual mapping structs natively.
     * @param totemHeight Sizing metric limiting graphical components dynamically.
     * @param stainHeight Graphic dimensional constraint natively mapped.
     * @param fontScale The numerical index binding textual limits reliably natively.
     * @param slotWidth The reactive binding managing width formatting accurately natively.
     * @param maxScore Extracted bounds formatting relative sizing properties safely effectively natively.
     * @return Assembled structured layout encapsulation wrapping actions correctly natively.
     */
    private VBox createPlayerSlot(Score score, String stainAssetPath, int rank, double totemHeight, double stainHeight, double fontScale, DoubleBinding slotWidth, int maxScore) {
        VBox playerSlot = new VBox();
        playerSlot.setAlignment(Pos.BOTTOM_CENTER);
        playerSlot.spacingProperty().bind(scaleBinding.multiply(5));

        playerSlot.minWidthProperty().bind(slotWidth);
        playerSlot.maxWidthProperty().bind(slotWidth);

        String targetColorHex = getColorHexFromModel(score.getNickname(), rank);

        Label scoreValueLabel = new Label("0");
        scoreValueLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                loadFont(FONT_SIZE_SCORE * fontScale * scaleBinding.get()), scaleBinding));
        scoreValueLabel.setTextFill(Color.WHITE);
        scoreValueLabel.setEffect(new DropShadow(5, Color.BLACK));
        scoreValueLabel.setOpacity(0);

        Region scoreBar = new Region();
        scoreBar.setStyle("-fx-background-color: " + targetColorHex + "; -fx-border-color: white; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        scoreBar.setMaxWidth(Region.USE_PREF_SIZE);
        scoreBar.prefWidthProperty().bind(slotWidth.multiply(0.35));

        DoubleProperty animatedBaseHeight = new SimpleDoubleProperty(0);
        scoreBar.prefHeightProperty().bind(animatedBaseHeight.multiply(scaleBinding));
        scoreBar.minHeightProperty().bind(scoreBar.prefHeightProperty());

        StackPane totemStack = createTotemWithStain(targetColorHex, stainAssetPath, totemHeight, stainHeight, slotWidth);

        Label nameLabel = new Label(score.getNickname().toUpperCase());
        nameLabel.fontProperty().bind(Bindings.createObjectBinding(() ->
                loadFont(FONT_SIZE_NAME * fontScale * scaleBinding.get()), scaleBinding));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setEffect(new DropShadow(5, Color.BLACK));

        playerSlot.getChildren().addAll(scoreValueLabel, scoreBar, totemStack, nameLabel);

        int finalScorePoints = Math.max(0, score.getPrestigeScore() + score.getFoodScore());
        double duration = Math.max(0.1, finalScorePoints / POINTS_PER_SECOND);

        Transition climb = new Transition() {
            {
                setCycleDuration(Duration.seconds(duration));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double frac) {
                double targetH = ((double) finalScorePoints / maxScore) * MAX_BAR_HEIGHT;
                animatedBaseHeight.set(targetH * frac);
                scoreValueLabel.setText(String.valueOf((int) (finalScorePoints * frac)));
            }
        };

        climb.setOnFinished(e -> {
            scoreValueLabel.setText(String.valueOf(finalScorePoints));

            ScaleTransition st = new ScaleTransition(Duration.millis(300), scoreValueLabel);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.3);
            st.setToY(1.3);
            st.setAutoReverse(true);
            st.setCycleCount(2);
            st.play();
        });

        PauseTransition initialDelay = new PauseTransition(Duration.seconds(1));
        initialDelay.setOnFinished(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(400), scoreValueLabel);
            ft.setToValue(1.0);
            ft.play();

            climb.play();
        });
        initialDelay.play();

        return playerSlot;
    }

    /**
     * Resolves layout bindings encapsulating standard totems underneath dynamically mapped visual adjustments matching player choices safely.
     *
     * @param colorHex Extracted graphical model parameter mapping.
     * @param stainPath Resolved asset mapping accurately formatting properties naturally natively.
     * @param totemHeight Formatting visual bindings effectively correctly mapping variables.
     * @param stainHeight Formatting visual constraints maintaining internal configurations mapping metrics accurately natively.
     * @param slotWidth The graphical component formatting mapping struct matching values accurately naturally natively.
     * @return Fully formatted stack pane containing valid visuals perfectly mapped properly securely.
     */
    private StackPane createTotemWithStain(String colorHex, String stainPath, double totemHeight, double stainHeight, DoubleBinding slotWidth) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.BOTTOM_CENTER);

        ImageView stainView = new ImageView(getImage(stainPath));
        stainView.fitHeightProperty().bind(scaleBinding.multiply(stainHeight));
        stainView.fitWidthProperty().bind(slotWidth);
        stainView.setPreserveRatio(true);
        stainView.setOpacity(0.8);

        Lighting lighting = new Lighting(new Light.Distant(45, 45, Color.web(colorHex)));
        lighting.setSurfaceScale(0.0);
        stainView.setEffect(lighting);

        String randomAsset = TOTEM_ASSETS[random.nextInt(TOTEM_ASSETS.length)];
        ImageView totemView = new ImageView(getImage(randomAsset));
        totemView.fitHeightProperty().bind(scaleBinding.multiply(totemHeight));
        totemView.fitWidthProperty().bind(slotWidth);
        totemView.setPreserveRatio(true);

        ColorAdjust stone = new ColorAdjust();
        stone.setSaturation(-1.0);
        stone.setBrightness(-0.1);
        totemView.setEffect(stone);

        stack.getChildren().addAll(stainView, totemView);
        return stack;
    }

    /**
     * Discovers visually accurate struct formats encapsulating the local model tracking metrics properly securely naturally natively.
     *
     * @param nickname The descriptor mapping lookup variables.
     * @param rank Used safely rendering fallback structures properly.
     * @return Generated valid String representation successfully.
     */
    private String getColorHexFromModel(String nickname, int rank) {
        if (GUI.smallModel != null) {
            if (nickname.equals(GUI.smallModel.getPlayer().getNickname()) && GUI.smallModel.getPlayer().getColor() != null)
                return Totem.getTotem(GUI.smallModel.getPlayer().getColor()).getTotemColorHex();
            for (PlayerView opp : GUI.smallModel.getOpponents()) {
                if (nickname.equals(opp.getNickname()) && opp.getColor() != null)
                    return Totem.getTotem(opp.getColor()).getTotemColorHex();
            }
        }
        return switch (rank) {
            case 1 -> Totem.ORANGE.getTotemColorHex();
            case 2 -> Totem.TURQUOISE.getTotemColorHex();
            case 3 -> Totem.WHITE.getTotemColorHex();
            case 4 -> Totem.YELLOW.getTotemColorHex();
            case 5 -> Totem.PURPLE.getTotemColorHex();
            default -> "#FFFFFF";
        };
    }

    /**
     * Resolves structural paths driving accurate model graphics appropriately correctly formatting bounds securely properly.
     *
     * @param iv Local formatting container bound correctly properly natively.
     * @param path The string defining asset maps effectively mapping models safely properly visually natively.
     */
    private void loadImage(ImageView iv, String path) {
        Image img = getImage(path);
        if (img != null) iv.setImage(img);
    }

    /**
     * Constructs valid formatting metrics safely checking relative pathways securely effectively cleanly seamlessly natively.
     *
     * @param path Source mapping effectively structurally dynamically seamlessly natively.
     * @return Safely extracted image object explicitly successfully securely natively.
     */
    private Image getImage(String path) {
        URL url = getClass().getResource(path);
        return (url != null) ? new Image(url.toExternalForm()) : null;
    }
}