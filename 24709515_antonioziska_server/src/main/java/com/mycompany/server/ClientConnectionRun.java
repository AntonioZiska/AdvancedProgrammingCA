/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.server;

import com.mycompany.booking.Booking;
import com.mycompany.exception.InvalidCommandException;
import static com.mycompany.server.Server.list;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;

/**
 *
 * @author anton
 */
public class ClientConnectionRun implements Runnable {
    Socket link = null;
    
    public ClientConnectionRun(Socket socketConnection) {
        this.link = socketConnection;
  }
    
    @Override
    public void run(){
    
    try{
        BufferedReader in = new BufferedReader( new InputStreamReader(link.getInputStream()));
        PrintWriter out = new PrintWriter(link.getOutputStream(),true);

        boolean running = true;
        while (running){
            try{
        String message = in.readLine();
        if (message == null){ 
            System.out.println("Client abruptly disconected");
            return;
        }
        synchronized (list) {
        if(message.split(";", -1)[0].trim().equalsIgnoreCase("stop")){
            out.println("TERMINATE");
            running = false;
            
        }
        else if(message.split(";")[0].trim().equalsIgnoreCase("import")){
            if (message.split(";", -1).length != 2) {
               throw new InvalidCommandException("import command input has to look like: import; <public-URL-to-bookings.txt>");
            }
            handleImport(message.split(";")[1].trim(), out);
        }
        else if(message.trim().split(";", -1).length != 5){
            throw new InvalidCommandException("Incorrect number of fields(5 expected)");
        }
        else{
        String[] msg = message.trim().split(";", -1);
            Booking booking = new Booking(msg[1].trim(), msg[2].trim(),  msg[3].trim(), msg[4].trim());
        
        switch (message.split(";")[0].trim().toLowerCase()){
            case "book" -> {
                Book(booking, out, msg);
            }
            case "list" -> {
                if (!msg[1].trim().equalsIgnoreCase("-") && msg[2].trim().equalsIgnoreCase("-") && msg[3].trim().equalsIgnoreCase("-") && msg[4].trim().equalsIgnoreCase("-")) {
                    List(out, msg);
                }
                else{
                    throw new InvalidCommandException("list command input has to look like: list; name; -; -; -");
                }
            }
            case "cancel" -> {
                if ((msg[1].trim().equalsIgnoreCase("-") || msg[2].trim().equalsIgnoreCase("-")) || msg[3].trim().equalsIgnoreCase("-") || msg[4].trim().equalsIgnoreCase("-")) {
                    throw new InvalidCommandException("cancel command input has to look like: cancel; name; workshop name; date; time");
                } 
                else {

                if(list.removeIf(book -> book.getName().equalsIgnoreCase(booking.getName()) && book.getWname().equalsIgnoreCase(booking.getWname()) && book.getDate().equalsIgnoreCase(booking.getDate()) && book.getTime().equalsIgnoreCase(booking.getTime()))){
                    List(out, msg);
                }
                else{
                    throw new InvalidCommandException(msg[1].trim() + " does not currently have this booking");
                }
                
                }
            }
            default -> throw new InvalidCommandException("action must be book/cancel/list");
        }
      }
     }
    }catch (InvalidCommandException e) {
        System.out.println("Error: " + e.getMessage());
        out.println("ERROR: " + e.getMessage());
    }
        }
    }catch(IOException e){
        System.out.println(e);
    }finally{
        try {
            link.close();
	}catch(NullPointerException e){
            System.out.println("Failed to connect!");
        }catch(IOException e){
            System.out.println("Failed to disconnect!");
       }
    }
    }
        
    
    public static void List(PrintWriter out, String[] msg) {
        int numb = 0;
        String back = "";
        for (Booking bookings : list) {
            if (bookings.getName().equalsIgnoreCase(msg[1].trim())) {
                numb++;
                back += (bookings) + "; ";
            }
        }
        out.println(msg[1].trim() + " currently has " + numb + " booking(s): " + back);
    }
    
    public static void Book(Booking booking, PrintWriter out, String[] msg) throws InvalidCommandException {
        if ((msg[1].trim().equalsIgnoreCase("-") || msg[2].trim().equalsIgnoreCase("-")) || msg[3].trim().equalsIgnoreCase("-") || msg[4].trim().equalsIgnoreCase("-")) {
            throw new InvalidCommandException("book command input has to look like: book; name; workshop name; date; time");
        } 
        else {
            for (Booking bookings : list) {
                if (bookings.getName().equalsIgnoreCase(msg[1].trim()) && bookings.getWname().equalsIgnoreCase(msg[2].trim()) && bookings.getDate().equalsIgnoreCase(msg[3].trim()) && bookings.getTime().equalsIgnoreCase(msg[4].trim())) {
                    throw new InvalidCommandException("This booking already exists");
                }
            }
            list.add(booking);
            List(out, msg);
        }
    }
    private static void handleImport(String urlString, PrintWriter out) throws InvalidCommandException {
        
        int s = 0; 
        int i = 0;
            
        try {
            URLConnection conn = URI.create(urlString).toURL().openConnection();


                
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {



                String line = reader.readLine();
                if (line == null || !line.trim().toLowerCase().startsWith("member=")) {
                    throw new InvalidCommandException("First line must be member=<Name>");
                }                
                String name = line.trim().substring(7).trim();
                if (name.isEmpty() || name.equalsIgnoreCase("-")){
                    throw new InvalidCommandException("missing name in import file");
                }
                while ((line = reader.readLine()) != null) {
                    String message = "book; " + name + "; " + line;

                    String[] msg = message.trim().split(";", -1);
                    if(line.trim().isEmpty() || msg.length != 5 || msg[2].trim().equalsIgnoreCase("-") || msg[3].trim().equalsIgnoreCase("-") || msg[4].trim().equalsIgnoreCase("-")){
                        s++;
                        out.println("line skipped due to malformed/missing data");
                        continue;
                    }
                    try{
                        Booking booking = new Booking(msg[1].trim(), msg[2].trim(),  msg[3].trim(), msg[4].trim());
                        Book(booking, out, msg);
                        i++;
                    }catch(InvalidCommandException e){
                        s++;
                        out.println("duplicate");
                    }
                }
                out.println("import complete");
                out.println("imported " + i + " skipped " + s);

            }
        }catch (IOException | IllegalArgumentException e) {
            throw new InvalidCommandException("import failed: " + e.getMessage());
        }
    }

}
