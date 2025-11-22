package coml.hcl.ewallet.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionNotification {
    private String customerId;
    private String customerName;
    private Long customerMobileNumber;
    private String customerEmailId;
    private Float amount;
    private String transactionId;
    private String merchantId;
    private String merchantName;
    private Map<String, Integer> productMap;
    private String merchantEmail;
    private Long merchantMobileNumber;
    private String status;
    private String reasonForFailure;
}
