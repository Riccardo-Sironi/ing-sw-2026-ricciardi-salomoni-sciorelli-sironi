package it.polimi.gc06.mesos.network_and_db.network;

import it.polimi.gc06.mesos.network.server.ServerMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TCPClientManagerTest {

    record Client(Socket socket, BufferedReader in, PrintWriter out) {}
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
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(nickname); //sends nickname
            assertEquals("OK", in.readLine(), "Nickname sent");

            clients.put(nickname,new Client(socket,in,out));

        } catch (IOException e) {
            fail("Error during communication");
        }
    }

    private void createMatch(String nickname, int numOfPlayers){
        try{
            BufferedReader in = clients.get(nickname).in;
            PrintWriter out = clients.get(nickname).out;
            out.println("CREATE");
            assertEquals("OK", in.readLine(), "CREATE request sent");
            out.println(String.valueOf(numOfPlayers));
            assertEquals("OK", in.readLine(), "NumOfPlayer sent");
        } catch (IOException e) {
            fail("Error during communication");
        }
    }
    private void joinMatch(String nickname, int id){
        try{
            BufferedReader in = clients.get(nickname).in;
            PrintWriter out = clients.get(nickname).out;
            out.println("JOIN");
            in.readLine(); //waits for server input
            out.println(String.valueOf(id));
            assertEquals("OK", in.readLine(), "Match id sent");
        } catch (IOException e) {
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
    }

    @Test
    public void testGameStart(){
        String[] names = {"Alice","Bob","Carl","David","Eva"};
        startSimpleMatch(List.of(names));
    }



}
