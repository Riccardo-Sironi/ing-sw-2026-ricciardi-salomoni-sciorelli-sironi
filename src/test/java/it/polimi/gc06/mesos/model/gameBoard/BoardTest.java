package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.*;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.EndGameBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.GathererCard;
import it.polimi.gc06.mesos.model.cards.characters.HunterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.PaintingsEvent;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;

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

    @BeforeEach
    void setUp() {
        board = new Board(mock(ModifierBuildingCard.class));
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
                deck.add(new RitualEvent(era, 1, 0, null, null));
            }
            Collections.shuffle(deck);
            tribeDecks.put(era, deck);
        }

        // setup building decks
        buildingDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            ArrayList<BuildingCard> deck = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                deck.add(new EndGameBuildingCard());
            }
            buildingDecks.put(era, deck);
        }

        // final events
        finalEvents = new EventCard[]{new SustenanceEvent(Era.ERA_III, 1, null, null, null),
                new RitualEvent(Era.ERA_III, 1, 0, null, null)};

        when(modelMock.getPlayers()).thenReturn(players);
        when(modelMock.getTribeCardsDeck()).thenReturn(tribeDecks);
        when(modelMock.getBuildingCardsDecks()).thenReturn(buildingDecks);
        when(modelMock.getFinalEventCards()).thenReturn(finalEvents);
    }

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

    @Test
    void testInitBoardSuccess() {
        assertDoesNotThrow(() -> board.initBoard(modelMock));

        assertEquals(players.size() + 1, board.getBottomRow().size(),
                "La bottom row dovrebbe avere n+1 carte");

        assertEquals(players.size() + 4, board.getTopRow().size(),
                "La top row dovrebbe avere n+4 carte");

        assertNotNull(board.getTurnOrderTile(), "La TurnOrderTile non dovrebbe essere null");

        assertFalse(board.getTopBuildings().isEmpty(), "Top buildings space should have cards");
        assertTrue(board.getBottomBuildings().isEmpty(), "Bottom buildings space should be empty at initialization");

        assertEquals(board.getBuildingsDecks().size(), Era.values().length, "There should be a building deck for each era");
        assertTrue(board.getBuildingsDecks().get(board.getCurrentEra()).isEmpty(), "There should be a building deck for each era");

        assertFalse(board.getOfferTrack().isEmpty());

        assertEquals(Era.ERA_I, board.getCurrentEra());

        assertTrue(board.isOfferTrackEmpty(), "Offer track should be empty after initialization");
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
        board = new Board(mock(ModifierBuildingCard.class));
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
                board.getBottomRow().add(new SustenanceEvent(Era.ERA_I, 1, null, null, null));
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
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2, null, null, null));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1, null, null));
        board.getBottomRow().addLast(new PaintingsEvent(Era.ERA_I, 2, 1, 1, null));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1, null, null));

        ArrayList<EventCard> eventCards = board.cleanBottomRow();

        assertInstanceOf(SustenanceEvent.class, eventCards.getLast(), "The last event card should be the SustenanceEvent");
    }

    @Test
    void testCleanBottomRowCorrectOrderMultipleSustenanceEvent() {
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2, null, null, null));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1, null, null));
        board.getBottomRow().addLast(new SustenanceEvent(Era.ERA_I, 2, null, null, null));
        board.getBottomRow().addLast(new PaintingsEvent(Era.ERA_I, 2, 1, 1, null));
        board.getBottomRow().addLast(new RitualEvent(Era.ERA_I, 2, 1, null, null));

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
        board.initBoard(modelMock);
        assertFalse(board.getTopBuildings().isEmpty(),
                "top buildings space should be populated at initialization, so it should not be empty");
    }

    @Test
    void testPopulateTopBuildingsThrowExceptionCurrentEraDeckNull() {
        // we are missing the initialization of the board, which is the situation in which this method is expected to be called, so the current era deck will be empty
        assertThrows(IllegalStateException.class, () -> board.populateTopBuildings());
    }
}
