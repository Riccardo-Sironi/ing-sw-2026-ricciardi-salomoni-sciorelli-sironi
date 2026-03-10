package it.polimi.gc06.mesos.Model;

public interface TileEffect {

    abstract boolean execute(Player player, GameModel context) throws IllegalArgumentException;
}
