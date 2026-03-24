package it.polimi.gc06.mesos.model.gameBoard;

import it.polimi.gc06.mesos.model.*;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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
        board = new Board();
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
            for (int i = 0; i < 99; i++) {
                deck.add(mock(TribeCard.class));
            }
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
        finalEvents = new EventCard[]{ mock(EventCard.class), mock(EventCard.class) };

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

        // La riga inferiore (bottomRow) deve contenere nPlayers + 1 carte
        assertEquals(players.size() + 1, board.getBottomRow().size(),
                "La bottom row dovrebbe avere n+1 carte");

        // Verifichiamo che la TurnOrderTile sia stata creata
        assertNotNull(board.getTurnOrderTile(), "La TurnOrderTile non dovrebbe essere null");

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
    void testInitBoardThrowsExceptionWhenPlayersInsufficient() {
        // Setup con un solo giocatore (illegale secondo i tuoi controlli)
        players.clear();
        players.add(mock(Player.class));

        assertThrows(IllegalArgumentException.class, () -> board.initBoard(modelMock));
    }
}