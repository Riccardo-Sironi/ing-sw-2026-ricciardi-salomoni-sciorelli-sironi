package it.polimi.gc06.mesos.network.client;

public interface ServerConnection {
    // Login and Game setup
    boolean login(String nickname) throws Exception;
    void logout(String nickname) throws Exception;
    String getAvailableMatches() throws Exception;
    void createMatch(int numOfPlayers, String nickname) throws Exception;
    boolean joinMatch(int matchId, String nickname) throws Exception;

    // Game Actions
    void placeTotem(String nickname, int tileIndex) throws Exception;
    void pickCardFromBottom(String nickname, int cardIndex) throws Exception;
    void pickCardFromTop(String nickname, int cardIndex) throws Exception;
    void pickBuildingFromBottom(String nickname, int cardIndex) throws Exception;
    void pickBuildingFromTop(String nickname, int cardIndex) throws Exception;
}

