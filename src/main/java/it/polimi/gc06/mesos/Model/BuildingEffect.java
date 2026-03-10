package it.polimi.gc06.mesos.Model;

@FunctionalInterface
public interface BuildingEffect {
    void applyEffect(Player player, TurnManager turnManager, BuildingCard card);
}
