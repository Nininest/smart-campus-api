package com.smartcampus.models;

public class Sensor {

    private String id;
    private String type;
    private String roomId;
    private double currentValue;
    private String status;

    // Default constructor FOR JAXRS
    public Sensor() {
    }

    public Sensor(String id, String type, String roomId, double currentValue, String status) {
        this.id = id;
        this.type = type;
        this.roomId = roomId;
        this.currentValue = currentValue;
        this.status = status;
    }
    // Getters and Setters

    public String getId()  { return id;  }
    public void  setId ( String id) {this.id = id;}

    public String getType() { return type;  }
    public void   setType(String type) {this.type = type;}

    public String getRoomId()  {return roomId;}
    public void setRoomId(String roomId) { this.roomId = roomId;}

    public double getCurrentValue()   {return currentValue;}
    public void  setCurrentValue(double currentValue) {this.currentValue = currentValue;}

    public String getStatus()  {return status;}
    public void  setStatus (String status) {this.status= status;}
}

