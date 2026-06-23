package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;

import java.io.Serializable;

/**
 * Encapsulates a player's action into a serializable command object.
 * This allows requests to be sent over the network from the client
 * and executed securely on the server's GameController.
 */
public class ControllerCommand implements Serializable {

    private Request request;
    private int index;
    private String nickname;

    /**
     * Constructs an empty ControllerCommand with default null or negative values.
     */
    public ControllerCommand() {
        this.request = null;
        this.index = -1;
        this.nickname = null;
    }

    /**
     * Constructs a ControllerCommand with the specified parameters.
     *
     * @param nickname The player's nickname.
     * @param index The target index for the action.
     * @param request The specific Request to be executed.
     */
    public ControllerCommand(String nickname, int index, Request request) {
        this.nickname = nickname;
        this.index = index;
        this.request = request;
    }

    /**
     * This method sets the request type for this command.
     *
     * @param request The specific Request to be executed.
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * This method sets the index associated with this command.
     *
     * @param index The target index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * This method sets the nickname of the player issuing the command.
     *
     * @param nickname The player's nickname.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method retrieves the nickname of the player issuing the command.
     *
     * @return The player's nickname.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * This method executes the encapsulated request on the provided controller.
     *
     * @param controller The game controller that will handle the request.
     * @throws Exception If an error occurs during the execution of the command.
     */
    public void execute(GameController controller) throws Exception {
        request.call(controller, this.nickname, this.index);
    }

    /**
     * Debug only.
     * * @return The request associated with this command.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Debug only.
     * * @return The index associated with this command.
     */
    public int getIndex() {
        return index;
    }
}