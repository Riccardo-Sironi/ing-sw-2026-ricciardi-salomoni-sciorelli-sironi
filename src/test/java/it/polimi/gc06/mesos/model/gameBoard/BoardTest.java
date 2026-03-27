package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.*;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.GathererCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
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

        // 1. Setup Giocatori (minimo 2 per passare i controlli di initBoard)
        players = new ArrayList<>();
        players.add(mock(Player.class));
        players.add(mock(Player.class));

        // 2. Setup Decks Tribù (almeno una carta per Era I per la bottom row)
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

        // 3. Setup Decks Edifici
        buildingDecks = new EnumMap<>(Era.class);
        for (Era era : Era.values()) {
            ArrayList<BuildingCard> deck = new ArrayList<>();
            for (int i = 0; i < 99; i++) {
                deck.add(mock(BuildingCard.class));
            }
            buildingDecks.put(era, deck);
        }

        // 4. Setup Eventi Finali
        finalEvents = new EventCard[]{mock(EventCard.class), mock(EventCard.class)};

        // Istruiamo il mock a restituire questi oggetti
        when(modelMock.getPlayers()).thenReturn(players);
        when(modelMock.getTribeCardsDeck()).thenReturn(tribeDecks);
        when(modelMock.getBuildingCardsDecks()).thenReturn(buildingDecks);
        when(modelMock.getFinalEventCards()).thenReturn(finalEvents);
    }

    @Test
    void testInitBoardSuccess() {
        // Esecuzione
        assertDoesNotThrow(() -> board.initBoard(modelMock));

        // Verifiche post-inizializzazione

        assertEquals(players.size() + 1, board.getBottomRow().size(),
                "La bottom row dovrebbe avere n+1 carte");

        assertEquals(players.size() + 4, board.getTopRow().size(),
                "La top row dovrebbe avere n+4 carte");

        assertNotNull(board.getTurnOrderTile(), "La TurnOrderTile non dovrebbe essere null");

        assertFalse(board.getTopBuildings().isEmpty(), "Top buildings space should have cards");
        assertTrue(board.getBottomBuildings().isEmpty(), "Bottom buildings space should be empty at initialization");

        // Verifichiamo che l'Offer Track sia stata popolata (es. per 2 giocatori)
        assertFalse(board.getOfferTrack().isEmpty());

        // Verifichiamo che l'era iniziale sia ERA_I
        assertEquals(Era.ERA_I, board.getCurrentEra());
    }

    @Test
    void testInitBoardThrowsExceptionWhenModelIsNull() {
        assertThrows(IllegalArgumentException.class, () -> board.initBoard(null));
    }

    @Test
    void testInitBoardThrowsExceptionWhenPlayersInsufficientOrMoreThenExpected() {
        // Setup con un solo giocatore
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
        // Setup con decks di tribù null
        when(modelMock.getTribeCardsDeck()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingCardsDecksNull() {
        // Setup con decks di edifici null
        when(modelMock.getBuildingCardsDecks()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeCardsDeckEmpty() {
        // Setup con decks di tribù vuoti
        when(modelMock.getTribeCardsDeck()).thenReturn(new EnumMap<>(Era.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingCardsDecksEmpty() {
        // Setup con decks di edifici vuoti
        when(modelMock.getBuildingCardsDecks()).thenReturn(new EnumMap<>(Era.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeDecksInsufficient() {
        // Setup con decks di tribù vuoti
        for (Era era : Era.values()) {
            tribeDecks.put(era, new ArrayList<>());
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenTribeDecksNull() {
        // Setup con decks di tribù null
        for (Era era : Era.values()) {
            tribeDecks.put(era, null);
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingDecksInsufficient() {
        // Setup con decks di edifici vuoti
        for (Era era : Era.values()) {
            buildingDecks.put(era, new ArrayList<>());
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenBuildingDecksNull() {
        // Setup con decks di edifici null
        for (Era era : Era.values()) {
            buildingDecks.put(era, null);
        }

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardThrowsExceptionWhenFinalEventsInsufficient() {
        // Setup con eventi finali nulli
        when(modelMock.getFinalEventCards()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));

        // Setup con eventi finali insufficienti
        when(modelMock.getFinalEventCards()).thenReturn(new EventCard[]{mock(EventCard.class)});

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }

    @Test
    void testInitBoardOfferTrackInit() {
        for (int i = 2; i < 6; i++) {
            board = new Board(mock(ModifierBuildingCard.class));
            board.getBottomRow().clear();
            board.getTopRow().clear();
            players.clear();
            for (int j = 0; j < i; j++) {
                players.add(mock(Player.class));
            }

            when(modelMock.getPlayers()).thenReturn(players);

            board.initBoard(modelMock);

            switch (players.size()) {
                case 2:
                    assertEquals(4, board.getOfferTrack().size(), "for 2 players, the Offer Track should have 4 tiles");
                    break;
                case 3:
                    assertEquals(5, board.getOfferTrack().size(), "for 3 players, the Offer Track should have 5 tiles");
                    break;
                case 4:
                    assertEquals(6, board.getOfferTrack().size(), "for 4 players, the Offer Track should have 6 tiles");
                    break;
                case 5:
                    assertEquals(7, board.getOfferTrack().size(), "for 5 players, the Offer Track should have 7 tiles");
                    break;

                default:
                    fail("Numero di giocatori non previsto per il test");
            }
        }
    }
}
