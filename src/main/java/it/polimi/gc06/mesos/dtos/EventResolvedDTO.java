package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

/**
 * Broadcasts that an event card has been successfully resolved.
 * The actual state changes are typically handled by other DTOs or visitors,
 * while this acts as a trigger for UI animations or logging.
 */
public class EventResolvedDTO implements SmallModelEditor {

    private EventCard eventCard;
    private Integer sequenceNumber;

    /**
     * Constructs a new EventResolvedDTO.
     *
     * @param eventCard The event card that was resolved.
     */
    public EventResolvedDTO(EventCard eventCard) {
        this.eventCard = eventCard;
        this.sequenceNumber = null;
    }

    /**
     * {@inheritDoc}
     *
     * @param smallModel The client's small model.
     * @throws IllegalStateException If the internal state does not allow this operation.
     * @throws Error On critical failures.
     */
    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
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
     * Retrieves the resolved event card.
     *
     * @return The event card.
     */
    public EventCard getEventCard() {
        return eventCard;
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