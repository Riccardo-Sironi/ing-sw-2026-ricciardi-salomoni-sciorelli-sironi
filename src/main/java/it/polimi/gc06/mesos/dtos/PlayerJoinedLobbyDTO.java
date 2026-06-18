package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;

public class PlayerJoinedLobbyDTO implements SmallModelEditor {

    private final ArrayList<String> currentPlayers;
    private Integer sequenceNumber;

    public PlayerJoinedLobbyDTO(ArrayList<String> currentPlayers) {
        this.currentPlayers = currentPlayers;
        this.sequenceNumber = null;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException {
        smallModel.getOpponents().clear();

        for (String pName : currentPlayers) {
            if (!pName.equals(smallModel.getPlayer().getNickname())) {
                PlayerView opponent = new PlayerView(pName, null);
                smallModel.addOpponent(opponent);
            }
        }
    }

    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setSequenceNumber(int sNum) {
        this.sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}