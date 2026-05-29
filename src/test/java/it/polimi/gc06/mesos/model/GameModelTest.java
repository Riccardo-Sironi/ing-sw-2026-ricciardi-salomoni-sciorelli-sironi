package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.TurnManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameModelTest {

    @Mock private Board boardMock;
    @Mock private TurnManager turnManagerMock;
    @Mock private TurnOrderTile turnOrderTileMock;

    private EnumMap<Era, ArrayList<BuildingCard>> buildingCardsDecks;
    private EnumMap<Era, ArrayList<TribeCard>> tribeCardsDeck;
    private EventCard[] finalEventCards;
    private ArrayList<Player> players;
    private DTONotifier notifier;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting GameModelTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending GameModelTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        buildingCardsDecks = new EnumMap<>(Era.class);
        tribeCardsDeck = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            buildingCardsDecks.put(era, new ArrayList<>());
            tribeCardsDeck.put(era, new ArrayList<>());
        }
        finalEventCards = new EventCard[2];
        players = new ArrayList<>();
        notifier = new DTONotifier();

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testStartGame_PlayersCountLessThanTwo() {
        players.add(mock(Player.class));
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertThrows(IllegalStateException.class, model::startGame);
    }

    @Test
    void testStartGame_PlayersCountMoreThanFive() {
        for (int i = 0; i < 6; i++) {
            players.add(mock(Player.class));
        }
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertThrows(IllegalStateException.class, model::startGame);
    }

    @Test
    void testStartGame_BoardInitThrowsIllegalStateException() {
        players.addAll(List.of(mock(Player.class), mock(Player.class)));
        doThrow(new IllegalStateException("Board Error")).when(boardMock).initBoard(any());
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertThrows(IllegalStateException.class, model::startGame);
    }

    @Test
    void testStartGame_BoardInitThrowsIllegalArgumentException() {
        players.addAll(List.of(mock(Player.class), mock(Player.class)));
        doThrow(new IllegalArgumentException("Arg Error")).when(boardMock).initBoard(any());
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertThrows(IllegalStateException.class, model::startGame);
    }

    @Test
    void testStartGame_Success_AllocatesFoodCorrectlyForFivePlayers() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        Player p4 = mock(Player.class);
        Player p5 = mock(Player.class);

        ArrayList<Player> playersOrder = new ArrayList<>(List.of(p1, p2, p3, p4, p5));
        players.addAll(playersOrder);

        buildingCardsDecks.get(Era.ERA_I).add(mock(BuildingCard.class));
        tribeCardsDeck.get(Era.ERA_I).add(mock(TribeCard.class));

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        when(turnManagerMock.getPlayersOrder()).thenReturn(playersOrder);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        ArrayList<TileSlot> slots = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            slots.add(mock(TileSlot.class));
        }
        when(turnOrderTileMock.slots()).thenReturn(slots);

        model.startGame();

        assertTrue(buildingCardsDecks.isEmpty());
        for (TileSlot slot : slots) {
            verify(slot).setPlayer(any(Player.class));
        }

        verify(playersOrder.get(0)).addFoodTokens(2);
        verify(playersOrder.get(1)).addFoodTokens(3);
        verify(playersOrder.get(2)).addFoodTokens(3);
        verify(playersOrder.get(3)).addFoodTokens(4);
        verify(playersOrder.get(4)).addFoodTokens(4);
    }

    @Test
    void testEndGame_CalculationsAndSorting() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        players.addAll(List.of(p1, p2, p3));

        when(p1.getBuildersPrestige()).thenReturn(2);
        when(p1.getInventorsCounter()).thenReturn(2);
        when(p1.getNumOfIcon()).thenReturn(2);
        when(p1.getArtistsCounter()).thenReturn(3);

        BuildingCard b1 = mock(BuildingCard.class);
        when(b1.getPrestigeGain(p1)).thenReturn(5);
        when(p1.getBuildingCards()).thenReturn(new ArrayList<>(List.of(b1)));

        when(p1.getPrestigeTokens()).thenReturn(10);
        when(p1.getFoodTokens()).thenReturn(5);

        when(p2.getPrestigeTokens()).thenReturn(20);
        when(p2.getFoodTokens()).thenReturn(2);

        when(p3.getPrestigeTokens()).thenReturn(20);
        when(p3.getFoodTokens()).thenReturn(8);

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);
        model.endGame();

        verify(p1).addPrestigeTokens(2);
        verify(p1).addPrestigeTokens(4);
        verify(p1).addPrestigeTokens(10);
        verify(p1).addPrestigeTokens(5);

        assertEquals(p3, model.getPlayers().get(0));
        assertEquals(p2, model.getPlayers().get(1));
        assertEquals(p1, model.getPlayers().get(2));
    }

    @Test
    void testGetStars() {
        Player p1 = mock(Player.class);
        when(p1.getShamanStars()).thenReturn(1);
        Player p2 = mock(Player.class);
        when(p2.getShamanStars()).thenReturn(5);
        Player p3 = mock(Player.class);
        when(p3.getShamanStars()).thenReturn(5);

        players.addAll(List.of(p1, p2, p3));
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertEquals(5, model.getMaxStars());
        assertEquals(1, model.getMinStars());
        assertEquals(2, model.getNumPlayerMaxStars());
    }

    @Test
    void testGetStars_EmptyPlayers() {
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, new ArrayList<>(), turnManagerMock, notifier);

        assertEquals(0, model.getMaxStars());
        assertEquals(0, model.getMinStars());
        assertEquals(0, model.getNumPlayerMaxStars());
    }

    @Test
    void testAddObserver() {
        DrawObserver observer = mock(DrawObserver.class);
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        model.addObserver(observer);

        verify(boardMock).addObserver(observer);
    }

    @Test
    void testGetters() {
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertEquals(boardMock, model.getBoard());
        assertEquals(buildingCardsDecks, model.getBuildingCardsDecks());
        assertEquals(tribeCardsDeck, model.getTribeCardsDeck());
        assertEquals(finalEventCards, model.getFinalEventCards());
        assertEquals(players, model.getPlayers());
        assertEquals(turnManagerMock, model.getTurnManager());
    }

    @Test
    void testStartGame_FoodDistributionForFivePlayers() {
        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);
        Player p4 = mock(Player.class);
        Player p5 = mock(Player.class);
        players.addAll(List.of(p1, p2, p3, p4, p5));

        buildingCardsDecks.get(Era.ERA_I).add(mock(BuildingCard.class));

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        when(turnManagerMock.getPlayersOrder()).thenReturn(players);
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        ArrayList<TileSlot> slots = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            slots.add(mock(TileSlot.class));
        }
        when(turnOrderTileMock.slots()).thenReturn(slots);

        model.startGame();

        verify(players.get(0)).addFoodTokens(2);
        verify(players.get(1)).addFoodTokens(3);
        verify(players.get(2)).addFoodTokens(3);
        verify(players.get(3)).addFoodTokens(4);
        verify(players.get(4)).addFoodTokens(4);
    }
}