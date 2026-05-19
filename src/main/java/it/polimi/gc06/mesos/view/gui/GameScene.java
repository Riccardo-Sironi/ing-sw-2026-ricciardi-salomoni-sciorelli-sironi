package it.polimi.gc06.mesos.view.gui;

public enum GameScene {
    START("/it/polimi/gc06/mesos/fxml/Start.fxml"),
    LOGIN("/it/polimi/gc06/mesos/fxml/Login.fxml"),
    SELECT("/it/polimi/gc06/mesos/fxml/SelectGame.fxml"),
    GAME("/it/polimi/gc06/mesos/fxml/Mesos.fxml"),
    LEADERBOARD(""),
    END("");

    private final String path;

    GameScene(String text) {
        this.path = text;
    }

    public String getPath() {
        return path;
    }
}
