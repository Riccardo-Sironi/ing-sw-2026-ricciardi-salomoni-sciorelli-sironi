package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.network.rmi.RMIServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

public class RMIServerConnection extends UnicastRemoteObject implements RMIClientInterface {

    private final RMIServerInterface serverStub;
    // TODO Fix this - da cambiare ASAP con DTO, per uniformare a TCP
    //oggetto che deve ricevere il messaggio lato client
    private final Consumer<String> messageHandler;

    public RMIServerConnection(String host, int port, Consumer<String> messageHandler) throws Exception {
        super();
        this.messageHandler = messageHandler;
        Registry registry = LocateRegistry.getRegistry(host, port);
        this.serverStub = (RMIServerInterface) registry.lookup("MesosRMIServer");
    }

    /**
     * Sends a message to the intended reader client-side, uses a thread to free the caller immediately
     * since RMI calls are blocking.
     *
     * @param message the update from the server
     * @throws RemoteException //TODO perché?
     */
    @Override
    public void receiveMessage(String message) throws RemoteException {
        new Thread(() -> messageHandler.accept(message)).start();
    }

    @Override
    public void ping() throws RemoteException {
        // Just to make sure the client is still alive
    }

    public boolean login(String nickname) throws Exception {
        return serverStub.login(nickname);
    }


    public void logout(String nickname) throws Exception {
        serverStub.logout(nickname);
    }

    public String getAvailableMatches() throws Exception {
        return serverStub.getAvailableMatches();
    }

    public void createMatch(int numOfPlayers, String nickname) throws Exception {
        int matchId = serverStub.createMatch(numOfPlayers);
        serverStub.joinMatch(matchId, nickname, this);
    }

    public boolean joinMatch(int matchId, String nickname) throws Exception {
        return serverStub.joinMatch(matchId, nickname, this);
    }

    public void placeTotem(String nickname, int tileIndex) throws Exception {
        serverStub.handleTotemOfferTilePlacement(nickname, tileIndex);
    }

    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickBottomRow(nickname, cardIndex);
    }

    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickTopRow(nickname, cardIndex);
    }

    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickBottomRow(nickname, cardIndex);
    }

    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickTopRow(nickname, cardIndex);
    }
}

