package it.polimi.gc06.mesos.server.infos;
import it.polimi.gc06.mesos.model.cards.Card;

public record CardPickedInfo(String nickname, Card card) { }
