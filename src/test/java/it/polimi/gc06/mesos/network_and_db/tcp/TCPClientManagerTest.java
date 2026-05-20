package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.controller.commands.ControllerCommand;
import it.polimi.gc06.mesos.controller.commands.Request;
import it.polimi.gc06.mesos.dtos.ErrorDTO;
import it.polimi.gc06.mesos.dtos.LobbyInitializedDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TCPClientManagerTest {

    private final HashMap<String, Client> clients = new HashMap<>();

    @BeforeEach
    void setup() {
        clients.clear();

        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"1234", "1099"});
        });
        serverThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException _) {
        }
    }

    private void connect(String nickname) {
        try {
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("LOGIN" + nickname);
            assertEquals("OK", (String) in.readObject(), "Nickname login failed");

            clients.put(nickname, new Client(socket, in, out, new SmallModel(nickname)));

        } catch (IOException | ClassNotFoundException e) {
            fail("Error during communication setup for " + nickname);
        }
    }

    private void createMatch(String nickname, int numOfPlayers) {
        try {
            ObjectInputStream in = clients.get(nickname).in;
            ObjectOutputStream out = clients.get(nickname).out;
            out.writeObject("CREATE" + numOfPlayers);
            assertNotEquals("KO", (String) in.readObject(), "CREATE request failed");
            System.out.println("Match created successfully by " + nickname);
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during match creation");
        }
    }

    private void joinMatch(String nickname, int id) {
        try {
            ObjectInputStream in = clients.get(nickname).in;
            ObjectOutputStream out = clients.get(nickname).out;
            out.writeObject("JOIN" + id);
            assertEquals("OK", (String) in.readObject(), "JOIN request failed");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during match join");
        }
    }

    private void startSimpleMatch(List<String> names) {
        for (String name : names) {
            connect(name);
        }
        createMatch(names.getFirst(), names.size());
        for (int i = 1; i < names.size(); i++) {
            joinMatch(names.get(i), 0);
        }
        try {
            Thread.sleep(800);
        } catch (InterruptedException _) {
        }
        assertTrue(ServerMain.getMatchManager().hasMatchStarted(0), "Match started check");
    }

    @Test
    public void testGameStart() {
        String[] names = {"Alice", "Bob", "Carl"};
        startSimpleMatch(List.of(names));
        try {
            Client c = clients.get(names[0]);
            SmallModelEditor editor = (SmallModelEditor) c.in.readObject();
            assertInstanceOf(LobbyInitializedDTO.class, editor, "First DTO should be GameStartedDTO");
        } catch (ClassNotFoundException | IOException e) {
            fail("Unexpected exception while reading dto: " + e.getMessage());
        }
    }

    private String getActivePlayer() throws NoSuchElementException {
        return clients.entrySet().stream().filter(e -> e.getValue().model.isActive())
                .map(Entry::getKey).findFirst().orElseThrow(() -> new NoSuchElementException("Can't find active player"));
    }

    private List<String> getInactivePLayers() throws NoSuchElementException {
        List<String> r = clients.entrySet().stream().filter(e -> !e.getValue().model.isActive())
                .map(Entry::getKey).toList();
        if (r.isEmpty()) throw new NoSuchElementException("Cant get inactive player");
        return r;
    }

    @Test
    public void testReceiveErrorDTO() {
        String[] names = {"Alice", "Bob", "Carl"};
        // Avvia una partita standard a 3 giocatori
        startSimpleMatch(List.of(names));

        try {
            for (String name : names) {
                SmallModelEditor firstDto = (SmallModelEditor) clients.get(name).in().readObject();
                assertInstanceOf(LobbyInitializedDTO.class, firstDto, "First dto should be GameStartedDTO");
                firstDto.edit(clients.get(name).model);
            }

            String active = getActivePlayer();
            List<String> inactive = getInactivePLayers();
            Client activeC = clients.get(active);
            List<Client> inactiveC = inactive.stream().map(clients::get).toList();

            //null action (from active player)
            activeC.out().writeObject(null);
            Object responseDto = activeC.in().readObject();
            assertInstanceOf(ErrorDTO.class, responseDto, "Server should have answered with an ErrorDTO");

            //invalid action (from active player)
            ControllerCommand invalidCommand = new ControllerCommand(active, 0, Request.SKIP_REQUEST);
            activeC.out().writeObject(invalidCommand);
            responseDto = activeC.in().readObject();
            assertInstanceOf(ErrorDTO.class, responseDto, "Server should have answered with an ErrorDTO");

            //valid action from inactive player
            ControllerCommand validCommand = new ControllerCommand(inactive.getFirst(), 0, Request.OFFER_TRACK_REQUEST);
            inactiveC.getFirst().out().writeObject(validCommand);
            responseDto = inactiveC.getFirst().in().readObject();
            assertInstanceOf(ErrorDTO.class, responseDto, "Server should have answered with an ErrorDTO");

        } catch (ClassNotFoundException | IOException e) {
            fail("Unexpected exception while reading dto: " + e.getMessage());
        } catch (NoSuchElementException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testReceiveCorrectDTO() {
        String[] names = {"Alice", "Bob", "Carl"};
        startSimpleMatch(List.of(names));

        try {
            for (String name : names) {
                SmallModelEditor firstDto = (SmallModelEditor) clients.get(name).in().readObject();
                assertInstanceOf(LobbyInitializedDTO.class, firstDto, "First dto should be GameStartedDTO");
                firstDto.edit(clients.get(name).model);
            }

            String active = getActivePlayer();
            Client activeC = clients.get(active);

            int pos = 0;
            activeC.out.writeObject(new ControllerCommand(active, pos, Request.OFFER_TRACK_REQUEST));
            SmallModelEditor dto = (SmallModelEditor) activeC.in.readObject();
            dto.edit(activeC.model);
            assertEquals(activeC.model.getOfferTrack().get(pos).getPlayer().getNickname(), active, "Dto didnt modify the model correctly.");

        } catch (ClassNotFoundException | IOException e) {
            fail("Unexpected exception while reading dto: " + e.getMessage());
        } catch (NoSuchElementException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }

    @Test
    public void testFirstDtoSetsPhase() {
        String[] names = {"Alice", "Bob", "Carl"};
        // Avvia una partita standard a 3 giocatori
        startSimpleMatch(List.of(names));

        try {
            for (String name : names) {
                SmallModelEditor firstDto = (SmallModelEditor) clients.get(name).in().readObject();
                assertInstanceOf(LobbyInitializedDTO.class, firstDto, "First dto should be GameStartedDTO");
                firstDto.edit(clients.get(name).model);
                assertEquals(clients.get(name).model.getPhase(), (new PlacingTotemPhase()).toString());
            }

        } catch (ClassNotFoundException | IOException e) {
            fail("Unexpected exception while reading dto: " + e.getMessage());
        } catch (NoSuchElementException e) {
            fail("Unexpected error: " + e.getMessage());
        }
    }


    record Client(Socket socket, ObjectInputStream in, ObjectOutputStream out, SmallModel model) {
    }
}
