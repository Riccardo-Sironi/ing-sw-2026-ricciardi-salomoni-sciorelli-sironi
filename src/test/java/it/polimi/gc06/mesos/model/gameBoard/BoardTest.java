package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.TribeCardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.GathererCard;
import it.polimi.gc06.mesos.model.cards.characters.HunterCard;
import it.polimi.gc06.mesos.model.cards.events.*;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    //da modificare in base a necessità, sono stati forniti degli esempi di utilizzo
    private void initBoardTestBlueprint() {

        final int numOfDummyBuildings = 21;
        final int numOfDummyCharacters = 84;
        final int numOfDummyEvents = 10;


        Board board = new Board();

        EnumMap<Era, ArrayList<BuildingCard>> buildingDeck = new EnumMap<Era, ArrayList<BuildingCard>>(Era.class);
        //aggiunta building cards
        BuildingCard dummyBuilding = new ModifierBuildingCard(null,0,0);
        for(int i = 0; i < numOfDummyBuildings; i++ ) buildingDeck.get(Era.ERA_I).add(dummyBuilding);

        EnumMap<Era, ArrayList<TribeCard>> tribeDeck  = new EnumMap<Era, ArrayList<TribeCard>>(Era.class);;
        //aggiunta tribe cards
        CharacterCard dummyCharacter = new GathererCard(null);
        EventCard dummyEvent = new HuntEvent(null,0,null);
        for(int i = 0; i < numOfDummyCharacters; i++ ) tribeDeck.get(Era.ERA_I).add(dummyCharacter);
        for(int i = 0; i < numOfDummyEvents; i++ ) tribeDeck.get(Era.ERA_I).add(dummyEvent);
        //Collections.shuffle(tribeDeck.get(Era.ERA_I));

        EventCard[] finalEvents = new EventCard[2];
        finalEvents[0]=new RitualEvent(Era.ERA_III,15,7,null, null);
        finalEvents[1]=new SustenanceEvent(null,3,null,null,null);

        ArrayList<Player> players = new ArrayList<Player>();
        ModifierBuildingCard threeStarCard = new ModifierBuildingCard(Era.ERA_II,6,4);
        //Aggiunta player
        players.add(new Player("BLUE", Color.BLUE,threeStarCard));
        players.add(new Player("RED", Color.RED,threeStarCard));
        players.add(new Player("PURPLE", Color.PURPLE,threeStarCard));
        players.add(new Player("WHITE", Color.WHITE,threeStarCard));
        players.add(new Player("YELLOW", Color.YELLOW,threeStarCard));

        TurnManager turnManager = new TurnManager(players,null,0,null,null);

        GameModel model = new GameModel(board,buildingDeck,tribeDeck,finalEvents,players,turnManager);

        board.initBoard(model);

    }

    @Test
    void initBoard(){
        final int numOfDummyBuildings = 21;
        final int numOfDummyCharacters = 84;
        final int numOfDummyEvents = 10;


        Board board = new Board();

        EnumMap<Era, ArrayList<BuildingCard>> buildingDeck = new EnumMap<Era, ArrayList<BuildingCard>>(Era.class);
        //aggiunta building cards
        BuildingCard dummyBuilding = new ModifierBuildingCard(null,0,0);
        for(int i = 0; i < numOfDummyBuildings; i++ ) buildingDeck.get(Era.ERA_I).add(dummyBuilding);

        EnumMap<Era, ArrayList<TribeCard>> tribeDeck  = new EnumMap<Era, ArrayList<TribeCard>>(Era.class);;
        //aggiunta tribe cards
        CharacterCard dummyCharacter = new GathererCard(null);
        EventCard dummyEvent = new HuntEvent(null,0,null);
        for(int i = 0; i < numOfDummyCharacters; i++ ) tribeDeck.get(Era.ERA_I).add(dummyCharacter);
        for(int i = 0; i < numOfDummyEvents; i++ ) tribeDeck.get(Era.ERA_I).add(dummyEvent);
        //Collections.shuffle(tribeDeck.get(Era.ERA_I));

        EventCard[] finalEvents = new EventCard[2];
        finalEvents[0]=new RitualEvent(Era.ERA_III,15,7,null, null);
        finalEvents[1]=new SustenanceEvent(null,3,null,null,null);

        ArrayList<Player> players = new ArrayList<Player>();
        ModifierBuildingCard threeStarCard = new ModifierBuildingCard(Era.ERA_II,6,4);
        //Aggiunta player
        players.add(new Player("BLUE", Color.BLUE,threeStarCard));
        players.add(new Player("RED", Color.RED,threeStarCard));
        players.add(new Player("PURPLE", Color.PURPLE,threeStarCard));
        players.add(new Player("WHITE", Color.WHITE,threeStarCard));
        players.add(new Player("YELLOW", Color.YELLOW,threeStarCard));

        TurnManager turnManager = new TurnManager(players,null,0,null,null);

        GameModel model = new GameModel(board,buildingDeck,tribeDeck,finalEvents,players,turnManager);

        board.initBoard(model);
    }


}