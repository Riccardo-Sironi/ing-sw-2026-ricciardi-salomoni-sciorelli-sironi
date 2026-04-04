package it.polimi.gc06.mesos.model.InstancesManager;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.buildings.BuildingCard;
import it.polimi.gc06.mesos.model.cards.buildings.ModifierBuildingsRegistry;
import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.model.cards.events.RitualEvent;
import it.polimi.gc06.mesos.model.cards.events.SustenanceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InstanceSorterCardVisitorTest {

    private InstanceSorterCardVisitor visitor;
    private ModifierBuildingsRegistry mockRegistry;

    @BeforeEach
    void setUp() {
        mockRegistry = mock(ModifierBuildingsRegistry.class);
        visitor = new InstanceSorterCardVisitor(mockRegistry);
    }

    @Test
    void testConstructorInitializesMaps() {
        EnumMap<Era, ArrayList<TribeCard>> tribeCards = visitor.getTribeCards();
        EnumMap<Era, ArrayList<BuildingCard>> buildingCards = visitor.getBuildingCards();

        assertNotNull(tribeCards);
        assertNotNull(buildingCards);

        for (Era era : Era.values()) {
            assertNotNull(tribeCards.get(era), "TribeCards map should be initialized for era: " + era);
            assertNotNull(buildingCards.get(era), "BuildingCards map should be initialized for era: " + era);
            assertTrue(tribeCards.get(era).isEmpty());
            assertTrue(buildingCards.get(era).isEmpty());
        }
    }

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

        // Null era is going to be an unhandled bug if they expect null mapping.
        // It throws NPE when EnumMap tries to put/get a null key in this case.
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

    @Test
    void testVisitEventCard() {
        EventCard mockCard = mock(EventCard.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_I);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry); // Ensuring registry gets injected
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(mockCard));
    }

    @Test
    void testVisitRitualEvent_EraI() {
        RitualEvent mockCard = mock(RitualEvent.class);
        when(mockCard.getEra()).thenReturn(Era.ERA_I);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);
        assertTrue(visitor.getTribeCards().get(Era.ERA_I).contains(mockCard));
        
        // Ensure finalEvents does NOT contain this
        boolean contains = false;
        for (EventCard card : visitor.getFinalEvents()) {
            if (card == mockCard) contains = true;
        }
        assertFalse(contains);
    }

    @Test
    void testVisitRitualEvent_EraIII() {
        RitualEvent mockCard = mock(RitualEvent.class);
        // Important: check if ERA_III exists
        when(mockCard.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(mockCard);

        verify(mockCard).setRegistry(mockRegistry);
        // ERA_III events go into Final Events
        boolean contains = false;
        for (EventCard card : visitor.getFinalEvents()) {
            if (card == mockCard) contains = true;
        }
        assertTrue(contains);
        
        // Ensure it did not go into regular tribe cards map
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
            if (card == mockCard) contains = true;
        }
        assertTrue(contains);
    }

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
        // Technically there should be exactly two ERA_III events in the game,
        // but if someone puts 3 what happens?
        SustenanceEvent m1 = mock(SustenanceEvent.class); when(m1.getEra()).thenReturn(Era.ERA_III);
        RitualEvent m2 = mock(RitualEvent.class); when(m2.getEra()).thenReturn(Era.ERA_III);
        EventCard m3 = mock(EventCard.class); when(m3.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(m1);
        visitor.visit(m2);
        // Note: generic EventCard in ERA_III goes to tribeCards, not finalEvents! Let's use RitualEvent again.
        RitualEvent m4 = mock(RitualEvent.class); when(m4.getEra()).thenReturn(Era.ERA_III);
        visitor.visit(m4);

        EventCard[] finalEvents = visitor.getFinalEvents();
        // toArray(new EventCard[2]) allocates a larger array automatically if size > 2.
        assertEquals(3, finalEvents.length); 
    }

    @Test
    void testGetFinalEvents_WithFewerThanTwoElements() {
        SustenanceEvent m1 = mock(SustenanceEvent.class); when(m1.getEra()).thenReturn(Era.ERA_III);

        visitor.visit(m1);

        EventCard[] finalEvents = visitor.getFinalEvents();
        // toArray(new EventCard[2]) will return an array of length 2, padded with nulls at index 1
        assertEquals(2, finalEvents.length);
        assertEquals(m1, finalEvents[0]);
        assertNull(finalEvents[1]);
    }
}

