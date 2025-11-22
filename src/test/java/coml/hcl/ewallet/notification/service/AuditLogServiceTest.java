package coml.hcl.ewallet.notification.service;

import coml.hcl.ewallet.notification.entity.AuditLog;
import coml.hcl.ewallet.notification.model.AuditLogRequest;
import coml.hcl.ewallet.notification.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLogRequest auditLogRequest;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLogRequest = new AuditLogRequest();
        auditLogRequest.setTransactionId(123456L);
        auditLogRequest.setRequest("{\"amount\":1000,\"customerId\":\"CUST001\"}");
        auditLogRequest.setResponse("{\"status\":\"SUCCESS\",\"transactionId\":\"TXN001\"}");
        auditLogRequest.setEvent("TRANSACTION_COMPLETED");

        auditLog = new AuditLog();
        auditLog.setId(1L);
        auditLog.setTransactionId(123456L);
        auditLog.setRequest("{\"amount\":1000,\"customerId\":\"CUST001\"}");
        auditLog.setResponse("{\"status\":\"SUCCESS\",\"transactionId\":\"TXN001\"}");
        auditLog.setEvent("TRANSACTION_COMPLETED");
    }

    @Test
    void testCreateAuditLog_Success() {
        // Arrange
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // Act
        AuditLog result = auditLogService.createAuditLog(auditLogRequest);

        // Assert
        assertNotNull(result);
        assertEquals(123456L, result.getTransactionId());
        assertEquals("TRANSACTION_COMPLETED", result.getEvent());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testCreateAuditLog_VerifySavedData() {
        // Arrange
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // Act
        auditLogService.createAuditLog(auditLogRequest);

        // Assert
        verify(auditLogRepository).save(argThat(savedAuditLog -> {
            assertEquals(123456L, savedAuditLog.getTransactionId());
            assertEquals("{\"amount\":1000,\"customerId\":\"CUST001\"}", savedAuditLog.getRequest());
            assertEquals("{\"status\":\"SUCCESS\",\"transactionId\":\"TXN001\"}", savedAuditLog.getResponse());
            assertEquals("TRANSACTION_COMPLETED", savedAuditLog.getEvent());
            return true;
        }));
    }

    @Test
    void testGetAllAuditLogs() {
        // Arrange
        List<AuditLog> auditLogs = Arrays.asList(auditLog, new AuditLog());
        when(auditLogRepository.findAll()).thenReturn(auditLogs);

        // Act
        List<AuditLog> result = auditLogService.getAllAuditLogs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogRepository, times(1)).findAll();
    }

    @Test
    void testGetAuditLogById_Found() {
        // Arrange
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));

        // Act
        Optional<AuditLog> result = auditLogService.getAuditLogById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("TRANSACTION_COMPLETED", result.get().getEvent());
        verify(auditLogRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAuditLogById_NotFound() {
        // Arrange
        when(auditLogRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<AuditLog> result = auditLogService.getAuditLogById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(auditLogRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAuditLogsByTransactionId() {
        // Arrange
        List<AuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByTransactionId(123456L)).thenReturn(auditLogs);

        // Act
        List<AuditLog> result = auditLogService.getAuditLogsByTransactionId(123456L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123456L, result.get(0).getTransactionId());
        verify(auditLogRepository, times(1)).findByTransactionId(123456L);
    }

    @Test
    void testGetAuditLogsByEvent() {
        // Arrange
        List<AuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByEvent("TRANSACTION_COMPLETED")).thenReturn(auditLogs);

        // Act
        List<AuditLog> result = auditLogService.getAuditLogsByEvent("TRANSACTION_COMPLETED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TRANSACTION_COMPLETED", result.get(0).getEvent());
        verify(auditLogRepository, times(1)).findByEvent("TRANSACTION_COMPLETED");
    }

    @Test
    void testGetAuditLogsByTransactionIdAndEvent() {
        // Arrange
        List<AuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByTransactionIdAndEvent(123456L, "TRANSACTION_COMPLETED"))
                .thenReturn(auditLogs);

        // Act
        List<AuditLog> result = auditLogService.getAuditLogsByTransactionIdAndEvent(
                123456L, "TRANSACTION_COMPLETED");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123456L, result.get(0).getTransactionId());
        assertEquals("TRANSACTION_COMPLETED", result.get(0).getEvent());
        verify(auditLogRepository, times(1))
                .findByTransactionIdAndEvent(123456L, "TRANSACTION_COMPLETED");
    }

    @Test
    void testDeleteAuditLog() {
        // Arrange
        doNothing().when(auditLogRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> auditLogService.deleteAuditLog(1L));

        // Assert
        verify(auditLogRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreateAuditLog_WithNullFailureReason() {
        // Arrange
        auditLogRequest.setEvent("EMAIL_SENT");
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        // Act
        AuditLog result = auditLogService.createAuditLog(auditLogRequest);

        // Assert
        assertNotNull(result);
        verify(auditLogRepository).save(argThat(savedAuditLog -> {
            assertEquals("EMAIL_SENT", savedAuditLog.getEvent());
            return true;
        }));
    }

    @Test
    void testGetAuditLogsByTransactionId_EmptyList() {
        // Arrange
        when(auditLogRepository.findByTransactionId(999L)).thenReturn(Arrays.asList());

        // Act
        List<AuditLog> result = auditLogService.getAuditLogsByTransactionId(999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(auditLogRepository, times(1)).findByTransactionId(999L);
    }
}
