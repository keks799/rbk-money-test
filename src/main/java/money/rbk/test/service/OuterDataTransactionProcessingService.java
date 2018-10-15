package money.rbk.test.service;

import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.repository.OuterDataRepository;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.xtext.money.rbkdsl.RbkMoneyDslStandaloneSetup;
import org.xtext.money.rbkdsl.RbkMoneyDslStandaloneSetupGenerated;
import org.xtext.money.rbkdsl.rbkMoneyDsl.TotalLine;
import org.xtext.money.rbkdsl.rbkMoneyDsl.TransactionRecord;
import org.xtext.money.rbkdsl.rbkMoneyDsl.TransactionsFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static money.rbk.test.entity.OuterDataEntity.Status.NEED_CHECK;
import static money.rbk.test.entity.OuterDataEntity.Status.NEW;

/**
 * Controller which prepares input data from outer resource
 */

@Service
@Slf4j
public class OuterDataTransactionProcessingService { // todo unification?

    @Autowired
    private OuterDataRepository outerDataRepository;

    public void processOuterInformation(String outerDataContent) {
        List<TransactionsFile> objectsFromCsv = getObjectsFromCsv(outerDataContent);
        if (objectsFromCsv.isEmpty()) {
            log.error("Nothing is read");
            return;
        }
        final TransactionsFile transactionsFile = objectsFromCsv.get(0); // should be only one file, means only one total line among transaction records
        final TotalLine totalLine = transactionsFile.getTotalLine();
        if (totalLine == null) {
            log.error("Wrong format! No total section");
            return;
        }
        final BigDecimal total = totalLine.getTotal();
        if (total.intValue() != transactionsFile.getTransaction().size()) {
            log.error("Wrong transactions count. Total is: %d actual transaction list size is: %d", total.intValue(), transactionsFile.getTransaction().size());
            return;
        }
        storeData(transactionsFile);
    }

    private OuterDataEntity getCounterpart(Long transactionId) throws IncorrectResultSizeDataAccessException {
        try {
            return outerDataRepository.findByTransactionId(transactionId);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("For outer data transaction id " + transactionId + " there are more than one record.", e);
            throw e;
        }
    }

    private void storeData(TransactionsFile transactionsFile) {
        for (TransactionRecord record : transactionsFile.getTransaction()) {
            final OuterDataEntity counterpart;
            try {
                counterpart = getCounterpart(record.getPid().longValue());
            } catch (IncorrectResultSizeDataAccessException e) {
                continue;
            }
            if (counterpart != null) {
                counterpart.setStatus(NEED_CHECK);
                counterpart.setAmount(record.getPamount());
                counterpart.setData(record.getPdata().toString());
                outerDataRepository.save(counterpart);
            } else {
                outerDataRepository.save(OuterDataEntity.createFromPlainData(record.getPid().longValue(), record.getPamount(),
                        record.getPdata().toString(), NEW)
                );
            }
        }
    }

    private String getResourceAsXML(XtextResourceSet resourceSet, Resource resource) throws IOException {
        Resource resource1 = resourceSet.createResource(URI.createURI("name.xmi"));
        resource1.getContents().addAll(resource.getContents());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resource1.save(byteArrayOutputStream, Collections.EMPTY_MAP);
        return new String(byteArrayOutputStream.toByteArray());
    }

    /**
     * Processing csv data
     *
     * @param outerDataContent csv data from outer source
     */

    @SuppressWarnings("unchecked")
    private List<TransactionsFile> getObjectsFromCsv(String outerDataContent) {

        RbkMoneyDslStandaloneSetup.doSetup(); // init model
        RbkMoneyDslStandaloneSetupGenerated sg = new RbkMoneyDslStandaloneSetup();
        final Injector injector = sg.createInjector(); // get injector to get environment members

//        XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outerDataContent.getBytes());
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

        Resource resource = resourceSet.createResource(URI.createURI("name.rbkdsl"));
        try {
            resource.load(inputStream, Collections.EMPTY_MAP);
        } catch (IOException e) {
            log.error("Error while reading from resource");
            return Collections.EMPTY_LIST;
        }
        return (List<TransactionsFile>) (EList) resource.getContents(); // i am 100% sure in this cast
    }

    public List<OuterDataEntity> findAllOuterDataEntitiesWithStatuses(OuterDataEntity.Status... statuses) {
        return outerDataRepository.findAllByStatuses(statuses);
    }

    public void save(OuterDataEntity newOuterDataEntry) {
        outerDataRepository.save(newOuterDataEntry);
    }
}
