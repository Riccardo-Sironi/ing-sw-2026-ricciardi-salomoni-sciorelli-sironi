package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.DTOvisitor;
import it.polimi.gc06.mesos.dtos.GameStateChangeDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.network.socket.BlockingBox;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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
    private final List<ModelListener> listeners;
    private Client prioritizedListener;

    //state
    private volatile boolean nicknameSent;
    private volatile boolean isInsideMatch;

    //futures and result
    //if isDone it means that the value was accepted, otherwise it is emptied
    private final BlockingBox<String> request;
    private final BlockingBox<Boolean> success; //represents whether the last request was successful
    private final BlockingBox<String> matches;
    private final BlockingBox<Integer> matchID;
    private final BlockingBox<String> matchStatus;
    private final BlockingQueue<ControllerCommand> commands;

    public TCPServerConnection(String host, int port) {
        this.host = host;
        this.port = port;

        listeners = new ArrayList<>();
        prioritizedListener = null;

        request = new BlockingBox<>();
        success = new BlockingBox<>();
        matches = new BlockingBox<>();
        matchID = new BlockingBox<>();
        matchStatus = new BlockingBox<>();
        commands = new LinkedBlockingQueue<>();

        out = null;
        in = null;

        nicknameSent = false;
        isInsideMatch = false;
    }

    @Override
    public void receiveDTO(SmallModelEditor dto) {
        prioritizedListener.update(dto);
        listeners.forEach(l -> l.update(dto));
    }

    @Override
    public void ping() {
        if (!request.store("PING")) throw new IllegalStateException("An action is already getting performed");
    }

    @Override
    public boolean login(String nickname) throws Exception {
        //if the action was already performed before with success, exit
        if (nicknameSent || isInsideMatch) throw new IllegalStateException("This action shouldn't be performed now");
        if (!request.store("LOGIN"+nickname)) throw new IllegalStateException("An action is already getting performed");
        return this.success.take();
    }

    @Override
    public void logout(String nickname) throws Exception {
        if (!nicknameSent || isInsideMatch) throw new IllegalStateException("Login not yet performed or match already started");
        if (!request.store("LOGOUT")) throw new IllegalStateException("An action is already getting performed");
    }

    @Override
    public int getPlayersMatchId(String nickname) throws Exception {
        if (!request.store("MATCH_ID")) throw new IllegalStateException("An action is already getting performed");
        return this.matchID.take();
    }

    @Override
    public String getMatchInfo(int matchId) throws Exception {
        if (!request.store("MATCH_STATUS_" + matchId))
            throw new IllegalStateException("An action is already getting performed");
        return this.matchStatus.take();
    }

    @Override
    public String getAvailableMatches() throws Exception {
        if (isInsideMatch) throw new IllegalStateException("Match already started");
        if (!request.store("AVAILABLE")) throw new IllegalStateException("An action is already getting performed");
        return this.matches.take();
    }

    @Override
    public int createMatch(int numOfPlayers, String nickname) throws Exception {
        if (!nicknameSent || !login(nickname))
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("CREATE" + numOfPlayers))
            throw new IllegalStateException("An action is already getting performed");

        boolean success = this.success.take();
        if (!success) throw new IllegalArgumentException("Match num of player not valid.");
        return 0;
    }

    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        if (!nicknameSent || !login(nickname))
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("JOIN" + matchId))
            throw new IllegalStateException("An action is already getting performed");

        boolean success = this.success.take();
        return success;
    }

    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, tileIndex, Request.OFFER_TRACK_REQUEST));
    }

    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.BOTTOM_CARD_REQUEST));
    }

    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.TOP_CARD_REQUEST));
    }

    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.BOTTOM_BUILDING_REQUEST));
    }

    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception {
        if (!isInsideMatch) throw new IllegalStateException("Not inside a match yet.");
        commands.put(new ControllerCommand(nickname, cardIndex, Request.TOP_BUILDING_REQUEST));
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port)) {

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String req;
            while (true) {
                while (!isInsideMatch) {

                    req = request.look(); //takes request

                    //request dispatch
                    if (req.equals("AVAILABLE") || req.startsWith("MATCH_STATUS_")) {
                        //request that needs a matches result
                        out.writeObject(req);
                        matches.store((String) in.readObject());
                    } else if (req.equals("PING") || req.equals("LOGOUT")) {
                        //request that do not need a result
                        out.writeObject(req);
                        if(req.equals("LOGOUT")) nicknameSent = false;
                    } else if (req.equals("MATCH_ID")) {
                        // request that needs a match ID
                        out.writeObject(req);
                        matchID.store((Integer) in.readObject());
                    } else {
                        //request that needs a confirmation
                        out.writeObject(req);
                        if(((String) in.readObject()).equals("OK")){
                            success.store(true);
                            if(req.startsWith("LOGIN")) nicknameSent = true;
                            else isInsideMatch = true;
                        }
                        else success.store(false);
                    }

                    request.empty(); //permits other actions
                }

                new Thread(this::DTOReceiverLoop).start(); //starts the receiver
                while (isInsideMatch) {
                    ControllerCommand cmd = commands.poll(100, TimeUnit.MILLISECONDS);
                    if (cmd != null) out.writeObject(cmd);
                }
            }

        } catch (ClassNotFoundException | EOFException e) {
            System.err.println("Server connection ended.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Invalid data for server communication.");
            e.printStackTrace();
        } catch (InterruptedException _) {
        }
    }

    private void DTOReceiverLoop() {
        try {
            while (true) {
                SmallModelEditor dto = (SmallModelEditor) in.readObject();
                AtomicBoolean isEndgame = new AtomicBoolean();
                DTOvisitor visitor = new DTOvisitor() {
                    @Override
                    public void visit(GameStateChangeDTO dto) {
                        isEndgame.set(dto.isEndgame());
                    }
                };
                visitor.visit(dto);
                if (isEndgame.get()) {
                    isInsideMatch = false;
                    receiveDTO(dto);
                    return;
                }
                receiveDTO(dto);

            }
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Server connection ended or fatal error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void subscribe(ModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(ModelListener listener) {
        listeners.remove(listener);
        if (listener.equals(prioritizedListener)) prioritizedListener = null;
    }

    @Override
    public void prioritizedSubscribe(Client listener) {
        prioritizedListener = listener;
    }
}