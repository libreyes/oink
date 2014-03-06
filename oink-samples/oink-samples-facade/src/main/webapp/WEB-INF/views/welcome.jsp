<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>OINK Facade Module</title>
</head>
<body>
	<center>
		<h1>OINK Facade Module</h1>
		<p>
			<%
				Date date = new Date();
				out.print(date.toString());
			%>
		</p>
		<c:if test="${not empty oinkVersion}">
			<p>Version ${oinkVersion}</p>
		</c:if>
	</center>
	<p></p>
	<h2>Overview</h2>
	<p>
		This is a fhir adapter to the OINK System that allows <a
			href="http://www.openeyes.org.uk">OpenEyes</a> to interact with other
		components using OINK by making FHIR requests.
	</p>

	<h2>Configured Services</h2>
	<p>Facades are currently exposed for the following services:</p>
	<table>
		<tr>
			<th>Service Name</th>
			<th>Relative Path</th>
		</tr>
		<c:forEach items="${servicePaths}" var="servicePath">
			<tr>
				<td>${servicePath.key}</td>
				<td><a href="<c:url value="${servicePath.value}"/>">${servicePath.value}</a></td>
			</tr>
		</c:forEach>
	</table>
	<small>Replace the ** with a FHIR Resource</small>

	<h2>RabbitMQ Information</h2>
	<table>
		<tr>
			<td>Rabbit Broker</td>
			<td>amqp://${rabbitBrokerHost}:${rabbitBrokerPort}</td>
		</tr>
		<tr>
			<td>Can connect?</td>
			<td><c:if test="${rabbitConnectionOk}">
			YES
		</c:if> <c:if test="${not rabbitConnectionOk}">
			NO
		</c:if></td>
		</tr>
		<tr>
			<td>Rabbit Broker Management Plugin URL</td>
			<td><c:if test="${empty rabbitManagementPort}">
			N/A
		</c:if> <c:if test="${not empty rabbitManagementPort}">
			${rabbitBrokerHost}:${rabbitManagementPort}
		</c:if></td>
		</tr>
	</table>
</body>
</html>