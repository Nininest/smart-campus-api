package com.smartcampus.resources;

import com.smartcampus.application.DataStore;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sensor Operations and Linking
 * Sub-Resource Locator for readings
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllSensors(@QueryParam("type")String type) {
        List<Sensor> result = store.getSensors().values().stream()
                .filter(s -> type == null || s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        return Response.ok(result).build();
    }

    @POST
    public Response createSensor(Sensor sensor){
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field id is required. "))
                    .build();
        }
        if(sensor.getRoomId() == null || sensor.getRoomId().isBlank()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field roomId is required"))
                    .build();
        }
        if (!store.getRooms().containsKey(sensor.getRoomId())){
            throw new LinkedResourceNotFoundException("Room", sensor.getRoomId());
        }
        if (store.getSensors().containsKey(sensor.getId())){
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody(409, "Conflict",  "Sensor " + sensor.getId() + " already exists" ))
                    .build();
        }

        if (sensor.getStatus() == null || sensor.getStatus().isBlank()){
            sensor.setStatus("ACTIVE");
        }
        store.getSensors().put(sensor.getId(), sensor);

        store.getRooms().get(sensor.getRoomId()).addSensorId(sensor.getId());

        URI location = UriBuilder.fromResource(SensorResource.class)
                .path(sensor.getId())
                .build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId){
        Sensor sensor = store.getSensors().get(sensorId);
        if(sensor == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404, "Not Found", "Sensor " + sensorId + " does not exist."))
                    .build();
        }
        return Response.ok(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId){
        return new SensorReadingResource(sensorId);
    }

    private Map<String, Object> errorBody(int status, String error, String message){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}
