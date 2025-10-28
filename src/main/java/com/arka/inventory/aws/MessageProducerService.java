package com.arka.inventory.aws;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {

    private final SqsTemplate sqsTemplate;
    @Value("${keys.queue-name-inventory-movements}")
    private String QUEUE_NAME;// = "queue-to-ec2-movements";

    // SqsTemplate is auto-configured and injected
    public MessageProducerService(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    public void send(String messagePayload) {
        // Use the send method, specifying the queue name and the message payload
        sqsTemplate.send(QUEUE_NAME, messagePayload);

        // Alternatively, to send a complex object:
        // sqsTemplate.send(QUEUE_NAME, new MyObject("data", 123));

        System.out.println("Message sent to SQS queue: " + messagePayload);

    }
//    public void send(String messagePayload, String infoAttributeValue) {
//
//        sqsTemplate.send(options -> options
//                        // 1. Set the Queue Name (Endpoint)
//                        .queue(QUEUE_NAME)
//
//                        // 2. Set the Message Body (Payload)
//                        .payload(messagePayload)
//
//                        // 3. Set the Message Attribute (Header)
//                        // The key "Info" will become the SQS Message Attribute name
//                        .header("Info", infoAttributeValue)
//
//                // Optional: Set the SQS Message Attribute type (defaults to String)
//                // .header("Info", MessageAttributeValue.builder()
//                //      .stringValue(infoAttributeValue).dataType("String").build())
//        );
//
//        System.out.println("Message sent to SQS queue: " + QUEUE_NAME +
//                " with payload: " + messagePayload +
//                " and attribute Info: " + infoAttributeValue);
//    }

}