package com.steeplesoft.monumentum.model;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author jason
 */
public class User {
    private String id;
    private String oauthId;
    private String email;
    private String name;
    private String picture;

    public User(String oauthId, String email, String name, String picture) {
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
    
    public User(Document doc) {
        this.id = doc.get("_id", ObjectId.class).toHexString();
        this.oauthId = doc.getString("oauthId");
        this.email = doc.getString("email");
        this.name = doc.getString("name");
        this.picture = doc.getString("picture");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public Document toDocument() {
        Document doc = new Document()
                .append("id", getId())
                .append("oauthId", getOauthId())
                .append("email", getEmail())
                .append("name", getName())
                .append("picture", getPicture());
        return doc;
    }
}
/*
{
    "id": "111057881135904815971",
    "email": "flakfizer@gmail.com",
    "verified_email": true,
    "name": "Jason Lee",
    "given_name": "Jason",
    "family_name": "Lee",
    "link": "https://plus.google.com/+JasonLee1",
    "picture": "https://lh6.googleusercontent.com/-Sh39Iq9V4PA/AAAAAAAAAAI/AAAAAAAAHsc/2n4mA39AGKo/photo.jpg",
    "gender": "male",
    "locale": "en"
}

*/