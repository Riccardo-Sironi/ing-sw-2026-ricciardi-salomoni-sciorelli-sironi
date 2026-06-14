package it.polimi.gc06.mesos.model.InstancesManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffectRegistryAssigner;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModelInstancesManager {

    public static final String JSON_PATH = "/it/polimi/gc06/mesos/jsons/";
    private final DTONotifier notifier;

    public ModelInstancesManager(DTONotifier notifier){
        this.notifier = notifier;
    }

    /**
     * This method creates and initializes a complete GameModel instance by parsing JSON configurations
     * for cards, modifier buildings, turn order tiles, and offer tracks.
     * It also sets up players, the game board, and the turn manager based on the provided list of nicknames.
     *
     * @param nicknames the list of player nicknames to join the game.
     * @return a fully initialized GameModel ready to be played.
     * @throws IOException if there is an error reading the JSON configuration files.
     * @throws IllegalArgumentException if the nicknames list is null, empty, or has an invalid size.
     */
    public GameModel createGame(List<String> nicknames) throws IOException, IllegalArgumentException {

        if (nicknames == null) throw new IllegalArgumentException("Nicknames list is null");
        if (nicknames.isEmpty()) throw new IllegalArgumentException("Nicknames list is empty");
        if (nicknames.size() > 5 || nicknames.size() < 2)
            throw new IllegalArgumentException("Nicknames list size should be between 2 and 5");

        int numOfPlayers = nicknames.size();
        List<Card> cards = new ArrayList<>();
        List<ModifierBuildingCard> modifierCards;
        ObjectMapper mapper = new ObjectMapper();

        //loads normal cards
        InputStream input = getClass().getResourceAsStream(JSON_PATH + "cards.json");
        Map<String, List<Card>> cardMap = mapper.readValue(input, new TypeReference<Map<String, List<Card>>>() {
        });
        for (int i = 2; i <= numOfPlayers; i++) {
            cards.addAll(cardMap.get(String.valueOf(i)));
        }

        //loads modifierBuildingCards
        input = getClass().getResourceAsStream(JSON_PATH + "modifierCards.json");
        modifierCards = mapper.readValue(input, new TypeReference<List<ModifierBuildingCard>>() {
        });

        //register modifierBuildingCards in registry
        ModifierBuildingsRegistry registry = new ModifierBuildingsRegistry();
        modifierCards.forEach(registry::register);
        cards.addAll(modifierCards);

        //sorts cards into EnumMaps for GameModel & injects registry when needed (with visitor)
        InstanceSorterCardVisitor sorter = new InstanceSorterCardVisitor(registry);
        cards.forEach(x -> x.accept(sorter));

        //creates players
        ArrayList<Player> players = new ArrayList<Player>();
        for (int i = 0; i < numOfPlayers; i++) {
            players.add(new Player(nicknames.removeFirst(), null, registry, notifier));
        }

        //loads turn order tile
        input = getClass().getResourceAsStream(JSON_PATH + "turnOrderTileConfigs.json");
        Map<String, ArrayList<TileSlot>> turnOrderTileConfig = mapper.readValue(input, new TypeReference<Map<String, ArrayList<TileSlot>>>() {
        });
        TurnOrderTile turnOrderTile = new TurnOrderTile(turnOrderTileConfig.get(String.valueOf(numOfPlayers)));

        //inject registry in each tile (with visitor)
        TileEffectRegistryAssigner registryAssigner = new TileEffectRegistryAssigner(registry);

        turnOrderTile.slots().stream().map(TileSlot::getTileEffect).filter(Objects::nonNull)
                .forEach(effect -> effect.accept(registryAssigner));

        //load offer track
        input = getClass().getResourceAsStream(JSON_PATH + "offerTrackConfigs.json");
        Map<String, ArrayList<TileSlot>> offerTrackConfig = mapper.readValue(input, new TypeReference<Map<String, ArrayList<TileSlot>>>() {
        });
        List<TileSlot> offerTrack = offerTrackConfig.get(String.valueOf(numOfPlayers));
        offerTrack.forEach((slot) -> {
            if (slot.getTileEffect() != null) {
                slot.getTileEffect().accept(registryAssigner);
            }
        });

        //set up Board & GameModel & TurnManager
        TurnManager turnManager = new TurnManager(new ArrayList<>(players), registry, notifier);

        Board board = new Board(turnOrderTile, offerTrack, notifier);

        GameModel model = new GameModel(board,
                sorter.getBuildingCards(),
                sorter.getTribeCards(),
                sorter.getFinalEvents(),
                players,
                turnManager, notifier);

        turnManager.setGameModel(model);

        for (Player player : turnManager.getPlayersOrder()) {
            player.setEnvironment(model);
        }

        return model;
    }

}
