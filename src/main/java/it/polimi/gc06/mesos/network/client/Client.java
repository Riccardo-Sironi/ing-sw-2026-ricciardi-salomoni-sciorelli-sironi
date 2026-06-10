package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements ModelListener {

    private SmallModel smallModel;
    private ServerConnection serverConnection = null;
    private final List<ModelListener> listeners;
    private Integer nextSequenceNumber;
    private final Map<Integer, SmallModelEditor> earlyDto; //out of sequence DTO

    public Client() {
        listeners = new ArrayList<>();
        nextSequenceNumber = 0;
        earlyDto = new HashMap<>();
    }

    public void connect(String tech, String host, int hostPort, String clientIp, int clientPort) throws IllegalStateException, ConnectException {
        if (serverConnection != null) {
            throw new IllegalStateException("Already connected to a server");
        }

        try {
            System.setProperty("java.rmi.server.hostname", clientIp);
            serverConnection = tech.equals("RMI") ? new RMIServerConnection(host, hostPort, clientPort) :
                    new TCPServerConnection(host, hostPort);
            serverConnection.startConnection();
            serverConnection.prioritizedSubscribe(this);
        } catch (Exception e) {
            //e.printStackTrace();
            throw new ConnectException(e.getMessage());
        }
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Should be called BEFORE other listeners to ensure that the small model is updated.
     *
     * @param dto the dto that the notification stemmed from.
     */
    @Override
    public synchronized void update(SmallModelEditor dto) {
        if (dto.getSequenceNumber() == null) {
            //non-sequenced DTO
            try {
                dto.edit(smallModel);
            } catch (Error _) {
            } //if it's an error DTO an Error will be thrown
            listeners.forEach(l -> l.update(dto));
        } else if (nextSequenceNumber.equals(dto.getSequenceNumber())) {
            //correct DTO
            nextSequenceNumber++;
            try {
                dto.edit(smallModel);
            } catch (Error _) {
            } //if it's an error DTO an Error will be thrown
            listeners.forEach(l -> l.update(dto));
            //takes all correct early dto
            while (earlyDto.containsKey(nextSequenceNumber)) {
                SmallModelEditor nextDTO = earlyDto.remove(nextSequenceNumber);
                nextSequenceNumber++;
                try {
                    nextDTO.edit(smallModel);
                } catch (Error _) {
                } //if it's an error DTO an Error will be thrown
                listeners.forEach(l -> l.update(nextDTO));
            }
        } else {
            //out of sequence DTO
            earlyDto.put(dto.getSequenceNumber(), dto);
        }
    }

    /**
     * Subscribes a standard UI observer/listener to the model updates.
     *
     * @param l the observer component that requires game state deltas
     */
    public void subscribe(ModelListener l) {
        listeners.add(l);
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
