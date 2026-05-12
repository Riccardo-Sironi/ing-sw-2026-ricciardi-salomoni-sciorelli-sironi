package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
    void receiveMessage(String message) throws RemoteException;
    void receiveDTO(SmallModelEditor dto);
    void ping() throws RemoteException;
}

