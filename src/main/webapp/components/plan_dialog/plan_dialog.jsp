<%@page import="manager.system.career.PlanSetting"%>
<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<script src="${pageContext.request.contextPath}/components/plan_dialog/plan_dialog.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/components/plan_dialog/plan_dialog.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<div class="modal fade" id="plan_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="plan_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="plan_dialog_label"></h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div id="plan_dialog_basic_info_main_container" class="plan_dialog_main_container">
      		<div class="plan_dialog_header">
      			<div class="plan_dialog_title">基本信息</div>
      			<span class="plan_dialog_switch_container_visibility common_blue_font common_hover"></span>
      		</div>
      		<div class="plan_dialog_content">
      			<form id="plan_dialog_basic_info_form">
      			<div id="plan_dialog_basic_info_sub_container">
    					<div class="plan_dialog_basic_info_form_one_row">
    						<div  class="plan_dialog_basic_unit_container">
    							<span class="plan_dialog_basic_info_sub_title">名称</span>
    							<input type="text" name="name"/>		
    						</div>
    						<div class="plan_dialog_basic_unit_container">
    							<span class="plan_dialog_basic_info_sub_title">状态</span>
    							<span class="plan_dialog_basic_info_state common_state_block"></span>
    						</div>
    					</div>
    					<div class="plan_dialog_basic_info_form_error_mes p_name"></div>
    					<div class="plan_dialog_basic_info_form_one_row">
    						<div class="plan_dialog_basic_unit_container">
    							<span class="plan_dialog_basic_info_sub_title">开始</span>
    							<input type="date" name="start_date"/>		
    						</div>
    						<div class="plan_dialog_basic_unit_container">
    							<span class="plan_dialog_basic_info_sub_title">结束</span>
    							<input type="date" name="end_date"/>
    						</div>
					<div title="在重新设置结束日期前，该计划将一直被系统认为进行中" id="work_end_date_null_for_save_plan" class="common_blue_font">暂时没想好</div>
    					</div>
    					<div class="plan_dialog_basic_info_form_error_mes p_date"></div>
    					
    					<div class="plan_dialog_basic_info_form_one_row">
    						<div class="plan_dialog_basic_unit_container copy_plan_items_relative">
    							<span class="plan_dialog_basic_info_sub_title" title="用于计划的更改、复制">ID</span>
    							<span class="plan_dialog_basic_info_decoded_id" title="用于计划的更改、复制"></span>
    							<span class="plan_dialog_basic_info_copy_btn common_blue_font common_hover">复制</span>
    							<span class="plan_dialog_basic_info_copy_hint common_blue_font common_hover"></span>
    						</div>
    						<div class="plan_dialog_basic_unit_container copy_plan_items_relative">
    							<span class="plan_dialog_basic_info_sub_title">允许他人根据ID复制计划项</span>
    							<input type="checkbox" title="当勾选后，非本人的用户可以通过ID复制计划项"  class="allow_others_copy_plan_items" name="plan_setting" value="<%=PlanSetting.ALLOW_OTHERS_COPY_PLAN_ITEMS.getDbCode() %>"/>
    							<span class="plan_dialog_basic_allow_others_copy_plan_items_rlt"></span>
    						</div>
    					</div>
    					
    					<div class="plan_dialog_basic_info_form_one_row plan_tag" title="用于时间范围的数据分析">
   							<span class="plan_dialog_basic_info_sub_title">标签</span>
   							<span id="plan_dialog_basic_unit_container_edit_btn" class="common_blue_font common_hover"></span>
   							<div id="plan_dialog_tag_righer">
    							<div class="plan_dialog_basic_unit_container_plan_tags"></div>
    							
   								<div class="plan_dialog_sync_plan_tags_to_ws_container">
   									<span id="plan_dialog_sync_plan_tags_to_ws_container_btn" class="common_blue_font common_hover">同步相关工作表</span>
   									<div id="plan_dialog_sync_plan_tags_desc" class="common_hover" data-placement="top" data-toggle="popover" title="相关工作表的标签同步" data-html="true"
 				 						data-content="a. 同步时，对于<em>基于该计划</em>生成的<em>所有</em>工作表，系统会将标签重置为计划标签。<br/>b. 为了尽量<em>保留用户手动修改的工作表痕迹</em>，系统通过计划标签和工作表标签的文字比较，<em>尽可能地识别出用户手动设置的工作表标签</em>，将其保留，其余，直接替换">说明</div>
   								</div>
   							</div>
    					</div>
    					
				<div class="plan_dialog_basic_info_form_one_row">
    						<div class="plan_dialog_basic_unit_container">
    							<span class="plan_dialog_basic_info_sub_title">顺序权重</span>
    							<span class="plan_dialog_basic_info_sub_title_seq_weight_span"></span>
    							<input type="text" name="seq_weight"/>
    							<div class="plan_dialog_basic_info_sub_seq_weight_hint">非负整数，决定计划卡片的显示顺序，值越大越靠前</div>
    						</div>
    					</div>
    					<div class="plan_dialog_basic_info_form_one_row">
    						<textarea name="note"></textarea>
    					</div>
      			</div>
      			<div id="plan_dialog_save_basic_controgroup">
      				<div id="plan_dialog_save_basic_hint_mes"></div>
      				<div class="plan_dialog_save_basic_recalculate_controgroup">
      					<input type="checkbox" name="recalculate_state" value="true" id="plan_dialog_recalculate_plan_state" title="系统会根据开始日期和结束日期重新计算状态，即便它已经<%=PlanState.ABANDONED.getName()%>或 <%=PlanState.FINISHED.getName()%>" checked="checked"/>
      					<label for="plan_dialog_recalculate_plan_state" title="系统会根据开始日期和结束日期重新计算状态，即便它已经 <%=PlanState.ABANDONED.getName()%> 或 <%=PlanState.FINISHED.getName()%>">重新计算状态</label>
      				</div>
      				<div id="plan_dialog_save_plan_basic_info_button" class="btn btn-success common_btn_small_padding">保存</div>
      			</div>
      			</form>
      		</div>
      	</div>
      	<div id="plan_dialog_items_main_container" class="plan_dialog_main_container">
      		<div class="plan_dialog_header">
      			<div class="plan_dialog_title">每日计划项</div>
      			<span class="plan_dialog_switch_container_visibility common_blue_font common_hover"></span>
      		</div>
      		<div class="plan_dialog_content">
      			<div id="plan_dialog_items_cards_container">
      			
      			</div>
      			<div id="plan_dialog_items_control_group_container">
      				<input type="hidden" id="plan_dialog_item_id_when_modify_item">
      				<div class="plan_dialog_items_form_one_row">
      					<div  class="plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_sub_title">名称</span>
      						<input type="text" name="cat_name" placeholder="不能重复" autocomplete="off"/>
      						<input type="text" name="cat_name_when_modify" placeholder="不能重复" autocomplete="off"/>
      					</div>
      					<div class="plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_sub_title">类型</span>
      						<div class="btn-group btn-group-toggle plan_dialog_type_chooser" data-toggle="buttons" title="计划类型">
      							<label class="btn btn-outline-primary common_btn_small_padding"> <input
									type="radio" name="cat_type" minutes
									checked="checked"
									value="<%=PlanItemType.MINUTES.getDbCode()%>"><%=PlanItemType.MINUTES.getName()%>
								</label>
      							<label class="btn btn-outline-primary common_btn_small_padding"> <input
									type="radio" name="cat_type" times
									value="<%=PlanItemType.TIMES.getDbCode()%>"><%=PlanItemType.TIMES.getName()%>
								</label>
      						</div>
      						<div class="plan_dialog_type_when_modifying common_blue_font"></div>
      					</div>
      					<div class="plan_dialog_items_unit_container" id="plan_dialog_items_father_and_son_relationship_container">
  							<div class="plan_dialog_items_sub_title">从属</div>
							<div class="btn-group dropdown" name="father_relation">
 								<button type="button" id="plan_dialog_items_father_relation_btn" father_id="0" class="btn btn-primary dropdown-toggle common_btn_small_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 									无
 								</button>
 							 	<div class="dropdown-menu"></div>
							</div>
						    <div class="plan_dialog_subordinate_when_modifying common_blue_font"></div>
      					</div>
      				</div>
      				<div class="plan_dialog_items_error_mes items_header"></div>
      				<div class="plan_dialog_items_form_one_row plan_dialog_items_values_container">
      				    <div id="plan_dialog_items_inputs_for_minutes_type" class="plan_dialog_items_inputs_by_type_container plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_prefix_title">投入</span>
      						<input type="text" name="value_by_minutes" placeholder="只允许整数"/>
      						<span class="plan_dialog_items_suffix_title">分钟</span>
      					</div>
      					<div id="plan_dialog_items_inputs_for_times_type" class="plan_dialog_items_inputs_by_type_container plan_dialog_items_unit_container">
      						<div class="plan_dialog_items_unit">
      							<span class="plan_dialog_items_prefix_title">投入</span>
      							<input type="text" name="value_by_times" placeholder="只允许整数"/>
      							<span class="plan_dialog_items_suffix_title">次</span>
      						</div>
      					</div>
      				     <div id="plan_dialog_items_inputs_for_same_type_mapping_of_minutes" class="plan_dialog_items_inputs_by_type_container plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_prefix_title">关系</span>
      						<em>1分钟</em><em><span class="plan_dialog_items_son_title"></span></em>换算<input type="text" name="mapping_val_for_same_type" placeholder="允许小数" />分钟<em><span class="plan_dialog_items_father_title"></span></em>
      					</div>
      				     <div id="plan_dialog_items_inputs_for_same_type_mapping_of_times" class="plan_dialog_items_inputs_by_type_container plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_prefix_title">关系</span>
      						<em>1次</em><em><span class="plan_dialog_items_son_title"></span></em>换算<input type="text" name="mapping_val_for_same_type" placeholder="允许小数" />次<em><span class="plan_dialog_items_father_title"></span></em>
      					</div>
      					<div id="plan_dialog_items_inputs_for_different_type_mapping" class="plan_dialog_items_inputs_by_type_container plan_dialog_items_unit_container">
      						<span class="plan_dialog_items_prefix_title">关系</span>
      						<em>1次</em><em><span class="plan_dialog_items_inputs_of_times_tyep"></span></em>换算<input type="text" name='mapping_val_for_differ_type' placeholder="只允许整数"/>分钟<em><span class="plan_dialog_items_inputs_of_minutes_tyep"></span></em>
      					</div>
      				</div>
      				<div class="plan_dialog_items_error_mes for_different_type"></div>
      				<div class="plan_dialog_items_form_one_row plan_dialog_items_note">
      					<span class="plan_dialog_items_sub_title plan_note">备注</span>
      					<textarea type="text" name="note"></textarea>
      				</div>
      				<div class="plan_dialog_items_form_hint">修改计划项不会对已生成的工作表产生影响</div>
      				<div class="plan_dialog_items_controgroup_container">
      					<div id="plan_dialog_save_or_add_item_hint_mes"></div>
      				    <span id="plan_dialog_save_item_button" class="btn btn-success common_btn_small_padding">保存</span>
      					<span id="plan_dialog_add_item_button"  class="btn btn-success common_btn_small_padding">添加</span>
      				</div>

      			</div>
      			<div class="plan_dialog_copy_plan_items_container">
      				<span id="plan_dialog_copy_plan_hint_mes"></span>
      				<span>通过ID</span>
      				<input type="text" name="copy_plan_id">
      				<span class="common_blue_font common_hover" id="plan_dialog_copy_plan_btn">复制</span>
      			</div>
      			
      			
      		</div>
      	</div>
      	<div id="plan_dialog_logs_main_container" class="plan_dialog_main_container">
      	     <div class="plan_dialog_header">
      			<div class="plan_dialog_title">日志</div>
      			<span class="plan_dialog_switch_container_visibility common_blue_font"></span>
      		</div>
      		<div class="plan_dialog_content">
      			<div id="plan_dialog_plan_logs_container"></div>
      			<div id="plan_dialog_plan_latest_update_time">更新于<span></span></div>
      		</div>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="plan_dialog_close_edit_mode">取消编辑</button>
        <button type="button" class="btn btn-primary" id="plan_dialog_open_edit_mode">编辑</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>

<div id="plan_dialog_pattern_container" class="common_pattern_container">
	<div class="plan_dialog_tag_unit_container">
		<span  class="plan_dialog_tag_unit_container_context"></span>
	</div>
	<div class="plan_item_container_first_level">
		<div class="plan_item_container_first_level_header">
			<div></div>
		</div>
		<div  class="plan_item_container_wrap">
			<div class="plan_item_container_sub_container">
				<div class="plan_item_container_body"> </div>
				<div class="plan_item_container_footer">
					<div class="plan_item_add_mark" title="添加"></div>
					<div class="plan_item_delete_mark" title="删除"></div>
					<div class="plan_item_modify_mark" title="修改"></div>
				</div>
				
				<div class="plan_item_container_fold_info_container">
					<div class="plan_item_fold_btn" title="折叠"></div>
					<div class="plan_item_unfold_btn" title="打开"></div>
					<div class="plan_item_save_fold_info common_blue_font common_hover">保存</div>
					<div class="plan_item_save_fold_hint"></div>
				</div>
				
			</div>
		</div>
	</div>


	<div class="plan_item_container_unit_level">
		<div class="plan_item_container_unit_header">
			<div></div>
		</div>
		<div  class="plan_item_container_wrap">
			<div class="plan_item_container_sub_container">
				<div class="plan_item_container_body"></div>
				<div class="plan_item_container_footer">
					<div class="plan_item_add_mark" title="添加"></div>
					<div class="plan_item_delete_mark" title="删除"></div>
					<div class="plan_item_modify_mark" title="修改"></div>
				</div>

				<div class="plan_item_container_fold_info_container">
					<div class="plan_item_fold_btn" title="折叠"></div>
					<div class="plan_item_unfold_btn" title="打开"></div>
					<div class="plan_item_save_fold_info common_blue_font common_hover">保存</div>
					<div class="plan_item_save_fold_hint"></div>
				</div>
			</div>
		</div>
	</div>

	<div class="plan_item_container_root_container">
		<div class="plan_item_circle_container"></div>
		<div class="plan_item_container_root_controlgroup">
			<div title="添加" class="plan_item_root_add_mark"></div>
		</div>
	</div>
	
	<div class="plan_dialog_plan_log_container">
		<div class="plan_dialog_plan_log_content"></div>
		<div class="plan_dialog_plan_log_footer">
			<div class="plan_dialog_creator_name"></div>
			<div class="plan_dialog_log_time"></div>
		</div>
	</div>
</div>