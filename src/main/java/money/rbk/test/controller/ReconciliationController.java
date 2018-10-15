package money.rbk.test.controller;

import money.rbk.test.model.ReconciliationResult;
import money.rbk.test.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * To reconcile
 */
@Controller
public class ReconciliationController {

    @Autowired
    private ReconciliationService reconciliationService;

    public ReconciliationResult reconciliation() {
        return reconciliationService.reconciliation();
    }
}
