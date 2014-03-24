<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<c:set var="isFileBlock" value="true"/>

<div class="file_block">

	<c:if test="${not empty element.formElement.name}">
	<br/><h3><c:out value="${element.formElement.name}"/></h3>
	</c:if>
	<c:if test="${not empty element.formElement.description}">
	<p><c:out value="${element.formElement.description}"/></p>
	</c:if>
	
	<c:forEach items="${element.entries}" var="entry" varStatus="entryRow">
		
		<div class="entry_block">
		
			<div class="form_field file_field ${entryRow.index != 0 ? "contains_repeat_control" : ""} ${not empty entry.file ? "filled" : ""}">
      	<c:if test="${entryRow.index != 0}">
      		<button name="_elements[${elementRow.index}].entries[${entryRow.index}]" value="1" class="removeEntry"></button>
      	</c:if>
        
				<label><c:if test="${not empty element.formElement.usage}"><a title="${element.formElement.usage}">(i)</a></c:if>&nbsp;</label>
				
				<input name="elements[${elementRow.index}].entries[${entryRow.index}].file" type="file" class="file" size="40"/>
				
				<c:if test="${not empty entry.file}">
					<span class="description">
						<b><c:out value="${entry.file.filename}"/></b>
						<input type="submit" name="_elements[${elementRow.index}].entries[${entryRow.index}].file" value="Remove file" class="remove"/>
					</span>
				</c:if>
				
				<c:if test="${element.formElement.required}"><span class="red">*</span></c:if>
				<form:errors cssClass="red" path="elements[${elementRow.index}].entries[${entryRow.index}].file" />
			</div>
		
			<c:forEach items="${element.formElement.ports}" var="port" varStatus="portRow">
			
				<spring:bind path="deposit.elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" ignoreNestedPath="true">
					<% if (pageContext.getAttribute("port") instanceof DateInputField) { %>
						<%@include file="ports/dateInputField.jsp" %>
					<% } else if (pageContext.getAttribute("port") instanceof TextInputField) { %>
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
