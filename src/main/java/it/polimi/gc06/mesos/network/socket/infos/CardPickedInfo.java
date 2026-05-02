package it.polimi.gc06.mesos.network.socket.infos;

import it.polimi.gc06.mesos.model.cards.Card;

public record CardPickedInfo(String nickname, int cardIndex, Card card) {
}
