<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="crosswalk.*"%>
<div class="metadata_block">
	<div class="indented_block">
		<div class="form_field width_Normal">
			<label>${element.formElement.label}</label>
			<form:select path="form.elements[${elementRow.index}].selectedMajorIndex">
				<c:forEach items="${element.formElement.majorEntries}" var="majorEntry" varStatus="majorRow">
					<form:option value="${majorRow.index}">${majorEntry.name}</form:option>
				</c:forEach>
			</form:select>
		</div>
	</div>
</div>
