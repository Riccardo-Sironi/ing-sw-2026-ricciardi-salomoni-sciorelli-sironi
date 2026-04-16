package it.polimi.gc06.mesos.model.gameTurnManager;

import it.polimi.gc06.mesos.gameExceptions.IllegalPhaseActionException;
import it.polimi.gc06.mesos.model.GameModel;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.gameBoard.Board;
import it.polimi.gc06.mesos.model.gameBoard.TileSlot;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class PhaseTest {

    private Phase phase;
    private TurnManager turnManager;
    private Player player;
    private Board board;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PhaseTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PhaseTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {

        phase = new Phase() {
        };
        turnManager = mock(TurnManager.class);
        player = mock(Player.class);
        board = mock(Board.class);

        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }


    @Test
    void testPlaceTotemThrowsException() {
        TileSlot tileSlot = mock(TileSlot.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.placeTotem(turnManager, player, tileSlot, board));

        assertEquals("You can't place a totem in this phase!", exception.getMessage());
    }

    @Test
    void testStartPlayerOfferResolutionThrowsException() {
        TileSlot tileSlot = mock(TileSlot.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.startPlayerOfferResolution(turnManager, player, tileSlot));

        assertEquals("You can't resolve an offer in this phase!", exception.getMessage());
    }

    @Test
    void testResolveEventThrowsException() {
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.resolveEvent(turnManager, board));

        assertEquals("You can't resolve an event in this phase!", exception.getMessage());
    }

    @Test
    void testEndOfRoundThrowsException() {
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.endOfRound(turnManager, board, mock(GameModel.class)));

        assertEquals("You can't end the round in this phase!", exception.getMessage());
    }

    @Test
    void testPickCharacterCardFromTopThrowsException() {
        CharacterCard card = mock(CharacterCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromTop(turnManager, player, card, board));

        assertEquals("You cannot draw yet!", exception.getMessage());
    }

    @Test
    void testPickCharacterCardFromBottomThrowsException() {
        CharacterCard card = mock(CharacterCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromBottom(turnManager, player, card, board));

        assertEquals("You cannot draw yet!", exception.getMessage());
    }

    @Test
    void testPickBuildingCardFromTopThrowsException() {
        BuildingCard card = mock(BuildingCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromTop(turnManager, player, card, board));

        assertEquals("You cannot draw yet!", exception.getMessage());
    }

    @Test
    void testPickBuildingCardFromBottomThrowsException() {
        BuildingCard card = mock(BuildingCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromBottom(turnManager, player, card, board));

        assertEquals("You cannot draw yet!", exception.getMessage());
    }

    @Test
    void testPickEventCardFromTopThrowsException() {
        EventCard card = mock(EventCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromTop(turnManager, player, card, board));

        assertEquals("You can't pick Event Cards during the Offer Resolution Phase!", exception.getMessage());
    }

    @Test
    void testPickEventCardFromBottomThrowsException() {
        EventCard card = mock(EventCard.class);
        IllegalPhaseActionException exception = assertThrows(IllegalPhaseActionException.class, () ->
                phase.pickCardFromBottom(turnManager, player, card, board));

        assertEquals("You can't pick Event Cards during the Offer Resolution Phase!", exception.getMessage());
    }
}

