package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import it.polimi.gc06.mesos.model.cards.characters.InventionIcon;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import it.polimi.gc06.mesos.model.gameBoard.TurnOrderTile;
import it.polimi.gc06.mesos.model.gameTurnManager.DrawObserver;
import it.polimi.gc06.mesos.model.gameTurnManager.Phase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameModelTest {

    @Mock
    private Board boardMock;
    @Mock
    private TurnManager turnManagerMock;
    @Mock
    private TurnOrderTile turnOrderTileMock;

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
        notifier = mock(DTONotifier.class);

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

        when(p1.getNickname()).thenReturn("P1");
        when(p2.getNickname()).thenReturn("P2");
        when(p3.getNickname()).thenReturn("P3");
        when(p4.getNickname()).thenReturn("P4");
        when(p5.getNickname()).thenReturn("P5");
        when(turnManagerMock.getActivePlayer()).thenReturn(p1);

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

        EventCard e1 = mock(EventCard.class);
        when(e1.isLastToBeResolved()).thenReturn(true);
        when(e1.getEra()).thenReturn(Era.ERA_I);

        EventCard e2 = mock(EventCard.class);
        when(e2.isLastToBeResolved()).thenReturn(false);
        when(e2.getEra()).thenReturn(Era.ERA_II);

        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>(List.of(e1)), new ArrayList<>(List.of(e2)));
        when(turnManagerMock.getPlayersOrder()).thenReturn(players);
        when(boardMock.getBottomRow()).thenReturn(new ArrayList<>());

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);
        model.endGame();

        verify(p1).addPrestigeTokens(2);
        verify(p1).addPrestigeTokens(4);
        verify(p1).addPrestigeTokens(10);
        verify(p1).addPrestigeTokens(5);
        verify(e1, times(3)).resolveEvent(any(Player.class));
        verify(e2, times(3)).resolveEvent(any(Player.class));

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

        when(p1.getNickname()).thenReturn("P1");
        when(p2.getNickname()).thenReturn("P2");
        when(p3.getNickname()).thenReturn("P3");
        when(p4.getNickname()).thenReturn("P4");
        when(p5.getNickname()).thenReturn("P5");
        when(turnManagerMock.getActivePlayer()).thenReturn(p1);

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

    @Test
    void testGetLeaderboard_ThrowsException() {
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);
        assertThrows(IllegalStateException.class, model::getLeaderboard);
    }

    @Test
    void testIsFinishedAndGetLeaderboard() {
        Player p1 = mock(Player.class);
        when(p1.getNickname()).thenReturn("P1");
        when(p1.getPrestigeTokens()).thenReturn(10);
        when(p1.getFoodTokens()).thenReturn(5);
        players.add(p1);

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);
        assertFalse(model.isFinished());

        when(boardMock.cleanBottomRow()).thenReturn(new ArrayList<>());
        when(boardMock.getBottomRow()).thenReturn(new ArrayList<>());
        when(turnManagerMock.getPlayersOrder()).thenReturn(players);

        model.endGame();

        assertTrue(model.isFinished());
        assertNotNull(model.getLeaderboard());
        assertEquals(1, model.getLeaderboard().getScores().size());
    }

    @Test
    void testSetNotifier() {
        Player p1 = mock(Player.class);
        players.add(p1);
        DTONotifier newNotifier = mock(DTONotifier.class);
        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        model.setNotifier(newNotifier);

        verify(boardMock).setNotifier(newNotifier);
        verify(turnManagerMock).setNotifier(newNotifier);
        verify(p1).setNotifier(newNotifier);
    }

    @Test
    void testSaveAndGetSnapshot() {
        Player p1 = mock(Player.class);
        when(p1.getNickname()).thenReturn("P1");
        when(p1.getPlayerColor()).thenReturn(Color.TURQUOISE);
        EnumMap<CharacterType, ArrayList<CharacterCard>> cDeck = new EnumMap<>(CharacterType.class);
        when(p1.getCharacterDeck()).thenReturn(cDeck);
        when(p1.getBuildingCards()).thenReturn(new ArrayList<>());
        when(p1.hasSetBuildingCard()).thenReturn(true);
        when(p1.getCharactersSets()).thenReturn(new EnumMap<>(CharacterType.class));
        when(p1.hasPairBuildingCard()).thenReturn(true);
        when(p1.getInventorPairs()).thenReturn(new EnumMap<>(InventionIcon.class));

        Player p2 = mock(Player.class);
        when(p2.getNickname()).thenReturn("P2");
        when(p2.getPlayerColor()).thenReturn(Color.ORANGE);
        when(p2.getCharacterDeck()).thenReturn(cDeck);
        when(p2.getBuildingCards()).thenReturn(new ArrayList<>());
        when(p2.hasSetBuildingCard()).thenReturn(false);
        when(p2.hasPairBuildingCard()).thenReturn(false);

        players.addAll(List.of(p1, p2));

        when(turnOrderTileMock.slots()).thenReturn(new ArrayList<>());
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);
        when(boardMock.getOfferTrack()).thenReturn(new ArrayList<>());
        when(boardMock.getTopRow()).thenReturn(new ArrayList<>());
        when(boardMock.getBottomRow()).thenReturn(new ArrayList<>());
        when(boardMock.getTopBuildings()).thenReturn(new ArrayList<>());
        when(boardMock.getBottomBuildings()).thenReturn(new ArrayList<>());
        when(boardMock.getBuildingsDecks()).thenReturn(new EnumMap<>(Era.class));
        when(boardMock.getCurrentEra()).thenReturn(Era.ERA_I);

        when(turnManagerMock.getRound()).thenReturn(1);
        when(turnManagerMock.getActivePlayerIndex()).thenReturn(0);
        when(turnManagerMock.getPlayersOrder()).thenReturn(players);

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertNull(model.getLatestSnapshot());
        model.saveSnapshot();
        assertNotNull(model.getLatestSnapshot());
    }

    @Test
    void testSendResumeInfo() throws Exception {
        Player p1 = mock(Player.class);
        when(p1.getNickname()).thenReturn("P1");
        when(p1.getPlayerColor()).thenReturn(Color.TURQUOISE);

        Player p2 = mock(Player.class);
        when(p2.getNickname()).thenReturn("P2");
        when(p2.getPlayerColor()).thenReturn(Color.ORANGE);

        EnumMap<CharacterType, ArrayList<CharacterCard>> cDeck = new EnumMap<>(CharacterType.class);
        for (CharacterType ct : CharacterType.values()) {
            cDeck.put(ct, new ArrayList<>());
        }
        when(p1.getCharacterDeck()).thenReturn(cDeck);
        when(p1.getBuildingCards()).thenReturn(new ArrayList<>());
        when(p2.getCharacterDeck()).thenReturn(cDeck);
        when(p2.getBuildingCards()).thenReturn(new ArrayList<>());

        players.addAll(List.of(p1, p2));

        when(boardMock.getCurrentEra()).thenReturn(Era.ERA_I);
        when(turnManagerMock.getRound()).thenReturn(1);
        Phase phaseMock = mock(Phase.class);
        when(turnManagerMock.getPhase()).thenReturn(phaseMock);
        when(turnManagerMock.getActivePlayer()).thenReturn(p1);
        when(phaseMock.toString()).thenReturn("Phase1");

        when(phaseMock.checkForRightToSkip(p1, boardMock)).thenReturn(true);
        when(phaseMock.checkForRightToSkip(p2, boardMock)).thenThrow(new IllegalGameActionException("Test Exception"));

        TileSlot t1 = mock(TileSlot.class);
        when(t1.getPlayer()).thenReturn(p1);
        when(t1.getTileEffect()).thenReturn(mock(TileEffect.class));
        TileSlot t2 = mock(TileSlot.class);
        when(t2.getPlayer()).thenReturn(null);
        TileSlot t5 = mock(TileSlot.class);
        when(t5.getPlayer()).thenReturn(p2);
        when(t5.getTileEffect()).thenReturn(mock(TileEffect.class));

        when(turnOrderTileMock.slots()).thenReturn(new ArrayList<>(List.of(t1, t2, t5)));
        when(boardMock.getTurnOrderTile()).thenReturn(turnOrderTileMock);

        TileSlot t3 = mock(TileSlot.class);
        when(t3.getPlayer()).thenReturn(p1);
        when(t3.getTileEffect()).thenReturn(mock(TileEffect.class));
        TileSlot t4 = mock(TileSlot.class);
        when(t4.getPlayer()).thenReturn(null);
        when(t4.getTileEffect()).thenReturn(mock(TileEffect.class));

        when(boardMock.getOfferTrack()).thenReturn(new ArrayList<>(List.of(t3, t4)));

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        assertDoesNotThrow(() -> model.sendResumeInfo());
    }

    @Test
    void testGetStartingStateAsDTO() {
        Player p1 = mock(Player.class);
        when(p1.getNickname()).thenReturn("P1");
        when(p1.getFoodTokens()).thenReturn(2);
        when(p1.getPlayerColor()).thenReturn(Color.TURQUOISE);
        when(p1.getTopDrawNum()).thenReturn(1);
        when(p1.getBottomDrawNum()).thenReturn(2);
        players.add(p1);

        when(turnManagerMock.getActivePlayer()).thenReturn(p1);
        when(turnManagerMock.getPlayersOrder()).thenReturn(players);

        TileSlot t1 = mock(TileSlot.class);
        when(t1.getTileEffect()).thenReturn(mock(TileEffect.class));
        when(boardMock.getOfferTrack()).thenReturn(new ArrayList<>(List.of(t1)));
        when(boardMock.getTopRow()).thenReturn(new ArrayList<>());
        when(boardMock.getTopBuildings()).thenReturn(new ArrayList<>());
        when(boardMock.getBottomRow()).thenReturn(new ArrayList<>());
        when(boardMock.getBottomBuildings()).thenReturn(new ArrayList<>());

        GameModel model = new GameModel(boardMock, buildingCardsDecks, tribeCardsDeck, finalEventCards, players, turnManagerMock, notifier);

        SmallModelEditor dto = model.getStartingStateAsDTO("P1");
        assertNotNull(dto);

        GameModel modelNullDeck = new GameModel(boardMock, buildingCardsDecks, null, finalEventCards, players, turnManagerMock, notifier);
        SmallModelEditor dtoNullDeck = modelNullDeck.getStartingStateAsDTO("P1");
        assertNotNull(dtoNullDeck);
    }
}