package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.*;
import it.polimi.gc06.mesos.model.cards.characters.*;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstanceSorterCardVisitorTest {

    private InstanceSorterCardVisitor visitor;
    private ModifierBuildingsRegistry mockRegistry;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting InstanceSorterCardVisitorTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending InstanceSorterCardVisitorTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        mockRegistry = mock(ModifierBuildingsRegistry.class);
        visitor = new InstanceSorterCardVisitor(mockRegistry);
        System.out.println("[START] " + testInfo.getDisplayName() + " DONE");
    }

    // --- CONSTRUCTOR & INITIALIZATION ---

    @Test
    void testConstructorInitializesMaps() {
        EnumMap<Era, ArrayList<TribeCard>> tribeCards = visitor.getTribeCards();
        EnumMap<Era, ArrayList<BuildingCard>> buildingCards = visitor.getBuildingCards();

        assertNotNull(tribeCards);
        assertNotNull(buildingCards);

        for (Era era : Era.values()) {
            assertNotNull(tribeCards.get(era));
            assertNotNull(buildingCards.get(era));
            assertTrue(tribeCards.get(era).isEmpty());
            assertTrue(buildingCards.get(era).isEmpty());
        }
    }

    // --- STANDARD CARDS SORTING ---

    @Test
    void testVisitBuildingCard() {
        BuildingCard mockCard = mock(BuildingCard.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_I);

        visitor.visit(mockCard);

        assertTrue(visitor.getBuildingCards().get(Era.ERA_I).contains(mockCard));
        assertEquals(1, visitor.getBuildingCards().get(Era.ERA_I).size());
    }

    @Test
    void testVisitBuildingCard_WithNullEra() {
        BuildingCard mockCard = mock(BuildingCard.class);
        when(mockCard.getEra()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> visitor.visit(mockCard));
    }

    @Test
    void testVisitTribeCard() {
        TribeCard mockCard = mock(TribeCard.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_II);

        visitor.visit(mockCard);

        assertTrue(visitor.getTribeCards().get(Era.ERA_II).contains(mockCard));
        assertEquals(1, visitor.getTribeCards().get(Era.ERA_II).size());
    }

    // --- SPECIALIZED BUILDING CARDS ---

    @Test
    void testVisitSpecializedBuildingCards() {
        EndGameBuildingCard endGameCard = mock(EndGameBuildingCard.class);
        when(endGameCard.getEra()).thenReturn(Era.ERA_I);
        visitor.visit(endGameCard);
        assertTrue(visitor.getBuildingCards().get(Era.ERA_I).contains(endGameCard));

        ModifierBuildingCard modifierCard = mock(ModifierBuildingCard.class);
        when(modifierCard.getEra()).thenReturn(Era.ERA_II);
        visitor.visit(modifierCard);
        assertTrue(visitor.getBuildingCards().get(Era.ERA_II).contains(modifierCard));

        ObserverSetBuildingCard observerSetCard = mock(ObserverSetBuildingCard.class);
        when(observerSetCard.getEra()).thenReturn(Era.ERA_III);
        visitor.visit(observerSetCard);
        assertTrue(visitor.getBuildingCards().get(Era.ERA_III).contains(observerSetCard));

        ObserverPairBuildingCard observerPairCard = mock(ObserverPairBuildingCard.class);
        when(observerPairCard.getEra()).thenReturn(Era.ERA_I);
        visitor.visit(observerPairCard);
        assertTrue(visitor.getBuildingCards().get(Era.ERA_I).contains(observerPairCard));
    }

    // --- SPECIALIZED TRIBE & CHARACTER CARDS ---

    @Test
    void testVisitSpecializedCharacterCards() {
        HunterCard hunterCard = mock(HunterCard.class);
        when(hunterCard.getEra()).thenReturn(Era.ERA_I);
        visitor.visit(hunterCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(hunterCard));

        ShamanCard shamanCard = mock(ShamanCard.class);
        when(shamanCard.getEra()).thenReturn(Era.ERA_II);
        visitor.visit(shamanCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_II).contains(shamanCard));

        ArtistCard artistCard = mock(ArtistCard.class);
        when(artistCard.getEra()).thenReturn(Era.ERA_III);
        visitor.visit(artistCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_III).contains(artistCard));

        BuilderCard builderCard = mock(BuilderCard.class);
        when(builderCard.getEra()).thenReturn(Era.ERA_I);
        visitor.visit(builderCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(builderCard));

        InventorCard inventorCard = mock(InventorCard.class);
        when(inventorCard.getEra()).thenReturn(Era.ERA_II);
        visitor.visit(inventorCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_II).contains(inventorCard));

        GathererCard gathererCard = mock(GathererCard.class);
        when(gathererCard.getEra()).thenReturn(Era.ERA_III);
        visitor.visit(gathererCard);
        assertTrue(visitor.getTribeCards().get(Era.ERA_III).contains(gathererCard));
    }

    // --- EVENT CARDS & BRANCHES ---

    @Test
    void testVisitEventCard() {
        EventCard mockCard = mock(EventCard.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_I);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(mockCard));
    }

    @Test
    void testVisitRitualEvent_EraI() {
        RitualEvent mockCard = mock(RitualEvent.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_I);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(mockCard));

        boolean contains = false;
        for (EventCard card : visitor.getFinalEvents()) {
            if (card == mockCard) {
                contains = true;
                break;
            }
        }
        assertFalse(contains);
    }

    @Test
    void testVisitRitualEvent_EraIII() {
        RitualEvent mockCard = mock(RitualEvent.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);

        boolean contains = false;
        for (EventCard card : visitor.getFinalEvents()) {
            if (card == mockCard) {
                contains = true;
                break;
            }
        }
        assertTrue(contains);
        assertFalse(visitor.getTribeCards().get(Era.ERA_III).contains(mockCard));
    }

    @Test
    void testVisitSustenanceEvent_EraII() {
        SustenanceEvent mockCard = mock(SustenanceEvent.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_II);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);
        assertTrue(visitor.getTribeCards().get(Era.ERA_II).contains(mockCard));
    }

    @Test
    void testVisitSustenanceEvent_EraIII() {
        SustenanceEvent mockCard = mock(SustenanceEvent.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);

        boolean contains = false;
        for (EventCard card : visitor.getFinalEvents()) {
            if (card == mockCard) {
                contains = true;
                break;
            }
        }
        assertTrue(contains);
    }

    // --- FINAL EVENTS ARRANGEMENT ---

    @Test
    void testGetFinalEvents_ReturnsArrayOfCorrectSize() {
        SustenanceEvent mockCard1 = mock(SustenanceEvent.class);
        when(mockCard1.getEra()).thenReturn(Era.ERA_III);
        RitualEvent mockCard2 = mock(RitualEvent.class);
        when(mockCard2.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(mockCard1);
        visitor.visit(mockCard2);

        EventCard[] finalEvents = visitor.getFinalEvents();
        assertEquals(2, finalEvents.length);
        assertEquals(mockCard1, finalEvents[0]);
        assertEquals(mockCard2, finalEvents[1]);
    }

    @Test
    void testGetFinalEvents_WithMoreThanTwoElements() {
        SustenanceEvent m1 = mock(SustenanceEvent.class);
        when(m1.getEra()).thenReturn(Era.ERA_III);
        RitualEvent m2 = mock(RitualEvent.class);
        when(m2.getEra()).thenReturn(Era.ERA_III);
        RitualEvent m4 = mock(RitualEvent.class);
        when(m4.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(m1);
        visitor.visit(m2);
        visitor.visit(m4);

        EventCard[] finalEvents = visitor.getFinalEvents();
        assertEquals(3, finalEvents.length);
    }

    @Test
    void testGetFinalEvents_WithFewerThanTwoElements() {
        SustenanceEvent m1 = mock(SustenanceEvent.class);
        when(m1.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(m1);

        EventCard[] finalEvents = visitor.getFinalEvents();
        assertEquals(2, finalEvents.length);
        assertEquals(m1, finalEvents[0]);
        assertNull(finalEvents[1]);
    }
}