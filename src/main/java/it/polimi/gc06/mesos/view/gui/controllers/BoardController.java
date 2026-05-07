package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
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

    private static final int DEFAULT_SPACING = 5;

    private static final double CARD_ROWS_HEIGHT_PERCENTAGE = 0.3;
    private static final double CENTER_ZONE_HEIGHT_PERCENTAGE =
            1 - (CARD_ROWS_HEIGHT_PERCENTAGE * 2);

    private static final double TRIBE_CARDS_WIDTH_PERCENTAGE = 0.7;
    private static final double BUILDING_CARDS_WIDTH_PERCENTAGE =
            1 - TRIBE_CARDS_WIDTH_PERCENTAGE;

    private static final double TILES_BOX_WIDTH_PERCENTAGE = 0.7;
    private static final double DECK_BOX_WIDTH_PERCENTAGE =
            (1 - TILES_BOX_WIDTH_PERCENTAGE) / 2;
    private static final double SKIP_BUTTON_BOX_WIDTH_PERCENTAGE =
            (1 - TILES_BOX_WIDTH_PERCENTAGE) / 2;

    @FXML
    public void initialize() {
        topContainerInit();
        centerContainerInit();
        bottomContainerInit();

        deckInit();
        skipButtonBoxInit();

        deckImage.setImage(new Image("tribe_card_era_I_back.png"));

        for (int i = 0; i < 9; i++) {
            topRowContainer
                    .getChildren()
                    .add(createCard(new Image("shaman_1_card.png")));
        }

        for (int i = 0; i < 7; i++) {
            bottomRowContainer
                    .getChildren()
                    .add(createCard(new Image("shaman_1_card.png")));
        }
        for (int i = 0; i < 4; i++) {
            topBuildingsContainer
                    .getChildren()
                    .add(createCard(new Image("1.png")));
        }
        for (int i = 0; i < 4; i++) {
            bottomBuildingsContainer
                    .getChildren()
                    .add(createCard(new Image("1.png")));
        }

        TurnOrderTileView turnOrderTile = createTurnOrderTile(
                new Image("turn_order_tile_5p.png"),
                new ArrayList<>(
                        Arrays.asList(
                                Totem.YELLOW,
                                Totem.TURQUOISE,
                                Totem.PURPLE,
                                Totem.WHITE,
                                Totem.ORANGE
                        )
                )
        );
        tilesContainer.getChildren().add(turnOrderTile);

        tilesContainer
                .getChildren()
                .add(
                        createOfferTile(
                                new Image("offer_tile_A.png"),
                                Totem.YELLOW,
                                "Player 1"
                        )
                );
        tilesContainer
                .getChildren()
                .add(
                        createOfferTile(
                                new Image("offer_tile_B.png"),
                                Totem.ORANGE,
                                "Player 2"
                        )
                );
        tilesContainer
                .getChildren()
                .add(createOfferTile(new Image("offer_tile_C.png")));
        tilesContainer
                .getChildren()
                .add(
                        createOfferTile(
                                new Image("offer_tile_D.png"),
                                Totem.TURQUOISE,
                                "Player 3"
                        )
                );
        tilesContainer
                .getChildren()
                .add(
                        createOfferTile(
                                new Image("offer_tile_E.png"),
                                Totem.PURPLE,
                                "Player 4"
                        )
                );
        tilesContainer
                .getChildren()
                .add(createOfferTile(new Image("offer_tile_F.png")));
        tilesContainer
                .getChildren()
                .add(
                        createOfferTile(
                                new Image("offer_tile_G.png"),
                                Totem.WHITE,
                                "Player 5"
                        )
                );
    }

    private void topContainerInit() {
        topContainer
                .prefHeightProperty()
                .bind(
                        boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE)
                );
        DoubleBinding topWidth = topContainer
                .widthProperty()
                .subtract(DEFAULT_SPACING);

        topRow
                .prefWidthProperty()
                .bind(topWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        topRow.prefHeightProperty().bind(topContainer.heightProperty());

        topBuildings
                .prefWidthProperty()
                .bind(topWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        topBuildings.prefHeightProperty().bind(topContainer.heightProperty());
    }

    private void centerContainerInit() {
        centerContainer
                .prefHeightProperty()
                .bind(
                        boardRoot
                                .heightProperty()
                                .multiply(CENTER_ZONE_HEIGHT_PERCENTAGE)
                );

        DoubleBinding centerWidth = centerContainer
                .widthProperty()
                .subtract(DEFAULT_SPACING);

        deckBox
                .prefWidthProperty()
                .bind(centerWidth.multiply(DECK_BOX_WIDTH_PERCENTAGE));
        deckBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());

        tiles
                .prefWidthProperty()
                .bind(centerWidth.multiply(TILES_BOX_WIDTH_PERCENTAGE));
        tiles.prefHeightProperty().bind(centerContainer.prefHeightProperty());
        tilesContainer.prefWidthProperty().bind(tiles.widthProperty());
        tilesContainer.prefHeightProperty().bind(tiles.heightProperty());

        skipButtonBox
                .prefWidthProperty()
                .bind(centerWidth.multiply(SKIP_BUTTON_BOX_WIDTH_PERCENTAGE));
        skipButtonBox
                .prefHeightProperty()
                .bind(centerContainer.prefHeightProperty());
    }

    private void bottomContainerInit() {
        bottomContainer
                .prefHeightProperty()
                .bind(
                        boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE)
                );
        DoubleBinding bottomWidth = bottomContainer
                .widthProperty()
                .subtract(DEFAULT_SPACING);

        bottomRow
                .prefWidthProperty()
                .bind(bottomWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        bottomRow.prefHeightProperty().bind(bottomContainer.heightProperty());

        bottomBuildings
                .prefWidthProperty()
                .bind(bottomWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        bottomBuildings
                .prefHeightProperty()
                .bind(bottomContainer.heightProperty());
    }

    /**
     * Draws a card based on the given image and binds its height to the top row height with a percentage
     * to ensure that it fits well in the container.
     *
     * @param image The image of the card.
     * @return the {@link CardView} object.
     */
    private CardView createCard(Image image) {
        CardView card = new CardView(image);
        card.fitHeightProperty().bind(topRow.heightProperty().multiply(0.98));

        return card;
    }

    private TurnOrderTileView createTurnOrderTile(Image image) {
        int nPlayers;
        if (smallModel == null) {
            nPlayers = 8;
        } else {
            nPlayers = smallModel.getOpponents().size() + 1;
        }

        TurnOrderTileView turnOrderTile = new TurnOrderTileView(image);

        DoubleBinding tileSize = tiles
                .widthProperty()
                .divide(nPlayers)
                .multiply(0.98);

        turnOrderTile.prefWidthProperty().bind(tileSize);
        turnOrderTile.maxWidthProperty().bind(tileSize);
        turnOrderTile.prefHeightProperty().bind(tiles.heightProperty());
        turnOrderTile.maxHeightProperty().bind(tiles.heightProperty());
        turnOrderTile.getImageView().fitWidthProperty().bind(tileSize);
        turnOrderTile
                .getImageView()
                .fitHeightProperty()
                .bind(tiles.heightProperty());

        return turnOrderTile;
    }

    private TurnOrderTileView createTurnOrderTile(
            Image image,
            ArrayList<Totem> totems
    ) {
        TurnOrderTileView turnOrderTile = createTurnOrderTile(image);

        ArrayList<TotemPieceView> totemPieces = new ArrayList<>();
        for (Totem totem : totems) {
            totemPieces.add(new TotemPieceView(totem));
        }

        turnOrderTile.setTotemPieces(totemPieces);

        return turnOrderTile;
    }

    private OffertTileView createOfferTile(Image image) {
        int nPlayers;
        if (smallModel == null) {
            nPlayers = 8;
        } else {
            nPlayers = smallModel.getOpponents().size() + 1;
        }

        OffertTileView tile = new OffertTileView(image);
        DoubleBinding tileSize = tiles
                .widthProperty()
                .divide(nPlayers)
                .multiply(0.98);

        tile.prefWidthProperty().bind(tileSize);
        tile.maxWidthProperty().bind(tileSize);
        tile.prefHeightProperty().bind(tiles.heightProperty());
        tile.maxHeightProperty().bind(tiles.heightProperty());
        tile.getImageView().fitWidthProperty().bind(tileSize);
        tile.getImageView().fitHeightProperty().bind(tiles.heightProperty());

        //tile.getTotemPiece().fitWidthProperty().bind(tileSize);
        //tile.getTotemPiece().fitHeightProperty().bind(tiles.heightProperty());

        tile.getTotemOverlay().fitWidthProperty().bind(tileSize);
        tile.getTotemOverlay().fitHeightProperty().bind(tiles.heightProperty());

        return tile;
    }

    /**
     * Create the {@link OffertTileView} based on the given image and totem.
     *
     * @param image Image of the tile
     * @param totem The {@link Totem} associated to the player on the tile
     * @return the {@link OffertTileView} object.
     */
    private OffertTileView createOfferTile(Image image, Totem totem) {
        OffertTileView tile = createOfferTile(image);

        tile.setTotem(totem);

        return tile;
    }

    /**
     * Create the {@link OffertTileView} based on the given image, totem and player name.
     *
     * @param image      Image of the tile
     * @param totem      The {@link Totem} associated to the player on the tile
     * @param playerName The name of the player on the tile
     * @return the {@link OffertTileView} object.
     */
    private OffertTileView createOfferTile(
            Image image,
            Totem totem,
            String playerName
    ) {
        OffertTileView tile = createOfferTile(image, totem);

        tile.setPlayerName(playerName);

        return tile;
    }

    private void drawTurnOrderTile() {
        // TODO : the tile view is not implemented yet
        // ...
    }

    private void drawOfferTrack() {
        // TODO : the tile view is not implemented yet
        // ...
    }

    /**
     * sets the card effect based on player state and card type.
     *
     * @param cardView {@link CardView} on which the effects get applied.
     */
    private void setCardEffect(CardView cardView) {
        CardEffectVisitor visitor = new CardEffectVisitor(cardView);
        cardView.getCard().accept(visitor);
    }

    /**
     * Init the deck image and bind its height to the deck box height with a percentage to ensure that it fits well in
     * the container.
     */
    private void deckInit() {
        // drawDeck();
        deckImage
                .fitHeightProperty()
                .bind(deckBox.prefHeightProperty().multiply(0.8));
        deckImage.setPreserveRatio(true);

        setupDeckPopup();
    }

    private void setupDeckPopup() {
        Popup nCards = createDeckPopup();

        deckImage.setOnMouseEntered(
                (event -> {
                    nCards.show(deckImage, event.getScreenX(), event.getScreenY());
                    EffectsManager.playPopupIn(nCards);
                })
        );

        deckImage.setOnMouseMoved(
                (event -> {
                    nCards.setX(event.getScreenX() - 35);
                    nCards.setY(event.getScreenY() - 60);
                })
        );

        deckImage.setOnMouseExited(
                (event -> {
                    nCards.hide();
                })
        );
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
        nCardsText.setFont(
                Font.loadFont(
                        this.getClass().getResourceAsStream(
                                "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
                        ),
                        20
                )
        );
        nCardsText.setFill(Color.WHITE);
        nCardsText.setText(
                smallModel == null ? "N/A" : "" + smallModel.getTribeDeckSize()
        );

        popupContent.getChildren().add(nCardsText);
        popup.getContent().add(popupContent);

        return popup;
    }

    /**
     * Init the skip button and bind its dimension to its container.
     */
    private void skipButtonBoxInit() {
        // drawSkipButton();
        skipButton
                .prefWidthProperty()
                .bind(skipButtonBox.prefHeightProperty().multiply(0.3));
        skipButton
                .prefHeightProperty()
                .bind(skipButtonBox.prefWidthProperty().multiply(0.3));
    }

    /**
     * Draws the skip button based on the player possibility to skip and player state.
     */
    private void drawSkipButton() {
        if (!smallModel.isActive() || !smallModel.isCanSkip()) {
            skipButton.setDisable(true);
        } else {
            skipButton.setDisable(false);
        }
    }

    /**
     * Draws the deck based on the current era.
     */
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

    /**
     * Draws the top row cards and sets the effect based on player and card status.
     */
    private void drawTopRowCards() {
        // TODO : obviously not the final implementation (we can't re-draw everything every time)
        topRowContainer.getChildren().clear();
        for (Card card : smallModel.getTopRow()) {
            CardView cardView = createCard(new Image(imageFetcher.fetch(card)));
            setCardEffect(cardView);
            cardView.setCard(card);
            topRowContainer.getChildren().add(cardView);
        }
    }

    /**
     * Draws the top buildings cards and sets the effect based on player and card status.
     */
    private void drawTopBuildingsCards() {
        // TODO : obviously not the final implementation (we can't re-draw everything every time)
        topBuildingsContainer.getChildren().clear();
        for (Card building : smallModel.getTopBuilding()) {
            CardView cardView = createCard(
                    new Image(imageFetcher.fetch(building))
            );
            setCardEffect(cardView);
            cardView.setCard(building);
            topBuildingsContainer.getChildren().add(cardView);
        }
    }

    /**
     * Draws the bottom row cards and sets the effect based on player and card status.
     */
    private void drawBottomRowCards() {
        // TODO : obviously not the final implementation (we can't re-draw everything every time)
        bottomRowContainer.getChildren().clear();
        for (Card card : smallModel.getBottomRow()) {
            CardView cardView = createCard(new Image(imageFetcher.fetch(card)));
            setCardEffect(cardView);
            cardView.setCard(card);
            bottomRowContainer.getChildren().add(cardView);
        }
    }

    /**
     * Draws the bottom buildings cards and sets the effect based on player and card status.
     */
    private void drawBottomBuildingCards() {
        // TODO : obviously not the final implementation (we can't re-draw everything every time)
        bottomBuildingsContainer.getChildren().clear();
        for (Card building : smallModel.getBottomBuilding()) {
            CardView cardView = createCard(
                    new Image(imageFetcher.fetch(building))
            );
            setCardEffect(cardView);
            cardView.setCard(building);
            bottomBuildingsContainer.getChildren().add(cardView);
        }
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
                    drawTopRowCards();
                    drawDeck();
                    break;
                case TOP_BUILDINGS_REFILL:
                    drawTopBuildingsCards();
                    drawBottomRowCards();
                    break;
                case PICK_FROM_TOP_ROW:
                    drawTopRowCards();
                    drawSkipButton();
                    break;
                case PICK_FROM_BOTTOM_ROW:
                    drawBottomRowCards();
                    drawSkipButton();
                    break;
                case PICK_FROM_TOP_BUILDINGS:
                    drawTopBuildingsCards();
                    break;
                case PICK_FROM_BOTTOM_BUILDINGS:
                    drawBottomBuildingCards();
                    break;
                case TOTEM_MOVED:
                    drawOfferTrack();
                    drawTurnOrderTile();
                    break;
                case PHASE_CHANGED:
                    break;
                case ERA_CHANGED:
                    break;
                case ROUND_CHANGED:
                    break;
                case IS_END_GAME:
                    break;
                case ACTIVE_PLAYER_CHANGED:
                    break;
                default:
                    break;
            }
        });
    }
}
