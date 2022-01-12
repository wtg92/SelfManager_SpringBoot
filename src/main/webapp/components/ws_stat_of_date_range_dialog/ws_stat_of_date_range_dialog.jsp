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
      			<div id="ws_stat_switch_stat_mode_container">
      				<label for="ws_stat_mode_of_plan_group" class="common_hover">按计划</label>
      				<input type="radio"  name="ws_stat_mode" id="ws_stat_mode_of_plan_group" group_by="plan">
      				<label for="ws_stat_mode_of_tag_group" class="common_hover">按标签</label>
      				<input type="radio"  name="ws_stat_mode" id="ws_stat_mode_of_tag_group" group_by="tag">
      			</div>
      			
      		</div>
      		<div id="ws_stat_content_container">
      			<div id="ws_stat_content_container_of_text">
      				<div class="ws_stat_content_container_of_text_lefter">
      					<span class="stat_date_range"></span>共有<em class="count_for_search_date_range"></em>天，其中<em class="count_for_stat_days"></em>天开启了工作表。 
      				</div>
      				<div class="ws_stat_content_container_of_text_righter" >
      					<span class="ws_stat_switch_to_show_more_info common_blue_font common_hover"></span>
      				</div>
      			</div>
      			<div id="ws_stat_content_container_of_more_info_container">
      				<div class="ws_stat_group_by_plan"></div>
      				<div class="ws_stat_group_by_tag"></div>
      			</div>
      			<div id="ws_stat_content_container_of_charts_for_plan_and_state_distribution">
      			    <div id="ws_stat_content_container_of_charts_for_ws_state_distribution" title="工作表状态分布情况"></div>
      				<div id="ws_stat_content_container_of_charts_for_finish_situation" title="工作实际完成情况（刨除同步项）"></div>
      			</div>
      			<div id="ws_stat_content_container_of_charts_for_type_count_finish_situation">
      				<div id="ws_stat_content_container_of_charts_for_distribution"></div>
      				<div id="ws_stat_content_container_of_charts_for_type_count"></div>
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
<div id="ws_stat_pattern_container" class="common_pattern_container">
	
	<div class="ws_stat_unit_for_one_plan">
		<div class="ws_stat_unit_for_one_plan_header">
			<div class="ws_stat_unit_for_one_plan_header_plan_name"></div>
			<div class="ws_stat_unit_for_one_plan_header_count_days"><em></em>天</div>
			<div class="ws_stat_unit_for_one_plan_header_switch_to_show_btn common_blue_font common_hover"></div>
		</div>
		<div class="ws_stat_unit_for_one_plan_body">
			<div class="ws_stat_unit_for_one_plan_body_week_info"><em class="count_days_for_saturday"></em>个星期六，<em class="count_days_for_sunday"></em>个星期天</div>
			<div class="ws_stat_container_for_one_plan_items_parent">
				<div class="ws_stat_container_for_one_plan_items">
					
				</div>
			</div>
		
		</div>
	</div>
	
	<div class="ws_stat_unit_for_one_item">
		<div class="ws_stat_unit_for_one_item_name"></div>
		<div class="ws_stat_unit_for_one_item_stat_container">
			<div class="ws_stat_unit_for_one_item_sum_value">
				<span>总计</span>
				<em></em>
			</div>
			<div class="ws_stat_unit_for_one_item_median_value">
				<span>中位数</span>
				<em></em>
			</div>
			<div class="ws_stat_unit_for_one_item_avg_value">
				<span>平均数</span>
				<em></em>
			</div>
			<div class="ws_stat_unit_for_one_item_std_dev_value">
				<span>标准差</span>
				<em></em>
			</div>
		</div>
	</div>
	
</div>