package money.rbk.test.controller;

import lombok.extern.slf4j.Slf4j;
import money.rbk.test.model.ReconciliationResult;
import money.rbk.test.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    public void report(ReconciliationResult result) {
        log.info(result.toString());
        if (!(result.getConformityList().isEmpty() && result.getDiscrepancyList().isEmpty() && result.getNotFoundList().isEmpty())) { // todo add to props setup of it
            notificationService.report(result);
        }
    }
}
