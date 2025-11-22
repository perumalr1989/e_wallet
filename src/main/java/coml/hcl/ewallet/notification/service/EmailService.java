package coml.hcl.ewallet.notification.service;

import coml.hcl.ewallet.notification.model.EmailNotification;
import coml.hcl.ewallet.notification.model.TransactionNotification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateService templateService;

    public void sendCustomerTransactionEmail(String emailSubject, TransactionNotification transaction) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", transaction.getCustomerName());
            variables.put("transactionId", transaction.getTransactionId());
            variables.put("amount", transaction.getAmount());
            variables.put("status", transaction.getStatus());
            variables.put("merchantName", transaction.getMerchantName());
            variables.put("merchantId", transaction.getMerchantId());
            variables.put("productMap", transaction.getProductMap());
            variables.put("customerId", transaction.getCustomerId());
            variables.put("customerMobileNumber", transaction.getCustomerMobileNumber());
            variables.put("reasonForFailure", transaction.getReasonForFailure());

            String htmlContent = templateService.processTemplate("customer-email", variables);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(transaction.getCustomerEmailId());
            helper.setSubject(emailSubject);
            helper.setText(htmlContent, true);

//            mailSender.send(mimeMessage);
            log.info("Customer transaction email sent successfully for transaction: {}", transaction.getTransactionId());
        } catch (MessagingException e) {
            log.error("Failed to send customer transaction email for transaction: {}", transaction.getTransactionId(), e);
            throw new RuntimeException("Failed to send customer transaction email", e);
        }
    }

    public void sendMerchantTransactionEmail(String emailSubject, TransactionNotification transaction) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("merchantName", transaction.getMerchantName());
            variables.put("transactionId", transaction.getTransactionId());
            variables.put("amount", transaction.getAmount());
            variables.put("status", transaction.getStatus());
            variables.put("customerName", transaction.getCustomerName());
            variables.put("customerId", transaction.getCustomerId());
            variables.put("customerMobileNumber", transaction.getCustomerMobileNumber());
            variables.put("productMap", transaction.getProductMap());
            variables.put("merchantId", transaction.getMerchantId());
            variables.put("reasonForFailure", transaction.getReasonForFailure());

            String htmlContent = templateService.processTemplate("merchant-email", variables);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(transaction.getMerchantEmail());
            helper.setSubject(emailSubject);
            helper.setText(htmlContent, true);

//            mailSender.send(mimeMessage);
            log.info("Merchant transaction email sent successfully for transaction: {}", transaction.getTransactionId());
        } catch (MessagingException e) {
            log.error("Failed to send merchant transaction email for transaction: {}", transaction.getTransactionId(), e);
            throw new RuntimeException("Failed to send merchant transaction email", e);
        }
    }
}