package money.rbk.test;

import money.rbk.test.controller.OuterDataTransactionController;
import money.rbk.test.controller.ReconciliationController;
import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Building routes for camel
 */

@Component
public class CamelRouterBuilder extends RouteBuilder {

    @BeanInject
    private OuterDataTransactionController outerDataTransactionController;

    @BeanInject
    private ReconciliationController reconciliationController;

    @Value("${camel.outer.data.input}") // camel string to read csv file
    private String outerDataInput;

    /**
     * Camel configuration.
     * <p>
     * I can handle a lot here: ActiveMQ, file, mail, rest, Twitter, Telegram etc. (http://camel.apache.org/components.html)
     * <p>
     * Below i have file implementation and commented rest as an example
     */
    @Override
    public void configure() {

        // process data from .csv, mail, etc.
        from(outerDataInput).bean(outerDataTransactionController, "processOuterInformation");

        // Using rest to post transaction info as an example (plain text format is ok)

//        restConfiguration()
//                .component("restlet")
//                .host("localhost").port("8082");
//
//        rest("/v1")
//                .post("/reconcile")
//                .to("bean:outerDataTransactionController?method=processOuterInformationAsRest")
//                .post("/report")
//                .to("bean:reconciliationController?method=reconciliationOnDemand");

    }
}
