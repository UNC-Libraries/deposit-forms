<#--

    Copyright 2010 The University of North Carolina at Chapel Hill

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
An unrecoverable error occurred on ${form.title} at ${receivedDate?datetime}:

response body____________________________
${result.getResponseBody()!"no response body"}
=========================================

The following information was entered on ${siteUrl}/forms/${formId}.form
 * Depositor Username: ${form.currentUser!""}
 * Depositor Email: ${depositorEmail!"not available"}
 <#list deposit.elements as element>
  	<#list element.entries as entry>
  		<#if entry.fields??>
			<#list entry.fields as field>
				<#if field.class.name == "cdr.forms.DateDepositField">
 * ${field.formInputField.label}: ${field.value?datetime!""}
				<#else>
 * ${field.formInputField.label}: ${field.value!""}
				</#if>
			</#list>
		</#if>
    </#list>
 </#list>
