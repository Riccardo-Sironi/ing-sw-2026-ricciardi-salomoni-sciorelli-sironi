package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.smallModel.SmallModelNotifier;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerConnection extends Remote, SmallModelNotifier {
    void receiveDTO(SmallModelEditor dto) throws RemoteException;

    void ping() throws RemoteException;

    String getMatchInfo(int matchId) throws Exception;

    public boolean login(String nickname) throws Exception;

    public void logout(String nickname) throws Exception;

    public int getPlayersMatchId(String nickname) throws Exception;

    public String getAvailableMatches() throws Exception;

    public int createMatch(int numOfPlayers, String nickname) throws Exception;

    public boolean joinMatch(int matchId, String nickname) throws Exception;

    public void placeTotem(String nickname, int tileIndex) throws Exception;

    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception;

    public void pickCardFromTop(String nickname, int cardIndex) throws Exception;

    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception;

    public void pickBuildingFromTop(String nickname, int cardIndex) throws Exception;
}

