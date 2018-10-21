package money.rbk.test.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.log4j.Log4j;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Prepare output data service
 */

@Log4j
@Service
public class NotificationService {

    @Autowired
    private Configuration freemarkerConfig;

    @Value("${report.template.filename}") // report template file name
    private String templateFileName;
    @Value("${construct.empty.report.too}") // turn on/off create report file, if nothing is changed
    private boolean isTurnOn;

    /**
     * @return - decision to report or not
     */
    private boolean shouldReport(ReconciliationResult result) {
        if (isTurnOn) {
            return true;
        } else
            return !result.getConformityList().isEmpty() || !result.getDiscrepancyList().isEmpty() || !result.getNotFoundList().isEmpty();
    }


    public OutputStream reportAsStream(ReconciliationResult result) throws IOException, TemplateException {
        if (shouldReport(result)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Template template = freemarkerConfig.getTemplate(templateFileName);
            try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream)) {
                template.process(result, streamWriter);
                return outputStream;
            }
        }
        return null; // to prevent report file creation
    }
}
