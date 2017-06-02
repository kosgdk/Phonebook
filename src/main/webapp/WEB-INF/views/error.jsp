<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link rel="stylesheet" href='<c:url value="/resources/css/bootstrap.css" />'>
	<link rel="stylesheet" href='<c:url value="/resources/css/font-awesome.css" />'>
	<script src='<c:url value="/resources/js/jquery.min.js" />'></script>
	<script src='<c:url value="/resources/js/bootstrap.min.js" />'></script>

	<title>Ошибка</title>


</head>
<body>
	<div class="container col-md-4">
		<legend>
			<a href="<spring:url value='/'/>"><i class="fa fa-phone" aria-hidden="true" style="margin-right: 5px"></i>Телефонная книга</a>
		</legend>

		<div class="alert alert-danger">
			${message}
		</div>
	</div>

</body>
</html> 