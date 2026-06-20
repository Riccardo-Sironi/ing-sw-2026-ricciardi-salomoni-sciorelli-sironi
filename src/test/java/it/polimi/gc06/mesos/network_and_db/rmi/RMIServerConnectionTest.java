package it.polimi.gc06.mesos.network_and_db.rmi;

import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RMIServerConnectionTest {

    private static final int TCP_PORT = 45177;
    private static final int RMI_PORT = 1119;
    private final List<Client> activeClients = new ArrayList<>();

    private static final AtomicInteger clientCounter = new AtomicInteger(1);

    @BeforeAll
    static void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"--tcp=" + TCP_PORT, "--rmi=" + RMI_PORT});
        });
        serverThread.setDaemon(true);
        serverThread.start();
        try {
            Thread.sleep(3000); // RMI impiega un po' di più ad avviare il Registry
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
    @Order(1)
    @DisplayName("Login via RMI establishes connection successfully")
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
    @Order(2)
    @DisplayName("Match creation via RMI works properly")
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
    @Order(3)
    @DisplayName("Join an existing match via RMI")
    public void testMatchJoinRMI() {
        // Assegniamo i nomi prima, per evitare disallineamenti tra Model e Login
        String hostNick = "Host_" + clientCounter.getAndIncrement();
        String joinerNick = "Joiner_" + clientCounter.getAndIncrement();

        // Host setup
        Client c1 = new Client();
        c1.setSmallModel(new SmallModel(hostNick));
        try {
            c1.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        activeClients.add(c1);

        // Joiner setup
        Client c2 = new Client();
        c2.setSmallModel(new SmallModel(joinerNick));
        try {
            c2.connect("RMI", "localhost", RMI_PORT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        activeClients.add(c2);

        try {
            c1.getServerConnection().login(hostNick);
            int matchId = c1.getServerConnection().createMatch(5, hostNick);

            c2.getServerConnection().login(joinerNick);
            assertTrue(c2.getServerConnection().joinMatch(matchId, joinerNick), "RMI Join failed");
        } catch (Exception e) {
            fail("Match join failed: " + e.getMessage());
        }
    }

    private List<Client> createMatchAndStartRMI(int n) throws Exception {
        String nickname = "Alice_" + clientCounter.getAndIncrement();
        ArrayList<Client> clients = new ArrayList<>();

        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        clients.add(c);
        activeClients.add(c);

        // CORREZIONE: Impostato su RMI invece di TCP
        c.connect("RMI", "localhost", RMI_PORT);
        ServerConnection conn = c.getServerConnection();

        int matchId = -1;
        try {
            assertTrue(conn.login(nickname), "'" + nickname + "' login failed");
            matchId = conn.createMatch(n, nickname);
        } catch (Exception e) {
            fail("Match creation failed due to exception.");
        }

        for (int i = 0; i < n - 1; i++) {
            String nickname2 = "Bob_" + clientCounter.getAndIncrement();
            Client c2 = new Client();
            c2.setSmallModel(new SmallModel(nickname2));
            clients.add(c2);
            activeClients.add(c2);

            // CORREZIONE: Impostato su RMI invece di TCP
            c2.connect("RMI", "localhost", RMI_PORT);
            ServerConnection conn2 = c2.getServerConnection();

            try {
                assertTrue(conn2.login(nickname2), "'" + nickname2 + "' login failed");
                conn2.joinMatch(matchId, nickname2);
            } catch (Exception e) {
                fail("'" + nickname2 + "' match join failed due to exception: " + e.getMessage());
            }
        }

        try {
            Thread.sleep(1500); // Attesa fisiologica per la propagazione iniziale della partita
        } catch (InterruptedException ignored) {
        }

        String expectedPhase = new PlacingTotemPhase().toString();

        for (Client client : clients) {
            assertEquals(expectedPhase, client.getModel().getPhase(), client.getModel().getPlayer().getNickname() + " doesn't see the game start.");
        }

        return clients;
    }

    @Test
    @Order(4)
    @DisplayName("Match transitions to start phase successfully over RMI")
    public void testMatchStartRMI() throws Exception {
        createMatchAndStartRMI(3);
    }

    @ParameterizedTest
    @ValueSource(ints = {1000})
    @Order(5)
    @DisplayName("Full Totem Placement Phase over RMI")
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