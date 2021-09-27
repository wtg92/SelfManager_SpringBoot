<%@ page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<script src="${pageContext.request.contextPath}/js/header.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/header.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />

<div id="header_main_container"  class="common_dark_background common_light_font">
	<div id="header_sign_out_container" class="common_sub_main_container">
		<div id="left_flex_header_sign_out_container">
			<img class="web_logo" src="<%=SM.BASIC_FILES_FOLDER %>web_logo_white.png" title="返回首页"/>
		<a class="what_software_do common_dark_white_font common_hover_font" href="web_info.jsp?container=web_info_what_sm_to_do">Self 能做什么？</a>
		</div>
		<div id="right_flex_header_sign_out_container">
			<img src="<%=SM.BASIC_FILES_FOLDER %>web_spirit.png" class="spirit_img" title="精神"/>
		</div>
	</div>
	<div id="header_sign_in_container" class="common_sub_main_container">
		<div id="left_flex_header_sign_in_container">
	<img class="web_logo" src="<%=SM.BASIC_FILES_FOLDER %>web_logo_white.png" title="返回首页"/> 

	
		</div>
		<div id="middle_flex_header_sign_in_container">
			<span class="users_module_button">
				<span page="users.jsp" class="common_hover_font">用户管理</span>
			</span>
			<span class="works_module_button">
				<span page="work.jsp" class="common_hover_font">工作表</span>
			</span>
			<span class="notes_module_button">
				<span page="notes.jsp" class="common_hover_font">笔记</span>
			</span>
			<span class="tools_module_button">
				<span page="tools.jsp" class="common_hover_font">工具</span>
			</span>
		</div>
		<div id="right_flex_header_sign_in_container">
			<img src="<%=SM.BASIC_FILES_FOLDER %>web_spirit.png" class="spirit_img" title="精神"/>
			<div id="header_avator_container"data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></div>
			 <div class="dropdown-menu" aria-labelledby="header_avator_container" id="header_user_actions_container">
   	 			<span class="dropdown-item" id="header_sign_out">退出登录</span>
 			 </div>
		</div>
	</div>
</div>
