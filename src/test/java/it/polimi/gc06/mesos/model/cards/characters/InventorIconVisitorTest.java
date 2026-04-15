package it.polimi.gc06.mesos.model.cards.characters;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventorIconVisitorTest {

    private InventorCard inventorCard;
    private InventionIcon inventorIcon;

    private InventorIconVisitor inventorIconVisitor;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting InventorIconVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending InventorIconVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }


    @BeforeEach
    void setUp(TestInfo testInfo) {

        inventorCard = mock(InventorCard.class);
        inventorIcon = mock(InventionIcon.class);

        inventorIconVisitor = new InventorIconVisitor();

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testVisitIcon() {
        when(inventorCard.getIcon()).thenReturn(inventorIcon);
        inventorIconVisitor.visit(inventorCard);

        assertEquals(inventorIcon, inventorIconVisitor.getAndClearIcon(), "The visitor should return the correct icon");
    }

    @Test
    void testGetAndClearIcon() {
        when(inventorCard.getIcon()).thenReturn(inventorIcon);
        inventorIconVisitor.visit(inventorCard);

        assertEquals(inventorIcon, inventorIconVisitor.getAndClearIcon());
        assertNull(inventorIconVisitor.getAndClearIcon(), "After getting the icon, it should be cleared and return null");
    }

    @Test
    void testGetAndClearWithoutVisitor() {
        assertNull(inventorIconVisitor.getAndClearIcon(), "If visit has not been called, getAndClearIcon should return null");
    }

    @Test
    void testVisitOverridesPreviousValue() {
        InventorCard inventorCard1 = mock(InventorCard.class);
        InventorCard inventorCard2 = mock(InventorCard.class);

        InventionIcon inventionIcon1 = mock(InventionIcon.class);
        InventionIcon inventionIcon2 = mock(InventionIcon.class);

        when(inventorCard1.getIcon()).thenReturn(inventionIcon1);
        when(inventorCard2.getIcon()).thenReturn(inventionIcon2);

        inventorIconVisitor.visit(inventorCard1);
        inventorIconVisitor.visit(inventorCard2);

        assertEquals(inventionIcon2, inventorIconVisitor.getAndClearIcon(), "The visitor should return the icon of the last visited card");
    }
}