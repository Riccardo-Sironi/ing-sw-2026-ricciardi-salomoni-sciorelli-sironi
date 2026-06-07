package it.polimi.gc06.mesos.network_and_db.rmi;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.rmi.RMIServerInterfaceImpl;
import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.server.RMIClientManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RMIServerInterfaceImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MatchManager serverManagerMock;

    private ServerConnection clientCallbackMock = mock(ServerConnection.class);

    private RMIClientManager rmiClientManagerMock = mock(RMIClientManager.class);

    private GameController gameControllerMock = mock(GameController.class);

    private RMIServerInterfaceImpl serverImpl;
    private final String testUser = "PlayerOne";

    @BeforeAll
    static void whichTest() {
        System.out.println("--- Starting RMIServerInterfaceImplTest ---");
    }

    @AfterAll
    static void endTest() {
        System.out.println("--- Ending RMIServerInterfaceImplTest ---");
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("[END] " + testInfo.getDisplayName() + " DONE");
    }

    @BeforeEach
    void setUp(TestInfo testInfo) throws RemoteException {
        serverImpl = new RMIServerInterfaceImpl(serverManagerMock, 1100);

        System.out.println("[START] " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Login returns the value from MatchManager")
    void testLogin() throws RemoteException {
        when(serverManagerMock.login(testUser)).thenReturn(true);

        assertTrue(serverImpl.login(testUser));
        verify(serverManagerMock, times(1)).login(testUser);
    }

    @Test
    @DisplayName("Get available matches queries the MatchManager")
    void testGetAvailableMatches() throws RemoteException {
        String expectedString = "Match1, Match2";
        when(serverManagerMock.getAvailableMatchesString()).thenReturn(expectedString);

        assertEquals(expectedString, serverImpl.getAvailableMatches());
        verify(serverManagerMock, times(1)).getAvailableMatchesString();
    }

    @Test
    @DisplayName("Create match uses deep stubs to return match ID")
    void testCreateMatch() throws RemoteException {
        when(serverManagerMock.createMatch(4).getMatchId()).thenReturn(101);

        int matchId = serverImpl.createMatch(4);
        assertEquals(101, matchId);
    }

    @Test
    @DisplayName("Logout an existing user closes connection and removes from map")
    void testLogoutExistingUser() throws RemoteException {
        injectManagerIntoMap(testUser, rmiClientManagerMock);
        when(serverManagerMock.logout(testUser)).thenReturn(true);

        boolean result = serverImpl.logout(testUser);

        assertTrue(result);
        verify(rmiClientManagerMock, times(1)).closeConnection(); // La connessione deve essere chiusa
        verify(serverManagerMock, times(1)).logout(testUser);

        Map<?, ?> map = getPrivateField(serverImpl, "clientManagers");
        assertFalse(map.containsKey(testUser));
    }

    @Test
    @DisplayName("Logout a non-existing user only calls MatchManager")
    void testLogoutNonExistingUser() throws RemoteException {
        when(serverManagerMock.logout("GhostUser")).thenReturn(false);

        boolean result = serverImpl.logout("GhostUser");

        assertFalse(result);
        verify(serverManagerMock, times(1)).logout("GhostUser");
    }

    @Test
    @DisplayName("Join match success adds new manager to the map")
    void testJoinMatchSuccess() throws RemoteException {
        when(serverManagerMock.joinMatch(eq(1), any(RMIClientManager.class))).thenReturn(true);

        boolean result = serverImpl.joinMatch(1, testUser, clientCallbackMock);

        assertTrue(result);

        Map<?, ?> map = getPrivateField(serverImpl, "clientManagers");
        assertTrue(map.containsKey(testUser));
    }

    @Test
    @DisplayName("Join match failure does not add to the map")
    void testJoinMatchFailure() throws RemoteException {
        when(serverManagerMock.joinMatch(eq(1), any(RMIClientManager.class))).thenReturn(false);

        boolean result = serverImpl.joinMatch(1, testUser, clientCallbackMock);

        assertFalse(result);

        Map<?, ?> map = getPrivateField(serverImpl, "clientManagers");
        assertFalse(map.containsKey(testUser));
    }

    @Test
    @DisplayName("Private getController method returns correct controller or null")
    void testGetControllerPrivateMethod() throws Exception {
        Method getControllerMethod = RMIServerInterfaceImpl.class.getDeclaredMethod("getController", String.class);
        getControllerMethod.setAccessible(true);

        GameController nullController = (GameController) getControllerMethod.invoke(serverImpl, "Unknown");
        assertNull(nullController);

        when(rmiClientManagerMock.getController()).thenReturn(gameControllerMock);
        injectManagerIntoMap(testUser, rmiClientManagerMock);

        GameController foundController = (GameController) getControllerMethod.invoke(serverImpl, testUser);
        assertEquals(gameControllerMock, foundController);
    }

    @Test
    @DisplayName("All command handlers enqueue commands successfully")
    void testCommandHandlersSuccess() throws RemoteException {
        injectManagerIntoMap(testUser, rmiClientManagerMock);

        serverImpl.handleTotemOfferTilePlacement(testUser, 1);
        serverImpl.handleCardPickBottomRow(testUser, 2);
        serverImpl.handleCardPickTopRow(testUser, 3);
        serverImpl.handleBuildingPickBottomRow(testUser, 4);
        serverImpl.handleBuildingPickTopRow(testUser, 5);
        serverImpl.handleSkip(testUser);

        verify(rmiClientManagerMock, times(6)).enqueueCommand(any(ControllerCommand.class));
    }

    @Test
    @DisplayName("All command handlers throw exception if user is missing")
    void testCommandHandlersUserMissing() {
        String missingUser = "Ghost";

        assertThrows(RemoteException.class, () -> serverImpl.handleTotemOfferTilePlacement(missingUser, 1));
        assertThrows(RemoteException.class, () -> serverImpl.handleCardPickBottomRow(missingUser, 1));
        assertThrows(RemoteException.class, () -> serverImpl.handleCardPickTopRow(missingUser, 1));
        assertThrows(RemoteException.class, () -> serverImpl.handleBuildingPickBottomRow(missingUser, 1));
        assertThrows(RemoteException.class, () -> serverImpl.handleBuildingPickTopRow(missingUser, 1));
        assertThrows(RemoteException.class, () -> serverImpl.handleSkip(missingUser));
    }

    @SuppressWarnings("unchecked")
    private void injectManagerIntoMap(String nickname, RMIClientManager manager) {
        Map<String, RMIClientManager> map = getPrivateField(serverImpl, "clientManagers");
        map.put(nickname, manager);
    }

    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get private field", e);
        }
    }
}