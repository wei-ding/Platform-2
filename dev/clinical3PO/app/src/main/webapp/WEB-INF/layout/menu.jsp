<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">

#header {
  -moz-border-radius:  10px 10px 0 0;
}

ul.topnav {  
	font: 11px Arial;
	border-top: none;
	text-align: center;
	vertical-align: middle;
    list-style: none;  
    padding: 0 20px;  
    margin: 0;  
    width: 600px;  
	margin-left: 100px;
    background-color: #FFF; /*#929292*/ 
    font: 12px Arial;
	height: 27px;
	border-radius: 0 0 10px 10px;
    -moz-border-radius: 0 0 10px 10px;
}

ul.topnav li {  
    float: left;
	font-weight: bold;
    margin: 0;  
    padding: 0 15px 0 0;
	width: 170px;
	position: relative;
	text-align: left;
}

ul.topnav li a{  
    padding: 5px;  
    color: #636363;  
    display: block;  
	float: left;
    text-decoration: none;  
}  

ul.topnav li a:hover{  
    background-color: #CBCBCB;  
}

.selected{
	background-color: #CBCBCB;
}

ul.topnav li ul.subnav {  
    list-style: none;  
    position: absolute; /*--Important - Keeps subnav from affecting main navigation flow--*/  
    left: 0; 
	top: 25px;  
    background: #CBCBCB;  
    margin: 0;
	padding: 0;  
    display: none;  
    width: 200px;  
}  

ul.subnav li{
  font-size: 11px;
  color: #FFF;
  font-weight: normal;
  display: block;
  width: 190px;
}

ul.subnav li a{
  text-decoration: none;
  width: 150px;
}

ul.subnav li a:hover{  
    background-color: #929292;  
}

</style>
<head>
<script type="text/javascript">
$(document).ready(function(){  
    $("ul.topnav li a").hover(function() { //When trigger is hovered...  
        //Following events are applied to the subnav itself (moving subnav up and down)
		$("a",$(this).parent()).addClass("selected");
        $(this).parent().find("ul.subnav").slideDown('fast').show(); //Drop down the subnav on hoverk  
        $(this).parent().hover(function() {  
        }, function(){  
            $(this).parent().find("ul.subnav").slideUp('slow'); //When the mouse hovers out of the subnav, move it back up  
			if($("a","ul.topnav li").hasClass("selected")){
			 $("a","ul.topnav li").removeClass("selected")
			}
        });  
  
        //Following events are applied to the trigger (Hover events for the trigger)  
        }).hover(function() {  
            $(this).addClass("subhover"); //On hover over, add class "subhover"  
        }, function(){  //On Hover Out  
            $(this).removeClass("subhover"); //On hover out, remove class "subhover"  
    });  
  
});
</script>
</head>

<div class="bold" id="menu">
<ul class="topnav">

  <li id="patientsearchsubmenu"><a href="#" >Search</a>
    <ul class="subnav">

	 <li class="menubar"><a href="#">PatientSearch</a>
		<ul>
			<li><a href="<c:url value = '/PatientSearchRestricted/' />" title="Accumulo">Accumulo</a></li>
			<li><a href="<c:url value = '/PatientSearch/' />" title="Hadoop">Hadoop</a></li>
			<li><a href="<c:url value = '/PatientSearch/HiveQLBatch/' />" title="Hive">Hive</a></li>
			<li><a href="<c:url value = '/RPatientSearch/' />" title="R">R</a></li>
		</ul>
	 </li>

	 <li class="menubar"><a href="#">ObservationSearch</a>
		<ul>
			<li><a href="<c:url value = '/ObservationSearchRestricted/' />" title="Accumulo">Accumulo</a></li>
			<li><a href="<c:url value = '/ObservationSearch/' />" title="Hadoop">Hadoop</a></li>
			<li><a href="<c:url value = '/ObservationSearch/HiveQLBatch/' />" title="Hive">Hive</a></li>
			<li><a href="<c:url value = '/RObservationSearch/' />" title="R">R</a></li>
		</ul>
	 </li>

	 <li class="menubar"><a href="#">BatchSearch</a>
		<ul>
			<li><a href="<c:url value = '/ObservationSearchRestricted/FileUpload/' />" title="Accumulo">Accumulo</a></li>
			<li><a href="<c:url value = '/ObservationSearch/FileUpload' />" title="Hadoop">Hadoop</a></li>
			<li><a href="<c:url value = '/ObservationSearch/FileUploadHiveQL/' />" title="Hive">Hive</a></li>
			<li><a href="<c:url value = '/RObservationSearch/FileUpload' />" title="R">R</a></li>
		</ul>
	 </li>

	 <li class="menubar"><a href="#">PatientSearchUserPrefs</a>
		<ul>
			<li><a href="<c:url value = '/PatientSearchUserPrefs/'/>">Hadoop</a></li>
		</ul>
	 </li>

	 <li><a href="<c:url value = '/MLCrossValidation/'/>">ML - Cross Validation</a></li>
	 <li><a href="<c:url value = '/MLCrossValidation/FExtraction'/>">ML - Feature Extraction</a></li>
	</ul>
  </li>

  <li id="Utilities"> 
    <a href="<c:url value = '/MySearch/'/>">My Searches</a> 
  </li>

  <li id="utilitiessubmenu"> 
    <a href="#" >Utilities</a> 
     <ul class="subnav">
		<li><a href="<c:url value = '/j_spring_security_logout'/>">Logout</a></li>
	</ul>
  </li>
  
</ul>
</div>
