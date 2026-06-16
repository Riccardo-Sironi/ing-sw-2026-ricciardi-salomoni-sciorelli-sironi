package it.polimi.gc06.mesos.network.server.matches;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.dtos.snapshots.*;
import it.polimi.gc06.mesos.model.DTONotifier;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardTypifiedVisitor;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverSetBuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffectRegistryAssigner;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import it.polimi.gc06.mesos.network.server.VirtualClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RestoredMatch extends Match {

    private final Map<String, Boolean> previousPlayers;
    private final GameModel restoredModel;

    /**
     * Initializes a new match waiting for players to join.
     *
     * @param matchId       the unique identifier for this match.
     * @param restoredModel the model of the match.
     */
    public RestoredMatch(int matchId, GameModel restoredModel) {
        super(matchId, restoredModel.getPlayers().size());
        previousPlayers = new HashMap<>();
        List<String> players = restoredModel.getPlayers().stream().map(Player::getNickname).toList();
        for (String p : players) previousPlayers.put(p, false);
        this.restoredModel = restoredModel;
    }

    /**
     * Attempts to add a new {@link VirtualClient client} to the match, checking if he was already in the match.
     * If the match reaches its target capacity with this new player, the game will start automatically!
     *
     * @param c the client trying to join
     * @return true only if the player was connected before server crashed.
     * @throws IllegalStateException if the match is full or has already started
     * @throws IOException           if there is an issue establishing the initial game model for the clients
     */
    @Override
    public synchronized boolean addPlayer(VirtualClient c) throws IllegalStateException, IOException {
        if (!previousPlayers.containsKey(c.getNickname())) return false;
        return super.addPlayer(c);
    }

    /**
     * Generates a new game model via the instance manager and starts the internal match executor loop
     * to begin processing player actions.
     */
    @Override
    protected synchronized void start() {
        ArrayList<String> playerNames = players.stream().map(VirtualClient::getNickname).collect(Collectors.toCollection(ArrayList::new));
        DTONotifier notifier = new DTONotifier();
        restoredModel.setNotifier(notifier);

        players.forEach(c -> c.subscribeToNotifier(notifier)); //adds all listeners
        restoredModel.sendResumeInfo();

        controller = new GameController(restoredModel, notifier);
        hasStarted = true;
        players.forEach(c -> c.setController(controller));
        players.forEach(c -> c.setActionQueue(actionQueue));

        // TODO : Manuel look here!
//        players.forEach(c -> playersThreads.add(new Thread(c, c.getNickname())));
//        playersThreads.forEach(Thread::start);

        matchExecutorThread = new Thread(this::matchLoop, "MatchExecutorThread-" + getMatchId());
        matchExecutorThread.start();
    }

    public static GameModel restoreGame(GameSnapshot snapshot, ModifierBuildingsRegistry registry) {

        Map<String, Player> livePlayersMap = new HashMap<>();
        List<Player> orderedPlayers = new ArrayList<>();

        //restore players
        for (PlayerSnapshot pDto : snapshot.players()) {
            Player player = new Player(pDto.nickname(), pDto.color(), registry, null);

            player.forceState(
                    pDto.prestigeTokens(), pDto.foodTokens(), pDto.shamanStars(),
                    pDto.topDrawNum(), pDto.bottomDrawNum(),
                    pDto.characterDeck(), pDto.buildingDeck(),
                    pDto.charactersSets(), pDto.inventorPairs()
            );

            livePlayersMap.put(player.getNickname(), player);
            orderedPlayers.add(player);
        }

        //restore board
        BoardSnapshot bDto = snapshot.board();

        //restores turnOrderTile
        ArrayList<TileSlot> turnOrderSlots = new ArrayList<>();
        TileEffectRegistryAssigner tileRegistryInjector = new TileEffectRegistryAssigner(registry);
        for (TileSlotSnapshot slotDto : bDto.turnOrderTileSlots()) {
            TileSlot slot = new TileSlot();
            slot.setTileEffect(slotDto.tileEffect());
            tileRegistryInjector.visit(slot.getTileEffect()); //injects registry
            if (slotDto.playerNickname() != null) {
                slot.setPlayer(livePlayersMap.get(slotDto.playerNickname()));
            }
            turnOrderSlots.add(slot);
        }
        TurnOrderTile turnOrderTile = new TurnOrderTile(turnOrderSlots);

        //restores offer track
        List<TileSlot> offerTrack = new ArrayList<>();
        for (TileSlotSnapshot slotDto : bDto.offerTrack()) {
            TileSlot slot = new TileSlot();
            slot.setTileEffect(slotDto.tileEffect());
            if (slotDto.playerNickname() != null) {
                slot.setPlayer(livePlayersMap.get(slotDto.playerNickname()));
            }
            offerTrack.add(slot);
        }

        //restores board content
        Board board = new Board(turnOrderTile, offerTrack, null);
        board.forceState(
                bDto.currentEra(), bDto.isEndGame(),
                bDto.topRow(), bDto.bottomRow(),
                bDto.topBuildings(), bDto.bottomBuildings(),
                bDto.buildingsDecks()
        );

        //restores turn manager
        TurnManagerSnapshot tmDto = snapshot.turnManager();
        List<Player> tmPlayersOrder = new ArrayList<>();
        for (String nick : tmDto.playersOrderNicknames()) {
            tmPlayersOrder.add(livePlayersMap.get(nick));
        }

        TurnManager turnManager = new TurnManager(tmPlayersOrder, registry, null);
        turnManager.forceState(tmDto.round(), tmDto.activePlayerIndex(), new PlacingTotemPhase());

        //restores game model tribe deck
        EnumMap<Era, ArrayList<BuildingCard>> buildingDecks = new EnumMap<>(Era.class);

        EnumMap<Era, ArrayList<TribeCard>> tribeDecks = new EnumMap<>(Era.class);
        snapshot.tribeCardsDeck().forEach((era, list) -> tribeDecks.put(era, new ArrayList<>(list)));

        GameModel model = new GameModel(
                board,
                buildingDecks,
                tribeDecks,
                snapshot.finalEventCards().toArray(EventCard[]::new),
                new ArrayList<>(orderedPlayers),
                turnManager,
                null
        );

        turnManager.setGameModel(model);
        for (Player player : orderedPlayers) {
            player.setEnvironment(model);
        }

        //inject registry in event cards
        CardTypifiedVisitor<ModifierBuildingsRegistry> cardRegistryInjector = new CardTypifiedVisitor<>() {
            @Override
            public void visit(EventCard card) {
                card.setRegistry(getResult());
            }
        };
        cardRegistryInjector.setResult(registry);
        model.getTribeCardsDeck().forEach((e, l) -> l.forEach(t -> t.accept(cardRegistryInjector)));
        board.getTopRow().forEach(t -> t.accept(cardRegistryInjector));
        board.getBottomRow().forEach(t -> t.accept(cardRegistryInjector));
        model.getFinalEventCards()[0].accept(cardRegistryInjector);
        model.getFinalEventCards()[1].accept(cardRegistryInjector);

        //subscribe observer building to board
        CardVisitor observerRegistrant = new CardVisitor() {
            public void visit(ObserverSetBuildingCard building) {
                board.addObserver(building);
            }

            public void visit(ObserverPairBuildingCard building) {
                board.addObserver(building);
            }
        };
        for (Player p : orderedPlayers) {
            p.getBuildingCards().forEach(card -> card.accept(observerRegistrant));
        }

        return model;
    }

    @Override
    public void accept(MatchVisitor mv) {
        mv.visit(this);
    }

    /**
     * Previous player getter.
     *
     * @return the {@link List} of player that were in the match before the server crashed.
     */
    public Map<String, Boolean> getPreviousPlayers() {
        return previousPlayers;
    }
}
