package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.socket.commands.ControllerCommand;
import it.polimi.gc06.mesos.network.socket.commands.Request;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

//WARNING: Class is not thread safe! Only one thread running this class should be active and only
//one other thread or main thread should use other methods.
public class TCPServerConnection implements ServerConnection, Runnable{

    //socket data
    private final String host;
    private final int port;

    //output and input
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //listeners
    private final List<ModelListener> listeners;
    private Client prioritizedListener;

    //futures and result
    //if isDone it means that the value was accepted, otherwise it is emptied
    private CompletableFuture<String> futureNickname;
    private CompletableFuture<String> futureCreateRequest;
    private CompletableFuture<String> futureJoinRequest;
    private CompletableFuture<Boolean> futureSuccess; //represents whether the last request was successful
    private CompletableFuture<String> availableMatches;
    private BlockingQueue<ControllerCommand> commands;

    public TCPServerConnection(String host, int port){
        this.host = host;
        this.port = port;

        listeners = new ArrayList<>();
        prioritizedListener = null;

        futureNickname = new CompletableFuture<>();
        futureSuccess = new CompletableFuture<>();
        futureCreateRequest = new CompletableFuture<>();
        futureJoinRequest = new CompletableFuture<>();
        commands = new LinkedBlockingQueue<>();
        availableMatches = new CompletableFuture<>();

        out = null;
        in = null;
    }

    @Override
    public void receiveDTO(SmallModelEditor dto) {
        prioritizedListener.update(dto);
        listeners.forEach(l -> l.update(dto));
    }

    @Override
    public void ping() throws RemoteException {
        //TODO: what to put here?
    }

    @Override
    public boolean login(String nickname) throws Exception {
        //if the action was already performed before with success, exit
        if(!futureNickname.complete(nickname)) throw new IllegalStateException();
        boolean success = futureSuccess.join();
        futureSuccess = new CompletableFuture<>(); //reset success
        return success;
    }

    @Override
    public void logout(String nickname) throws Exception {
        //TODO: what to put here
    }

    @Override
    public String getAvailableMatches() throws Exception {
        return null; //TODO
    }

    @Override
    public void createMatch(int numOfPlayers, String nickname) throws Exception {
        //if the action (or match join) was already performed before with success, exit
        if(futureJoinRequest.isDone() || !futureCreateRequest.complete("CREATE "+numOfPlayers)) throw new IllegalStateException();
        boolean success = futureSuccess.join();
        futureSuccess = new CompletableFuture<>(); //reset success
        if(!success) throw new IllegalArgumentException("Match num of player not valid.");
    }

    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        //if the action (or match creation) was already performed before with success, exit
        if(futureCreateRequest.isDone() || !futureJoinRequest.complete("JOIN "+matchId)) throw new IllegalStateException();
        boolean success = futureSuccess.join();
        futureSuccess = new CompletableFuture<>(); //reset success
        return success;
    }

    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {
        commands.put(new ControllerCommand(nickname,tileIndex, Request.OFFER_TRACK_REQUEST));
    }

    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        commands.put(new ControllerCommand(nickname,cardIndex, Request.BOTTOM_CARD_REQUEST));
    }

    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        commands.put(new ControllerCommand(nickname,cardIndex, Request.TOP_CARD_REQUEST));
    }

    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        commands.put(new ControllerCommand(nickname,cardIndex, Request.BOTTOM_BUILDING_REQUEST));
    }

    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception{
        commands.put(new ControllerCommand(nickname,cardIndex, Request.TOP_BUILDING_REQUEST));
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port)) {

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(futureNickname.join());
            while (!((String) in.readObject()).equals("OK")){
                futureSuccess.complete(false);
                futureNickname = new CompletableFuture<>(); //refused
                out.writeObject(futureNickname.join());
            }
            futureSuccess.complete(true);

            //TODO: fix create and join logic client side
            String req = (String) CompletableFuture.anyOf(futureCreateRequest, futureJoinRequest).join();
            out.writeObject(req);
            while(!((String) in.readObject()).equals("OK")){
                futureSuccess.complete(false);
                if(req.startsWith("CREATE")) futureCreateRequest = new CompletableFuture<>();
                else futureJoinRequest = new CompletableFuture<>();
                req = (String) CompletableFuture.anyOf(futureCreateRequest, futureJoinRequest).join();
                out.writeObject(req);
            }
            futureSuccess.complete(true);

            new Thread(this::DTOReceiverLoop).start(); //starts the receiver
            while (true){
                out.writeObject(commands.take());
            }

        }catch (ClassNotFoundException | EOFException e){
            System.err.println("Server connection ended.");
            e.printStackTrace();
        }catch (IOException e){
            System.err.println("Invalid data for connection to server.");
            e.printStackTrace();
        } catch (InterruptedException _) {}
    }

    private void DTOReceiverLoop(){
        try {
            while(true){
                receiveDTO((SmallModelEditor) in.readObject());
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
        if(listener.equals(prioritizedListener)) prioritizedListener = null;
    }

    @Override
    public void prioritizedSubscribe(Client listener) {
        prioritizedListener = null;
    }
}
