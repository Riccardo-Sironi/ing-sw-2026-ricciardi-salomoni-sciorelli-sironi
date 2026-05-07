package it.polimi.gc06.mesos.view.gui.controllers;

import static it.polimi.gc06.mesos.view.gui.GUI.imageFetcher;
import static it.polimi.gc06.mesos.view.gui.GUI.smallModel;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.PropertyChangeName;
import it.polimi.gc06.mesos.view.gui.elements.CardView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PlayerInventoryController implements PropertyChangeListener {

    @FXML
    public HBox playerInventoryRoot;

    @FXML
    public ImageView shamanStarsImage;

    @FXML
    public Text shamanStarsText;

    @FXML
    public VBox gathererQuantity;

    @FXML
    public ImageView gathererQuantityImage;

    @FXML
    public Text gathererQuantityText;

    @FXML
    public VBox hunterQuantity;

    @FXML
    public ImageView hunterQuantityImage;

    @FXML
    public Text hunterQuantityText;

    @FXML
    public VBox artistQuantity;

    @FXML
    public ImageView artistQuantityImage;

    @FXML
    public Text artistQuantityText;

    @FXML
    public VBox buildersDiscount;

    @FXML
    public ImageView buildersDiscountImage;

    @FXML
    public Text buildersDiscountText;

    @FXML
    public HBox stats;

    @FXML
    public VBox shamanStars;

    @FXML
    public ScrollPane cards;

    @FXML
    public HBox cardsContainer;

    @FXML
    public HBox tokens;

    @FXML
    public VBox prestigeTokens;

    @FXML
    public ImageView prestigeTokensImage;

    @FXML
    public Text prestigeTokensText;

    @FXML
    public VBox foodTokens;

    @FXML
    public ImageView foodTokensImage;

    @FXML
    public Text foodTokensText;

    private static final double CARDS_PERCENTAGE = 0.5;
    private static final double TOKENS_PERCENTAGE = 0.3;

    private final Font mesosFont = Font.loadFont(
            this.getClass().getResourceAsStream(
                    "/it/polimi/gc06/mesos/fonts/KidKnowledge.otf"
            ),
            20
    );

    @FXML
    public void initialize() {
        initPlayerCardInventory();
        initTokensContainer();

        for (int i = 0; i < 10; i++) {
            CardView card = createCard(new Image("shaman_1_card.png"));
            cardsContainer.getChildren().add(card);
        }
    }

    public void initPlayerCardInventory() {
        cards
                .prefWidthProperty()
                .bind(
                        playerInventoryRoot.widthProperty().multiply(CARDS_PERCENTAGE)
                );
        cards.prefHeightProperty().bind(playerInventoryRoot.heightProperty());

        cardsContainer.prefWidthProperty().bind(cards.widthProperty());
        cardsContainer.prefHeightProperty().bind(cards.heightProperty());
    }

    public void initTokensContainer() {
        tokens
                .prefWidthProperty()
                .bind(
                        playerInventoryRoot.widthProperty().multiply(TOKENS_PERCENTAGE)
                );

        // Enforce max height bounds to prevent infinite expansion
        tokens.maxHeightProperty().bind(playerInventoryRoot.heightProperty());
        tokens.prefHeightProperty().bind(playerInventoryRoot.heightProperty());

        prestigeTokens
                .prefWidthProperty()
                .bind(tokens.widthProperty().multiply(0.5));
        prestigeTokens.maxHeightProperty().bind(tokens.heightProperty());
        prestigeTokens.prefHeightProperty().bind(tokens.heightProperty());

        foodTokens
                .prefWidthProperty()
                .bind(tokens.widthProperty().multiply(0.5));
        foodTokens.maxHeightProperty().bind(tokens.heightProperty());
        foodTokens.prefHeightProperty().bind(tokens.heightProperty());

        initPrestigeContainer();
        initFoodContainer();
    }

    public void initPrestigeContainer() {
        prestigeTokensImage.setImage(new Image("prestige_token.png"));
        prestigeTokensImage.setPreserveRatio(true);
        prestigeTokensImage.setSmooth(true);

        // Bind directly to the root to skip the VBox in the size calculation
        prestigeTokensImage
                .fitHeightProperty()
                .bind(playerInventoryRoot.heightProperty().multiply(0.34));

        prestigeTokensText.setText("10");
        prestigeTokensText.setFont(mesosFont);
        prestigeTokensText.setTextAlignment(TextAlignment.CENTER);
        prestigeTokensText
                .wrappingWidthProperty()
                .bind(prestigeTokens.widthProperty());
    }

    public void initFoodContainer() {
        foodTokensImage.setImage(new Image("food_token.png"));
        foodTokensImage.setPreserveRatio(true);
        foodTokensImage.setSmooth(true);

        // Bind directly to the root
        foodTokensImage
                .fitHeightProperty()
                .bind(playerInventoryRoot.heightProperty().multiply(0.35));

        foodTokensText.setText("15");
        foodTokensText.setFont(mesosFont);
        foodTokensText.setTextAlignment(TextAlignment.CENTER);
        foodTokensText.wrappingWidthProperty().bind(foodTokens.widthProperty());
    }

    public void initStatsContainer() {
        stats
                .prefWidthProperty()
                .bind(
                        playerInventoryRoot.widthProperty().multiply(TOKENS_PERCENTAGE)
                );

        stats.maxHeightProperty().bind(playerInventoryRoot.heightProperty());
        stats.prefHeightProperty().bind(playerInventoryRoot.heightProperty());

        shamanStars.prefWidthProperty().bind(stats.widthProperty().divide(5));
        shamanStars.maxHeightProperty().bind(stats.heightProperty());
        shamanStars.prefHeightProperty().bind(stats.heightProperty());

        gathererQuantity
                .prefWidthProperty()
                .bind(stats.widthProperty().divide(5));
        gathererQuantity.maxHeightProperty().bind(stats.heightProperty());
        gathererQuantity.prefHeightProperty().bind(stats.heightProperty());

        hunterQuantity
                .prefWidthProperty()
                .bind(stats.widthProperty().divide(5));
        hunterQuantity.maxHeightProperty().bind(stats.heightProperty());
        hunterQuantity.prefHeightProperty().bind(stats.heightProperty());

        artistQuantity
                .prefWidthProperty()
                .bind(stats.widthProperty().divide(5));
        artistQuantity.maxHeightProperty().bind(stats.heightProperty());
        artistQuantity.prefHeightProperty().bind(stats.heightProperty());

        buildersDiscount
                .prefWidthProperty()
                .bind(stats.widthProperty().divide(5));
        buildersDiscount.maxHeightProperty().bind(stats.heightProperty());
        buildersDiscount.prefHeightProperty().bind(stats.heightProperty());
    }

    // ... containers

    private CardView createCard(Image image) {
        CardView card = new CardView(image);
        card.fitHeightProperty().bind(cards.heightProperty());
        return card;
    }

    public void drawPlayerCards() {
        cardsContainer.getChildren().clear();
        for (Card card : smallModel.getPlayer().getCharacters()) {
            CardView cardView = createCard(new Image(imageFetcher.fetch(card)));
            cardsContainer.getChildren().add(cardView);
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
                    break;
                case TOP_BUILDINGS_REFILL:
                    break;
                case PICK_FROM_TOP_ROW:
                    break;
                case PICK_FROM_BOTTOM_ROW:
                    break;
                case PICK_FROM_TOP_BUILDINGS:
                    break;
                case PICK_FROM_BOTTOM_BUILDINGS:
                    break;
                case TOTEM_MOVED:
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
