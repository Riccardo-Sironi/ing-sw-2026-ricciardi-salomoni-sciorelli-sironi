package it.polimi.gc06.mesos.network.socket;

import it.polimi.gc06.mesos.network.server.MatchManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    private final int port;
    private final MatchManager sharedManager;

    public TCPServer(int port, MatchManager sharedManager) {
        this.port = port;
        this.sharedManager = sharedManager;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port + ".");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientDispatcher(clientSocket, sharedManager)).start();
            }
        } catch (IOException e) {
            System.err.println("Error while establishing TCP Connection: " + e.getMessage());
        }
    }


}
