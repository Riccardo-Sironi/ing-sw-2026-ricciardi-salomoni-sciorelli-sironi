package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.network.leaderboard.Score;
import it.polimi.gc06.mesos.view.gui.GUI;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

    private static final String FONT_PATH = "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf";
    private static final String IMG_STELE = "/imgs/background/lobby_background.png"; // Usa lo sfondo corretto se ne hai un altro
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

    private static final double LEADERBOARD_MAX_WIDTH = 200.0;
    private static final double DISTANCE_FROM_BOTTOM = 80.0;
    private static final double PIXELS_PER_POINT = 2.0;

    private static final double FONT_SIZE_NAME = 24.0;
    private static final double FONT_SIZE_SCORE = 36.0;

    private static final double POINTS_PER_SECOND = 10.0;

    private final Random random = new Random();

    @FXML
    public void initialize() {
        loadImage(steleBackground, IMG_STELE);
        steleBackground.fitWidthProperty().bind(rootPane.widthProperty());
        steleBackground.fitHeightProperty().bind(rootPane.heightProperty());

        setupAtmosphere();

        Platform.runLater(() -> {
            if (GUI.smallModel != null && GUI.smallModel.getLeaderboard() != null && !GUI.smallModel.getLeaderboard().isEmpty()) {

                setupStaticLeaderboard(GUI.smallModel.getLeaderboard());

            } else {
                System.out.println("Error: Leaderboard empty or not found. Using mock data.");
                List<Score> mockScores = new ArrayList<>();
                mockScores.add(new Score("A", 110, 10));
                mockScores.add(new Score("B", 85, 8));
                mockScores.add(new Score("C", 65, 12));
                mockScores.add(new Score("D", 45, 5));
                mockScores.add(new Score("E", 20, 2));

                setupStaticLeaderboard(mockScores);
            }
        });
    }

    private void setupAtmosphere() {
        loadImage(smokeGifView, IMG_SMOKE);
        loadImage(fireGifView, IMG_FIRE);

        fireGifView.setFitWidth(450.0);
        fireGifView.setPreserveRatio(true);
        fireGifView.setBlendMode(BlendMode.SCREEN);
        fireGifView.setEffect(new GaussianBlur(18.0));
        AnchorPane.setLeftAnchor(fireGifView, 0.0);
        AnchorPane.setBottomAnchor(fireGifView, 140.0);

        smokeGifView.setFitWidth(450.0);
        smokeGifView.setPreserveRatio(true);
        smokeGifView.setBlendMode(BlendMode.SCREEN);
        smokeGifView.setEffect(new GaussianBlur(18.0));
        AnchorPane.setLeftAnchor(smokeGifView, -5.0);
        AnchorPane.setBottomAnchor(smokeGifView, 200.0);
    }

    public void setupStaticLeaderboard(List<Score> finalScores) {
        totemsContainer.getChildren().clear();
        int maxPlayers = finalScores.size();

        HBox playersRow = new HBox();
        playersRow.setAlignment(Pos.BOTTOM_CENTER);
        playersRow.setPrefHeight(800);

        AnchorPane.setBottomAnchor(playersRow, DISTANCE_FROM_BOTTOM);
        AnchorPane.setLeftAnchor(playersRow, 0.0);
        AnchorPane.setRightAnchor(playersRow, 0.0);

        double dynamicSpacing = (LEADERBOARD_MAX_WIDTH / maxPlayers) * 0.15;
        playersRow.setSpacing(dynamicSpacing);

        double dynamicTotemHeight = 350.0 - (maxPlayers * 25.0);
        double dynamicStainHeight = dynamicTotemHeight * 1.25;
        double fontScale = maxPlayers > 3 ? 0.8 : 1.0;

        List<String> availableStains = new ArrayList<>(Arrays.asList(STAIN_ASSETS));
        Collections.shuffle(availableStains, random);

        for (int i = 0; i < maxPlayers; i++) {
            Score score = finalScores.get(i);
            String assignedStain = availableStains.get(i % availableStains.size());

            VBox playerSlot = createPlayerSlot(score, assignedStain, i + 1, dynamicTotemHeight, dynamicStainHeight, fontScale);
            playersRow.getChildren().add(playerSlot);
        }

        totemsContainer.getChildren().add(playersRow);
    }

    private VBox createPlayerSlot(Score score, String stainAssetPath, int rank, double totemHeight, double stainHeight, double fontScale) {
        VBox playerSlot = new VBox(5);
        playerSlot.setAlignment(Pos.BOTTOM_CENTER);

        String targetColorHex = getColorHexFromModel(score.getNickname(), rank);

        Label scoreValueLabel = new Label("0");
        scoreValueLabel.setFont(loadFont(FONT_SIZE_SCORE * fontScale));
        scoreValueLabel.setTextFill(Color.WHITE);
        scoreValueLabel.setEffect(new DropShadow(5, Color.BLACK));
        scoreValueLabel.setOpacity(0);

        Region scoreBar = new Region();
        scoreBar.setStyle("-fx-background-color: " + targetColorHex + "; -fx-border-color: white; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");
        scoreBar.setMaxWidth(Region.USE_PREF_SIZE);
        scoreBar.setPrefWidth(45.0 * fontScale);
        scoreBar.setMinHeight(0);
        scoreBar.setPrefHeight(0);

        StackPane totemStack = createTotemWithStain(targetColorHex, stainAssetPath, totemHeight, stainHeight);

        Label nameLabel = new Label(score.getNickname().toUpperCase());
        nameLabel.setFont(loadFont(FONT_SIZE_NAME * fontScale));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setEffect(new DropShadow(5, Color.BLACK));

        playerSlot.getChildren().addAll(scoreValueLabel, scoreBar, totemStack, nameLabel);

        int finalScorePoints = score.getPrestigeScore() + score.getFoodScore();
        double targetH = Math.max(0, finalScorePoints * PIXELS_PER_POINT);

        double duration = Math.max(0.1, Math.max(0, finalScorePoints) / POINTS_PER_SECOND);

        Transition climb = new Transition() {
            {
                setCycleDuration(Duration.seconds(duration));
                setInterpolator(Interpolator.LINEAR);
            }

            @Override
            protected void interpolate(double frac) {
                scoreBar.setPrefHeight(targetH * frac);
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

    private StackPane createTotemWithStain(String colorHex, String stainPath, double totemHeight, double stainHeight) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER);

        ImageView stainView = new ImageView(getImage(stainPath));
        stainView.setFitHeight(stainHeight);
        stainView.setPreserveRatio(true);
        stainView.setOpacity(0.8);

        Lighting lighting = new Lighting(new Light.Distant(45, 45, Color.web(colorHex)));
        lighting.setSurfaceScale(0.0);
        stainView.setEffect(lighting);

        String randomAsset = TOTEM_ASSETS[random.nextInt(TOTEM_ASSETS.length)];
        ImageView totemView = new ImageView(getImage(randomAsset));
        totemView.setFitHeight(totemHeight);
        totemView.setPreserveRatio(true);

        ColorAdjust stone = new ColorAdjust();
        stone.setSaturation(-1.0);
        stone.setBrightness(-0.1);
        totemView.setEffect(stone);

        stack.getChildren().addAll(stainView, totemView);
        return stack;
    }

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

    private Font loadFont(double size) {
        InputStream is = getClass().getResourceAsStream(FONT_PATH);
        if (is != null) return Font.loadFont(is, size);
        return Font.font("System", FontWeight.BOLD, size);
    }

    private void loadImage(ImageView iv, String path) {
        Image img = getImage(path);
        if (img != null) iv.setImage(img);
    }

    private Image getImage(String path) {
        URL url = getClass().getResource(path);
        return (url != null) ? new Image(url.toExternalForm()) : null;
    }
}