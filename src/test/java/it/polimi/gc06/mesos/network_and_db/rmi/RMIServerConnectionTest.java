package it.polimi.gc06.mesos.network_and_db.rmi;

import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.rmi.RMIServerInterface;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RMIServerConnectionTest {

    private static final int TCP_PORT = 45177;
    private static final int RMI_PORT = 1119;
    private static RMIServerInterface rmiStub;
    private List<Client> activeClients = new ArrayList<>();

    private static final AtomicInteger clientCounter = new AtomicInteger(1);


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

    @Test
    public void testLoginRMI() {
        String nickname = "Alice_RMI_" + clientCounter.getAndIncrement();
        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));

        try {
            c.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail("Failed to connect to RMI server: " + e.getMessage());
        }

        activeClients.add(c);

        try {
            assertTrue(c.getServerConnection().login(nickname), "RMI Login failed");
        } catch (Exception e) {
            fail("Login failed: " + e.getMessage());
        }
    }

    @Test
    public void testMatchCreationRMI() {
        String nickname = "Alice_RMI_" + clientCounter.getAndIncrement();
        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        try {
            c.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail("Failed to connect to RMI server: " + e.getMessage());
        }
        activeClients.add(c);

        try {
            c.getServerConnection().login(nickname);
            c.getServerConnection().createMatch(5, nickname);
        } catch (Exception e) {
            fail("RMI Match creation failed: " + e.getMessage());
        }
    }

    @Test
    public void testMatchJoinRMI() {
        // Host
        Client c1 = new Client();
        c1.setSmallModel(new SmallModel("Host_" + clientCounter.getAndIncrement()));
        try {
            c1.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail("Failed to connect to RMI server: " + e.getMessage());
        }
        activeClients.add(c1);

        // Joiner
        Client c2 = new Client();
        c2.setSmallModel(new SmallModel("Joiner_" + clientCounter.getAndIncrement()));
        try {
            c2.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail("Failed to connect to RMI server: " + e.getMessage());
        }
        activeClients.add(c2);

        try {
            c1.getServerConnection().login("Host_" + clientCounter.getAndIncrement());
            int matchId = c1.getServerConnection().createMatch(5, "Host_" + clientCounter.getAndIncrement());

            c2.getServerConnection().login("Joiner_" + clientCounter.getAndIncrement());
            assertTrue(c2.getServerConnection().joinMatch(matchId, "Joiner_" + clientCounter.getAndIncrement()), "RMI Join failed");
        } catch (Exception e) {
            fail("Match join failed: " + e.getMessage());
        }
    }

    @Test
    public void testMatchStartRMI() throws ConnectException {
        createMatchAndStartRMI(3);
    }

    private List<Client> createMatchAndStartRMI(int n) throws ConnectException {
        String nickname = "Alice_" + clientCounter.getAndIncrement();
        ArrayList<Client> clients = new ArrayList<>();

        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        clients.add(c);
        activeClients.add(c);

        c.connect("TCP", "localhost", TCP_PORT);
        ServerConnection conn = c.getServerConnection();

        int matchId = -1;
        try {
            assertTrue(conn.login(nickname), "'" + nickname + "' login failed");
            matchId = conn.createMatch(n, nickname);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Match creation failed due to exception.");
        }

        for (int i = 0; i < n - 1; i++) {
            String nickname2 = "Bob_" + clientCounter.getAndIncrement();
            Client c2 = new Client();
            c2.setSmallModel(new SmallModel(nickname2));
            clients.add(c2);
            activeClients.add(c2);
            c2.connect("TCP", "localhost", TCP_PORT);
            ServerConnection conn2 = c2.getServerConnection();
            try {
                assertTrue(conn2.login(nickname2), "'" + nickname2 + "' login failed");
                conn2.joinMatch(matchId, nickname2);
            } catch (Exception e) {
                fail("'" + nickname2 + "' match join failed due to exception: " + e.getMessage());
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String expectedPhase = new PlacingTotemPhase().toString();

        for (Client client : clients) {
            assertEquals(expectedPhase, client.getModel().getPhase(), client.getModel().getPlayer().getNickname() + " doesn't see the game start.");
        }

        return clients;
    }

    @ParameterizedTest
    @ValueSource(ints = {1000})
    public void testFirstPhaseRMI(int timeout) throws Exception {
        int n = 3;
        List<Client> clients = createMatchAndStartRMI(n);
        Thread.sleep(1000);

        for (Client c : clients) {
            assertEquals(new PlacingTotemPhase().toString(), c.getModel().getPhase());
        }
        for (int i = 0; i < n; i++) {
            Client active = clients.stream().filter(c -> c.getModel().isActive()).findFirst().orElseThrow();
            try {
                active.getServerConnection().placeTotem(active.getModel().getPlayer().getNickname(), i);
            } catch (Exception e) {
                fail("Unexpected exception during placing totem phase");
            }
            Thread.sleep(timeout);
        }

        assertTrue(clients.stream().map(c -> c.getModel().getPhase())
                        .allMatch(p -> p != null && p.equals(new OfferResolutionPhase().toString())),
                "RMI: Phase didn't change");
    }
}

