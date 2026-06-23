package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.Card;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Broadcasts out that a character or event card got picked up from the top row.
 */
public class PickTopRowDTO implements SmallModelEditor {

    private final String player;
    private final int cardIndex;
    private Integer sequenceNumber;

    /**
     * Constructs a new PickTopRowDTO.
     *
     * @param player The nickname of the player who picked the card.
     * @param cardIndex The index of the card in the top row.
     */
    public PickTopRowDTO(String player, int cardIndex) {
        this.player = player;
        this.cardIndex = cardIndex;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException If the target player is not found.
     */
    @Override
    public void edit(SmallModel smallModel) {
        Card card = smallModel.getTopRow().remove(cardIndex);

        if (smallModel.getPlayer().getNickname().equals(player)) {
            smallModel.getPlayer().getCharacters().add(card);
        } else {
            smallModel.getOpponents().stream().filter(v -> v.getNickname().equals(player)).findFirst()
                    .orElseThrow(IllegalStateException::new).getCharacters().add(card);
        }
    }

    /**
     * @return The nickname of the drafting player.
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @return The index from where the card was drafted.
     */
    public int getCardIndex() {
        return cardIndex;
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

    /**
     * Sets the sequence number for this DTO.
     *
     * @param sNum The sequence number.
     */
    @Override
    public void setSequenceNumber(int sNum) {
        sequenceNumber = sNum;
    }

    /**
     * Retrieves the sequence number of this DTO.
     *
     * @return The sequence number, or null if not set.
     */
    @Override
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}