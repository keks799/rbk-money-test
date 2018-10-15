package money.rbk.test.controller;

import lombok.extern.slf4j.Slf4j;
import money.rbk.test.model.ReconciliationResult;
import money.rbk.test.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * Report/notification controller
 */
@Controller
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Value("${construct.empty.report.too}") // turn on/off create report file, if nothing is changed
    private boolean isTurnOn;

    public void report(ReconciliationResult result) {
        log.info(result.toString());
        if (!result.getConformityList().isEmpty() || !result.getDiscrepancyList().isEmpty() || !result.getNotFoundList().isEmpty() || isTurnOn) {
            notificationService.report(result);
        }
    }
}
