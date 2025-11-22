package coml.hcl.ewallet.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {
    private Long transactionId;
    private String request;
    private String response;
    private String event;
}
