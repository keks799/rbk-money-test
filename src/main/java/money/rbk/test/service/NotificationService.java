package money.rbk.test.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.log4j.Log4j;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Prepare output data service
 */

@Log4j
@Service
public class NotificationService {

    @Autowired
    private Configuration freemarkerConfig;

    @Value("${report.postfix.date.pattern}") // postfix for report file to prevent erase
    private String datePattern;
    private final SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.isEmpty(datePattern) ? "HHmmss" : datePattern);
    @Value("${report.template.filename}") // report template file name
    private String templateFileName;
    @Value("${report.dir.filename}") // report directory and file name format
    private String reportDirFilename;

    public void report(ReconciliationResult result) {

        try {
            Template template = freemarkerConfig.getTemplate(templateFileName);
            try (Writer fileWriter = new FileWriter(new File(String.format(reportDirFilename, sdf.format(new Date()))))) {
                template.process(result, fileWriter);
            }
        } catch (IOException | TemplateException e) {
            log.error("Error has been occurred while writing report", e);
            e.printStackTrace();
        }
    }

    public OutputStream reportAsStream(ReconciliationResult result) throws IOException, TemplateException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Template template = freemarkerConfig.getTemplate(templateFileName);
        try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream)) {
            template.process(result, streamWriter);
            return outputStream;
        }
    }
}
