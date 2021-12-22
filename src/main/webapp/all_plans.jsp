<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<script src="${pageContext.request.contextPath}/js/all_plans.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/all_plans.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<title><%=SM.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div id="all_plans_sub_main_container" class="common_sub_main_container">
			<div id="all_plans_header_container">
				<div id="all_plans_state_statistics_container">
					<div class="all_plans_state_circle_container">
						<div class="all_plans_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=PlanState.PREPARED.getDbCode() %>'>
							<div class="all_plans_unit_title"><%=PlanState.PREPARED.getName() %></div>
							<div class="all_plans_unit_count">0</div>
						</div>
					</div>				
					<div class="all_plans_align_line"><div title="日期到了开始日期"></div></div>
					<div class="all_plans_state_circle_container">
						<div class="all_plans_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=PlanState.ACTIVE.getDbCode() %>'>
							<div class="all_plans_unit_title"><%=PlanState.ACTIVE.getName() %></div>
							<div class="all_plans_unit_count">0</div>
						</div>
						<div class="all_plans_verticle_line" title="手动废弃"></div>
						<div class="all_plans_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=PlanState.ABANDONED.getDbCode() %>'>
							<div class="all_plans_unit_title"><%=PlanState.ABANDONED.getName() %></div>
							<div class="all_plans_unit_count">0</div>
						</div>
					</div>
					<div class="all_plans_align_line"><div title="手动废弃或日期过了结束日期"></div></div>
					<div class="all_plans_state_circle_container">
						<div class="all_plans_state_unit_container common_hover" title="按状态搜索" state_code='<%=PlanState.FINISHED.getDbCode() %>'>
							<div class="all_plans_unit_title"><%=PlanState.FINISHED.getName() %></div>
							<div class="all_plans_unit_count">0</div>
						</div>
					</div>
				</div>
			</div>
			<div id="all_plans_statics">
				<div id="all_plans_hint_when_loading">加载中......</div>
				<table></table>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
<jsp:include page="components/tag_edit_dialog/tag_edit_dialog.jsp" flush="false" />
<jsp:include page="components/plan_dialog/plan_dialog.jsp" flush="false" />
</body>
</html>