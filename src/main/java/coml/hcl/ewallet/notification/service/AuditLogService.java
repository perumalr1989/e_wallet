package coml.hcl.ewallet.notification.service;

import coml.hcl.ewallet.notification.entity.AuditLog;
import coml.hcl.ewallet.notification.model.AuditLogRequest;
import coml.hcl.ewallet.notification.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog createAuditLog(AuditLogRequest request) {
        log.info("Creating audit log for transaction: {}, event: {}", request.getTransactionId(), request.getEvent());

        AuditLog auditLog = new AuditLog();
        auditLog.setTransactionId(request.getTransactionId());
        auditLog.setRequest(request.getRequest());
        auditLog.setResponse(request.getResponse());
        auditLog.setEvent(request.getEvent());

        AuditLog savedLog = auditLogRepository.save(auditLog);
        log.info("Audit log created successfully with ID: {}", savedLog.getId());

        return savedLog;
    }

    public List<AuditLog> getAllAuditLogs() {
        log.info("Fetching all audit logs");
        return auditLogRepository.findAll();
    }

    public Optional<AuditLog> getAuditLogById(Long id) {
        log.info("Fetching audit log by ID: {}", id);
        return auditLogRepository.findById(id);
    }

    public List<AuditLog> getAuditLogsByTransactionId(Long transactionId) {
        log.info("Fetching audit logs for transaction ID: {}", transactionId);
        return auditLogRepository.findByTransactionId(transactionId);
    }

    public List<AuditLog> getAuditLogsByEvent(String event) {
        log.info("Fetching audit logs for event: {}", event);
        return auditLogRepository.findByEvent(event);
    }

    public List<AuditLog> getAuditLogsByTransactionIdAndEvent(Long transactionId, String event) {
        log.info("Fetching audit logs for transaction ID: {} and event: {}", transactionId, event);
        return auditLogRepository.findByTransactionIdAndEvent(transactionId, event);
    }

    @Transactional
    public void deleteAuditLog(Long id) {
        log.info("Deleting audit log with ID: {}", id);
        auditLogRepository.deleteById(id);
        log.info("Audit log deleted successfully");
    }
}
