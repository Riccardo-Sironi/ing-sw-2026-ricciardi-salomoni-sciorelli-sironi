package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameTurnManager.EndOfRoundPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.EventResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.gui.elements.*;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.LayoutConfiguration;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.gui.visitors.CardEffectVisitor;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

import static it.polimi.gc06.mesos.view.gui.GUI.*;

public class BoardController implements ModelListener {

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
    public Text roundText;
    @FXML
    public Text phaseText;
    @FXML
    public Text eraText;

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
    private Text deckText;
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

    private StackPane inventoryOverlay = null;

    private TurnOrderTileView turnOrderTile = null;
    private ArrayList<OfferTileView> offerTrackTiles = null;

    private HashMap<String, OpponentBox> opponentsBoxes;

    private HBox helpOverlay = null;

    private static final PseudoClass DISABLED_STYLE = PseudoClass.getPseudoClass("skip-disabled");

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ), 20
    );

    private static final double RESIZE_CARD_FACTOR = 0.85;

    private LayoutConfiguration currentLayout = LayoutConfiguration.CAVE_COLORS;

    @FXML
    public void initialize() {
        setupArchitecturalLayout();

        setupGameCommands();
    }

    public void setupGameCommands() {
        Platform.runLater(() -> {
            mainRoot.requestLayout();
            mainRoot.getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.H) {
                    toggleHelpOverlay();
                }
                if (e.getCode() == KeyCode.DIGIT1) {
                    smallModel.setPhase(new PlacingTotemPhase().toString());
                    smallModel.setActive(true);
                    drawEverything();
                }
                if (e.getCode() == KeyCode.DIGIT2) {
                    smallModel.setPhase(new OfferResolutionPhase().toString());
                    smallModel.setActive(true);
                    drawEverything();
                }
                if (e.getCode() == KeyCode.DIGIT3) {
                    smallModel.setPhase(new EventResolutionPhase().toString());
                    smallModel.setActive(true);
                    drawEverything();
                }
                if (e.getCode() == KeyCode.DIGIT4) {
                    smallModel.setPhase(new EndOfRoundPhase().toString());
                    smallModel.setActive(true);
                    drawEverything();
                }
                if (e.getCode() == KeyCode.DIGIT8) {
                    currentLayout = LayoutConfiguration.CAVE_COLORS;
                    drawPlayerInventory();
                    drawOpponentsSidebar();
                }
                if (e.getCode() == KeyCode.DIGIT9) {
                    currentLayout = LayoutConfiguration.CAVE;
                    drawPlayerInventory();
                    drawOpponentsSidebar();
                }
                // HIDE OPPONENTS SIDE BAR
                if (e.getCode() == KeyCode.O) {
                    if (!mainRoot.getChildren().contains(opponentsSidebar) && leftZone.getChildren().contains(inventoryBox)) {
                        mainRoot.getChildren().add(opponentsSidebar);
                        leftZone.prefWidthProperty().bind(mainRoot.widthProperty().multiply(0.85));
                    } else {
                        mainRoot.getChildren().remove(opponentsSidebar);
                        leftZone.prefWidthProperty().bind(mainRoot.widthProperty());
                    }
                }
                // FOCUS MODE (HIDE INVENTORY AND OPPONENTS)
                if (e.getCode() == KeyCode.F) {
                    Pane root = (Pane) mainRoot.getParent();

                    if (inventoryOverlay != null && inventoryOverlay.getChildren().contains(inventoryBox)) {
                        hideInventoryOverlay(root);
                    }

                    if (!leftZone.getChildren().contains(inventoryBox) && !mainRoot.getChildren().contains(opponentsSidebar)) {
                        leftZone.getChildren().add(inventoryBox);
                        mainRoot.getChildren().add(opponentsSidebar);

                        // re-bind the board root height to previous dimension
                        boardRoot.prefHeightProperty().bind(leftZone.heightProperty().multiply(0.80));
                        leftZone.prefWidthProperty().bind(mainRoot.widthProperty().multiply(0.85));
                    } else {
                        leftZone.getChildren().remove(inventoryBox);
                        mainRoot.getChildren().remove(opponentsSidebar);

                        // the board takes the full height of leftzone
                        boardRoot.prefHeightProperty().bind(leftZone.heightProperty());
                        // the left zone takes ful width
                        leftZone.prefWidthProperty().bind(mainRoot.widthProperty());
                    }
                }
                // SHOW INVENTORY WHILE IN FOCUS MODE
                if (e.getCode() == KeyCode.I) {
                    if (leftZone.getChildren().contains(inventoryBox)) {
                        return;
                    }

                    Pane root = (Pane) mainRoot.getParent();

                    if (inventoryOverlay != null && inventoryOverlay.getChildren().contains(inventoryBox)) {
                        hideInventoryOverlay(root);
                    } else {
                        showInventoryOverlay(root);
                    }
                }
            });
        });
    }

    public void drawEverything() {
        drawEraText();
        drawRoundText();
        drawPhaseText();
        drawEraText();
        drawDeck();
        drawTopRowCards();
        drawTopBuildingsCards();
        drawBottomRowCards();
        drawBottomBuildingCards();
        drawTurnOrderTile();
        drawOfferTrack();
        drawSkipButton();
        drawPlayerInventory();
        initOpponentsSideBar();
        drawOpponentsSidebar();
    }

    private void setupArchitecturalLayout() {
        final double WIDTH_LEFT_ZONE = 0.85;
        final double WIDTH_OPPONENTS = 0.15;

        leftZone.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_LEFT_ZONE));
        opponentsSidebar.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_OPPONENTS));
        leftZone.prefHeightProperty().bind(mainRoot.heightProperty());
        opponentsSidebar.prefHeightProperty().bind(mainRoot.heightProperty());

        leftZone.setSpacing(10);

        final double HEIGHT_BOARD = 0.80;
        final double HEIGHT_INVENTORY = 0.20;

        boardRoot.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_BOARD));
        inventoryBox.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_INVENTORY));

        boardRoot.setSpacing(10);
        boardRoot.setPadding(new Insets(5));

        inventoryBox.setAlignment(Pos.CENTER);

        configureInventoryStyle();

        playerStatsBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.3));
        configureScrollPane(cardsScroll, playerCardsContainer, leftZone.widthProperty().multiply(0.55));
        tokensBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));

        cardsScroll.setMaxHeight(Region.USE_PREF_SIZE);

        tokensBox.setAlignment(Pos.CENTER);
        tokensBox.setSpacing(5);

        configureTokensContainer(prestigeTokensBox, prestigeTokensImage, loadImage("imgs/tokens/prestige_token.png"), prestigeTokensText);
        configureTokensContainer(foodTokensBox, foodTokensImage, loadImage("imgs/tokens/food_token.png"), foodTokensText);

        playerStatsBox.setAlignment(Pos.CENTER);
        playerStatsBox.setSpacing(5);

        configureStatContainer(shamanStarsBox, shamanStarsImage, loadImage("imgs/tokens/shaman_stars_token.png"), shamanStarsText);
        configureStatContainer(gathererQuantityBox, gathererQuantityImage, loadImage("imgs/tokens/gatherers_token.png"), gathererQuantityText);
        configureStatContainer(hunterQuantityBox, hunterQuantityImage, loadImage("imgs/tokens/hunters_token.png"), hunterQuantityText);
        configureStatContainer(artistQuantityBox, artistQuantityImage, loadImage("imgs/tokens/artists_token.png"), artistQuantityText);
        configureStatContainer(buildersDiscountBox, buildersDiscountImage, loadImage("imgs/tokens/builder_discount_token.png"), buildersDiscountText);

        topBar.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.1));
        topRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));
        centerRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));
        bottomRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));

        topBar.setAlignment(Pos.CENTER);

        roundText.wrappingWidthProperty().bind(leftZone.widthProperty().divide(3).subtract(100));
        phaseText.wrappingWidthProperty().bind(leftZone.widthProperty().divide(3).subtract(100));
        eraText.wrappingWidthProperty().bind(leftZone.widthProperty().divide(3).subtract(100));

        roundText.setText("Round ?");
        roundText.setFont(Font.font(mesosFont.getFamily(), 30));
        roundText.setTextAlignment(TextAlignment.CENTER);
        roundText.setFill(Color.WHITE);
        phaseText.setText("Phase ?");
        phaseText.setFont(Font.font(mesosFont.getFamily(), 35));
        phaseText.setTextAlignment(TextAlignment.CENTER);
        phaseText.setFill(Color.WHITE);
        eraText.setText("Era ?");
        eraText.setFont(Font.font(mesosFont.getFamily(), 30));
        eraText.setTextAlignment(TextAlignment.CENTER);
        eraText.setFill(Color.WHITE);

        topRowBox.setAlignment(Pos.CENTER);
        topRowBox.setSpacing(20);
        DoubleBinding topWidth = leftZone.widthProperty().multiply(0.80).subtract(40);
        configureScrollPane(topCharactersScroll, topCharactersContainer, topWidth.multiply(0.7).subtract(20));
        configureScrollPane(buildingsScroll, buildingsContainer, topWidth.multiply(0.3).subtract(20));

        centerRowBox.setAlignment(Pos.CENTER);
        centerRowBox.setSpacing(30);
        deckContainer.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));
        deckContainer.setAlignment(Pos.CENTER);
        HBox.setHgrow(deckContainer, Priority.NEVER);
        deckImage.fitHeightProperty().bind(deckContainer.heightProperty().multiply(0.85));
        setupDeckPopup();

        turnOrderContainer.setAlignment(Pos.CENTER_RIGHT);

        offerTrackContainer.setAlignment(Pos.CENTER);
        offerTrackContainer.setSpacing(-2);

        skipButtonContainer.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));
        skipButtonContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(skipButtonContainer, Priority.NEVER);

        setupSkipButton();
    }

    private void configureTokensContainer(VBox container, ImageView containerImageView, Image containerImage,
                                          Text containerText) {
        container.prefWidthProperty().bind(tokensBox.widthProperty().divide(2));
        container.prefHeightProperty().bind(tokensBox.heightProperty());
        container.maxHeightProperty().bind(tokensBox.heightProperty());
        container.setMinWidth(0);

        containerImageView.setImage(containerImage);
        containerImageView.setPreserveRatio(true);
        containerImageView.setSmooth(true);
        containerImageView.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(0.3));

        containerText.setText("0");
        containerText.setFont(mesosFont);
        containerText.setFill(Color.WHITE);
        containerText.setTextAlignment(TextAlignment.CENTER);
        containerText.wrappingWidthProperty().bind(container.widthProperty());
    }

    private void configureStatContainer(VBox container, ImageView containerImageView,
                                        Image containerImage, Text containerText) {
        container.prefWidthProperty().bind(playerStatsBox.widthProperty().divide(5));
        container.maxHeightProperty().bind(playerStatsBox.heightProperty());
        container.prefHeightProperty().bind(playerStatsBox.heightProperty());
        container.setMinWidth(0);

        containerImageView.setImage(containerImage);
        containerImageView.setPreserveRatio(true);
        containerImageView.setSmooth(true);
        containerImageView.fitHeightProperty().bind(container.heightProperty().multiply(0.3));

        containerText.setText("0");
        containerText.setFont(mesosFont);
        containerText.setFill(Color.WHITE);
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
        String mainColor = "0,0,0";
        String color = "0,0,0";

        String backgroundInventoryStyle = "-fx-background-color: rgba(" + color + ", 0.4);";
        String borderInventoryStyle = "-fx-border-color: rgba(" + mainColor + ", 1.0); -fx-border-width: 2px; -fx-border-radius: 10px";

        inventoryBox.setStyle(backgroundInventoryStyle + " " + borderInventoryStyle);

        playerStatsBox.setPadding(new Insets(5));
        tokensBox.setPadding(new Insets(5));

        double radius = 10;
        cardsScroll.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            Rectangle clip = new Rectangle(newBounds.getWidth(), newBounds.getHeight());
            clip.setArcWidth(radius * 2);
            clip.setArcHeight(radius * 2);
            cardsScroll.setClip(clip);
        });
    }

    public void updateBottomRowLayout(boolean showBuildings) {
        bottomRowBox.setAlignment(Pos.CENTER);
        bottomRowBox.setSpacing(showBuildings ? 20 : 0);

        DoubleBinding bottomWidth = leftZone.widthProperty().multiply(0.80);
        if (showBuildings) bottomWidth = bottomWidth.subtract(20);

        if (showBuildings) {
            configureScrollPane(bottomCharactersScroll, bottomCharactersContainer, bottomWidth.multiply(0.7));
            if (bottomBuildingsScroll != null) {
                bottomBuildingsScroll.setVisible(true);
                bottomBuildingsScroll.setManaged(true);
                configureScrollPane(bottomBuildingsScroll, bottomBuildingsContainer, bottomWidth.multiply(0.3));
            }
        } else {
            configureScrollPane(bottomCharactersScroll, bottomCharactersContainer, bottomWidth.multiply(0.7));
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

    public void initOpponentsSideBar() {
        opponentsSidebar.getChildren().clear();
        opponentsSidebar.setSpacing(15);
        opponentsSidebar.setPadding(new Insets(15, 5, 15, 5));
        opponentsBoxes = new HashMap<>();
        for (PlayerView opponent : smallModel.getOpponents()) {
            String rgbColor = Totem.getTotem(opponent.getColor()).getTotemColorRGB();
            OpponentBox opponentBox = createOpponentInventoryBox(opponent.getNickname(), rgbColor, opponent);
            opponentsBoxes.put(opponent.getNickname(), opponentBox);
            opponentsSidebar.getChildren().add(opponentBox);
        }
    }

    public void drawOpponentsSidebar() {
        for (PlayerView opponent : smallModel.getOpponents()) {
            OpponentBox opp = opponentsBoxes.get(opponent.getNickname());

            opp.getCardsContainer().getChildren().clear();

            for (Card c : opponent.getCharacters()) {
                CardView cardView = new CardView(loadImage(imageFetcher.fetch(c)));
                cardView.fitHeightProperty().bind(opp.getCardsContainer().heightProperty().multiply(RESIZE_CARD_FACTOR));
                opp.getCardsContainer().getChildren().add(cardView);
            }

            opp.getPrestigeTokensText().setText(String.valueOf(opponent.getNumPrestige()));
            opp.getFoodTokensText().setText(String.valueOf(opponent.getNumFood()));

            opp.getShamanStarsText().setText(String.valueOf(opponent.getNumShamanStar()));
            opp.getGatherersText().setText(String.valueOf(opponent.getNumGatherer()));
            opp.getHuntersText().setText(String.valueOf(opponent.getNumHunter()));
            opp.getArtistsText().setText(String.valueOf(opponent.getNumArtist()));
            opp.getBuildersDiscountText().setText(String.valueOf(opponent.getBuildersDiscount()));

            String backgroundColor;
            String borderColor;

            switch (currentLayout) {
                case CAVE -> {
                    opp.getShamanStarsText().setFill(Color.WHITE);
                    opp.getGatherersText().setFill(Color.WHITE);
                    opp.getHuntersText().setFill(Color.WHITE);
                    opp.getArtistsText().setFill(Color.WHITE);
                    opp.getBuildersDiscountText().setFill(Color.WHITE);

                    backgroundColor = "0,0,0";
                    borderColor = Totem.getTotem(opponent.getColor()).getTotemColorRGB();
                }
                case CAVE_COLORS -> {
                    borderColor = Totem.getTotem(opponent.getColor()).getTotemColorRGB();
                    backgroundColor = borderColor;
                }
                default -> {
                    backgroundColor = borderColor = "0,0,0";
                }
            }

            String backgroundInventoryStyle = "-fx-background-color: rgba(" + backgroundColor + ", 0.4);";
            String borderInventoryStyle = "-fx-border-color: rgba(" + borderColor + ", 1.0); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px";

            opp.setStyle(backgroundInventoryStyle + borderInventoryStyle);

        }
    }

    private OpponentBox createOpponentInventoryBox(String nickname, String rgbColor, PlayerView opponent) {
        OpponentBox container = new OpponentBox(nickname, rgbColor, opponent);

        container.minWidthProperty().bind(opponentsSidebar.widthProperty().multiply(0.9));
        container.maxWidthProperty().bind(opponentsSidebar.widthProperty().multiply(0.9));

        container.getNicknameText().fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font(mesosFont != null ? mesosFont.getFamily() : "Arial", opponentsSidebar.getWidth() / 10),
                opponentsSidebar.widthProperty()
        ));

        VBox prestigeStat = createResponsiveStat("imgs/tokens/prestige_token.png", 6.5, container::setPrestigeTokensText);
        VBox foodStat = createResponsiveStat("imgs/tokens/food_token.png", 6.5, container::setFoodTokensText);

        VBox shamanStarsStat = createResponsiveStat("imgs/tokens/shaman_stars_token.png", 6.5, container::setShamanStarsText);
        VBox gatherersStat = createResponsiveStat("imgs/tokens/gatherers_token.png", 6.5, container::setGatherersText);
        VBox huntersStat = createResponsiveStat("imgs/tokens/hunters_token.png", 6.5, container::setHuntersText);
        VBox artistsStat = createResponsiveStat("imgs/tokens/artists_token.png", 6.5, container::setArtistsText);
        VBox builderDiscount = createResponsiveStat("imgs/tokens/builder_discount_token.png", 6.5, container::setBuildersDiscountText);

        container.getPrestigeTokensText().setFill(Color.WHITE);
        container.getFoodTokensText().setFill(Color.WHITE);
        container.getTokensBox().setSpacing(20);

        container.getShamanStarsText().setFill(Color.WHITE);
        container.getGatherersText().setFill(Color.WHITE);
        container.getHuntersText().setFill(Color.WHITE);
        container.getArtistsText().setFill(Color.WHITE);
        container.getBuildersDiscountText().setFill(Color.WHITE);

        container.getTokensBox().getChildren().addAll(prestigeStat, foodStat);
        container.getStatsBox().getChildren().addAll(shamanStarsStat, gatherersStat, huntersStat, artistsStat, builderDiscount);

        configureScrollPane(container.getCardsScroll(), container.getCardsContainer(), container.widthProperty().multiply(0.8));

        // setup toggle animation for opponent info
        container.setOnMouseClicked(event -> {
            Timeline timeline = new Timeline();
            boolean isOpening = (expandedOpponentBox != container);

            for (Node node : opponentsSidebar.getChildren()) {
                VBox box = (VBox) node;
                VBox details = (VBox) box.getChildren().get(1);

                if (isOpening && box == container) {
                    details.setManaged(true);
                    details.setVisible(true);

                    double targetHeight = opponentsSidebar.getHeight() * 0.5;
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

    private VBox createResponsiveStat(String imagePath, double divideFactor, Consumer<Text> textSetter) {
        VBox stat = new VBox(5);
        stat.setAlignment(Pos.CENTER);

        ImageView icon = new ImageView(loadImage(imagePath));
        icon.setPreserveRatio(true);
        icon.fitHeightProperty().bind(opponentsSidebar.widthProperty().divide(divideFactor));

        Text text = new Text("0");
        text.fontProperty().bind(Bindings.createObjectBinding(() ->
                        Font.font(mesosFont != null ? mesosFont.getFamily() : "Arial", opponentsSidebar.getWidth() / 15),
                opponentsSidebar.widthProperty()
        ));

        stat.getChildren().addAll(icon, text);

        if (textSetter != null) {
            textSetter.accept(text);
        }

        return stat;
    }

    private void drawPlayerInventory() {
        if (smallModel == null || smallModel.getPlayer() == null) return;

        PlayerView p = smallModel.getPlayer();

        prestigeTokensText.setText(String.valueOf(p.getNumPrestige()));
        foodTokensText.setText(String.valueOf(p.getNumFood()));
        shamanStarsText.setText(String.valueOf(p.getNumShamanStar()));
        gathererQuantityText.setText(String.valueOf(p.getNumGatherer()));
        hunterQuantityText.setText(String.valueOf(p.getNumHunter()));
        artistQuantityText.setText(String.valueOf(p.getNumArtist()));
        buildersDiscountText.setText(String.valueOf(p.getBuildersDiscount()));

        playerCardsContainer.getChildren().clear();

        for (Card card : p.getCharacters()) {
            CardView cardView = new CardView(loadImage(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(inventoryBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            playerCardsContainer.getChildren().add(cardView);
        }

        String backgroundColor;
        String borderColor;

        switch (currentLayout) {
            case CAVE -> {
                backgroundColor = "0,0,0";
                borderColor = Totem.getTotem(p.getColor()).getTotemColorRGB();
            }
            case CAVE_COLORS -> {
                backgroundColor = borderColor = Totem.getTotem(p.getColor()).getTotemColorRGB();
            }
            default -> {
                backgroundColor = borderColor = "0,0,0";
            }
        }

        String backgroundInventoryStyle = "-fx-background-color: rgba(" + backgroundColor + ", 0.4);";
        String borderInventoryStyle = "-fx-border-color: rgba(" + borderColor + ", 1.0); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px";

        inventoryBox.setStyle(backgroundInventoryStyle + borderInventoryStyle);
    }

    private void drawDeck() {
        if (smallModel == null) return;

        deckText.setText(String.valueOf(smallModel.getTribeDeckSize()));

        if (smallModel.isEndgame()) {
            deckImage.setImage(loadImage("/cards/backs/tribe_card_era_III_final_back.png"));
            return;
        }

        switch (smallModel.getEra()) {
            case ERA_I:
                deckImage.setImage(loadImage("/cards/backs/tribe_card_era_I_back.png"));
                break;
            case ERA_II:
                deckImage.setImage(loadImage("/cards/backs/tribe_card_era_II_back.png"));
                break;
            case ERA_III:
                deckImage.setImage(loadImage("/cards/backs/tribe_card_era_III_back.png"));
                break;
        }
    }

    private void drawTopRowCards() {
        topCharactersContainer.getChildren().clear();
        if (smallModel == null) return;
        for (int i = 0; i < smallModel.getTopRow().size(); i++) {
            Card card = smallModel.getTopRow().get(i);
            final int cardIndex = i;

            CardView cardView = new CardView(loadImage(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(topRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            cardView.setCard(card);

            applyPickCardAnimation(cardView, playerCardsContainer, mainRoot, () -> {
                try {
                    client.getServerConnection().pickCardFromTop(smallModel.getPlayer().getNickname(), cardIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            CardEffectVisitor visitor = new CardEffectVisitor(cardView, smallModel.getTopDrawNum());
            cardView.getCard().accept(visitor);

            topCharactersContainer.getChildren().add(cardView);
        }
    }

    private void drawTopBuildingsCards() {
        buildingsContainer.getChildren().clear();
        if (smallModel == null) return;

        for (int i = 0; i < smallModel.getTopBuildings().size(); i++) {
            Card building = smallModel.getTopBuildings().get(i);
            final int cardIndex = i;

            CardView cardView = new CardView(loadImage(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(topRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            cardView.setCard(building);

            applyPickCardAnimation(cardView, playerCardsContainer, mainRoot, () -> {
                try {
                    client.getServerConnection().pickBuildingFromTop(smallModel.getPlayer().getNickname(), cardIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            CardEffectVisitor visitor = new CardEffectVisitor(cardView, smallModel.getTopDrawNum());
            cardView.getCard().accept(visitor);

            buildingsContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomRowCards() {
        bottomCharactersContainer.getChildren().clear();
        if (smallModel == null) return;

        for (int i = 0; i < smallModel.getBottomRow().size(); i++) {
            Card card = smallModel.getBottomRow().get(i);
            final int cardIndex = i;

            CardView cardView = new CardView(loadImage(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            cardView.setCard(card);

            applyPickCardAnimation(cardView, playerCardsContainer, mainRoot, () -> {
                try {
                    client.getServerConnection().pickCardFromBottom(smallModel.getPlayer().getNickname(), cardIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            CardEffectVisitor visitor = new CardEffectVisitor(cardView, smallModel.getBottomDrawNum());
            cardView.getCard().accept(visitor);

            bottomCharactersContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomBuildingCards() {
        // Gestione layout visivo esistente
        if (smallModel.getBottomBuildings().isEmpty()) {
            updateBottomRowLayout(false);
        } else {
            updateBottomRowLayout(true);
        }

        if (bottomBuildingsContainer == null) return;
        bottomBuildingsContainer.getChildren().clear();
        if (smallModel == null) return;

        for (int i = 0; i < smallModel.getBottomBuildings().size(); i++) {
            Card building = smallModel.getBottomBuildings().get(i);
            final int cardIndex = i;

            CardView cardView = new CardView(loadImage(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(RESIZE_CARD_FACTOR));
            cardView.setCard(building);

            applyPickCardAnimation(cardView, playerCardsContainer, mainRoot, () -> {
                try {
                    client.getServerConnection().pickBuildingFromBottom(smallModel.getPlayer().getNickname(), cardIndex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            CardEffectVisitor visitor = new CardEffectVisitor(cardView, smallModel.getBottomDrawNum());
            cardView.getCard().accept(visitor);

            bottomBuildingsContainer.getChildren().add(cardView);
        }
    }

    private void drawSkipButton() {
        if (!smallModel.getPhase().equals(new OfferResolutionPhase().toString())) {
            skipButton.setVisible(false);
        } else {
            skipButton.setVisible(true);
            if (!smallModel.isActive() || !smallModel.isCanSkip()) {
                skipButton.setDisable(false);
                toggleSkipButton(true);
            } else {
                skipButton.setDisable(false);
                toggleSkipButton(true);
            }
        }
    }

    private void setupDeckPopup() {
        Popup nCards = createDeckPopup();
        deckImage.setOnMouseEntered((event -> {
            nCards.show(deckImage, event.getScreenX(), event.getScreenY());
            EffectsManager.playPopupIn(nCards);
        }));
        deckImage.setOnMouseMoved((event -> {
            nCards.setX(event.getScreenX() - 25);
            nCards.setY(event.getScreenY() - 50);
        }));
        deckImage.setOnMouseExited((event -> {
            nCards.hide();
        }));
    }

    public Popup createDeckPopup() {
        Popup popup = new Popup();
        HBox popupContent = new HBox();
        popupContent.setStyle(
                "-fx-background-color: rgba(255,255,255,0.8);" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: rgba(0,0,0);" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-padding: 8px 15px;"
        );
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMouseTransparent(true);

        deckText = new Text();
        deckText.setFont(mesosFont);
        deckText.setFill(Color.BLACK);

        popupContent.getChildren().add(deckText);
        popup.getContent().add(popupContent);

        return popup;
    }

    /**
     * This method applies a smooth animation to a card when it's picked from the top/bottom rows or the buildings rows and moved to the player's inventory.
     * The card will visually move from its original position to the player's inventory area, creating a more engaging user experience.
     * It is called before redrawing the personal decks of the target player, based on the small model data.
     *
     * @param card the card picked
     */
    public static void applyPickCardAnimation(CardView card, HBox targetContainer, HBox mainRoot, Runnable networkRequest) {
        card.setOnMouseClicked(event -> {
            Bounds cardScreen = card.localToScreen(card.getBoundsInLocal());
            Bounds targetScreen = targetContainer.localToScreen(targetContainer.getBoundsInLocal());
            if (cardScreen == null || targetScreen == null) return;

            // we need this overlay pane to make the card appear above all other elements during the animation
            Pane overlayPane = new Pane();
            Pane root = (Pane) mainRoot.getScene().getRoot();
            Bounds rootScreen = root.localToScreen(root.getBoundsInLocal());

            overlayPane.prefWidthProperty().bind(root.widthProperty());
            overlayPane.prefHeightProperty().bind(root.heightProperty());
            overlayPane.setMouseTransparent(true);
            root.getChildren().add(overlayPane);

            double cardX = cardScreen.getMinX() - rootScreen.getMinX();
            double cardY = cardScreen.getMinY() - rootScreen.getMinY();
            double targetX = targetScreen.getMinX() - rootScreen.getMinX();
            double targetY = targetScreen.getMinY() - rootScreen.getMinY();

            Pane originalParent = (Pane) card.getParent();
            originalParent.getChildren().remove(card); // TODO : we could do this and then redraw the original container

            card.relocate(cardX, cardY);
            card.setTranslateX(0);
            card.setTranslateY(0);
            overlayPane.getChildren().add(card);

            // adjust the speed of the animation based on the distance to travel, with a minimum and maximum duration
            double deltaX = targetX - cardX;
            double deltaY = targetY - cardY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            double speed = 0.9;
            long durationMs = (long) (distance / speed);

            durationMs = Math.clamp(durationMs, 300, 600);

            TranslateTransition transition = EffectsManager.createCardMoveTransition(card, targetX - cardX, targetY - cardY, durationMs);
            transition.setInterpolator(Interpolator.EASE_OUT);

            transition.setOnFinished(e -> {
                overlayPane.getChildren().remove(card);
                root.getChildren().remove(overlayPane);
                card.setTranslateX(0);
                card.setTranslateY(0);
                // the bind below ensure that the card is the same size of the others visually, it matters just during the
                // animation, then we re draw the inventory, and it will be draw like the others
                card.fitHeightProperty().bind(targetContainer.heightProperty().multiply(1));
                EffectsManager.normalCard(card);
                card.setOnMouseEntered(ev -> {
                });
                //targetContainer.getChildren().add(card); // TODO : we could do this and then redraw the original container

                if (networkRequest != null) {
                    networkRequest.run();
                }
            });

            transition.play();

            // TODO : the card should be redrawn in the inventory so no need to remove the effects but we keep it for now
            card.setOnMouseClicked(null);
        });
    }

    public void applySetTotemAnimation(OfferTileView tile) {
        TotemPieceView totemPiece = turnOrderTile.getTotemPieces().stream()
                .filter(tp -> tp.getPlayer() != null && tp.getPlayer().getNickname().equals(smallModel.getPlayer().getNickname()))
                .findFirst()
                .orElse(null);

        if (totemPiece != null) {
            tile.setOnMouseClicked(e -> {
                try {
                    client.getServerConnection().placeTotem(smallModel.getPlayer().getNickname(), offerTrackTiles.indexOf(tile));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    private void applyEffectToContainerCardViews(HBox container, int drawNum) {
        if (container == null) return;
        for (Node node : container.getChildren()) {
            if (node instanceof CardView cardView) {
                CardEffectVisitor visitor = new CardEffectVisitor(cardView, drawNum);
                cardView.getCard().accept(visitor);
            }
        }
    }

    private void setupSkipButton() {
        skipButton.setFont(mesosFont);

        ScaleTransition inS = new ScaleTransition(Duration.millis(150), skipButton);
        TranslateTransition inT = new TranslateTransition(Duration.millis(150), skipButton);
        inS.setInterpolator(Interpolator.EASE_BOTH);
        inT.setInterpolator(Interpolator.EASE_OUT);
        inS.setToX(1.1);
        inS.setToY(1.1);
        inT.setToY(-3);
        ScaleTransition outS = new ScaleTransition(Duration.millis(150), skipButton);
        TranslateTransition outT = new TranslateTransition(Duration.millis(150), skipButton);
        outS.setInterpolator(Interpolator.EASE_BOTH);
        outT.setInterpolator(Interpolator.EASE_BOTH);
        outS.setToX(1);
        outS.setToY(1);
        outT.setToY(1);

        skipButton.setOnMouseEntered(e -> {
            outT.stop();
            inT.play();
            outS.stop();
            inS.play();
        });

        skipButton.setOnMouseExited(e -> {
            inT.stop();
            outT.play();
            inS.stop();
            outS.play();
        });

        skipButton.setOnMouseClicked(e -> {
            // TODO : skip turn request
        });
    }

    private void initTurnOrderTile() {
        Image turnOrderImage = loadImage(imageFetcher.getTurnOrderTileUrl());
        turnOrderTile = new TurnOrderTileView(turnOrderImage);
        turnOrderTile.prefHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        turnOrderTile.maxHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        turnOrderTile.getImageView().fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        double ratio = turnOrderImage.getWidth() / turnOrderImage.getHeight();
        turnOrderTile.minWidthProperty().bind(turnOrderTile.prefHeightProperty().multiply(ratio));
        turnOrderTile.prefWidthProperty().bind(turnOrderTile.prefHeightProperty().multiply(ratio));

        turnOrderContainer.getChildren().add(turnOrderTile);
    }

    public void drawTurnOrderTile() {
        if (turnOrderTile == null) {
            initTurnOrderTile();
        }

        ArrayList<TotemPieceView> totemPieces = new ArrayList<>();
        for (PlayerView player : smallModel.getTurnOrderTile()) {
            if (player != null) {
                totemPieces.add(new TotemPieceView(player));
            } else {
                totemPieces.add(new TotemPieceView());
            }
        }

        turnOrderTile.setTotemPieces(totemPieces);
    }

    private void initOfferTrack() {
        offerTrackTiles = new ArrayList<>();

        for (TileSlotView tile : smallModel.getOfferTrack()) {
            OfferTileView offerTile = createOfferTile(tile);
            offerTrackTiles.add(offerTile);

            offerTrackContainer.getChildren().add(offerTile);
        }
    }

    private void drawOfferTrack() {

        if (offerTrackTiles == null) {
            initOfferTrack();
        }

        for (int i = 0; i < smallModel.getOfferTrack().size(); i++) {
            if (smallModel.getOfferTrack().get(i).getPlayer() != null) {
                offerTrackTiles.get(i).setTotem(new TotemPieceView(smallModel.getOfferTrack().get(i).getPlayer()));
            } else {
                OfferTileView tile = offerTrackTiles.get(i);
                tile.setTotem(new TotemPieceView());

                if (smallModel.isActive() && smallModel.getPhase().equals(new PlacingTotemPhase().toString())) {
                    // TODO : this should work but keep an eye on it
                    applySetTotemAnimation(tile);
                }
            }

            EffectsManager.setTileEffect(offerTrackTiles.get(i));
        }
    }

    private OfferTileView createOfferTile(TileSlotView tileSlotView) {
        Image img = loadImage(imageFetcher.fetch(tileSlotView));
        OfferTileView tile = new OfferTileView(img);

        tile.prefHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        tile.maxHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));
        tile.getImageView().fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        double ratio = img.getWidth() / img.getHeight();
        tile.minWidthProperty().bind(tile.prefHeightProperty().multiply(ratio));
        tile.prefWidthProperty().bind(tile.prefHeightProperty().multiply(ratio));

        tile.setCenterPercentage(0.5, 0.2);
        return tile;
    }

    private void toggleSkipButton(boolean canSkip) {
        skipButton.pseudoClassStateChanged(DISABLED_STYLE, !canSkip);
        skipButton.applyCss();
        skipButton.setCursor(canSkip ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void toggleHelpOverlay() {
        // TODO : we could add even more help information in the future ...

        Pane root = (Pane) mainRoot.getParent();
        if (helpOverlay == null) {
            helpOverlay = new HBox();
            helpOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            helpOverlay.prefWidthProperty().bind(root.widthProperty());
            helpOverlay.prefHeightProperty().bind(root.heightProperty());
            helpOverlay.setAlignment(Pos.CENTER);
            helpOverlay.setSpacing(50);

            ImageView helpImage1 = new ImageView(loadImage("/cards/info_card_front.png"));
            helpImage1.fitHeightProperty().bind(root.heightProperty().multiply(0.5));
            helpImage1.setPreserveRatio(true);

            ImageView helpImage2 = new ImageView(loadImage("/cards/info_card_back.png"));
            helpImage2.fitHeightProperty().bind(root.heightProperty().multiply(0.5));
            helpImage2.setPreserveRatio(true);

            helpOverlay.getChildren().addAll(helpImage1, helpImage2);

            helpOverlay.setOnMouseClicked(event -> {
                root.getChildren().remove(helpOverlay);
            });
        }

        if (root.getChildren().contains(helpOverlay)) {
            root.getChildren().remove(helpOverlay);
        } else {
            root.getChildren().add(helpOverlay);
        }
    }

    private void drawEraText() {
        eraText.setText(smallModel.getEra().toString());
    }

    private void drawPhaseText() {
        phaseText.setText(
                switch (smallModel.getPhase()) {
                    case "placing_totem" -> "Placing Totem Phase";
                    case "offer_resolution" -> "Offer Resolution Phase";
                    case "event_resolution" -> "Event Resolution Phase";
                    case "end_of_round" -> "End Of Round Phase";
                    default -> "?";
                }
        );
    }

    private void drawRoundText() {
        roundText.setText("Round " + smallModel.getRound());
    }

    private void showInventoryOverlay(Pane root) {
        if (inventoryOverlay == null) {
            inventoryOverlay = new StackPane();
            inventoryOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
            inventoryOverlay.prefWidthProperty().bind(root.widthProperty());
            inventoryOverlay.prefHeightProperty().bind(root.heightProperty());

            inventoryOverlay.setOnMouseClicked(event -> {
                if (event.getTarget() == inventoryOverlay && inventoryOverlay.getChildren().contains(inventoryBox)) {
                    hideInventoryOverlay(root);
                }
            });
        }

        inventoryBox.maxWidthProperty().bind(leftZone.widthProperty().multiply(0.95));
        inventoryBox.maxHeightProperty().bind(leftZone.heightProperty().multiply(0.25));

        inventoryOverlay.getChildren().add(inventoryBox);
        if (!root.getChildren().contains(inventoryOverlay)) {
            root.getChildren().add(inventoryOverlay);
        }
    }

    private void hideInventoryOverlay(Pane root) {
        inventoryOverlay.getChildren().remove(inventoryBox);
        root.getChildren().remove(inventoryOverlay);

        inventoryBox.maxWidthProperty().unbind();
        inventoryBox.maxHeightProperty().unbind();
        inventoryBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
        inventoryBox.setMaxHeight(Region.USE_COMPUTED_SIZE);
    }

    @Override
    public void update(SmallModelEditor dto) {
        System.out.println("PROVA");
        Platform.runLater(() -> {
            dto.accept(guidtovisitor);
        });
    }

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
        applyEffectToContainerCardViews(buildingsContainer, smallModel.getTopDrawNum());
        drawSkipButton();
    }

    public void handleBottomRowPick() {
        drawBottomRowCards();
        applyEffectToContainerCardViews(bottomBuildingsContainer, smallModel.getBottomDrawNum());
        drawSkipButton();
    }

    public void handleTopBuildingsPick() {
        drawTopBuildingsCards();
        applyEffectToContainerCardViews(topCharactersContainer, smallModel.getTopDrawNum());
        drawSkipButton();
    }

    public void handleBottomBuildingsPick() {
        drawBottomBuildingCards();
        applyEffectToContainerCardViews(bottomCharactersContainer, smallModel.getBottomDrawNum());
        drawSkipButton();
    }

    public void handleTotemMoved() {
        drawTurnOrderTile();
        drawOfferTrack();
    }

    public void handleRoundChanged() {
        drawRoundText();
    }

    public void handleActivePlayerChanged() {
        updateAllBoardEffects();
        drawSkipButton();
        drawOfferTrack();
    }

    public void handleGameStarted() {
        // TODO : da fare bene
        drawEverything();
    }

    public void handlePlayerResourcesChange() {
        drawPlayerInventory();
        drawOpponentsSidebar();
        updateAllBoardEffects();
    }

    public void handlePhaseChanged() {
        drawPhaseText();
        updateAllBoardEffects();
        drawOfferTrack();
        drawTurnOrderTile();
        drawTopRowCards();
        drawTopBuildingsCards();
        drawBottomRowCards();
        drawBottomBuildingCards();
        drawSkipButton();
    }

    public void handleEraChanged() {
        drawEraText();
    }

    public void updateAllBoardEffects() {
        applyEffectToContainerCardViews(topCharactersContainer, smallModel.getTopDrawNum());
        applyEffectToContainerCardViews(buildingsContainer, smallModel.getTopDrawNum());
        applyEffectToContainerCardViews(bottomCharactersContainer, smallModel.getBottomDrawNum());
        applyEffectToContainerCardViews(bottomBuildingsContainer, smallModel.getBottomDrawNum());
    }
}