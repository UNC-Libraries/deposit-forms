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
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<%@ page import="java.util.*"%>
<%@ page import="crosswalk.impl.*"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.io.*"%>
<%@ page import="cdr.forms.*"%>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" type="text/css" href="/static/css/cdr_access.css" />

<!--[if IE 8]>
	<link rel="stylesheet" type="text/css" href="/static/css/cdrui_styles_ie8.css" />
<![endif]-->
<meta name="description" content="Carolina Digital Repository Deposit Form" />
<meta name="keywords" content="Carolina Digital Repository, deposit" />
<meta name="robots" content="index, nofollow" />
<link rel="shortcut icon" href="/static/images/favicon.ico" type="image/x-icon" />
<title>Forms</title>
</head>
<body>
<div id="pagewrap">
	<div id="pagewrap_inside">
		<%@ include file="header.html"%>
		<div id="content">
			<div class="content-wrap">
				<div class="contentarea">

<h2>Forms</h2>

<c:forEach items="${ids}" var="id">
	<c:set var="form" value="${forms[id]}"/>
	<h3><a href="${id}.form"><c:out value="${form.title}"/></a></h3>
	<p><c:out value="${form.description}"/></p>
</c:forEach>

</div>
</div>
<%@ include file="footer.html"%>
</div>
</div>
</div>
</body>
</html>

