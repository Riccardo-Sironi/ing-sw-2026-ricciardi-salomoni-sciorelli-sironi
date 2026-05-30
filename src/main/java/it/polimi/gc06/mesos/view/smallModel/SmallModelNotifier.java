package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.network.client.Client;

/**
 * Interface that defines the contract for subscribing UI representations to updates.
 * Clients or views implementations can attach their listeners to handle specific updates
 * transmitted by the server.
 */
public interface SmallModelNotifier {

    /**
     * Unsubscribes a previously attached observer from receiving further network abstraction updates.
     *
     * @param listener the UI listener intended for removal
     * @throws java.rmi.RemoteException if a network disruption is encountered
     */
    void unsubscribe(Client listener) throws java.rmi.RemoteException;

    /**
     * Subscribes a special high-priority observer (usually the main Client abstraction itself)
     * dictating critical core state alterations overriding regular view endpoints.
     *
     * @param listener the principal observer system handling core networking elements
     * @throws java.rmi.RemoteException if an underlying connection mechanism disrupts
     */
    void prioritizedSubscribe(Client listener) throws java.rmi.RemoteException;
}
