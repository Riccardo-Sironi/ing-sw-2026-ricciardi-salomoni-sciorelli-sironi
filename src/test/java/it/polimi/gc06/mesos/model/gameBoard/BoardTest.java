package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.gameExceptions.IllegalGameActionException;
import it.polimi.gc06.mesos.model.*;
import it.polimi.gc06.mesos.model.cards.BottomRowInitVisitor;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.buildings.ObserverPairBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import it.polimi.gc06.mesos.model.cards.characters.GathererCard;
import it.polimi.gc06.mesos.model.cards.characters.HunterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BoardTest {

    private Board board;
    private GameModel modelMock;

    // Supporto per i dati finti
    private ArrayList<Player> players;
    private EnumMap<Era, ArrayList<TribeCard>> tribeDecks;
    private EnumMap<Era, ArrayList<BuildingCard>> buildingDecks;
    private EventCard[] finalEvents;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting BottomRowInitVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending BottomRowInitVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        board = new Board(null, null);
        modelMock = mock(GameModel.class);

        // setup players (at least 2)
        players = new ArrayList<>();
        players.add(mock(Player.class));
        players.add(mock(Player.class));

        // setup tribe decks
        tribeDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            ArrayList<TribeCard> deck = new ArrayList<>();
            // Aggiungiamo un numero sufficiente di carte mockate
            for (int i = 0; i < 84; i++) {
                deck.add(new GathererCard(era));
            }
            for (int i = 0; i < 10; i++) {
                deck.add(new RitualEvent(era, 1, 0));
            }
            Collections.shuffle(deck);
            tribeDecks.put(era, deck);
        }

        // setup building decks
        buildingDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            ArrayList<BuildingCard> deck = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                deck.add(new ModifierBuildingCard());
            }
            buildingDecks.put(era, deck);
        }

        // final events
        finalEvents = new EventCard[]{new SustenanceEvent(Era.ERA_III, 1),
                new RitualEvent(Era.ERA_III, 1, 0)};

        when(modelMock.getPlayers()).thenReturn(players);
        when(modelMock.getTribeCardsDeck()).thenReturn(tribeDecks);
        when(modelMock.getBuildingCardsDecks()).thenReturn(buildingDecks);
        when(modelMock.getFinalEventCards()).thenReturn(finalEvents);

        System.out.println("[START] " + testInfo.getDisplayName());
    }



    /*
    @Test
    void testGetOfferTrackPlayerSlotSuccess() {
        board.initBoard(modelMock);

        Player p1 = mock(Player.class);
        int p1Index = 2;
        board.getOfferTrack().get(p1Index).setPlayer(p1);

        assertEquals(board.getOfferTrackPlayerSlot(p1), board.getOfferTrack().get(p1Index),
                "it should return the offer track slot occupied by the player");
    }

    @Test
    void testGetOfferTrackPlayerSlotPlayerNotInTrack() {
        board.initBoard(modelMock);

        Player p1 = mock(Player.class);
        assertNull(board.getOfferTrackPlayerSlot(p1), "it should return null if the player is not in the offer track");
    }

    @Test
    void testIsOfferTrackEmptyReturnFalseWhenPlayerPresent() {
        board.initBoard(modelMock);

        Player p = mock(Player.class);
        board.getOfferTrack().getFirst().setPlayer(p);

        assertFalse(board.isOfferTrackEmpty(), "it should return false if there is at least one player in the offer track");
    }

    */

    @Test
    void testInitBoardSuccess() {
        assertDoesNotThrow(() -> board.initBoard(modelMock));

        assertEquals(players.size() + 1, board.getBottomRow().size(),
                "La bottom row dovrebbe avere n+1 carte");

        assertEquals(players.size() + 4, board.getTopRow().size(),
                "La top row dovrebbe avere n+4 carte");

        assertFalse(board.getTopBuildings().isEmpty(), "Top buildings space should have cards");
        assertTrue(board.getBottomBuildings().isEmpty(), "Bottom buildings space should be empty at initialization");

        assertEquals(board.getBuildingsDecks().size(), Era.values().length, "There should be a building deck for each era");
        assertTrue(board.getBuildingsDecks().get(board.getCurrentEra()).isEmpty(), "There should be a building deck for each era");

        assertEquals(Era.ERA_I, board.getCurrentEra());

        //assertTrue(board.isOfferTrackEmpty(), "Offer track should be empty after initialization");
    }

    @Test
    void testInitBoardThrowsExceptionWhenModelIsNull() {
        assertThrows(IllegalArgumentException.class, () -> board.initBoard(null));
    }

    @Test
    void testInitBoardThrowsExceptionWhenPlayersInsufficientOrMoreThenExpected() {
        players.clear();
        players.add(mock(Player.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));

        players.clear();

        for (int i = 0; i < 6; i++) {
            players.add(mock(Player.class));
        }
        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeCardsDeckNUll() {
        when(modelMock.getTribeCardsDeck()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingCardsDecksNull() {
        when(modelMock.getBuildingCardsDecks()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeCardsDeckEmpty() {
        when(modelMock.getTribeCardsDeck()).thenReturn(new EnumMap<>(Era.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingCardsDecksEmpty() {
        when(modelMock.getBuildingCardsDecks()).thenReturn(new EnumMap<>(Era.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeDecksInsufficient() {
        for (Era era : Era.values()) {
            tribeDecks.put(era, new ArrayList<>());
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeDecksNull() {
        for (Era era : Era.values()) {
            tribeDecks.put(era, null);
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingDecksInsufficient() {
        for (Era era : Era.values()) {
            buildingDecks.put(era, new ArrayList<>());
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingDecksNull() {
        for (Era era : Era.values()) {
            buildingDecks.put(era, null);
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenFinalEventsInsufficient() {
        when(modelMock.getFinalEventCards()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));

        // Setup con eventi finali insufficienti
        when(modelMock.getFinalEventCards()).thenReturn(new EventCard[]{mock(EventCard.class)});

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    /*
    @Test
    void testInitBoardOfferTrackInitParameterized() {
        int[][] mtx = {{2, 4}, {3, 5}, {4, 6}, {5, 7}};

        for (int[] ints : mtx) {
            int numberOfPlayers = ints[0];
            int expectedTrackSize = ints[1];
            testOfferTrackSizeForPlayers(numberOfPlayers, expectedTrackSize);
        }
    }

    private void testOfferTrackSizeForPlayers(int numberOfPlayers, int expectedTrackSize) {
        board = new Board(mock(ModifierBuildingCard.class));

        ArrayList<Player> customPlayers = new ArrayList<>();
        for (int j = 0; j < numberOfPlayers; j++) {
            customPlayers.add(mock(Player.class));
        }
        when(modelMock.getPlayers()).thenReturn(customPlayers);

        board.initBoard(modelMock);

        assertEquals(expectedTrackSize, board.getOfferTrack().size(),
                "Dimensione errata per " + numberOfPlayers + " giocatori");
    }
    */


    @Test
    void populateTopRowCardCount() {
        board.populateTopRow(modelMock);
        assertEquals(modelMock.getPlayers().size() + 4, board.getTopRow().size(),
                "La top row dovrebbe avere n+4 carte dopo la popolazione");
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenModelNull() {
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(null));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenTribeDecksNull() {
        when(modelMock.getTribeCardsDeck()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenCurrentEraDeckNull() {
        EnumMap<Era, ArrayList<TribeCard>> deck = new EnumMap<>(Era.class);
        deck.put(board.getCurrentEra(), null);

        when(modelMock.getTribeCardsDeck()).thenReturn(deck);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenPlayersWrong() {
        ArrayList<Player> wrongPlayers = new ArrayList<>();
        wrongPlayers.add(mock(Player.class));

        when(modelMock.getPlayers()).thenReturn(wrongPlayers);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));

        for (int i = 0; i < 6; i++) {
            wrongPlayers.add(mock(Player.class));
        }
        when(modelMock.getPlayers()).thenReturn(wrongPlayers);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenFinalCardEventWrong() {
        EventCard[] justOneEvent = new EventCard[]{mock(EventCard.class)};

        when(modelMock.getFinalEventCards()).thenReturn(justOneEvent);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));

        when(modelMock.getFinalEventCards()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenCardToDrawIsZero() {
        board.populateTopRow(modelMock);
        assertThrows(IllegalStateException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenTopRowFilled() {
        // we fill the top row with more cards than expected to trigger the exception
        for (int i = 0; i < modelMock.getPlayers().size() + 5; i++) {
            board.getTopRow().add(mock(TribeCard.class));
        }

        assertThrows(IllegalStateException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowEndGame() {
        board.initBoard(modelMock);
        board.getTopRow().clear();

        // add just one card per deck to avoid exceptions and trigger end game condition
        for (Era era : Era.values()) {
            ArrayList<TribeCard> deck = new ArrayList<>();
            deck.add(mock(HunterCard.class));
            modelMock.getTribeCardsDeck().put(era, deck);
        }

        board.populateTopRow(modelMock);

        assertEquals(Era.ERA_III, board.getCurrentEra(), "Current era should be ERA_III");
        assertTrue(board.isEndGame(), "Game should be marked as ended when current era deck is empty and we are in ERA_III");
    }

    @Test
    void testPopulateTopRowEraAdvancement() {
        board.initBoard(modelMock);

        board.getTopRow().clear();
        modelMock.getTribeCardsDeck().put(Era.ERA_I, new ArrayList<>());

        board.populateTopRow(modelMock);
        assertEquals(Era.ERA_II, board.getCurrentEra(), "Era should advance to ERA_II when current era deck is empty");
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenNextEraDeckNull() {
        board.initBoard(modelMock);

        board.getTopRow().clear();

        EnumMap<Era, ArrayList<TribeCard>> deck = new EnumMap<>(Era.class);
        deck.put(board.getCurrentEra(), new ArrayList<>());
        deck.put(Era.ERA_II, null);

        when(modelMock.getTribeCardsDeck()).thenReturn(deck);
        assertThrows(IllegalStateException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowThrowExceptionWhenNextEraDeckEmpty() {
        board.initBoard(modelMock);

        board.getTopRow().clear();

        EnumMap<Era, ArrayList<TribeCard>> deck = new EnumMap<>(Era.class);
        deck.put(board.getCurrentEra(), new ArrayList<>());
        deck.put(Era.ERA_II, new ArrayList<>());

        when(modelMock.getTribeCardsDeck()).thenReturn(deck);
        assertThrows(IllegalStateException.class, () -> board.populateTopRow(modelMock));
    }

    @Test
    void testPopulateTopRowEndGameCondition(){
        for (Era era : Era.values()) {
            board.getBuildingsDecks().get(era).add(mock(BuildingCard.class));
        }

        tribeDecks.get(Era.ERA_I).clear();
        for (int i = 0; i < 6; i++) tribeDecks.get(Era.ERA_I).add(mock(TribeCard.class));

        tribeDecks.get(Era.ERA_II).clear();
        for (int i = 0; i < 6; i++) tribeDecks.get(Era.ERA_II).add(mock(TribeCard.class));

        tribeDecks.get(Era.ERA_III).clear();
        for (int i = 0; i < 2; i++) tribeDecks.get(Era.ERA_III).add(mock(TribeCard.class));

        assertDoesNotThrow(() -> board.populateTopRow(modelMock));
        board.getTopRow().clear();

        assertDoesNotThrow(() -> board.populateTopRow(modelMock));
        board.getTopRow().clear();

        assertDoesNotThrow(() -> board.populateTopRow(modelMock));

        assertTrue(board.isEndGame(), "Game should be marked as ended when current era deck is empty and we are in ERA_III");

        assertEquals(4, board.getTopRow().size(), "Top row should be populated with final event cards when end game condition is met");

        assertEquals(finalEvents[0],  board.getTopRow().get(2), "should have been added the first FINAL EVENT");
        assertEquals(finalEvents[1],  board.getTopRow().get(3), "should have been added the second FINAL EVENT");
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenModelNull() {
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(null));
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenTribeDecksNull() {
        when(modelMock.getTribeCardsDeck()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenTribeDeckEmptyFromKeys() {
        EnumMap<Era, ArrayList<TribeCard>> deck = new EnumMap<>(Era.class);
        when(modelMock.getTribeCardsDeck()).thenReturn(deck);
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenTribeDecksEmpty() {
        EnumMap<Era, ArrayList<TribeCard>> deck = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            deck.put(era, new ArrayList<>());
        }
        when(modelMock.getTribeCardsDeck()).thenReturn(deck);
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testPopulateBottomRowWrongPlayers() {
        ArrayList<Player> wrongPlayers = new ArrayList<>();
        wrongPlayers.add(mock(Player.class));
        when(modelMock.getPlayers()).thenReturn(wrongPlayers);
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(modelMock));

        for (int i = 0; i < 6; i++) {
            wrongPlayers.add(mock(Player.class));
        }
        when(modelMock.getPlayers()).thenReturn(wrongPlayers);
        assertThrows(IllegalArgumentException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenBottomRowNotEmpty() {
        board.getBottomRow().add(mock(TribeCard.class));

        assertThrows(IllegalStateException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testPopulateBottomRowThrowExceptionWhenCurrentEraDeckEmptyWhileFilling() {
        for (Era era : Era.values()) {
            ArrayList<TribeCard> deck = new ArrayList<>();
            deck.add(mock(HunterCard.class));
            modelMock.getTribeCardsDeck().put(era, deck);
        }

        assertThrows(IllegalStateException.class, () -> board.populateBottomRow(modelMock));
    }

    @Test
    void testMoveFromTopToBottomSuccess() {
        board.initBoard(modelMock);

        int cardRemovedFromTopRow = 3;

        for (int i = 0; i < 3; i++) {
            board.getTopRow().removeLast();
        }

        int expectedBottomSize = board.getBottomRow().size() + cardRemovedFromTopRow;
        int expectedTopSize = 0;

        board.moveFromTopToBottom();

        assertEquals(expectedBottomSize, board.getBottomRow().size(),
                "bottom row should have its card plus the ones moved from top row");
        assertEquals(expectedTopSize, board.getTopRow().size(),
                "top row should be empty after moving all cards to bottom row");

    }

    @Test
    void testCleanBottomRowSuccess() {
        int numberOfCards = 5;
        int numberOfEventCards = 2;

        for (int i = 0; i < numberOfCards; i++) {
            if (numberOfEventCards > 0) {
                board.getBottomRow().add(new SustenanceEvent(Era.ERA_I, 1));
                numberOfEventCards--;
            } else {
                board.getBottomRow().add(new HunterCard(Era.ERA_I, true));
            }
        }

        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertEquals(2, eventCards.size(), "Should return all event cards from the bottom row");
        assertTrue(board.getBottomRow().isEmpty(), "Bottom row should be empty after cleaning");
    }

    @Test
    void testCleanBottomRowCorrectOrderOneSustenanceEvent() {
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1));
        board.getBottomRow().addLast(new PaintingsEvent(Era.ERA_I, 2, 1, 1));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1));

        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertInstanceOf(SustenanceEvent.class, eventCards.getLast(), "The last event card should be the SustenanceEvent");
    }

    @Test
    void testCleanBottomRowCorrectOrderMultipleSustenanceEvent() {
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1));
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2));
        board.getBottomRow().addLast(new PaintingsEvent(Era.ERA_I, 2, 1, 1));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1));

        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertInstanceOf(SustenanceEvent.class, eventCards.get(eventCards.size() - 2), "The 'eventCards.size() - 2' event card should be the SustenanceEvent");
        assertInstanceOf(SustenanceEvent.class, eventCards.get(eventCards.size() - 1), "The 'eventCards.size() - 1' event card should be the SustenanceEvent");
    }

    @Test
    void testCleanBottomRowNonEventCardsOnly() {
        board.initBoard(modelMock);

        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertTrue(eventCards.isEmpty(), "Cleaning an already empty bottom row should return an empty list");
    }

    @Test
    void testCleanBottomRowAlreadyEmpty() {
        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertTrue(eventCards.isEmpty(), "Cleaning an already empty bottom row should return an empty list");
    }

    @Test
    void testPopulateTopBuildingsSuccess() {
        assertDoesNotThrow(() -> board.initBoard(modelMock));
        assertFalse(board.getTopBuildings().isEmpty(),
                "top buildings space should be populated at initialization, so it should not be empty");
    }

    @Test
    void testPopulateTopBuildingsThrowExceptionCurrentEraDeckNull() {
        // we are missing the initialization of the board, which is the situation in which this method is expected to be called, so the current era deck will be empty
        assertThrows(IllegalStateException.class, () -> board.populateTopBuildings());
    }

    @Test
    void testPickCardFromTopRowSuccess() {
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));

        HunterCard card = new HunterCard(Era.ERA_I, true);
        board.getTopRow().addFirst(card);

        int initialTopRowSize = board.getTopRow().size();

        assertDoesNotThrow(() -> board.pickCardFromTopRow(p, card));

        assertEquals(initialTopRowSize - 1, board.getTopRow().size(),
                "Picking a card from the top row should decrease its size by one");
        // we know that the card is a HunterCard, so it should be added to the player's character deck
        assertTrue(p.getCharacterDeck().get(CharacterType.HUNTER).contains(card), " The picked card should be added to the player's character deck");
    }

    @Test
    void testPickCardFromTopRowThrowExceptionWhenPlayerNull() {
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromTopRow(null, new HunterCard(Era.ERA_I, true)));
    }

    @Test
    void testPickCardFromTopRowThrowExceptionWhenCardNull() {
        HunterCard card = null;
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromTopRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromTopRowThrowExceptionWhenCardNotInTopRow() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromTopRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromTopRowThrowExceptionWhenCardIsEvent() {
        SustenanceEvent card = new SustenanceEvent(Era.ERA_I, 1);
        board.getTopRow().addFirst(card);

        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromTopRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromTopRowWhenTribeCard() {
        assertDoesNotThrow(() -> board.pickCardFromTopRow(mock(Player.class), mock(TribeCard.class)));
    }

    @Test
    void testPickCardBottomRowSuccess() {
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));

        HunterCard card = new HunterCard(Era.ERA_I, true);
        board.getBottomRow().addFirst(card);

        int initialBottomRowSize = board.getBottomRow().size();

        assertDoesNotThrow(() -> board.pickCardFromBottomRow(p, card));

        assertEquals(initialBottomRowSize - 1, board.getBottomRow().size(),
                "Picking a card from the bottom row should decrease its size by one");
        // we know that the card is a HunterCard, so it should be added to the player's character deck
        assertTrue(p.getCharacterDeck().get(CharacterType.HUNTER).contains(card), "The picked card should be added to the player's character deck");
    }

    @Test
    void testPickCardFromBottomRowThrowExceptionWhenPlayerNull() {
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromBottomRow(null, new HunterCard(Era.ERA_I, true)));
    }

    @Test
    void testPickCardFromBottomRowThrowExceptionWhenCardNull() {
        HunterCard card = null;
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromBottomRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromBottomRowThrowExceptionWhenCardNotInBottomRow() {
        HunterCard card = new HunterCard(Era.ERA_I, true);
        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromBottomRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromBottomRowThrowExceptionWhenCardIsEvent() {
        SustenanceEvent card = new SustenanceEvent(Era.ERA_I, 1);
        board.getBottomRow().addFirst(card);

        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromBottomRow(mock(Player.class), card));
    }

    @Test
    void testPickCardFromBottomRowWhenTribeCard() {
        assertDoesNotThrow(() -> board.pickCardFromBottomRow(mock(Player.class), mock(TribeCard.class)));
    }

    @Test
    void testPickCardFromBottomRowThrowExceptionWhenCardIsEventAndBottomRowHasMultipleCards() {
        SustenanceEvent card = new SustenanceEvent(Era.ERA_I, 1);
        board.getBottomRow().addFirst(card);
        assertDoesNotThrow(() -> board.getBottomRow().addFirst(new HunterCard(Era.ERA_I, true)));

        assertThrows(IllegalArgumentException.class, () -> board.pickCardFromBottomRow(mock(Player.class), card));
    }

    @Test
    void testByBuildingFromTopRowSuccess() {
        int foodTokensBeforePurchase = 20;
        int foodCost = 5;
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        p.addFoodTokens(foodTokensBeforePurchase);
        ModifierBuildingCard card = new ModifierBuildingCard();
        card.setFoodCost(foodCost);

        board.getTopBuildings().addLast(card);

        assertDoesNotThrow(() -> board.buyBuildingFromTopRow(p, card));
        assertTrue(p.getBuildingCards().contains(card), "The bought building should be added to the player's owned buildings");
        assertEquals(foodTokensBeforePurchase - foodCost, p.getFoodTokens(), "The player should have spent food tokens to buy the building");
        assertFalse(board.getTopBuildings().contains(card), "The bought building should be removed from the top buildings space");
    }

    @Test
    void testByBuildingFromTopRowUnsuccess() {
        int foodTokensBeforePurchase = 3;
        int foodCost = 5;
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        p.addFoodTokens(foodTokensBeforePurchase);
        ModifierBuildingCard card = new ModifierBuildingCard();
        card.setFoodCost(foodCost);

        board.getTopBuildings().addLast(card);

        assertThrows(IllegalGameActionException.class, () -> board.buyBuildingFromTopRow(p, card), "Buying a building without enough food tokens should throw an exception");
        assertFalse(p.getBuildingCards().contains(card), "The building should not be added to the player's owned buildings if the purchase was unsuccessful");
        assertEquals(foodTokensBeforePurchase, p.getFoodTokens(), "The player should not have spent any food tokens if the purchase was unsuccessful");
        assertTrue(board.getTopBuildings().contains(card), "The building should remain in the top buildings space if the purchase was unsuccessful");
    }

    @Test
    void testBuyBuildingFromTopRowThrowExceptionWhenPlayerNull() {
        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromTopRow(null, null));
    }

    @Test
    void testBuyBuildingFromTopRowThrowExceptionWhenCardNull() {
        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromTopRow(mock(Player.class), null));
    }

    @Test
    void testBuyBuildingFromTopRowThrowExceptionWhenCardNotInTopBuildings() {
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        ModifierBuildingCard card = new ModifierBuildingCard();

        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromTopRow(p, card));
    }

    @Test
    void testBuyBuildingFromBottomRowSuccess() {
        int foodTokensBeforePurchase = 20;
        int foodCost = 5;
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        p.addFoodTokens(foodTokensBeforePurchase);
        ModifierBuildingCard card = new ModifierBuildingCard();
        card.setFoodCost(foodCost);

        board.getBottomBuildings().addLast(card);

        assertDoesNotThrow(() -> board.buyBuildingFromBottomRow(p, card));
        assertTrue(p.getBuildingCards().contains(card), "The bought building should be added to the player's owned buildings");
        assertEquals(foodTokensBeforePurchase - foodCost, p.getFoodTokens(), "The player should have spent food tokens to buy the building");
        assertFalse(board.getBottomBuildings().contains(card), "The bought building should be removed from the bottom buildings space");
    }

    @Test
    void testBuyBuildingFromBottomRowUnsuccess() {
        int foodTokensBeforePurchase = 3;
        int foodCost = 5;
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        p.addFoodTokens(foodTokensBeforePurchase);
        ModifierBuildingCard card = new ModifierBuildingCard();
        card.setFoodCost(foodCost);

        board.getBottomBuildings().addLast(card);

        assertThrows(IllegalGameActionException.class, () -> board.buyBuildingFromBottomRow(p, card), "Buying a building without enough food tokens should throw an exception");
        assertFalse(p.getBuildingCards().contains(card), "The building should not be added to the player's owned buildings if the purchase was unsuccessful");
        assertEquals(foodTokensBeforePurchase, p.getFoodTokens(), "The player should not have spent any food tokens if the purchase was unsuccessful");
        assertTrue(board.getBottomBuildings().contains(card), "The building should remain in the bottom buildings space if the purchase was unsuccessful");
    }

    @Test
    void testBuyBuildingFromBottomRowThrowExceptionWhenPlayerNull() {
        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromBottomRow(null, null));
    }

    @Test
    void testBuyBuildingFromBottomRowThrowExceptionWhenCardNull() {
        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromBottomRow(mock(Player.class), null));
    }

    @Test
    void testBuyBuildingFromBottomRowThrowExceptionWhenCardNotInBottomBuildings() {
        Player p = new Player("TestPlayer", Color.RED, mock(ModifierBuildingsRegistry.class));
        ModifierBuildingCard card = new ModifierBuildingCard();

        assertThrows(IllegalArgumentException.class, () -> board.buyBuildingFromBottomRow(p, card));
    }

    @Test
    void testAddObserverSuccess() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        assertDoesNotThrow(() -> board.addObserver(observerPairBuildingCard), "Adding a valid observer should not throw an exception");
        assertTrue(board.getObservers().contains(observerPairBuildingCard), "The added observer should be present in the board's observers list");
    }

    @Test
    void testAddObserverThrowExceptionWhenObserverNull() {
        assertThrows(IllegalArgumentException.class, () -> board.addObserver(null), "Adding a null observer should throw an exception");
    }

    @Test
    void testAddObserverWhenObserverAddedPreviously() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        board.addObserver(observerPairBuildingCard);

        assertThrows(IllegalArgumentException.class, () -> board.addObserver(observerPairBuildingCard), "Adding the same observer twice should throw an exception");
    }

    @Test
    void testRemoveObserverSuccess() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        board.addObserver(observerPairBuildingCard);

        assertDoesNotThrow(() -> board.removeObserver(observerPairBuildingCard), "Removing an existing observer should not throw an exception");
        assertFalse(board.getObservers().contains(observerPairBuildingCard), "The removed observer should no longer be present in the board's observers list");
    }

    @Test
    void testRemoveObserverThrowExceptionWhenObserverNull() {
        assertThrows(IllegalArgumentException.class, () -> board.removeObserver(null), "Removing a null observer should throw an exception");
    }

    @Test
    void testRemoveObserverWhenObserverNotAddedPreviously() {
        ObserverPairBuildingCard observerPairBuildingCard = new ObserverPairBuildingCard();
        assertThrows(IllegalArgumentException.class, () -> board.removeObserver(observerPairBuildingCard), "Removing an observer that was never added should throw an exception");
    }

    @Test
    void testIndexGetters() {
        BuildingCard topBuilding = mock(BuildingCard.class);
        BuildingCard bottomBuilding = mock(BuildingCard.class);
        TribeCard topCard = mock(TribeCard.class);
        TribeCard bottomCard = mock(TribeCard.class);

        board.getTopBuildings().add(topBuilding);
        board.getBottomBuildings().add(bottomBuilding);
        board.getTopRow().add(topCard);
        board.getBottomRow().add(bottomCard);

        assertEquals(topBuilding, board.getTopBuildingFromIndex(0), "return the wrong top building");
        assertEquals(bottomBuilding, board.getBottomBuildingFromIndex(0), "return the wrong bottom building");
        assertEquals(topCard, board.getTopCardFromIndex(0), "return the wrong top card");
        assertEquals(bottomCard, board.getBottomCardFromIndex(0), "return the wrong top card");

        assertThrows(IndexOutOfBoundsException.class, () -> board.getTopBuildingFromIndex(1), "Index out of bounds exception");
        assertThrows(IndexOutOfBoundsException.class, () -> board.getBottomBuildingFromIndex(1), "Index out of bounds exception");
        assertThrows(IndexOutOfBoundsException.class, () -> board.getTopCardFromIndex(1), "Index out of bounds exception");
        assertThrows(IndexOutOfBoundsException.class, () -> board.getBottomCardFromIndex(1), "Index out of bounds exception");
    }

    @Test
    void testOfferTrackAndTurnOrderTile() {
        TurnOrderTile mockTurnOrderTile = mock(TurnOrderTile.class);

        TileSlot slot1 = mock(TileSlot.class);
        TileSlot slot2 = mock(TileSlot.class);
        List<TileSlot> mockOfferTrack = new ArrayList<>();
        mockOfferTrack.add(slot1);
        mockOfferTrack.add(slot2);

        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        Board trackBoard = new Board(mockTurnOrderTile, mockOfferTrack);

        assertEquals(mockTurnOrderTile, trackBoard.getTurnOrderTile(), "Turn Order Tile should be the same");
        assertEquals(mockOfferTrack, trackBoard.getOfferTrack(), "Offer Track should be the same");

        when(slot1.getPlayer()).thenReturn(null);
        when(slot2.getPlayer()).thenReturn(null);

        assertTrue(trackBoard.isOfferTrackEmpty(), "Offer track should be empty when all slots are unoccupied");

        assertNull(trackBoard.getOfferTrackPlayerSlot(player1), "should return null if the player isn't in any slot");

        when(slot1.getPlayer()).thenReturn(player1);

        assertFalse(trackBoard.isOfferTrackEmpty(), "Offer track should not be empty when at least one slot is occupied");

        assertEquals(slot1, trackBoard.getOfferTrackPlayerSlot(player1), "Offer track should return player1");

        assertNull(trackBoard.getOfferTrackPlayerSlot(player2), "should return null for another player not in any slot");
    }
}
