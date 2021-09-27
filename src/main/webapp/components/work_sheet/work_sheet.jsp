<%@page import="manager.system.career.WorkSheetState"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<script src="${pageContext.request.contextPath}/components/work_sheet/work_sheet.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/components/work_sheet/work_sheet.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<div id="work_sheet_main_container">
 	<div id="work_sheet_main_container_header">
 		<div class="work_sheet_main_container_header_date_title"></div>
 		<div class="work_sheet_main_container_header_right_body">
 		 	<div class="work_sheet_main_container_header_state">
				<span class="common_state_block"></span> 			
 			</div>
 			<span class="work_sheet_main_container_header_mood" title="当日平均心情，按时间比例计算"></span>
 			<div class="work_sheet_main_container_header_base_plan">基于<em></em></div>

 			<div class="work_sheet_main_ctrogroup_buttons">
 				<span class="work_sheet_main_container_assume_finsihed common_blue_font common_hover"><%=WorkSheetState.NO_MONITOR.getName()%></span>
 				<span class="work_sheet_main_container_cancel_assumen_finished common_blue_font common_hover">取消<%=WorkSheetState.NO_MONITOR.getName()%></span>
 				<span class="work_sheet_main_container_delete_button common_blue_font common_hover">删除</span>
 			 	<span class="work_sheet_switch_to_show_ws_note common_blue_font common_hover"></span>
 			 </div>
 		</div>
 	</div>
	<div id="work_sheet_main_container_for_basic_info">
		<textarea id="work_note_textarea"></textarea>
		<div id="work_save_ws_rlt_container"></div>
	</div>
	<div id="work_sheet_container_with_header_and_content">
 	<div id="work_sheet_main_container_header">
 		<div class="work_sheet_main_container_today_plan_title"></div>
 		<div class="work_sheet_main_container_today_plan_right_body">
 			<span class="work_sheet_main_container_switch_to_completion_mode common_blue_font common_hover">看<span>完成情况</span></span>
 			<span class="work_sheet_main_container_switch_to_plan_mode common_blue_font common_hover">看<span>计划</span></span>
 			<span class="work_sheet_main_container_sync_all_plan_item common_blue_font common_hover">同步所有</span>
 			<span class="work_sheet_main_container_open_plan_edit_mode common_blue_font common_hover">编辑</span>
 			<span class="work_sheet_main_container_close_plan_edit_mode common_blue_font common_hover">取消编辑</span>
 			<span class="work_sheet_switch_to_show_today_plan_main_container common_blue_font common_hover"></span>
 		</div>
 	</div>
	<div id="work_sheet_today_play_main_container" open_edit_mode='false'>
      	<div id="work_sheet_today_plan_items_cards_container"></div>
      	<div id="work_sheet_plan_completion_hint">
      		算式求出的值，代表仅以该项计算，还与计划差多少
      	</div>
      	<div id="work_sheet_today_plan_control_group_container">
      		<input type="hidden" id="work_sheet_item_id_when_modify_item">
      		<div class="work_sheet_today_plan_items_for_one_row">
      			<div  class="work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_sub_title">名称</span>
      				<input type="text" name="cat_name" placeholder="不能与已有重复"/>
      				<input type="text" name="cat_name_when_modify" placeholder="不能与已有重复"/>
      			</div>
      			<div class="work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_sub_title">类型</span>
      				<div class="btn-group btn-group-toggle work_sheet_today_plan_type_chooser" data-toggle="buttons" title="计划类型">
      					<label class="btn btn-outline-primary common_btn_small_padding"> 
      						<input type="radio" name="cat_type" minutes 
      						value="<%=PlanItemType.MINUTES.getDbCode()%>"><%=PlanItemType.MINUTES.getName()%>
						</label>
      					<label class="btn btn-outline-primary common_btn_small_padding"> 
      						<input type="radio" name="cat_type" times value="<%=PlanItemType.TIMES.getDbCode()%>"><%=PlanItemType.TIMES.getName()%>
						</label>
      				</div>
      				<div class="work_sheet_today_plan_type_when_modifying common_blue_font"></div>
      			</div>
      			<div class="work_sheet_today_plan_unit_container" id="work_sheet_today_plan_father_and_son_relationship_container">
  					<div class="work_sheet_today_plan_items_sub_title">从属</div>
					<div class="btn-group dropdown" name="father_relation">
 						<button type="button" id="work_sheet_today_plan_father_relation_btn" father_id="0" class="btn btn-primary dropdown-toggle common_btn_small_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">无</button>
 						<div class="dropdown-menu"></div>
					</div>
					<div class="work_sheet_today_plan_subordinate_when_modifying common_blue_font"></div>
      			</div>
      		</div>
      		<div class="work_sheet_today_plan_items_error_mes items_header"></div>
      		<div class="work_sheet_today_plan_items_for_one_row work_sheet_today_plan_items_values_container">
      			<div id="work_sheet_today_plan_items_inputs_for_minutes_type" class="work_sheet_today_plan_items_inputs_by_type_container work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_prefix_title">投入</span>
      				<input type="text" name="value_by_minutes" placeholder="只允许整数"/>
      				<span class="work_sheet_today_plan_items_suffix_title">分钟</span>
      			</div>
      			<div id="work_sheet_today_plan_items_inputs_for_times_type" class="work_sheet_today_plan_items_inputs_by_type_container work_sheet_today_plan_unit_container">
      				<div class="work_sheet_today_plan_items_unit">
      					<span class="work_sheet_today_plan_items_prefix_title">投入</span>
      					<input type="text" name="value_by_times" placeholder="只允许整数"/>
      					<span class="work_sheet_today_plan_items_suffix_title">次</span>
      				</div>
      			</div>
      			<div id="work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes" class="work_sheet_today_plan_items_inputs_by_type_container work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_prefix_title">关系</span>
      				<em>1分钟</em><em><span class="work_sheet_today_plan_items_son_title"></span></em>换算<input type="text" name="mapping_val_for_same_type" placeholder="允许小数" />分钟<em><span class="work_sheet_today_plan_items_father_title"></span></em>
      			</div>
      			<div id="work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times" class="work_sheet_today_plan_items_inputs_by_type_container work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_prefix_title">关系</span>
      				<em>1次</em>
      				<em><span class="work_sheet_today_plan_items_son_title"></span></em>
      				换算<input type="text" name="mapping_val_for_same_type" placeholder="允许小数" />次
      				<em><span class="work_sheet_today_plan_items_father_title"></span></em>
      			</div>
      			<div id="work_sheet_today_plan_items_inputs_for_different_type_mapping" class="work_sheet_today_plan_items_inputs_by_type_container work_sheet_today_plan_unit_container">
      				<span class="work_sheet_today_plan_items_prefix_title">关系</span>
      				<em>1次</em>
      				<em><span class="work_sheet_today_plan_items_inputs_of_times_type"></span></em>
      				换算
      				<input type="text" name='mapping_val_for_differ_type' placeholder="只允许整数"/>分钟
      				<em><span class="work_sheet_today_plan_items_inputs_of_minutes_type"></span></em>
      			</div>
      		</div>
      		<div class="work_sheet_today_plan_items_error_mes for_different_type"></div>
      		<div class="work_sheet_today_plan_items_for_one_row">
      			<span class="work_sheet_today_plan_items_sub_title">备注</span>
      			<textarea type="text" name="note"></textarea>
      		</div>
      		<div class="work_sheet_today_plan_items_for_hint">修改只会对当日计划生效</div>
      		<div class="work_sheet_today_plan_items_controgroup_container">
      			<div id="work_sheet_today_plan_save_or_add_item_hint_mes"></div>
      			<span id="work_sheet_today_plan_save_item_button" class="btn btn-success common_btn_small_padding">保存</span>
      			<span id="work_sheet_today_plan_add_item_button"  class="btn btn-success common_btn_small_padding">添加</span>
      		</div>
      </div>
	</div>
	</div>
	<div id="work_sheet_work_items_container_with_header_and_body">
 		<div id="work_sheet_work_items_container_header">
 			<div class="work_sheet_work_items_container_title">工作项</div>
 			<div class="work_sheet_work_items_container_control_buttons">
 				<span class="work_sheet_main_container_open_ws_statistics common_blue_font common_hover">当日统计</span>
 				<span class="work_sheet_main_container_show_all_work_items_note common_blue_font common_hover">显示所有备注</span>
 				<span class="work_sheet_main_container_hide_all_work_items_note common_blue_font common_hover">隐藏所有备注</span>
 				
 				<span class="work_sheet_main_container_open_work_items_edit_mode common_blue_font common_hover">编辑（ctrl+q）</span>
 				<span class="work_sheet_main_container_close_work_items_edit_mode common_blue_font common_hover">取消编辑（ctrl+q）</span>
 				<span class="work_sheet_switch_to_show_work_items_main_body common_blue_font common_hover"></span>
 			</div>
 		</div>
 		<div id="work_sheet_work_items_container_main_body">
 			<div id="work_sheet_work_items_container_main_body_ws_items">
 			</div>
 			<div id="work_sheet_work_items_container_main_body_save_hint">修改完成后，<em><span class="save_hint_seconds"></span>秒内自动保存工作项信息</em>，保存前请不要切换工作表，以防丢失信息。最新保存时间 <span class="save_hint_update_time"></span></div>
 			<div id="work_sheet_work_items_container_main_body_controgroup">
 				<div class="work_sheet_work_items_container_main_body_controgroup_hint">您还可以通过<em>点击上方计划项</em>来添加工作项</div>
				<div class="btn-group dropdown" name="add_work_item_by_plan_item">
 					<button type="button" title="可以通过直接点击计划项添加" class="btn btn-primary dropdown-toggle common_btn_small_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 						添加
 					</button>
 					<div class="dropdown-menu"></div>
				</div>
 			</div>
 		</div>
	</div>
	<div id="work_sheet_logs_main_container">
      	     <div class="work_sheet_logs_header">
      			<div class="work_sheet_logs_title">日志</div>
      			<span class="work_sheet_logs_switch_container_visibility common_blue_font"></span>
      		</div>
      		<div class="work_sheet_logs_content">
      			<div id="work_sheet_logs_container"></div>
      			<div id="work_sheet_latest_update_time">更新于<span></span></div>
      		</div>
    </div>
</div>

<div id="work_sheet_pattern_container" class="common_pattern_container">
	<div class="work_sheet_work_item_container">
		<div class="work_sheet_work_item_unit_container_for_phone em_of_desc_without_margin"></div>
		<div class="work_sheet_work_item_unit_container">
			<div class="work_sheet_work_item_unit_left_container">
			<span class="work_sheet_work_item_container_start_time_span" title="开始时间" ></span>
			<input type="time" name="start_time" title="开始时间" >
			<span class="work_sheet_work_item_container_plan_item" title="计划项"></span>
			<span class="work_sheet_work_item_container_calculate_info" title="完成情况">
				<span class="work_sheet_work_item_container_calculate_info_start" title="工作项开始时，计划项的剩余值"></span>
				<span class="work_sheet_work_item_container_calculate_info_mark work_sheet_work_item_container_math_mark" title="当认为做了不该做的事时，可以增加计划值"></span>
				<span class="work_sheet_work_item_container_calculate_info_val" title="用于消耗计划的值"></span>
				<input type="text" name="val" title="用于消耗计划的值" autocomplete="off" />
				<span class="work_sheet_work_item_container_math_mark">=</span>
				<span class="work_sheet_work_item_container_calculate_info_rlt" title="工作项结束时，计划项的剩余值"></span>
				<span class="work_sheet_work_item_container_calculate_info_type_name"></span>
				<span class="work_sheet_work_item_calculate_val_by_end_time_btn common_blue_font common_hover" title="当类型为时间时，由结束时间自动计算；当类型为次数时，默认为1">计算</span>
			</span>
			<span class="work_sheet_work_item_end_time_container">
				<span class="work_sheet_work_item_container_end_time_span"  title="结束时间"></span>
				<input type="time" name="end_time" title="结束时间" >
				<!-- 只有当类型是时间 才可用 -->
				<span class="work_sheet_work_item_calculate_end_time_by_val_btn common_blue_font common_hover" title="由持续时间自动计算">计算</span>
				<span class="work_sheet_work_item_calculate_end_time_by_now common_blue_font common_hover" title="可以手动修改，与工作项值不要求对应">结束</span>
			</span>
			<span class="work_sheet_work_item_container_mood" title="心情"></span>
			</div>
			<span class="work_sheet_work_item_container_switch_to_show_note common_blue_font common_hover"></span>
			<span class="work_sheet_work_item_container_delete_button close">&times;</span>
		</div>
		<textarea class="work_sheet_work_item_container_note" name="note"></textarea>
		<div class="work_sheet_work_item_container_note_container">
			<div class="work_sheet_work_item_container_note_body"></div>
		</div>
	</div>
	
	<div class="work_sheet_work_item_of_dept_container">
		<span class="work_sheet_work_item_of_dept_time" title="同步时间" ></span>
		<span class="work_sheet_work_item_of_dept_plan_item" title="计划项"></span>
		<span class="work_sheet_work_item_of_dept_plan_item_mark">
			<span>同步</span>
		</span>
		<span class="work_sheet_work_item_container_calculate_info" title="完成情况">
			<span class="work_sheet_work_item_container_calculate_info_start" title="同步前，计划项的剩余值"></span>
			<span class="work_sheet_work_item_of_dept_calculate_info_mark work_sheet_work_item_container_math_mark"></span>
			<span class="work_sheet_work_item_of_dept_calculate_info_val" title="同步值"></span>
			<span class="work_sheet_work_item_container_math_mark">=</span>
			<span class="work_sheet_work_item_container_calculate_info_rlt" title="同步后，计划项的剩余值"></span>
			<span class="work_sheet_work_item_container_calculate_info_type_name"></span>
		</span>

	</div>
	<div class="work_sheet_plan_item_container_root_container">
		<div class="work_sheet_plan_item_circle_container"></div>
		<div class="work_sheet_plan_item_container_root_controlgroup">
			<div title="添加" class="work_sheet_plan_item_root_add_mark"></div>
		</div>
	</div>
	
	<div class="work_sheet_plan_item_container_unit_level">
		<div class="work_sheet_plan_item_container_unit_header">
			<div></div>
		</div>
		<div  class="work_sheet_plan_item_container_wrap">
			<div class="work_sheet_plan_item_container_sub_container">
				<div class="work_sheet_plan_item_container_body"></div>
				<div class="work_sheet_plan_item_completion_body">
				
				</div>
				<div class="work_sheet_plan_item_container_footer">
					<div class="work_sheet_plan_item_add_mark" title="添加"></div>
					<div class="work_sheet_plan_item_delete_mark" title="删除"></div>
					<div class="work_sheet_plan_item_modify_mark" title="修改"></div>
					<div class="work_sheet_plan_sync_completion_to_dept common_hover common_blue_font">同步历史欠账</div>
				</div>
				
				<div class="work_sheet_plan_item_container_fold_info_container">
					<div class="work_sheet_plan_item_fold_btn" title="折叠"></div>
					<div class="work_sheet_plan_item_unfold_btn" title="打开"></div>
					<div class="work_sheet_plan_item_save_fold_info common_blue_font common_hover">保存</div>
					<div class="work_sheet_plan_item_save_fold_hint"></div>
				</div>
				
			</div>
		</div>
	</div>
	<div class="work_sheet_plan_item_container_first_level">
		<div class="work_sheet_plan_item_container_first_level_header">
			<div></div>
		</div>
		<div  class="work_sheet_plan_item_container_wrap">
			<div class="work_sheet_plan_item_container_sub_container">
				<div class="work_sheet_plan_item_container_body"></div>
				<div class="work_sheet_plan_item_completion_body"></div>
				<div class="work_sheet_plan_item_container_footer">
					<div class="work_sheet_plan_item_add_mark" title="添加"></div>
					<div class="work_sheet_plan_item_delete_mark" title="删除"></div>
					<div class="work_sheet_plan_item_modify_mark" title="修改"></div>
					<div class="work_sheet_plan_sync_completion_to_dept common_hover common_blue_font">同步历史欠账</div>
				</div>
				
				<div class="work_sheet_plan_item_container_fold_info_container">
					<div class="work_sheet_plan_item_fold_btn" title="折叠"></div>
					<div class="work_sheet_plan_item_unfold_btn" title="打开"></div>
					<div class="work_sheet_plan_item_save_fold_info common_blue_font common_hover">保存</div>
					<div class="work_sheet_plan_item_save_fold_hint"></div>
				</div>
				
			</div>
		</div>
	</div>
	
</div>