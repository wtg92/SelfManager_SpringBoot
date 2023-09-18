<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<link href="${pageContext.request.contextPath}/css/index.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/index.js?<%= SM.VERSION %>" type="text/javascript"></script>
<title><%=SM.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div id="index_main_container" class="common_dark_background common_light_font common_main_container">
		<div id="index_sub_main_container" class="common_sub_main_container">
		<div id="index_text_info_container">
			<p>
			<span>欢迎来到<%=SM.BRAND_NAME%></span>
			<br/>
			<span>一个面向个人的信息管理系统</span>
			</p>
		</div>
			<div id="index_sign_in_info_container">
				<form>
					<div id="index_switch_log_in_method_container">
						<div class="btn-group btn-group-toggle" data-toggle="buttons">
							<%
								for(VerifyUserMethod method:VerifyUserMethod.values()){
																if(method == VerifyUserMethod.UNDECIDED)
																	continue;
							%>
							<label class="btn btn-outline-primary"> 
								<input type="radio" name="sign_in_method" value="<%=method.getDbCode()%>"/><%=method.getName()%>
							</label>
							<%
								}
							%>
						</div>
					</div>
					<div id="index_sign_in_main_container">
						<div id="index_account_pwd_sign_in_container"
							code="<%=VerifyUserMethod.ACCOUNT_PWD.getDbCode()%>">
							<div class="index_entity_for_sign_in">
								<div class="sign_in_title">账号</div>
								<input name="account" type="text" />
								<div class="index_sign_in_error_container"></div>
								<div class="index_forgot_container">
									<span id="index_forgot_account_btn" class="common_hover common_blue_font">忘记了账户？</span>
								</div>
							</div>
							<div class="index_entity_for_sign_in">
								<div class="index_pwd_sign_in_container">
									<span class="sign_in_title">密码</span>
									<span class="index_control_pwd_visibility_container"></span>
								</div>
								<input name="account_pwd" type="password" />
								<div class="index_sign_in_error_container"></div>
								<div class="index_forgot_container">
									<span id="index_forgot_pwd_btn" class="common_hover common_blue_font">忘记了密码？</span>
								</div>
							</div>
						</div>
						<div id="index_email_pwd_sign_in_container"
							code="<%=VerifyUserMethod.EMAIL_VERIFY_CODE.getDbCode()%>">
							<div class="index_entity_for_sign_in">
								<div class="sign_in_title">邮箱</div>
								<input type="text" name="email"/>
								<div class="index_sign_in_error_container"></div>
							</div>
							<div class="index_entity_for_sign_in">
								<div class="index_pwd_sign_in_container">
									<span class="sign_in_title">验证码</span>
									<span id="index_send_email_verify_code_btn" class="common_blue_font common_hover">发送验证码</span>
								</div>
								<input type="text" name="email_verify_code" />
								<div class="index_sign_in_error_container"></div>
							</div>
						</div>
						<div id="index_tel_verify_code_sign_in_container"
							code="<%=VerifyUserMethod.TEL_VERIFY_CODE.getDbCode()%>">
							<div class="index_entity_for_sign_in">
								<div class="sign_in_title">手机</div>
								<input type="text" name="tel" />
								<div class="index_sign_in_error_container"></div>
							</div>
							<div class="index_entity_for_sign_in">
								<div class="index_pwd_sign_in_container">
									<span class="sign_in_title">验证码</span>
									<span id="index_send_tel_verify_code_btn" class="common_blue_font common_hover">发送验证码</span>
								</div>
								<input type="text" name="tel_verify_code" />
								<div class="index_sign_in_error_container"></div>
							</div>
						</div>
					</div>
					<div id="index_sign_in_controgroup_container">
						<div id="index_select_remember_me_info"><input type="checkbox" name="remember_me" checked="checked"><span>记住我</span></div>
						<div id="sign_up_container" class="common_blue_font">
							<a href="sign_up.jsp">注册</a>
						</div>
						<div id="index_sign_in_button" >登    录</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	
<div class="modal fade" id="index_find_account_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="index_find_account_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="index_find_account_dialog_label">找回账户</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div class="one_row_of_find_acount">
      	<div class="one_row_of_find_acount_title">找回方式</div>
     
			<div id="index_switch_find_account_method_container">
						<div class="btn-group btn-group-toggle" data-toggle="buttons">
					<%
						for(VerifyUserMethod method:VerifyUserMethod.values()){
												if(method == VerifyUserMethod.UNDECIDED || method == VerifyUserMethod.ACCOUNT_PWD)
													continue;
					%>
							<label class="btn btn-outline-primary"> 
								<input type="radio" name="retrieve_method" value="<%=method.getDbCode() %>"/><%=method.getName() %>
							</label>
							<%
					}
				%>
						</div>
		</div>
		</div>
		<div class="one_row_of_find_acount">
			<input type="text" name="retrieve_account_val" placeholder="邮箱或手机号"/>
			<div class="send_account_for_find_account_btn_container">
				<span class="common_blue_font common_hover" id="send_account_for_find_account_btn">发送账号信息</span>
			</div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>


<div class="modal fade" id="index_reset_pwd_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="index_reset_pwd_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="index_reset_pwd_dialog_label">重置密码</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<form id="index_reset_pwd_form">
      	<div id="reset_pwd_dialog_step_one_container">
      	<div class="one_row_of_reset_pwd">
      		<div class="one_row_of_reset_pwd_title">验证方式</div>
			<div id="one_row_of_reset_pwd_title_container">
				<div class="btn-group btn-group-toggle" data-toggle="buttons">
					<%
						for(VerifyUserMethod method:VerifyUserMethod.values()){
							if(method == VerifyUserMethod.UNDECIDED || method == VerifyUserMethod.ACCOUNT_PWD)
								continue;
					%>
							<label class="btn btn-outline-primary"> 
								<input type="radio" name="method" value="<%=method.getDbCode() %>"/><%=method.getName() %>
							</label>
							<%
					}
				%>
				</div>
			</div>
		</div>
		<div class="one_row_of_reset_pwd">
			<input type="text" name="account" placeholder="账号"/>
		</div>
		<div class="one_row_of_reset_pwd">
			<input type="text" name="verify_src" placeholder="邮箱或手机号"/>
			<div class="send_verify_code_for_reset_pwd_btn_container">
				<span class="common_blue_font common_hover" id="send_verify_code_for_reset_pwd_btn">发送验证码</span>
			</div>
		</div>

		
		
		</div>
		<div class="index_reset_pwd_container">
			<span class="one_row_of_reset_pwd_title">重置密码</span> 
			<span class="intex_control_reset_pwd_visibility_container"></span>
		</div>
		<div id="reset_pwd_dialog_step_two_container">
			<input type="text" name="verify_code" placeholder="验证码" />
			<input type="password" name="reset_pwd_val" placeholder="包含字母，数字，至少8位" />
		</div>
			
		<div id="commit_reset_pwd_btn">
			确认重置
		</div>
		
      	<div class="modal-footer">
        	<button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      	</div>
      	</form>
      </div>
    </div>
  </div>
</div>

</body>
</html>