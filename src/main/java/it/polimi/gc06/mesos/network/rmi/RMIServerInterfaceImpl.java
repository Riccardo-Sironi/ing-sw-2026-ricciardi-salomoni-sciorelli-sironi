package it.polimi.gc06.mesos.network.rmi;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.network.client.RMIClientInterface;
import it.polimi.gc06.mesos.network.client.RMIClientManager;
import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.socket.commands.ControllerCommand;
import it.polimi.gc06.mesos.network.socket.commands.Request;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RMIServerInterfaceImpl extends UnicastRemoteObject implements RMIServerInterface {
    private final MatchManager serverManager;
    private final Map<String, RMIClientManager> clientManagers;

    public RMIServerInterfaceImpl(MatchManager serverManager) throws RemoteException {
        super();
        this.serverManager = serverManager;
        this.clientManagers = new ConcurrentHashMap<>();
    }

    @Override
    public boolean login(String nickname) throws RemoteException {
        return serverManager.login(nickname);
    }

    @Override
    public boolean logout(String nickname) throws RemoteException {
        RMIClientManager manager = clientManagers.remove(nickname);
        if (manager != null) {
            manager.closeConnection();
        }
        return serverManager.logout(nickname);
    }

    @Override
    public String getAvailableMatches() throws RemoteException {
        return serverManager.getAvailableMatchesString();
    }

    @Override
    public int createMatch(int numOfPlayers) throws RemoteException {
        return serverManager.createMatch(numOfPlayers).getMatchId();
    }

    @Override
    public boolean joinMatch(int matchId, String nickname, RMIClientInterface clientCallback) throws RemoteException {
        RMIClientManager rmiClientManager = new RMIClientManager(nickname, clientCallback, serverManager);
        boolean success = serverManager.joinMatch(matchId, rmiClientManager);
        if (success) {
            clientManagers.put(nickname, rmiClientManager);
        }
        return success;
    }

    private GameController getController(String nickname) {
        RMIClientManager manager = clientManagers.get(nickname);
        return manager != null ? manager.getController() : null;
    }

    @Override
    public void handleTotemOfferTilePlacement(String nickname, int tileIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, tileIndex, Request.OFFER_TRACK_REQUEST);

        manager.enqueueCommand(command);
    }

    @Override
    public void handleCardPickBottomRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.BOTTOM_CARD_REQUEST);

        manager.enqueueCommand(command);
    }

    @Override
    public void handleCardPickTopRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.TOP_CARD_REQUEST);

        manager.enqueueCommand(command);
    }

    @Override
    public void handleBuildingPickBottomRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.BOTTOM_BUILDING_REQUEST);

        manager.enqueueCommand(command);
    }

    @Override
    public void handleBuildingPickTopRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.TOP_BUILDING_REQUEST);

        manager.enqueueCommand(command);
    }

    @Override
    public void handleSkip(String nickname) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, 0, Request.SKIP_REQUEST);

        manager.enqueueCommand(command);
    }
}