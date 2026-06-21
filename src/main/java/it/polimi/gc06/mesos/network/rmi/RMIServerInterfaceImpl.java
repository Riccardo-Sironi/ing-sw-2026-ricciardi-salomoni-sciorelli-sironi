package it.polimi.gc06.mesos.network.rmi;

import it.polimi.gc06.mesos.controller.GameController;
import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.matches.MatchManager;
import it.polimi.gc06.mesos.network.server.RMIClientManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete implementation of the RMI server interface.
 * It connects remote RMI calls from the clients to the central MatchManager
 * and routes game commands to the appropriate client managers.
 */
public class RMIServerInterfaceImpl extends UnicastRemoteObject implements RMIServerInterface {
    private final MatchManager serverManager;
    private final Map<String, RMIClientManager> clientManagers;

    /**
     * Creates the RMI server interface implementation.
     *
     * @param serverManager the main server manager handling matches and logins
     * @param exportPort    Specify a new UnicastRemoteObject object using the particular supplied port.
     * @throws RemoteException if RMI initialization fails
     */
    public RMIServerInterfaceImpl(MatchManager serverManager, int exportPort) throws RemoteException {
        super(exportPort);
        this.serverManager = serverManager;
        this.clientManagers = new ConcurrentHashMap<>();
    }


    private void logRMICall(String nickname, String action) throws RemoteException {
        int matchId = serverManager.getPlayersMatchId(nickname);
        System.out.println("[RMI - Match " + matchId + "] Received a command: " + action + " from " + nickname);
    }

    /**
     * Logs in a player with the given nickname.
     *
     * @param nickname the player's nickname
     * @return true if the login is successful, false if the name is already taken
     * @throws RemoteException if a network error occurs
     */
    @Override
    public boolean login(String nickname) throws RemoteException {
        System.out.println("[RMI] Received a login request from " + nickname);
        return (serverManager.login(nickname));
    }


    /**
     * Logs out a player, removing them from the server and closing their connection.
     *
     * @param nickname the player's nickname
     * @return true if successfully logged out
     * @throws RemoteException if a network error occurs
     */
    @Override
    public boolean logout(String nickname) throws RemoteException {
        RMIClientManager manager = clientManagers.remove(nickname);
        if (manager != null) {
            manager.closeConnection();
        }
        System.out.println("[RMI] Received a logout request from " + nickname);

        return serverManager.logout(nickname);
    }

    /**
     * Finds out which match a specific player is currently in.
     *
     * @param nickname the player's nickname
     * @return the ID of the match, or -1 if not in a match
     * @throws RemoteException if a network error occurs
     */
    @Override
    public int getPlayersMatchId(String nickname) throws RemoteException {
        return serverManager.getPlayersMatchId(nickname);
    }

    /**
     * Gets formatted text containing details about a specific match.
     *
     * @param matchId the ID of the match
     * @return a string with the match details
     * @throws RemoteException if a network error occurs
     */
    @Override
    public String getMatchInfo(int matchId) throws RemoteException {
        return serverManager.getMatchInfo(matchId);
    }

    /**
     * Gets a list of all matches that are currently waiting for players.
     *
     * @return a formatted string listing the available matches
     * @throws RemoteException if a network error occurs
     */
    @Override
    public String getAvailableMatches(String nickname) throws RemoteException {
        return serverManager.getAvailableMatchesString(nickname);
    }

    /**
     * Creates a new match.
     *
     * @param numOfPlayers the number of players required for this match
     * @return the ID of the newly created match
     * @throws RemoteException if a network error occurs
     */
    @Override
    public int createMatch(int numOfPlayers) throws RemoteException {
        System.out.println("[RMI] Received a match creation request for a " + numOfPlayers + "-player match");
        return serverManager.createMatch(numOfPlayers).getMatchId();
    }

    /**
     * Adds a player to a match and sets up their RMI connection manager.
     *
     * @param matchId        the ID of the match to join
     * @param nickname       the player's nickname
     * @param clientCallback the client's remote object to receive updates
     * @return true if the player joined successfully, false otherwise
     * @throws RemoteException if a network error occurs
     */
    @Override
    public boolean joinMatch(int matchId, String nickname, ServerConnection clientCallback) throws RemoteException {
        RMIClientManager rmiClientManager = new RMIClientManager(nickname, clientCallback, serverManager);
        System.out.println("[RMI - Match " + matchId + "] Received a join request from " + nickname);
        boolean success = serverManager.joinMatch(matchId, rmiClientManager);
        if (success) {
            clientManagers.put(nickname, rmiClientManager);
        }
        return success;
    }

    /**
     * Dispatches the command to place the player's totem on a specific slot in the offer track.
     *
     * @param nickname  the player's nickname
     * @param tileIndex the index of the offer tile
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleTotemOfferTilePlacement(String nickname, int tileIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, tileIndex, Request.OFFER_TRACK_REQUEST);

        manager.enqueueCommand(command);

        logRMICall(nickname, "OFFER_TRACK_REQUEST");
    }

    /**
     * Dispatches the command to pick a character card from the bottom row for the given player.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the index of the card in the row
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleCardPickBottomRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.BOTTOM_CARD_REQUEST);

        manager.enqueueCommand(command);
        logRMICall(nickname, "BOTTOM_CARD_REQUEST");
    }

    /**
     * Dispatches the command to pick a character card from the top row for the given player.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the index of the card in the row
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleCardPickTopRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.TOP_CARD_REQUEST);

        manager.enqueueCommand(command);

        logRMICall(nickname, "TOP_CARD_REQUEST");
    }

    /**
     * Dispatches the command to pick a building card from the bottom row for the given player.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the index of the building card in the row
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleBuildingPickBottomRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.BOTTOM_BUILDING_REQUEST);

        manager.enqueueCommand(command);

        logRMICall(nickname, "BOTTOM_BUILDING_REQUEST");
    }

    /**
     * Dispatches the command to pick a building card from the top row for the given player.
     *
     * @param nickname  the player's nickname
     * @param cardIndex the index of the building card in the row
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleBuildingPickTopRow(String nickname, int cardIndex) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, cardIndex, Request.TOP_BUILDING_REQUEST);

        manager.enqueueCommand(command);

        logRMICall(nickname, "TOP_BUILDING_REQUEST");
    }

    /**
     * Dispatches the command to skip the player's current optional action.
     *
     * @param nickname the player's nickname
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleSkip(String nickname) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }

        ControllerCommand command = new ControllerCommand(nickname, 0, Request.SKIP_REQUEST);

        manager.enqueueCommand(command);

        logRMICall(nickname, "SKIP_REQUEST");
    }

    /**
     * Dispatches the command to select the player's chosen totem color during the setup phase.
     *
     * @param nickname the player's nickname
     * @param color    the chosen color
     * @throws RemoteException if a network error occurs or the player is not found
     */
    @Override
    public void handleChooseTotemColor(String nickname, Color color) throws RemoteException {
        RMIClientManager manager = clientManagers.get(nickname);
        if (manager == null) {
            throw new RemoteException("User not found: " + nickname);
        }
        ControllerCommand command = new ControllerCommand(nickname, color.ordinal(), Request.CHOOSE_TOTEM_COLOR_REQUEST);
        manager.enqueueCommand(command);

        logRMICall(nickname, "CHOOSE_TOTEM_COLOR_REQUEST");
    }

}