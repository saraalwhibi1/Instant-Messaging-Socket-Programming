/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsystem_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ChatSystem_Server {

    public static List<RequestsProcessing> usersRecords = new LinkedList <RequestsProcessing>();
    public static int userIndex = 0;
    public static Socket user_s;

    public ChatSystem_Server(){
        try {
            ServerSocket server_s = new ServerSocket(5111);
            while(true){
               user_s = server_s.accept();
               RequestsProcessing requestProcessing = new RequestsProcessing(userIndex,this);
               requestProcessing.start();
               usersRecords.add(requestProcessing);
               userIndex++;
           }
            
            
        } catch (IOException ex) {
           System.err.println(ex.getMessage());
           
           
        }
    }
    public static void main(String[] args) {
        System.out.println("Welcome\n#########################################################");
        
       new ChatSystem_Server();
    }
    
}
