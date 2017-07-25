package com.steeplesoft.deskdroid.desktop;

import com.steeplesoft.deskdroid.model.Conversation;
import com.steeplesoft.deskdroid.model.Message;
import com.steeplesoft.deskdroid.model.Participant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author jason
 */
public class ConversationService {

    protected final Client client;
    protected String phoneAddress;
    protected String token;
    protected final Map<String, Participant> participants = new HashMap<>();
    protected final DeskDroidPreferences preferences = DeskDroidPreferences.getInstance();
    protected boolean stopListening = false;

    public static class LazyHolder {

        public static final ConversationService INSTANCE = new ConversationService();
    }

    public static ConversationService getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ConversationService() {
        Configuration configuration = new ResourceConfig()
                .register(JacksonFeature.class)
                .register(SseFeature.class);
        client = ClientBuilder.newClient(configuration);
    }

    public WebTarget getWebTarget() {
        WebTarget webTarget = client.target("http://"
                + preferences.getPhoneAddress()
                + ":49152/");
        return webTarget;
    }

    public void setPhoneAddress(String phoneAddress) {
        this.phoneAddress = phoneAddress;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Optional<String> getAuthorization(String code) {
        Response response = getWebTarget().path("authorize")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.text(code));

        Optional<String> result;
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            token = response.readEntity(String.class);
            result = Optional.of(token);
        } else {
            result = Optional.empty();
        }

        return result;
    }

    public List<Conversation> getConversations() {
        List<Conversation> list;

        try {
            list = getWebTarget().path("conversations")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .get(new GenericType<List<Conversation>>() {
                    });
        } catch (Exception ce) {
            list = new ArrayList<>();
        }
        return list;
        //Arrays.asList(list.get(0));
    }

    public void subscribeToNewMessageEvents(Consumer<Message> callback) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                stopListening = false;
                EventInput eventInput = getWebTarget().path("status")
                        .request()
                        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                        .get(EventInput.class);
                while (!eventInput.isClosed() && !stopListening) {
                    final InboundEvent inboundEvent = eventInput.read();
                    if (inboundEvent == null) {
                        // connection has been closed
                        break;
                    }
                    if ("new-message".equals(inboundEvent.getName())) {
                        Message message = inboundEvent.readData(Message.class);
                        if (message != null) {
                            callback.accept(message);
                        }
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void stopListeningForNewMessages() {
        stopListening = true;
    }

    public boolean sendMessage(Message message) {
        Response r = getWebTarget().path("conversations")
                .request()
                .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                .post(Entity.json(message));

        return r.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    public Participant getParticipant(String number) {
        if (number == null) {
            return null;
        }
        Participant p = participants.get(number);
        if (p == null) {
            Response response = getWebTarget()
                    .path("participants")
                    .path(number)
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeader())
                    .get(Response.class);
            if (response.getStatus() == 200) {
                p = response.readEntity(Participant.class);
                participants.put(number, p);
                if (p.getThumbnail() != null) {
                    File thumb = new File(number + ".png");
                    thumb.deleteOnExit();
                    try (OutputStream stream = new FileOutputStream(thumb)) {
                        byte[] data = DatatypeConverter.parseBase64Binary(p.getThumbnail());
                        stream.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return p;
    }

    public InputStream getParticipantThumbnail(String participant) {
        try {
            return new FileInputStream(new File(participant + ".png"));
        } catch (FileNotFoundException ex) {
            return getClass().getResourceAsStream("/unknown.png");
        }
    }

    private String getAuthorizationHeader() {
        return "Bearer " + preferences.getToken();
    }
}
