package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.gui.elements.*;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.css.PseudoClass;


public class BoardController implements PropertyChangeListener {

    @FXML private VBox boardRoot;

    @FXML private HBox topBar;

    @FXML private HBox topRowBox;
    @FXML private ScrollPane topCharactersScroll;
    @FXML private HBox topCharactersContainer;
    @FXML private ScrollPane buildingsScroll;
    @FXML private HBox buildingsContainer;

    @FXML private HBox centerRowBox;
    @FXML private HBox deckContainer;
    @FXML public ImageView deckImage;
    @FXML private HBox turnOrderContainer;
    @FXML private HBox offerTrackContainer;
    @FXML private HBox skipButtonContainer;
    @FXML public Button skipButton;

    @FXML private HBox bottomRowBox;
    @FXML private ScrollPane bottomCharactersScroll;
    @FXML private HBox bottomCharactersContainer;

    @FXML private HBox inventoryBox;

    private static final PseudoClass DISABLED_STYLE = PseudoClass.getPseudoClass("skip-disabled");

    @FXML
    public void initialize() {
        setupArchitecturalLayout();

        deckImage.setImage(loadImage("tribe_card_era_I_back.png"));

        for (int i = 0; i < 12; i++) {
            CardView card = new CardView(loadImage("shaman_1_card.png"));
            card.fitHeightProperty().bind(topRowBox.heightProperty().multiply(0.85));
            EffectsManager.activeCard(card);
            topCharactersContainer.getChildren().add(card);
        }

        for (int i = 0; i < 4; i++) {
            CardView building = new CardView(loadImage("1.png"));
            building.fitHeightProperty().bind(topRowBox.heightProperty().multiply(0.85));
            buildingsContainer.getChildren().add(building);
        }

        for (int i = 0; i < 7; i++) {
            CardView card = new CardView(loadImage("shaman_1_card.png"));
            card.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(0.85));
            EffectsManager.disableCard(card);
            bottomCharactersContainer.getChildren().add(card);
        }

        TurnOrderTileView turnOrderTile = createTurnOrderTile(
                loadImage("turn_order_tile_5_players.png"),
                new ArrayList<>(Arrays.asList(Totem.YELLOW, Totem.TURQUOISE, Totem.PURPLE, Totem.WHITE, Totem.ORANGE))
        );
        turnOrderContainer.getChildren().add(turnOrderTile);

        OffertTileView tileA = createOfferTile(loadImage("offer_tile_A.png"), Totem.YELLOW, "Player 1", 0.53);
        OffertTileView tileB = createOfferTile(loadImage("offer_tile_B.png"), Totem.YELLOW, "Mock Player", 0.497);
        OffertTileView tileC = createOfferTile(loadImage("offer_tile_C.png"), Totem.ORANGE, "Player 2", 0.465);
        OffertTileView tileD = createOfferTile(loadImage("offer_tile_D.png"), Totem.TURQUOISE, "Player 3", 0.5);
        OffertTileView tileE = createOfferTile(loadImage("offer_tile_E.png"), Totem.PURPLE, "Player 4", 0.527);
        OffertTileView tileF = createOfferTile(loadImage("offer_tile_F.png"), Totem.YELLOW, "Mock Player", 0.53);
        OffertTileView tileG = createOfferTile(loadImage("offer_tile_G.png"), Totem.WHITE, "Player 5", 0.48);

        offerTrackContainer.getChildren().addAll(tileA, tileB, tileC, tileD, tileE, tileF, tileG);
    }

    private Image loadImage(String path) {
        try {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Immagine non trovata, controlla il percorso: " + path);
            return null;
        }
    }

    private void setupArchitecturalLayout() {

        final double HEIGHT_TOP_BAR    = 0.05;
        final double HEIGHT_TOP_ROW    = 0.25;
        final double HEIGHT_CENTER_ROW = 0.2;
        final double HEIGHT_BOTTOM_ROW = 0.25;
        final double HEIGHT_INVENTORY  = 0.25;

        topBar.prefHeightProperty().bind(boardRoot.heightProperty().multiply(HEIGHT_TOP_BAR));
        topRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(HEIGHT_TOP_ROW));
        centerRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(HEIGHT_CENTER_ROW));
        bottomRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(HEIGHT_BOTTOM_ROW));
        inventoryBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(HEIGHT_INVENTORY));

        topRowBox.setAlignment(Pos.CENTER);
        topRowBox.setSpacing(20);

        DoubleBinding topWidth = topRowBox.widthProperty().multiply(0.80).subtract(40);

        topCharactersScroll.prefWidthProperty().bind(topWidth.multiply(0.75));
        topCharactersScroll.setFitToWidth(true);
        topCharactersScroll.setFitToHeight(true);

        buildingsScroll.prefWidthProperty().bind(topWidth.multiply(0.25));
        buildingsScroll.setFitToWidth(true);
        buildingsScroll.setFitToHeight(true);

        topCharactersContainer.setAlignment(Pos.CENTER);
        topCharactersContainer.setSpacing(10);
        buildingsContainer.setAlignment(Pos.CENTER);
        buildingsContainer.setSpacing(10);

        centerRowBox.setAlignment(Pos.CENTER);
        centerRowBox.setSpacing(30);

        deckContainer.prefWidthProperty().bind(centerRowBox.widthProperty().multiply(0.15));
        deckContainer.setAlignment(Pos.CENTER);
        deckImage.fitHeightProperty().bind(centerRowBox.heightProperty().multiply(0.85));

        DoubleBinding singleTileWidth = centerRowBox.widthProperty().multiply(0.60).divide(8);

        turnOrderContainer.prefWidthProperty().bind(singleTileWidth);
        turnOrderContainer.setAlignment(Pos.CENTER);

        offerTrackContainer.prefWidthProperty().bind(singleTileWidth.multiply(7));
        offerTrackContainer.setAlignment(Pos.BOTTOM_CENTER);
        offerTrackContainer.setSpacing(-2);

        skipButtonContainer.prefWidthProperty().bind(centerRowBox.widthProperty().multiply(0.15));
        skipButtonContainer.setAlignment(Pos.CENTER);

        bottomRowBox.setAlignment(Pos.CENTER);

        bottomCharactersScroll.prefWidthProperty().bind(bottomRowBox.widthProperty().multiply(0.60));
        bottomCharactersScroll.setFitToWidth(true);
        bottomCharactersScroll.setFitToHeight(true);

        bottomCharactersContainer.setAlignment(Pos.CENTER);
        bottomCharactersContainer.setSpacing(10);
    }

    private TurnOrderTileView createTurnOrderTile(Image image, int nPlayers) {
        TurnOrderTileView turnOrderTile = new TurnOrderTileView(image, nPlayers);
        turnOrderTile.setMinSize(0, 0);
        turnOrderTile.prefHeightProperty().bind(turnOrderContainer.heightProperty());
        turnOrderTile.maxHeightProperty().bind(turnOrderContainer.heightProperty());
        turnOrderTile.getImageView().fitHeightProperty().bind(turnOrderContainer.heightProperty());
        return turnOrderTile;
    }

    private TurnOrderTileView createTurnOrderTile(Image image, ArrayList<Totem> totems) {
        int nPlayers = (smallModel == null) ? 5 : smallModel.getOpponents().size() + 1;
        TurnOrderTileView turnOrderTile = createTurnOrderTile(image, nPlayers);
        ArrayList<TotemPieceView> totemPieces = new ArrayList<>();

        int count = 1;
        for (Totem totem : totems) {
            totemPieces.add(new TotemPieceView(totem, "Player " + count));
            count++;
        }

        turnOrderTile.setTotemPieces(totemPieces);
        return turnOrderTile;
    }

    private OffertTileView createOfferTile(Image image, Totem totem, String playerName, double xPercent) {
        OffertTileView tile = new OffertTileView(image);
        tile.setMinSize(0, 0);
        tile.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        tile.prefHeightProperty().bind(offerTrackContainer.heightProperty());
        tile.maxHeightProperty().bind(offerTrackContainer.heightProperty());
        tile.getImageView().fitHeightProperty().bind(offerTrackContainer.heightProperty());

        if (totem != null) {
            tile.setTotem(new TotemPieceView(totem, playerName));
        }

        tile.setCenterPercentage(xPercent, 0.2);
        return tile;
    }

    private void toggleSkipButton(boolean canSkip) {
        skipButton.pseudoClassStateChanged(DISABLED_STYLE, !canSkip);
        skipButton.applyCss();
        skipButton.setCursor(canSkip ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void setCardEffect(CardView cardView) {
        CardEffectVisitor visitor = new CardEffectVisitor(cardView);
        cardView.getCard().accept(visitor);
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
                // --- PICKING ---
                case PICK_FROM_TOP_ROW: handleTopRowPick(); break;
                case PICK_FROM_BOTTOM_ROW: handleBottomRowPick(); break;
                case PICK_FROM_TOP_BUILDINGS: handleTopBuildingsPick(); break;
                case PICK_FROM_BOTTOM_BUILDINGS: handleBottomBuildingsPick(); break;

                // --- REFILL ---
                case TOP_ROW_REFILL: handleTopRowRefill(); break;
                case TOP_BUILDINGS_REFILL: handleTopBuildingsRefill(); break;

                // --- PLAYER INFO ---
                case PLAYER_CAN_SKIP: handlePlayerCanSkip(); break;
                case FOOD_CHANGED: handleFoodChanged(); break;
                case PRESTIGE_CHANGED: handlePrestigeChanged(); break;
                case TOP_NUM_DRAW_CHANGED: handleTopNumDrawChanged(); break;
                case BOTTOM_NUM_DRAW_CHANGED: handleBottomNumDrawChanged(); break;

                // --- MISCELLANEOUS ---
                case TOTEM_MOVED_OFFER: handleTotemMovedOffer(); break;
                case TOTEM_PLACEMENT_TURN: handleTotemPlacementTurn(); break;
                case PHASE_CHANGED: handlePhaseChanged(); break;
                case ACTIVE_PLAYER_CHANGED: handleActivePlayerChanged(); break;
                case ROUND_CHANGED: handleRoundChanged(); break;
                case ERA_CHANGED: handleEraChanged(); break;
                case IS_END_GAME: handleEndGame(); break;
                default: break;
            }
        });
    }

    // =========================================================================
    // TODO: METODI VUOTI DA IMPLEMENTARE PER LA LOGICA DI GIOCO
    // =========================================================================
    private void handleTopRowPick() {}
    private void handleBottomRowPick() {}
    private void handleTopBuildingsPick() {}
    private void handleBottomBuildingsPick() {}
    private void handleTopRowRefill() {}
    private void handleTopBuildingsRefill() {}
    private void handlePlayerCanSkip() {}
    private void handleFoodChanged() {}
    private void handlePrestigeChanged() {}
    private void handleTopNumDrawChanged() {}
    private void handleBottomNumDrawChanged() {}
    private void handleTotemMovedOffer() {}
    private void handleTotemPlacementTurn() {}
    private void handlePhaseChanged() {}
    private void handleActivePlayerChanged() {}
    private void handleRoundChanged() {}
    private void handleEraChanged() {}
    private void handleEndGame() {}
}