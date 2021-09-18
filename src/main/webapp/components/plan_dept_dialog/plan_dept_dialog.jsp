<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<script src="${pageContext.request.contextPath}/components/plan_dept_dialog/plan_dept_dialog.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/components/plan_dept_dialog/plan_dept_dialog.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<div class="modal fade" id="plan_dept_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="plan_dept_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="plan_dept_dialog_label">历史欠账</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div class="plan_dept_dialog_main_container" id="plan_dept_dialog_surplus_main_container">
      		<div class="plan_dept_dialog_header">
      			<div class="plan_dept_dialog_title">盈余</div>
      			<span class="plan_dept_dialog_switch_container_visibility common_blue_font"></span>
      		</div>
      		<div class="plan_dept_dialog_content">
      		
      		</div>
      	</div>
      	<div class="plan_dept_dialog_main_container" id="plan_dept_dialog_credit_main_container">
      		<div class="plan_dept_dialog_header">
      			<div class="plan_dept_dialog_title">赊欠</div>
      			<span class="plan_dept_dialog_switch_container_visibility common_blue_font"></span>
      		</div>
      		<div class="plan_dept_dialog_content">
      		
      		</div>
      	</div>
      	<div class="plan_dept_dialog_for_modify_plan_dept_item">
      		修改后的名称如果和其它欠账项<em>同类同名</em>，系统将<em>合并两项</em><br/>修改值如果<em>不填写或为0</em>，系统将<em>删除该项</em>
      	</div>
      	<div id="plan_dept_dialog_logs_main_container" class="plan_dept_dialog_main_container">
      	     <div class="plan_dept_dialog_header">
      			<div class="plan_dept_dialog_title">日志</div>
      			<span class="plan_dept_dialog_switch_container_visibility common_blue_font"></span>
      		</div>
      		<div class="plan_dept_dialog_content">
      			<div id="plan_dept_dialog_plan_logs_container"></div>
      			<div id="plan_dept_dialog_plan_latest_update_time">更新于<span></span></div>
      		</div>
      	</div>
      </div>
      <div class="modal-footer">
      	<button type="button" class="btn btn-primary" id="close_plan_dept_edit_mode_btn">取消编辑</button>
       	<button type="button" class="btn btn-primary" id="open_plan_dept_edit_mode_btn">编辑</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
<div id="plan_dept_dialog_pattern_container" class="common_pattern_container">
	<div class="plan_dept_item_container">
		<span class="plan_dept_item_name"></span>
		<div class="plan_dept_item_val_and_type"></div>
		<div class="plan_dept_item_modify_container">
			<input name="plan_dept_item_name" type="text" placeholder="修改名称"/>
			<input name="plan_dept_item_val"  type="text" placeholder="修改值"/>
			<span class="plan_dept_modify_item_type"></span>
			<span class="common_blue_font common_hover plan_dept_save_modify_item_btn">保存</span>
		</div>
	</div>
</div>