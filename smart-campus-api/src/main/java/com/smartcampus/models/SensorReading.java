package com.smartcampus.models;

import java.util.UUID;

public class SensorReading {

    private String id;
    private double value;
    private long timestamp;

    // Default constructor for Jaxrs
    public SensorReading() {}

    /**
     * Constructor used when the service creates a new reading.
     * Automatically assigns a UUID and the current epoch time.
     */
    public SensorReading(double value) {
        this.id = UUID.randomUUID().toString();
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    //Getters and Setters

    public String getId()   {return id; }
    public void setId(String id)  {this.id = id;}

    public double getValue()    {return value;}
    public void setValue(double value)   { this.value = value;}

    public long getTimestamp()    {return timestamp;  }
    public void setTimestamp(long ts)  {this.timestamp = ts;  }
    }
    