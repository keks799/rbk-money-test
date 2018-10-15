package money.rbk.test.service;

import money.rbk.test.controller.NotificationController;
import money.rbk.test.controller.ReconciliationController;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduling reconciliation task
 */
@Service
public class ReconciliationScheduler {

    private static final long PERIOD = 1L * 60L * 1000L; // todo move to props
    @Autowired
    private ReconciliationController reconciliationController;
    @Autowired
    private NotificationController notificationController;

    @Scheduled(fixedRate = PERIOD)
    public void periodicalReconciliation() {
        ReconciliationResult reconciliationResult = reconciliationController.reconciliation();
        notificationController.report(reconciliationResult);
    }
}
