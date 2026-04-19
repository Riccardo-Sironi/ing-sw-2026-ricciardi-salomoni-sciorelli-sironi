package it.polimi.gc06.mesos.network.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
    void ping() throws RemoteException;
}

