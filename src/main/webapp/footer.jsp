<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<script src="${pageContext.request.contextPath}/js/footer.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/footer.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />

<div id="footer_main_container">
	<div id="footer_information_groups_container" class="common_sub_main_container">
		
	</div>

	<div id="bei_an_mes_container" class="common_sub_main_container">
		<div class="bei_an_mes">
			<img src="<%=SM.BASIC_FILES_FOLDER%>beian.png"/>
			<a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=32011202000541">苏公网安备 32011202000541号</a>
		</div>
		<span  class="version_mes common_grey_font">更新于 <span></span> </span>
	</div>
</div>
	


<div id="footer_pattern_container" class="common_pattern_container">
	<div class="info_group_container">
		<div class="name"></div>
		<div class="items">
			<ul></ul>
		</div>
	</div>
</div>