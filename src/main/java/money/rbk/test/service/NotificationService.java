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
    @Value("${report.dir.filename}") // report directory and file name format
    private String reportDirFilename;

    public OutputStream reportAsStream(ReconciliationResult result) throws IOException, TemplateException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Template template = freemarkerConfig.getTemplate(templateFileName);
        try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream)) {
            template.process(result, streamWriter);
            return outputStream;
        }
    }
}
