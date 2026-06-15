package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.rmi.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Client-side component handling the RMI connection to the server.
 * It sends local player commands to the server and receives game state updates.
 */
public class RMIServerConnection extends UnicastRemoteObject implements ServerConnection {
    private Client prioritizedListener = null;
    private RMIServerInterface serverStub;
    private String nickname = null;

    private final String host;
    private final int port;

    /**
     * Prepares a new RMI connection aiming at the specified server.
     *
     * @param host the server's IP address or hostname
     * @param port the server's RMI port
     * @throws Exception if something goes wrong during initialization
     */
    public RMIServerConnection(String host, int port, int clientPort) throws Exception {
        super(clientPort);
        this.host = host;
        this.port = port;
    }

    /**
     * Connects to the RMI registry and looks up the remote server object.
     *
     * @throws RemoteException   if there's a network error connecting to the registry
     * @throws NotBoundException if the server object isn't found
     */
    public void startConnection() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        this.serverStub = (RMIServerInterface) registry.lookup("MesosRMIServer");

    }

    /**
     * Receives a game state update from the server and notifies all local listeners.
     * Uses a separate thread so the server doesn't get blocked.
     *
     * @param dto the update from the server
     */
    @Override
    public void receiveDTO(SmallModelEditor dto) {
        new Thread(() -> {
            if (prioritizedListener != null) prioritizedListener.update(dto);
        }).start();
    }

    /**
     * A simple hook used by the server to check if the client is still alive.
     *
     * @throws RemoteException if the network connection breaks
     */
    @Override
    public void ping() throws RemoteException {
        // Just to make sure the client is still alive
    }

    /**
     * Attempts to log in with the specified nickname.
     *
     * @param nickname the player's nickname
     * @return true if successful, false if the name is taken
     * @throws Exception if a network error occurs
     */
    @Override
    public boolean login(String nickname) throws Exception {
        if(serverStub.login(nickname)){
            this.nickname = nickname;
            return true;
        }
        return false;
    }

    /**
     * Logs the player out.
     *
     * @param nickname the player's nickname
     * @throws Exception if a network error occurs
     */
    @Override
    public void logout(String nickname) throws Exception {
        if(serverStub.logout(nickname)){
            this.nickname = null;
        }
    }

    /**
     * Checks which match the player is currently in.
     *
     * @param nickname the player's nickname
     * @return the match ID, or -1 if not in any match
     * @throws Exception if a network error occurs
     */
    @Override
    public int getPlayersMatchId(String nickname) throws Exception {
        return serverStub.getPlayersMatchId(nickname);
    }

    /**
     * Gets a readable list of matches that can be joined.
     *
     * @return a text description of the available matches
     * @throws Exception if a network error occurs
     */
    @Override
    public String getAvailableMatches() throws Exception {
        if(nickname == null) return "";
        return serverStub.getAvailableMatches(nickname);
    }

    /**
     * Creates a new match on the server and joins it.
     *
     * @param numOfPlayers the number of players needed for the match
     * @param nickname     the player's nickname
     * @return the ID of the newly created match
     * @throws Exception if a network error occurs
     */
    @Override
    public int createMatch(int numOfPlayers, String nickname) throws Exception {
        int matchId = serverStub.createMatch(numOfPlayers);
        serverStub.joinMatch(matchId, nickname, this);
        return matchId;
    }

    /**
     * Asks the server for detailed information about a specific match.
     *
     * @param matchId the target match ID
     * @return the match info text
     * @throws Exception if a network error occurs
     */
    @Override
    public String getMatchInfo(int matchId) throws Exception {
        return serverStub.getMatchInfo(matchId);
    }

    /**
     * Joins an existing match.
     *
     * @param matchId  the target match ID
     * @param nickname the player's nickname
     * @return true if successfully joined, false otherwise
     * @throws Exception if a network error occurs
     */
    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        return serverStub.joinMatch(matchId, nickname, this);
    }

    /**
     * Tells the server to place the player's totem on the track.
     *
     * @param nickname  the player's nickname
     * @param tileIndex the chosen tile's index
     * @throws Exception if a network error occurs
     */
    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {
        serverStub.handleTotemOfferTilePlacement(nickname, tileIndex);
    }

    /**
     * Tells the server to pick a character card from the bottom row.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the chosen card's index
     * @throws Exception if a network error occurs
     */
    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickBottomRow(nickname, cardIndex);
    }

    /**
     * Tells the server to pick a character card from the top row.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the chosen card's index
     * @throws Exception if a network error occurs
     */
    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleCardPickTopRow(nickname, cardIndex);
    }

    /**
     * Tells the server to pick a building card from the bottom row.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the chosen building's index
     * @throws Exception if a network error occurs
     */
    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickBottomRow(nickname, cardIndex);
    }

    /**
     * Tells the server to pick a building card from the top row.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the chosen building's index
     * @throws Exception if a network error occurs
     */
    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception {
        serverStub.handleBuildingPickTopRow(nickname, cardIndex);
    }

    /**
     * Tells the server the player wants to skip their current action.
     *
     * @param nickname the player's nickname
     * @throws Exception if a network error occurs
     */
    @Override
    public void handleSkip(String nickname) throws Exception {
        serverStub.handleSkip(nickname);
    }

    /**
     * Tells the server which totem color the player chose during setup.
     *
     * @param nickname the player's nickname
     * @param color    the chosen color
     * @throws Exception if a network error occurs
     */
    @Override
    public void chooseTotemColor(String nickname, Color color) throws Exception {
        serverStub.handleChooseTotemColor(nickname, color);
    }

    /**
     * Stops sending updates to a local UI element.
     *
     * @param listener the UI component to remove
     */
    @Override
    public void unsubscribe(Client listener) {
        if (listener.equals(prioritizedListener)) prioritizedListener = null;
    }

    /**
     * Subscribes a priority listener, usually the main Client class, to receive updates first.
     *
     * @param listener the priority client runner
     */
    @Override
    public void prioritizedSubscribe(Client listener) {
        prioritizedListener = listener;
    }

}
