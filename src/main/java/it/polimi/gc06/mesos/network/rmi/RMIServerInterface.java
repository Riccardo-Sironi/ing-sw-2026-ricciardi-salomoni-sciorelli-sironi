package it.polimi.gc06.mesos.network.rmi;

import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.network.client.ServerConnection;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface exposing all the callable actions a server provides to its RMI clients.
 * It strictly dictates lobby interactions, match manipulation, and gameplay commands
 * accessible over Java RMI calls.
 */
public interface RMIServerInterface extends Remote {

    /**
     * Executes the login process for a client within the network framework.
     *
     * @param nickname the unique identifier the player uses to login
     * @return true if login succeeds, false if the nickname is already taken or unavailable
     * @throws RemoteException if the remote execution context is interrupted
     */
    boolean login(String nickname) throws RemoteException;

    /**
     * Executes the process to detach a client component systematically from tracking bounds.
     *
     * @param nickname the active user's target identifier
     * @return true if the logout request executes successfully, false otherwise
     * @throws RemoteException if underlying communication disrupts
     */
    boolean logout(String nickname) throws RemoteException;

    /**
     * Exposes all universally available match lobbies
     *
     * @return a serialized string summarizing the joinable matchmaking groups
     * @throws RemoteException if underlying communication disrupts
     */
    String getAvailableMatches(String nickname) throws RemoteException;

    /**
     * Instantiates a new match lobby with the specified player capacity and returns its unique identifier for future reference.
     *
     * @param numOfPlayers the cap defining the size limits of the matchmaking boundaries
     * @return the sequential match ID indexing the resulting setup
     * @throws RemoteException if remote instantiation parameters fail execution
     */
    int createMatch(int numOfPlayers) throws RemoteException;

    /**
     * Executes the process of joining a specific match lobby, binding the client callback reference to the resulting match context for future interactions.
     * s
     *
     * @param matchId        the numerical ID classifying the target match
     * @param nickname       the user's identity entering the space
     * @param clientCallback the client endpoint component passed to handle backwards DTO abstraction streams
     * @return true if the join operation processes correctly, false otherwise
     * @throws RemoteException if logic interrupts via remote disconnection anomalies
     */
    boolean joinMatch(int matchId, String nickname, ServerConnection clientCallback) throws RemoteException;

    /**
     * Fetches the match index a designated participant represents within its bounds.
     *
     * @param nickname the chosen target participant
     * @return the id of the game match this participant tracks
     * @throws RemoteException if a remote failure breaks standard flow bounds
     */
    int getPlayersMatchId(String nickname) throws RemoteException;

    /**
     * Extracts readable details concerning specific structured matches from its ID wrapper.
     *
     * @param matchId the parameter detailing a tracked matchmaking setup
     * @return the literal abstraction tracking details over the specified setup
     * @throws RemoteException if RMI abstractions disrupt transmission
     */
    String getMatchInfo(int matchId) throws RemoteException;

    /**
     * Dispatches the action of placing a specific placement marker onto a game tile.
     *
     * @param nickname  the player executing the request
     * @param tileIndex the chosen tile coordinate relative mapping
     * @throws RemoteException if an unforeseen communication disconnect ensues
     */
    void handleTotemOfferTilePlacement(String nickname, int tileIndex) throws RemoteException;

    /**
     * Dispatches the action of explicitly picking a standard character card abstracted to the bottom row array limits.
     *
     * @param nickname  the player executing the request
     * @param cardIndex the coordinate bound to this action inside the bottom logical row bounds
     * @throws RemoteException if communication interrupts execution
     */
    void handleCardPickBottomRow(String nickname, int cardIndex) throws RemoteException;

    /**
     * Dispatches the action of explicitly picking a standard character card abstracted to the top row array limits.
     *
     * @param nickname  the player executing the request
     * @param cardIndex the coordinate bound to this action inside the top logical row bounds
     * @throws RemoteException if communication interrupts execution
     */
    void handleCardPickTopRow(String nickname, int cardIndex) throws RemoteException;

    /**
     * Dispatches the action of selecting building-level board sequence elements via bottom array logic limits.
     *
     * @param nickname  the player executing the request
     * @param cardIndex the card index corresponding to the targeted array section
     * @throws RemoteException if connection boundaries falter
     */
    void handleBuildingPickBottomRow(String nickname, int cardIndex) throws RemoteException;

    /**
     * Dispatches the action of selecting building-level board sequence elements via top array logic limits.
     *
     * @param nickname  the player executing the request
     * @param cardIndex the card index corresponding to the targeted array section
     * @throws RemoteException if connection boundaries falter
     */
    void handleBuildingPickTopRow(String nickname, int cardIndex) throws RemoteException;

    /**
     * Dispatches the action of skipping execution when resolving phase logic bounds.
     *
     * @param nickname the player executing the request
     * @throws RemoteException if standard connection breaks block delivery
     */
    void handleSkip(String nickname) throws RemoteException;

    /**
     * Dispatches the action of configuring early color selection.
     *
     * @param nickname the initial participant establishing bounds
     * @param color    the color choice this participant has made for their totem configuration
     * @throws RemoteException via framework level interaction disruption
     */
    void handleChooseTotemColor(String nickname, Color color) throws RemoteException;
}
