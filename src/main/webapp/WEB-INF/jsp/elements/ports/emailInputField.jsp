<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<div class="form_field ${entryRow.index != 0 && portRow.index == 0 ? "contains_repeat_control" : ""}">

	<c:if test="${entryRow.index != 0 && portRow.index == 0}">
		<button name="_elements[${elementRow.index}].entries[${entryRow.index}]" value="1" class="remove"></button>
	</c:if>

	<label><c:if test="${not empty port.usage}"><a title="${port.usage}">(i)</a>&nbsp;</c:if><c:out value="${port.label}"/></label>
	
	<form:input path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}"/>
	<c:if test="${port.required}"><span class="red">*</span></c:if>
	<form:errors cssClass="red" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" />
	<br/>
	
</div>