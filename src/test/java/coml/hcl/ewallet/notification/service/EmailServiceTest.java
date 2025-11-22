package coml.hcl.ewallet.notification.service;

import coml.hcl.ewallet.notification.model.TransactionNotification;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateService templateService;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private TransactionNotification transactionNotification;

    @BeforeEach
    void setUp() {
        transactionNotification = new TransactionNotification();
        transactionNotification.setCustomerId("CUST123456");
        transactionNotification.setCustomerName("John Doe");
        transactionNotification.setCustomerMobileNumber(919876543210L);
        transactionNotification.setCustomerEmailId("john.doe@example.com");
        transactionNotification.setAmount(2499.99f);
        transactionNotification.setTransactionId("TXN20241122001234");
        transactionNotification.setMerchantId("MERCH98765");
        transactionNotification.setMerchantName("Electronics Store");

        Map<String, Integer> productMap = new HashMap<>();
        productMap.put("PROD001", 2);
        productMap.put("PROD002", 1);
        transactionNotification.setProductMap(productMap);

        transactionNotification.setMerchantEmail("merchant@electronics-store.com");
        transactionNotification.setMerchantMobileNumber(919123456789L);
        transactionNotification.setStatus("SUCCESS");
        transactionNotification.setReasonForFailure(null);
    }

    @Test
    void testSendCustomerTransactionEmail_Success() {
        // Arrange
        String emailSubject = "Transaction on your e-Wallet";
        String htmlContent = "<html><body>Test Email</body></html>";

        when(templateService.processTemplate(eq("customer-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendCustomerTransactionEmail(emailSubject, transactionNotification));

        // Assert
        verify(templateService, times(1)).processTemplate(eq("customer-email"), anyMap());
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendCustomerTransactionEmail_TemplateServiceCalled() {
        // Arrange
        String emailSubject = "Transaction on your e-Wallet";
        String htmlContent = "<html><body>Test Email</body></html>";

        when(templateService.processTemplate(eq("customer-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendCustomerTransactionEmail(emailSubject, transactionNotification);

        // Assert
        verify(templateService).processTemplate(eq("customer-email"), argThat(variables -> {
            assertEquals("John Doe", variables.get("customerName"));
            assertEquals("TXN20241122001234", variables.get("transactionId"));
            assertEquals(2499.99f, variables.get("amount"));
            assertEquals("SUCCESS", variables.get("status"));
            assertEquals("Electronics Store", variables.get("merchantName"));
            return true;
        }));
    }

    @Test
    void testSendCustomerTransactionEmail_WithFailedTransaction() {
        // Arrange
        String emailSubject = "Transaction on your e-Wallet";
        String htmlContent = "<html><body>Test Email</body></html>";

        transactionNotification.setStatus("FAILED");
        transactionNotification.setReasonForFailure("Insufficient balance");

        when(templateService.processTemplate(eq("customer-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendCustomerTransactionEmail(emailSubject, transactionNotification);

        // Assert
        verify(templateService).processTemplate(eq("customer-email"), argThat(variables -> {
            assertEquals("FAILED", variables.get("status"));
            assertEquals("Insufficient balance", variables.get("reasonForFailure"));
            return true;
        }));
    }

    @Test
    void testSendMerchantTransactionEmail_Success() {
        // Arrange
        String emailSubject = "Merchant Transaction";
        String htmlContent = "<html><body>Merchant Email</body></html>";

        when(templateService.processTemplate(eq("merchant-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        assertDoesNotThrow(() -> emailService.sendMerchantTransactionEmail(emailSubject, transactionNotification));

        // Assert
        verify(templateService, times(1)).processTemplate(eq("merchant-email"), anyMap());
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendMerchantTransactionEmail_TemplateServiceCalled() {
        // Arrange
        String emailSubject = "Merchant Transaction";
        String htmlContent = "<html><body>Merchant Email</body></html>";

        when(templateService.processTemplate(eq("merchant-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendMerchantTransactionEmail(emailSubject, transactionNotification);

        // Assert
        verify(templateService).processTemplate(eq("merchant-email"), argThat(variables -> {
            assertEquals("Electronics Store", variables.get("merchantName"));
            assertEquals("TXN20241122001234", variables.get("transactionId"));
            assertEquals(2499.99f, variables.get("amount"));
            assertEquals("John Doe", variables.get("customerName"));
            assertEquals("CUST123456", variables.get("customerId"));
            return true;
        }));
    }

    @Test
    void testSendMerchantTransactionEmail_WithProductMap() {
        // Arrange
        String emailSubject = "Merchant Transaction";
        String htmlContent = "<html><body>Merchant Email</body></html>";

        when(templateService.processTemplate(eq("merchant-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendMerchantTransactionEmail(emailSubject, transactionNotification);

        // Assert
        verify(templateService).processTemplate(eq("merchant-email"), argThat(variables -> {
            @SuppressWarnings("unchecked")
            Map<String, Integer> productMap = (Map<String, Integer>) variables.get("productMap");
            assertNotNull(productMap);
            assertEquals(2, productMap.size());
            assertEquals(2, productMap.get("PROD001"));
            assertEquals(1, productMap.get("PROD002"));
            return true;
        }));
    }

    @Test
    void testSendCustomerTransactionEmail_ThrowsException() {
        // Arrange
        String emailSubject = "Transaction on your e-Wallet";

        when(templateService.processTemplate(eq("customer-email"), anyMap()))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendCustomerTransactionEmail(emailSubject, transactionNotification);
        });

        assertEquals("Failed to send customer transaction email", exception.getMessage());
        verify(templateService, times(1)).processTemplate(eq("customer-email"), anyMap());
    }

    @Test
    void testSendMerchantTransactionEmail_ThrowsException() {
        // Arrange
        String emailSubject = "Merchant Transaction";

        when(templateService.processTemplate(eq("merchant-email"), anyMap()))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendMerchantTransactionEmail(emailSubject, transactionNotification);
        });

        assertEquals("Failed to send merchant transaction email", exception.getMessage());
        verify(templateService, times(1)).processTemplate(eq("merchant-email"), anyMap());
    }

    @Test
    void testSendCustomerTransactionEmail_VerifyAllVariables() {
        // Arrange
        String emailSubject = "Transaction on your e-Wallet";
        String htmlContent = "<html><body>Test Email</body></html>";

        when(templateService.processTemplate(eq("customer-email"), anyMap())).thenReturn(htmlContent);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendCustomerTransactionEmail(emailSubject, transactionNotification);

        // Assert
        verify(templateService).processTemplate(eq("customer-email"), argThat(variables ->
            variables.get("customerMobileNumber").equals(919876543210L) &&
            variables.get("customerId").equals("CUST123456") &&
            variables.containsKey("productMap")
        ));
    }
}
