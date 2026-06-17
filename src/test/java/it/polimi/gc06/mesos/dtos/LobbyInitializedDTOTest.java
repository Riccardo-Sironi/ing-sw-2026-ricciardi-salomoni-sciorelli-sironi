package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.model.gameBoard.TileEffect;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import it.polimi.gc06.mesos.view.smallModel.TileSlotView;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LobbyInitializedDTOTest {

    private LobbyInitializedDTO dto;
    private final String nickname = "PlayerOne";
    private final ArrayList<String> playersOrder = new ArrayList<>(List.of("PlayerOne", "PlayerTwo"));
    private final Map<String, Color> colorMap = Map.of("PlayerOne", Color.YELLOW, "PlayerTwo", Color.PURPLE);
    private final Map<String, Integer> foodMap = Map.of("PlayerOne", 5, "PlayerTwo", 3);

    private ArrayList<Card> topRow;
    private ArrayList<Card> topBuildings;
    private ArrayList<Card> bottomRow;
    private ArrayList<Card> bottomBuildings;
    private ArrayList<TileEffect> tileEffects;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting LobbyInitializedDTOTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending LobbyInitializedDTOTest ---");
    }

    @BeforeEach
    void setUp() {
        topRow = new ArrayList<>(List.of(mock(Card.class)));
        topBuildings = new ArrayList<>(List.of(mock(Card.class)));
        bottomRow = new ArrayList<>(List.of(mock(Card.class)));
        bottomBuildings = new ArrayList<>(List.of(mock(Card.class)));
        tileEffects = new ArrayList<>(List.of(mock(TileEffect.class)));

        dto = new LobbyInitializedDTO(
                nickname, playersOrder, colorMap, foodMap,
                topRow, topBuildings, bottomRow, bottomBuildings,
                true, tileEffects, 2, 1, 50
        );
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @Test
    void testSequenceNumber() {
        assertNull(dto.getSequenceNumber());
        dto.setSequenceNumber(1);
        assertEquals(1, dto.getSequenceNumber());
    }

    @Test
    void testEdit() {
        SmallModel smallModelMock = mock(SmallModel.class);
        PlayerView localPlayerMock = mock(PlayerView.class);

        ArrayList<Card> smTopRow = new ArrayList<>();
        ArrayList<Card> smTopBuildings = new ArrayList<>();
        ArrayList<Card> smBottomRow = new ArrayList<>();
        ArrayList<Card> smBottomBuildings = new ArrayList<>();
        ArrayList<TileSlotView> smOfferTrack = new ArrayList<>();
        ArrayList<PlayerView> smTurnOrderTile = new ArrayList<>();
        ArrayList<PlayerView> smOpponents = new ArrayList<>();

        when(smallModelMock.getPlayer()).thenReturn(localPlayerMock);
        when(smallModelMock.getTopRow()).thenReturn(smTopRow);
        when(smallModelMock.getTopBuildings()).thenReturn(smTopBuildings);
        when(smallModelMock.getBottomRow()).thenReturn(smBottomRow);
        when(smallModelMock.getBottomBuildings()).thenReturn(smBottomBuildings);
        when(smallModelMock.getOfferTrack()).thenReturn(smOfferTrack);
        when(smallModelMock.getTurnOrderTile()).thenReturn(smTurnOrderTile);
        when(smallModelMock.getOpponents()).thenReturn(smOpponents);

        dto.edit(smallModelMock);

        verify(smallModelMock).setEra(Era.ERA_I);
        verify(smallModelMock).setPhase(anyString());
        verify(smallModelMock).setRound(1);
        verify(smallModelMock).setTribeDeckSize(50);
        verify(smallModelMock).setTopDrawNum(2);
        verify(smallModelMock).setBottomDrawNum(1);
        verify(smallModelMock).setCanSkip(false);
        verify(smallModelMock).setPlayer(nickname, Color.YELLOW);
        verify(smallModelMock).setActive(true);

        assertEquals(1, smTopRow.size());
        assertEquals(1, smTopBuildings.size());
        assertEquals(1, smBottomRow.size());
        assertEquals(1, smBottomBuildings.size());
        assertEquals(1, smOfferTrack.size());

        verify(localPlayerMock).setNumFood(5);
        verify(localPlayerMock).setNumPrestige(0);

        assertEquals(2, smTurnOrderTile.size());

        ArgumentCaptor<PlayerView> opponentCaptor = ArgumentCaptor.forClass(PlayerView.class);
        verify(smallModelMock, times(1)).addOpponent(opponentCaptor.capture());

        PlayerView addedOpponent = opponentCaptor.getValue();
        assertEquals("PlayerTwo", addedOpponent.getNickname());
    }

    @Test
    void testAccept() {
        DTOVisitor visitorMock = mock(DTOVisitor.class);
        dto.accept(visitorMock);
        verify(visitorMock, times(1)).visit(dto);
    }
}