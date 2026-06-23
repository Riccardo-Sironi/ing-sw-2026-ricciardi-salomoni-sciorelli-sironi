package it.polimi.gc06.mesos.network.client;

import it.polimi.gc06.mesos.controller.ModelListener;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the connection to the server on the client side, handling network communication,
 * sequence validation of incoming DTOs, and synchronizing the local SmallModel.
 */
public class Client implements ModelListener {

    private SmallModel smallModel;
    private ServerConnection serverConnection = null;
    private final List<ModelListener> listeners;
    private Integer nextSequenceNumber;
    private final Map<Integer, SmallModelEditor> earlyDto; // Out of sequence DTO

    /**
     * Constructs a new Client instance, initializing the listeners list, sequence number, and early DTO buffer.
     */
    public Client() {
        listeners = new CopyOnWriteArrayList<>();
        nextSequenceNumber = 0;
        earlyDto = new HashMap<>();
    }

    /**
     * Connects the client to the server using the specified technology, host, and port.
     *
     * @param tech The connection technology to use ("RMI" or "TCP").
     * @param host The server host address.
     * @param hostPort The server port number.
     * @throws IllegalStateException If the client is already connected to a server.
     * @throws ConnectException If the connection to the server fails.
     */
    public void connect(String tech, String host, int hostPort) throws IllegalStateException, ConnectException {
        if (serverConnection != null) {
            throw new IllegalStateException("Already connected to a server");
        }
        try {
            serverConnection = tech.equals("RMI") ? new RMIServerConnection(host, hostPort, 0) :
                    new TCPServerConnection(host, hostPort);
            serverConnection.prioritizedSubscribe(this);
            serverConnection.startConnection();
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Connects the client to the server using the specified technology, host, server port, and specific client port.
     *
     * @param tech The connection technology to use ("RMI" or "TCP").
     * @param host The server host address.
     * @param hostPort The server port number.
     * @param clientPort The specific client port number to bind for RMI connections.
     * @throws IllegalStateException If the client is already connected to a server.
     * @throws ConnectException If the connection to the server fails.
     */
    public void connect(String tech, String host, int hostPort, int clientPort) throws IllegalStateException, ConnectException {
        if (serverConnection != null) {
            throw new IllegalStateException("Already connected to a server");
        }

        try {
            serverConnection = tech.equals("RMI") ? new RMIServerConnection(host, hostPort, clientPort) :
                    new TCPServerConnection(host, hostPort);
            serverConnection.prioritizedSubscribe(this);
            serverConnection.startConnection();
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    /**
     * Retrieves the active server connection.
     *
     * @return The current ServerConnection instance.
     */
    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    /**
     * Should be called BEFORE other listeners to ensure that the small model is updated.
     *
     * @param dto The dto that the notification stemmed from.
     */
    @Override
    public synchronized void update(SmallModelEditor dto) {
        if (dto.getSequenceNumber() == null) {
            try {
                dto.edit(smallModel);
            } catch (Error _) {
            }
            catch (Exception e) {
                System.err.println("Error during dto client edit:");
            }
            listeners.forEach(l -> l.update(dto));
        } else if (nextSequenceNumber.equals(dto.getSequenceNumber())) {
            nextSequenceNumber++;
            try {
                dto.edit(smallModel);
            } catch (Error _) {
            }
            catch (Exception e) {
                System.err.println("Error during dto client edit:");
            }
            listeners.forEach(l -> l.update(dto));
            while (earlyDto.containsKey(nextSequenceNumber)) {
                SmallModelEditor nextDTO = earlyDto.remove(nextSequenceNumber);
                nextSequenceNumber++;
                try {
                    nextDTO.edit(smallModel);
                } catch (Error _) {
                }
                catch (Exception e) {
                    System.err.println("Error during dto client edit:");
                }
                listeners.forEach(l -> l.update(nextDTO));
            }
        } else {
            earlyDto.put(dto.getSequenceNumber(), dto);
        }
    }

    /**
     * Subscribes a standard UI observer/listener to the model updates if not already subscribed.
     *
     * @param l The observer component that requires game state deltas.
     */
    public void subscribe(ModelListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Retrieves the local SmallModel instance.
     * For testing purposes only!
     *
     * @return The local SmallModel instance.
     */
    public SmallModel getModel() {
        return smallModel;
    }

    /**
     * Sets the local SmallModel instance used by the client to track the game state.
     *
     * @param smallModel The SmallModel instance to be associated with this client.
     */
    public void setSmallModel(SmallModel smallModel) {
        this.smallModel = smallModel;
    }
}