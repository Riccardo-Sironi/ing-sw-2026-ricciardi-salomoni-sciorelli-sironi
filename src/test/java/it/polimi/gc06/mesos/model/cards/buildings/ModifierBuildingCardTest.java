package it.polimi.gc06.mesos.model.cards.buildings;

import it.polimi.gc06.mesos.model.cards.CardVisitor;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModifierBuildingCardTest {

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting ModifierBuildingCardTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending ModifierBuildingCardTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo)  {
        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testCardKeySetterAndGetter(){
        ModifierBuildingCard modifierBuildingCard = new ModifierBuildingCard();
        assertNull(modifierBuildingCard.getCardKey());

        assertThrows(IllegalArgumentException.class, () -> modifierBuildingCard.setCardKey(null));

        assertDoesNotThrow(() -> modifierBuildingCard.setCardKey(ModifierBuildingRegistryKey.PICK_FROM_TOP));
        assertEquals(ModifierBuildingRegistryKey.PICK_FROM_TOP, modifierBuildingCard.getCardKey());

        assertThrows(IllegalStateException.class, () -> modifierBuildingCard.setCardKey(ModifierBuildingRegistryKey.TILE_FOOD_BONUS));
    }

    @Test
    void testAccept(){
        CardVisitor visitor = mock(CardVisitor.class);
        ModifierBuildingCard modifierBuildingCard = new ModifierBuildingCard();
        modifierBuildingCard.accept(visitor);

        verify(visitor, times(1)).visit(modifierBuildingCard);
    }

    @Test
    void testEqualsAndHashCode() {
        ModifierBuildingCard card1 = new ModifierBuildingCard();
        card1.setCardKey(ModifierBuildingRegistryKey.PICK_FROM_TOP);

        ModifierBuildingCard card2 = new ModifierBuildingCard();
        card2.setCardKey(ModifierBuildingRegistryKey.PICK_FROM_TOP);

        ModifierBuildingCard card3 = new ModifierBuildingCard();
        card3.setCardKey(ModifierBuildingRegistryKey.TILE_FOOD_BONUS);

        assertEquals(card1, card1);
        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, null);
        assertNotEquals(card1, new Object());

        assertEquals(card1.hashCode(), card2.hashCode());
        assertNotEquals(card1.hashCode(), card3.hashCode());
    }
}