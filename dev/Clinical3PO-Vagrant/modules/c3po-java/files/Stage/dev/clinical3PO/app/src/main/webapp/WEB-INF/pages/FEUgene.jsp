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

<form:form modelAttribute="feUgeneForm" method="POST" enctype="multipart/form-data">
	
	<table class="gridtable">
	<tr>
		<td>Class Property</td>
		<td><form:input path="classProperty" id="classPropertyInput" /></td>
	</tr>
	
	<tr>
		<td>class time</td>
		<td>
			<form:input path="classTime" id="classTimeInput" />
			<form:errors path="classTime" cssClass="error" />
		</td>
		</tr>	
		
	<tr>
		<td>start time</td>
		<td>
			<form:input path="startTime" id="startTimeInput" />
			<form:errors path="startTime" cssClass="error" />
		</td>
	</tr>
		
	<tr>
		<td>end time</td>
		<td>
			<form:input path="endTime" id="endTimeInput" />
			<form:errors path="endTime" cssClass="error" />
		</td>
	</tr>
	
	<tr align="center">
		<td colspan="2"><input type="submit" class="styled-button-4" value="Submit">
		&nbsp;<input type="reset" class="styled-button-4" value="Clear"></td>
	</tr>
	
	<tr>
		<td colspan="2">
			<form:errors path="classProperty" cssClass="error" />
		</td>
	</tr>
	
	</table>
</form:form>