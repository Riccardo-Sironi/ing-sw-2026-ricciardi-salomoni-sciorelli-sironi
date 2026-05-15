package it.polimi.gc06.mesos.view.gui.elements;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OpponentBox extends VBox {
    private Text nicknameText;
    private VBox detailsContainer;
    private HBox tokensBox;
    private HBox statsBox;
    private ScrollPane cardsScroll;
    private HBox cardsContainer;

    private Text shamanStarsText;
    private Text gatherersText;
    private Text huntersText;
    private Text artistsText;
    private Text buildersDiscountText;

    public OpponentBox(String nickname, String rgbColor, PlayerView player) {
        super();
        this.setAlignment(Pos.CENTER);
        this.setSpacing(0);
        this.setCursor(Cursor.HAND);
        String style = String.format("-fx-background-color: rgba(%s, 0.6); -fx-border-color: rgb(%s); -fx-border-width: 2px; -fx-background-radius: 10; -fx-border-radius: 10;", "0,0,0", rgbColor);
        this.setStyle(style);
        this.setPadding(new Insets(10));
        this.setMinHeight(0);
        this.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(this, Priority.ALWAYS);

        this.nicknameText = new Text(nickname);
        this.nicknameText.setTextAlignment(TextAlignment.CENTER);
        this.nicknameText.setFill(Color.WHITE);

        this.detailsContainer = new VBox();
        this.detailsContainer.setAlignment(Pos.CENTER);
        this.detailsContainer.minWidthProperty().bind(this.widthProperty().subtract(20));
        this.detailsContainer.maxWidthProperty().bind(this.widthProperty().subtract(20));
        VBox.setVgrow(this.detailsContainer, Priority.ALWAYS);

        this.tokensBox = new HBox();
        this.tokensBox.setAlignment(Pos.CENTER);

        this.statsBox = new HBox(5);
        this.statsBox.setAlignment(Pos.CENTER);

        this.cardsScroll = new ScrollPane();
        this.cardsContainer = new HBox(5);
        this.cardsContainer.setAlignment(Pos.CENTER);
        this.cardsScroll.setContent(this.cardsContainer);

        this.cardsScroll.prefHeightProperty().bind(this.detailsContainer.heightProperty().multiply(0.4));

        this.cardsScroll.minWidthProperty().bind(this.detailsContainer.widthProperty());
        this.cardsScroll.maxWidthProperty().bind(this.detailsContainer.widthProperty());

        this.cardsScroll.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                this.cardsScroll.setHvalue(this.cardsScroll.getHvalue() - event.getDeltaY() * 0.003);
                event.consume();
            }
        });

        this.cardsScroll.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            Rectangle clip = new Rectangle(newBounds.getWidth(), newBounds.getHeight());
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            this.cardsScroll.setClip(clip);
        });

        Region spacer1 = new Region();
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        VBox.setVgrow(spacer2, Priority.ALWAYS);
        Region spacer3 = new Region();
        VBox.setVgrow(spacer3, Priority.ALWAYS);
        Region spacer4 = new Region();
        VBox.setVgrow(spacer4, Priority.ALWAYS);

        this.detailsContainer.getChildren().addAll(spacer1, this.tokensBox, spacer2, this.statsBox, spacer3, this.cardsScroll, spacer4);

        this.detailsContainer.setManaged(false);
        this.detailsContainer.setVisible(false);
        this.detailsContainer.setOpacity(0);
        this.detailsContainer.setMinHeight(0);
        this.detailsContainer.setMaxHeight(0);

        this.getChildren().addAll(this.nicknameText, this.detailsContainer);
    }

    public Text getNicknameText() {
        return nicknameText;
    }

    public VBox getDetailsContainer() {
        return detailsContainer;
    }

    public HBox getTokensBox() {
        return tokensBox;
    }

    public HBox getStatsBox() {
        return statsBox;
    }

    public ScrollPane getCardsScroll() {
        return cardsScroll;
    }

    public HBox getCardsContainer() {
        return cardsContainer;
    }

    public Text getShamanStarsText() {
        return shamanStarsText;
    }

    public Text getGatherersText() {
        return gatherersText;
    }

    public Text getHuntersText() {
        return huntersText;
    }

    public Text getArtistsText() {
        return artistsText;
    }

    public Text getBuildersDiscountText() {
        return buildersDiscountText;
    }

    public void setNicknameText(Text nicknameText) {
        this.nicknameText = nicknameText;
    }

    public void setDetailsContainer(VBox detailsContainer) {
        this.detailsContainer = detailsContainer;
    }

    public void setTokensBox(HBox tokensBox) {
        this.tokensBox = tokensBox;
    }

    public void setStatsBox(HBox statsBox) {
        this.statsBox = statsBox;
    }

    public void setCardsScroll(ScrollPane cardsScroll) {
        this.cardsScroll = cardsScroll;
    }

    public void setCardsContainer(HBox cardsContainer) {
        this.cardsContainer = cardsContainer;
    }

    public void setShamanStarsText(Text shamanStarsText) {
        this.shamanStarsText = shamanStarsText;
    }

    public void setGatherersText(Text gatherersText) {
        this.gatherersText = gatherersText;
    }

    public void setHuntersText(Text huntersText) {
        this.huntersText = huntersText;
    }

    public void setArtistsText(Text artistsText) {
        this.artistsText = artistsText;
    }

    public void setBuildersDiscountText(Text buildersDiscountText) {
        this.buildersDiscountText = buildersDiscountText;
    }
}
