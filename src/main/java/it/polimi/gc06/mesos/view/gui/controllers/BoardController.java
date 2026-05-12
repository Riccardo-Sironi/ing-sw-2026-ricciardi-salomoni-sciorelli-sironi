package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.OffertTileView;
import it.polimi.gc06.mesos.view.gui.elements.TotemPieceView;
import it.polimi.gc06.mesos.view.gui.elements.TurnOrderTileView;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.OfferTileInfo;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.gui.helpers.TurnOrderTileInfo;
import it.polimi.gc06.mesos.view.gui.visitors.CardEffectVisitor;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

public class BoardController {

    @FXML
    private HBox mainRoot;
    @FXML
    private VBox leftZone;
    @FXML
    private VBox opponentsSidebar;
    @FXML
    private VBox expandedOpponentBox = null;

    @FXML
    private VBox boardRoot;

    @FXML
    private HBox inventoryBox;

    @FXML
    public HBox playerStatsBox;
    @FXML
    public VBox shamanStarsBox;
    @FXML
    public ImageView shamanStarsImage;
    @FXML
    public Text shamanStarsText;
    @FXML
    public VBox gathererQuantityBox;
    @FXML
    public ImageView gathererQuantityImage;
    @FXML
    public Text gathererQuantityText;
    @FXML
    public VBox hunterQuantityBox;
    @FXML
    public ImageView hunterQuantityImage;
    @FXML
    public Text hunterQuantityText;
    @FXML
    public VBox artistQuantityBox;
    @FXML
    public ImageView artistQuantityImage;
    @FXML
    public Text artistQuantityText;
    @FXML
    public VBox buildersDiscountBox;
    @FXML
    public ImageView buildersDiscountImage;
    @FXML
    public Text buildersDiscountText;
    @FXML
    public ScrollPane cardsScroll;
    @FXML
    public HBox playerCardsContainer;
    @FXML
    public HBox tokensBox;
    @FXML
    public VBox prestigeTokensBox;
    @FXML
    public ImageView prestigeTokensImage;
    @FXML
    public Text prestigeTokensText;
    @FXML
    public VBox foodTokensBox;
    @FXML
    public ImageView foodTokensImage;
    @FXML
    public Text foodTokensText;

    @FXML
    private HBox topBar;

    @FXML
    private HBox topRowBox;
    @FXML
    private ScrollPane topCharactersScroll;
    @FXML
    private HBox topCharactersContainer;
    @FXML
    private ScrollPane buildingsScroll;
    @FXML
    private HBox buildingsContainer;

    @FXML
    private HBox centerRowBox;
    @FXML
    private HBox deckContainer;
    @FXML
    public ImageView deckImage;
    @FXML
    public HBox tracksWrapper;
    @FXML
    private HBox turnOrderContainer;
    @FXML
    private HBox offerTrackContainer;
    @FXML
    private HBox skipButtonContainer;
    @FXML
    public Button skipButton;

    @FXML
    private HBox bottomRowBox;
    @FXML
    private ScrollPane bottomCharactersScroll;
    @FXML
    private HBox bottomCharactersContainer;
    @FXML
    private ScrollPane bottomBuildingsScroll;
    @FXML
    private HBox bottomBuildingsContainer;

    private static final PseudoClass DISABLED_STYLE = PseudoClass.getPseudoClass("skip-disabled");

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ), 20
    );

    private static final double RESIZE_CARD_FACTOR = 0.85;

    @FXML
    public void initialize() {
        setupArchitecturalLayout();
        updateBottomRowLayout(false);

        setupDeckPopup();

        if (smallModel != null) {
            drawDeck();
            drawTopRowCards();
            drawTopBuildingsCards();
            drawBottomRowCards();
            drawBottomBuildingCards();
            drawTurnOrderTile();
            drawOfferTrack();
            drawSkipButton();
            drawOpponentsSidebar(null);
        } else {
            int testNumPlayers = 5;
            setupMockData(testNumPlayers);
        }

        Platform.runLater(() -> {
            mainRoot.requestLayout();
        });
    }


    private void setupMockData(int numPlayers) {

        deckImage.setImage(loadImage("tribe_card_era_I_back.png"));

        topCharactersContainer.getChildren().clear();
        bottomCharactersContainer.getChildren().clear();

        for (int i = 0; i < 10; i++) {
            CardView card = new CardView(loadImage("shaman_1_card.png"));
            card.fitHeightProperty().bind(topRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            EffectsManager.activeCard(card);
            topCharactersContainer.getChildren().add(card);

            CardView bCard = new CardView(loadImage("shaman_1_card.png"));
            bCard.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            EffectsManager.disableCard(bCard);
            bottomCharactersContainer.getChildren().add(bCard);

            CardView pCard = new CardView(loadImage("shaman_1_card.png"));
            pCard.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            playerCardsContainer.getChildren().add(pCard);
        }

        turnOrderContainer.getChildren().clear();

        TurnOrderTileInfo tileInfo = TurnOrderTileInfo.getInfo(numPlayers);
        if (tileInfo == null) {
            tileInfo = TurnOrderTileInfo.TURN_ORDER_TILE_5_PLAYERS;
        }

        Image toImage = loadImage(tileInfo.getImagePath());
        if (toImage == null) {
            toImage = loadImage(TurnOrderTileInfo.TURN_ORDER_TILE_5_PLAYERS.getImagePath());
        }

        Totem[] tuttiITotem = {Totem.YELLOW, Totem.TURQUOISE, Totem.PURPLE, Totem.WHITE, Totem.ORANGE};
        ArrayList<Totem> totemAttivi = new ArrayList<>();
        for (int i = 0; i < numPlayers && i < tuttiITotem.length; i++) {
            totemAttivi.add(tuttiITotem[i]);
        }
        TurnOrderTileView turnOrderTile = createTurnOrderTile(toImage, totemAttivi);
        turnOrderContainer.getChildren().add(turnOrderTile);

        offerTrackContainer.getChildren().clear();

        String[] offerImages = switch (numPlayers) {
            case 5 ->
                // 5 Players: A, B, C, D, E, F, G
                    new String[]{
                            "offer_tile_A.png",
                            "offer_tile_B.png",
                            "offer_tile_C.png",
                            "offer_tile_D.png",
                            "offer_tile_E.png",
                            "offer_tile_F.png",
                            "offer_tile_G.png"
                    };
            case 4 ->
                // 4 Players: B, C, D, E, F, G
                    new String[]{
                            "offer_tile_B.png",
                            "offer_tile_C.png",
                            "offer_tile_D.png",
                            "offer_tile_E.png",
                            "offer_tile_F.png",
                            "offer_tile_G.png"
                    };
            case 3 ->
                // 3 Players: B, C, D, E, F
                    new String[]{
                            "offer_tile_B.png",
                            "offer_tile_C.png",
                            "offer_tile_D.png",
                            "offer_tile_E.png",
                            "offer_tile_F.png"
                    };
            case 2 ->
                // 2 Players: B, C, E, F
                    new String[]{
                            "offer_tile_B.png",
                            "offer_tile_C.png",
                            "offer_tile_E.png",
                            "offer_tile_F.png"
                    };
            default -> new String[]{
                    "offer_tile_B.png",
                    "offer_tile_C.png",
                    "offer_tile_E.png",
                    "offer_tile_F.png"
            };
        };

        for (int i = 0; i < offerImages.length; i++) {
            Totem t = null;
            String playerName = null;

            if (i < totemAttivi.size()) {
                t = totemAttivi.get(i);
                playerName = "Player " + (i + 1);
            }

            OffertTileView tile = createOfferTile(
                    loadImage(offerImages[i]),
                    t,
                    playerName,
                    OfferTileInfo.valueOf(
                            "OFFER_TILE_" + offerImages[i].split("_")[2].split("\\.")[0]).getXPercent(),
                    OfferTileInfo.valueOf(
                            "OFFER_TILE_" + offerImages[i].split("_")[2].split("\\.")[0]).getYPercent()
            );

            offerTrackContainer.getChildren().add(tile);
        }

        drawOpponentsSidebar(totemAttivi);
    }

    private void setupArchitecturalLayout() {
        final double WIDTH_LEFT_ZONE = 0.85;
        final double WIDTH_OPPONENTS = 0.15;

        leftZone.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_LEFT_ZONE));
        opponentsSidebar.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_OPPONENTS));
        opponentsSidebar.prefHeightProperty().bind(mainRoot.heightProperty());

        final double HEIGHT_BOARD = 0.75;
        final double HEIGHT_INVENTORY = 0.25;

        boardRoot.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_BOARD));
        inventoryBox.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_INVENTORY));

        inventoryBox.setAlignment(Pos.CENTER);

        configureInventoryStyle();

        playerStatsBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.3));
        configureScrollPane(cardsScroll, playerCardsContainer, leftZone.widthProperty().multiply(0.55));
        tokensBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));

        cardsScroll.setMaxHeight(Region.USE_PREF_SIZE);

        tokensBox.setAlignment(Pos.CENTER);
        tokensBox.setSpacing(5);

        configureTokensContainer(prestigeTokensBox, prestigeTokensImage, loadImage("prestige_token.png"), prestigeTokensText, "0");
        configureTokensContainer(foodTokensBox, foodTokensImage, loadImage("food_token.png"), foodTokensText, "0");

        playerStatsBox.setAlignment(Pos.CENTER);
        playerStatsBox.setSpacing(5);

        configureStatContainer(shamanStarsBox, shamanStarsImage, loadImage("shaman_stars_token.png"), shamanStarsText, "0");
        configureStatContainer(gathererQuantityBox, gathererQuantityImage, loadImage("gatherers_token.png"), gathererQuantityText, "0");
        configureStatContainer(hunterQuantityBox, hunterQuantityImage, loadImage("hunters_token.png"), hunterQuantityText, "0");
        configureStatContainer(artistQuantityBox, artistQuantityImage, loadImage("artists_token.png"), artistQuantityText, "0");
        configureStatContainer(buildersDiscountBox, buildersDiscountImage, loadImage("blank_token.png"), buildersDiscountText, "0");

        topBar.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.1));
        topRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));
        centerRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));
        bottomRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));

        topRowBox.setAlignment(Pos.CENTER);
        topRowBox.setSpacing(20);
        DoubleBinding topWidth = leftZone.widthProperty().multiply(0.80).subtract(40);
        configureScrollPane(topCharactersScroll, topCharactersContainer, topWidth.multiply(0.75));
        configureScrollPane(buildingsScroll, buildingsContainer, topWidth.multiply(0.25));

        centerRowBox.setAlignment(Pos.CENTER);
        centerRowBox.setSpacing(30);
        deckContainer.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));
        deckContainer.setAlignment(Pos.CENTER);
        deckImage.fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        turnOrderContainer.setAlignment(Pos.CENTER_RIGHT);

        offerTrackContainer.setAlignment(Pos.CENTER);
        offerTrackContainer.setSpacing(-2);

        skipButtonContainer.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));
        skipButtonContainer.setAlignment(Pos.CENTER);
    }

    private void configureTokensContainer(VBox container, ImageView containerImageView, Image containerImage,
                                          Text containerText, String containerTextLabel) {
        container.prefWidthProperty().bind(tokensBox.widthProperty().divide(2));
        container.prefHeightProperty().bind(tokensBox.heightProperty());
        container.maxHeightProperty().bind(tokensBox.heightProperty());
        container.setMinWidth(0);

        containerImageView.setImage(containerImage);
        containerImageView.setPreserveRatio(true);
        containerImageView.setSmooth(true);
        containerImageView.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(0.3));

        containerText.setText(containerTextLabel);
        containerText.setFont(mesosFont);
        containerText.setTextAlignment(TextAlignment.CENTER);
        containerText.wrappingWidthProperty().bind(container.widthProperty());
    }

    private void configureStatContainer(VBox container, ImageView containerImageView,
                                        Image containerImage, Text containerText, String containerTextLabel) {
        container.prefWidthProperty().bind(playerStatsBox.widthProperty().divide(5));
        container.maxHeightProperty().bind(playerStatsBox.heightProperty());
        container.prefHeightProperty().bind(playerStatsBox.heightProperty());
        container.setMinWidth(0);

        containerImageView.setImage(containerImage);
        containerImageView.setPreserveRatio(true);
        containerImageView.setSmooth(true);
        containerImageView.fitHeightProperty().bind(container.heightProperty().multiply(0.3));

        containerText.setText(containerTextLabel);
        containerText.setFont(mesosFont);
        containerText.setTextAlignment(TextAlignment.CENTER);
        containerText.wrappingWidthProperty().bind(container.widthProperty());
    }

    private void configureScrollPane(ScrollPane scroll, HBox container, DoubleBinding widthBinding) {
        if (scroll == null || container == null) return;
        scroll.prefWidthProperty().bind(widthBinding);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent;");

        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);
    }

    private void configureInventoryStyle() {
        String color = "";

        if (smallModel == null) {
            color = Totem.TURQUOISE.getTotemColorRGB();

        } else {
            // TODO : color based on player's totem
        }

        String backgroundInventoryStyle = "-fx-background-color: rgb(" + color + ", 0.6);";
        String borderInventoryStyle = "-fx-border-color: rgb(" + color + ",1); -fx-border-width: 2px;";

        inventoryBox.setStyle(backgroundInventoryStyle + borderInventoryStyle);

        String playerCardsBackgroundStyle = "-fx-background-color: rgb(255,255,255);";

        playerCardsContainer.setStyle(playerCardsBackgroundStyle);

        double radius = 10;
        cardsScroll.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            Rectangle clip = new Rectangle(newBounds.getWidth(), newBounds.getHeight());
            clip.setArcWidth(radius * 2);
            clip.setArcHeight(radius * 2);
            cardsScroll.setClip(clip);
        });

        // TODO : same white box for the stats
    }

    public void updateBottomRowLayout(boolean showBuildings) {
        bottomRowBox.setAlignment(Pos.CENTER);
        bottomRowBox.setSpacing(showBuildings ? 40 : 0);

        DoubleBinding bottomWidth = leftZone.widthProperty().multiply(0.60);
        if (showBuildings) bottomWidth = bottomWidth.subtract(40);

        if (showBuildings) {
            configureScrollPane(bottomCharactersScroll, bottomCharactersContainer, bottomWidth.multiply(0.75));
            if (bottomBuildingsScroll != null) {
                bottomBuildingsScroll.setVisible(true);
                bottomBuildingsScroll.setManaged(true);
                configureScrollPane(bottomBuildingsScroll, bottomBuildingsContainer, bottomWidth.multiply(0.25));
            }
        } else {
            configureScrollPane(bottomCharactersScroll, bottomCharactersContainer, bottomWidth);
            if (bottomBuildingsScroll != null) {
                bottomBuildingsScroll.setVisible(false);
                bottomBuildingsScroll.setManaged(false);
            }
        }
    }

    private Image loadImage(String path) {
        try {
            if (!path.startsWith("/")) path = "/" + path;
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            System.err.println("image not found: " + path);
            return null;
        }
    }

    public void drawOpponentsSidebar(ArrayList<Totem> activeTotems) {
        opponentsSidebar.getChildren().clear();
        expandedOpponentBox = null;
        opponentsSidebar.setSpacing(15);
        opponentsSidebar.setPadding(new Insets(15, 5, 15, 5));

        if (smallModel == null) {
            for (int i = 1; i < activeTotems.size(); i++) {
                Totem t = activeTotems.get(i);
                opponentsSidebar.getChildren().add(createOpponentInventoryBox("Player " + (i + 1), t.getTotemColorRGB(), null));
            }
            return;
        }

        for (PlayerView opponent : smallModel.getOpponents()) {
            String rgbColor = getRGBFromColor(opponent.getColor());
            opponentsSidebar.getChildren().add(createOpponentInventoryBox(opponent.getNickname(), rgbColor, opponent));
        }
    }

    private VBox createOpponentInventoryBox(String nickname, String rgbColor, PlayerView opponent) {
        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(0);
        container.setCursor(Cursor.HAND);

        String style = String.format("-fx-background-color: rgba(%s, 0.4); -fx-border-color: rgb(%s); -fx-border-width: 2px; -fx-background-radius: 10; -fx-border-radius: 10;", rgbColor, rgbColor);
        container.setStyle(style);
        container.setPadding(new Insets(10));

        container.minWidthProperty().bind(opponentsSidebar.widthProperty().multiply(0.9));
        container.maxWidthProperty().bind(opponentsSidebar.widthProperty().multiply(0.9));
        container.setMinHeight(0);
        container.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(container, Priority.ALWAYS);

        Text nameText = new Text(nickname);
        nameText.setTextAlignment(TextAlignment.CENTER);
        nameText.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font(mesosFont != null ? mesosFont.getFamily() : "Arial", opponentsSidebar.getWidth() / 10),
                opponentsSidebar.widthProperty()
        ));

        VBox detailsContainer = new VBox();
        detailsContainer.setAlignment(Pos.CENTER);
        detailsContainer.minWidthProperty().bind(container.widthProperty().subtract(20));
        detailsContainer.maxWidthProperty().bind(container.widthProperty().subtract(20));
        VBox.setVgrow(detailsContainer, Priority.ALWAYS);

        HBox resourcesBox = new HBox(20);
        resourcesBox.setAlignment(Pos.CENTER);
        resourcesBox.getChildren().addAll(
                createResponsiveStat("prestige_token.png", opponent != null ? String.valueOf(opponent.getNumPrestige()) : "0", 4.5),
                createResponsiveStat("food_token.png", opponent != null ? String.valueOf(opponent.getNumFood()) : "0", 4.5)
        );

        int shamans = 0, gatherers = 0, hunters = 0, artists = 0, builders = 0;
        if (opponent != null && opponent.getCharacters() != null) {
            for (Object item : opponent.getCharacters()) {
                if (item instanceof List) {
                    for (Object cardObj : (List<?>) item) {
                        String cardType = cardObj.getClass().getSimpleName().toUpperCase();
                        if (cardType.contains("SHAMAN")) shamans++;
                        else if (cardType.contains("GATHERER")) gatherers++;
                        else if (cardType.contains("HUNTER")) hunters++;
                        else if (cardType.contains("ARTIST")) artists++;
                        else if (cardType.contains("BUILDER")) builders++;
                    }
                }
            }
        }

        HBox charactersBox = new HBox(5);
        charactersBox.setAlignment(Pos.CENTER);
        charactersBox.getChildren().addAll(
                createResponsiveStat("shaman_stars_token.png", String.valueOf(shamans), 6.5),
                createResponsiveStat("gatherers_token.png", String.valueOf(gatherers), 6.5),
                createResponsiveStat("hunters_token.png", String.valueOf(hunters), 6.5),
                createResponsiveStat("artists_token.png", String.valueOf(artists), 6.5),
                createResponsiveStat("blank_token.png", String.valueOf(builders), 6.5)
        );

        ScrollPane cardsScroll = new ScrollPane();
        HBox cardsContainer = new HBox(5);
        cardsContainer.setAlignment(Pos.CENTER_LEFT);

        cardsScroll.setContent(cardsContainer);
        cardsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        cardsScroll.setFitToHeight(true);
        cardsScroll.setFitToWidth(true);
        cardsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        cardsScroll.minWidthProperty().bind(detailsContainer.widthProperty());
        cardsScroll.maxWidthProperty().bind(detailsContainer.widthProperty());

        cardsScroll.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                cardsScroll.setHvalue(cardsScroll.getHvalue() - event.getDeltaY() * 0.003);
                event.consume();
            }
        });

        cardsScroll.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            Rectangle clip = new Rectangle(newBounds.getWidth(), newBounds.getHeight());
            clip.setArcWidth(10); clip.setArcHeight(10);
            cardsScroll.setClip(clip);
        });

        if (opponent == null || opponent.getCharacters() == null) {
            for(int i = 0; i < 3; i++) {
                CardView cardView = new CardView(loadImage("shaman_1_card.png"));
                cardView.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
                cardsContainer.getChildren().add(cardView);
            }
        } else {
            for (Object item : opponent.getCharacters()) {
                if (item instanceof List) {
                    for (Object cardObj : (List<?>) item) {
                        CardView cardView = new CardView(new Image(imageFetcher.fetch((Card) cardObj)));
                        cardView.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
                        cardsContainer.getChildren().add(cardView);
                    }
                }
            }
        }

        Region spacer1 = new Region(); VBox.setVgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region(); VBox.setVgrow(spacer2, Priority.ALWAYS);
        Region spacer3 = new Region(); VBox.setVgrow(spacer3, Priority.ALWAYS);
        Region spacer4 = new Region(); VBox.setVgrow(spacer4, Priority.ALWAYS);

        detailsContainer.getChildren().addAll(spacer1, resourcesBox, spacer2, charactersBox, spacer3, cardsScroll, spacer4);

        detailsContainer.setManaged(false);
        detailsContainer.setVisible(false);
        detailsContainer.setOpacity(0);
        detailsContainer.setMinHeight(0);
        detailsContainer.setMaxHeight(0);

        container.getChildren().addAll(nameText, detailsContainer);

        container.setOnMouseClicked(event -> {
            Timeline timeline = new Timeline();
            boolean isOpening = (expandedOpponentBox != container);

            for (Node node : opponentsSidebar.getChildren()) {
                VBox box = (VBox) node;
                VBox details = (VBox) box.getChildren().get(1);

                if (isOpening && box == container) {
                    details.setManaged(true);
                    details.setVisible(true);

                    double targetHeight = opponentsSidebar.getHeight() * 0.65;
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(350),
                            new KeyValue(details.opacityProperty(), 1, Interpolator.EASE_BOTH),
                            new KeyValue(details.prefHeightProperty(), targetHeight, Interpolator.EASE_BOTH),
                            new KeyValue(details.minHeightProperty(), targetHeight, Interpolator.EASE_BOTH),
                            new KeyValue(details.maxHeightProperty(), targetHeight, Interpolator.EASE_BOTH)
                    ));
                } else if (expandedOpponentBox == box || (!isOpening && box == container)) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(350),
                            new KeyValue(details.opacityProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(details.prefHeightProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(details.minHeightProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(details.maxHeightProperty(), 0, Interpolator.EASE_BOTH)
                    ));
                }
            }

            timeline.setOnFinished(e -> {
                for (Node node : opponentsSidebar.getChildren()) {
                    VBox box = (VBox) node;
                    VBox details = (VBox) box.getChildren().get(1);
                    if (box != expandedOpponentBox) {
                        details.setManaged(false);
                        details.setVisible(false);
                    }
                }
            });

            expandedOpponentBox = isOpening ? container : null;
            timeline.play();
        });

        return container;
    }

    private VBox createResponsiveStat(String imagePath, String value, double divideFactor) {
        VBox stat = new VBox(2);
        stat.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(loadImage(imagePath));
        icon.setPreserveRatio(true);
        icon.fitHeightProperty().bind(opponentsSidebar.widthProperty().divide(divideFactor));

        Text text = new Text(value);
        text.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font(mesosFont != null ? mesosFont.getFamily() : "Arial", opponentsSidebar.getWidth() / 15),
                opponentsSidebar.widthProperty()
        ));

        stat.getChildren().addAll(icon, text);
        return stat;
    }

    private String getRGBFromColor(Color color) {
        if (color == null) return "200, 200, 200"; //fallback
        switch (color.name()) {
            case "YELLOW": return Totem.YELLOW.getTotemColorRGB();
            case "PURPLE": return Totem.PURPLE.getTotemColorRGB();
            case "WHITE": return Totem.WHITE.getTotemColorRGB();
            case "ORANGE": return Totem.ORANGE.getTotemColorRGB();
            case "TURQUOISE": return Totem.TURQUOISE.getTotemColorRGB();
            default: return "200, 200, 200";
        }
    }

    private void drawTurnOrderTile() {
        turnOrderContainer.getChildren().clear();
        if (smallModel == null) return;

        List<?> playersOnTile = (List<?>) smallModel.getTurnOrderTile();
        int nPlayers = playersOnTile.size();

        if (nPlayers == 0) {
            nPlayers = smallModel.getOpponents().size() + 1;
        }

        TurnOrderTileInfo tileInfo = TurnOrderTileInfo.getInfo(nPlayers);
        if (tileInfo == null) {
            tileInfo = TurnOrderTileInfo.TURN_ORDER_TILE_5_PLAYERS; // Fallback
        }

        Image img = loadImage(tileInfo.getImagePath());
        if (img == null) {
            img = loadImage(TurnOrderTileInfo.TURN_ORDER_TILE_5_PLAYERS.getImagePath());
        }

        ArrayList<Totem> activeTotems = new ArrayList<>();
        for (Object obj : playersOnTile) {
            if (obj instanceof PlayerView) {
                PlayerView playerView = (PlayerView) obj;
                Totem t = Totem.NONE;
                if (playerView.getColor() != null) {
                    switch (playerView.getColor().name()) {
                        case "YELLOW": t = Totem.YELLOW; break;
                        case "PURPLE": t = Totem.PURPLE; break;
                        case "WHITE": t = Totem.WHITE; break;
                        case "ORANGE": t = Totem.ORANGE; break;
                        case "TURQUOISE": t = Totem.TURQUOISE; break;
                    }
                }
                activeTotems.add(t);
            }
        }

        if (activeTotems.isEmpty()) {
            activeTotems = new ArrayList<>(Arrays.asList(Totem.YELLOW, Totem.TURQUOISE, Totem.PURPLE, Totem.WHITE, Totem.ORANGE));
        }

        TurnOrderTileView turnOrderTile = createTurnOrderTile(img, activeTotems);
        turnOrderContainer.getChildren().add(turnOrderTile);
    }

    private void drawOfferTrack() {
        offerTrackContainer.getChildren().clear();
        if (smallModel == null) return;

        for (var tile : smallModel.getOfferTrack()) {
            Image img = new Image(imageFetcher.fetch(tile));
            // TODO: give the player a Totem or retrieve it
            String nickname = tile.getPlayer() != null ? tile.getPlayer().getNickname() : "Empty";
            Totem t = Totem.NONE;

            offerTrackContainer.getChildren().add(createOfferTile(img, t, nickname, 0.50, 0.20));
        }
    }

    private void drawDeck() {
        if (smallModel == null) return;
        switch (smallModel.getEra()) {
            case ERA_I:
                deckImage.setImage(loadImage("tribe_card_era_I_back.png"));
                break;
            case ERA_II:
                deckImage.setImage(loadImage("tribe_card_era_II_back.png"));
                break;
            case ERA_III:
                deckImage.setImage(loadImage("tribe_card_era_III_back.png"));
                break;
        }
    }

    private void drawTopRowCards() {
        topCharactersContainer.getChildren().clear();
        if (smallModel == null) return;
        for (Card card : smallModel.getTopRow()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(topRowBox.heightProperty().multiply(0.85));
            setCardEffect(cardView);
            cardView.setCard(card);
            topCharactersContainer.getChildren().add(cardView);
        }
    }

    private void drawTopBuildingsCards() {
        buildingsContainer.getChildren().clear();
        if (smallModel == null) return;
        for (Card building : smallModel.getTopBuildings()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(topRowBox.heightProperty().multiply(0.85));
            setCardEffect(cardView);
            cardView.setCard(building);
            buildingsContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomRowCards() {
        bottomCharactersContainer.getChildren().clear();
        if (smallModel == null) return;
        for (Card card : smallModel.getBottomRow()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(0.85));
            setCardEffect(cardView);
            cardView.setCard(card);
            bottomCharactersContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomBuildingCards() {
        if (bottomBuildingsContainer == null) return;
        bottomBuildingsContainer.getChildren().clear();
        if (smallModel == null) return;

        for (Card building : smallModel.getBottomBuildings()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(0.85));
            setCardEffect(cardView);
            cardView.setCard(building);
            bottomBuildingsContainer.getChildren().add(cardView);
        }
    }

    private void drawSkipButton() {
        if (smallModel == null) return;
        if (!smallModel.isActive() || !smallModel.isCanSkip()) {
            skipButton.setDisable(true);
            toggleSkipButton(false);
        } else {
            skipButton.setDisable(false);
            toggleSkipButton(true);
        }
    }

    private void setupDeckPopup() {
        Popup nCards = EffectsManager.createDeckPopup();
        deckImage.setOnMouseEntered((event -> {
            nCards.show(deckImage, event.getScreenX(), event.getScreenY());
            EffectsManager.playPopupIn(nCards);
        }));
        deckImage.setOnMouseMoved((event -> {
            nCards.setX(event.getScreenX() - 35);
            nCards.setY(event.getScreenY() - 60);
        }));
        deckImage.setOnMouseExited((event -> {
            nCards.hide();
        }));
    }

    private void setCardEffect(CardView cardView) {
        CardEffectVisitor visitor = new CardEffectVisitor(cardView);
        cardView.getCard().accept(visitor);
    }

    private void applyEffectToContainerCardViews(HBox container) {
        if (container == null) return;
        for (Node node : container.getChildren()) {
            if (node instanceof CardView) {
                setCardEffect((CardView) node);
            }
        }
    }

    private TurnOrderTileView createTurnOrderTile(Image image, ArrayList<Totem> totems) {
        int nPlayers = (smallModel == null) ? totems.size() : smallModel.getOpponents().size() + 1;
        TurnOrderTileView turnOrderTile = new TurnOrderTileView(image, nPlayers);

        turnOrderTile.prefHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        turnOrderTile.maxHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        turnOrderTile.getImageView().fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        double ratio = image.getWidth() / image.getHeight();
        turnOrderTile.minWidthProperty().bind(turnOrderTile.prefHeightProperty().multiply(ratio));
        turnOrderTile.prefWidthProperty().bind(turnOrderTile.prefHeightProperty().multiply(ratio));

        ArrayList<TotemPieceView> totemPieces = new ArrayList<>();
        int count = 1;
        for (Totem totem : totems) {
            totemPieces.add(new TotemPieceView(totem, "Player " + count));
            count++;
        }

        turnOrderTile.setTotemPieces(totemPieces);
        return turnOrderTile;
    }

    private OffertTileView createOfferTile(Image image, Totem totem, String playerName, double xPercent, double yPercent) {
        OffertTileView tile = new OffertTileView(image);

        tile.prefHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        tile.maxHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        tile.getImageView().fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        double ratio = image.getWidth() / image.getHeight();
        tile.minWidthProperty().bind(tile.prefHeightProperty().multiply(ratio));
        tile.prefWidthProperty().bind(tile.prefHeightProperty().multiply(ratio));

        if (totem != null) {
            tile.setTotem(new TotemPieceView(totem, playerName));
        }
        tile.setCenterPercentage(xPercent, yPercent);
        return tile;
    }

    private void toggleSkipButton(boolean canSkip) {
        skipButton.pseudoClassStateChanged(DISABLED_STYLE, !canSkip);
        skipButton.applyCss();
        skipButton.setCursor(canSkip ? Cursor.HAND : Cursor.DEFAULT);
    }

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        PropertyChangeName eventName;
//        try {
//            eventName = PropertyChangeName.valueOf(evt.getPropertyName());
//        } catch (IllegalArgumentException ex) {
//            System.err.println("Illegal property: " + evt.getPropertyName());
//            return;
//        }
//
//        Platform.runLater(() -> {
//            switch (eventName) {
//                case PICK_FROM_TOP_ROW:
//                    handleTopRowPick();
//                    break;
//                case PICK_FROM_BOTTOM_ROW:
//                    handleBottomRowPick();
//                    break;
//                case PICK_FROM_TOP_BUILDINGS:
//                    handleTopBuildingsPick();
//                    break;
//                case PICK_FROM_BOTTOM_BUILDINGS:
//                    handleBottomBuildingsPick();
//                    break;
//                case TOP_ROW_REFILL:
//                    handleTopRowRefill();
//                    break;
//                case TOP_BUILDINGS_REFILL:
//                    handleTopBuildingsRefill();
//                    break;
//
//                case TOTEM_MOVED_OFFER:
//                case TOTEM_PLACEMENT_TURN:
//                    handleTotemMoved();
//                    break;
//
//                case ROUND_CHANGED:
//                    handleRoundChanged();
//                    break;
//                case ACTIVE_PLAYER_CHANGED:
//                    handleActivePlayerChanged();
//                    break;
//
//                // Da implementare
//                case PLAYER_CAN_SKIP:
//                case FOOD_CHANGED:
//                case PRESTIGE_CHANGED:
//                case TOP_NUM_DRAW_CHANGED:
//                case BOTTOM_NUM_DRAW_CHANGED:
//                case PHASE_CHANGED:
//                case ERA_CHANGED:
//                case IS_END_GAME:
//                    break;
//            }
//        });
//    }

    public void handleTopRowRefill() {
        drawTopRowCards();
        drawDeck();
    }

    public void handleTopBuildingsRefill() {
        drawTopBuildingsCards();
        drawBottomBuildingCards();
    }

    public void handleTopRowPick() {
        drawTopRowCards();
        applyEffectToContainerCardViews(buildingsContainer);
        drawSkipButton();
    }

    public void handleBottomRowPick() {
        drawBottomRowCards();
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
    }

    public void handleTopBuildingsPick() {
        drawTopBuildingsCards();
        applyEffectToContainerCardViews(topCharactersContainer);
        drawSkipButton();
    }

    public void handleBottomBuildingsPick() {
        drawBottomBuildingCards();
        applyEffectToContainerCardViews(bottomCharactersContainer);
        drawSkipButton();
    }

    public void handleTotemMoved() {
        drawTurnOrderTile();
        drawOfferTrack();
    }

    public void handleRoundChanged() {
        drawDeck();
        drawTopRowCards();
        drawTopBuildingsCards();
        drawBottomRowCards();
        drawBottomBuildingCards();
    }

    public void handleActivePlayerChanged() {
        applyEffectToContainerCardViews(topCharactersContainer);
        applyEffectToContainerCardViews(buildingsContainer);
        applyEffectToContainerCardViews(bottomCharactersContainer);
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
    }
}