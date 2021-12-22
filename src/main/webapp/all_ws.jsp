<%@page import="manager.system.career.WorkSheetState"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<script src="${pageContext.request.contextPath}/js/all_ws.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/all_ws.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<title><%=SM.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div id="all_ws_sub_main_container" class="common_sub_main_container">
			<div id="all_ws_header_container">
				<div id="all_ws_state_statistics_container">
					<div class="all_ws_state_circle_container">
						<div class="all_ws_state_unit_container hidden_pie">
							<div class="all_ws_unit_title"></div>
							<div class="all_ws_unit_count"></div>
						</div>
						<div class="all_ws_verticle_line hidden_line" ></div>
						<div class="all_ws_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=WorkSheetState.ACTIVE.getDbCode() %>'>
							<div class="all_ws_unit_title"><%=WorkSheetState.ACTIVE.getName() %></div>
							<div class="all_ws_unit_count">0</div>
						</div>
						<div class="all_ws_verticle_line" title="自动计算"></div>
						<div class="all_ws_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=WorkSheetState.OVERDUE.getDbCode() %>'>
							<div class="all_ws_unit_title"><%=WorkSheetState.OVERDUE.getName() %></div>
							<div class="all_ws_unit_count">0</div>
						</div>
					</div>				
					<div class="all_ws_align_line">
						<div title="自动计算"></div>
						<div title="自动计算"></div>
						<div title="手动操作"></div>
						<div title="手动操作"></div>
					</div>
					<div class="all_ws_state_circle_container">
						<div class="all_ws_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=WorkSheetState.OVER_FINISHED.getDbCode() %>'>
							<div class="all_ws_unit_title"><%=WorkSheetState.OVER_FINISHED.getName() %></div>
							<div class="all_ws_unit_count">0</div>
						</div>
						<div class="all_ws_verticle_line" title="同步"></div>
						<div class="all_ws_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=WorkSheetState.FINISHED.getDbCode() %>'>
							<div class="all_ws_unit_title"><%=WorkSheetState.FINISHED.getName() %></div>
							<div class="all_ws_unit_count">0</div>
						</div>
						<div class="all_ws_verticle_line" title="同步"></div>
						<div class="all_ws_state_unit_container common_hover"  title="按状态搜索"  state_code='<%=WorkSheetState.NO_MONITOR.getDbCode()%>'>
							<div class="all_ws_unit_title"><%=WorkSheetState.NO_MONITOR.getName()%></div>
							<div class="all_ws_unit_count">0</div>
						</div>
					</div>
				</div>
			</div>
			<div id="all_ws_genral_btn_groups">
				<span class="btn btn-outline-primary" id="all_ws_open_plan_dept_dialog_btn">查看历史欠账</span>
			</div>
			<div id="all_ws_statics">
				<div id="all_ws_hint_when_loading">加载中......</div>
				<table></table>
			</div>
		</div>
	</div>
<div class="modal fade" id="all_ws_plan_work_sheet_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="all_ws_plan_work_sheet_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-xl">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="all_ws_plan_work_sheet_dialog_label"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<jsp:include page="components/work_sheet/work_sheet.jsp" flush="false" />
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal" >关闭</button>
      </div>
    </div>
  </div>
</div>
<jsp:include page="components/work_sheet/work_statistics.jsp" flush="false" />
<jsp:include page="components/plan_dept_dialog/plan_dept_dialog.jsp" flush="false" />

<jsp:include page="footer.jsp" flush="false" />
</body>
</html>