package coml.hcl.ewallet.notification.controller;

import coml.hcl.ewallet.notification.model.TransactionNotification;
import coml.hcl.ewallet.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@Slf4j
public class NotificationController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    String notif = """
    {
      "customerId": "CUST123456",
      "customerName": "John Doe",
      "customerMobileNumber": 919876543210,
      "customerEmailId": "john.doe@example.com",
      "amount": 2499.99,
      "transactionId": "TXN20241122001234",
      "merchantId": "MERCH98765",
      "merchantName": "Electronics Store",
      "productMap": {
        "PROD001": 2,
        "PROD002": 1,
        "PROD003": 3
      },
      "merchantEmail": "merchant@electronics-store.com",
      "merchantMobileNumber": 919123456789,
      "status": "SUCCESS",
      "reasonForFailure": null
    }
    """;

    String audit = """
            {
              "transactionId": 20241122001237,
              "request": "{\\"topic\\":\\"ewallet_notification\\",\\"payload\\":\\"{\\\\\\"customerId\\\\\\":\\\\\\"CUST001\\\\\\",\\\\\\"amount\\\\\\":5000}\\"}",
              "response": "{\\"status\\":\\"PROCESSED\\",\\"timestamp\\":\\"2024-11-22T10:35:00Z\\"}",
              "event": "NOTIFICATION_RECEIVED"
            }
            """;

    @GetMapping("/notif")
    public ResponseEntity<String> sendCustomerNotification() {
        kafkaTemplate.send("ewallet_notification", notif);
        return ResponseEntity.ok("Sent");
    }

    @GetMapping("/audit")
    public ResponseEntity<String> sendAudit() {
        kafkaTemplate.send("ewallet_audit", audit);
        return ResponseEntity.ok("Sent");
    }

}
