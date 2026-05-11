package it.polimi.gc06.mesos.network_and_db.rmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.client.RMIClientInterface;
import it.polimi.gc06.mesos.network.client.RMIClientManager;
import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.socket.commands.Command;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RMIClientManagerTest {

    private RMIClientInterface rmiClientMock = mock(RMIClientInterface.class);
    private MatchManager sharedManagerMock = mock(MatchManager.class);
    private GameController controllerMock = mock(GameController.class);
    private BlockingQueue<Command> actionQueueMock = mock(BlockingQueue.class);
    private Command commandMock = mock(Command.class);

    private RMIClientManager clientManager;
    private final String testNickname = "testUser";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting RMIClientManagerTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending RMIClientManagerTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        clientManager = new RMIClientManager(testNickname, rmiClientMock, sharedManagerMock);

        System.out.println("[START] " + testInfo.getDisplayName());
    }


    @Test
    @DisplayName("Initialization and getters")
    void testInitAndGetters() {
        assertEquals(testNickname, clientManager.getNickname());
        assertNull(clientManager.getController());
    }

    @Test
    @DisplayName("Set Controller links the listener")
    void testSetController() {
        clientManager.setController(controllerMock);

        assertEquals(controllerMock, clientManager.getController());
        verify(controllerMock, times(1)).addListener(clientManager);
    }

    @Test
    @DisplayName("Enqueue Command successfully when ActionQueue is set")
    void testEnqueueCommandSuccess() throws InterruptedException {
        clientManager.setActionQueue(actionQueueMock);
        clientManager.enqueueCommand(commandMock);

        verify(actionQueueMock, times(1)).put(commandMock);
    }

    @Test
    @DisplayName("Enqueue Command fails and sends Error Message when ActionQueue is null")
    void testEnqueueCommandWhenQueueIsNull() {
        clientManager.enqueueCommand(commandMock);

        BlockingQueue<PropertyChangeEvent> noticeQueue = getPrivateField(clientManager);
        assertEquals(1, noticeQueue.size());

        PropertyChangeEvent event = noticeQueue.peek();
        assertNotNull(event);
        assertEquals("ACTION_ERROR", event.getPropertyName());
        assertEquals("The match hasn't started yet!", event.getNewValue());
    }

    private <T> T getPrivateField(Object target) {
        try {
            Field field = target.getClass().getDeclaredField("noticeQueue");
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get private field", e);
        }
    }

    @Test
    @DisplayName("Enqueue Command handles InterruptedException properly")
    void testEnqueueCommandWhenInterrupted() throws InterruptedException {
        clientManager.setActionQueue(actionQueueMock);
        doThrow(new InterruptedException()).when(actionQueueMock).put(any(Command.class));
        clientManager.enqueueCommand(commandMock);

        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @Test
    @DisplayName("Send error message handles InterruptedException properly")
    void testErrorMessageInterrupted() throws InterruptedException {
        BlockingQueue noticeQueueMock = mock(BlockingQueue.class);
        setPrivateField(clientManager, noticeQueueMock);
        doThrow(new InterruptedException()).when(noticeQueueMock).put(any(PropertyChangeEvent.class));

        clientManager.sendErrorMessage("Test error");

        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    private void setPrivateField(Object target, Object value) {
        try {
            Field field = target.getClass().getDeclaredField("noticeQueue");
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set private field", e);
        }
    }

    @Test
    @DisplayName("PropertyChange adds events to noticeQueue")
    void testPropertyChange() {
        PropertyChangeEvent event = new PropertyChangeEvent(this, "TEST", "old", "new");
        clientManager.propertyChange(event);

        BlockingQueue<PropertyChangeEvent> noticeQueue = getPrivateField(clientManager);
        assertTrue(noticeQueue.contains(event));
    }

    @Test
    @DisplayName("Close Connection performs cleanup")
    void testCloseConnection() {
        clientManager.setController(controllerMock);

        clientManager.closeConnection();
        verify(sharedManagerMock, times(1)).logout(testNickname);
        verify(controllerMock, times(1)).removeListener(clientManager);

        clientManager.closeConnection();
        verify(sharedManagerMock, times(1)).logout(testNickname);
        verify(controllerMock, times(1)).removeListener(clientManager);
    }

    @Test
    @DisplayName("Close connection handles null controller")
    void testCloseConnectionWhenControllerIsNull() {
        assertDoesNotThrow(() -> clientManager.closeConnection());
        verify(sharedManagerMock, times(1)).logout(testNickname);
    }

    @Test
    @DisplayName("Run method processes messages and sends them via RMI")
    void testRunProcessesMessages() throws Exception {
        PropertyChangeEvent event = new PropertyChangeEvent("MockSource", "TEST_EVENT", null, "TEST_VALUE");
        clientManager.propertyChange(event);

        doAnswer(invocation -> {
            clientManager.closeConnection();
            return null;
        }).when(rmiClientMock).receiveMessage(anyString());

        clientManager.run();

        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(event);
        verify(rmiClientMock, times(1)).receiveMessage(expectedJson);
    }

    @Test
    @DisplayName("Run method pings the client when the queue is empty")
    void testRunPingsWhenIdle() throws Exception {
        BlockingQueue<PropertyChangeEvent> noticeQueueMock = mock(BlockingQueue.class);
        setPrivateField(clientManager, noticeQueueMock);
        when(noticeQueueMock.poll(5, TimeUnit.SECONDS)).thenReturn(null);

        doAnswer(invocation -> {
            clientManager.closeConnection();
            return null;
        }).when(rmiClientMock).ping();

        clientManager.run();

        verify(rmiClientMock, times(1)).ping();
    }

    @Test
    @DisplayName("Run method handles InterruptedException on poll")
    void testRunHandlesInterruptedException() throws Exception {
        BlockingQueue<PropertyChangeEvent> noticeQueueMock = mock(BlockingQueue.class);
        setPrivateField(clientManager, noticeQueueMock);
        when(noticeQueueMock.poll(5, TimeUnit.SECONDS)).thenThrow(new InterruptedException());

        assertDoesNotThrow(() -> clientManager.run());
    }

    @Test
    @DisplayName("Run method handles connection loss (Exception) gracefully")
    void testRunHandlesConnectionLoss() throws Exception {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, "TEST", null, "TEST");
        clientManager.propertyChange(evt);

        doThrow(new RemoteException("Connection Lost")).when(rmiClientMock).receiveMessage(anyString());

        clientManager.run();

        verify(sharedManagerMock, times(1)).logout(testNickname);
    }

    @Test
    @DisplayName("Close Connection handles null nickname gracefully")
    void testCloseConnectionWithNullNickname() {
        RMIClientManager nullNicknameManager = new RMIClientManager(null, rmiClientMock, sharedManagerMock);

        assertDoesNotThrow(() -> nullNicknameManager.closeConnection());

        verify(sharedManagerMock, never()).logout(any());
    }
}
