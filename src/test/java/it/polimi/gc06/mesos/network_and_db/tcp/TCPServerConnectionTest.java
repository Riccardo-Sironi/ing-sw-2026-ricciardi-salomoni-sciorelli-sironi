package it.polimi.gc06.mesos.network_and_db.tcp;

import it.polimi.gc06.mesos.model.gameTurnManager.OfferResolutionPhase;
import it.polimi.gc06.mesos.model.gameTurnManager.PlacingTotemPhase;
import it.polimi.gc06.mesos.network.client.Client;
import it.polimi.gc06.mesos.network.client.ServerConnection;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.view.smallModel.PlayerView;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TCPServerConnectionTest {

    @BeforeEach
    void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"1234", "1099"});
        });
        serverThread.start();
        try { Thread.sleep(1000); } catch (InterruptedException _) {}
    }

    @Test
    public void testLogin(){
        String nickname = "Alice";
        Client c = new Client(nickname);
        c.connect("TCP","localhost",1234);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "Login failed");
        } catch(Exception e) { fail("Login failed due to exception: "+e.getMessage()); }
    }

    @Test
    public void testMatchCreation(){
        String nickname = "Alice";
        Client c = new Client(nickname);
        c.connect("TCP","localhost",1234);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "Login failed");
            conn.createMatch(5,nickname);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Match creation failed due to exception: ");
        }
    }

    @Test
    public void testMatchJoin(){
        String nickname = "Alice";
        Client c = new Client(nickname);
        c.connect("TCP","localhost",1234);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "Login failed");
            conn.createMatch(5,nickname);
        } catch(Exception e) { fail("Match creation failed due to exception: "+e.getMessage()); }

        String nickname2 = "Bob";
        Client c2 = new Client(nickname);
        c2.connect("TCP","localhost",1234);
        ServerConnection conn2 = c2.getServerConnection();
        try {
            assertTrue(conn2.login(nickname2), "Login failed");
            assertTrue(conn2.joinMatch(0,nickname2));
        } catch(Exception e) { fail("Match join failed due to exception: "+e.getMessage()); }
    }

    @Test
    public void testMatchStart(){
        createMatchAndStart(3);
    }

    private List<Client> createMatchAndStart(int n){
        String nickname = "Alice";
        ArrayList<Client> clients = new ArrayList<Client>();
        clients.add(new Client(nickname));
        Client c = clients.getFirst();
        c.connect("TCP","localhost",1234);
        ServerConnection conn = c.getServerConnection();
        try {
            assertTrue(conn.login(nickname), "'"+nickname+"' login failed");
            conn.createMatch(3,nickname);
        } catch(Exception e) {
            e.printStackTrace();
            fail("Match creation failed due to exception.");
        }

        for(int i=0; i<n-1; i++){
            String nickname2 = "Bob"+i;
            Client c2 = new Client(nickname2);
            clients.add(c2);
            c2.connect("TCP","localhost",1234);
            ServerConnection conn2 = c2.getServerConnection();
            try {
                assertTrue(conn2.login(nickname2), "'"+nickname2+"' login failed");
                conn2.joinMatch(0,nickname2);
            } catch(Exception e) { fail("'"+nickname2+"' match join failed due to exception: "+e.getMessage()); }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String expectedPhase = new PlacingTotemPhase().toString();

        assertEquals(expectedPhase, clients.get(0).getModel().getPhase(), nickname + " doesn't see the game start.");
        assertEquals(expectedPhase, clients.get(1).getModel().getPhase(), "Bob0 doesn't see the game start.");
        assertEquals(expectedPhase, clients.get(2).getModel().getPhase(), "Bob1 doesn't see the game start.");

        return clients;
    }

    private Client getActiveClient(List<Client> clients){
        return clients.stream().filter(c -> c.getModel().isActive()).findFirst().orElse(null);
    }

    @ParameterizedTest
    @ValueSource(ints = {400})
    public void testFirstPhase(int timeout) throws Exception {
        int n = 3;
        List<Client> clients = createMatchAndStart(n);

        //placing totem phase
        for(int i=0;i<n;i++){
            Client active = getActiveClient(clients);
            if(active == null) fail("Active player not found.");
            try{
                active.getServerConnection().placeTotem(active.getModel().getPlayer().getNickname(),i);
                Thread.sleep(timeout);
            }catch (Exception e){
                e.printStackTrace();
                fail("Unexpected exception during placing totem phase");
            }
        }

        //check if phase has changed
        Thread.sleep(timeout);
        assertTrue(clients.stream().map(c -> c.getModel().getPhase())
                .allMatch(p -> p.equals(new OfferResolutionPhase().toString())),"Phase didnt " +
                "change in specified time requirements");
        //offer resolution phase
    }

}
