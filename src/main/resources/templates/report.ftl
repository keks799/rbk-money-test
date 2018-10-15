Report
==============
conformity list:
<#if conformityList?has_content>
    <#list conformityList as conformity>${conformity.transactionId}<#sep>, </#list>
<#else>
    None
</#if>

discrepancy list:
<#if discrepancyList?has_content>
    <#list discrepancyList as discrepancy>${discrepancy.transactionId}<#sep>, </#list>
<#else>
    No inconsistencies
</#if>

not found list:
<#if notFoundList?has_content>
    <#list notFoundList as notFound>${notFound.transactionId}<#sep>, </#list>
<#else>
    No missing
</#if>
