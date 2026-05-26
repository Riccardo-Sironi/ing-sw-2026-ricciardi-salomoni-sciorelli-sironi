package it.polimi.gc06.mesos.controller.commands;

import it.polimi.gc06.mesos.controller.GameController;

import java.io.Serializable;

public class ControllerCommand implements Serializable {

    private Request request;
    private int index;
    private String nickname;

    public ControllerCommand() {
        this.request = null;
        this.index = -1;
        this.nickname = null;
    }

    public ControllerCommand(String nickname, int index, Request request) {
        this.nickname = nickname;
        this.index = index;
        this.request = request;
    }

    /**
     * This method sets the request type for this command.
     *
     * @param request the specific Request to be executed.
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * This method sets the index associated with this command.
     *
     * @param index the target index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * This method sets the nickname of the player issuing the command.
     *
     * @param nickname the player's nickname.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method retrieves the nickname of the player issuing the command.
     *
     * @return the player's nickname.
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * This method executes the encapsulated request on the provided controller.
     *
     * @param controller the game controller that will handle the request.
     * @throws Exception if an error occurs during the execution of the command.
     */
    public void execute(GameController controller) throws Exception {
        request.call(controller, this.nickname, this.index);
    }

    /**
     * Debug only.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Debug only.
     */
    public int getIndex() {
        return index;
    }
}
