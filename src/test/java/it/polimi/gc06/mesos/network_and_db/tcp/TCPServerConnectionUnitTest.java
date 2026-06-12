package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.dtos.DTOVisitor;
import it.polimi.gc06.mesos.dtos.LeaderboardChangeDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.TCPServerConnection;
import it.polimi.gc06.mesos.network.socket.BlockingBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TCPServerConnectionUnitTest {

    private TCPServerConnection connection;
    private Client mockListener;

    private ObjectOutputStream mockOut;
    private ObjectInputStream mockIn;
    private BlockingQueue<Object> mockResponses;
    private BlockingQueue<ControllerCommand> mockCommands;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() throws Exception {
        connection = new TCPServerConnection("localhost", 1234);
        mockListener = mock(Client.class);
        connection.prioritizedSubscribe(mockListener);

        mockOut = mock(ObjectOutputStream.class);
        mockIn = mock(ObjectInputStream.class);

        setPrivateField(connection, "out", mockOut);
        setPrivateField(connection, "in", mockIn);

        mockResponses = (BlockingQueue<Object>) getPrivateField(connection, "responses");
        mockCommands = (BlockingQueue<ControllerCommand>) getPrivateField(connection, "commands");
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private void setBooleanState(String fieldName, boolean value) throws Exception {
        setPrivateField(connection, fieldName, value);
    }

    // --- STATE AND ILLEGAL EXCEPTIONS TESTS ---

    @Test
    void testPing() {
        assertDoesNotThrow(() -> connection.ping());
    }

    @Test
    void testUnsubscribe() {
        connection.unsubscribe(mockListener);
        assertDoesNotThrow(() -> assertNull(getPrivateField(connection, "prioritizedListener")));

        Client another = mock(Client.class);
        connection.prioritizedSubscribe(mockListener);
        connection.unsubscribe(another);
        assertDoesNotThrow(() -> assertEquals(mockListener, getPrivateField(connection, "prioritizedListener")));
    }

    @Test
    void testLoginFailsIfAlreadySent() throws Exception {
        setBooleanState("nicknameSent", true);
        assertThrows(IllegalStateException.class, () -> connection.login("Alice"));
    }

    @Test
    void testLoginFailsIfInsideMatch() throws Exception {
        setBooleanState("isInsideMatch", true);
        assertThrows(IllegalStateException.class, () -> connection.login("Alice"));
    }

    @Test
    void testLogoutFailsIfNotSentOrInMatch() throws Exception {
        setBooleanState("nicknameSent", false);
        assertThrows(IllegalStateException.class, () -> connection.logout("Alice"));

        setBooleanState("nicknameSent", true);
        setBooleanState("isInsideMatch", true);
        assertThrows(IllegalStateException.class, () -> connection.logout("Alice"));
    }

    @Test
    void testCreateMatchFailsConditions() throws Exception {
        setBooleanState("nicknameSent", false);
        assertThrows(IllegalStateException.class, () -> connection.createMatch(2, "Alice"));

        setBooleanState("nicknameSent", true);
        setBooleanState("isInsideMatch", true);
        assertThrows(IllegalStateException.class, () -> connection.createMatch(2, "Alice"));
    }

    @Test
    void testJoinMatchFailsConditions() throws Exception {
        setBooleanState("nicknameSent", false);
        assertThrows(IllegalStateException.class, () -> connection.joinMatch(1, "Alice"));

        setBooleanState("nicknameSent", true);
        setBooleanState("isInsideMatch", true);
        assertThrows(IllegalStateException.class, () -> connection.joinMatch(1, "Alice"));
    }

    @Test
    void testGetAvailableMatchesFailsIfInMatch() throws Exception {
        setBooleanState("isInsideMatch", true);
        assertThrows(IllegalStateException.class, () -> connection.getAvailableMatches());
    }

    // --- CONCURRENT REQUEST BLOCKS (COVERING request.store IFs) ---

    @Test
    @SuppressWarnings("unchecked")
    void testConcurrentRequestsAreRejected() throws Exception {
        BlockingBox<String> requestBox = (BlockingBox<String>) getPrivateField(connection, "request");
        requestBox.store("DUMMY_REQUEST");

        assertThrows(IllegalStateException.class, () -> connection.login("Bob"));

        setBooleanState("nicknameSent", true);
        assertThrows(IllegalStateException.class, () -> connection.logout("Bob"));
        assertThrows(IllegalStateException.class, () -> connection.getPlayersMatchId("Bob"));
        assertThrows(IllegalStateException.class, () -> connection.getMatchInfo(1));
        assertThrows(IllegalStateException.class, () -> connection.getAvailableMatches());
        assertThrows(IllegalStateException.class, () -> connection.createMatch(3, "Bob"));
        assertThrows(IllegalStateException.class, () -> connection.joinMatch(1, "Bob"));
    }

    // --- GAMEPLAY ACTION COMMANDS EXCEPTIONS ---

    @Test
    void testActionsFailIfNotInsideMatch() {
        assertThrows(IllegalStateException.class, () -> connection.placeTotem("Alice", 0));
        assertThrows(IllegalStateException.class, () -> connection.pickCardFromBottom("Alice", 0));
        assertThrows(IllegalStateException.class, () -> connection.pickCardFromTop("Alice", 0));
        assertThrows(IllegalStateException.class, () -> connection.pickBuildingFromBottom("Alice", 0));
        assertThrows(IllegalStateException.class, () -> connection.pickBuildingFromTop("Alice", 0));
        assertThrows(IllegalStateException.class, () -> connection.handleSkip("Alice"));
        assertThrows(IllegalStateException.class, () -> connection.chooseTotemColor("Alice", Color.ORANGE));
    }

    // --- HAPPY PATH COMMAND ENQUEUING ---

    @Test
    void testEnqueuingCommands() throws Exception {
        setBooleanState("isInsideMatch", true);

        connection.placeTotem("A", 1);
        connection.pickCardFromBottom("A", 1);
        connection.pickCardFromTop("A", 1);
        connection.pickBuildingFromBottom("A", 1);
        connection.pickBuildingFromTop("A", 1);
        connection.handleSkip("A");
        connection.chooseTotemColor("A", Color.ORANGE);

        assertEquals(7, mockCommands.size());
    }

    // --- RECEIVE DTO & RUN LOOP VISITOR ---

    @Test
    void testReceiveDTONormal() throws Exception {
        SmallModelEditor dummyDto = mock(SmallModelEditor.class);
        connection.receiveDTO(dummyDto);
        verify(mockListener, times(1)).update(dummyDto);
    }

    @Test
    void testRunLoopEndgameDto() throws Exception {
        LeaderboardChangeDTO endgameDto = new LeaderboardChangeDTO(null);
        when(mockIn.readObject()).thenReturn(endgameDto).thenThrow(new EOFException());

        setBooleanState("isInsideMatch", true);

        connection.run();

        boolean insideMatch = (boolean) getPrivateField(connection, "isInsideMatch");
        assertFalse(insideMatch, "Match should end when LeaderboardChangeDTO is received");
        verify(mockListener, times(1)).update(endgameDto);
    }

    @Test
    void testRunLoopHandlesObjectResponse() throws Exception {
        String serverResponse = "TEST_OK";
        when(mockIn.readObject()).thenReturn(serverResponse).thenThrow(new EOFException());

        connection.run();

        assertEquals(serverResponse, mockResponses.take());
    }

    // --- CATCH BLOCKS IN RUN() ---

    @Test
    void testRunLoopHandlesClassNotFoundException() throws Exception {
        when(mockIn.readObject()).thenThrow(new ClassNotFoundException("Forced ClassNotFound"));
        assertDoesNotThrow(() -> connection.run());
    }

    @Test
    void testRunLoopHandlesIOException() throws Exception {
        when(mockIn.readObject()).thenThrow(new IOException("Forced IOException"));
        assertDoesNotThrow(() -> connection.run());
    }

    @Test
    void testRunLoopHandlesDTOProcessingException() throws Exception {
        SmallModelEditor dummyDto = mock(SmallModelEditor.class);
        doThrow(new RuntimeException("Simulated DTO crash")).when(dummyDto).accept(any(DTOVisitor.class));
        when(mockIn.readObject()).thenReturn(dummyDto).thenThrow(new EOFException());

        assertDoesNotThrow(() -> connection.run());
    }

    // --- THREAD INTERRUPTIONS (COVERING CATCH BLOCKS) ---

    // --- THREAD INTERRUPTIONS (COVERING CATCH BLOCKS) ---

    @Test
    void testRequestHandlerLoopInterruption() throws Exception {
        // Il metodo look() non dichiara InterruptedException, quindi mockiamo take() su responses.
        BlockingBox<String> mockedRequest = mock(BlockingBox.class);
        when(mockedRequest.look()).thenReturn("DUMMY_REQUEST");
        setPrivateField(connection, "request", mockedRequest);

        BlockingQueue<Object> mockedResponses = mock(BlockingQueue.class);
        // La prima chiamata lancia l'eccezione desiderata per coprire il catch.
        // La seconda chiamata lancia una RuntimeException per uscire dal while(true).
        when(mockedResponses.take())
                .thenThrow(new InterruptedException("Forced for coverage"))
                .thenThrow(new RuntimeException("Break infinite loop"));

        setPrivateField(connection, "responses", mockedResponses);

        // Possiamo chiamare il metodo direttamente senza thread separati,
        // perché la RuntimeException forzerà l'uscita immediata e noi la catturiamo.
        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                // Invochiamo il metodo nel thread corrente
                method.invoke(connection);
            } catch (java.lang.reflect.InvocationTargetException e) {
                // Il metodo ha lanciato la nostra RuntimeException per uscire dal ciclo. Test riuscito!
                assertTrue(e.getCause() instanceof RuntimeException);
                assertEquals("Break infinite loop", e.getCause().getMessage());
            }
        });
    }

    @Test
    void testCommandHandlerLoopInterruption() throws Exception {
        BlockingQueue<ControllerCommand> mockedQueue = mock(BlockingQueue.class);

        // Stessa strategia: prima solleva l'InterruptedException (copre il catch),
        // poi solleva una RuntimeException per spaccare il ciclo infinito.
        when(mockedQueue.poll(anyLong(), any()))
                .thenThrow(new InterruptedException("Forced for coverage"))
                .thenThrow(new RuntimeException("Break infinite loop"));

        setPrivateField(connection, "commands", mockedQueue);

        assertDoesNotThrow(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("commandHandlerLoop");
                method.setAccessible(true);
                // Invochiamo il metodo nel thread corrente
                method.invoke(connection);
            } catch (java.lang.reflect.InvocationTargetException e) {
                // Catturiamo la scappatoia dal ciclo infinito
                assertTrue(e.getCause() instanceof RuntimeException);
                assertEquals("Break infinite loop", e.getCause().getMessage());
            }
        });

        // Verifica che il ramo catch sia stato attivato (l'eccezione iniziale è stata lanciata)
        verify(mockedQueue, times(2)).poll(anyLong(), any());
    }


    // --- REQUEST HANDLER SIMULATION ---

    @Test
    void testLoginSuccess() throws Exception {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put("OK");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        boolean res = connection.login("Alice");

        assertTrue(res);
        assertTrue((boolean) getPrivateField(connection, "nicknameSent"));

        reqThread.interrupt();
    }

    @Test
    void testLoginFailure() throws Exception {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put("KO");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        boolean res = connection.login("Alice");

        assertFalse(res);
        assertFalse((boolean) getPrivateField(connection, "nicknameSent"));

        reqThread.interrupt();
    }

    @Test
    void testMatchCreationFailure() throws Exception {
        setBooleanState("nicknameSent", true);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put("KO");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        assertThrows(IllegalArgumentException.class, () -> connection.createMatch(2, "Alice"));

        reqThread.interrupt();
    }

    @Test
    void testJoinMatchSuccess() throws Exception {
        setBooleanState("nicknameSent", true);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put("OK");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        assertTrue(connection.joinMatch(1, "Alice"));
        assertTrue((boolean) getPrivateField(connection, "isInsideMatch"));

        reqThread.interrupt();
    }

    @Test
    void testJoinMatchFailure() throws Exception {
        setBooleanState("nicknameSent", true);

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put("KO");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        assertFalse(connection.joinMatch(1, "Alice"));
        assertFalse((boolean) getPrivateField(connection, "isInsideMatch"));

        reqThread.interrupt();
    }

    @Test
    void testMatchStatusAndIdRetrieval() throws Exception {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                mockResponses.put(42);
                Thread.sleep(100);
                mockResponses.put("1/5 players");
                Thread.sleep(100);
                mockResponses.put("1=1/5");
            } catch (InterruptedException e) {}
        });

        Thread reqThread = new Thread(() -> {
            try {
                java.lang.reflect.Method method = TCPServerConnection.class.getDeclaredMethod("requestHandlerLoop");
                method.setAccessible(true);
                method.invoke(connection);
            } catch (Exception e) {}
        });

        reqThread.start();
        t.start();

        assertEquals(42, connection.getPlayersMatchId("Alice"));
        assertEquals("1/5 players", connection.getMatchInfo(42));
        assertEquals("1=1/5", connection.getAvailableMatches());

        reqThread.interrupt();
    }
}