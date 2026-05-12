package it.polimi.gc06.mesos.network_and_db.network;

import it.polimi.gc06.mesos.network.server.MatchManager;
import it.polimi.gc06.mesos.network.server.ServerMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.net.Socket;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.function.Try.success;

public class TCPClientDispatcherTest {

    @BeforeEach
    void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"1234", "1099"});
        });
        serverThread.start();
        try { Thread.sleep(1000); } catch (InterruptedException _) {}
    }

    @Test
    void testLogin(){
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("Alice"); //sends nickname
            assertTrue(((String)in.readObject()).equals("OK"));

        } catch (IOException | ClassNotFoundException e) {
            fail("Error during communication");
        }
    }

    @Test
    void testLoginFail(){
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String nickname = "Alice";
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");

        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String nickname = "Alice";
            out.writeObject(nickname); //sends nickname
            assertEquals("KO", (String) in.readObject(), "Nickname sent.");

        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    @Test
    void testLogout(){
        MatchManager manager = ServerMain.getMatchManager();
        try{
            Socket socket = new Socket("localhost", 1234);
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            String nickname = "Alice";
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");
            socket.close();
            try { Thread.sleep(500); } catch (InterruptedException _) {}
            assertFalse(manager.isUserLogged(nickname));


        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    @Test
    void testLoginAfterLogout(){
        String nickname = "Alice";
        try{
            Socket socket = new Socket("localhost", 1234);
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");
            socket.close();
            try { Thread.sleep(500); } catch (InterruptedException _) {}

        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
        //2nd login with the same nickname
        try{
            Socket socket = new Socket("localhost", 1234);
            assertTrue(socket.isConnected());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");

        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3}) //min 2, max 5 (both included), give a single value
    void testMatchCreation(int numOfPlayers){
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("Alice"); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");
            out.writeObject("CREATE");
            assertEquals("OK", (String) in.readObject(), "CREATE request sent");
            out.writeObject("1");
            assertEquals("KO", (String) in.readObject(), "Errata num of player sent.");
            out.writeObject(String.valueOf(numOfPlayers));
            assertEquals("OK", (String) in.readObject(), "Correct numOfPlayer sent");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    private void createMatch(String nickname, int numOfPlayers){
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent.");
            out.writeObject("CREATE");
            assertEquals("OK", (String) in.readObject(), "CREATE request sent");
            out.writeObject(String.valueOf(numOfPlayers));
            assertEquals("OK", (String) in.readObject(), "Correct numOfPlayer sent");

        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    /*With previous version of test we have seen that if a player disconnects when match is created but not started
       the server does not see this disconnection until the match has started so the cannot relog with the same nickname
       until match si started. */
    @ParameterizedTest
    @ValueSource(ints = {5})
    void testMultipleMatchCreation(int n){
        Random r = new Random();
        for(int i=0; i<n; i++) {
            createMatch("Alice"+i , r.nextInt(2, 6));
            try { Thread.sleep(1000); } catch (InterruptedException _) {}
        }
        System.out.println(ServerMain.getMatchManager().getAvailableMatchesString().replace(',','\n'));
        success("Matches created with success");
    }

    @Test
    void testMatchJoin(){
        createMatch("Alice",5);
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("Bob"); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent");
            out.writeObject("JOIN");
            in.readObject(); //waits for the server input
            out.writeObject("a!'/");
            assertNotEquals("OK",(String) in.readObject(),"Not numeric match id sent");
            out.writeObject("1");
            assertNotEquals("OK",(String) in.readObject(),"Not existing match id sent");
            out.writeObject("0");
            assertEquals("OK",(String) in.readObject(),"Correct match id sent");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    private void joinMatch(String nickname, int id){
        try (Socket socket = new Socket("localhost", 1234)) {
            assertTrue(socket.isConnected());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent");
            out.writeObject("JOIN");
            in.readObject(); //waits for the server input
            out.writeObject(String.valueOf(id));
            assertEquals("OK",(String) in.readObject(),"Correct match id sent");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3})
    void testMatchStart(int numOfPlayers){
        createMatch("Alice",2);
        createMatch("Bob",2);
        createMatch("Carl",numOfPlayers);
        for(int i=0;i<numOfPlayers-1;i++) joinMatch("David"+i,2);
        assertTrue(ServerMain.getMatchManager().hasMatchStarted(2) &&
                !ServerMain.getMatchManager().hasMatchEnded(2), "Check match state");
    }

    @Test
    void testLogoutAfterMatchCreation(){
        MatchManager mm = ServerMain.getMatchManager();
        createMatch("Alice",3);
        //the server should not yet see the disconnection
        assertTrue(mm.isUserLogged("Alice"), "Creator disconnection check");
        joinMatch("Bob",0);
        assertTrue(mm.isUserLogged("Bob"), "Joiner disconnection check");
        joinMatch("Carl",0);
        assertTrue(mm.hasMatchStarted(0) && !mm.hasMatchEnded(0), "Check match state");
        try { Thread.sleep(1000); } catch (InterruptedException _) {}
        assertFalse(mm.isUserLogged("Alice"), "Creator disconnection check after match started");
        assertFalse(mm.isUserLogged("Bob") || mm.isUserLogged("Carl"),
                "Joiners disconnection check after match started");
    }
}
