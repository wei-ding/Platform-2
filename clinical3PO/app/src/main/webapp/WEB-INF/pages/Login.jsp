<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
.errorblock {
	color: #ff0000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>

<form name='loginForm' action="<c:url value='j_spring_security_check' />" method='POST'>
	<table border="1" width="20%" style="margin-left:380px;margin-top:110px">
		<tr><td colspan="2"><center><h3>clinical3PO Login</h3></center></td></tr>
		<tr>
			<td>User:</td>
			<td><input type='text' name='j_username' value=''></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><input type='password' name='j_password' /></td>
		</tr>
		<tr>
			<td colspan="2"><center><input name="Submit" type="submit" value="Submit"/>&nbsp;&nbsp;<input name="Reset" type="reset" /></center></td>
		</tr>
		<tr><td colspan="2">
			<c:if test="${not empty error}">
				<div class="errorblock">
					Your login attempt was not successful, try again.
				</div>
			</c:if>
		</td></tr>
	</table>

</form>