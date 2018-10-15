package money.rbk.test.service;

import lombok.extern.slf4j.Slf4j;
import money.rbk.test.controller.OuterDataTransactionController;
import money.rbk.test.controller.TransactionsController;
import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.TransactionEntity;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import static money.rbk.test.entity.OuterDataEntity.Status.*;

/**
 * Main processing service
 */

@Service
@Slf4j
public class ReconciliationService {

    @Autowired
    private OuterDataTransactionController outerDataTransactionController;
    @Autowired
    private TransactionsController transactionsController;

    public ReconciliationResult reconciliation() {
        ReconciliationResult reconciliationResult = new ReconciliationResult();

        List<OuterDataEntity> newOuterDataEntriesList = outerDataTransactionController.findAllOuterDataEntitiesWithStatuses(NEW, NEED_CHECK); // looking for undone transaction records from outer source
        TransactionEntity storedTransaction;

        for (OuterDataEntity newOuterDataEntry : newOuterDataEntriesList) {
            try {
                storedTransaction = transactionsController.getWithTransactionId(newOuterDataEntry.getTransactionId()); // fetch transaction from transaction table
            } catch (IncorrectResultSizeDataAccessException e) {
                log.error("Not unique transaction record in database. Transaction id is: %d", newOuterDataEntry.getId()); // not sure if this will happen
//                reconciliationResult.getNotUniqueDbRecordsIdList().add(newOuterDataEntry.getTransactionId());
                continue;
            }
            if (storedTransaction == null) {
                // mark as no in db
                reconciliationResult.getNotFoundList().add(newOuterDataEntry);
            } else if (!newOuterDataEntry.getAmount().equals(storedTransaction.getAmount())) {
                // mark as wrong amount
                reconciliationResult.getDiscrepancyList().add(newOuterDataEntry);
            } else {
                // mark as correct one
                reconciliationResult.getConformityList().add(newOuterDataEntry);
            }
            markAsProcessed(newOuterDataEntry);
        }

        return reconciliationResult;
    }

    private void markAsProcessed(OuterDataEntity newOuterDataEntry) {
        newOuterDataEntry.setStatus(DONE);
        outerDataTransactionController.save(newOuterDataEntry);
    }
}
