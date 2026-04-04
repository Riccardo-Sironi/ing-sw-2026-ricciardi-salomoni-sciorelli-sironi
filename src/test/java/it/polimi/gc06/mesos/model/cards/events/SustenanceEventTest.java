package it.polimi.gc06.mesos.model.cards.events;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.Player;
import it.polimi.gc06.mesos.model.cards.CardVisitor;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingRegistryKey;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.characters.CharacterCard;
import it.polimi.gc06.mesos.model.cards.characters.CharacterType;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class SustenanceEventTest {

    private SustenanceEvent sustenanceEvent;
    private Player player;
    private ModifierBuildingsRegistry mockRegistry;

    private ModifierBuildingCard mockGathererDiscountCard;
    private ModifierBuildingCard mockArtistDiscountCard;
    private ModifierBuildingCard mockInventorDiscountCard;

    private final int numPrestigeLoss = 2; // Fixed loss per unpaid food

    @BeforeAll
    static void whichTest() {
        System.out.println(">>> Starting SustenanceEventTest <<<");
    }

    @AfterAll
    static void endTest() {
        System.out.println(">>> Ending SustenanceEventTest <<<");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        mockGathererDiscountCard = mock(ModifierBuildingCard.class);
        mockArtistDiscountCard = mock(ModifierBuildingCard.class);
        mockInventorDiscountCard = mock(ModifierBuildingCard.class);

        mockRegistry = mock(ModifierBuildingsRegistry.class);
        when(mockRegistry.get(ModifierBuildingRegistryKey.SUSTENANCE_GATHERER_DISCOUNT)).thenReturn(mockGathererDiscountCard);
        when(mockRegistry.get(ModifierBuildingRegistryKey.SUSTENANCE_ARTIST_DISCOUNT)).thenReturn(mockArtistDiscountCard);
        when(mockRegistry.get(ModifierBuildingRegistryKey.SUSTENANCE_INVENTOR_DISCOUNT)).thenReturn(mockInventorDiscountCard);

        sustenanceEvent = new SustenanceEvent(Era.ERA_I, numPrestigeLoss) {
            @Override
            protected ModifierBuildingsRegistry getRegistry() {
                return mockRegistry;
            }
        };

        player = mock(Player.class);

        // Defaults for player methods
        when(player.getCharacterDeck()).thenReturn(new EnumMap<>(CharacterType.class));
        when(player.getGatherersCounter()).thenReturn(0);
        when(player.getArtistsCounter()).thenReturn(0);
        when(player.getInventorsCounter()).thenReturn(0);
        when(player.getFoodTokens()).thenReturn(0);
        when(player.getBuildingCards()).thenReturn(new ArrayList<>());

        System.out.println("--- [START] " + testInfo.getDisplayName() + " ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("--- [END] " + testInfo.getDisplayName() + " DONE! ---");
    }

    @Test
    void testIsLastToBeResolvedIsTrue() {
        assertTrue(sustenanceEvent.isLastToBeResolved());
    }

    @Test
    void testAcceptCallsCardVisitor() {
        CardVisitor visitor = mock(CardVisitor.class);
        sustenanceEvent.accept(visitor);
        verify(visitor).visit(sustenanceEvent);
    }

    @Test
    void testAccept_With_NullTribeCardVisitor() {
        assertThrows(NullPointerException.class, () -> sustenanceEvent.accept((CardVisitor) null));
    }

    @Test
    void testResolveEvent_0RequiredFood_NoTokensChanged() {
        // totalCharacterCards = 0
        sustenanceEvent.resolveEvent(player);

        verify(player, never()).removeFoodTokens(anyInt());
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    private EnumMap<CharacterType, ArrayList<CharacterCard>> createMockDeck(int size) {
        EnumMap<CharacterType, ArrayList<CharacterCard>> deck = new EnumMap<>(CharacterType.class);
        ArrayList<CharacterCard> cards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            cards.add(mock(CharacterCard.class));
        }
        if (size > 0) {
            deck.put(CharacterType.GATHERER, cards); // Using size as the map size is technically incorrect if it counts keys. player.getCharacterDeck().size() counts the number of map entries, not total cards!
        }
        return deck;
    }

    private EnumMap<CharacterType, ArrayList<CharacterCard>> createMockDeckWithEntries(int numEntries) {
        EnumMap<CharacterType, ArrayList<CharacterCard>> deck = new EnumMap<>(CharacterType.class);
        CharacterType[] types = CharacterType.values();
        for (int i = 0; i < numEntries && i < types.length; i++) {
            deck.put(types[i], new ArrayList<>());
        }
        return deck;
    }


    @Test
    void testResolveEvent_RequiredFoodMatchesGathererDiscount_NoTokensChanged() {
        // 3 characters entries, 1 gatherer (provides default discount of 3)
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(3));
        when(player.getGatherersCounter()).thenReturn(1);

        sustenanceEvent.resolveEvent(player);

        verify(player, never()).removeFoodTokens(anyInt());
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_HasEnoughFood_RemovesFoodNoPrestigeLoss() {
        // 4 character entries, 0 gatherers -> required food = 4
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(4));
        when(player.getFoodTokens()).thenReturn(5);

        sustenanceEvent.resolveEvent(player);

        verify(player).removeFoodTokens(4);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_NotEnoughFood_RemovesAllFoodAndLosesPrestige() {
        // 5 character entries, 0 gatherers -> required food = 5
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(5));
        when(player.getFoodTokens()).thenReturn(2); // Short by 3 food

        sustenanceEvent.resolveEvent(player);

        verify(player).removeFoodTokens(2);
        verify(player).removePrestigeTokens(3 * numPrestigeLoss); // 3 unfed * 2 = 6
    }

    @Test
    void testResolveEvent_NoFoodAtAll_LosesOnlyPrestige() {
        // 2 character entries, required food = 2
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(2));
        when(player.getFoodTokens()).thenReturn(0);

        sustenanceEvent.resolveEvent(player);

        verify(player).removeFoodTokens(0);
        verify(player).removePrestigeTokens(2 * numPrestigeLoss); // 2 unfed * 2 = 4
    }

    @Test
    void testResolveEvent_WithGathererBonusDiscountBuilding() {
        // 4 character entries, 1 gatherer -> base required = 4 - 3 = 1
        // Bonus building -> required = max(1 - 1, 0) = 0
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(4));
        when(player.getGatherersCounter()).thenReturn(1);
        when(player.getFoodTokens()).thenReturn(0);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockGathererDiscountCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        sustenanceEvent.resolveEvent(player);

        verify(player, never()).removeFoodTokens(anyInt());
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_WithArtistDiscountBuilding() {
        // 4 character entries, 0 gatherers -> base required = 4
        // 2 artists with bonus building -> required = 4 - 2 = 2
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(4));
        when(player.getArtistsCounter()).thenReturn(2);
        when(player.getFoodTokens()).thenReturn(0); // Short by 2 food

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockArtistDiscountCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        sustenanceEvent.resolveEvent(player);

        verify(player).removeFoodTokens(0);
        verify(player).removePrestigeTokens(2 * numPrestigeLoss); // 2 unfed * 2 = 4
    }

    @Test
    void testResolveEvent_WithInventorDiscountBuilding() {
        // 5 character entries, 0 gatherers -> base required = 5
        // 3 inventors with bonus building -> required = 5 - 3 = 2
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(5));
        when(player.getInventorsCounter()).thenReturn(3);
        when(player.getFoodTokens()).thenReturn(2);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockInventorDiscountCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        sustenanceEvent.resolveEvent(player);

        verify(player).removeFoodTokens(2);
        verify(player, never()).removePrestigeTokens(anyInt());
    }

    @Test
    void testResolveEvent_WithMultipleDiscountBuildings_OverDiscount() {
        // 5 character entries, 1 gatherer (default 3), 1 artist, 1 inventor
        // base required = 5 - (1 * 3) = 2
        // Gatherer bonus building (-1), Artist bonus (-1), Inventor bonus (-1)
        // Expected required = max(2 - 1 - 1 - 1, 0) = 0
        when(player.getCharacterDeck()).thenReturn(createMockDeckWithEntries(5));
        when(player.getGatherersCounter()).thenReturn(1);
        when(player.getArtistsCounter()).thenReturn(1);
        when(player.getInventorsCounter()).thenReturn(1);
        when(player.getFoodTokens()).thenReturn(0);

        ArrayList<BuildingCard> buildingCards = new ArrayList<>();
        buildingCards.add(mockGathererDiscountCard);
        buildingCards.add(mockArtistDiscountCard);
        buildingCards.add(mockInventorDiscountCard);
        when(player.getBuildingCards()).thenReturn(buildingCards);

        sustenanceEvent.resolveEvent(player);

        verify(player, never()).removeFoodTokens(anyInt());
        verify(player, never()).removePrestigeTokens(anyInt());
    }
}
