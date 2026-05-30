package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.network.leaderboard.LeaderboardDAO;
import it.polimi.gc06.mesos.network.rmi.RMIServerInterfaceImpl;
import it.polimi.gc06.mesos.network.socket.TCPServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;

/**
 * Entrypoint for the server application.
 * Initializes and starts both {@link RMIServerInterfaceImpl RMI} and {@link TCPServer TCP} (Socket) server components,
 * sharing a common {@link MatchManager} instance between them.
 */
public class ServerMain {

    private static MatchManager sharedManager = new MatchManager();

    /**
     * Main method to start the server.
     *
     * @param args the first argument is the TCP port number, the second argument is the RMI port number
     */
    public static void main(String[] args) {
        // Mesos expressed as number
        // M = 13 = 1+3 = 4, E = 5, S = 19 = 1+9 = 1, O = 15 = 6, S = 1
        int tcpPortNumber = 45161; //should be --tcp=<port>
        // Leave the RMI Port to the default value
        int RMIPortNumber = 1099; //should be --rmi=<port>
        String ipAddress = null;

        //db data
        String dbUser = null; //should be --duser=<user:password>
        String dbPwd = null;
        String dbName = null; //should be --dname=<name>
        String dbLocation = null; //should be --dloc=<ip>:<port>

        try {
            try {
                for(String arg : args){
                    if(arg.startsWith("--tcp=")) tcpPortNumber = Integer.parseInt(arg.split("=")[1]);
                    else if(arg.startsWith("--rmi=")) RMIPortNumber = Integer.parseInt(arg.split("=")[1]);
                    else if(arg.startsWith("--dname=")) dbName = arg.split("=")[1];
                    else if(arg.startsWith("--dloc")) dbLocation = arg.split("=")[1];
                    else if(arg.startsWith("--duser=")){
                        String s = arg.split("=")[1];
                        dbUser = s.split(":")[0];
                        dbPwd = s.split(":")[1];
                    }
                    else if(arg.startsWith("--ip=")) ipAddress = arg.split("=")[1];
                }
            } catch (NumberFormatException | IndexOutOfBoundsException | PatternSyntaxException e) {
                System.err.println("Failed to parse server startup arguments.");
                e.printStackTrace();
                return;
            }

            if(dbUser != null && dbPwd != null){
                LeaderboardDAO.setDbInfo(dbUser, dbPwd, dbName, dbLocation);
                try{
                    LeaderboardDAO.init();
                    System.out.println("Connected to db correctly.");
                }catch(SQLException e){
                    System.err.println("Connection to db failed.");
                }
            }
            else if(dbLocation != null || dbName != null){
                System.err.println("Cannot setup db name or location if correct user has not been given.");
            }

            System.out.println("Server started");

            //tries to start RMI protocol
            //TODO: handle ip
            try {
                RMIServerInterfaceImpl rmiImpl = new RMIServerInterfaceImpl(sharedManager);
                Registry registry = null;
                try {
                    registry = LocateRegistry.createRegistry(RMIPortNumber);
                } catch (Exception e) {
                    registry = LocateRegistry.getRegistry(RMIPortNumber);
                }
                registry.rebind("MesosRMIServer", rmiImpl);
                System.out.println("RMI started on port " + RMIPortNumber);
            } catch (Exception e) {
                System.err.println("Failed to start RMI server component on port " + RMIPortNumber + ".");
                e.printStackTrace();
                return;
            }

            //tries to start TCP protocol (socket)
            try {
                TCPServer tcpServer = null;
                if(ipAddress == null) tcpServer = new TCPServer(tcpPortNumber, sharedManager);
                else tcpServer = new TCPServer(tcpPortNumber, sharedManager, ipAddress);
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

    /**
     * MatchManager getter, should only be used in testing.
     *
     * @return the match manager.
     */
    public static MatchManager getMatchManager() {
        return sharedManager;
    }
}
