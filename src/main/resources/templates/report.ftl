Report
=================
conformity list:
<#list conformityList as conformity>
    ${conformity.transactionId}
</#list>

discrepancy list:
<#list discrepancyList as discrepancy>
    ${discrepancy.transactionId}
</#list>

not found list:
<#list notFoundList as notFound>
    ${notFound.transactionId}
</#list>
