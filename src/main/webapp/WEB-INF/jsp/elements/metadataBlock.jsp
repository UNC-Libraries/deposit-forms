<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<div class="metadata_block">
	<c:if test="${not empty element.name}">
		<h3><c:out value="${element.name}"/></h3>
	</c:if>
	<c:if test="${not empty element.description}">
		<p><c:out value="${element.description}"/></p>
	</c:if>
	<div class="indented_block">
		<c:forEach items="${deposit.form.elements[elementRow.index].ports}" var="port" varStatus="portRow">
			<spring:bind path="deposit.form.elements[${elementRow.index}].ports[${portRow.index}]" ignoreNestedPath="true">
				<% if (status.getValue() instanceof DateInputField) { %>
					<%@include file="ports/dateInputField.jsp" %>
				<% } else if (status.getValue() instanceof TextInputField) { %>
					<%@include file="ports/textInputField.jsp" %>
				<% } else { %>
					<form:input path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" />
				<% } %>
			</spring:bind>
		</c:forEach>
	</div>
</div>