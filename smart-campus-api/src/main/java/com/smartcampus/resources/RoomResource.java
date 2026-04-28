package com.smartcampus.resources;

import com.smartcampus.application.DataStore;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Room Management
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(store.getRooms().values());
        return Response.ok(roomList).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'id' is required."))
                    .build();
        }
        if (room.getName() == null || room.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorBody(400, "Bad Request", "Field 'name' is required."))
                    .build();
        }
        if (store.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorBody(409, "conflict", "A room with id" + room.getId() + "already exists."))
                    .build();
        }
        store.getRooms().put(room.getId(), room);

        URI location = UriBuilder.fromResource(RoomResource.class)
                .path(room.getId())
                .build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomBy(@PathParam("roomId") String roomId){
        Room room = store.getRooms().get(roomId);
        if (room == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404, "Not Found", "Room " + roomId + " does not exist"))
                    .build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId){
        Room room = store.getRooms().get(roomId);
        if (room == null){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorBody(404,"Not Found","Room" + roomId + "does not exist."))
                    .build();
        }
         if (!room.getSensorIds().isEmpty()) {
             throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
         }
          store.getRooms().remove(roomId);
          return Response.noContent().build();
    }

    private java.util.Map<String,Object> errorBody(int status, String error, String message) {
        java.util.Map<String,Object> body =  new java.util.LinkedHashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}