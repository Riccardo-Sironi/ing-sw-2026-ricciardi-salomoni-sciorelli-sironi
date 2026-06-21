package it.polimi.gc06.mesos.dtos.snapshots;

import it.polimi.gc06.mesos.model.Era;
import it.polimi.gc06.mesos.model.cards.TribeCard;
import it.polimi.gc06.mesos.model.cards.events.EventCard;

import java.util.List;
import java.util.Map;

public record GameSnapshot(
        List<PlayerSnapshot> players,
        BoardSnapshot board,
        TurnManagerSnapshot turnManager,
        Map<Era, List<TribeCard>> tribeCardsDeck,
        List<EventCard> finalEventCards
) {
}