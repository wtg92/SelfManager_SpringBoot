<%@page import="manager.system.career.PlanSetting"%>
<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<script src="${pageContext.request.contextPath}/components/tag_edit_dialog/tag_edit_dialog.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/components/tag_edit_dialog/tag_edit_dialog.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<div class="modal fade" id="tag_edit_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="tag_edit_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="tag_edit_dialog_label">标签管理</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div id="tag_edit_dialog_tag_list_container"></div>
		<div id="tag_edit_dialog_controlgroup_container">
      		<input type="text" id="tag_edit_dialog_add_tag_input" placeholder="不能重复" autocomplete="off">
      		<span id="tag_edit_dialog_add_tag_btn" class="common_blue_font common_hover">添加</span>
      	</div>
      </div>
      <div class="modal-footer">
      	<button type="button" id="tag_edit_dialog_reset_tags_btn" class="btn btn-primary">保存</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>



<div id="tag_edit_dialog_pattern_container" class="common_pattern_container">
	<div class="tag_unit_container">
		<span class="tag_unit_container_content"></span>
		<i class="tag_unit_close_icon common_hover">
			&times;
		</i>
	</div>
</div>