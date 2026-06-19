package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.network.server.matches.MatchManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TCPClientReceiverTest {

    private static final int TCP_PORT = 45163;
    private static final int RMI_PORT = 1103;

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

    @Test
    @Order(1)
    @DisplayName("Login successful connection")
    void testLogin() {
        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGINAlice_1");
            assertEquals("OK", in.readObject());
        } catch (Exception e) {
            fail("Communication error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Login failure duplicate nickname")
    void testLoginFail() {
        try (Socket socket1 = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
            ObjectInputStream in1 = new ObjectInputStream(socket1.getInputStream());
            out1.writeObject("LOGINBob_Dup");
            assertEquals("OK", in1.readObject());

            try (Socket socket2 = new Socket("localhost", TCP_PORT)) {
                ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
                ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream());

                out2.writeObject("LOGINBob_Dup");
                assertEquals("KO", in2.readObject());
            }
        } catch (Exception e) {
            fail("Communication error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Logout successful disconnect")
    void testLogout() {
        MatchManager manager = ServerMain.getMatchManager();
        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGINCarl_Out");
            assertEquals("OK", in.readObject());

            out.writeObject("LOGOUT");
            assertEquals("OK", in.readObject());

            Thread.sleep(500);
            assertFalse(manager.isUserLogged("Carl_Out"));
        } catch (Exception e) {
            fail("Communication error.");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Login granted after previous logout")
    void testLoginAfterLogout() {
        String loginMsg = "LOGINDavid_Relog";
        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(loginMsg);
            assertEquals("OK", in.readObject());

            out.writeObject("LOGOUT");
            assertEquals("OK", in.readObject());
            Thread.sleep(500);
        } catch (Exception e) {
            fail("Communication error.");
        }

        try (Socket socket2 = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
            ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream());

            out2.writeObject(loginMsg);
            assertEquals("OK", in2.readObject());
        } catch (Exception e) {
            fail("Communication error.");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3})
    @Order(5)
    @DisplayName("Match creation size validation")
    void testMatchCreation(int numOfPlayers) {
        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGINEve_Creator");
            assertEquals("OK", in.readObject());

            out.writeObject("CREATE1");
            assertEquals("KO", in.readObject());

            out.writeObject("CREATE" + numOfPlayers);
            assertNotEquals("KO", in.readObject());
        } catch (Exception e) {
            fail("Communication error.");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Match join edge cases validation")
    void testMatchJoin() {
        int matchId = createMatch("Frank_Host", 5);

        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGINGrace_Joiner");
            assertEquals("OK", in.readObject());

            out.writeObject("JOINa!'/");
            assertEquals("KO", in.readObject());

            out.writeObject("JOIN9999");
            assertEquals("KO", in.readObject());

            out.writeObject("JOIN" + matchId);
            assertEquals("OK", in.readObject());
        } catch (Exception e) {
            fail("Communication error.");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3})
    @Order(7)
    @DisplayName("Match auto start on full capacity")
    void testMatchStart(int numOfPlayers) {
        int matchId = createMatch("Henry_Host", numOfPlayers);

        for (int i = 0; i < numOfPlayers - 1; i++) {
            joinMatch("Joiner_" + i, matchId);
        }

        assertTrue(ServerMain.getMatchManager().hasMatchStarted(matchId));
        assertFalse(ServerMain.getMatchManager().hasMatchEnded(matchId));
    }

    @ParameterizedTest
    @ValueSource(ints = {3})
    @Order(8)
    @DisplayName("Multiple creations and available matches list")
    void testMultipleMatchCreation(int n) {
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            createMatch("MultiHost_" + i, r.nextInt(2, 6));
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
        String available = ServerMain.getMatchManager().getAvailableMatchesString("AnyUser");
        assertNotNull(available);
    }

    private int createMatch(String nickname, int numOfPlayers) {
        try {
            Socket socket = new Socket("localhost", TCP_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGIN" + nickname);
            assertEquals("OK", in.readObject());

            out.writeObject("CREATE" + numOfPlayers);
            while (true) {
                Object response = in.readObject();
                if ("KO".equals(response)) {
                    fail("Match creation rejected");
                    return -1;
                }
                if (response instanceof String str && !str.isEmpty() && str.chars().allMatch(Character::isDigit)) {
                    return Integer.parseInt(str);
                }
            }
        } catch (Exception e) {
            fail("Error in createMatch");
            return -1;
        }
    }

    private void joinMatch(String nickname, int id) {
        try {
            Socket socket = new Socket("localhost", TCP_PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("LOGIN" + nickname);
            assertEquals("OK", in.readObject());

            out.writeObject("JOIN" + id);
            Object response;
            while (true) {
                response = in.readObject();
                if ("OK".equals(response) || "KO".equals(response)) {
                    break;
                }
            }
            assertEquals("OK", response);
        } catch (Exception e) {
            fail("Error in joinMatch");
        }
    }
}