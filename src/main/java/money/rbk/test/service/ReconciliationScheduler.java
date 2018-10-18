package money.rbk.test.service;

import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduling reconciliation task
 */
@Service
public class ReconciliationScheduler {

    @Autowired
    private ReconciliationService reconciliationService;
    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRateString = "${reconciliation.period.of.time}")
    public void periodicalReconciliation() {
        ReconciliationResult reconciliationResult = reconciliationService.reconciliation();
        notificationService.report(reconciliationResult);
    }
}
