package it.polimi.gc06.mesos.view;

import it.polimi.gc06.mesos.network.ConnectionDetails;

/**
 * Interface representing the View for the connection and lobby phase.
 * It ensures both TUI and GUI implement the same core functionalities.
 */
public interface LobbyView {
    /**
     * Prompts the user to insert connection details.
     */
    ConnectionDetails askConnectionDetails();

    /**
     * Displays a success message when the connection is established.
     *
     * @param message The success message.
     */
    void showConnectionSuccess(String message);

    /**
     * Displays an error message if the connection fails.
     *
     * @param errorMessage The error message.
     */
    void showConnectionError(String errorMessage);
}
