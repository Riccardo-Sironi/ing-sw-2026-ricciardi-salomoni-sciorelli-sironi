package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.ArrayList;

/**
 * Notifies the client that the lobby composition has changed.
 * Rebuilds the list of opponents locally based on the server's current player list.
 */
public class PlayerJoinedLobbyDTO implements SmallModelEditor {

    private final ArrayList<String> currentPlayers;
    private Integer sequenceNumber;

    /**
     * Constructs a new PlayerJoinedLobbyDTO.
     *
     * @param currentPlayers The updated list of all player nicknames currently in the lobby.
     */
    public PlayerJoinedLobbyDTO(ArrayList<String> currentPlayers) {
        this.currentPlayers = currentPlayers;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException If the state update violates integrity.
     */
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

    /**
     * {@inheritDoc}
     *
     * @param visitor The visitor handling this DTO.
     */
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