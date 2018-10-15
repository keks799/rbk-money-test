package money.rbk.test.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Prepare output data controller
 */

@Service
public class NotificationService {

    @Autowired
    private Configuration freemarkerConfig;

    private String datePattern = "HHmmss"; // todo move to props
    private final SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

    public void report(ReconciliationResult result) {

        try {
            Template template = freemarkerConfig.getTemplate("report.ftl"); // todo move to props
            try (Writer fileWriter = new FileWriter(new File(String.format("reportDir/report - %s.txt", sdf.format(new Date()))))) {
                template.process(result, fileWriter);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
