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

<form:form method="POST">

	<H3>
		<input type="checkbox" name="NLP-HIVE">Proceed with updating
		Hive DB from NLP output(XML)<BR>
	</H3>
 
	<input type="submit" value="Submit">
</form:form>