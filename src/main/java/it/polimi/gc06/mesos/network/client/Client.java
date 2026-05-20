package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

public class Client implements ModelListener {

    private SmallModel smallModel;
    private ServerConnection serverConnection = null;

    public Client(){

    }

    //TODO: remove if small model needs to be instantiated out of the client
    public Client(String nickname) {
        smallModel = new SmallModel(nickname);
    }

    public void connect(String tech, String host, int port) {
        if (serverConnection != null) {
            throw new IllegalStateException("Already connected to a server");
        }

        try {
            serverConnection = tech.equals("RMI") ? new RMIServerConnection(host, port) :
                    new TCPServerConnection(host, port);
            serverConnection.startConnection();
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
    public void update(SmallModelEditor dto){
        try {
            dto.edit(smallModel); //if it's an error DTO an Error will be thrown
        }catch (Error _) {}
    }

    /**
     * For testing purpose only!
     */
    public SmallModel getModel() {
        return smallModel;
    }

    /**
     * ...
     *
     * @param smallModel
     */
    public void setSmallModel(SmallModel smallModel) {
        this.smallModel = smallModel;
    }
}
