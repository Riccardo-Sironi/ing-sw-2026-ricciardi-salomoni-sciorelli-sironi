package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TCPClientReceiver implements Runnable {

    private final MatchManager sharedManager;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private String nickname;
    private TCPClientManager virtualClient;

    public TCPClientReceiver(Socket socket, MatchManager sharedManager) throws IOException {
        this.sharedManager = sharedManager;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        nickname = null;
        virtualClient = null;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Object input = in.readObject();

                if (input instanceof ControllerCommand) {
                    ControllerCommand command = (ControllerCommand) input;
                    System.out.println(command == null ? "Received an empty command, discarding..." : "Received a command from " + command.getNickname());

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
                    System.out.println("Received a message: " + message);

                    if (message == null) {
                        synchronized (out) {
                            out.writeObject("KO");
                            out.flush();
                        }
                    }
                    else if (message.startsWith("LOGIN") && nickname == null && sharedManager.login(message.substring(5))) {
                        nickname = message.substring(5);
                        synchronized (out) {
                            out.writeObject("OK");
                            out.flush();
                        }
                    }
                    else if (message.startsWith("CREATE") && virtualClient == null && nickname != null && isNumeric(message.substring(6))
                            && Integer.parseInt(message.substring(6)) > 1 && Integer.parseInt(message.substring(6)) < 6) {
                        int matchId = sharedManager.createMatch(Integer.parseInt(message.substring(6))).getMatchId();

                        virtualClient = new TCPClientManager(nickname, out);
                        sharedManager.joinMatch(matchId, virtualClient);

                        synchronized (out) {
                            out.writeObject(String.valueOf(matchId));
                            out.flush();
                        }
                    }
                    else if (message.startsWith("JOIN") && virtualClient == null && nickname != null && isNumeric(message.substring(4))) {
                        virtualClient = new TCPClientManager(nickname, out);
                        if (sharedManager.joinMatch(Integer.parseInt(message.substring(4)), virtualClient)) {
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
                    }
                    else if (message.equals("LOGOUT") && nickname != null) {
                        int matchId = sharedManager.getPlayersMatchId(nickname);
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
                    }
                    else if (message.equals("MATCH_ID")) {
                        synchronized (out) {
                            out.writeObject(sharedManager.getPlayersMatchId(nickname));
                            out.flush();
                        }
                    }
                    else if (message.startsWith("MATCH_STATUS") && isNumeric(message.substring(12))) {
                        synchronized (out) {
                            out.writeObject(sharedManager.getMatchInfo(Integer.parseInt(message.substring(12))));
                            out.flush();
                        }
                    }
                    else if (message.equals("AVAILABLE")) {
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
            } catch (EOFException | SocketException _ ) {
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

    private boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }
}