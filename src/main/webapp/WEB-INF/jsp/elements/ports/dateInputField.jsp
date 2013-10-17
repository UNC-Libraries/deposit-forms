<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<div class="form_field">
	<label><c:if test="${not empty port.usage}"><a title="${port.usage}">(i)</a>&nbsp;</c:if><c:out value="${port.label}"/></label>
	<c:choose>
		<c:when test="${port.datePrecision.name == 'month'}">
			<form:input cssClass="monthpicker" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" />
		</c:when>
		<c:when test="${port.datePrecision.name == 'day'}">
			<form:input cssClass="datepicker" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" />
		</c:when>
		<c:when test="${port.datePrecision.name == 'year'}">
			<%-- Display a select of years, from 190 in the past to 10 in the future --%>
			<form:select path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}">
				<%-- Select the year from the form bean or the current year if none is provided --%>
				<c:choose>
					<c:when test="${deposit.elements[elementRow.index].entries[entryRow.index].fields[portRow.index].value != null}">
						<c:set var="selectedIndex" value="${currentYear + 10 - (deposit.elements[elementRow.index].entries[entryRow.index].fields[portRow.index].value.year + 1900)}"/>
					</c:when>
					<c:otherwise>
						<c:set var="selectedIndex" value="${10}"/>
					</c:otherwise>
				</c:choose>
				<c:forEach var="i" begin="0" end="200">
					<c:choose>
						<c:when test="${i == selectedIndex}">
							<form:option value="${currentYear - i + 10}" selected="true"/>
						</c:when>
						<c:otherwise>
							<form:option value="${currentYear - i + 10}"/>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</form:select>
		</c:when>
	</c:choose>
	<c:if test="${port.required}"><span class="red">*</span></c:if>
	<form:errors cssClass="red" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" />
	<br/>
</div>