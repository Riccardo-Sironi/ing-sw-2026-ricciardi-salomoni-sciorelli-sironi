package it.polimi.gc06.mesos.network_and_db.rmi;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.RMIClientManager;
import it.polimi.gc06.mesos.network.server.matches.MatchManager;
import org.junit.jupiter.api.*;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RMIClientManagerTest {

    private ServerConnection rmiClientMock = mock(ServerConnection.class);
    private MatchManager sharedManagerMock = mock(MatchManager.class);
    private GameController controllerMock = mock(GameController.class);
    private BlockingQueue<ControllerCommand> actionQueueMock = mock(BlockingQueue.class);
    private ControllerCommand commandMock = mock(ControllerCommand.class);

    private RMIClientManager clientManager;
    private final String testNickname = "testUser";

    @BeforeEach
    void setUp() {
        clientManager = new RMIClientManager(testNickname, rmiClientMock, sharedManagerMock);
    }

    @Test
    @Order(1)
    @DisplayName("Initialization and getters check")
    void testInitAndGetters() {
        assertEquals(testNickname, clientManager.getNickname());
        assertNull(clientManager.getController());
    }

    @Test
    @Order(2)
    @DisplayName("Set and get controller correctly")
    void testSetController() {
        clientManager.setController(controllerMock);
        assertEquals(controllerMock, clientManager.getController());
    }

    @Test
    @Order(3)
    @DisplayName("Enqueue Command succeeds when ActionQueue is provided")
    void testEnqueueCommandSuccess() throws InterruptedException {
        clientManager.setActionQueue(actionQueueMock);
        clientManager.enqueueCommand(commandMock);
        verify(actionQueueMock, times(1)).put(commandMock);
    }

    @Test
    @Order(4)
    @DisplayName("Enqueue command throws exception if queue is not set")
    void testEnqueueCommandWithoutQueue() {
        ControllerCommand cmd = mock(ControllerCommand.class);

        clientManager.enqueueCommand(cmd);

        BlockingQueue<SmallModelEditor> noticeQueue = getPrivateField(clientManager, "noticeQueue");
        assertFalse(noticeQueue.isEmpty(), "An error message should have been queued");
        assertEquals(ErrorDTO.class, noticeQueue.peek().getClass());
    }

    @Test
    @Order(5)
    @DisplayName("Enqueue Command handles InterruptedException")
    void testEnqueueCommandWhenInterrupted() throws InterruptedException {
        clientManager.setActionQueue(actionQueueMock);
        doThrow(new InterruptedException()).when(actionQueueMock).put(any(ControllerCommand.class));
        clientManager.enqueueCommand(commandMock);
        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @Test
    @Order(6)
    @DisplayName("Close connection calls logout on MatchManager")
    void testCloseConnection() {
        clientManager.closeConnection();
        verify(sharedManagerMock, times(1)).logout(testNickname);
    }


    @Test
    @Order(7)
    @DisplayName("Run method performs ping when idle")
    void testRunPingsWhenIdle() throws Exception {
        BlockingQueue<PropertyChangeEvent> noticeQueueMock = mock(BlockingQueue.class);
        setPrivateField(clientManager, "noticeQueue", noticeQueueMock);
        when(noticeQueueMock.poll(5, TimeUnit.SECONDS)).thenReturn(null);
        doAnswer(invocation -> {
            clientManager.closeConnection();
            return null;
        })
                .when(rmiClientMock).ping();

        clientManager.run();
        verify(rmiClientMock, times(1)).ping();
    }

    // Helper methods for reflection
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}