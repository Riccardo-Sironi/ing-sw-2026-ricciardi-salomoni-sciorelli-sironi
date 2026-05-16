package it.polimi.gc06.mesos.network.server;

import it.polimi.gc06.mesos.network.socket.BlockingBox;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// ClientDispatcher is needed for deciding to what Match the client should participate
public class TCPClientDispatcher implements Runnable {

    private final Socket clientSocket;
    private final MatchManager sharedManager;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final BlockingBox<String> mainLoopRequest;
    private final BlockingBox<Boolean> mainLoopConfirm;
    private String nickname;

    public TCPClientDispatcher(Socket clientSocket, MatchManager sharedManager) {
        this.clientSocket = clientSocket;
        this.sharedManager = sharedManager;
        this.mainLoopRequest = new BlockingBox<>();
        this.mainLoopConfirm = new BlockingBox<>();
    }

    @Override
    public void run() {

        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error while accepting client: ");
            e.printStackTrace();
            return;
        }

        nickname = null;
        mainLoopConfirm.store(true); //necessary to avoid deadlock
        new Thread(this::receiverLoop).start();

        try {
            String input;

            //nickname handling
            String nicknameReq = mainLoopRequest.take(); //client side: login request
            while (!nicknameReq.startsWith("LOGIN") || !sharedManager.login(nicknameReq.substring(5))) {
                out.writeObject("KO");
                mainLoopConfirm.store(true);
                nicknameReq = mainLoopRequest.take();
            }
            nickname = nicknameReq.substring(5);
            out.writeObject("OK");
            mainLoopConfirm.store(true);

            //creates dispatcher confirm
            CompletableFuture<Void> managerConfirm = new CompletableFuture<>();
            //match handling
            boolean success = false; //whether ot not the match request was dispatched
            while (!success) {
                input = mainLoopRequest.take(); //client side: create match or join match decision
                if (input.startsWith("CREATE")) {
                    input = input.substring(6);
                    if (!isNumeric(input) || Integer.parseInt(input) < Match.MIN_PLAYERS
                            || Integer.parseInt(input) > Match.MAX_PLAYERS) {
                        out.writeObject("KO");
                        mainLoopConfirm.store(true);
                    } else {
                        Match newMatch = sharedManager.createMatch(Integer.parseInt(input));
                        mainLoopConfirm.storeException(new Exception());
                        sharedManager.joinMatch(newMatch.getMatchId(), new TCPClientManager(clientSocket, nickname,
                                sharedManager, in, out, managerConfirm));
                        out.writeObject("OK");
                        managerConfirm.complete(null);
                        success = true;
                    }
                } else if (input.startsWith("JOIN")) {
                    input = input.substring(4);
                    if (!isNumeric(input) || !sharedManager.joinMatch(Integer.parseInt(input), new TCPClientManager(
                            clientSocket, nickname, sharedManager, in, out, managerConfirm))) {
                        out.writeObject("KO");
                        mainLoopConfirm.store(true);
                    } else {
                        mainLoopConfirm.storeException(new Exception());
                        out.writeObject("OK"); //manager confirm is needed or this message could go in conflict with manager (for the last player)
                        managerConfirm.complete(null); //now the client managers can start
                        success = true;
                    }
                } else {
                    out.writeObject("KO");
                    mainLoopConfirm.store(true);
                }
            }
        } catch (IOException | IllegalArgumentException | CompletionException e) {
            System.err.print("Error while accepting player: ");
            e.printStackTrace();
            if (nickname != null) sharedManager.logout(nickname);
            mainLoopConfirm.storeException(e);
        }
    }

    //handles request different for classing client dispatching (such as available match request, logout, ping)
    //if it cannot handle the request it is sent to the main loop
    private void receiverLoop() {
        try {
            while (true) {

                mainLoopConfirm.take();

                String input = (String) in.readObject();

                if (input.startsWith("MATCH_STATUS_")) {
                    // Start parsing from the 13th character (right after the _ )
                    int queryId = Integer.parseInt(input.substring(13));
                    out.writeObject(sharedManager.getMatchInfo(queryId));
                    continue;
                }

                switch (input) {
                    case "AVAILABLE":
                        out.writeObject(sharedManager.getAvailableMatchesString());
                        break;
                    case "LOGOUT":
                        if (nickname != null) sharedManager.logout(nickname);
                        break;
                    //TODO: da capire ping
                    case "PING":
                        break;
                    case "MATCH_ID":
                        out.writeObject(sharedManager.getPlayersMatchId(nickname));
                        break;
                    default:
                        mainLoopRequest.store(input);
                }
            }
        } catch (IOException | ClassNotFoundException | CompletionException e) {
            mainLoopRequest.storeException(e);
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