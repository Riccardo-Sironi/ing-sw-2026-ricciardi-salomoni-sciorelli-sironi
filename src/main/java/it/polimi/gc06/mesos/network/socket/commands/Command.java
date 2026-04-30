package it.polimi.gc06.mesos.network.socket.commands;

import it.polimi.gc06.mesos.controller.GameController;

public class Command {

    private Request request;
    private int index;
    private String nickname;

    public Command() {
        this.request = null;
        this.index = -1;
        this.nickname = null;
    }

    public Command(String nickname, int index, Request request) {
        this.nickname = nickname;
        this.index = index;
        this.request = request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void execute(GameController controller) throws Exception {
        request.call(controller, this.nickname, this.index);
    }
}
