package money.rbk.test.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import money.rbk.test.entity.OuterDataEntity;
import money.rbk.test.model.OuterData;
import money.rbk.test.repository.OuterDataRepository;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xtext.money.rbkdsl.RbkMoneyDslStandaloneSetup;
import org.xtext.money.rbkdsl.RbkMoneyDslStandaloneSetupGenerated;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static money.rbk.test.entity.OuterDataEntity.Status.NEW;

/**
 * Controller which prepares input data from outer resource
 */

@Service
@Slf4j
public class OuterDataTransactionProcessingService { // todo unification?

    @Autowired
    private OuterDataRepository outerDataRepository;

    public static void main(String[] args) throws IOException {
        RbkMoneyDslStandaloneSetup.doSetup();
        RbkMoneyDslStandaloneSetupGenerated sg = new RbkMoneyDslStandaloneSetup();
        final Injector injector = sg.createInjector();

        String src = "PID;PAMOUNT;PDATA;\n" +
                "123;94.7;20160101120000;\n" +
                "124;150.75;20160101120001;\n" +
                "125;1020.2;20160101120002;\n" +
                "126;15.5;20160101120003;\n" +
                "127;120.74;20160101120004;\n" +
                "TOTAL;5;";

        XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(src.getBytes());
        XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
//        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createURI("1.rbkdsl"));
        resource.load(inputStream, Collections.EMPTY_MAP);


        EObject eObject = resource.getContents().get(0);

        Resource resource1 = resourceSet.createResource(URI.createURI("2.xmi"));
        resource1.getContents().addAll(resource.getContents());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resource1.save(byteArrayOutputStream, Collections.EMPTY_MAP);


        System.out.println(new String(byteArrayOutputStream.toByteArray()));

    }

    public void processOuterInformation(String outerDataContent) {
        List<OuterData> objectsFromCsv = getObjectsFromCsv(outerDataContent);
        storeData(objectsFromCsv);
    }

    private void storeData(List<OuterData> outerData) { // todo think: new record will create even if it already is
//        outerDataRepository.saveAll(outerData);
        for (OuterData outerDatum : outerData) {
            outerDataRepository.save(OuterDataEntity.createFromPlainData(outerDatum.getId(), outerDatum.getAmount(),
                    outerDatum.getData(), NEW)
            );
        }
    }

    /**
     * Processing csv data
     *
     * @param outerDataContent csv data from outer source
     */
    private List<OuterData> getObjectsFromCsv(String outerDataContent) {


        try {
            CsvSchema bootstrapSchema = CsvSchema.builder()
                    .setColumnSeparator(';')
                    .setStrictHeaders(true)
                    .setUseHeader(true)
                    .build();
            CsvMapper mapper = new CsvMapper();
            MappingIterator<OuterData> readValues =
                    mapper.readerFor(OuterData.class).with(bootstrapSchema).readValues(outerDataContent);
            return readValues.readAll();
        } catch (Exception e) {
            log.error("Error occurred while loading object list", e);
            return Collections.emptyList();
        }
    }

    public List<OuterDataEntity> findOuterDataEntitiesWithStatus(OuterDataEntity.Status status) {
        return outerDataRepository.findByStatus(status);
    }
}
