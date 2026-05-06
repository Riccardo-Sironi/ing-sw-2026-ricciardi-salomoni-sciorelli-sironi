package it.polimi.gc06.mesos.view.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.cards.Card;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.NoSuchElementException;
import java.util.Random;

public class ImageFetcher {

    private static final String JSON_URL = "/it/polimi/gc06/mesos/jsons/imageFetcherConfig.json";

    private final int numOfPlayers;
    private final IdentityHashMap<Card,String> cardMap;
    private final Random randomizer;
    private final ArrayList<CardImagesInfo> imagesInfos;

    public ImageFetcher(int numOfPlayers) throws IOException {
        this.numOfPlayers = numOfPlayers;
        this.cardMap = new IdentityHashMap<>();
        this.randomizer = new Random();

        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass().getResourceAsStream(JSON_URL);
        this.imagesInfos = mapper.readValue(input, new TypeReference<ArrayList<CardImagesInfo>>() {});
    }

    public ImageFetcher(int numOfPlayers, long seed) throws IOException {
        this.numOfPlayers = numOfPlayers;
        this.cardMap = new IdentityHashMap<>();
        this.randomizer = new Random(seed);

        ObjectMapper mapper = new ObjectMapper();
        InputStream input = getClass().getResourceAsStream(JSON_URL);
        this.imagesInfos = mapper.readValue(input, new TypeReference<ArrayList<CardImagesInfo>>() {});
    }

    /**
     * Provides an image for the given card by an internal logic.
     *
     * @param card which image will be provided.
     * @return the image.
     * @throws NoSuchElementException if the fetching logic fails to find a valid image
     */
    public String fetch(Card card) throws NoSuchElementException{

        if(cardMap.containsKey(card)) return cardMap.get(card);

        //fetching logic
        CardImagesInfo info = imagesInfos.stream().filter(i->i.numOfPlayers() == numOfPlayers)
                .filter(i -> i.cardInfo().equals(card)).findFirst().orElseThrow(NoSuchElementException::new);
        cardMap.put(card,info.validImagesUrls.get(randomizer.nextInt(0,info.validImagesUrls.size())));

        return cardMap.get(card);
    }

    record CardImagesInfo(Card cardInfo, int numOfPlayers, ArrayList<String> validImagesUrls){}
}
