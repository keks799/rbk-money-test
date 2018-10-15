Report
==============
conformity list:
<#if conformityList?has_content>
    ${conformityList?join(", ")}
<#else>
    None
</#if>

discrepancy list:
<#if discrepancyList?has_content>
    ${discrepancyList?join(", ")}
<#--<#list discrepancyList as discrepancy>-->
<#--${discrepancy.transactionId}-->
<#--</#list>-->
<#else>
    No inconsistencies
</#if>

not found list:
<#if notFoundList?has_content>
    ${notFoundList?join(", ")}
<#--<#list notFoundList as notFound>-->
<#--${notFound.transactionId}-->
<#--</#list>-->
<#else>
    No missing
</#if>
