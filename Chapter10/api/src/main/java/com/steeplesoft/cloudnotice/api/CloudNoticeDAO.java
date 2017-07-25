package com.steeplesoft.cloudnotice.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import java.util.List;
import java.util.UUID;

public class CloudNoticeDAO {
    public static final String TABLE_NAME = "recipients";

protected final AmazonDynamoDB ddb;
protected final DynamoDBMapper mapper;

    public CloudNoticeDAO(boolean local) {
        ddb = local ? DynamoDBEmbedded.create().amazonDynamoDB()
                : AmazonDynamoDBClientBuilder.defaultClient();
        verifyTables();
        mapper = new DynamoDBMapper(ddb);
    }

    public List<Recipient> getRecipients() {
        return mapper.scan(Recipient.class, new DynamoDBScanExpression());
    }

    public void saveRecipient(Recipient recip) {
        if (recip.getId() == null) {
            recip.setId(UUID.randomUUID().toString());
        }
        mapper.save(recip);
    }

    public void deleteRecipient(Recipient recip) {
        mapper.delete(recip);
    }

    private void verifyTables() {
        try {
            ddb.describeTable(TABLE_NAME);
        } catch (ResourceNotFoundException rnfe) {
            createRecipientTable();
        }
    }

    private void createRecipientTable() {
        CreateTableRequest request
                = new CreateTableRequest()
                        .withTableName(TABLE_NAME)
                        .withAttributeDefinitions(
                                new AttributeDefinition("_id", ScalarAttributeType.S)
                        )
                        .withKeySchema(
                                new KeySchemaElement("_id", KeyType.HASH)
                        )
                        .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

        ddb.createTable(request);
        try {
            TableUtils.waitUntilActive(ddb, TABLE_NAME);
        } catch (InterruptedException  e) {
            throw new RuntimeException(e);
        }
    }
    
    public void shutdown() {
        ddb.shutdown();
    }
}
