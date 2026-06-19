package it.polimi.gc06.mesos.network_and_db;

import it.polimi.gc06.mesos.network.server.ServerMain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class ServerIntegrationTest {

    private static final int TCP_PORT = 45164;
    private static final int RMI_PORT = 1104;

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
    @DisplayName("TCP server socket connection")
    void testTCPConnection() {
        try (Socket socket = new Socket("localhost", TCP_PORT)) {
            assertTrue(socket.isConnected());
        } catch (IOException e) {
            fail("Cannot connect to TCP server");
        }
    }

    @Test
    @DisplayName("RMI registry binding")
    void testRMIBinding() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", RMI_PORT);
            assertNotNull(registry.lookup("MesosRMIServer"));
        } catch (Exception e) {
            fail("Cannot connect to RMI server");
        }
    }
}