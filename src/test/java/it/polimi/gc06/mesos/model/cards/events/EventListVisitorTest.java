package it.polimi.gc06.mesos.model.cards.events;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EventListVisitorTest {

    private ArrayList<EventCard> events;
    private EventListVisitor visitor;

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
    void setUp(TestInfo testInfo)  {
        events = new ArrayList<>();
        visitor = new EventListVisitor(events);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testVisitEvents() {
        RitualEvent ritual = mock(RitualEvent.class);
        SustenanceEvent sustenance = mock(SustenanceEvent.class);
        HuntEvent hunt = mock(HuntEvent.class);
        PaintingsEvent paintings = mock(PaintingsEvent.class);

        visitor.visit(ritual);
        visitor.visit(sustenance);
        visitor.visit(hunt);
        visitor.visit(paintings);

        assertEquals(4, events.size());
        assertSame(ritual, events.get(0));
        assertSame(sustenance, events.get(1));
        assertSame(hunt, events.get(2));
        assertSame(paintings, events.get(3));
    }

}