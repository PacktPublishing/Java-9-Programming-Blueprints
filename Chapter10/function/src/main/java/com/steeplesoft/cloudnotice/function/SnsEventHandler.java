package com.steeplesoft.cloudnotice.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.steeplesoft.cloudnotice.api.CloudNoticeDAO;
import com.steeplesoft.cloudnotice.api.SnsClient;
import com.steeplesoft.cloudnotice.api.Recipient;
import com.steeplesoft.cloudnotice.api.SesClient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * http://docs.aws.amazon.com/lambda/latest/dg/with-sns.html
 *
 * @author jason
 */
public class SnsEventHandler implements RequestHandler<SNSEvent, Object> {

    @Override
    public Object handleRequest(SNSEvent request, Context context) {
        final LambdaLogger logger = context.getLogger();
        final String message = request.getRecords().get(0).getSNS().getMessage();
        logger.log("Handle message '" + message + "'");

        final List<Recipient> recipients = new CloudNoticeDAO(false)
                .getRecipients();
        final List<String> emailAddresses = recipients.stream()
                .filter(r -> "email".equalsIgnoreCase(r.getType()))
                .map(r -> r.getAddress())
                .collect(Collectors.toList());
        final List<String> phoneNumbers = recipients.stream()
                .filter(r -> "sms".equalsIgnoreCase(r.getType()))
                .map(r -> r.getAddress())
                .collect(Collectors.toList());
        final SesClient sesClient = new SesClient();
        final SnsClient snsClient = new SnsClient();

        sesClient.sendEmails(emailAddresses, "j9bp@steeplesoft.com",
                        "Cloud Notification", message);
        snsClient.sendTextMessages(phoneNumbers, message);
        sesClient.shutdown();
        snsClient.shutdown();

        logger.log("Message handling complete.");

        return null;
    }
}
