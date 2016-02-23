<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script>
	$(document).ready(function(){
		//Examples of how to assign the Colorbox event to elements
		$(".group1").colorbox({rel:'group1'});
		$(".group2").colorbox({rel:'group2', transition:"none", width:"100%", height:"100%", arrowKey:false});
		$(".group3").colorbox({rel:'group3', transition:"none", width:"100%", height:"1000%", arrowKey:false});
		
		$(document).bind('cbox_open', function(){
		 $('body').css({overflow:'hidden'});
		}).bind('cbox_closed', function(){
		 $('body').css({overflow:'auto'});
		});
			

	});
</script>

<table class="gridtable-mysearch">
<tr>
	<th>Search On</th>
	<th>Search Start Time</th>
	<th>Search Finish Time</th>
	<th>Search Parameters</th>
	<th>Status</th>
</tr>

<c:forEach items="${jobs}" var="job">
	<tr>
		<td>${job.searchOn}</td>
		<td>${job.searchStartTime}</td>
		<c:if test="${job.searchEndTime == null}">
				<td>&nbsp;</td>
		</c:if>	
		<c:if test="${job.searchEndTime != null}">
				<td>${job.searchEndTime}</td>
		</c:if>		
		<td>
			<div class="div-scrollable">
				<c:forEach var="entry" items="${job.searchParameters}">
					${entry.key} | ${entry.value}
				</c:forEach>
			</div>
		</td>
		<td><c:choose>
				<c:when test="${job.status == 'FINISHED'}">
				   <c:choose>
					    <c:when test="${(job.searchOn == 'Patient ID') || (job.searchOn == 'Patient ID Restricted') || (job.searchOn == 'Patient ID Hive') || (job.searchOn == 'Patient ID R')}">
							<a class="group1" href="../Visualization/Patient/${job.id}"
								>${job.status}</a>
						</c:when>
					    <c:when test="${(job.searchOn == 'Observation ID') || (job.searchOn == 'Observation ID Restricted') || (job.searchOn == 'Observation ID Hive') || (job.searchOn == 'Observation ID R')}">
							<a class="group2" href="../Visualization/Observation/${job.id}"
								>${job.status}</a>
						</c:when>				
					    <c:when test="${(job.searchOn == 'Patient ID User Prefs - Batch') || (job.searchOn == 'Patient ID User Prefs R- Batch')}">
							<a target="_blank" href="../Visualization/PatientWithUserPrefs/${job.id}">${job.status}</a>
							<a href="../FileDownload/DownloadFile/${job.id}">Download file</a>							
						</c:when>
						<c:when test="${(job.searchOn == 'Patient ID User Prefs - Batch Restricted')}">
							<a target="_blank" href="../Visualization/PatientWithUserPrefs/${job.id}">${job.status}</a>
							<a href="../FileDownload/DownloadFile/${job.id}">Download file</a>							
						</c:when>
						<c:when test="${(job.searchOn == 'Patient ID User Prefs - Hive')}">
							<a target="_blank" href="../Visualization/PatientWithUserPrefs/${job.id}">${job.status}</a>
							<a href="../FileDownload/DownloadFile/${job.id}">Download file</a>							
						</c:when>
						<c:when test="${(job.searchOn == 'Patient ID User Prefs')}">
							<a target="_blank" href="../Visualization/PatientWithUserPrefs/${job.id}">${job.status}</a>							
						</c:when>
						<c:when test="${(job.searchOn == 'Cross Validation') || (job.searchOn == 'Feature Extraction - MlFlex')}">
							${job.status}
							<a target="_blank" href="../MLCrossValidation/DownloadReport/${job.id}">Download Report</a>		
							<a href="../FileDownload/DownloadFile/${job.id}">Download file</a>						
						</c:when>
						<c:when test="${(job.searchOn == 'Feature Extraction - Ugene')}">
							${job.status}
							<a target="_blank" href="../FExtraction/DownloadUgeneReport/${job.id}">Download Report</a>
						</c:when>
						<c:when test="${(job.searchOn == 'NLP To Hive Update')}">
							<a target="_blank">${job.status}</a>
						</c:when>
						<c:otherwise>			
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${(job.searchOn == 'Patient ID User Prefs - Batch') || (job.searchOn == 'Patient ID User Prefs - Batch Restricted')}">
							${job.status}
							<a href="../FileDownload/DownloadFile/${job.id}">Download file</a>
						</c:when>
						<c:otherwise>
							${job.status}
						</c:otherwise>
						
					</c:choose>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
</c:forEach>
</table>
