<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>

<!DOCTYPE HTML>
<html>
<head>
<title>Basic Search</title>
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
		<h3>basic Search:</h3>
	</div>
	<div class="container">
		<div id="header">
			<div id='search-box'>
				<a href="javascript:history.back()">Back</a>
				<form action='elasticsearch' id='search-form' method='get'
					target='_top'>
					<input id='search-text' name='searchPhrase' placeholder='type here'
						type='text' />
					<button id='search-button' type='submit'>
						<span>Search</span>
					</button>
				</form>
					<a href="javascript:history.back()">Back</a>
			</div>
		</div>
	</div>
</body>
</html>