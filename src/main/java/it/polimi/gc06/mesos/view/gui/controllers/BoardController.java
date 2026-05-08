package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.gui.elements.*;
import it.polimi.gc06.mesos.view.gui.visitors.CardEffectVisitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.css.PseudoClass;

public class BoardController implements PropertyChangeListener {

    @FXML
    private BorderPane boardRoot;

    @FXML
    private HBox topContainer;

    @FXML
    private ScrollPane topRow;

    @FXML
    private HBox topRowContainer;

    @FXML
    private ScrollPane topBuildings;

    @FXML
    private HBox topBuildingsContainer;

    @FXML
    private HBox centerContainer;

    @FXML
    private HBox deckBox;

    @FXML
    public ImageView deckImage;

    @FXML
    public ScrollPane tiles;

    @FXML
    public HBox tilesContainer;

    @FXML
    private HBox skipButtonBox;

    @FXML
    public Button skipButton;

    @FXML
    private HBox bottomContainer;

    @FXML
    private ScrollPane bottomRow;

    @FXML
    private HBox bottomRowContainer;

    @FXML
    private ScrollPane bottomBuildings;

    @FXML
    private HBox bottomBuildingsContainer;


    private static final PseudoClass DISABLED_STYLE = PseudoClass.getPseudoClass("skip-disabled");
    private final Font mesosFont = Font.loadFont(this.getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 20);

    private static final int DEFAULT_SPACING = 5;

    private static final double CARD_ROWS_HEIGHT_PERCENTAGE = 0.3;
    private static final double CENTER_ZONE_HEIGHT_PERCENTAGE = 1 - (CARD_ROWS_HEIGHT_PERCENTAGE * 2);

    private static final double TRIBE_CARDS_WIDTH_PERCENTAGE = 0.7;
    private static final double BUILDING_CARDS_WIDTH_PERCENTAGE = 1 - TRIBE_CARDS_WIDTH_PERCENTAGE;

    private static final double TILES_BOX_WIDTH_PERCENTAGE = 0.7;
    private static final double DECK_BOX_WIDTH_PERCENTAGE = (1 - TILES_BOX_WIDTH_PERCENTAGE) / 2;
    private static final double SKIP_BUTTON_BOX_WIDTH_PERCENTAGE = (1 - TILES_BOX_WIDTH_PERCENTAGE) / 2;

    @FXML
    public void initialize() {
        topContainerInit();
        centerContainerInit();
        bottomContainerInit();

        deckInit();
        skipButtonBoxInit();
        toggleSkipButton(false);

        deckImage.setImage(new Image("tribe_card_era_I_back.png"));

        for (int i = 0; i < 9; i++) {
            CardView card = new CardView(new Image("shaman_1_card.png"));
            card.fitHeightProperty().bind(topRow.prefHeightProperty().multiply(0.98));
            EffectsManager.activeCard(card);
            topRowContainer.getChildren().add(card);
        }

        for (int i = 0; i < 7; i++) {
            CardView card = new CardView(new Image("shaman_1_card.png"));
            card.fitHeightProperty().bind(bottomRow.prefHeightProperty().multiply(0.98));
            EffectsManager.disableCard(card);
            bottomRowContainer.getChildren().add(card);
        }

        for (int i = 0; i < 4; i++) {
            CardView building = new CardView(new Image("1.png"));
            building.fitHeightProperty().bind(topBuildings.prefHeightProperty().multiply(0.98));
            topBuildingsContainer.getChildren().add(building);
        }

        for (int i = 0; i < 4; i++) {
            CardView building = new CardView(new Image("1.png"));
            building.fitHeightProperty().bind(bottomBuildings.prefHeightProperty().multiply(0.98));
            bottomBuildingsContainer.getChildren().add(building);
        }

        TurnOrderTileView turnOrderTile = createTurnOrderTile(
                new Image("turn_order_tile_5p.png"),
                new ArrayList<>(
                        Arrays.asList(
                                new TotemPieceView(Totem.YELLOW, "Player 1"),
                                new TotemPieceView(Totem.TURQUOISE, "Player 2"),
                                new TotemPieceView(Totem.PURPLE, "Player 3"),
                                new TotemPieceView(Totem.WHITE, "Player 4"),
                                new TotemPieceView(Totem.ORANGE, "Player 5")
                        )
                )
        );
        tilesContainer.getChildren().add(turnOrderTile);

        //OK
        OffertTileView tileA = createOfferTile(new Image("offer_tile_A.png"), new TotemPieceView(Totem.YELLOW, "Player 1"));
        tileA.setCenterPercentage(0.53, 0.2);
        tilesContainer.getChildren().add(tileA);


        OffertTileView tileB = createOfferTile(new Image("offer_tile_B.png"));
        tileB.setCenterPercentage(0.497, 0.2);
        tilesContainer.getChildren().add(tileB);

        //OK
        OffertTileView tileC = createOfferTile(new Image("offer_tile_C.png"), new TotemPieceView(Totem.ORANGE, "Player 2"));
        tileC.setCenterPercentage(0.465, 0.2);
        tilesContainer.getChildren().add(tileC);

        //OK
        OffertTileView tileD = createOfferTile(new Image("offer_tile_D.png"), new TotemPieceView(Totem.TURQUOISE, "Player 3"));
        tileD.setCenterPercentage(0.5, 0.2);
        tilesContainer.getChildren().add(tileD);

        //OK
        OffertTileView tileE = createOfferTile(new Image("offer_tile_E.png"), new TotemPieceView(Totem.PURPLE, "Player 4"));
        tileE.setCenterPercentage(0.527, 0.2);
        tilesContainer.getChildren().add(tileE);

        //OK
        OffertTileView tileF = createOfferTile(new Image("offer_tile_F.png"));
        tileF.setCenterPercentage(0.53, 0.2);
        tilesContainer.getChildren().add(tileF);

        //OK
        OffertTileView tileG = createOfferTile(new Image("offer_tile_G.png"), new TotemPieceView(Totem.WHITE, "Player 5"));
        tileG.setCenterPercentage(0.48, 0.2);
        tilesContainer.getChildren().add(tileG);
    }

    private void topContainerInit() {
        topContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        topContainer.maxHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));

        DoubleBinding topWidth = topContainer.widthProperty().subtract(DEFAULT_SPACING);

        topRow.prefWidthProperty().bind(topWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        topRow.prefHeightProperty().bind(topContainer.heightProperty());
        topRow.maxHeightProperty().bind(topContainer.heightProperty());

        topBuildings.prefWidthProperty().bind(topWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        topBuildings.prefHeightProperty().bind(topContainer.heightProperty());
        topBuildings.maxHeightProperty().bind(topContainer.heightProperty());
    }

    private void centerContainerInit() {
        centerContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CENTER_ZONE_HEIGHT_PERCENTAGE));
        centerContainer.maxHeightProperty().bind(boardRoot.heightProperty().multiply(CENTER_ZONE_HEIGHT_PERCENTAGE));

        DoubleBinding centerWidth = centerContainer.widthProperty().subtract(DEFAULT_SPACING);

        deckBox.prefWidthProperty().bind(centerWidth.multiply(DECK_BOX_WIDTH_PERCENTAGE));
        deckBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());
        deckBox.maxHeightProperty().bind(centerContainer.prefHeightProperty());

        tiles.prefWidthProperty().bind(centerWidth.multiply(TILES_BOX_WIDTH_PERCENTAGE));
        tiles.prefHeightProperty().bind(centerContainer.prefHeightProperty());
        tiles.maxHeightProperty().bind(centerContainer.prefHeightProperty());

        tilesContainer.prefWidthProperty().bind(tiles.widthProperty());
        tilesContainer.prefHeightProperty().bind(tiles.heightProperty());
        tilesContainer.maxHeightProperty().bind(tiles.heightProperty());

        skipButtonBox.prefWidthProperty().bind(centerWidth.multiply(SKIP_BUTTON_BOX_WIDTH_PERCENTAGE));
        skipButtonBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());
        skipButtonBox.maxHeightProperty().bind(centerContainer.prefHeightProperty());
    }

    private void bottomContainerInit() {
        bottomContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        bottomContainer.maxHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));

        DoubleBinding bottomWidth = bottomContainer.widthProperty().subtract(DEFAULT_SPACING);

        bottomRow.prefWidthProperty().bind(bottomWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        bottomRow.prefHeightProperty().bind(bottomContainer.heightProperty());
        bottomRow.maxHeightProperty().bind(bottomContainer.heightProperty());

        bottomBuildings.prefWidthProperty().bind(bottomWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        bottomBuildings.prefHeightProperty().bind(bottomContainer.heightProperty());
        bottomBuildings.maxHeightProperty().bind(bottomContainer.heightProperty());
    }

    private CardView createCard(Image image, HBox container) {
        CardView card = new CardView(image);
        return card;
    }

    private TurnOrderTileView createTurnOrderTile(Image image) {
        int nPlayers;
        if (smallModel == null) {
            nPlayers = 5;
        } else {
            nPlayers = smallModel.getOpponents().size() + 1;
        }

        TurnOrderTileView turnOrderTile = new TurnOrderTileView(image, nPlayers);

        int totalTilesOnScreen = 8;
        DoubleBinding tileSize = tiles.widthProperty().divide(totalTilesOnScreen).multiply(0.95);

        turnOrderTile.prefWidthProperty().bind(tileSize);
        turnOrderTile.maxWidthProperty().bind(tileSize);
        turnOrderTile.prefHeightProperty().bind(tiles.heightProperty());
        turnOrderTile.maxHeightProperty().bind(tiles.heightProperty());

        turnOrderTile.getImageView().fitWidthProperty().bind(tileSize);
        turnOrderTile.getImageView().fitHeightProperty().bind(tiles.heightProperty());

        return turnOrderTile;
    }

    private TurnOrderTileView createTurnOrderTile(Image image, ArrayList<TotemPieceView> totems) {
        TurnOrderTileView turnOrderTile = createTurnOrderTile(image);

        ArrayList<TotemPieceView> totemPieces = new ArrayList<>(totems);

        turnOrderTile.setTotemPieces(totemPieces);
        return turnOrderTile;
    }

    private OffertTileView createOfferTile(Image image) {
        OffertTileView tile = new OffertTileView(image);

        int totalTilesOnScreen = 8;
        DoubleBinding tileSize = tiles.widthProperty().divide(totalTilesOnScreen).multiply(0.95);

        tile.prefWidthProperty().bind(tileSize);
        tile.maxWidthProperty().bind(tileSize);
        tile.prefHeightProperty().bind(tiles.heightProperty());
        tile.maxHeightProperty().bind(tiles.heightProperty());

        tile.getImageView().fitWidthProperty().bind(tileSize);
        tile.getImageView().fitHeightProperty().bind(tiles.heightProperty());

        return tile;
    }

    private OffertTileView createOfferTile(Image image, TotemPieceView totemPieceView) {
        OffertTileView tile = createOfferTile(image);
        tile.setTotem(totemPieceView);
        return tile;
    }

    private void drawTurnOrderTile() {
//        TurnOrderTileView turnOrderTile = createTurnOrderTile(new Image(imageFetcher.fetch(smallModel.getTurnOrderTile())));
//        tilesContainer.getChildren().add(turnOrderTile);
//        // TODO : give the player a Totem or retrive it somewhere
//        ((TurnOrderTileView) tilesContainer.getChildren().get(0)).setTotemPieces(
//                new ArrayList<>(
//                        Arrays.asList(
//                                new TotemPieceView(Totem.YELLOW),
//                                new TotemPieceView(Totem.TURQUOISE),
//                                new TotemPieceView(Totem.PURPLE),
//                                new TotemPieceView(Totem.WHITE),
//                                new TotemPieceView(Totem.ORANGE)
//                        )
//                )
//        );
    }

    private void drawOfferTrack() {
        for (TileSlot tile : smallModel.getOfferTrack()) {
            // TODO : give the player a Totem or retrive it somewhere
            tilesContainer.getChildren().add(createOfferTile(new Image(imageFetcher.fetch(tile)), new TotemPieceView(Totem.TURQUOISE, tile.getPlayer().getNickname())));
        }
    }

    private void setCardEffect(CardView cardView) {
        CardEffectVisitor visitor = new CardEffectVisitor(cardView);
        cardView.getCard().accept(visitor);
    }

    private void applyEffectToContainerCardViews(HBox container) {
        for (Node node : container.getChildren()) {
            setCardEffect((CardView) node);
        }
    }

    private void deckInit() {
        deckImage.fitHeightProperty().bind(deckBox.prefHeightProperty().multiply(0.8));
        deckImage.setPreserveRatio(true);
        setupDeckPopup();
    }

    private void setupDeckPopup() {
        Popup nCards = createDeckPopup();

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

    private Popup createDeckPopup() {
        Popup popup = new Popup();

        HBox popupContent = new HBox();
        popupContent.setStyle(
                "-fx-background-color:rgba(255,255,255,0.4);" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.6);" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-padding: 8px 15px;"
        );
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMouseTransparent(true);

        Text nCardsText = new Text();
        nCardsText.setFont(Font.loadFont(this.getClass().getResourceAsStream("/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"), 20));
        nCardsText.setFill(Color.WHITE);
        nCardsText.setText(smallModel == null ? "N/A" : "" + smallModel.getTribeDeckSize());

        popupContent.getChildren().add(nCardsText);
        popup.getContent().add(popupContent);

        return popup;
    }

    private void toggleSkipButton(boolean canSkip) {
        skipButton.pseudoClassStateChanged(DISABLED_STYLE, !canSkip);
        skipButton.applyCss();
        skipButton.setCursor(canSkip ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void skipButtonBoxInit() {
        DoubleBinding btnWidth = skipButtonBox.prefWidthProperty().multiply(0.4);
        DoubleBinding btnHeight = skipButtonBox.prefHeightProperty().multiply(0.15);

        skipButton.prefWidthProperty().bind(btnWidth);
        skipButton.maxWidthProperty().bind(btnWidth);
        skipButton.prefHeightProperty().bind(btnHeight);
        skipButton.maxHeightProperty().bind(btnHeight);
        skipButton.setFont(mesosFont);
    }

    private void drawSkipButton() {
        if (!smallModel.isActive() || !smallModel.isCanSkip()) {
            skipButton.setDisable(true);
            toggleSkipButton(false);
        } else {
            skipButton.setDisable(false);
            toggleSkipButton(true);
        }
    }

    private void drawDeck() {
        switch (smallModel.getEra()) {
            case ERA_I:
                deckImage.setImage(new Image("tribe_card_era_I_back.png"));
                break;
            case ERA_II:
                deckImage.setImage(new Image("tribe_card_era_II_back.png"));
                break;
            case ERA_III:
                deckImage.setImage(new Image("tribe_card_era_III_back.png"));
                break;
        }
    }

    private void drawTopRowCards() {
        topRowContainer.getChildren().clear();
        for (Card card : smallModel.getTopRow()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(topRow.prefHeightProperty().multiply(0.98));
            setCardEffect(cardView);
            cardView.setCard(card);
            topRowContainer.getChildren().add(cardView);
        }
    }

    private void drawTopBuildingsCards() {
        topBuildingsContainer.getChildren().clear();
        for (Card building : smallModel.getTopBuilding()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(topBuildings.prefHeightProperty().multiply(0.98));
            setCardEffect(cardView);
            cardView.setCard(building);
            topBuildingsContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomRowCards() {
        bottomRowContainer.getChildren().clear();
        for (Card card : smallModel.getBottomRow()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(card)));
            cardView.fitHeightProperty().bind(bottomRow.prefHeightProperty().multiply(0.98));
            setCardEffect(cardView);
            cardView.setCard(card);
            bottomRowContainer.getChildren().add(cardView);
        }
    }

    private void drawBottomBuildingCards() {
        bottomBuildingsContainer.getChildren().clear();
        for (Card building : smallModel.getBottomBuilding()) {
            CardView cardView = new CardView(new Image(imageFetcher.fetch(building)));
            cardView.fitHeightProperty().bind(bottomBuildings.prefHeightProperty().multiply(0.98));
            setCardEffect(cardView);
            cardView.setCard(building);
            bottomBuildingsContainer.getChildren().add(cardView);
        }
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
        applyEffectToContainerCardViews(topBuildingsContainer);
        drawSkipButton();
    }

    private void handleBottomRowPick() {
        drawBottomRowCards();
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
    }

    private void handleTopBuildingsPick() {
        drawTopBuildingsCards();
        applyEffectToContainerCardViews(topRowContainer);
        drawSkipButton();
    }

    private void handleBottomBuildingsPick() {
        drawBottomBuildingCards();
        applyEffectToContainerCardViews(bottomRowContainer);
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
        applyEffectToContainerCardViews(topRowContainer);
        applyEffectToContainerCardViews(topBuildingsContainer);
        applyEffectToContainerCardViews(bottomRowContainer);
        applyEffectToContainerCardViews(bottomBuildingsContainer);
        drawSkipButton();
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
                case TOP_ROW_REFILL:
                    handleTopRowRefill();
                    break;
                case TOP_BUILDINGS_REFILL:
                    handleTopBuildingsRefill();
                    break;
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
                case TOTEM_MOVED:
                    handleTotemMoved();
                    break;
                case PHASE_CHANGED:
                    break;
                case ERA_CHANGED:
                    break;
                case ROUND_CHANGED:
                    handleRoundChanged();
                    break;
                case IS_END_GAME:
                    break;
                case ACTIVE_PLAYER_CHANGED:
                    handleActivePlayerChanged();
                    break;
                default:
                    break;
            }
        });
    }
}