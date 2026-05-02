package it.polimi.gc06.mesos.view.viewControllers;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.view.GUI;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class BoardController {
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
    private HBox tilesBox;
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

    private static final int NUM_PLAYERS = 4;


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

        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));
        topRowContainer.getChildren().add(drawCard(new Image("offer_tile_A.png")));


        tilesBox.getChildren().add(drawTile(new Image("offer_tile_A.png")));
        tilesBox.getChildren().add(drawTile(new Image("offer_tile_A.png")));
        tilesBox.getChildren().add(drawTile(new Image("offer_tile_A.png")));
        tilesBox.getChildren().add(drawTile(new Image("offer_tile_A.png")));
        tilesBox.getChildren().add(drawTile(new Image("offer_tile_A.png")));

    }

    private void centerContainerInit() {
        centerContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CENTER_ZONE_HEIGHT_PERCENTAGE));

        DoubleBinding centerWidth = centerContainer.widthProperty().subtract(DEFAULT_SPACING);

        deckBox.prefWidthProperty().bind(centerWidth.multiply(DECK_BOX_WIDTH_PERCENTAGE));
        deckBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());

        tilesBox.prefWidthProperty().bind(centerWidth.multiply(TILES_BOX_WIDTH_PERCENTAGE));
        tilesBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());

        skipButtonBox.prefWidthProperty().bind(centerWidth.multiply(SKIP_BUTTON_BOX_WIDTH_PERCENTAGE));
        skipButtonBox.prefHeightProperty().bind(centerContainer.prefHeightProperty());
    }

    private void topContainerInit() {
        topContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        DoubleBinding topWidth = topContainer.widthProperty().subtract(DEFAULT_SPACING);

        topRow.prefWidthProperty().bind(topWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        topRow.prefHeightProperty().bind(topContainer.heightProperty());

        topBuildings.prefWidthProperty().bind(topWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        topBuildings.prefHeightProperty().bind(topContainer.heightProperty());
    }

    private void bottomContainerInit() {
        bottomContainer.prefHeightProperty().bind(boardRoot.heightProperty().multiply(CARD_ROWS_HEIGHT_PERCENTAGE));
        DoubleBinding bottomWidth = bottomContainer.widthProperty().subtract(DEFAULT_SPACING);

        bottomRow.prefWidthProperty().bind(bottomWidth.multiply(TRIBE_CARDS_WIDTH_PERCENTAGE));
        bottomRow.prefHeightProperty().bind(bottomContainer.heightProperty());

        bottomBuildings.prefWidthProperty().bind(bottomWidth.multiply(BUILDING_CARDS_WIDTH_PERCENTAGE));
        bottomBuildings.prefHeightProperty().bind(bottomContainer.heightProperty());
    }

//    private void topContainerInit() {
//        double topHeight = GUI.HEIGHT * CARD_ROWS_HEIGHT_PERCENTAGE;
//
//        topContainer.setPrefWidth(GUI.WIDTH);
//        topRow.setPrefWidth(GUI.WIDTH * TRIBE_CARDS_WIDTH_PERCENTAGE);
//        topBuildings.setPrefWidth(GUI.WIDTH * BUILDING_CARDS_WIDTH_PERCENTAGE);
//
//
//        topContainer.setPrefHeight(topHeight);
//        topRow.setPrefHeight(topHeight);
//        topBuildings.setPrefHeight(topHeight);
//    }

//    private void centerContainerInit() {
//        double centerHeight = GUI.HEIGHT * CENTER_ZONE_HEIGHT_PERCENTAGE;
//
//        centerContainer.setPrefWidth(GUI.WIDTH);
//        deckBox.setPrefWidth(GUI.WIDTH * DECK_BOX_WIDTH_PERCENTAGE);
//        tilesBox.setPrefWidth(GUI.WIDTH * TILES_BOX_WIDTH_PERCENTAGE);
//        skipButtonBox.setPrefWidth(GUI.WIDTH * SKIP_BUTTON_BOX_WIDTH_PERCENTAGE);
//
//        centerContainer.setPrefHeight(centerHeight);
//        deckBox.setPrefHeight(centerHeight);
//        tilesBox.setPrefHeight(centerHeight);
//        skipButtonBox.setPrefHeight(centerHeight);
//    }

//    private void bottomContainerInit() {
//        double bottomHeight = GUI.HEIGHT * CARD_ROWS_HEIGHT_PERCENTAGE;
//
//        bottomContainer.setPrefWidth(GUI.WIDTH);
//        bottomRow.setPrefWidth(GUI.WIDTH * TRIBE_CARDS_WIDTH_PERCENTAGE);
//        bottomBuildings.setPrefWidth(GUI.WIDTH * BUILDING_CARDS_WIDTH_PERCENTAGE);
//
//        bottomContainer.setPrefHeight(bottomHeight);
//        bottomRow.setPrefHeight(bottomHeight);
//        bottomBuildings.setPrefHeight(bottomHeight);
//
//    }

    private ImageView drawCard(Image image) {
        ImageView card = new ImageView(image);
        card.setPreserveRatio(true);
        card.fitHeightProperty().bind(topRow.heightProperty().multiply(0.9));
        card.setSmooth(true);

        return card;
    }

    private ImageView drawTile(Image image) {
        ImageView card = new ImageView(image);
        card.setPreserveRatio(true);
        card.fitHeightProperty().bind(tilesBox.heightProperty().multiply(0.9));
        card.setSmooth(true);

        return card;
    }

    private StackPane drawTurnOrderTile(Image image, TurnOrderTile tile) {
        return new StackPane(new ImageView(image));
    }

    private StackPane drawOfferTrackTile(Image image, TileSlot tileSlot) {
        return new StackPane(new ImageView(image));
    }

    private void setCardEffect(ImageView view, Card card) {
    }

    private void setSkipButtonVisibility() {
    }

    private void drawSkipButton() {
    }

    private void drawDeck() {
    }

    private void drawTiles() {
    }

    private void drawTopRowCards() {
    }

    private void drawTopBuildingsCards() {
    }

    private void drawBottomRowCards() {
    }

    private void drawBottomBuildingCards() {
    }


}
