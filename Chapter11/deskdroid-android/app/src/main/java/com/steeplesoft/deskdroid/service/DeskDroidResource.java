package com.steeplesoft.deskdroid.service;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;

import com.steeplesoft.deskdroid.model.Conversation;
import com.steeplesoft.deskdroid.model.ConversationComparator;
import com.steeplesoft.deskdroid.model.Message;
import com.steeplesoft.deskdroid.model.MessageComparator;
import com.steeplesoft.deskdroid.model.Participant;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by jason on 4/27/2017.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class DeskDroidResource {
    private DeskDroidService deskDroidService;

    public DeskDroidResource(DeskDroidService deskDroidService) {
        this.deskDroidService = deskDroidService;
    }

    @GET
    @Path("conversations")
    @Secure
    public Response getConversations() {
        List<Conversation> conversations = new ArrayList<>();
        Cursor cur = deskDroidService.getApplication().getContentResolver()
                .query(Telephony.Sms.Conversations.CONTENT_URI, null, null, null, null);
        while (cur.moveToNext()) {
            conversations.add(buildConversation(cur));
        }
        cur.close();

        Collections.sort(conversations, new ConversationComparator());

        return Response.ok(new GenericEntity<List<Conversation>>(conversations) {
        }).build();
    }

    @POST
    @Path("conversations")
    @Secure
    public Response sendMessage(Message message) throws InterruptedException {
        final SmsManager sms = SmsManager.getDefault();
        final ArrayList<String> parts = sms.divideMessage(message.getBody());
        final CountDownLatch sentLatch = new CountDownLatch(parts.size());
        final AtomicInteger statusCode =
                new AtomicInteger(Response.Status.CREATED.getStatusCode());
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() != Activity.RESULT_OK) {
                    statusCode.set(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                }
                sentLatch.countDown();
            }
        };
        deskDroidService.registerReceiver(receiver, new IntentFilter("com.steeplesoft.deskdroid.SMS_SENT"));
        ArrayList<PendingIntent> sentPIs = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            sentPIs.add(PendingIntent.getBroadcast(deskDroidService.getApplicationContext(), 0,
                    new Intent("com.steeplesoft.deskdroid.SMS_SENT"), 0));
        }
        sms.sendMultipartTextMessage(message.getAddress(), null, parts, sentPIs, null);

        sentLatch.await(5, TimeUnit.SECONDS);
        deskDroidService.unregisterReceiver(receiver);
        return Response.status(statusCode.get()).build();

    }

    @GET
    @Path("status")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Secure
    public EventOutput streamStatus() {
        final EventOutput eventOutput = new EventOutput();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final LinkedBlockingQueue<SmsMessage> queue = new LinkedBlockingQueue<>();
                BroadcastReceiver receiver = null;
                try {
                    receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Bundle intentExtras = intent.getExtras();
                            if (intentExtras != null) {
                                Object[] sms = (Object[]) intentExtras.get("pdus");

                                for (int i = 0; i < sms.length; ++i) {
                                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                                    queue.add(smsMessage);
                                }
                            }
                        }
                    };
                    deskDroidService.registerReceiver(receiver,
                            new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
                    while (!eventOutput.isClosed()) {
                        SmsMessage message = queue.poll(5, TimeUnit.SECONDS);
                        while (message != null) {
                            JSONObject json = new JSONObject()
                                    .put("participant", message.getDisplayOriginatingAddress())
                                    .put("body", message.getDisplayMessageBody());
                            final Message data = new Message(message.getDisplayOriginatingAddress(),
                                    message.getDisplayMessageBody());
                            eventOutput.write(new OutboundEvent.Builder()
                                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                                    .name("new-message")
                                    .data(data)
                                    .build());
                            message = queue.poll();
                        }
                    }
                } catch (JSONException | InterruptedException | IOException e) {
                    System.out.println("Disconnected.");
                } finally {
                    try {
                        if (receiver != null) {
                            deskDroidService.unregisterReceiver(receiver);
                        }
                        eventOutput.close();
                    } catch (IOException ioClose) {
                        ioClose.printStackTrace();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        return eventOutput;
    }

    @POST
    @Path("authorize")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getAuthorization(String clientCode) {
        if (clientCode != null && clientCode.equals(deskDroidService.code)) {

            String jwt = Jwts.builder()
                    .setSubject("DeskDroid")
                    .signWith(SignatureAlgorithm.HS512,
                            KeyGenerator.getKey(deskDroidService.getApplicationContext()))
                    .compact();
            LocalBroadcastManager.getInstance(deskDroidService.getApplicationContext())
                    .sendBroadcast(new Intent(DeskDroidService.CODE_ACCEPTED));

            return Response.ok(jwt).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("participants/{address}")
    @Secure
    public Response getParticipant(@PathParam("address") String address) {
        Participant p = null;
        try {
            p = getContactsDetails(address);
        } catch (IOException e) {
            return Response.serverError().build();
        }
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(p).build();
        }
    }

    protected Participant getContactsDetails(String address) throws IOException {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));

        Cursor phones = deskDroidService.getApplicationContext().getContentResolver().query(contactUri,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        "number",
                        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI},
                null, null, null);
        Participant participant = new Participant();
        if (phones.moveToNext()) {
            participant.setName(phones.getString(phones.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            participant.setPhoneNumber(phones.getString(phones.getColumnIndex("number")));

            String image_uri = phones.getString(phones.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
            if (image_uri != null) {
                try (InputStream input = deskDroidService.getApplicationContext().
                        getContentResolver().openInputStream(Uri.parse(image_uri));
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    int nRead;
                    byte[] data = new byte[16384];

                    while ((nRead = input.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    participant.setThumbnail(Base64.encodeToString(buffer.toByteArray(), Base64.DEFAULT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        phones.close();
        return participant;
    }

    @NonNull
    private List<Message> getSmsMessages(int threadId) {

        List<Message> messages = new ArrayList<>();
        Cursor cur = null;
        try {
            cur = deskDroidService.getApplicationContext().getContentResolver().query(
                    Telephony.Sms.CONTENT_URI,
                    null, "thread_id = ?", new String[]{Integer.toString(threadId)},
                    "date DESC");

            while (cur.moveToNext()) {
                Message message = new Message();
                message.setId(cur.getInt(cur.getColumnIndex("_id")));
                message.setThreadId(cur.getInt(cur.getColumnIndex("thread_id")));
                message.setAddress(cur.getString(cur.getColumnIndex("address")));
                message.setBody(cur.getString(cur.getColumnIndexOrThrow("body")));
                message.setDate(new Date(cur.getLong(cur.getColumnIndexOrThrow("date"))));
                message.setMine(cur.getInt(cur.getColumnIndex("type")) == Telephony.Sms.MESSAGE_TYPE_SENT);
                messages.add(message);
            }
            Collections.sort(messages, new MessageComparator());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
        return messages;
    }

    @NonNull
    private Conversation buildConversation(Cursor cur) {
        final Conversation conv = new Conversation();
        final int threadId = cur.getInt(cur.getColumnIndex("thread_id"));
        conv.setThreadId(threadId);
        conv.setMessageCount(cur.getInt(cur.getColumnIndex("msg_count")));
        conv.setSnippet(cur.getString(cur.getColumnIndex("snippet")));
        final List<Message> messages = getSmsMessages(conv.getThreadId());
        for (Message message : messages) {
//            if (!message.isMine()) {
                // We're dealing with SMS, so there will only be one participant
                conv.setParticipant(message.getAddress());
//                break;
//            }
        }
        conv.setMessages(messages);
        return conv;
    }
}
