<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>
<div class="form_field width_${port.width.name}">
	<label><c:if test="${not empty port.usage}"><a title="${port.usage}">(i)</a>&nbsp;</c:if><c:out value="${port.label}"/></label>
	<c:if test="${port.width.name == 'FullLine' && port.type.name != 'MultipleLines'}">
		<br/>
	</c:if>
	<c:choose>
		<c:when test="${port.type.name == 'MultipleLines'}">
			<div class="multi_notes">
				<c:if test="${port.required}"><span class="red">*</span></c:if>
				<form:errors cssClass="red" path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" />
			</div>
			<c:if test="${port.width.name == 'FullLine'}">
				<br/>
			</c:if>													
			<c:choose>
				<c:when test="${port.maxCharacters != null}">
					<form:textarea path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}"/>
				</c:when>
				<c:otherwise>
					<form:textarea path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" placeholder="${port.usage}"/>
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
									<form:input path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}" cssClass="cv_${port.vocabularyURL.hashCode()}"/>
								</c:when>
								<c:otherwise>
									<form:input path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}" cssClass="cv_${port.vocabularyURL.hashCode()}_elements${elementRow.index}.ports${portRow.index}.enteredValue"/>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<form:select path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}">
								<form:options items="${vocabURLMap[port.vocabularyURL.hashCode().toString()]}"/>
								<form:options items="${port.validValues}"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</c:when>											
				<c:otherwise>
					<c:choose>
						<c:when test="${port.validValues == null || port.validValues.size() == 0}">
							<form:input path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}" placeholder="${port.usage}" maxlength="${port.maxCharacters}"/>
						</c:when>
						<c:otherwise>
							<form:select path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" title="${port.usage}">
								<form:options items="${port.validValues}"/>
							</form:select>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			<c:if test="${port.required}"><span class="red">*</span></c:if>
			<form:errors cssClass="red" path="form.elements[${elementRow.index}].ports[${portRow.index}].enteredValue" />
			<br/>
		</c:otherwise>
	</c:choose>
</div>