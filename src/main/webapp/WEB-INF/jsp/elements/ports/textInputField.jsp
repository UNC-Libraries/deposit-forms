<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<div class="form_field width_${port.width.name} ${entryRow.index != 0 && isFileBlock == false && portRow.index == 0 ? "contains_repeat_control" : ""}">

	<c:if test="${entryRow.index != 0 && isFileBlock == false && portRow.index == 0}">
		<button name="_elements[${elementRow.index}].entries[${entryRow.index}]" value="1" class="removeEntry"></button>
	</c:if>

	<label><c:if test="${not empty port.usage}"><a title="${port.usage}">(i)</a>&nbsp;</c:if><c:out value="${port.label}"/></label>
	<c:if test="${port.width.name == 'FullLine' && port.type.name != 'MultipleLines'}">
		<br/>
	</c:if>
	
	<c:choose>
		<c:when test="${port.type.name == 'MultipleLines'}">
			<div class="multi_notes">
				<c:if test="${port.required}"><span class="red">*</span></c:if>
				<form:errors cssClass="red" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" />
			</div>
			<c:if test="${port.width.name == 'FullLine'}">
				<br/>
			</c:if>													
			<c:choose>
				<c:when test="${port.maxCharacters != null}">
					<form:textarea path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}"/>
				</c:when>
				<c:otherwise>
					<form:textarea path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}"/>
				</c:otherwise>
			</c:choose>
			<br/>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${port.vocabularyURL != null}">
					<c:choose>
						<c:when test="${port.allowFreeText}">
							<c:choose>
								<c:when test="${port.validValues == null || port.validValues.size() == 0}">
									<form:input path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}" cssClass="cv_${port.vocabularyURL.hashCode()}"/>
								</c:when>
								<c:otherwise>
									<form:input path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}" cssClass="cv_${port.vocabularyURL.hashCode()}_elements${elementRow.index}.ports${portRow.index}.enteredValue"/>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<form:select path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}">
								<form:options items="${vocabURLMap[port.vocabularyURL.hashCode().toString()]}"/>
								<form:options items="${port.validValues}"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</c:when>											
				<c:otherwise>
					<c:choose>
						<c:when test="${port.validValues == null || port.validValues.size() == 0}">
							<form:input path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}"/>
						</c:when>
						<c:otherwise>
							<form:select path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" title="${port.usage}">
								<form:options items="${port.validValues}"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			<c:if test="${port.required}"><span class="red">*</span></c:if>
			<form:errors cssClass="red" path="elements[${elementRow.index}].entries[${entryRow.index}].fields[${portRow.index}].value" />
			<br/>
		</c:otherwise>
	</c:choose>
</div>