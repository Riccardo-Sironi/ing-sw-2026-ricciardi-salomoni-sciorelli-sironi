package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.dtos.DTOvisitor;
import it.polimi.gc06.mesos.dtos.LeaderboardChangeDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.socket.BlockingBox;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServerConnection implements ServerConnection, Runnable {

    //socket data
    private final String host;
    private final int port;

    //output and input
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //listeners
    private Client prioritizedListener;

    //state
    private volatile boolean nicknameSent;
    private volatile boolean isInsideMatch;

    //futures and result
    //if isDone it means that the value was accepted, otherwise it is emptied
    private final BlockingBox<String> request;
    private final BlockingBox<Object> result;
    private final BlockingQueue<ControllerCommand> commands;
    private final BlockingQueue<Object> responses;

    public TCPServerConnection(String host, int port) {
        this.host = host;
        this.port = port;

        prioritizedListener = null;

        request = new BlockingBox<>();
        result = new BlockingBox<>();
        commands = new LinkedBlockingQueue<>();
        responses = new LinkedBlockingQueue<>();

        out = null;
        in = null;

        nicknameSent = false;
        isInsideMatch = false;
    }

    /**
     * Kicks off the parallel connection loop threads so the client starts listening and chatting.
     */
    @Override
    public void startConnection() throws Exception {
        new Thread(this).start();
    }

    /**
     * Drops a model update straight to the listeners so the UI knows what happened.
     *
     * @param dto the newest game patch.
     */
    @Override
    public void receiveDTO(SmallModelEditor dto) {
        System.out.println("'" + Thread.currentThread().getName() + "' client received a dto: " + dto.getClass().getSimpleName());
        prioritizedListener.update(dto);
    }

    /**
     * Ghost ping just because the interface asks for it. Only RMI cares about this!
     */
    @Override
    public void ping() {
        //Does nothing (only valid in RMI)
    }

    /**
     * Try to send your nickname over to see if the server lets you in.
     *
     * @param nickname Your chosen username.
     * @return True if you're good to go, false otherwise.
     */
    @Override
    public boolean login(String nickname) throws Exception {
        //if the action was already performed before with success, exit
        if (nicknameSent || isInsideMatch) throw new IllegalStateException("This action shouldn't be performed now");
        if (!request.store("LOGIN" + nickname))
            throw new IllegalStateException("An action is already getting performed");
        if (((String) this.result.take()).equals("OK")) {
            nicknameSent = true;
            return true;
        }
        return false;
    }

    /**
     * Tell the server you're heading out.
     *
     * @param nickname The name to logout from.
     */
    @Override
    public void logout(String nickname) throws Exception {
        if (!nicknameSent || isInsideMatch)
            throw new IllegalStateException("Login not yet performed or match already started");
        if (!request.store("LOGOUT")) throw new IllegalStateException("An action is already getting performed");
    }

    /**
     * Ask the server which match lobby you currently belong to.
     *
     * @return The ID of your current match setup.
     */
    @Override
    public int getPlayersMatchId(String nickname) throws Exception {
        if (!request.store("MATCH_ID")) throw new IllegalStateException("An action is already getting performed");
        return (Integer) this.result.take();
    }

    /**
     * Pulls the detailed status info mapping roughly how many are inside the given ID.
     *
     * @param matchId Target match footprint.
     * @return A status description line.
     */
    @Override
    public String getMatchInfo(int matchId) throws Exception {
        if (!request.store("MATCH_STATUS" + matchId))
            throw new IllegalStateException("An action is already getting performed");
        return (String) result.take();
    }

    /**
     * Grabs a printable text dump of any match that still has open seats.
     *
     * @return String representation of available match IDs and player counts.
     */
    @Override
    public String getAvailableMatches() throws Exception {
        if (isInsideMatch) throw new IllegalStateException("Match already started");
        if (!request.store("AVAILABLE")) throw new IllegalStateException("An action is already getting performed");
        return (String) result.take();
    }

    /**
     * Creates up a brand-new game lobby.
     *
     * @param numOfPlayers Target max seats (2, 3, 4, 5).
     * @param nickname     Who is booking the place.
     * @return The resulting Match ID so you know where you landed.
     */
    @Override
    public int createMatch(int numOfPlayers, String nickname) throws Exception {
        if (!nicknameSent)
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("CREATE" + numOfPlayers))
            throw new IllegalStateException("An action is already getting performed");

        String r = (String) result.take();
        if (r.equals("KO")) throw new IllegalArgumentException("Match num of player not valid.");
        else isInsideMatch = true;
        return Integer.parseInt(r);
    }

    /**
     * Tries joining an already created game.
     *
     * @param matchId  The room you want in.
     * @param nickname Your alias.
     * @return True if you slid in successfully.
     */
    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        if (!nicknameSent)
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("JOIN" + matchId))
            throw new IllegalStateException("An action is already getting performed");

        if (((String) this.result.take()).equals("OK")) {
            isInsideMatch = true;
            return true;
        }
        return false;
    }

    /**
     * Let the server know exactly where you are slamming your totem piece.
     *
     * @param nickname  Who you are.
     * @param tileIndex The track slot code (0-based).
     */
    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, tileIndex, Request.OFFER_TRACK_REQUEST));
    }

    /**
     * Ask the server to grab a card from the bottom display row.
     *
     * @param nickname  The acting player nickname.
     * @param cardIndex The column location requested.
     */
    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.BOTTOM_CARD_REQUEST));
    }

    /**
     * Ask the server to snatch a card off the top display row instead.
     */
    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.TOP_CARD_REQUEST));
    }

    /**
     * Asks the server to grab a specialized building piece from the bottom queue.
     */
    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.BOTTOM_BUILDING_REQUEST));
    }

    /**
     * Asks the server if you can secure a building directly from the top menu.
     */
    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.TOP_BUILDING_REQUEST));
    }

    /**
     * Asks the server if you can skip your turn and let the next player go instead.
     */
    @Override
    public void handleSkip(String nickname) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, 0, Request.SKIP_REQUEST));
    }

    /**
     * Sends your preferred totem color over to server.
     */
    @Override
    public void chooseTotemColor(String nickname, Color color) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, color.ordinal(), Request.CHOOSE_TOTEM_COLOR_REQUEST));
    }

    /**
     * The main loop keeping the TCP engine alive.
     * Manages input socket streams and routes them back or sets endgame statuses.
     */
    @Override
    public void run() {

        Thread reqThread = new Thread(this::requestHandlerLoop);
        Thread cmdThread = new Thread(this::commandHandlerLoop);

        try (Socket socket = new Socket(host, port)) {

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            reqThread.start();
            cmdThread.start();

            while (true) {
                // TODO Come in TCPClientReceiver, il prof. Cugola ha approvato l'uso in questo caso di instanceof. Ma è davvero necessario? Se possibile sarebbe carino toglierlo.
                //dispatches input
                Object input = in.readObject();
                if (input instanceof SmallModelEditor dto) {
                    AtomicBoolean isEndgame = new AtomicBoolean();
                    DTOvisitor visitor = new DTOvisitor() {
                        @Override
                        public void visit(LeaderboardChangeDTO dto) {
                            isEndgame.set(true);
                        }
                    };
                    visitor.visit(dto);
                    if (isEndgame.get()) {
                        isInsideMatch = false;
                    }
                    receiveDTO(dto);
                } else {
                    responses.put(input);
                }
            }

        } catch (ClassNotFoundException | EOFException _) {
            System.err.println("Server connection ended.");
        } catch (IOException e) {
            System.err.println("Invalid data for server communication.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Runtime error during DTO processing!");
            e.printStackTrace();
        } finally {
            reqThread.interrupt();
            cmdThread.interrupt();
        }
    }

    /**
     * Sits running forever to dump synchronous state requests (like login) onto the out queue.
     */
    private void requestHandlerLoop() {
        while (true) try {
            String req = request.look();
            synchronized (out) {
                out.writeObject(req);
                out.flush();
                out.reset();
            }
            result.store(responses.take());
            request.empty(); // prepares for next request
        } catch (InterruptedException | IOException _) {
            return;
        }
    }

    /**
     * Polling mechanism that drains your controller actions and shoves them to the output stream.
     */
    private void commandHandlerLoop() {
        while (true) try {
            ControllerCommand cmd = commands.poll(5, TimeUnit.MILLISECONDS);
            if (cmd != null) {
                synchronized (out) {
                    out.writeObject(cmd);
                    out.flush();
                    out.reset();
                }
            }
        } catch (IOException | InterruptedException _) {
        }
    }

    /**
     * Pulls the plug on an active listener when it's done rendering frames.
     */
    @Override
    public void unsubscribe(Client listener) {
        if (listener.equals(prioritizedListener)) prioritizedListener = null;
    }

    /**
     * Register a new prioritized component waiting for incoming network updates.
     */
    @Override
    public void prioritizedSubscribe(Client listener) {
        prioritizedListener = listener;
    }
}