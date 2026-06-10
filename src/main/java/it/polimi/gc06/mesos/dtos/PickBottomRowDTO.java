package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Broadcasts out that a character or event card got picked up from the bottom row.
 */
public class PickBottomRowDTO implements SmallModelEditor {

    private final String player;
    private final int cardIndex;
    private Integer sequenceNumber;

    public PickBottomRowDTO(String player, int cardIndex) {
        this.player = player;
        this.cardIndex = cardIndex;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel the client's small model.
     * @throws IllegalStateException if the target player is not found.
     */
    @Override
    public void edit(SmallModel smallModel) {
        Card card = smallModel.getBottomRow().remove(cardIndex);

        if (smallModel.getPlayer().getNickname().equals(player)) {
            smallModel.getPlayer().getCharacters().add(card);
        } else {
            smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player)).findFirst()
                    .orElseThrow(IllegalStateException::new).getCharacters().add(card);
        }
    }

    /**
     * @return the nickname of the drafting player.
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return the index from where the card was drafted.
     */
    public int getCardIndex() {
        return cardIndex;
    }

    /**
     * {@inheritDoc}
     *
     * @param visitor
     */
    @Override
    public void accept(DTOVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
