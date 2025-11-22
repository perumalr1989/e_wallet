package coml.hcl.ewallet.notification.controller;

import coml.hcl.ewallet.notification.entity.AuditLog;
import coml.hcl.ewallet.notification.model.AuditLogRequest;
import coml.hcl.ewallet.notification.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Slf4j
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(@RequestBody AuditLogRequest request) {
        try {
            log.info("Received audit log creation request for transaction: {}", request.getTransactionId());
            AuditLog auditLog = auditLogService.createAuditLog(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(auditLog);
        } catch (Exception e) {
            log.error("Error creating audit log for transaction: {}", request.getTransactionId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        try {
            log.info("Received request to fetch all audit logs");
            List<AuditLog> auditLogs = auditLogService.getAllAuditLogs();
            return ResponseEntity.ok(auditLogs);
        } catch (Exception e) {
            log.error("Error fetching all audit logs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        try {
            log.info("Received request to fetch audit log by ID: {}", id);
            return auditLogService.getAuditLogById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching audit log by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTransactionId(@PathVariable Long transactionId) {
        try {
            log.info("Received request to fetch audit logs by transaction ID: {}", transactionId);
            List<AuditLog> auditLogs = auditLogService.getAuditLogsByTransactionId(transactionId);
            return ResponseEntity.ok(auditLogs);
        } catch (Exception e) {
            log.error("Error fetching audit logs by transaction ID: {}", transactionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/event/{event}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEvent(@PathVariable String event) {
        try {
            log.info("Received request to fetch audit logs by event: {}", event);
            List<AuditLog> auditLogs = auditLogService.getAuditLogsByEvent(event);
            return ResponseEntity.ok(auditLogs);
        } catch (Exception e) {
            log.error("Error fetching audit logs by event: {}", event, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/transaction/{transactionId}/event/{event}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTransactionIdAndEvent(
            @PathVariable Long transactionId,
            @PathVariable String event) {
        try {
            log.info("Received request to fetch audit logs by transaction ID: {} and event: {}", transactionId, event);
            List<AuditLog> auditLogs = auditLogService.getAuditLogsByTransactionIdAndEvent(transactionId, event);
            return ResponseEntity.ok(auditLogs);
        } catch (Exception e) {
            log.error("Error fetching audit logs by transaction ID: {} and event: {}", transactionId, event, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuditLog(@PathVariable Long id) {
        try {
            log.info("Received request to delete audit log with ID: {}", id);
            auditLogService.deleteAuditLog(id);
            return ResponseEntity.ok("Audit log deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting audit log with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete audit log: " + e.getMessage());
        }
    }
}
