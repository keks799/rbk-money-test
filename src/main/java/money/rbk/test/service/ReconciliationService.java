package money.rbk.test.service;

import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import money.rbk.test.controller.NotificationController;
import money.rbk.test.controller.OuterDataTransactionController;
import money.rbk.test.controller.TransactionsController;
import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.TransactionEntity;
import money.rbk.test.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
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
    @Autowired
    private NotificationController notificationController;

    public ReconciliationResult reconciliation() {
        ReconciliationResult reconciliationResult = new ReconciliationResult();

        List<OuterDataEntity> newOuterDataEntriesList = outerDataTransactionController.findAllOuterDataEntitiesWithStatuses(NEW, NEED_CHECK); // looking for undone transaction records from outer source
        TransactionEntity storedTransaction;

        for (OuterDataEntity newOuterDataEntry : newOuterDataEntriesList) {
            try {
                storedTransaction = transactionsController.getWithId(newOuterDataEntry.getTransactionId()); // fetch transaction from transaction table
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

    /**
     * Prepare reconciliation as Output stream to use in camel
     *
     * @return
     */
    public OutputStream reconciliationToOutputStream() {
        ReconciliationResult reconciliationResult = reconciliation();
        try {
            return notificationController.reportAsStream(reconciliationResult);
        } catch (IOException | TemplateException e) {
            log.error("Error occurred while creating output stream from report", e);
        }
        return null;
    }

    /**
     * Receive report using camel with REST for an example
     * @param exchange
     */
//    public void reconciliationOnDemand(Exchange exchange) {
//        final ReconciliationResult reconciliation = reconciliation();
//        try (OutputStream outputStream = notificationController.reportAsStream(reconciliation)) {
//            exchange.getOut().setBody(outputStream);
//        } catch (IOException | TemplateException e) {
//            log.error("Error while prepare report as stream", e);
//            exchange.getOut().setFault(true);
//        }
//    }
}
