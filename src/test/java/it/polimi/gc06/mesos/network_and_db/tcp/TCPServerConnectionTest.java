package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.ServerConnection;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TCPServerConnectionTest {

    private List<Client> activeClients = new ArrayList<>();

    @BeforeAll
    static void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"--tcp=1234", "--rmi=1099"});
        });
        serverThread.setDaemon(true);
        serverThread.start();
        try {
            Thread.sleep(1500);
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
    public void testLogin() throws ConnectException {
        String nickname = "Alice_" + UUID.randomUUID().toString().substring(0, 4);
        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        c.connect("TCP", "localhost", 1234);
        activeClients.add(c);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "Login failed");
        } catch (Exception e) {
            fail("Login failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testMatchCreation() throws ConnectException {
        String nickname = "Alice_" + UUID.randomUUID().toString().substring(0, 4);
        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        c.connect("TCP", "localhost", 1234);
        activeClients.add(c);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "Login failed");
            conn.createMatch(5, nickname);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Match creation failed due to exception: ");
        }
    }

    @Test
    public void testMatchJoin() throws ConnectException {
        String unique = UUID.randomUUID().toString().substring(0, 4);
        String nickname = "Alice_" + unique;
        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        c.connect("TCP", "localhost", 1234);
        activeClients.add(c);
        ServerConnection conn = c.getServerConnection();

        int matchId = -1;
        try {
            assertTrue(conn.login(nickname), "Login failed");
            matchId = conn.createMatch(5, nickname);
        } catch (Exception e) {
            fail("Match creation failed due to exception: " + e.getMessage());
        }

        String nickname2 = "Bob_" + unique;
        Client c2 = new Client();
        c2.setSmallModel(new SmallModel(nickname2));
        c2.connect("TCP", "localhost", 1234);
        activeClients.add(c2);
        ServerConnection conn2 = c2.getServerConnection();
        try {
            assertTrue(conn2.login(nickname2), "Login failed");
            assertTrue(conn2.joinMatch(matchId, nickname2));
        } catch (Exception e) {
            fail("Match join failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testMatchStart() throws ConnectException {
        createMatchAndStart(3, "Start");
    }

    private List<Client> createMatchAndStart(int n, String suffix) throws ConnectException {
        String unique = UUID.randomUUID().toString().substring(0, 4);
        String nickname = "Alice_" + suffix + "_" + unique;
        ArrayList<Client> clients = new ArrayList<>();

        Client c = new Client();
        c.setSmallModel(new SmallModel(nickname));
        clients.add(c);
        activeClients.add(c);

        c.connect("TCP", "localhost", 1234);
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
            String nickname2 = "Bob_" + suffix + "_" + i + "_" + unique;
            Client c2 = new Client();
            c2.setSmallModel(new SmallModel(nickname2));
            clients.add(c2);
            activeClients.add(c2);
            c2.connect("TCP", "localhost", 1234);
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

    private Client getActiveClient(List<Client> clients) {
        return clients.stream().filter(c -> c.getModel().isActive()).findFirst().orElse(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {500})
    public void testFirstPhase(int timeout) throws Exception {
        int n = 3;
        List<Client> clients = createMatchAndStart(n, "FirstPhase");

        for (int i = 0; i < n; i++) {
            Client active = getActiveClient(clients);
            if (active == null) fail("Active player not found.");
            try {
                active.getServerConnection().placeTotem(active.getModel().getPlayer().getNickname(), i);
                Thread.sleep(timeout);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception during placing totem phase");
            }
        }

        Thread.sleep(timeout);
        assertTrue(clients.stream().map(c -> c.getModel().getPhase())
                        .allMatch(p -> p != null && p.equals(new OfferResolutionPhase().toString())),
                "Phase didn't change in specified time requirements");
    }
}