<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<style>
.error {
    background-color: #ffffff;
    border: 0 solid #fa5858;
    color: #df0101;
    padding: 1px;
}
</style>

<form:form modelAttribute="observationSearchForm" method="post">
<table class="gridtable">
	<tr>
		<td>Patient Id</td>
		<td><form:input path="patientId" id="patientIdInput" /></td>
	</tr>
	<tr>
	<td>Observation Id</td>
	<td>
		<div class="styled-select">
			<form:select path="observationId" id="observationIdInput" items="${observationIds}" />
		</div>
		</td>
	</tr>
	<tr align="center">
		<td colspan="2"><input type="submit" class="styled-button-4" value="Submit">
		&nbsp;<input type="reset" class="styled-button-4" value="Clear"></td>
	</tr>
	
	<tr>
		<td colspan="2">
			<form:errors path="patientId" cssClass="error" />
		</td>
	</tr>
	
</table>
</form:form>
