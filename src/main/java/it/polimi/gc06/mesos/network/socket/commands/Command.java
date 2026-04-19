package it.polimi.gc06.mesos.network.socket.commands;

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

    public String getNickname() {
        return nickname;
    }

    public static Command create(String nickname, int index, Request request) {
        Command command = new Command();
        command.setNickname(nickname);
        command.setIndex(index);
        command.setRequest(request);
        return command;
    }


    public void execute(GameController controller) throws Exception {
        request.call(controller, nickname, index);
    }
}
