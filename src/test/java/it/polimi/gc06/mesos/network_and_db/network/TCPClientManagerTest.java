package it.polimi.gc06.mesos.network_and_db.network;

import it.polimi.gc06.mesos.dtos.GameStartedDTO;
import it.polimi.gc06.mesos.dtos.SmallModelEditor;
import it.polimi.gc06.mesos.network.server.ServerMain;
import it.polimi.gc06.mesos.view.smallModel.SmallModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TCPClientManagerTest {

    HashMap<String,Client> clients = new HashMap<>();

    @BeforeEach
    void setup() {
        Thread serverThread = new Thread(() -> {
            ServerMain.main(new String[]{"1234", "1099"});
        });
        serverThread.start();
        try { Thread.sleep(1000); } catch (InterruptedException _) {}
    }

    private void connect(String nickname){
        try {
            Socket socket = new Socket("localhost",1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            out.writeObject(nickname); //sends nickname
            assertEquals("OK", (String) in.readObject(), "Nickname sent");

            clients.put(nickname,new Client(socket,in,out,new SmallModel(nickname)));

        } catch (IOException | ClassNotFoundException e) {
            fail("Error during communication");
        }
    }

    private void createMatch(String nickname, int numOfPlayers){
        try{
            ObjectInputStream in = clients.get(nickname).in;
            ObjectOutputStream out = clients.get(nickname).out;
            out.writeObject("CREATE");
            assertEquals("OK", (String) in.readObject(), "CREATE request sent");
            out.writeObject(String.valueOf(numOfPlayers));
            assertEquals("OK", (String) in.readObject(), "NumOfPlayer sent");
            System.out.println("Match created successfully.");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }
    private void joinMatch(String nickname, int id){
        try{
            ObjectInputStream in = clients.get(nickname).in;
            ObjectOutputStream out = clients.get(nickname).out;
            out.writeObject("JOIN");
            in.readObject(); //waits for server input
            out.writeObject(String.valueOf(id));
            assertEquals("OK", (String) in.readObject(), "Match id sent");
        } catch (ClassNotFoundException | IOException e) {
            fail("Error during communication");
        }
    }

    private void startSimpleMatch(List<String> names){
        for(String name : names){
            connect(name);
        }
        createMatch(names.getFirst(),names.size());
        for(int i=1 ; i<names.size(); i++){
            joinMatch(names.get(i),0);
        }
        try { Thread.sleep(500); } catch (InterruptedException _) {}
        assertTrue(ServerMain.getMatchManager().hasMatchStarted(0),"Match started check");
        System.out.println("Match started successfully.");
    }

    @Test
    public void testGameStart(){
        String[] names = {"Alice","Bob","Carl","David","Eva"};
        startSimpleMatch(List.of(names));
        try {
            Client c = clients.get(names[0]);
            SmallModelEditor editor = (SmallModelEditor) c.in.readObject(); //should be GameStartedDTO
            assertInstanceOf(GameStartedDTO.class, editor, "Correct dto sent");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail("Unexpected IOException");
        }
    }


    record Client (Socket socket, ObjectInputStream in, ObjectOutputStream out, SmallModel model){}
}
