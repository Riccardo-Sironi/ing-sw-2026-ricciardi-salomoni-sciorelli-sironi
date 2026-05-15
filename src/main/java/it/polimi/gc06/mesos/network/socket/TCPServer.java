package it.polimi.gc06.mesos.network.socket;

import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.server.TCPClientDispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    private final int port;
    private final MatchManager sharedManager;
    private boolean running;
    private ServerSocket serverSocket;

    public TCPServer(int port, MatchManager sharedManager) {
        this.port = port;
        this.sharedManager = sharedManager;
        running = true;
        serverSocket = null;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP listening on port " + port + ".");
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new TCPClientDispatcher(clientSocket, sharedManager)).start();
            }
        } catch (IOException e) {
            System.err.println("Error while establishing TCP Connection: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error while closing server socket (TCP)");
                    e.printStackTrace();
                }
            }
        }
    }
}
