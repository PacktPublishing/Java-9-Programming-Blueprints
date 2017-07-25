package com.steeplesoft.cloudnotice.api;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SesClient {

    private static final int MAX_GROUP_SIZE = 50;
    private final AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                .defaultClient();

    public void sendEmails(List<String> emailAddresses,
            String from,
            String subject,
            String emailBody) {
        

        Message message = new Message()
                .withSubject(new Content().withData(subject))
                .withBody(new Body().withText(new Content().withData(emailBody)));

        getChunkedEmailList(emailAddresses)
                .forEach(group
                        -> client.sendEmail(new SendEmailRequest()
                        .withSource(from)
                        .withDestination(new Destination().withBccAddresses(group))
                        .withMessage(message))
                );

        shutdown();
    }

    public void shutdown() {
        client.shutdown();
    }

    private List<List<String>> getChunkedEmailList(List<String> emailAddresses) {
        final int numGroups = (int) Math.round(emailAddresses.size() /
                (MAX_GROUP_SIZE * 1.0) + 0.5);
        return IntStream.range(0, numGroups)
                .mapToObj(group -> emailAddresses.subList(MAX_GROUP_SIZE * group,
                Math.min(MAX_GROUP_SIZE * group + MAX_GROUP_SIZE,
                        emailAddresses.size())))
                .collect(Collectors.toList());
    }
}
