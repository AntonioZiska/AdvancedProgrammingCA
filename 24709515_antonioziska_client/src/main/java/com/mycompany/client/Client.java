/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author anton
 */
public class Client {
 private static InetAddress host;
    private static final int PORT = 6000;

    public static void main(String[] args) {
     try{
        host = InetAddress.getLocalHost();
     } 
     catch(UnknownHostException e){
	System.out.println("Host ID not found!");
	System.exit(1);
     }
     run();
   }
    
   private static void run() {
        boolean open = true;
        try (Socket link = new Socket(host,PORT)){
            
        BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
        PrintWriter out = new PrintWriter(link.getOutputStream(),true);

        BufferedReader userEntry =new BufferedReader(new InputStreamReader(System.in));
                
        do{
            String response;


                System.out.println("""
                                   Actions: book/cancel/list/import
                                   import sample: import; https://raw.githubusercontent.com/AntonioZiska/AdvancedProgrammingCA/refs/heads/main/bookings.txt""");
                String message =  userEntry.readLine();
                out.println(message);
                response = in.readLine();
                System.out.println("\n" + response);
                if(message.split(";")[0].trim().equalsIgnoreCase("import") && response != null && !response.split(" ")[0].equalsIgnoreCase("error:") && !response.equalsIgnoreCase("import complete")){
                    boolean run = true;
                    while(run){
                        response = in.readLine();
                        System.out.println("\n" + response);
                        if(response.equalsIgnoreCase("import complete")){
                            run = false;
                        }
                    }
                    response = in.readLine();
                    System.out.println("\n" + response);
                }                   
        if(response == null || response.equalsIgnoreCase("terminate")){
            open = false;
        }
        }while(open);
    }catch(IOException e){
	System.out.println(e);
    }
 }
}