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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static money.rbk.test.entity.OuterDataEntity.Status.NEED_CHECK;
import static money.rbk.test.entity.OuterDataEntity.Status.NEW;

/**
 * Service which prepares input data from outer resource
 */

@Service
@Slf4j
public class OuterDataTransactionProcessingService {

    @Autowired
    private OuterDataRepository outerDataRepository;

    /**
     * Camel use it automatically as soon as it need.
     * Check outer information before store it
     *
     * @param outerDataContent - data from outer source (file) as a string
     */
    public void processOuterInformation(InputStream outerDataContent) {
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

    /**
     * If transaction is already read from outer source
     *
     * @param transactionId
     * @return
     * @throws IncorrectResultSizeDataAccessException - emergency situation. Should not occur
     */
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
                continue; // we log it and skip it
            }
            if (counterpart != null) {                      // if we already have information about this transaction from outer source
                counterpart.setStatus(NEED_CHECK);          // we better check it again. Probably amount has been changed
                counterpart.setAmount(record.getPamount()); // get amount from outer source
                counterpart.setData(record.getPdata().toString());
                outerDataRepository.save(counterpart);
            } else {                                        // if we don't have it, so it's new
                outerDataRepository.save(OuterDataEntity.createFromPlainData(record.getPid().longValue(), record.getPamount(),
                        record.getPdata().toString(), NEW)
                );
            }
        }
    }

    /**
     * Any cases. Should be useful. Present information from outer source in xml format, or any other..
     *
     * @param resourceSet - set of available resources
     * @param resource    - my specified resource, which contains information from outer source
     * @return
     * @throws IOException
     */
    private String getResourceAsXML(XtextResourceSet resourceSet, Resource resource) throws IOException {
        Resource resource1 = resourceSet.createResource(URI.createURI("name.xmi")); // resource for new presentation
        resource1.getContents().addAll(resource.getContents());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resource1.save(byteArrayOutputStream, Collections.EMPTY_MAP);
        return new String(byteArrayOutputStream.toByteArray());
    }

    /**
     * Processing csv data with xtext.
     * I used xtext because it's easily and clearly and more useful than parse it with fasterxml as an example.
     *
     * @param outerDataContent csv data from outer source
     */

    @SuppressWarnings("unchecked")
    private List<TransactionsFile> getObjectsFromCsv(InputStream outerDataContent) {

        RbkMoneyDslStandaloneSetup.doSetup(); // init dsl model. It's standalone because it can work without eclipse and stuff
        RbkMoneyDslStandaloneSetupGenerated sg = new RbkMoneyDslStandaloneSetup();
        final Injector injector = sg.createInjector(); // get injector to get environment members

//        ByteArrayInputStream inputStream = new ByteArrayInputStream(outerDataContent.getBytes());
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);

        Resource resource = resourceSet.createResource(URI.createURI("name.rbkdsl")); // *.rbkdsl my own dsl to read ptxs.csv using format
        try {
            resource.load(outerDataContent, Collections.EMPTY_MAP); // no args
        } catch (IOException e) {
            log.error("Error while reading from resource");
            return Collections.EMPTY_LIST;
        }
        return (List<TransactionsFile>) (EList) resource.getContents(); // i am 100% sure in this cast
    }

    /**
     * Receive transaction information using camel with REST for an example
     *
     * @param exchange
     */
//    public void processOuterInformationAsRest(Exchange exchange) {
//        String content;
//        final Object body = exchange.getIn().getBody();
//        if (body instanceof String) {
//            content = (String) body;
//        } else {
//            StringWriter writer = new StringWriter();
//            try {
//                IOUtils.copy(((InputStream) body), writer, "UTF-8");
//            } catch (IOException e) {
//                log.error("Error while receiving rest body", e);
//            }
//            content = writer.toString();
//        }
//        if (!StringUtils.isEmpty(content)) {
//            processOuterInformation(content);
//            exchange.getOut().setBody("Received. Waiting for reconciliation schedule or calling /report method");
//        } else {
//            exchange.getOut().setFault(true);
//        }
//    }

    // utility methods

    /**
     * Looking for entries from outer source, which are not done yet
     *
     * @param statuses
     * @return
     */
    public List<OuterDataEntity> findAllOuterDataEntitiesWithStatuses(OuterDataEntity.Status... statuses) {
        return outerDataRepository.findAllByStatuses(statuses);
    }

    public void save(OuterDataEntity newOuterDataEntry) {
        outerDataRepository.save(newOuterDataEntry);
    }
}
