package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.network.rmi.RMIServerInterfaceImpl;
import it.polimi.gc06.mesos.network.socket.TCPServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMain {
    /**
     * Main method to start the server.
     *
     * @param args the first argument is the TCP port number, the second argument is the RMI port number
     */
    static void main(String[] args) {
        int tcpPortNumber = 1234;
        int RMIPortNumber = 1099;

        try {
            try {
                if (args.length >= 1) tcpPortNumber = Integer.parseInt(args[0]);
                if (args.length >= 2) RMIPortNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse server startup arguments: invalid TCP or RMI port number.");
                e.printStackTrace();
                return;
            }

            MatchManager sharedManager = new MatchManager();

            System.out.println("Server started");

            try {
                RMIServerInterfaceImpl rmiImpl = new RMIServerInterfaceImpl(sharedManager);
                Registry registry = LocateRegistry.createRegistry(RMIPortNumber);
                registry.rebind("MesosServer", rmiImpl);
                System.out.println("RMI started on port " + RMIPortNumber);
            } catch (Exception e) {
                System.err.println("Failed to start RMI server component on port " + RMIPortNumber + ".");
                e.printStackTrace();
                return;
            }

            try {
                TCPServer tcpServer = new TCPServer(tcpPortNumber, sharedManager);
                Thread tcpServerThread = new Thread(tcpServer);
                tcpServerThread.start();
            } catch (Exception e) {
                System.err.println("Failed to start TCP server component on port " + tcpPortNumber + ".");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Unexpected error while starting server components.");
            e.printStackTrace();
        }
    }
}
