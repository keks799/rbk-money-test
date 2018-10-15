package money.rbk.test.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Prepare output data service
 */

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

    public void report(ReconciliationResult result) { // todo add reset

        try {
            Template template = freemarkerConfig.getTemplate(templateFileName);
            try (Writer fileWriter = new FileWriter(new File(String.format(reportDirFilename, sdf.format(new Date()))))) {
                template.process(result, fileWriter);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
