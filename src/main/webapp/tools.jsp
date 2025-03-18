<%@page import="manager.system.career.BookStyle"%>
<%@page import="manager.service.work.WorkService"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SelfX"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />

<script src="${pageContext.request.contextPath}/js/dependency/webuploader.nolog.min.js" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/dependency/webuploader.css" type="text/css" rel="stylesheet" /> 

<script src="${pageContext.request.contextPath}/js/tools.js?<%= SelfX.VERSION %>" type="text/javascript" ></script>
<link href="${pageContext.request.contextPath}/css/tools.css?<%= SelfX.VERSION %>" type="text/css" rel="stylesheet" />
<meta charset="utf-8" />
<title><%=SelfX.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div class="common_sub_main_container">
			<div class="common_loading_message tool_record_summary">加载中......</div>
			<div id="tools_datatable_main_container">
				<table></table>
			</div>
			<div class="common_module_message">
				本模块旨在用程序来帮助用户，尽量简化本可以用程序解决的事情，因此，假如您有任何希望用程序简化的事情，
				您可以<span class="common_open_new_window common_blut_font common_hover" href="web_info.jsp?container=web_info_connect_with_us">联系我</span>，
				我会根据普适性和技术难度评估，假如合适，我会实现这个工具，供每一个用户使用。
				当然，假如本模块确实帮到了您，也希望您能<span class="common_open_new_window common_blut_font common_hover" href="web_info.jsp?container=web_info_i_want_to_donate">捐款支持</span>，多谢。
			</div> 
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	<div id="file_upload_btns_container">
		<input type="file" name="imgs_extractor_src" accept="application/zip,application/vnd.openxmlformats-officedocument.presentationml.presentation"/>
	</div>
	<div id="tools_pattern_container" class="common_pattern_container">
		<div class="tools_tool_statictics">
			<div class="tools_tool_statictics_row">
				<span>成功<em><span class="suc_count"></span></em>次</span>
				<span>失败<em><span class="fail_count"></span></em>次</span>
			</div>
			<div class="tools_tool_statictics_row">
				<span>总次数<em><span class="sum_count"></span></em></span>
				<span>成功率<em><span class="suc_ratio"></span></em></span>
			</div>
		</div>
	
	</div>
	
</body>
</html>