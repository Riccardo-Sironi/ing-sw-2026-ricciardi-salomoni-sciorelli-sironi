package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.rmi.RMIServerInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Consumer;

public class RMIServerConnection extends UnicastRemoteObject implements ServerConnection {

    private final RMIServerInterface serverStub;
    // TODO Fix this - da cambiare ASAP con DTO, per uniformare a TCP
    //oggetto che deve ricevere il messaggio lato client
    private final Consumer<SmallModelEditor> messageHandler;

    public RMIServerConnection(String host, int port, Consumer<SmallModelEditor> messageHandler) throws Exception {
        super();
        this.messageHandler = messageHandler;
        Registry registry = LocateRegistry.getRegistry(host, port);
        this.serverStub = (RMIServerInterface) registry.lookup("MesosRMIServer");
    }

    /**
     * Sends a DTO to the intended reader client-side, uses a thread to free the caller immediately
     * since RMI calls are blocking.
     *
     * @param dto the update from the server
     *
     */
    @Override
    public void receiveDTO(SmallModelEditor dto) {
        new Thread(() -> messageHandler.accept(dto)).start();
    }


    @Override
    public void ping() throws RemoteException {
        // Just to make sure the client is still alive
    }

    @Override
    public boolean login(String nickname) throws Exception {
        return serverStub.login(nickname);
    }

    @Override
    public void logout(String nickname) throws Exception {
        serverStub.logout(nickname);
    }

    @Override
    public String getAvailableMatches() throws Exception {
        return serverStub.getAvailableMatches();
    }

    @Override
    public void createMatch(int numOfPlayers, String nickname) throws Exception {
        int matchId = serverStub.createMatch(numOfPlayers);
        serverStub.joinMatch(matchId, nickname, this);
    }

    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        return serverStub.joinMatch(matchId, nickname, this);
    }

    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {
        serverStub.handleTotemOfferTilePlacement(nickname, tileIndex);
    }

    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickBottomRow(nickname, cardIndex);
    }

    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickTopRow(nickname, cardIndex);
    }

    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickBottomRow(nickname, cardIndex);
    }

    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickTopRow(nickname, cardIndex);
    }
}

