package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;

import java.rmi.RemoteException;

public class TCPServerConnection implements ServerConnection {
    @Override
    public void receiveDTO(SmallModelEditor dto) {

    }

    @Override
    public void ping() throws RemoteException {

    }

    @Override
    public boolean login(String nickname) throws Exception {
        return false;
    }

    @Override
    public void logout(String nickname) throws Exception {

    }

    @Override
    public String getAvailableMatches() throws Exception {
        return "";
    }

    @Override
    public void createMatch(int numOfPlayers, String nickname) throws Exception {

    }

    @Override
    public boolean joinMatch(int matchId, String nickname) throws Exception {
        return false;
    }

    @Override
    public void placeTotem(String nickname, int tileIndex) throws Exception {

    }

    @Override
    public void pickCardFromBottom(String nickname, int cardIndex) throws Exception {

    }

    @Override
    public void pickCardFromTop(String nickname, int cardIndex) throws Exception {

    }

    @Override
    public void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception {

    }

    @Override
    public void pickBuildingFromTop(String nickname, int cardIndex) {

    }
}
