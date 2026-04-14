package it.polimi.gc06.mesos.network.socket.server.infos;

import it.polimi.gc06.mesos.model.cards.Card;

public record CardPickedInfo(String nickname, Card card) {
}
