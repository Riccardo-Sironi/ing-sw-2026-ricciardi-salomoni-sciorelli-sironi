package it.polimi.gc06.mesos.model.cards.buildings;


import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class EndGameBuildingFunctionTest {
    private Player playerMock;
    private EnumMap<CharacterType, List<CharacterCard>> characterDeck;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting PlayerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending PlayerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {
        playerMock = mock(Player.class);
        characterDeck = new EnumMap<>(CharacterType.class);

        for (CharacterType type : CharacterType.values()) {
            characterDeck.put(type, new ArrayList<>());
        }

        doReturn(characterDeck).when(playerMock).getCharacterDeck();

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testCountArtist(){
        characterDeck.get(CharacterType.ARTIST).add(mock(CharacterCard.class));
        characterDeck.get(CharacterType.ARTIST).add(mock(CharacterCard.class));

        assertEquals(2, characterDeck.get(CharacterType.ARTIST).size());
        assertEquals(8, EndGameBuildingFunction.COUNT_ARTISTS.applyAsInt(playerMock), "COUNT_ARTISTS should return 8 when there are 2 ARTIST cards (2 * 4)");
    }

    @Test
    void testCountBuilders(){
        characterDeck.get(CharacterType.BUILDER).add(mock(CharacterCard.class));
        characterDeck.get(CharacterType.BUILDER).add(mock(CharacterCard.class));
        characterDeck.get(CharacterType.BUILDER).add(mock(CharacterCard.class));

        assertEquals(3, characterDeck.get(CharacterType.BUILDER).size());
        assertEquals(12, EndGameBuildingFunction.COUNT_BUILDERS.applyAsInt(playerMock), "COUNT_BUILDERS should return 12 when there are 3 BUILDER cards (3 * 4)");
    }

    @Test
    void testCountGatherers(){
        characterDeck.get(CharacterType.GATHERER).add(mock(CharacterCard.class));

        assertEquals(1, characterDeck.get(CharacterType.GATHERER).size());
        assertEquals(4, EndGameBuildingFunction.COUNT_GATHERERS.applyAsInt(playerMock), "COUNT_GATHERERS should return 4 when there is 1 GATHERER card (1 * 4)");
    }

    @Test
    void testCountHunters(){
        characterDeck.get(CharacterType.HUNTER).add(mock(CharacterCard.class));
        characterDeck.get(CharacterType.HUNTER).add(mock(CharacterCard.class));

        assertEquals(2, characterDeck.get(CharacterType.HUNTER).size());
        assertEquals(6, EndGameBuildingFunction.COUNT_HUNTERS.applyAsInt(playerMock), "COUNT_HUNTERS should return 6 when there are 2 HUNTER cards (2 * 3)");
    }

    @Test
    void testCountInventors(){
        characterDeck.get(CharacterType.INVENTOR).add(mock(CharacterCard.class));
        characterDeck.get(CharacterType.INVENTOR).add(mock(CharacterCard.class));

        assertEquals(2, characterDeck.get(CharacterType.INVENTOR).size());
        assertEquals(4, EndGameBuildingFunction.COUNT_INVENTORS.applyAsInt(playerMock), "COUNT_INVENTORS should return 4 when there are 2 INVENTOR cards (2 * 2)");
    }

    @Test
    void testCountShamans(){
        characterDeck.get(CharacterType.SHAMAN).add(mock(CharacterCard.class));

        assertEquals(1, characterDeck.get(CharacterType.SHAMAN).size());
        assertEquals(4, EndGameBuildingFunction.COUNT_SHAMANS.applyAsInt(playerMock), "COUNT_SHAMANS should return 4 when there is 1 SHAMAN card (1 * 4)");
    }

    @Test
    void testCountSets(){
        for (CharacterType type : CharacterType.values()) {
            characterDeck.get(type).add(mock(CharacterCard.class));
            characterDeck.get(type).add(mock(CharacterCard.class));
        }

        characterDeck.get(CharacterType.ARTIST).clear();
        characterDeck.get(CharacterType.ARTIST).add(mock(CharacterCard.class));

        assertEquals(6, EndGameBuildingFunction.COUNT_SETS.applyAsInt(playerMock), "COUNT_SETS should return 6 when the minimum number of cards in any character type is 1 (1 * 6)");
    }

    @Test
    void testCountSetsEmpty(){
        assertEquals(0, EndGameBuildingFunction.COUNT_SETS.applyAsInt(playerMock), "COUNT_SETS should return 0 when there are no character cards (0 * 6)");
    }

    @Test
    void testSetDoubleBuilders(){
        when(playerMock.getBuildersPrestige()).thenReturn(15);
        assertEquals(15, EndGameBuildingFunction.DOUBLE_BUILDERS.applyAsInt(playerMock), "DOUBLE_BUILDERS should return the player's builders prestige (15)");
    }

    @Test
    void test25Prestige(){
        assertEquals(25, EndGameBuildingFunction.FIXED_25.applyAsInt(playerMock), "FIXED_25 should always return 25");
    }

    @Test
    void testEnnumValuesAndValueOf(){
        String name = EndGameBuildingFunction.FIXED_25.name();

        assertEquals(EndGameBuildingFunction.FIXED_25, EndGameBuildingFunction.valueOf(name), "valueOf should return the correct enum constant for a valid name");
        assertEquals(9, EndGameBuildingFunction.values().length, "values should return an array of all enum constants with length 9");
    }

}