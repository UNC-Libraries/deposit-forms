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
<html>
<head>
  <title>
    <p>An unrecoverable error occurred on ${form.title} at ${receivedDate?datetime}.</p>
  </title>
</head>
<body>
  <img style="float: right;" alt="UNC Libraries logo" src="${siteUrl}/static/images/email_logo.png"/>
  <h3>${siteName}</h3>
  <p>An unrecoverable error occurred on ${form.title} at ${receivedDate?datetime}:</p>
  <pre>
${result.getResponseBody()!"no response body"}
  </pre>
  <p>The following information was entered on <a href="${siteUrl}/forms/${formId}.form">${form.title}</a>:</p>
  <ul>
    <li>Depositor Username: ${form.currentUser!""}</li>
    <li>Depositor Email: ${depositorEmail!"not available"}</li> 
<#list deposit.elements as element>
  	<#list element.entries as entry>
  		<#if entry.fields??>
			<#list entry.fields as field>
				<#if field.class.name == "cdr.forms.DateDepositField">
	<li>${field.formInputField.label}: ${field.value?datetime!""}</li>
				<#else>
	<li>${field.formInputField.label}: ${field.value!""}</li>
				</#if>
			</#list>
		</#if>
    </#list>
</#list>
  </ul>
</body>
</html>