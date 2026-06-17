package it.polimi.gc06.mesos.model;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DTONotifierTest {

    private DTONotifier notifier;
    private ModelListener mockListener1;
    private ModelListener mockListener2;
    private SmallModelEditor mockDto;

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting DTONotifierTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending DTONotifierTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        notifier = new DTONotifier();
        mockListener1 = mock(ModelListener.class);
        mockListener2 = mock(ModelListener.class);
        mockDto = mock(SmallModelEditor.class);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    void testAddAndRemoveListener() {
        notifier.addListener(mockListener1, "Alice");

        notifier.notifyChangeToPlayer("Alice", mockDto);
        verify(mockListener1, times(1)).update(mockDto);

        notifier.removeListener(mockListener1, "Alice");

        assertThrows(NullPointerException.class, () -> notifier.notifyChangeToPlayer("Alice", mockDto));
    }

    @Test
    void testNotifyChangeSuccess() {
        notifier.addListener(mockListener1, "Alice");
        notifier.addListener(mockListener2, "Bob");

        notifier.notifyChange(mockDto);

        verify(mockDto, times(1)).setSequenceNumber(0);
        verify(mockListener1, times(1)).update(mockDto);
        verify(mockListener2, times(1)).update(mockDto);

        SmallModelEditor mockDto2 = mock(SmallModelEditor.class);
        notifier.notifyChange(mockDto2);

        verify(mockDto2, times(1)).setSequenceNumber(1);
        verify(mockListener1, times(1)).update(mockDto2);
        verify(mockListener2, times(1)).update(mockDto2);
    }

    @Test
    void testNotifyChangeThrowsExceptionWhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> notifier.notifyChange(null));
    }

    @Test
    void testNotifyChangeToPlayerSuccess() {
        notifier.addListener(mockListener1, "Alice");
        notifier.addListener(mockListener2, "Bob");

        notifier.notifyChangeToPlayer("Alice", mockDto);

        verify(mockDto, never()).setSequenceNumber(anyInt());
        verify(mockListener1, times(1)).update(mockDto);
        verify(mockListener2, never()).update(any());
    }

    @Test
    void testNotifyChangeToPlayerThrowsExceptionWhenDtoIsNull() {
        notifier.addListener(mockListener1, "Alice");

        assertThrows(IllegalArgumentException.class, () -> notifier.notifyChangeToPlayer("Alice", null));
    }
}