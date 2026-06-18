package it.polimi.gc06.mesos.view.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameTurnManager.EndOfRoundPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.EventResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.view.gui.helpers.Totem;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImageFetcher {

    private static final String CARDS_URL = "/it/polimi/gc06/mesos/jsons/imageFetcherCards.json";
    private static final String TILES_URL = "/it/polimi/gc06/mesos/jsons/imageFetcherTiles.json";

    private final int numOfPlayers;

    private final Random randomizer;

    private final IdentityHashMap<Card, Image> cardMap;
    private final IdentityHashMap<TileEffect, Image> offerTileMap;
    private final EnumMap<Color, Image> totemsMap = new EnumMap<>(Color.class);

    private final HashMap<String, Image> phaseOverlayMap;
    private final EnumMap<Era, Image> eraOverlayMap;

    private final EnumMap<Era, String> eraBackgroundsMap;

    private final EnumMap<Era, Image> eraDeckImage;
    private final Image finalEventDeckImage;

    private final Image nullCardImage;
    private final Image noneTotemImage;
    private final Image turnOrderTileImage;

    private final Image positivePrestigeTokenImage;
    private final Image negativePrestigeTokenImage;

    private final ArrayList<CardImagesInfo> cardInfos;
    private final ArrayList<OfferTileInfo> tileInfos;

    private final Image skipButtonImage;

    public ImageFetcher(int numOfPlayers) throws IOException {
        Random r = new Random();
        this(numOfPlayers, r.nextLong());
    }

    public ImageFetcher(int numOfPlayers, long seed) throws IOException {
        this.numOfPlayers = numOfPlayers;
        this.cardMap = new IdentityHashMap<>();
        this.offerTileMap = new IdentityHashMap<>();
        this.tileInfos = new ArrayList<>();
        this.randomizer = new Random(seed);
        this.phaseOverlayMap = new HashMap<>();
        this.eraOverlayMap = new EnumMap<>(Era.class);
        this.eraDeckImage = new EnumMap<>(Era.class);
        this.eraBackgroundsMap = new EnumMap<>(Era.class);

        //fetches cards images
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass().getResourceAsStream(CARDS_URL);
        this.cardInfos = mapper.readValue(input, new TypeReference<>() {
        });

        //fetches offer tiles & turn order tile images
        input = getClass().getResourceAsStream(TILES_URL);
        Map<Integer, TileImagesInfo> map = mapper.readValue(input, new TypeReference<>() {
        });
        for (int i = 2; i <= numOfPlayers; i++) {
            tileInfos.addAll(map.get(i).offerTiles);
        }
        turnOrderTileImage = loadImage(map.get(numOfPlayers).turnOrderTileUrl);

        // init totem map
        for (Color color : Color.values()) {
            String path = Totem.getTotem(color).getTotemStanding();
            totemsMap.put(color, loadImage(path));
        }

        // era deck background map init
        eraDeckImage.put(Era.ERA_I, loadImage("/cards/backs/tribe_card_era_I_back.png"));
        eraDeckImage.put(Era.ERA_II, loadImage("/cards/backs/tribe_card_era_II_back.png"));
        eraDeckImage.put(Era.ERA_III, loadImage("/cards/backs/tribe_card_era_III_back.png"));
        finalEventDeckImage = loadImage("/cards/backs/tribe_card_era_III_final_back.png");

        for (Era era : Era.values()) {
            // era overlay map init
            eraOverlayMap.put(era, loadImage("/imgs/overlay/" + era.name().toLowerCase() + "_overlay.png"));

            // era background map init
            String bgPath = "/imgs/background/board_background_" + era.name().toLowerCase() + ".png";
            java.net.URL bgUrl = getClass().getResource(bgPath);

            if (bgUrl != null) {
                eraBackgroundsMap.put(era, bgUrl.toExternalForm());
            } else {
                System.err.println("Background image not found: " + bgPath);
            }
        }

        // phase overlay map init
        phaseOverlayMap.put(new PlacingTotemPhase().toString(), loadImage("/imgs/overlay/" + new PlacingTotemPhase().toString().toLowerCase() + "_phase_overlay.png"));
        phaseOverlayMap.put(new OfferResolutionPhase().toString(), loadImage("/imgs/overlay/" + new OfferResolutionPhase().toString().toLowerCase() + "_phase_overlay.png"));
        phaseOverlayMap.put(new EventResolutionPhase().toString(), loadImage("/imgs/overlay/" + new EventResolutionPhase().toString().toLowerCase() + "_phase_overlay.png"));
        phaseOverlayMap.put(new EndOfRoundPhase().toString(), loadImage("/imgs/overlay/" + new EndOfRoundPhase().toString().toLowerCase() + "_phase_overlay.png"));

        nullCardImage = loadImage("/cards/fronts/null_card.png");
        noneTotemImage = loadImage(Totem.NONE.getTotemStanding());

        positivePrestigeTokenImage = loadImage("imgs/tokens/prestige_token.png");
        negativePrestigeTokenImage = loadImage("imgs/tokens/negative_prestige_token.png");

        skipButtonImage = loadImage("imgs/skip_button_image.png");
    }

    /**
     * Provides an image for the given card.
     *
     * @param card which image will be provided.
     * @return the image.
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public Image fetch(Card card) throws NoSuchElementException {

        if (cardMap.containsKey(card)) return cardMap.get(card);

        //fetching logic
        CardImagesInfo info = cardInfos.stream().filter(i -> i.numOfPlayers <= numOfPlayers)
                .filter(i -> i.cardInfo.equals(card)).findFirst().orElseThrow(NoSuchElementException::new);

        String path = info.validImagesUrls.get(randomizer.nextInt(0, info.validImagesUrls.size()));

        cardMap.put(card, loadImage(path));

        return cardMap.get(card);
    }

    /**
     * Provides an image for the given tile.
     *
     * @param tile which image will be provided
     * @return the image
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public Image fetch(TileSlotView tile) throws NoSuchElementException {
        TileEffect effect = tile.getTileEffect();
        if (offerTileMap.containsKey(effect)) return offerTileMap.get(effect);

        //fetching logic
        OfferTileInfo info = tileInfos.stream().filter(i -> i.tileEffect.equals(effect)).findFirst()
                .orElseThrow(NoSuchElementException::new);

        offerTileMap.put(effect, loadImage(info.offerTileUrl));

        return offerTileMap.get(effect);
    }

    public Image getDeckBackImage(Era era) {
        if (eraDeckImage.containsKey(era)) return eraDeckImage.get(era);
        System.err.println("couldn't find deck back image for era " + era);
        return null;
    }

    public Image getEraOverlayImage(Era era) {
        if (eraOverlayMap.containsKey(era)) return eraOverlayMap.get(era);
        System.err.println("couldn't find overlay image for era " + era);
        return null;
    }

    public Image getPhaseOverlayImage(String phase) {
        if (phaseOverlayMap.containsKey(phase)) return phaseOverlayMap.get(phase);
        System.err.println("couldn't find overlay image for phase " + phase);
        return null;
    }

    public String getEraBackgroundsImage(Era era) {
        if (eraBackgroundsMap.containsKey(era)) return eraBackgroundsMap.get(era);
        System.err.println("couldn't find background image for era " + era);
        return null;
    }

    public Image getTotemImage(Color color) {
        return totemsMap.get(color);
    }

    public Image getTotemImage() {
        return noneTotemImage;
    }

    public Image getNullCardImage() {
        return nullCardImage;
    }

    public Image getTurnOrderTileImage() {
        return turnOrderTileImage;
    }

    public Image getFinalEventDeckImage() {
        return finalEventDeckImage;
    }

    public Image getPositivePrestigeTokenImage() {
        return positivePrestigeTokenImage;
    }

    public Image getNegativePrestigeTokenImage() {
        return negativePrestigeTokenImage;
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

    public Image getSkipButtonImage() {
        return skipButtonImage;
    }

    public static class CardImagesInfo {
        Card cardInfo;
        int numOfPlayers;
        ArrayList<String> validImagesUrls;

        public void setCardInfo(Card cardInfo) {
            this.cardInfo = cardInfo;
        }

        public void setNumOfPlayers(int numOfPlayers) {
            this.numOfPlayers = numOfPlayers;
        }

        public void setValidImagesUrls(ArrayList<String> validImagesUrls) {
            this.validImagesUrls = validImagesUrls;
        }
    }

    public static class OfferTileInfo {
        TileEffect tileEffect;
        String offerTileUrl;

        public void setTileEffect(TileEffect tileEffect) {
            this.tileEffect = tileEffect;
        }

        public void setOfferTileUrl(String offerTileUrl) {
            this.offerTileUrl = offerTileUrl;
        }
    }

    public static class TileImagesInfo {
        ArrayList<OfferTileInfo> offerTiles;
        String turnOrderTileUrl;

        public void setOfferTiles(ArrayList<OfferTileInfo> offerTiles) {
            this.offerTiles = offerTiles;
        }

        public void setTurnOrderTileUrl(String turnOrderTileUrl) {
            this.turnOrderTileUrl = turnOrderTileUrl;
        }
    }
}
