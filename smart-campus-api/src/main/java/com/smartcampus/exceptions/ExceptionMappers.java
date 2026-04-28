package com.smartcampus.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// 409 Room Not Empty
class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {
    @Provider
    public static class Registered extends RoomNotEmptyExceptionMapper {}

    @Override
            public Response toResponse(RoomNotEmptyException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",409);
        body.put("error", "Conflict");
        body.put("message","Room" + e.getRoomId() +"cannot be deleted."+
                "It still has" + e.getSensorCount()+" active sensor(s) assigned."+
                "please reassign or remove all sensors before decommissioning the room.");
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}

// 422 Linked Resource Not Found
class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Provider
    public static class Registered extends LinkedResourceNotFoundExceptionMapper {}

    @Override
    public Response toResponse(LinkedResourceNotFoundException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 422);
        body.put("error", "Unprocessable Entity");
        body.put("message", e.getResourceType() + "with id" + e.getResourceId() +
                " referenced in the request body does not exist.");
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}

// 403 Sensor Under Maintenance
class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Provider
    public static class Registered extends SensorUnavailableExceptionMapper {}

    @Override
    public Response toResponse(SensorUnavailableException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", "Sensor" + e.getSensorId() + "is currently under MAINTENANCE." +
                "New readings cannot be recorded until it is restored to ACTIVE status.");
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}

// 500 Global Safety Net
class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Provider
    public static class Registered extends GlobalExceptionMapper {}

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable t){
        LOG.log(Level.SEVERE, "Unhandled exception caught by global mapper",t);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",500);
        body.put("error","Internal Server Error");
        body.put("message", "An unexpected error occurred. Please contact the API administration.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}