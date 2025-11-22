package coml.hcl.ewallet.notification.consumer;

import coml.hcl.ewallet.notification.entity.AuditLog;
import coml.hcl.ewallet.notification.model.AuditLogRequest;
import coml.hcl.ewallet.notification.model.TransactionNotification;
import coml.hcl.ewallet.notification.service.AuditLogService;
import coml.hcl.ewallet.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class AuditConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogService auditLogService;

    @KafkaListener(
            topics = "${spring.kafka.audit.consumer.topic}",
            groupId = "${spring.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        AuditLogRequest request = objectMapper.readValue(payload, AuditLogRequest.class);
        log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);

        try {
            log.info("Received audit log creation request for transaction: {}", request.getTransactionId());
            auditLogService.createAuditLog(request);
            log.info("Audit information captured successfully");
        } catch (Exception e) {
            log.error("Error creating audit log for transaction: {}", request.getTransactionId(), e);
            log.info("An error has occurred while capturing an audit information");
        }
    }

}
