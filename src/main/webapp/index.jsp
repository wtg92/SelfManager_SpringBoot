<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SelfX"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Kindle Login</title>
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<link rel="stylesheet" href="css/basic.css" />
</head>
<body>
<h1>Kindle Login</h1>
<div id="step1_container">
	<input id="email" placeholder="Email" /><br />
	<input id="password" placeholder="Password" type="password" /><br />
	<button onclick="login()">Login</button>
</div>

<div id="records_container">

</div>


<div id="jquery_pattern">
	<div class="record_unit_container">


	</div>

</div>


</body>
</html>
