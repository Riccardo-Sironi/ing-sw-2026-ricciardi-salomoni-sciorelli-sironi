package it.polimi.gc06.mesos.view.gui.controllers;

import it.polimi.gc06.mesos.view.LobbyView;

public class LobbyGuiController implements LobbyView {

    @Override
    public void askConnectionDetails() {

    }

    @Override
    public void showConnectionSuccess(String message) {
        System.out.println("[GUI MOCK] " + "message");
    }

    @Override
    public void showConnectionError(String errorMessage) {
        System.err.println("[GUI MOCK ERROR] Show popup: " + errorMessage);
    }

    /**
     * Method to be called by the FXML "Connect" button.
     */
    public void handleConnectAction() {
        showConnectionSuccess("Connected");
    }
}
