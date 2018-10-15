package money.rbk.test.controller;

import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.entity.OuterDataEntity.Status;
import money.rbk.test.service.OuterDataTransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OuterDataTransactionController {

    @Autowired
    private OuterDataTransactionProcessingService outerDataTransactionProcessingService;

    public void processOuterInformation(String outerDataContent) {
        outerDataTransactionProcessingService.processOuterInformation(outerDataContent);
    }

    public List<OuterDataEntity> findOuterDataEntitiesWithStatus(Status status) {
        return outerDataTransactionProcessingService.findOuterDataEntitiesWithStatus(status);
    }

    public void save(OuterDataEntity newOuterDataEntry) {
        outerDataTransactionProcessingService.save(newOuterDataEntry);
    }
}
