package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Handles incoming traffic from a single TCP client connection on the server.
 * This runs in its own thread, reading objects and dispatching them down to the MatchManager or GameController.
 */
public class TCPClientReceiver implements Runnable {

    private final MatchManager sharedManager;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private String nickname;
    private TCPClientManager virtualClient;

    /**
     * Wires up the receiver to a fresh socket from the listener.
     *
     * @param socket        The direct pipe to the client.
     * @param sharedManager The match manager dictating game rooms.
     */
    public TCPClientReceiver(Socket socket, MatchManager sharedManager) throws IOException {
        this.sharedManager = sharedManager;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        nickname = null;
        virtualClient = null;
    }

    /**
     * The main loop to accept new commands or string requests coming from the input stream.
     * Routes everything to the right logic and pushes back "OK"/"KO" answers.
     */
    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Object input = in.readObject();

                if (input instanceof ControllerCommand command) {
                    if (command == null) {
                        System.out.println("Received an empty command, discarding...");
                    } else {
                        String p = command.getNickname();
                        int matchID = sharedManager.getPlayersMatchId(p);
                        System.out.println("[TCP - Match " + matchID + "] Received a command: " + command.getRequest() + " from " + p);
                    }

                    if (virtualClient == null) {
                        synchronized (out) {
                            out.writeObject("KO");
                            out.flush();
                        }
                    } else if (command == null) {
                        virtualClient.sendErrorMessage("Null command sent");
                    } else {
                        virtualClient.enqueueCommand(command);
                    }

                } else {
                    String message = (String) input;

                    if (message == null) {
                        synchronized (out) {
                            out.writeObject("KO");
                            out.flush();
                        }
                    } else if (message.startsWith("LOGIN") && nickname == null && sharedManager.login(message.substring(5))) {
                        nickname = message.substring(5);
                        System.out.println("[TCP] Received a login request from " + nickname);
                        synchronized (out) {
                            out.writeObject("OK");
                            out.flush();
                        }
                    } else if (message.startsWith("CREATE") && virtualClient == null && nickname != null && isNumeric(message.substring(6))
                            && Integer.parseInt(message.substring(6)) > 1 && Integer.parseInt(message.substring(6)) < 6) {
                        int matchId = sharedManager.createMatch(Integer.parseInt(message.substring(6))).getMatchId();

                        virtualClient = new TCPClientManager(nickname, out);
                        sharedManager.joinMatch(matchId, virtualClient);
                        System.out.println("[TCP] Received a match creation request for a " + Integer.parseInt(message.substring(6)) + "-player match");
                        synchronized (out) {
                            out.writeObject(String.valueOf(matchId));
                            out.flush();
                        }
                    } else if (message.startsWith("JOIN") && virtualClient == null && nickname != null && isNumeric(message.substring(4))) {
                        virtualClient = new TCPClientManager(nickname, out);
                        int matchId = Integer.parseInt(message.substring(4));
                        System.out.println("[TCP] Received a join request to Match " + matchId + "from " + nickname);
                        if (sharedManager.joinMatch(matchId, virtualClient)) {
                            synchronized (out) {
                                out.writeObject("OK");
                                out.flush();
                            }
                        } else {
                            synchronized (out) {
                                out.writeObject("KO");
                                out.flush();
                            }
                            virtualClient = null;
                        }
                    } else if (message.equals("LOGOUT") && nickname != null) {
                        int matchId = sharedManager.getPlayersMatchId(nickname);
                        System.out.println("[TCP] Received a logout request to Match " + matchId + "from " + nickname);
                        if ((matchId == -1 || !sharedManager.isMatchRunning(matchId)) && sharedManager.logout(nickname)) {
                            nickname = null;
                            synchronized (out) {
                                out.writeObject("OK");
                                out.flush();
                            }
                        } else {
                            synchronized (out) {
                                out.writeObject("KO");
                                out.flush();
                            }
                        }
                    } else if (message.equals("MATCH_ID")) {
                        synchronized (out) {
                            out.writeObject(sharedManager.getPlayersMatchId(nickname));
                            out.flush();
                        }
                    } else if (message.startsWith("MATCH_STATUS") && isNumeric(message.substring(12))) {
                        synchronized (out) {
                            out.writeObject(sharedManager.getMatchInfo(Integer.parseInt(message.substring(12))));
                            out.flush();
                        }
                    } else if (message.equals("AVAILABLE")) {
                        synchronized (out) {
                            out.writeObject(sharedManager.getAvailableMatchesString());
                            out.flush();
                        }
                    } else {
                        synchronized (out) {
                            out.writeObject("KO");
                            out.flush();
                        }
                    }
                }
            } catch (EOFException | SocketException _) {
                System.out.println("Client ['" + (nickname != null ? nickname : "Unknown") + "'] disconnected, terminating TCP receiver...");
                break;
            } catch (ClassNotFoundException | ClassCastException | IOException e) {
                System.err.println("Unexpected error occurred during ['" + (nickname != null ? nickname : "Unknown") + "'] message reading, " +
                        "message will be ignored: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Quick helper to check if a string is actually just a number hiding in disguise.
     *
     * @param s The string to test.
     * @return True if it's purely digits.
     */
    private boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}