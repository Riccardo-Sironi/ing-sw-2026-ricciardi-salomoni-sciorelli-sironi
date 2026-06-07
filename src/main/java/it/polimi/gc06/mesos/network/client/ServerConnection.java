package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.Color;
import it.polimi.gc06.mesos.view.smallModel.SmallModelNotifier;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerConnection extends Remote, SmallModelNotifier {

    void startConnection() throws Exception;

    void receiveDTO(SmallModelEditor dto) throws Exception;

    void ping() throws RemoteException;

    String getMatchInfo(int matchId) throws Exception;

    boolean login(String nickname) throws Exception;

    void logout(String nickname) throws Exception;

    int getPlayersMatchId(String nickname) throws Exception;

    String getAvailableMatches() throws Exception;

    int createMatch(int numOfPlayers, String nickname) throws Exception;

    boolean joinMatch(int matchId, String nickname) throws Exception;

    void placeTotem(String nickname, int tileIndex) throws Exception;

    void pickCardFromBottom(String nickname, int cardIndex) throws Exception;

    void pickCardFromTop(String nickname, int cardIndex) throws Exception;

    void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception;

    void pickBuildingFromTop(String nickname, int cardIndex) throws Exception;

    void handleSkip(String nickname) throws Exception;

    void chooseTotemColor(String nickname, Color color) throws Exception;
}

