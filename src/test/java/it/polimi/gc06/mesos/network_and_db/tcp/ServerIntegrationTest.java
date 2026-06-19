package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.network.server.ServerMain;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class ServerIntegrationTest {
    private static Thread serverThread;

    @BeforeAll
    static void setup() {
        serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"45161", "1099"});
        });
        serverThread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testTCPConnection() {
        try (Socket socket = new Socket("localhost", 45161)) {
            assertTrue(socket.isConnected());

        } catch (IOException e) {
            fail("Cannot connect to TCP server");
        }
    }

    @Test
    void testRMIBinding() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            assertNotNull(registry.lookup("MesosRMIServer"));
        } catch (Exception e) {
            fail("Cannot connect to RMI server");
        }
    }
}