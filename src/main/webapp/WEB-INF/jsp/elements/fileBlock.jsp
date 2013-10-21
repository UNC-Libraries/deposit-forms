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
	
	<div class="indented_block">
		<div class="form_field file_field ${not empty deposit.files[deposit.blockFileIndexMap[element.formElement]] ? "filled" : ""}">
			<label><c:if test="${not empty element.formElement.usage}"><a title="${element.formElement.usage}">(i)</a></c:if>&nbsp;</label>
			<input name="files[${deposit.blockFileIndexMap[element.formElement]}]" type="file" class="file" size="40"/>
			<c:if test="${not empty deposit.files[deposit.blockFileIndexMap[element.formElement]]}">
				<span class="description">
					<b><c:out value="${deposit.files[deposit.blockFileIndexMap[element.formElement]].filename}"/></b>
					<input type="submit" name="_files[${deposit.blockFileIndexMap[element.formElement]}]" value="Remove file" class="remove"/>
				</span>
			</c:if>
			<c:if test="${element.formElement.required}"><span class="red">*</span></c:if>
			<form:errors cssClass="red" path="files[${deposit.blockFileIndexMap[element.formElement]}]" />
		</div>
	</div>
</div>
