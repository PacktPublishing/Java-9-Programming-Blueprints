package com.steeplesoft.monumentum.rest.resource;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import com.steeplesoft.monumentum.model.Note;
import com.steeplesoft.monumentum.model.User;
import com.steeplesoft.monumentum.mongo.Collection;
import com.steeplesoft.monumentum.security.Secure;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bson.Document;
import org.bson.types.ObjectId;

@Path("/notes")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Secure
//***************************************************
// TODO: Move all of this business logic to service classes
//***************************************************
public class NoteResource {

    @Inject
    private User user;
    @Inject
    @Collection("notes")
    private MongoCollection<Document> collection;

    @Context
    private UriInfo uriInfo;

    @PostConstruct
    public void postConstruct() {
        String host = System.getProperty("mongo.host", "localhost");
        String port = System.getProperty("mongo.port", "27017");
    }

    @GET
    public Response getAll() {
        List<Note> notes = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(new BasicDBObject("user_id", user.getId())).iterator()) {
            while (cursor.hasNext()) {
                notes.add(new Note(cursor.next()));
            }
        }

        return Response.ok(new GenericEntity<List<Note>>(notes) {
        }).build();
    }

    @GET
    @Path("{id}")
    public Response getNote(@PathParam("id") String id) {
        Document doc = collection.find(buildQueryById(id)).first();
        if (doc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Note(doc)).build();
        }
    }

    @POST
    public Response createNote(Note note) {
        Document doc = note.toDocument();
        doc.append("user_id", user.getId());
        collection.insertOne(doc);
        final String id = doc.get("_id", ObjectId.class).toHexString();

        return Response.created(uriInfo.getRequestUriBuilder()
                .path(id).build())
                .build();
    }

    @PUT
    @Path("{id}")
    public Response updateNote(Note note) {
        note.setModified(LocalDateTime.now());
        note.setUser(user.getId());
        UpdateResult result = 
                collection.updateOne(buildQueryById(note.getId()),
                new Document("$set", note.toDocument()));
        if (result.getModifiedCount() == 0) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok().build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteNote(@PathParam("id") String id) {
        collection.deleteOne(buildQueryById(id));
        return Response.ok().build();
    }

    private BasicDBObject buildQueryById(String id) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id))
                .append("user_id", user.getId());
        return query;
    }
}
