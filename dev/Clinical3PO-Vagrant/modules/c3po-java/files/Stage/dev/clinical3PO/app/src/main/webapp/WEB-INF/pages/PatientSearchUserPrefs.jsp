<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:form modelAttribute="patientSearchListForm" method="post">
	<table class="gridtable-userprefs" id="PatientSearchTable">
	<tr align="center">
		<td colspan="7">
			<input class="styled-button-4" type="button" name="addRow" value="Add Row" id="addRow"/>&nbsp;
			<input class="styled-button-4" type="submit" value="Submit">&nbsp;
			<input class="styled-button-4" type="reset" value="Clear">
		</td>
	</tr>
	</table>
	<script type="text/javascript">
		$('input#addRow').on( "click", function() {
			
			rowIndex=$('table#PatientSearchTable tr:last').index();
			patientCell="<td>Patient Id</td><td><input id=\"patientIds"+rowIndex+"\" type=\"text\" size=\"30\""
				+" name=\"personList["+rowIndex+"].patientIds\"></td>";
			observationCell="<td>Observation Id</td><td><div class=\"styled-multiselect\"><select multiple id=\"observationIds"+rowIndex+"\" name=\"personList["+rowIndex+"].observationIds\">"
				+"<option value=\"Albumin\">Albumin</option><option value=\"ALP\">Alkaline phosphatase</option>"
				+"<option value=\"ALT\">Alanine transaminase</option><option value=\"AST\">Aspartate transaminase</option><option value=\"Bilirubin\">Bilirubin</option>"
				+"<option value=\"BUN\">Blood urea nitrogen</option><option value=\"Cholesterol\">Cholesterol</option><option value=\"Creatinine\">Serum creatinine</option>"
				+"<option value=\"DiasABP\">Invasive diastolic arterial blood pressure</option><option value=\"FiO2\">Fractional inspired O2</option>"
				+"<option value=\"GCS\">Glasgow Coma Score</option>"
				+"<option value=\"Glucose\">Serum glucose</option><option value=\"HCO3\">Seum bicarbonate</option><option value=\"HCT\">Hematocrit</option>"
				+"<option value=\"HR\">Heart Rate</option><option value=\"K\">Serum potassium</option><option value=\"Lactate\">Lactate</option>"
				+"<option value=\"Mg\">Serum magnesium</option><option value=\"MAP\">Invasive mean arterial blood pressure</option>"
				+"<option value=\"MechVent\">Mechanical ventilation respiration</option>"
				+"<option value=\"Na\">Serum sodium</option><option value=\"NIDiasABP\">Non-invasive diastolic arterial blood pressure</option>"
				+"<option value=\"NIMAP\">Non-invasive mean arterial blood pressure</option><option value=\"NISysABP\">Non-invasive systolic arterial blood pressure</option>"
				+"<option value=\"PaCO2\">Partial pressure of arterial CO2</option><option value=\"PaO2\">Partial pressure of arterial O2</option>"
				+"<option value=\"pH\">Arterial pH</option>"
				+"<option value=\"Platelets\">Platelets</option><option value=\"RespRate\">Respiration rate</option><option value=\"SaO2\">O2 saturation in hemoglobin</option>"
				+"<option value=\"SysABP\">Invasive systolic arterial blood pressure</option><option value=\"Temp\">Temperature</option><option value=\"TropI\">Troponin-I</option>"
				+"<option value=\"TropT\">Troponin-T</option><option value=\"Urine\">Urine</option><option value=\"WBC\">White blood cell count</option>"
				+"<option value=\"Weight\">Weight</option><option value=\"Height\">Height</option></select></td>"
			colorCodeCell="<td>Color Code</td><td><input id=\"picker"+rowIndex+"\""
				+"type=\"text\" name=\"personList["+rowIndex+"].colorCode\"></td>";
			deleteCheckbox="<td><input id=\"delete"+rowIndex+"\""
				+"type=\"checkbox\" title=\"Delete Record\" name=\"delete"+rowIndex+"\"></td>"
			newRow="<tr id=\"datarow\">"+patientCell+observationCell+colorCodeCell+deleteCheckbox+"</tr>";
	
			$('table#PatientSearchTable tr:last').before(newRow);
			
			$('#picker'+rowIndex).css({"margin":"0","padding":"0","border":"2","width":"40px","height":"20px"});
			
			$('#picker'+rowIndex).colpick({
				layout:'hex',
				submit:0,
				colorScheme:'dark',
				onChange:function(hsb,hex,rgb,el,bySetColor){
					$(el).css('background-color','#'+hex);
					$(el).css('color','#'+hex);
					if(!bySetColor) $(el).val(hex);
				}
				}).keyup(function(){
					$(this).colpickSetColor(this.value);
				});
		});
		
		// Deletes the selected row (checkbox click)
		$('table#PatientSearchTable').delegate('tr input:checkbox', 'click', function () {
			$(this).closest('tr').remove();
			
			rowIndex=$('table#PatientSearchTable tr:last').index();
			
			index=0;
			var id,name;
			
			if(rowIndex >=1) {
				$('table#PatientSearchTable tr#datarow').each(function (i, row) {
					var $row = $(row);
					var $tds = $row.find("td");
					
					var $patientIdTd=$tds.eq(1).find("input");
					
					id="patientIds"+index;
					$patientIdTd.attr('id',id);
					
					name="personList["+index+"].patientIds";
					$patientIdTd.attr('name',name);
					
					var $observationIdTd=$tds.eq(3).find("select");
					
					id="observationIds"+index;
					$observationIdTd.attr('id',id);
					
					name="personList["+index+"].observationIds";
					$observationIdTd.attr('name',name);
					
					var $colorCodeTd=$tds.eq(5).find("input");
					
					id="picker"+index;
					$colorCodeTd.attr('id',id);
					
					name="personList["+index+"].colorCode";
					$colorCodeTd.attr('name',name);
					
					index++;
				});
			}
			
		});
		
	</script>
</form:form>