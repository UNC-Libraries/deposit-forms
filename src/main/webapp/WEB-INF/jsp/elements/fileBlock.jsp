<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="crosswalk.*"%>

<div class="file_block">

	<c:if test="${not empty element.formElement.name}">
	<br/><h3><c:out value="${element.formElement.name}"/></h3>
	</c:if>
	<c:if test="${not empty element.formElement.description}">
	<p><c:out value="${element.formElement.description}"/></p>
	</c:if>
	
	<c:forEach items="${element.entries}" var="entry" varStatus="entryRow">
		
		<div class="entry_block">
		
			<div class="form_field file_field ${not empty entry.file ? "filled" : ""}">
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
		
		</div>
		
	</c:forEach>
	
</div>
