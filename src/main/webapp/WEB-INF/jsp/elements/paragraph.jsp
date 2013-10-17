<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<c:if test="${not empty element.formElement.heading}">
	<br/><h3><c:out value="${element.formElement.heading}"/></h3>
</c:if>
<c:if test="${not empty element.formElement.text}">
	<p><c:out value="${element.formElement.text}"/></p>
</c:if>