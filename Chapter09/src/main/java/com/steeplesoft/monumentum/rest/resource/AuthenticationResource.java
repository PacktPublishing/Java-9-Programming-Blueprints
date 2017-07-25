package com.steeplesoft.monumentum.rest.resource;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.steeplesoft.monumentum.model.User;
import com.steeplesoft.monumentum.mongo.Collection;
import com.steeplesoft.monumentum.security.KeyGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import org.bson.Document;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author jason
 */
@Path("auth")
public class AuthenticationResource {

    private final String clientId;
    private final String clientSecret;
    private final GoogleAuthorizationCodeFlow flow;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    @Context
    private UriInfo uriInfo;
    @Context
    private HttpServletRequest req;
    @Inject
    @Collection("users")
    private MongoCollection<Document> collection;
    @Inject
    private KeyGenerator keyGenerator;

    public AuthenticationResource() {
        clientId = System.getProperty("client_id");
        clientSecret = System.getProperty("client_secret");
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                new JacksonFactory(), clientId, clientSecret, SCOPES).build();
    }
//
//    @PostConstruct
//    public void postConstruct() {
//        collection = database.getCollection("users");
//    }

    @GET
    @Path("url")
    public String getAuthorizationUrl() {
        return flow.newAuthorizationUrl()
                .setRedirectUri(getCallbackUri()).build();
    }

    @GET
    @Path("callback")
    public Response handleCallback(@QueryParam("code") @NotNull String code) throws IOException {
        User user = getUserInfoJson(code);
        saveUserInformation(user);
        final String jwt = createToken(user.getEmail());
        return Response.seeOther(
                uriInfo.getBaseUriBuilder()
                        .path("../loginsuccess.html")
                        .queryParam("Bearer", jwt)
                        .build())
                .build();
    }

    private void saveUserInformation(User user) {
        Document doc = collection.find(new BasicDBObject("email", user.getEmail())).first();
        if (doc == null) {
            collection.insertOne(user.toDocument());
        }
    }

    private User getUserInfoJson(final String authCode) throws IOException {
        try {
            final GoogleTokenResponse response = flow.newTokenRequest(authCode)
                    .setRedirectUri(getCallbackUri())
                    .execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequest request = HTTP_TRANSPORT.createRequestFactory(credential)
                    .buildGetRequest(new GenericUrl(USER_INFO_URL));
            request.getHeaders().setContentType("application/json");
            final JSONObject identity = 
                    new JSONObject(request.execute().parseAsString());
            return new User(
                    identity.getString("id"),
                    identity.getString("email"),
                    identity.getString("name"),
                    identity.getString("picture"));
        } catch (JSONException ex) {
            Logger.getLogger(AuthenticationResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String getCallbackUri() throws UriBuilderException, IllegalArgumentException {
        return uriInfo.getBaseUriBuilder()
                .path("auth")
                .path("callback")
                .build()
                .toASCIIString();
    }

    private String createToken(String login) {
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(12L)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, keyGenerator.getKey())
                .compact();
        return jwtToken;
    }
}
