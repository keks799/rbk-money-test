package money.rbk.test.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
    @Value("${construct.empty.report.too}") // turn on/off create report file, if nothing is changed
    private boolean isTurnOn;

    public void report(ReconciliationResult result) {
        log.info(result.toString());
        if (!result.getConformityList().isEmpty() || !result.getDiscrepancyList().isEmpty() || !result.getNotFoundList().isEmpty() || isTurnOn) {
            reportProcessing(result);
        }
    }

    private void reportProcessing(ReconciliationResult result) {

        try {
            Template template = freemarkerConfig.getTemplate(templateFileName);
            try (Writer fileWriter = new FileWriter(new File(String.format(reportDirFilename, sdf.format(new Date()))))) {
                template.process(result, fileWriter);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    public OutputStream reportAsStream(ReconciliationResult result) throws IOException, TemplateException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Template template = freemarkerConfig.getTemplate(templateFileName);
            try (OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream)) {
                template.process(result, streamWriter);
                return outputStream;
            }
        } catch (IOException | TemplateException e) {
            throw e;
        }
    }
}
