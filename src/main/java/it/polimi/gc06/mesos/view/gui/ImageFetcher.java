package it.polimi.gc06.mesos.view.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImageFetcher {

    private static final String CARDS_URL = "/it/polimi/gc06/mesos/jsons/imageFetcherCards.json";
    private static final String TILES_URL = "/it/polimi/gc06/mesos/jsons/imageFetcherTiles.json";

    private final int numOfPlayers;
    private final Random randomizer;
    private final IdentityHashMap<Card,String> cardMap;
    private final ArrayList<CardImagesInfo> cardInfos;
    private final IdentityHashMap<TileEffect,String> offerTileMap;
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
        this.cardInfos = mapper.readValue(input, new TypeReference<ArrayList<CardImagesInfo>>() {});

        //fetches offer tiles & turn order tile images
        input = getClass().getResourceAsStream(TILES_URL);
        Map<Integer,TileImagesInfo> map = mapper.readValue(input, new TypeReference<Map<Integer, TileImagesInfo>>() {});
        for(int i = 2; i< numOfPlayers; i++){
            tileInfos.addAll(map.get(i).offerTiles());
        }
        turnOrderTileUrl = map.get(numOfPlayers).turnOrderTileUrl();
    }

    /**
     * Provides an image for the given card.
     *
     * @param card which image will be provided.
     * @return the image.
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public String fetch(Card card) throws NoSuchElementException{

        if(cardMap.containsKey(card)) return cardMap.get(card);

        //fetching logic
        CardImagesInfo info = cardInfos.stream().filter(i->i.numOfPlayers() == numOfPlayers)
                .filter(i -> i.cardInfo().equals(card)).findFirst().orElseThrow(NoSuchElementException::new);
        cardMap.put(card,info.validImagesUrls.get(randomizer.nextInt(0,info.validImagesUrls.size())));

        return cardMap.get(card);
    }

    /**
     * Provides an image for the given tile.
     *
     * @param tile which image will be provided
     * @return the image
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public String fetch(TileSlot tile) throws NoSuchElementException{
        TileEffect effect = tile.getTileEffect();
        if(offerTileMap.containsKey(effect)) return offerTileMap.get(effect);

        //fetching logic
        OfferTileInfo info = tileInfos.stream().filter(i -> i.tileEffect().equals(effect)).findFirst()
                .orElseThrow(NoSuchElementException::new);
        offerTileMap.put(effect,info.offerTileUrl);

        return offerTileMap.get(effect);
    }

    public String getTurnOrderTileUrl(){
        return turnOrderTileUrl;
    }

    record CardImagesInfo(Card cardInfo, int numOfPlayers, ArrayList<String> validImagesUrls){}
    record OfferTileInfo(TileEffect tileEffect, String offerTileUrl){}
    record TileImagesInfo(ArrayList<OfferTileInfo> offerTiles, String turnOrderTileUrl){}
}
