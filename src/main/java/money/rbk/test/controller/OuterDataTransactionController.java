package money.rbk.test.controller;

import lombok.extern.slf4j.Slf4j;
import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.OuterDataEntity.Status;
import money.rbk.test.service.OuterDataTransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller to work with outer transactions information (e.g. transactions from file list)
 * <p>
 * I am storing everything i read from outer source.
 * Put NEW status as soon i read it.
 * Put NEED_CHECK status as soon as i see transaction again.
 * Put DONE as soon as i done reconciliation
 */
@Slf4j
@Controller
public class OuterDataTransactionController {

    @Autowired
    private OuterDataTransactionProcessingService outerDataTransactionProcessingService;

    /**
     * camel use it automatically as soon as it need
     * @param outerDataContent - data from outer source (file) as a string
     */
    public void processOuterInformation(String outerDataContent) {
        outerDataTransactionProcessingService.processOuterInformation(outerDataContent);
    }

    /**
     * Looking for entries from outer source, which are not done yet
     * @param statuses
     * @return
     */
    public List<OuterDataEntity> findAllOuterDataEntitiesWithStatuses(Status... statuses) {
        return outerDataTransactionProcessingService.findAllOuterDataEntitiesWithStatuses(statuses);
    }

    public void save(OuterDataEntity newOuterDataEntry) {
        outerDataTransactionProcessingService.save(newOuterDataEntry);
    }

    /**
     * Receive transaction information using camel with REST for an example
     * @param exchange
     */
//    public void processOuterInformationAsRest(Exchange exchange) {
//        outerDataTransactionProcessingService.processOuterInformation(exchange);
//    }
}
