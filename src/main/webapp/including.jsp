<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="shortcut icon" href="<%=SM.BASIC_FILES_FOLDER%>web_logo.png" type="image/x-icon" />  

<script src="${pageContext.request.contextPath}/js/dependency/jquery-3.5.1.min.js" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/dependency/polyfill.min.js"></script>

<!-- jquery UI 和 bootstrap 有冲突 必须先于bootstrap引入  -->
<script src="${pageContext.request.contextPath}/js/dependency/jquery-ui.min.js" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/dependency/jquery-ui.min.css" type="text/css" rel="stylesheet" /> 

<script src="${pageContext.request.contextPath}/js/dependency/bootstrap.bundle.min.js" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/dependency/bootstrap.min.css" type="text/css" rel="stylesheet" /> 

<script src="${pageContext.request.contextPath}/js/dependency/datatables.min.js" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/dependency/datatables.min.css" type="text/css" rel="stylesheet" /> 

<script src="${pageContext.request.contextPath}/js/dependency/jquery.fancybox.min.js" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/dependency/jquery.fancybox.min.css" type="text/css" rel="stylesheet" /> 


<script src="${pageContext.request.contextPath}/js/config.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/util.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<script src="${pageContext.request.contextPath}/js/basic.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/basic.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" /> 


<script src="${pageContext.request.contextPath}/js/dependency/echarts.min.js" type="text/javascript" ></script>

<div id="including_pattern_container" class="common_pattern_container">
 	<svg width="1em" height="1em" viewBox="0 0 16 16" class="including_icon_eye_slash bi bi-eye-slash" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
  		<path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299l.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
  		<path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709z"/>
  		<path fill-rule="evenodd" d="M13.646 14.354l-12-12 .708-.708 12 12-.708.708z"/>
	</svg>
	
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="including_icon_eye bi bi-eye" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.134 13.134 0 0 0 1.66 2.043C4.12 11.332 5.88 12.5 8 12.5c2.12 0 3.879-1.168 5.168-2.457A13.134 13.134 0 0 0 14.828 8a13.133 13.133 0 0 0-1.66-2.043C11.879 4.668 10.119 3.5 8 3.5c-2.12 0-3.879 1.168-5.168 2.457A13.133 13.133 0 0 0 1.172 8z"/>
  		<path fill-rule="evenodd" d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
	</svg>
	
	
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_exclamation_mark bi bi-exclamation-circle" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
 		<path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
	</svg>
	
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_circle_arrow bi bi-arrow-counterclockwise" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M8 3a5 5 0 1 1-4.546 2.914.5.5 0 0 0-.908-.417A6 6 0 1 0 8 2v1z"/>
  		<path d="M8 4.466V.534a.25.25 0 0 0-.41-.192L5.23 2.308a.25.25 0 0 0 0 .384l2.36 1.966A.25.25 0 0 0 8 4.466z"/>
	</svg>
	
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="including_right_arrow bi bi-arrow-right" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h11.793l-3.147-3.146a.5.5 0 0 1 .708-.708l4 4a.5.5 0 0 1 0 .708l-4 4a.5.5 0 0 1-.708-.708L13.293 8.5H1.5A.5.5 0 0 1 1 8z"/>
	</svg>
	
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="including_avator bi bi-person-circle" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path d="M13.468 12.37C12.758 11.226 11.195 10 8 10s-4.757 1.225-5.468 2.37A6.987 6.987 0 0 0 8 15a6.987 6.987 0 0 0 5.468-2.63z"/>
  		<path fill-rule="evenodd" d="M8 9a3 3 0 1 0 0-6 3 3 0 0 0 0 6z"/>
  		<path fill-rule="evenodd" d="M8 1a7 7 0 1 0 0 14A7 7 0 0 0 8 1zM0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8z"/>
	</svg>
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_question_mark bi bi-question-circle" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
  		<path d="M5.255 5.786a.237.237 0 0 0 .241.247h.825c.138 0 .248-.113.266-.25.09-.656.54-1.134 1.342-1.134.686 0 1.314.343 1.314 1.168 0 .635-.374.927-.965 1.371-.673.489-1.206 1.06-1.168 1.987l.003.217a.25.25 0 0 0 .25.246h.811a.25.25 0 0 0 .25-.25v-.105c0-.718.273-.927 1.01-1.486.609-.463 1.244-.977 1.244-2.056 0-1.511-1.276-2.241-2.673-2.241-1.267 0-2.655.59-2.75 2.286zm1.557 5.763c0 .533.425.927 1.01.927.609 0 1.028-.394 1.028-.927 0-.552-.42-.94-1.029-.94-.584 0-1.009.388-1.009.94z"/>
	</svg>
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_modify_mark bi bi-pencil" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5L13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175l-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
	</svg>
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_add_mark bi bi-plus-circle" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
  		<path fill-rule="evenodd" d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4z"/>
	</svg>
	<svg width="1em" height="1em" viewBox="0 0 16 16" class="inluding_minus_mark bi bi-dash-circle" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
  		<path fill-rule="evenodd" d="M4 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 4 8z"/>
	</svg>
	
	<svg width="20px" height="20px" viewBox="0 0 16 16" class="inluding_circle_mark bi bi-circle-fill" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<circle cx="8" cy="8" r="8"/>
	</svg>
	
	<svg  width="30px" height="30px" viewBox="0 0 16 16" class="inluding_circle_with_left_line_mark bi bi-node-plus" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
  		<path fill-rule="evenodd" d="M11 4a4 4 0 1 0 0 8 4 4 0 0 0 0-8zM6.025 7.5a5 5 0 1 1 0 1H4A1.5 1.5 0 0 1 2.5 10h-1A1.5 1.5 0 0 1 0 8.5v-1A1.5 1.5 0 0 1 1.5 6h1A1.5 1.5 0 0 1 4 7.5h2.025zM11 5a.5.5 0 0 1 .5.5v2h2a.5.5 0 0 1 0 1h-2v2a.5.5 0 0 1-1 0v-2h-2a.5.5 0 0 1 0-1h2v-2A.5.5 0 0 1 11 5zM1.5 7a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z"/>
	</svg>

	<div class="including_log_container">
		<div class="common_log_content"></div>
		<div class="common_log_footer">
			<div class="common_log_creator_name"></div>
			<div class="common_log_time"></div>
		</div>
	</div>

</div>
<div class="modal fade" id="including_alert_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="including_alert_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="including_alert_dialog_label"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="including_confirm_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="including_confirm_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="including_confirm_dialog_label"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary positive_event" data-dismiss="modal" ></button>
        <button type="button" class="btn btn-secondary negative_event" data-dismiss="modal"></button>
      </div>
    </div>
  </div>
</div>


<div class="modal fade" id="including_confirm_dialog_2" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="including_confirm_dialog_2_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="including_confirm_dialog_2_dialog_label"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body"></div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary positive_event" data-dismiss="modal" ></button>
        <button type="button" class="btn btn-secondary negative_event" data-dismiss="modal"></button>
      </div>
    </div>
  </div>
</div>
