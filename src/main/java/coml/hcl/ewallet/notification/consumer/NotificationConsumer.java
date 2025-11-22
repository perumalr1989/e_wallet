package coml.hcl.ewallet.notification.consumer;

import coml.hcl.ewallet.notification.model.TransactionNotification;
import coml.hcl.ewallet.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @Value("${spring.kafka.notification.email.customer.subject}")
    private String customerEmailSubject;

    @Value("${spring.kafka.notification.email.merchant.subject}")
    private String merchantEmailSubject;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.notification.consumer.topic}",
            groupId = "${spring.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        TransactionNotification transactionNotification = objectMapper.readValue(payload, TransactionNotification.class);
        log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Email notification details - To: {}, Subject: {}",
                transactionNotification.getCustomerEmailId(), customerEmailSubject);

        try {
            emailService.sendCustomerTransactionEmail(customerEmailSubject, transactionNotification);
            log.info("Email notification sent to customer");

            emailService.sendMerchantTransactionEmail(merchantEmailSubject, transactionNotification);
            log.info("Email notification sent to merchant");
        } catch (Exception e) {
            log.error("Error processing email notification", e);
        }
    }

}
