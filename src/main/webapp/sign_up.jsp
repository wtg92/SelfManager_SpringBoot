<%@page import="manager.system.UserUniqueField"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<link href="${pageContext.request.contextPath}/css/sign_up.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/sign_up.js?<%= SM.VERSION %>" type="text/javascript"></script>
<title><%=SM.WEB_TITLE %></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div id="sign_up_main_container" class="common_main_container">
		<div id="sign_up_header_container">
			欢迎注册<%=SM.BRAND_NAME %>
		</div>
		<div id="sign_up_information_container">
			<form>
			<div>
				<div class="sign_up_entity_for_sign_up">
					<div class="sign_up_title">账号</div>
					<input placeholder="5-20个以字母开头，可带数字、下划线" name="account" type="text"  field="<%=UserUniqueField.ACCOUNT.getDbCode()%>"/>
					<div class="sign_up_error_container"></div>
				</div>		
				<div class="sign_up_entity_for_sign_up">
					<div class="sign_up_pwd_container">
						<span >密码</span> 
						<span class="sign_up_control_pwd_visibility_container"></span>
					</div>
					<input placeholder="包含字母，数字，至少8位" name="pwd" type="password" />
					<div class="sign_up_error_container"></div>
				</div>
				<div class="sign_up_entity_for_sign_up">
					<div  class="sign_up_title">昵称</div>
					<input placeholder="1-10个字符" name="nick_name" type="text" field="<%=UserUniqueField.NICK_NAME.getDbCode()%>"/>
					<div class="sign_up_error_container"></div>
				</div>
				<div class="sign_up_entity_for_sign_up gender">
					<div>性别</div>
					<div id="sign_up_switch_to_select_gender">
					<div class="btn-group btn-group-toggle" data-toggle="buttons">
							<%
					for(Gender gender:Gender.values()){
						if(gender == Gender.UNDECIDED)
							continue;
						%>
							<label class="btn btn-outline-primary"> <input
								type="radio" name="gender"
								value="<%=gender.getDbCode() %>"><%=gender.getName() %>
							</label>
							<%
					}
				%>
						</div>
					
					</div>
				</div>
				<div class="sign_up_entity_for_sign_up eamil_or_tel">
					<div class="email_or_tel_inputs_container">
						<div  class="sign_up_title">邮箱</div>
						<div class="sign_up_option_for_email_or_tel">
							<input id="sign_up_option_for_email" type="checkbox" name="email_null">
							<label for="sign_up_option_for_email" class="sign_up_hint_for_email_or_tel common_blue_font common_hover">暂时不想填写</label>
						</div>
					</div>
					<input name="email" type="text" placeholder="请至少验证一个邮箱或手机号"  field="<%=UserUniqueField.EMAIL.getDbCode()%>"/>
					<div class="sign_up_error_container"></div>
					<div class="sign_up_send_verify_code_container" for_email="true">
						<span id="sign_up_send_email_verfy_code_button">发送验证码</span>
						<input type="text" name="email_verify_code" placeholder="验证码"/>
					</div>
					<div class="sign_up_error_container_for_verify_code"></div>
				</div>
				<div class="sign_up_entity_for_sign_up eamil_or_tel">
					<div class="email_or_tel_inputs_container">
						<div  class="sign_up_title">手机</div>
						<div class="sign_up_option_for_email_or_tel">
							<input id="sign_up_option_for_tel" type="checkbox" name="tel_null" >
							<label for="sign_up_option_for_tel" class="sign_up_hint_for_email_or_tel common_blue_font common_hover">暂时不想填写</label>
						</div>
					</div>
					<input name="tel" type="text" placeholder="请至少验证一个邮箱或手机号" field="<%=UserUniqueField.TEL_NUM.getDbCode()%>" />
					<div class="sign_up_error_container"></div>
					<div class="sign_up_send_verify_code_container" for_email="false">
						<span id="sign_up_send_tel_verfy_code_button">发送验证码</span>
						<input type="text" name="tel_verify_code" placeholder="验证码"/>
					</div>
				</div>		
			</div>
			<div id="sign_up_commit_button">注    册</div>
			</form>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	
<div class="modal fade" id="sign_up_img_drag_check_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="sign_up_img_drag_check_dialog_label" aria-hidden="true">
  <input type="hidden" id="sign_up_yzm_src" />
  <input type="hidden" id="sign_up_yzm_for_email"/>
  <input type="hidden" id="sign_up_yzm_email_or_tel"/>
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="sign_up_img_drag_check_dialog_label">请完成图片验证</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div id="sign_up_yzm_hint_when_loading">验证码努力加载中，请稍候......</div>
      	<div id="sign_up_yzm_background_img">
      	    <img id="sign_up_yzm_cut_img"/>
      	</div>
      	<div class="verify-bar-area">
      	   <span class="verify-msg"></span>
      	   <div class = "verify-left-bar">
      	   		<span class="verify-msg"></span>
      	   		<div class="verify-move-block">
      	   			<div id="sign_up_rignt_arrow"></div>
      	   		</div>
      	   </div>
      	</div>
      </div>
    </div>
  </div>
</div>
	
	
</body>
</html>