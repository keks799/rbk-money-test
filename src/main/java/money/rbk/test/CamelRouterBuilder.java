package money.rbk.test;

import money.rbk.test.controller.OuterDataTransactionController;
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

    @Value("${camel.outer.data.input}") // camel string to read csv file
    private String outerDataInput;

    @Override
    public void configure() {

        // process data from .csv, mail, etc.
        from(outerDataInput).bean(outerDataTransactionController, "processOuterInformation");

        restConfiguration();

    }
}
