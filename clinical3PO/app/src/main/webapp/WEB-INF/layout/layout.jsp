<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>

<script type="text/javascript" src="<c:url value = '/resources/js/jquery-1.11.0.min.js'/>" charset="UTF-8"></script>
<script type="text/javascript" src="<c:url value = '/resources/js/jquery.colorbox.js'/>" ></script>
<script type="text/javascript" src="<c:url value = '/resources/js/colpick.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value = '/resources/css/main.css'/>">
<link rel="stylesheet" type="text/css" href="<c:url value = '/resources/css/colorbox.css'/>" />
<link rel="stylesheet" type="text/css" href="<c:url value = '/resources/css/colpick.css'/>" />
<link rel="stylesheet" type="text/css" href="<c:url value = '/resources/css/app.css'/>" />

<title>
   <tiles:getAsString name="title" />
</title>
</head>
<body>
<div class="full">
 <tiles:insertAttribute name="header" />
 <tiles:insertAttribute name="menu" />
 <tiles:insertAttribute name="body" /> <br />
 <tiles:insertAttribute name="footer" /></div>
</body>
</html>