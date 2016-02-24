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

<form method="POST" enctype="multipart/form-data">
	<table class="gridtable">
		<tr>
			<td>File</td>
			<td><input type="file" id="id" name="batchFile"/>
		</tr>
		<tr align="center">
			<td colspan="2"><input type="submit" class="styled-button-4"
				value="Submit"> &nbsp;<input type="reset"
				class="styled-button-4" value="Clear"></td>
		</tr>
	</table>
</form>
