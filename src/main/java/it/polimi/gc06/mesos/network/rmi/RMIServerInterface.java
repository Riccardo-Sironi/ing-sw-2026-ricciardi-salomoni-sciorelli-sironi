package it.polimi.gc06.mesos.network.rmi;

import it.polimi.gc06.mesos.network.client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote {
    boolean login(String nickname) throws RemoteException;

    boolean logout(String nickname) throws RemoteException;

    String getAvailableMatches() throws RemoteException;

    int createMatch(int numOfPlayers) throws RemoteException;

    boolean joinMatch(int matchId, String nickname, ClientInterface clientCallback) throws RemoteException;

    void handleTotemOfferTilePlacement(String nickname, int tileIndex) throws RemoteException;

    void handleCardPickBottomRow(String nickname, int cardIndex) throws RemoteException;

    void handleCardPickTopRow(String nickname, int cardIndex) throws RemoteException;

    void handleBuildingPickBottomRow(String nickname, int cardIndex) throws RemoteException;

    void handleBuildingPickTopRow(String nickname, int cardIndex) throws RemoteException;

    void handleSkip(String nickname) throws RemoteException;
}
