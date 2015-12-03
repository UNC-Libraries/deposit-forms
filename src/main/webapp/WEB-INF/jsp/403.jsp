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

<h2><c:out value="${form.title}"/></h2>
<p><c:out value="${form.description}" escapeXml="false"/></p>
<% if(request.getRemoteUser() != null) { %>
<h3>Not Authorized to Deposit</h3>
<p><c:out value="${message}"/></p>
<% } else { %>
<h3>Log In is Required</h3>
<p><c:out value="${message}"/></p>
<p id="login_block"><a href="/Shibboleth.sso/Login?target=%2fforms%2f${formId}.form">Log In</a></p>
<% } %>
</div>
</div>
<%@ include file="footer.html"%>
</div>
</div>
</div>
</body>
</html>
