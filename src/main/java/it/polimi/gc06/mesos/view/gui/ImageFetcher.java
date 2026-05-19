package it.polimi.gc06.mesos.view.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
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
    private static final EnumMap<Color, Image> totemsMap = new EnumMap<>(Color.class);
    private static Image noneTotemImage = null;
    private final ArrayList<CardImagesInfo> cardInfos;
    private static Image nullCardImage = null;
    private final IdentityHashMap<TileEffect, String> offerTileMap;
    private final ArrayList<OfferTileInfo> tileInfos;
    private final String turnOrderTileUrl;

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

        //fetches cards images
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass().getResourceAsStream(CARDS_URL);
        this.cardInfos = mapper.readValue(input, new TypeReference<ArrayList<CardImagesInfo>>() {
        });

        //fetches offer tiles & turn order tile images
        input = getClass().getResourceAsStream(TILES_URL);
        Map<Integer, TileImagesInfo> map = mapper.readValue(input, new TypeReference<Map<Integer, TileImagesInfo>>() {
        });
        for (int i = 2; i <= numOfPlayers; i++) {
            tileInfos.addAll(map.get(i).offerTiles);
        }

        turnOrderTileUrl = map.get(numOfPlayers).turnOrderTileUrl;

        for (Color color : Color.values()) {
            String path = Totem.getTotem(color).getTotemStanding();

            try {
                Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
                totemsMap.put(color, img);
            } catch (Exception e) {
                System.err.println("ERRORE: Impossibile caricare il totem per il colore: " + color);
            }
        }

        String path = Totem.NONE.getTotemStanding();

        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            noneTotemImage = img;
        } catch (Exception e) {
            System.err.println("ERRORE: Impossibile caricare il totem per il colore: NONE ");
        }

        String nulCardPath = "/cards/fronts/null_card.png";
        nullCardImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(nulCardPath)));
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

        Image img;
        try {
            if (!path.startsWith("/")) path = "/" + path;
            img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            System.err.println("image not found: " + path);
            img = null;
        }
        cardMap.put(card, img);

        return cardMap.get(card);
    }

    /**
     * Provides an image for the given tile.
     *
     * @param tile which image will be provided
     * @return the image
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public String fetch(TileSlotView tile) throws NoSuchElementException {
        TileEffect effect = tile.getTileEffect();
        if (offerTileMap.containsKey(effect)) return offerTileMap.get(effect);

        //fetching logic
        OfferTileInfo info = tileInfos.stream().filter(i -> i.tileEffect.equals(effect)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        offerTileMap.put(effect, info.offerTileUrl);

        return offerTileMap.get(effect);
    }

    public static Image getTotemImage(Color color) {
        return totemsMap.get(color);
    }

    public static Image getTotemImage() {
        return noneTotemImage;
    }

    public static Image getNullCardImage() {
        return nullCardImage;
    }

    public String getTurnOrderTileUrl() {
        return turnOrderTileUrl;
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
