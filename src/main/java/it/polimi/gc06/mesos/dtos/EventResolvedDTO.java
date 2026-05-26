package it.polimi.gc06.mesos.dtos;

import it.polimi.gc06.mesos.model.cards.events.EventCard;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class EventResolvedDTO implements SmallModelEditor {

    private EventCard eventCard;

    public EventResolvedDTO(EventCard eventCard) {
        this.eventCard = eventCard;
    }

    @Override
    public void edit(SmallModel smallModel) throws IllegalStateException, Error {
    }

    @Override
    public void accept(DTOvisitor visitor) {
        visitor.visit(this);
    }

    public EventCard getEventCard() {
        return eventCard;
    }
}
