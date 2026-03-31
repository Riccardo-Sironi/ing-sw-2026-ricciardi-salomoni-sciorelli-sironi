package it.polimi.gc06.mesos.model.InstancesManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import it.polimi.gc06.mesos.model.Color;
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

    private static final String JSON_PATH = "/it/polimi/gc06/mesos/jsons/";

    public GameModel createGame(int numOfPlayers) throws IOException{

        List<Card> cards;
        List<ModifierBuildingCard> modifierCards;
        ObjectMapper mapper = new ObjectMapper();

        //loads normal cards
        InputStream input = getClass().getResourceAsStream(JSON_PATH + "cards.json");
        cards = mapper.readValue(input, new TypeReference<List<Card>>() {});

        //loads modifierBuildingCards
        input = getClass().getResourceAsStream(JSON_PATH + "modifierCards.json");
        modifierCards = mapper.readValue(input, new TypeReference<List<ModifierBuildingCard>>() {});

        //register modifierBuildingCards in registry
        ModifierBuildingsRegistry registry = new ModifierBuildingsRegistry();
        modifierCards.forEach(registry::register);
        cards.addAll(modifierCards);

        //sorts cards into EnumMaps for GameModel & injects registry when needed (with visitor)
        InstanceSorterCardVisitor sorter = new InstanceSorterCardVisitor(registry);
        cards.forEach(x->x.accept(sorter));

        //creates players
        ArrayList<Player> players = new ArrayList<Player>();
        List<Color> colors = Arrays.asList(Color.values());
        for(int i=0; i<numOfPlayers; i++) players.add(new Player("Default",colors.removeLast(),registry));

        //loads turn order tile
        input = getClass().getResourceAsStream(JSON_PATH + "turnOrderTileConfigs.json");
        Map<String,ArrayList<TileSlot>> turnOrderTileConfig = mapper.readValue(input, new TypeReference<Map<String,ArrayList<TileSlot>>>() {});
        TurnOrderTile turnOrderTile = new TurnOrderTile(turnOrderTileConfig.get(String.valueOf(numOfPlayers)));

        //inject registry in each tile (with visitor)
        TileEffectRegistryAssigner registryAssigner = new TileEffectRegistryAssigner(registry);
        turnOrderTile.slots().forEach((slot)->slot.getTileEffect().accept(registryAssigner));
        //eventual null values will be mapped to visit(TileEffect) which does nothing, so it's not a problem

        //load offer track
        input = getClass().getResourceAsStream(JSON_PATH+"offerTrackConfigs.json");
        Map<String,ArrayList<TileSlot>> offerTrackConfig = mapper.readValue(input, new TypeReference<Map<String,ArrayList<TileSlot>>>() {});
        List<TileSlot> offerTrack = offerTrackConfig.get(String.valueOf(numOfPlayers));

        //creates TurnManager
        TurnManager turnManager = new TurnManager(new ArrayList<>(players), registry);

        //set up Board & GameModel
        Board board= new Board(turnOrderTile, offerTrack);
        GameModel model = new GameModel(board,
                sorter.getBuildingCards(),
                sorter.getTribeCards(),
                sorter.getFinalEvents(),
                players,
                turnManager);
        board.initBoard(model);

        return model;
    }

}
