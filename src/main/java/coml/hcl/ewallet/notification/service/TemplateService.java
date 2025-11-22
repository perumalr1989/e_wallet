package coml.hcl.ewallet.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@Slf4j
public class TemplateService {

    @Autowired
    private TemplateEngine templateEngine;

    public String processTemplate(String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            return templateEngine.process(templateName, context);
        } catch (Exception e) {
            log.error("Error processing template: {}", templateName, e);
            throw new RuntimeException("Failed to process email template", e);
        }
    }
}
