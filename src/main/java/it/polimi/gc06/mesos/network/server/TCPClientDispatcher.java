package it.polimi.gc06.mesos.network.server;

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
    private CompletableFuture<String> mainLoopRequest;
    private CompletableFuture<Boolean> mainLoopConfirm;
    private String nickname;

    public TCPClientDispatcher(Socket clientSocket, MatchManager sharedManager) {
        this.clientSocket = clientSocket;
        this.sharedManager = sharedManager;
        this.mainLoopRequest = new CompletableFuture<>();
        this.mainLoopConfirm = new CompletableFuture<>();
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
        mainLoopConfirm.complete(true); //necessary to avoid deadlock
        new Thread(this::receiverLoop).start();

        try {
            String input;

            //nickname handling
            nickname = mainLoopRequest.join(); //client side: nickname request
            mainLoopRequest = new CompletableFuture<>();
            while (!sharedManager.login(nickname)) {
                out.writeObject("KO");
                mainLoopConfirm.complete(true);
                nickname = mainLoopRequest.join();
                mainLoopRequest = new CompletableFuture<>();
            }
            out.writeObject("OK");
            mainLoopConfirm.complete(true);

            //match handling
            boolean success = false; //whether ot not the match request was dispatched
            while (!success) {
                input = mainLoopRequest.join(); //client side: create match or join match decision
                mainLoopRequest = new CompletableFuture<>();
                if (input.startsWith("CREATE")) {
                    input = input.substring(6);
                    if (!isNumeric(input) || Integer.parseInt(input) < Match.MIN_PLAYERS
                            || Integer.parseInt(input) > Match.MAX_PLAYERS) {
                        out.writeObject("KO");
                        mainLoopConfirm.complete(true);
                    } else {
                        Match newMatch = sharedManager.createMatch(Integer.parseInt(input));
                        mainLoopConfirm.completeExceptionally(new Exception());
                        sharedManager.joinMatch(newMatch.getMatchId(), new TCPClientManager(clientSocket, nickname,
                                sharedManager, in, out));
                        out.writeObject("OK");
                        success = true;
                    }
                } else if (input.startsWith("JOIN")) {
                    input = input.substring(4);
                    if (!isNumeric(input) || !sharedManager.joinMatch(Integer.parseInt(input), new TCPClientManager(
                            clientSocket, nickname, sharedManager, in, out))) {
                        out.writeObject("KO");
                        mainLoopConfirm.complete(true);
                    } else {
                        mainLoopConfirm.completeExceptionally(new Exception());
                        out.writeObject("OK");
                        success = true;
                    }
                } else {
                    out.writeObject("KO");
                    mainLoopConfirm.complete(true);
                }
            }
        } catch (IOException | IllegalArgumentException | CompletionException e) {
            System.err.print("Error while accepting player: ");
            e.printStackTrace();
            if (nickname != null) sharedManager.logout(nickname);
            mainLoopConfirm.completeExceptionally(e);
        }
    }

    //handles request different for classing client dispatching (such as available match request, logout, ping)
    //if it cannot handle the request it is sent to the main loop
    private void receiverLoop() {
        try {
            while (true) {

                mainLoopConfirm.join();
                mainLoopConfirm = new CompletableFuture<>();

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
                        mainLoopRequest.complete(input);
                }
            }
        } catch (IOException | ClassNotFoundException | CompletionException e) {
            mainLoopRequest.completeExceptionally(e);
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
