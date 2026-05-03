package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.SmallModel;
import it.polimi.gc06.mesos.view.gui.elements.CardView;
import it.polimi.gc06.mesos.view.gui.elements.TileView;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private SmallModel smallModel;

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

        deckImage.setImage(new Image("tribe_card_era_I_back.png"));

        for (int i = 0; i < 9; i++) {
            topRowContainer.getChildren().add(drawCard(new Image("shaman_1_card.png")));
        }

        for (int i = 0; i < 7; i++) {
            bottomRowContainer.getChildren().add(drawCard(new Image("shaman_1_card.png")));
        }
        for (int i = 0; i < 4; i++) {
            topBuildingsContainer.getChildren().add(drawCard(new Image("1.png")));
        }
        for (int i = 0; i < 4; i++) {
            bottomBuildingsContainer.getChildren().add(drawCard(new Image("1.png")));
        }

        int n = 8;

        tilesContainer.getChildren().add(drawTile(new Image("turn_order_tile_5p.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_A.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_B.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_C.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_D.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_E.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_F.png"), n));
        tilesContainer.getChildren().add(drawTile(new Image("offer_tile_G.png"), n));
    }


    private void topContainerInit() {
        topContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        DoubleBinding topWidth = topContainer.widthProperty().subtract(DEFAULT_SPACING);

        topRow.prefWidthProperty().bind(topWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        topRow.prefHeightProperty().bind(topContainer.heightProperty());

        topBuildings.prefWidthProperty().bind(topWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        topBuildings.prefHeightProperty().bind(topContainer.heightProperty());
    }

    private void centerContainerInit() {
        centerContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CENTER_ZONE_HEIGHT_PERCENTAGE));

        DoubleBinding centerWidth = centerContainer.widthProperty().subtract(DEFAULT_SPACING);

        deckBox.prefWidthProperty().bind(centerWidth.multiply(DECK_BOX_WIDTH_PERCENTAGE));
        deckBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());

        tiles.prefWidthProperty().bind(centerWidth.multiply(TILES_BOX_WIDTH_PERCENTAGE));
        tiles.prefHeightProperty().bind(centerContainer.prefHeightProperty());
        tilesContainer.prefWidthProperty().bind(tiles.widthProperty());
        tilesContainer.prefHeightProperty().bind(tiles.heightProperty());

        skipButtonBox.prefWidthProperty().bind(centerWidth.multiply(SKIP_BUTTON_BOX_WIDTH_PERCENTAGE));
        skipButtonBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());
    }

    private void bottomContainerInit() {
        bottomContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        DoubleBinding bottomWidth = bottomContainer.widthProperty().subtract(DEFAULT_SPACING);

        bottomRow.prefWidthProperty().bind(bottomWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        bottomRow.prefHeightProperty().bind(bottomContainer.heightProperty());

        bottomBuildings.prefWidthProperty().bind(bottomWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        bottomBuildings.prefHeightProperty().bind(bottomContainer.heightProperty());
    }

    /**
     * Draws a card based on the given image and binds its height to the top row height with a percentage
     * to ensure that it fits well in the container.
     *
     * @param image The image of the card.
     * @return the {@link CardView} object.
     */
    private CardView drawCard(Image image) {
        CardView card = new CardView(image);
        card.fitHeightProperty().bind(topRow.heightProperty().multiply(0.98));

        return card;
    }

    private TileView drawTile(Image image, int numTiles) {
        // TODO : this is provisory, the number of tiles should be base on the number of players (small model)
        TileView tile = new TileView(image);

        DoubleBinding tileSize = tiles.widthProperty().divide(numTiles).multiply(0.98);

        tile.prefWidthProperty().bind(tileSize);
        tile.maxWidthProperty().bind(tileSize);
        tile.prefHeightProperty().bind(tiles.heightProperty());
        tile.maxHeightProperty().bind(tiles.heightProperty());
        tile.getImageView().fitWidthProperty().bind(tileSize);
        tile.getImageView().fitHeightProperty().bind(tiles.heightProperty());

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

    private StackPane drawOfferTrackTile(Image image, TileSlot tileSlot) {
        return null;
    }

    /**
     * Sets the disabled effect on the card, which consist in adjust the color to a gray scale which suggest that the
     * player cant performa an action on it.
     *
     * @param card Card on which the effect get applied.
     */
    private void disableCard(CardView card) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0);
        colorAdjust.setSaturation(-1);
        colorAdjust.setBrightness(0);
        colorAdjust.setContrast(0);
        card.setEffect(colorAdjust);
        card.setDisable(true);
    }

    /**
     * Sets the active effect on the card, which consist in a bluish shadow which suggest that an action can be performed.
     *
     * @param card Card on which the effect get applied.
     */
    private void activeCard(CardView card) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(1);
        dropShadow.setOffsetY(1);
        dropShadow.setSpread(0.5);
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(Color.web("#348ceb", 0.2));
        card.setEffect(dropShadow);
        card.setStyle("-fx-cursor: hand");
    }

    /**
     * sets the card effect based on player state and card type.
     *
     * @param card Card on which the effects get applied.
     */
    private void setCardEffect(CardView card) {
        // TODO : this should be based on the card if its an event or not

        if (!smallModel.isActive()) {
            disableCard(card);
        } else {
            // TODO : should be based on event or character/building
            // ...
        }
    }

    /**
     * Init the deck image and bind its height to the deck box height with a percentage to ensure that it fits well in
     * the container.
     */
    private void deckInit() {
        // drawDeck();
        deckImage.fitHeightProperty().bind(deckBox.prefHeightProperty().multiply(0.8));
        deckImage.setPreserveRatio(true);
    }

    /**
     * Init the skip button and bind its dimension to its container.
     */
    private void skipButtonBoxInit() {
        // drawSkipButton();
        skipButton.prefWidthProperty().bind(skipButtonBox.prefHeightProperty().multiply(0.3));
        skipButton.prefHeightProperty().bind(skipButtonBox.prefWidthProperty().multiply(0.3));
        skipButton.setDisable(false);
    }

    /**
     * Draws the skip button based on the player possibility to skip and player state.
     */
    private void drawSkipButton() {
        // TODO : this should be based on the player possibility to skip

        if (!smallModel.isActive()) {
            skipButton.setDisable(true);
        }

        // ...
    }


    /**
     * Draws the deck based on the current era.
     */
    private void drawDeck() {
        switch (smallModel.era()) {
            case "ERA_I":
                deckImage.setImage(new Image("tribe_card_era_I_back.png"));
                break;
            case "ERA_II":
                deckImage.setImage(new Image("tribe_card_era_II_back.png"));
                break;
            case "ERA_III":
                deckImage.setImage(new Image("tribe_card_era_III_back.png"));
                break;
        }
    }

    /**
     * Draws the top row cards and sets the effect based on player and card status.
     */
    private void drawTopRowCards() {
        topRowContainer.getChildren().clear();
        for (String cardPath : smallModel.topRow()) {
            CardView card = drawCard(new Image(cardPath));

            setCardEffect(card);

            topRowContainer.getChildren().add(card);
        }
    }

    /**
     * Draws the top buildings cards and sets the effect based on player and card status.
     */
    private void drawTopBuildingsCards() {
        topBuildingsContainer.getChildren().clear();
        for (String cardPath : smallModel.topBuilding()) {
            CardView card = drawCard(new Image(cardPath));

            setCardEffect(card);

            topBuildingsContainer.getChildren().add(card);
        }
    }

    /**
     * Draws the bottom row cards and sets the effect based on player and card status.
     */
    private void drawBottomRowCards() {
        bottomRowContainer.getChildren().clear();
        for (String buildingPath : smallModel.bottomRow()) {
            CardView card = drawCard(new Image(buildingPath));

            setCardEffect(card);

            bottomRowContainer.getChildren().add(card);
        }
    }

    /**
     * Draws the bottom buildings cards and sets the effect based on player and card status.
     */
    private void drawBottomBuildingCards() {
        bottomBuildingsContainer.getChildren().clear();
        for (String buildingPath : smallModel.bottomBuilding()) {
            CardView card = drawCard(new Image(buildingPath));

            setCardEffect(card);

            bottomBuildingsContainer.getChildren().add(card);
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
