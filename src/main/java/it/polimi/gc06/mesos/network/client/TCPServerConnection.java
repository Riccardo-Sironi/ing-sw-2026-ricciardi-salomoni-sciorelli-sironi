package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.dtos.DTOvisitor;
import it.polimi.gc06.mesos.dtos.GameStateChangeDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
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
    private final BlockingBox<Object> result;
    private final BlockingQueue<ControllerCommand> commands;
    private final BlockingQueue<Object> responses;

    public TCPServerConnection(String host, int port) {
        this.host = host;
        this.port = port;

        listeners = new ArrayList<>();
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

    @Override
    public void startConnection() throws Exception {
        new Thread(this).start();
    }

    @Override
    public void receiveDTO(SmallModelEditor dto) {
        prioritizedListener.update(dto);
        listeners.forEach(l -> l.update(dto));
    }

    @Override
    public void ping() {
        //Does nothing (only valid in RMI)
    }

    @Override
    public boolean login(String nickname) throws Exception {
        //if the action was already performed before with success, exit
        if (nicknameSent || isInsideMatch) throw new IllegalStateException("This action shouldn't be performed now");
        if (!request.store("LOGIN" + nickname))
            throw new IllegalStateException("An action is already getting performed");
        if(((String)this.result.take()).equals("OK")){
            nicknameSent = true;
            return true;
        }
        return false;
    }

    @Override
    public void logout(String nickname) throws Exception {
        if (!nicknameSent || isInsideMatch)
            throw new IllegalStateException("Login not yet performed or match already started");
        if (!request.store("LOGOUT")) throw new IllegalStateException("An action is already getting performed");
    }

    @Override
    public int getPlayersMatchId(String nickname) throws Exception {
        if (!request.store("MATCH_ID")) throw new IllegalStateException("An action is already getting performed");
        return (Integer) this.result.take();
    }

    @Override
    public String getMatchInfo(int matchId) throws Exception {
        if (!request.store("MATCH_STATUS" + matchId))
            throw new IllegalStateException("An action is already getting performed");
        return (String) result.take();
    }

    @Override
    public String getAvailableMatches() throws Exception {
        if (isInsideMatch) throw new IllegalStateException("Match already started");
        if (!request.store("AVAILABLE")) throw new IllegalStateException("An action is already getting performed");
        return (String) result.take();
    }

    @Override
    public int createMatch(int numOfPlayers, String nickname) throws Exception {
        if (!nicknameSent)
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("CREATE" + numOfPlayers))
            throw new IllegalStateException("An action is already getting performed");

        String r = (String) result.take();
        if(r.equals("KO")) throw new IllegalArgumentException("Match num of player not valid.");
        else isInsideMatch = true;
        return Integer.parseInt(r);
    }

    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        if (!nicknameSent)
            throw new IllegalStateException("Login already performed or nickname not valid.");
        //if the action (or match join) was already performed before with success, exit
        if (isInsideMatch) throw new IllegalStateException("Match action already performed");
        if (!request.store("JOIN" + matchId))
            throw new IllegalStateException("An action is already getting performed");

        if(((String)this.result.take()).equals("OK")){
            nicknameSent = true;
            return true;
        }
        return false;
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

            new Thread(this::requestHandlerLoop).start();

            while(true){
                //sends command if necessary
                ControllerCommand cmd = commands.poll(5,TimeUnit.MILLISECONDS);
                if(cmd!=null) out.writeObject(cmd);

                //dispatches input
                Object input = in.readObject();
                if(input instanceof SmallModelEditor dto){
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
                    }
                    receiveDTO(dto);
                }
                else{
                    responses.put(input);
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

    private void requestHandlerLoop(){
        while (true) try{
            out.writeObject(request.look());
            Object res = null;
            result.store(responses.take());
            request.empty(); // prepares for next request
        } catch (InterruptedException | IOException _){
            return;
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