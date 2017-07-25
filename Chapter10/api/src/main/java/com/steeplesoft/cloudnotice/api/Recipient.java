package com.steeplesoft.cloudnotice.api;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = CloudNoticeDAO.TABLE_NAME)
public class Recipient {
    private String id;
    private String type = "SMS";
    private String address = "";

    public Recipient() {
    }
    
    public Recipient(String type, String address) {
        this.type = type;
        this.address = address;
    }

    @DynamoDBHashKey(attributeName = "_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @DynamoDBAttribute(attributeName = "type")
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName="address")
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}
