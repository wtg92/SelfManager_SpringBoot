<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="manager.system.SelfXPerms"%>
<%@page import="manager.system.DBConstants"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SelfX"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<link href="${pageContext.request.contextPath}/css/users.css?<%= SelfX.VERSION %>" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/users.js?<%= SelfX.VERSION %>" type="text/javascript"></script>
<title><%=SelfX.WEB_TITLE %></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div id="users_main_container" class="common_main_container">
		<div id="users_sub_main_container" class="common_sub_main_container">
			<div id="users_user_summary_container">
				<span class="users_sign_up_users">注册用户<em></em></span>
				<span class="users_active_users">当前活跃用户<em></em></span>
			</div>
			<div id="users_user_and_group_detail_container">
				<div id="users_groups_container" class="btn-group btn-group-toggle" data-toggle="buttons"></div>
				<div id="users_group_detail_container">
					<div id="users_group_controgroup_container">
						<button type="button" class="btn btn-primary" id="users_open_group_perms_dialog">编辑权限</button>
						<button type="button" class="btn btn-primary" id="users_open_group_summary_dialog">查看分析</button>
					</div>
					<div id="users_group_datatable_container">
						<span>最多显示<em><%=SelfX.MAX_DB_LINES_IN_ONE_SELECTS %></em>条</span>
					</div>
					<div id="users_datatable_container">
						<table></table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	
<div class="modal fade" id="users_group_perms_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="users_group_perms_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="users_group_perms_dialog_label">编辑权限</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<input type="hidden" id="users_target_group_id"/>
      	<% 
      	   Map<String,List<SelfXPerms>> permsByGroup =  SelfXPerms.getPermsByGroup();
      		for(String title :permsByGroup.keySet()){
      			%>
      			<div class="users_one_group_perms_container">
					<div class="users_title"><%=title%></div>
					<div class="users_perms_button_group btn-group btn-group-toggle" data-toggle="buttons">
						<%
							for(SelfXPerms one :permsByGroup.get(title)){
								%>
								  <label class="btn btn-outline-primary">
    									<input type="checkbox" name="perm" value="<%=one.getDbCode() %>" > <%=one.getName() %>
 								</label>
								<% 								
							}
						%>
					</div>
				</div>
      			<% 
      		}
      	%>


      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="users_override_group_perms_button">设置</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
	
	
</body>
</html>