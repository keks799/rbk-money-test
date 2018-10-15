package money.rbk.test;

import money.rbk.test.controller.OuterDataTransactionController;
import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Building routes for camel
 */

@Component
public class CamelRouterBuilder extends RouteBuilder {

    private static String OUTER_DATA_INPUT = "file://D:\\code\\IdeaProjects\\rbkMoneyTest\\src\\main\\resources\\reportDir?fileName=ptxs.csv&noop=true&idempotentKey=${file:name}-${file:modified}";    // todo: move to props
    @BeanInject
    private OuterDataTransactionController outerDataTransactionController;

    @Override
    public void configure() {

        // process data from .csv, mail, etc.
        from(OUTER_DATA_INPUT).bean(outerDataTransactionController, "processOuterInformation");

    }
}
