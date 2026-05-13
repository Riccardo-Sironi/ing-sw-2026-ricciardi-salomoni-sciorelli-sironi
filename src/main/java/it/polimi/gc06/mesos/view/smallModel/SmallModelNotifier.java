package it.polimi.gc06.mesos.view.smallModel;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.network.client.Client;

public interface SmallModelNotifier {

    void subscribe(ModelListener listener);
    void unsubscribe(ModelListener listener);
    void prioritizedSubscribe(Client listener);

}
