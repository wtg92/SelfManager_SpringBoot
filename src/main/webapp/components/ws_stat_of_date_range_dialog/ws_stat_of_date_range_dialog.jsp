<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<script src="${pageContext.request.contextPath}/components/ws_stat_of_date_range_dialog/ws_stat_of_date_range_dialog.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/components/ws_stat_of_date_range_dialog/ws_stat_of_date_range_dialog.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<div class="modal fade" id="ws_stat_of_date_range_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="ws_stat_of_date_range_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="ws_stat_of_date_range_dialog_label">时间范围分析</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      		<div id="ws_stat_controlgroup_container">
      			<div id="ws_stat_controlgroup_container_real_terms">
      				<div id="ws_stat_controlgroup_container_real_terms_lefter">
      					<input type="date" name="ws_stat_start_date" title="分析起止日期" placeholder="分析起始日期"/>
      					<span>到</span>
      					<input type="date" name="ws_stat_end_date" title="分析结束日期" placeholder="分析截止日期"/>
      				</div>
      				<div id="ws_stat_controlgroup_container_real_terms_righter">
      					<span class="common_blue_font common_hover" id="ws_stat_analyze_ws_btn">分析</span>
      				</div>
      			</div>
      			<div id="ws_stat_controlgroup_container_quick_terms" class="btn-group" role="group">
      				<button class="btn btn-outline-primary" id="ws_stat_controlgroup_container_quick_terms_this_week">本周</button>
      				<button class="btn btn-outline-primary" id="ws_stat_controlgroup_container_quick_terms_this_month">本月</button>
      				<button class="btn btn-outline-primary" id="ws_stat_controlgroup_container_quick_terms_this_quarter">本季度</button>
      				<button class="btn btn-outline-primary" id="ws_stat_controlgroup_container_quick_terms_this_year">本年度</button>
      			</div>
      		</div>
      		<div id="ws_stat_content_container">
      			<div id="ws_stat_content_container_of_text">
      				<span class="stat_date_range"></span>共有<em class="count_for_search_date_range"></em>天，其中<em class="count_for_stat_days"></em>天开启了工作表。 
      			</div>
      			<div id="ws_stat_content_container_of_charts_for_plan_and_state_distribution">
      			    <div id="ws_stat_content_container_of_charts_for_ws_state_distribution" title="工作表状态分布情况"></div>
      				<div id="ws_stat_content_container_of_charts_for_finish_situation" title="工作实际完成情况（刨除同步项）"></div>
      			</div>
      			
      			<div id="ws_stat_content_container_of_charts_for_type_count_finish_situation">
      				<div id="ws_stat_content_container_of_charts_for_plan_distribution" title="工作表使用计划分布情况"></div>
      				<div id="ws_stat_content_container_of_charts_for_type_count"  title="工作项总计时分布情况"></div>
      			</div>
      			<div id="ws_stat_content_container_of_charts_average_moods"></div>
      		</div>
      </div>
      <div class="modal-footer">
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