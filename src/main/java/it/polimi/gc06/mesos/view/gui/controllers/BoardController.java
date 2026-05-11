package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.gui.elements.*;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.OfferTileInfo;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.gui.helpers.TurnOrderTileInfo;
import it.polimi.gc06.mesos.view.gui.visitors.CardEffectVisitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.css.PseudoClass;

public class BoardController implements PropertyChangeListener {

    @FXML
    private HBox mainRoot;
    @FXML
    private VBox leftZone;
    @FXML
    private VBox opponentsSidebar;

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

        // SE IL GIOCO E' COLLEGATO (smallModel esiste): Disegna la board reale
        if (smallModel != null) {
            drawDeck();
            drawTopRowCards();
            drawTopBuildingsCards();
            drawBottomRowCards();
            drawBottomBuildingCards();
            drawTurnOrderTile();
            drawOfferTrack();
            drawSkipButton();
        } else {
            int testNumPlayers = 4;
            setupMockData(testNumPlayers);
        }
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

        playerStatsBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.3));
        configureScrollPane(cardsScroll, playerCardsContainer, leftZone.widthProperty().multiply(0.55));
        tokensBox.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));

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

        DoubleBinding singleTileWidth = leftZone.widthProperty().multiply(0.60).divide(8);
        turnOrderContainer.prefWidthProperty().bind(singleTileWidth);
        turnOrderContainer.setAlignment(Pos.CENTER_RIGHT);

        offerTrackContainer.prefWidthProperty().bind(singleTileWidth.multiply(Bindings.size(offerTrackContainer.getChildren())));
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

    private Image loadImage(String path) {
        try {
            if (!path.startsWith("/")) path = "/" + path;
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            System.err.println("Immagine non trovata: " + path);
            return null;
        }
    }

    private void drawTurnOrderTile() {
        turnOrderContainer.getChildren().clear();
        if (smallModel == null) return;

        Image img = new Image(imageFetcher.fetch((Card) smallModel.getTurnOrderTile()));
        ArrayList<Totem> dummyTotems = new ArrayList<>(Arrays.asList(Totem.YELLOW, Totem.TURQUOISE, Totem.PURPLE, Totem.WHITE, Totem.ORANGE)); // TODO: logic

        TurnOrderTileView turnOrderTile = createTurnOrderTile(img, dummyTotems);
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
        for (Card building : smallModel.getTopBuilding()) {
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

        for (Card building : smallModel.getBottomBuilding()) {
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

        turnOrderTile.setMinSize(0, 0);
        turnOrderTile.prefHeightProperty().bind(turnOrderContainer.heightProperty());
        turnOrderTile.maxHeightProperty().bind(turnOrderContainer.heightProperty());
        turnOrderTile.getImageView().fitHeightProperty().bind(turnOrderContainer.heightProperty());

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
        tile.setMinSize(0, 0);
        tile.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        tile.prefHeightProperty().bind(offerTrackContainer.heightProperty());
        tile.maxHeightProperty().bind(offerTrackContainer.heightProperty());
        tile.getImageView().fitHeightProperty().bind(offerTrackContainer.heightProperty());
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PropertyChangeName eventName;
        try {
            eventName = PropertyChangeName.valueOf(evt.getPropertyName());
        } catch (IllegalArgumentException ex) {
            System.err.println("Illegal property: " + evt.getPropertyName());
            return;
        }

        Platform.runLater(() -> {
            switch (eventName) {
                case PICK_FROM_TOP_ROW:
                    handleTopRowPick();
                    break;
                case PICK_FROM_BOTTOM_ROW:
                    handleBottomRowPick();
                    break;
                case PICK_FROM_TOP_BUILDINGS:
                    handleTopBuildingsPick();
                    break;
                case PICK_FROM_BOTTOM_BUILDINGS:
                    handleBottomBuildingsPick();
                    break;
                case TOP_ROW_REFILL:
                    handleTopRowRefill();
                    break;
                case TOP_BUILDINGS_REFILL:
                    handleTopBuildingsRefill();
                    break;

                case TOTEM_MOVED_OFFER:
                case TOTEM_PLACEMENT_TURN:
                    handleTotemMoved();
                    break;

                case ROUND_CHANGED:
                    handleRoundChanged();
                    break;
                case ACTIVE_PLAYER_CHANGED:
                    handleActivePlayerChanged();
                    break;

                // Da implementare
                case PLAYER_CAN_SKIP:
                case FOOD_CHANGED:
                case PRESTIGE_CHANGED:
                case TOP_NUM_DRAW_CHANGED:
                case BOTTOM_NUM_DRAW_CHANGED:
                case PHASE_CHANGED:
                case ERA_CHANGED:
                case IS_END_GAME:
                    break;
            }
        });
    }

    private void handleTopRowRefill() {
        drawTopRowCards();
        drawDeck();
    }

    private void handleTopBuildingsRefill() {
        drawTopBuildingsCards();
        drawBottomRowCards();
    }

    private void handleTopRowPick() {
        drawTopRowCards();
        applyEffectToContainerCardViews(buildingsContainer);
        drawSkipButton();
    }

    private void handleBottomRowPick() {
        drawBottomRowCards();
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
    }

    private void handleTopBuildingsPick() {
        drawTopBuildingsCards();
        applyEffectToContainerCardViews(topCharactersContainer);
        drawSkipButton();
    }

    private void handleBottomBuildingsPick() {
        drawBottomBuildingCards();
        applyEffectToContainerCardViews(bottomCharactersContainer);
        drawSkipButton();
    }

    private void handleTotemMoved() {
        drawTurnOrderTile();
        drawOfferTrack();
    }

    private void handleRoundChanged() {
        drawDeck();
        drawTopRowCards();
        drawTopBuildingsCards();
        drawBottomRowCards();
        drawBottomBuildingCards();
    }

    private void handleActivePlayerChanged() {
        applyEffectToContainerCardViews(topCharactersContainer);
        applyEffectToContainerCardViews(buildingsContainer);
        applyEffectToContainerCardViews(bottomCharactersContainer);
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
    }
}