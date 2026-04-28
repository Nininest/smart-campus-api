package com.smartcampus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Discovery Endpoint
 */

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("api","Smart Campus Sensor & Room Management API");
        response.put("version", "1.0.0");
        response.put("description","Restful API for managing campus rooms, sensors, and sensor readings.");
        response.put("contact", "hamed.hamzeh@westminster.ac.uk");
        response.put("module", "5C0SC022W Client-Server Architectures");

        Map<String,String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        response.put("resources",resources);

        return Response.ok(response).build();
    }
}
