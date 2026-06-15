package it.polimi.gc06.mesos.network.socket;

import it.polimi.gc06.mesos.network.server.matches.MatchManager;
import it.polimi.gc06.mesos.network.server.TCPClientReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Spins up the big TCP listener on the server side.
 * Catches incoming players knocking on the specified port.
 */
public class TCPServer implements Runnable {
    private final int port;
    private final MatchManager sharedManager;
    private boolean running;
    private ServerSocket serverSocket;
    private final String ipAddress;

    /**
     * Builds the listener logic, ready to be tossed into a thread.
     *
     * @param port          the TCP port to hog.
     * @param sharedManager the core MatchManager tracking all rooms.
     */
    public TCPServer(int port, MatchManager sharedManager, String ipAddress) {
        this.port = port;
        this.sharedManager = sharedManager;
        running = true;
        serverSocket = null;
        this.ipAddress = ipAddress;
    }

    public TCPServer(int port, MatchManager sharedManager) {
        this.port = port;
        this.sharedManager = sharedManager;
        running = true;
        serverSocket = null;
        this.ipAddress = null;
    }

    /**
     * Sits and spins, waiting for sockets. Each new connection
     * gets thrown to a fresh TCPClientReceiver to handle its business.
     */
    @Override
    public void run() {
        try {
            if (ipAddress != null) {
                java.net.InetAddress addr = java.net.InetAddress.getByName(ipAddress);
                serverSocket = new ServerSocket(port, 50, addr);
                System.out.println("TCP listening on IP " + ipAddress + ", port " + port);
            } else {
                serverSocket = new ServerSocket(port);
                System.out.println("TCP listening on port " + port);
            }
            while (running) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new TCPClientReceiver(clientSocket, sharedManager)).start();
            }
        } catch (IOException e) {
            System.err.println("Error while establishing TCP Connection: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error while closing server socket (TCP) " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
