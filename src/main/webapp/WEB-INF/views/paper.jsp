<%@ include file="/WEB-INF/views/includes/taglibs.jsp"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>MacPaper ResultPage</title>
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
		<h3>Detailed Information about the paper ${recievedPaperName}</h3>
	</div>
	<div class="container">

		<!-- 		<div id="header" class="prepend-1 span-22 append-1 last"> -->
		<div id="content" class="span-24 last">
			<a href="javascript:history.back()">back</a>
			<sf:form modelAttribute="paper" method="POST"
				action="${pageContext.request.contextPath}/paper/update"
				enctype="multipart/form-datahast ">
				<table>
					<tr>
						<td><sf:label path="paperId">PaperId:</sf:label></td>
						<td><sf:input path="paperId" readonly="true" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="uploadDate">Upload Date:</sf:label></td>
						<td><sf:input path="uploadDate" readonly="true" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="title">Title:</sf:label></td>
						<td><sf:input path="title" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="createDate">Creation Date:</sf:label></td>
						<td><sf:input path="createDate" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="kindOf">Categories:</sf:label></td>
						<td><sf:input path="kindOf" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="fileName">Filename:</sf:label></td>
						<td><sf:input path="fileName" size="50" /></td>
					</tr>

					<tr>
						<td><sf:label path="paperAbstract">Abstract:</sf:label></td>
						<td><sf:textarea path="paperAbstract" /></td>
					</tr>



				</table>

				<input type="submit" value="Save" />
			</sf:form>

		</div>
		<div id="footer" class="span-24 ">
			<p></p>
		</div>
	</div>
</body>
</html>