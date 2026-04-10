package it.polimi.gc06.mesos.server.commands;

import it.polimi.gc06.mesos.controller.GameController;

public class Command {

    private Request request;
    private int index;
    private String nickname;

    public Command() {
        request = null;
        index = -1;
        nickname = null;
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

    public void execute(GameController controller){
        request.call(controller,nickname,index);
    }
}
