package coml.hcl.ewallet.notification.repository;

import coml.hcl.ewallet.notification.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTransactionId(Long transactionId);

    List<AuditLog> findByEvent(String event);

    List<AuditLog> findByTransactionIdAndEvent(Long transactionId, String event);
}
