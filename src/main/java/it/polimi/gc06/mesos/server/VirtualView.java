package it.polimi.gc06.mesos.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.gc06.mesos.controller.GameController;

import java.beans.PropertyChangeEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VirtualView implements Runnable{

    private final Socket socket;
    private final String nickname;
    private final GameController controller;
    private final ModelListener listener;
    private final BlockingQueue<PropertyChangeEvent> noticeQueue;

    VirtualView(Socket socket, String nickname, GameController controller){
        this.socket = socket;
        this.nickname = nickname;
        this.controller = controller;
        noticeQueue = new LinkedBlockingQueue<>();
        listener = new ModelListener(noticeQueue);
        controller.addListener(listener);
    }

    public void unsubscribeListener(){
        controller.removeListener(listener);
    }

    @Override
    public void run() {

        //prepares the sender that responds to listener notice, necessary to ensure thread-safe notice
        Thread sender = new Thread(this::senderLoop);
        sender.start();

        try {
            //prepares input object
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //client input loop
            String input = "";
            while((input = inFromClient.readLine()) != null){

                //handling client input
                System.out.println("'"+nickname+"' client sent: "+input);
                try {
                    controller.parse(input);
                } catch(JsonProcessingException e){
                    System.err.print("Error on '"+nickname+"' client request: ");
                    e.printStackTrace();
                }
            }
        } catch(IOException e){
            System.err.print("Error on '"+nickname+"' client thread: ");
            e.printStackTrace();
        }
    }

    //it should never be called directly! only usable by a different Thread
    private void senderLoop(){

        //prepares the output object
        PrintWriter outToClient = null;
        try{
            outToClient =  new PrintWriter(socket.getOutputStream(),true);
        }catch (IOException e){
            System.err.print("Error on '"+nickname+"' client notice thread: ");
            e.printStackTrace();
            return;
        }

        //notice loop
        while(true){
            ObjectMapper mapper = new ObjectMapper();
            PropertyChangeEvent notice = null;
            try {
                notice = noticeQueue.take();
            }catch (InterruptedException _){ return; }
            try {
                outToClient.print(mapper.writeValueAsString(notice));
            }catch (IOException e){
                System.err.print("Error on '"+nickname+"' notice dispatch: ");
                e.printStackTrace();
            }
        }
    }

}
