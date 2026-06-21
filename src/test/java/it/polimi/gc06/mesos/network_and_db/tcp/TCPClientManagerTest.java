package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.network.server.TCPClientManager;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TCPClientManagerTest {

    private static final int TCP_PORT = 45165;
    private static final int RMI_PORT = 1105;

    private static final AtomicInteger clientCounter = new AtomicInteger(1);
    private final List<Client> activeClients = new ArrayList<>();

    @BeforeAll
    static void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"--tcp=" + TCP_PORT, "--rmi=" + RMI_PORT});
        });
        serverThread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }
    }

    @AfterEach
    void teardown() {
        for (Client c : activeClients) {
            try {
                if (c.getModel() != null && c.getModel().getPlayer() != null) {
                    c.getServerConnection().logout(c.getModel().getPlayer().getNickname());
                }
            } catch (Exception ignored) {
            }
        }
        activeClients.clear();
    }

    private List<Client> startSimpleMatch(int n, String testName) {
        String hostNick = "Host_" + testName + "_" + clientCounter.getAndIncrement();
        ArrayList<Client> clients = new ArrayList<>();

        try {
            Client hostClient = new Client();
            hostClient.setSmallModel(new SmallModel(hostNick));
            clients.add(hostClient);
            activeClients.add(hostClient);
            hostClient.connect("TCP", "localhost", TCP_PORT);

            ServerConnection hostConn = hostClient.getServerConnection();
            assertTrue(hostConn.login(hostNick));
            Thread.sleep(300);
            int matchId = hostConn.createMatch(n, hostNick);
            Thread.sleep(300);

            for (int i = 0; i < n - 1; i++) {
                String joinerNick = "Joiner_" + testName + "_" + clientCounter.getAndIncrement();
                Client joinerClient = new Client();
                joinerClient.setSmallModel(new SmallModel(joinerNick));
                clients.add(joinerClient);
                activeClients.add(joinerClient);
                joinerClient.connect("TCP", "localhost", TCP_PORT);

                ServerConnection joinerConn = joinerClient.getServerConnection();
                assertTrue(joinerConn.login(joinerNick));
                Thread.sleep(300);
                assertTrue(joinerConn.joinMatch(matchId, joinerNick));
                Thread.sleep(300);
            }

            Thread.sleep(1500);

        } catch (Exception e) {
            fail("Match creation failed: " + e.getMessage());
        }

        return clients;
    }

    private Client getActiveClient(List<Client> clients) {
        return clients.stream().filter(c -> c.getModel().isActive()).findFirst().orElse(null);
    }

    private Client getInactiveClient(List<Client> clients) {
        return clients.stream().filter(c -> !c.getModel().isActive()).findFirst().orElse(null);
    }

    @Test
    @Order(1)
    @DisplayName("Game initialization broadcasts correct state and phase to all players")
    public void testFirstDtoSetsPhase() {
        List<Client> clients = startSimpleMatch(3, "Init");
        String expectedPhase = new PlacingTotemPhase().toString();

        for (Client client : clients) {
            assertNotNull(client.getModel(), "SmallModel should be initialized");
            assertEquals(expectedPhase, client.getModel().getPhase(), "Phase didn't change correctly");
            assertFalse(client.getModel().getOfferTrack().isEmpty(), "Offer track should be populated");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Valid controller command correctly updates the SmallModel state across clients")
    public void testReceiveCorrectDTO() {
        List<Client> clients = startSimpleMatch(3, "ValidCmd");
        Client activeClient = getActiveClient(clients);
        assertNotNull(activeClient, "No active player found");

        String activeNick = activeClient.getModel().getPlayer().getNickname();
        try {
            activeClient.getServerConnection().placeTotem(activeNick, 0);
            Thread.sleep(1000);

            for (Client client : clients) {
                assertEquals(activeNick, client.getModel().getOfferTrack().getFirst().getPlayer().getNickname(),
                        "All clients should see the totem placed by the active player");
            }
        } catch (Exception e) {
            fail("Action execution failed: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Commands from inactive players are rejected and model remains untampered")
    public void testReceiveErrorDTOInactivePlayer() {
        List<Client> clients = startSimpleMatch(3, "InactiveErr");
        Client inactiveClient = getInactiveClient(clients);
        assertNotNull(inactiveClient, "No inactive player found");

        String inactiveNick = inactiveClient.getModel().getPlayer().getNickname();
        try {
            inactiveClient.getServerConnection().placeTotem(inactiveNick, 0);
            Thread.sleep(1000);

            for (Client client : clients) {
                assertNull(client.getModel().getOfferTrack().getFirst().getPlayer(),
                        "Offer track should remain empty since the action was rejected");
            }
        } catch (Exception e) {
            fail("Action execution failed: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Commands out of bounds are rejected without crashing the connection")
    public void testReceiveErrorDTOOutOfBounds() {
        List<Client> clients = startSimpleMatch(3, "OOB_Err");
        Client activeClient = getActiveClient(clients);
        assertNotNull(activeClient, "No active player found");

        String activeNick = activeClient.getModel().getPlayer().getNickname();
        try {
            activeClient.getServerConnection().placeTotem(activeNick, 999);
            Thread.sleep(1000);

            activeClient.getServerConnection().placeTotem(activeNick, 0);
            Thread.sleep(1000);

            assertEquals(activeNick, activeClient.getModel().getOfferTrack().getFirst().getPlayer().getNickname(),
                    "Client should still be able to send valid commands after an invalid one");
        } catch (Exception e) {
            fail("Action execution failed: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Multiple simultaneous matches do not interfere with each other's DTOs")
    public void testParallelMatchesIsolation() {
        List<Client> matchA = startSimpleMatch(2, "Parallel_A");
        List<Client> matchB = startSimpleMatch(2, "Parallel_B");

        Client activeA = getActiveClient(matchA);
        Client activeB = getActiveClient(matchB);

        assertNotNull(activeA, "Active player not found in Match A");
        assertNotNull(activeB, "Active player not found in Match B");

        String nickA = activeA.getModel().getPlayer().getNickname();
        try {
            activeA.getServerConnection().placeTotem(nickA, 1);
            Thread.sleep(1000);

            assertEquals(nickA, activeA.getModel().getOfferTrack().get(1).getPlayer().getNickname());

            for (Client clientB : matchB) {
                assertNull(clientB.getModel().getOfferTrack().get(1).getPlayer(),
                        "Match B should not receive DTOs from Match A");
            }
        } catch (Exception e) {
            fail("Action execution failed: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("enqueueCommand successfully adds to queue when queue is set")
    public void testEnqueueCommandSuccess() throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(byteStream);
        TCPClientManager localManager = new TCPClientManager("TestPlayer", outStream);

        BlockingQueue<ControllerCommand> queue = new LinkedBlockingQueue<>();
        localManager.setActionQueue(queue);

        ControllerCommand cmd = new ControllerCommand("TestPlayer", 0, Request.SKIP_REQUEST);
        localManager.enqueueCommand(cmd);

        assertEquals(1, queue.size(), "Queue should contain exactly one element");
        assertEquals(cmd, queue.poll(), "The command retrieved should be the one enqueued");
    }
}