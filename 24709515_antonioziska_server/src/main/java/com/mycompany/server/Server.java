/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server;

import com.mycompany.booking.Booking;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anton
 */
public class Server {
    private static ServerSocket servSock;
    private static final int PORT = 6000;
    private static int clientConnections = 0;
    public static final List<Booking> list= new ArrayList<>();
  
  public static void main(String[] args) {
    try{
        servSock = new ServerSocket(PORT);
    }catch(IOException e){
        System.out.println("Unable to attach to port");
        System.exit(1);
    }do{
        run();
    }while (true);

  }
  
  private static void run(){
    try{
        Socket link = servSock.accept();
        clientConnections++;
        Runnable resource = new ClientConnectionRun(link);
        Thread t = new Thread (resource);
        t.start();
    }catch(IOException e){
        System.out.println(e);
    }
  }
}

