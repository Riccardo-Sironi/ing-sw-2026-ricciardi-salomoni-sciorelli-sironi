package it.polimi.gc06.mesos.Model;

public interface DrawSubject {
    void addObserver(DrawObserver observer);

    void removeObserver(DrawObserver observer);

    void notifyObserverBuildings(Player player);

}
