package money.rbk.test;

import money.rbk.test.controller.OuterDataTransactionController;
import money.rbk.test.controller.ReconciliationController;
import money.rbk.test.service.NotificationService;
import money.rbk.test.service.ReconciliationService;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Building routes for camel
 */

@Component
public class CamelRouterBuilder extends RouteBuilder {

    @BeanInject
    private OuterDataTransactionController outerDataTransactionController;

    @BeanInject
    private ReconciliationController reconciliationController;

    @BeanInject
    private ReconciliationService reconciliationService;

    @BeanInject
    private NotificationService notificationService;

    @Value("${camel.outer.data.input}") // camel string to read csv file
    private String outerDataInput;

    @Value("${camel.report.file.mask}") // report directory and file name format
    private String reportFileMask;

    @Value("${camel.report.directory}") // report directory and file name format
    private String reportDirectory;

    @Value("${reconciliation.period.of.time}")
    private Long period;

    @Value("${reconciliation.initial.delay}")
    private Long delay;

    @Value("${report.postfix.date.pattern}") // postfix for report file to prevent erase
    private String datePattern;


    /**
     * Camel configuration.
     * <p>
     * I can handle a lot here: ActiveMQ, file, mail, rest, Twitter, Telegram etc. (http://camel.apache.org/components.html)
     * <p>
     * Below i have file implementation and commented rest as an example
     */
    @Override
    public void configure() {

        datePattern = StringUtils.isEmpty(datePattern) ? "YYYYMMddHHmmss" : datePattern;

        // process data from .csv, mail, etc.
        from(outerDataInput).bean(outerDataTransactionController, "processOuterInformation");

        from(String.format("scheduler://reconciliation?delay=%d&initialDelay=%d", period, delay))
                .to("bean:reconciliationService?method=reconciliationToOutputStream")
                .filter(body().isNotNull())
                .setHeader(Exchange.FILE_NAME, simpleF(reportFileMask, "${date:now:" + datePattern + "}"))
                .to(reportDirectory);

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
