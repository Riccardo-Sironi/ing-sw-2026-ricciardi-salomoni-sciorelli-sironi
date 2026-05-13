package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.util.function.Consumer;

public class Client implements ModelListener {

    private final SmallModel smallModel;
    private ServerConnection serverConnection = null;

    public Client(SmallModel smallModel) {
        this.smallModel = smallModel;
    }

    public void connect(String tech, String host, int port, Consumer<SmallModelEditor> messageHandler) {
        if (serverConnection != null) {
            throw new IllegalStateException("Already connected to a server");
        }

        try {
            serverConnection = tech.equals("RMI") ? new RMIServerConnection(host, port, messageHandler) :
                    new TCPServerConnection(host, port);
            serverConnection.prioritizedSubscribe(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to server: " + e.getMessage(), e);
        }
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public void subscribe(ModelListener listener) {

    }

    /**
     * Should be called BEFORE other listeners to ensure that the small model is updated.
     *
     * @param dto the dto that the notification stemmed from.
     */
    @Override
    public void update(SmallModelEditor dto) {
        dto.edit(smallModel);
    }
}
