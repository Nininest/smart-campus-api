package com.smartcampus.resources;


import com.smartcampus.application.DataStore;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId){
        this.sensorId = sensorId;
    }

    @GET
    public Response getReadings(){
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404, "Not Found", "Sensor " + sensorId + " does not exist."))
                    .build();
        }
        List<SensorReading> history = store.getReadingsForSensor(sensorId);
        return Response.ok(history).build();
    }

    @POST
    public Response addReading(SensorReading reading){
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404, "Not Found", "Sensor " + sensorId + " does not exist."))
                    .build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())){
            throw new SensorUnavailableException(sensorId);
        }
        if(reading == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Reading body with value field is required."))
                    .build();
        }

        SensorReading record = new SensorReading(reading.getValue());
        store.getReadingsForSensor(sensorId).add(record);

        sensor.setCurrentValue(record.getValue());

        return Response.status(Response.Status.CREATED).entity(record).build();
    }

    private Map<String, Object> errorBody(int status, String error, String message) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message",message);
        return body;
    }
}
