/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsystem_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestsProcessing extends Thread {

    private DataInputStream dis;
    private DataOutputStream dos;
    private String uName;
    private Socket user_s;
    private int userIndex;

    public RequestsProcessing(int userIndex,ChatSystem_Server server) {
        try {
            user_s = server.user_s;
            dis = new DataInputStream(user_s.getInputStream());
            dos = new DataOutputStream(user_s.getOutputStream());
            this.userIndex = userIndex;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    @Override
    public void run() {
        try {
            String recRequest = "";
            while (true) {
                recRequest = dis.readUTF();
                if ("exit".equals(recRequest)) {
                    closeThisConnection();
                }
                else if (recRequest.split(" ")[0].contains("uAdd*")){//
                    add(recRequest);
                }
                  else if(recRequest.split(" ")[0].contains("pcMsg*")) {
                   sendPublicMsg(getAllMsg(recRequest));
                } else if (recRequest.split(" ")[0].contains("pvMsg*")) {
                    sendPrivateMsg(recRequest);
                }
               

            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private String getAllMsg(String str){
        String all = "";
        int size = str.split(" ").length;
        for(int i=1;i<size;i++)
            all  = all + str.split(" ")[i] + " ";
        return all;
    }
 public void sendPublicMsg(String recRequest) {
        chatAll(recRequest);
    }

    public void chatAll(String string) {
        for (int u=0;u<ChatSystem_Server.usersRecords.size();u++){
            if(ChatSystem_Server.usersRecords.get(u) != null){
                try {
                    ChatSystem_Server.usersRecords.get(u).dos.writeUTF("pcMsg* " +  uName +" "+string);
                    ChatSystem_Server.usersRecords.get(u).dos.flush();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }

            }  
            
        } 
        System.out.println(string + " from " + uName);
    }

    public void sendPrivateMsg(String recRequest) {
         String all = "";
        int size = recRequest.split(" ").length;
        for(int i=2;i<size;i++)
            all  = all + recRequest.split(" ")[i]+ " ";
         for(int u=0;u<ChatSystem_Server.usersRecords.size();u++){
             if(ChatSystem_Server.usersRecords.get(u) != null){
                 if(recRequest.split(" ")[1].equals(ChatSystem_Server.usersRecords.get(u).uName)){
                     try {
                         ChatSystem_Server.usersRecords.get(u).dos.writeUTF("pvMsg* "+ uName + " " + all);
                         ChatSystem_Server.usersRecords.get(u).dos.flush();
                         
                         System.out.println( all + " from " + uName + " to " +  ChatSystem_Server.usersRecords.get(u).uName);
                         break;
                     } catch (IOException ex) {
                         System.err.println(ex.getMessage());
                     }
             }
           
            }
        }
    }
    public void closeThisConnection() {

        try {
            dis.close();
            dos.close();
            user_s.close();
            ChatSystem_Server.usersRecords.remove(this);
            sendUNames();
         
            System.out.println(uName + "  closed");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void sendUNames() {
        String uNames = "uName ";
        for (int u=0;u<ChatSystem_Server.usersRecords.size();u++) {
            if(ChatSystem_Server.usersRecords.get(u) != null){
            uNames += ChatSystem_Server.usersRecords.get(u).uName + ((u != ChatSystem_Server.usersRecords.size()-1)?" ":"");
            }
              
        }
         for (int u=0;u<ChatSystem_Server.usersRecords.size();u++) {
             if(ChatSystem_Server.usersRecords.get(u) != null)
                  try {
                      ChatSystem_Server.usersRecords.get(u).dos.writeUTF(uNames);
                      ChatSystem_Server.usersRecords.get(u).dos.flush();
                  } catch (IOException ex) {
                      System.err.println(ex.getMessage());
                  }
              
        }

    }

    public void add(String recRequest) {
       
        uName = recRequest.split(" ")[1];//uAdd* username
        System.out.println(uName + " opens");
        sendUNames();
    }

    
   
    
}
