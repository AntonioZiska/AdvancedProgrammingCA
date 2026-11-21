/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.booking;

/**
 *
 * @author anton
 */
public class Booking {
    private String name;
    private String wname;
    private String date;
    private String time;

    public Booking(String name, String wname, String date, String time) {
        this.name = name;
        this.wname = wname;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return   wname + " on " + date + " at " + time;
    }


}
