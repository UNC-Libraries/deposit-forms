<%--

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

--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="/static/css/cdr_access.css" />
<link rel="stylesheet" type="text/css" href="css/cdr_forms.css" />

<link type="text/css" href="/cdradmin/css/jquery/ui/jquery-ui.css" rel="stylesheet" />

<script type="text/javascript" src="/cdradmin/js/jquery/jquery.min.js"></script> 
<script type="text/javascript" src="/cdradmin/js/jquery/ui/jquery-ui.min.js"></script> 

<!--[if IE 8]>
	<link rel="stylesheet" type="text/css" href="/static/css/cdrui_styles_ie8.css" />
<![endif]-->
<meta name="description" content="Carolina Digital Repository Deposit Form" />
<meta name="keywords" content="Carolina Digital Repository, deposit" />
<meta name="robots" content="index, nofollow" />
<link rel="shortcut icon" href="/static/images/favicon.ico" type="image/x-icon" />
<title><c:out value="${form.title}"/></title>
</head>
<body>
	<div id="pagewrap">
		<div id="pagewrap_inside">
			<%@ include file="header.html"%>
			<div id="content">
				<div class="content-wrap">
					<div class="contentarea">

<h2>Deposit successful</h2>

<p>Your deposit for <b><c:out value="${deposit.form.title}"/></b> was successful and is being processed. In the next hour, you should receive a deposit receipt email confirming your submission.</p>

<p>
If you have any questions, please contact
<c:choose>
	<c:when test="${deposit.form.contactEmail != null && deposit.form.contactName != null}">
		<c:out value="${deposit.form.contactName}"/> at <a href="mailto:${deposit.form.contactEmail}"><c:out value="${deposit.form.contactEmail}"/></a>.
	</c:when>
	<c:when test="${deposit.form.contactEmail != null}">
		<a href="mailto:${deposit.form.contactEmail}"><c:out value="${deposit.form.contactEmail}"/></a>.
	</c:when>
	<c:otherwise>
		<a href="mailto:${administratorEmail}"><c:out value="${administratorEmail}"/></a>.
	</c:otherwise>
</c:choose>
</p>

<p>Help us improve the CDR. <a href="https://unc.az1.qualtrics.com/SE/?SID=SV_8HrpGlGgdAdieWx">Take a short survey</a>.</p>

<p><a href="https://cdr.lib.unc.edu/">Browse the CDR</a></p>

<c:if test="${deposit.form.promptForMultipleSubmissions}">
	<p><a href="<%= request.getAttribute("formId") %>.form">Click here to submit another deposit.</a></p>
</c:if>
	
</div>
</div>
<%@ include file="footer.html"%>
</div>
</div>
</div>
</body>
</html>
