package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.gui.elements.*;
import it.polimi.gc06.mesos.view.gui.helpers.EffectsManager;
import it.polimi.gc06.mesos.view.gui.helpers.OfferTileInfo;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.gui.visitors.CardEffectVisitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.css.PseudoClass;

public class BoardController implements PropertyChangeListener {

    @FXML private HBox mainRoot;
    @FXML private VBox leftZone;
    @FXML private VBox opponentsSidebar;

    @FXML private VBox boardRoot;
    @FXML private HBox inventoryBox;

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
    @FXML private ScrollPane bottomBuildingsScroll;
    @FXML private HBox bottomBuildingsContainer;

    private static final PseudoClass DISABLED_STYLE = PseudoClass.getPseudoClass("skip-disabled");

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
        }
        else {
            deckImage.setImage(loadImage("tribe_card_era_I_back.png"));

            for (int i = 0; i < 7; i++) {
                CardView card = new CardView(loadImage("shaman_1_card.png"));
                card.fitHeightProperty().bind(topRowBox.heightProperty().multiply(0.85));
                EffectsManager.activeCard(card);
                topCharactersContainer.getChildren().add(card);
            }

            for (int i = 0; i < 7; i++) {
                CardView bCard = new CardView(loadImage("shaman_1_card.png"));
                bCard.fitHeightProperty().bind(bottomRowBox.heightProperty().multiply(0.85));
                EffectsManager.disableCard(bCard);
                bottomCharactersContainer.getChildren().add(bCard);
            }

            ArrayList<Totem> totemList = new ArrayList<>(Arrays.asList(Totem.YELLOW, Totem.TURQUOISE, Totem.PURPLE, Totem.WHITE, Totem.ORANGE));
            TurnOrderTileView turnOrderTile = createTurnOrderTile(loadImage("turn_order_tile_5_players.png"), totemList);
            turnOrderContainer.getChildren().add(turnOrderTile);

            OffertTileView tileA = createOfferTile(loadImage("offer_tile_A.png"), Totem.YELLOW, "Player 1", OfferTileInfo.OFFER_TILE_A.getXPercent());
            OffertTileView tileB = createOfferTile(loadImage("offer_tile_B.png"), Totem.YELLOW, "Mock Player", OfferTileInfo.OFFER_TILE_B.getXPercent());
            OffertTileView tileC = createOfferTile(loadImage("offer_tile_C.png"), Totem.ORANGE, "Player 2", OfferTileInfo.OFFER_TILE_C.getXPercent());
            OffertTileView tileD = createOfferTile(loadImage("offer_tile_D.png"), Totem.TURQUOISE, "Player 3", OfferTileInfo.OFFER_TILE_D.getXPercent());
            OffertTileView tileE = createOfferTile(loadImage("offer_tile_E.png"), Totem.PURPLE, "Player 4", OfferTileInfo.OFFER_TILE_E.getXPercent());
            OffertTileView tileF = createOfferTile(loadImage("offer_tile_F.png"), Totem.YELLOW, "Mock Player", OfferTileInfo.OFFER_TILE_F.getXPercent());
            OffertTileView tileG = createOfferTile(loadImage("offer_tile_G.png"), Totem.WHITE, "Player 5", OfferTileInfo.OFFER_TILE_G.getXPercent());

            offerTrackContainer.getChildren().addAll(tileA, tileB, tileC, tileD, tileE, tileF, tileG);
        }
    }

    private void setupArchitecturalLayout() {
        final double WIDTH_LEFT_ZONE = 0.85;
        final double WIDTH_OPPONENTS = 0.15;

        leftZone.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_LEFT_ZONE));
        opponentsSidebar.prefWidthProperty().bind(mainRoot.widthProperty().multiply(WIDTH_OPPONENTS));
        opponentsSidebar.prefHeightProperty().bind(mainRoot.heightProperty());

        final double HEIGHT_BOARD = 0.7;
        final double HEIGHT_INVENTORY = 0.3;

        boardRoot.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_BOARD));
        inventoryBox.prefHeightProperty().bind(leftZone.heightProperty().multiply(HEIGHT_INVENTORY));

        topBar.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.10));
        topRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.35));
        centerRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.30));
        bottomRowBox.prefHeightProperty().bind(boardRoot.heightProperty().multiply(0.35));

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
        turnOrderContainer.setAlignment(Pos.CENTER);
        offerTrackContainer.prefWidthProperty().bind(singleTileWidth.multiply(7));
        offerTrackContainer.setSpacing(-2);

        skipButtonContainer.prefWidthProperty().bind(leftZone.widthProperty().multiply(0.15));
        skipButtonContainer.setAlignment(Pos.CENTER);
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

            offerTrackContainer.getChildren().add(createOfferTile(img, t, nickname, 0.50));
        }
    }

    private void drawDeck() {
        if (smallModel == null) return;
        switch (smallModel.getEra()) {
            case ERA_I: deckImage.setImage(loadImage("tribe_card_era_I_back.png")); break;
            case ERA_II: deckImage.setImage(loadImage("tribe_card_era_II_back.png")); break;
            case ERA_III: deckImage.setImage(loadImage("tribe_card_era_III_back.png")); break;
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
        int nPlayers = (smallModel == null) ? 5 : smallModel.getOpponents().size() + 1;
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
                case PICK_FROM_TOP_ROW: handleTopRowPick(); break;
                case PICK_FROM_BOTTOM_ROW: handleBottomRowPick(); break;
                case PICK_FROM_TOP_BUILDINGS: handleTopBuildingsPick(); break;
                case PICK_FROM_BOTTOM_BUILDINGS: handleBottomBuildingsPick(); break;
                case TOP_ROW_REFILL: handleTopRowRefill(); break;
                case TOP_BUILDINGS_REFILL: handleTopBuildingsRefill(); break;

                case TOTEM_MOVED_OFFER:
                case TOTEM_PLACEMENT_TURN: handleTotemMoved(); break;

                case ROUND_CHANGED: handleRoundChanged(); break;
                case ACTIVE_PLAYER_CHANGED: handleActivePlayerChanged(); break;

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