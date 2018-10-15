package money.rbk.test.controller;

import money.rbk.test.entity.TransactionEntity;
import money.rbk.test.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller to work with transactions already stored in db (transaction table. Not outer source transaction records)
 */
@Controller
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    public TransactionEntity getWithTransactionId(Long id) {
        return transactionsService.getWithTransactionId(id);
    }
}
