package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.dtos.SmallModelEditor;

import java.rmi.RemoteException;

public class TCPServerConnection implements ClientInterface {
    @Override
    public void receiveDTO(SmallModelEditor dto) {
        
    }

    @Override
    public void ping() throws RemoteException {

    }
}
