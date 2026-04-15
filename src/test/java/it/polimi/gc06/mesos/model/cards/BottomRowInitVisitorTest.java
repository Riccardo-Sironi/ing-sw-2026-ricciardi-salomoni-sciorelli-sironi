package it.polimi.gc06.mesos.model.cards;

import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BottomRowInitVisitorTest {

    private ArrayList<TribeCard> bottomRow;
    private ArrayList<TribeCard> topRow;
    private BottomRowInitVisitor visitor;
    private final int MAX_TOP_ROW_SIZE = 5;

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
        bottomRow = new ArrayList<>();
        topRow = new ArrayList<>();
        visitor = new BottomRowInitVisitor(bottomRow, topRow, MAX_TOP_ROW_SIZE);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testCharactersAreAddedToBottomRow() {

        HunterCard hunter = mock(HunterCard.class);
        ShamanCard shaman = mock(ShamanCard.class);
        ArtistCard artist = mock(ArtistCard.class);
        BuilderCard builder = mock(BuilderCard.class);
        InventorCard inventor = mock(InventorCard.class);
        GathererCard gatherer = mock(GathererCard.class);


        visitor.visit(hunter);
        visitor.visit(shaman);
        visitor.visit(artist);
        visitor.visit(builder);
        visitor.visit(inventor);
        visitor.visit(gatherer);


        assertEquals(6, bottomRow.size(), "La bottomRow dovrebbe contenere 6 carte");
        assertTrue(bottomRow.contains(hunter));
        assertTrue(bottomRow.contains(shaman));
        assertTrue(bottomRow.contains(artist));
        assertTrue(bottomRow.contains(builder));
        assertTrue(bottomRow.contains(inventor));
        assertTrue(bottomRow.contains(gatherer));


        assertTrue(topRow.isEmpty(), "La topRow dovrebbe essere vuota");
    }

    @Test
    void testEventsAreAddedToTopRow() {

        RitualEvent ritual = mock(RitualEvent.class);
        SustenanceEvent sustenance = mock(SustenanceEvent.class);
        HuntEvent hunt = mock(HuntEvent.class);
        PaintingsEvent paintings = mock(PaintingsEvent.class);


        visitor.visit(ritual);
        visitor.visit(sustenance);
        visitor.visit(hunt);
        visitor.visit(paintings);


        assertEquals(4, topRow.size(), "La topRow dovrebbe contenere 4 carte");
        assertTrue(topRow.contains(ritual));
        assertTrue(topRow.contains(sustenance));
        assertTrue(topRow.contains(hunt));
        assertTrue(topRow.contains(paintings));


        assertTrue(bottomRow.isEmpty(), "La bottomRow dovrebbe essere vuota");
    }

    @Test
    void testMaxTopRowSizeThrowsExceptionForRitualEvent() {

        for (int i = 0; i < MAX_TOP_ROW_SIZE; i++) {
            topRow.add(mock(TribeCard.class));
        }

        RitualEvent ritual = mock(RitualEvent.class);


        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> visitor.visit(ritual)
        );

        assertTrue(exception.getMessage().contains("Top row cannot contain more than"));
    }

    @Test
    void testMaxTopRowSizeThrowsExceptionForSustenanceEvent() {
        for (int i = 0; i < MAX_TOP_ROW_SIZE; i++) {
            topRow.add(mock(TribeCard.class));
        }

        SustenanceEvent sustenance = mock(SustenanceEvent.class);
        assertThrows(IllegalStateException.class, () -> visitor.visit(sustenance));
    }

    @Test
    void testMaxTopRowSizeThrowsExceptionForHuntEvent() {
        for (int i = 0; i < MAX_TOP_ROW_SIZE; i++) {
            topRow.add(mock(TribeCard.class));
        }

        HuntEvent hunt = mock(HuntEvent.class);
        assertThrows(IllegalStateException.class, () -> visitor.visit(hunt));
    }

    @Test
    void testMaxTopRowSizeThrowsExceptionForPaintingsEvent() {
        for (int i = 0; i < MAX_TOP_ROW_SIZE; i++) {
            topRow.add(mock(TribeCard.class));
        }

        PaintingsEvent paintings = mock(PaintingsEvent.class);
        assertThrows(IllegalStateException.class, () -> visitor.visit(paintings));
    }
}