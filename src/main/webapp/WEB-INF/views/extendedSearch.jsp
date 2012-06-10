<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>


<!DOCTYPE HTML>
<html>
<head>
<title>Extended Search</title>
<link
	href='http://fonts.googleapis.com/css?family=Open+Sans:400,600,700'
	rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="<c:url value='/css/blueprint/screen.css'/>"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="<c:url value='/css/blueprint/print.css'/>"
	type="text/css" media="print">
<link rel="stylesheet" href="<c:url value='/css/main.css'/>"
	type="text/css">
<script src="<c:url value='/js/jquery-1.6.1.min.js'/>"></script>
</head>
<body>
	<div id="head">
		<div id="logo">
			<a href="${pageContext.request.contextPath}" class="quiet">MacPaper</a>
		</div>

	</div>
	<div id="subhead">
		<h3>Extended Search:</h3>
		<div id="uploadPic">
			<a href="upload"> <img
				src="<s:url value="/icons/glyphicons_201_upload.png" />" />
			</a>
		</div>
		<div id="uploadButton">
			<a href="upload">Upload a new Paper </a>
		</div>
	</div>
	<div class="container">
		<div id="header">
			<div id='search-box'>
				<form action='search' id='search-form' method='get' target='_top'>
					<input id='search-text' name='searchPhrase' placeholder='type here'
						type='text' />
					<button id='search-button' type='submit'>
						<span>Search</span>
					</button>
					<button id='extendedsearch-button' type='button'
						onClick="location.href='${pageContext.request.contextPath}/extendedSearch'">
						<span>Extended Search</span>
					</button>
				</form>
			</div>
		</div>

		<div id="content" class="span-24 last">
			<form action='evaluateExtendedSearch' id='search-form' method='get'>
				<fieldset>
					<legend>Filter: </legend>
					Author: <br/> <input type="text" name="author" placeholder='Please insert author' /> <br/> 
					University: <br/> <input type="text" name="uni" placeholder='Please insert university' /> <br/> 
					Category: <br/> <input type="text" name="category" placeholder='Please insert category' /> <br/> 
					Tags: <br/> <input type="text" name="tags" placeholder='Please insert tags' /> <br/>
				</fieldset>
				<fieldset>
					<legend>Keywords:</legend>
					<input type="text" name="keywords"
						placeholder='Please insert keywords' /><br /> Search for <br/><input
						type="radio" name="andor" value=and />with all words<br /> <input
						type="radio" name="andor" value="andor" checked/>exact with this wordgroup

				</fieldset>
				<input id="save-button" type="submit" value="Search" />
			</form>
		</div>
	</div>
</body>
</html>