<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="crosswalk.*"%>

<div class="metadata_block">

	<c:if test="${not empty element.formElement.name}">
		<br/><h3><c:out value="${element.formElement.name}"/></h3>
	</c:if>
	<c:if test="${not empty element.formElement.description}">
		<p><c:out value="${element.formElement.description}"/></p>
	</c:if>
	
	<c:forEach items="${element.entries}" var="entry" varStatus="entryRow">
		
		<div class="entry_block">
		
			<c:forEach items="${element.formElement.ports}" var="port" varStatus="portRow">
			
				<spring:bind path="deposit.elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" ignoreNestedPath="true">
					<% if (pageContext.getAttribute("port") instanceof DateInputField) { %>
						<%@include file="ports/dateInputField.jsp" %>
					<% } else if (pageContext.getAttribute("port") instanceof TextInputField) { %>
						<%@include file="ports/textInputField.jsp" %>
					<% } else if (pageContext.getAttribute("port") instanceof EmailInputField) { %>
						<%@include file="ports/textInputField.jsp" %>
					<% } else { %>
						<form:input path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" />
					<% } %>
				</spring:bind>
				
			</c:forEach>
			
		</div>
	
	</c:forEach>
	
	<c:if test="${element.formElement.maxRepeat > 1}">
	
		<div class="add_another_block">
			<div class="form_field width_Normal contains_repeat_control">
				<c:choose>
					<c:when test="${fn:length(element.entries) < element.formElement.maxRepeat}">
						<button name="elements[${elementRow.index}].append" value="1" class="add">Add Another</button>
					</c:when>
					<c:otherwise>
						<button name="elements[${elementRow.index}].append" value="1" class="add" disabled="disabled">Add Another</button>
					</c:otherwise>
				</c:choose>
				<br/>
			</div>
		</div>
	
	</c:if>
	
</div>
