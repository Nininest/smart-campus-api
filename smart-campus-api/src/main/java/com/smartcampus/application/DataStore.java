package com.smartcampus.application;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    // keyed by entity ID for 0(1) lookup
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    //Reading keyed by sensorId = ordered list of readings for that sensor
    private final Map<String,List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private DataStore(){}

    public static DataStore getInstance(){
        return INSTANCE;
    }

    public Map<String, Room> getRooms()  { return rooms;}
    public Map<String, Sensor> getSensors() { return sensors;}

    /**
     * Returns the reading list for a given sensor, creating an empty list if
     * none exists yet. computeTfAbsent is atomic - safe under concureent access.
     */
    public List<SensorReading> getReadingsForSensor(String sensorId) {
        return sensorReadings.computeIfAbsent(sensorId, id -> new CopyOnWriteArrayList<>());
    }
}


