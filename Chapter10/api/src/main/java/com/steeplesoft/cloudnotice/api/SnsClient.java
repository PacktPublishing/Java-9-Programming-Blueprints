package com.steeplesoft.cloudnotice.api;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicResult;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SnsClient {

    private final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    public List<String> getTopics() {
        return snsClient.listTopics().getTopics().stream()
                .map(t -> t.getTopicArn())
                .collect(Collectors.toList());
    }

    public void sendTextMessages(List<String> phoneNumbers, String message) {
        String arn = createTopic(UUID.randomUUID().toString());
        phoneNumbers.forEach(phoneNumber -> subscribeToTopic(arn, "sms", phoneNumber));
        sendMessage(arn, message);
        deleteTopic(arn);
    }

    public void sendMessage(String topic, String message) {
        snsClient.publish(topic, message);
    }

    public void shutdown() {
        snsClient.shutdown();
    }

    private String createTopic(String arn) {
        CreateTopicResult result
                = snsClient.createTopic(new CreateTopicRequest(arn));
        return result.getTopicArn();
    }

    private DeleteTopicResult deleteTopic(String arn) {
        return snsClient.deleteTopic(arn);
    }

    private SubscribeResult subscribeToTopic(String arn,
            String protocol, String endpoint) {
        SubscribeRequest subscribe = new SubscribeRequest(arn, protocol,
                endpoint);
        return snsClient.subscribe(subscribe);
    }
}
