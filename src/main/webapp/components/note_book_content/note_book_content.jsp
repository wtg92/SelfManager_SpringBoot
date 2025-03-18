<%@page import="manager.system.career.BookStyle"%>
<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.SelfX"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>

<meta charset="utf-8" />

<link  href="${pageContext.request.contextPath}/css/dependency/drag_window.css" type="text/css" rel="stylesheet" /> 
<script src="${pageContext.request.contextPath}/js/dependency/drag_window.js"></script>


<script src="${pageContext.request.contextPath}/js/dependency/tinymce.min.js"></script>

<script src="${pageContext.request.contextPath}/components/note_book_content/note_book_content.js?<%= SelfX.VERSION %>" type="text/javascript" ></script>
<link href="${pageContext.request.contextPath}/components/note_book_content/note_book_content.css?<%= SelfX.VERSION %>" type="text/css" rel="stylesheet" />
</head>

<body>
<div id="note_book_content_main_container">
	<div id="note_book_content_main_header_container">
		<div id="note_book_content_main_header_units_container"></div>
		<div id="note_book_close_all_book_content" class="note_book_navagation_close_mark">&times;</div>
	</div>
	<div id="note_book_content_main_body_container" class="common_grey_background">
		
	</div>
</div>

<div class="modal fade" id="notes_memo_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="notes_memo_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="notes_memo_dialog_label">备忘录</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" id="memo_dialog_body">
      	<div id="memo_dialog_body_header_container">
      		<textarea id="memo_note_textarea" title="备注"></textarea>
      		<div id="memo_note_changed_hint"></div>
      	</div>
      	<div id="memo_dialog_body_body_container">
      		<div id="memo_dialog_body_body_btns_container">
      			<span class="common_blue_font common_hover" id="show_all_memo_items_footer_btn">显示所有备注</span>
      			<span class="common_blue_font common_hover" id="hide_all_memo_items_footer_btn">隐藏所有备注</span>
 				<span class="common_blue_font common_hover" id="open_notes_memo_edit_mode_btn">编辑（拖动调整顺序）</span>
 				<span class="common_blue_font common_hover" id="close_notes_memo_edit_mode_btn">取消编辑</span>
      		</div>
      		<div id="memo_dialog_body_items_fitler">
				<input id="memo_dialog_body_items_filter_key_words" type="text" placeholder="关键词"/>
				<div class="btn-group dropdown" id="memo_dialog_body_items_list_for_filter">
 					<button type="button"  class="btn btn-primary dropdown-toggle common_btn_smaller_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 									无
 					</button>
 					<div class="dropdown-menu"></div>
				</div>
				<span>过滤</span>
			</div>
			<div id="memo_dialog_body_items_container">
					
			</div>
			<div id="memo_dialog_body_control_group_container">
			<form>
      		<input type="hidden" id="memo_dialog_item_id_when_modifying">
      		<div class="memo_dialog_one_row_of_control_group_container memo_item_content" >
      			<div class="memo_dialog_one_row_title_of_control_group_container">内容</div>
      			<textarea name="content"></textarea>
      		</div>
      		<div class="memo_dialog_one_row_of_control_group_container memo_item_note">
      			<div class="memo_dialog_one_row_title_of_control_group_container">备注</div>
      			<textarea name="note"></textarea>
      		</div>
      		<div id="memo_dialog_body_control_group_container_of_operation_btns">
      			<div class="btn-group dropdown" id="memo_dialog_body_items_list_for_add_or_saving">
 					<button type="button"  class="btn btn-primary dropdown-toggle common_btn_smaller_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 									无
 					</button>
 					<div class="dropdown-menu"></div>
				</div>
				<span class="label_hint_for_add_or_saving_memo_item">标签</span>
      			<div id="memo_dialog_save_or_add_item_hint_mes"></div>
      			<span id="memo_dialog_give_up_save_item_button" class="btn btn-secondary common_btn_small_padding">放弃修改</span>
      			<span id="memo_dialog_save_item_button" class="btn btn-success common_btn_small_padding">保存</span>
      			<span id="memo_dialog_add_item_button"  class="btn btn-success common_btn_small_padding">添加</span>
      		</div>
      		</form>
      </div>
			
			
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>	


<div id="note_book_content_pattern_container" class="common_pattern_container">
	<div class="note_book_title_item_cotnainer common_grey_button_hover">
		<div class="note_book_title_item_flex_cotnainer">
			<div class="note_book_list_item_title"></div>
			<div class="note_book_list_item_delete_mark">&times;</div>
		</div>
	</div>
	
	<div draggable="true"  class="note_book_for_unit_header_navagation_container common_hover">
		<div class="note_book_for_unit_header_navagation_loading_mark">
			<div class="spinner-border" role="status">
  				<span class="sr-only">Loading...</span>
			</div>
		</div>
		<div class="note_book_for_unit_header_navagation_window_name"></div>
		<div class="note_book_for_unit_header_navagation_close_window_btn note_book_navagation_close_mark">&times;</div>
	</div>
	<div class="note_book_for_unit_body_container">
		<div class="note_book_for_unit_body_top_btns_container">
			 <span class="note_book_call_out_labels_manager_btn common_blue_font common_hover">标签管理器（ctrl+m）</span>
			 
			 <span class="note_book_main_info_hide_page_title_btn common_blue_font common_hover">隐藏页标题</span>
			 <span class="note_book_main_info_show_page_title_btn common_blue_font common_hover">显示页标题</span>
			 
			 <span class="note_book_main_info_swtich_merging_page_mode common_blue_font common_hover">合页显示</span>
			 <span class="note_book_main_info_swtich_spliting_page_mode common_blue_font common_hover">分页显示</span>
			 
			 <span class="note_book_main_info_show_hidden_notes common_blue_font common_hover">显示隐藏页</span>
			 <span class="note_book_main_info_close_show_hidden_notes common_blue_font common_hover">取消显示隐藏页</span>
			 
			 
			 <span class="note_book_main_info_open_note_page_edit_mode common_blue_font common_hover" >编辑页（ctrl+q）</span>
			 <span class="note_book_main_info_close_note_page_edit_mode common_blue_font common_hover" >取消编辑页（ctrl+q）</span>
			 <span class="note_book_main_info_open_note_list_edit_mode common_blue_font common_hover">编辑目录（拖动调整顺序）</span>
			 <span class="note_book_main_info_close_note_list_edit_mode common_blue_font common_hover">取消编辑目录</span>
			 <span class="note_book_baisc_info_infos_list_main_container common_blue_font common_hover"></span>
			 <span class="note_book_baisc_info_switch_container_visibility common_blue_font common_hover"></span>
		</div>
		<div class="note_book_baisc_info_for_unit_body_container">
			<div class="note_book_baisc_info_header">
      			<div class="note_book_baisc_info_title">基本信息</div>
      			<div class="note_book_baisc_info_controlgroup">
      			    <span class="note_book_baisc_info_open_edit_mode common_blue_font common_hover">编辑</span>
      			    <span class="note_book_baisc_info_close_edit_mode common_blue_font common_hover">取消编辑</span>
      			</div>
      		</div>
      		<div class="note_book_baisc_info_header_content">
      			<form class="note_book_baisc_info_form">
      			<div class="note_book_baisc_info_sub_container">
      					<div class="note_book_baisc_info_for_one_row">
      						<div  class="note_book_baisc_info_unit_container unit_note_book_name">
      							<span class="note_book_baisc_info_sub_title">名称</span>
      							<input type="text" name="name" placeholder="1-20个字符"/>
      							<span class="note_book_baisc_info_book_name_span"></span>
      							<span class="note_book_baisc_info_book_name_hint"></span>		
      						</div>
      						<div class="note_book_baisc_info_unit_container for_weight_seq">
      							<span class="note_book_baisc_info_sub_title">顺序权重</span>
      							<span class="note_book_baisc_info_sub_title_seq_weight_span"></span>
      							<input type="text" name="seq_weight"/>
      							<div class="note_book_baisc_info_sub_seq_weight_hint">非负整数，决定笔记本的显示顺序，值越大越靠前</div>
      						</div>
      					</div>
      					
      					<div class="note_book_baisc_info_for_one_row note_book_select_book_style">
      						<%
						for(BookStyle style:BookStyle.values()){
							if(style==BookStyle.UNDECIDED){
								continue;
							}
							
							%>
								<div class="note_book_content_style_unit" main_color="<%=style.getMainColor()%>" sub_color="<%=style.getSubColor()%>" >
									<div class="note_book_content_style_sample"></div>
									<input name="style" type="radio" value="<%=style.getDbCode()%>"/>
								</div>
							<% 
						}
							%>
      					
      					
      					</div>
      					<div class="note_book_baisc_info_for_one_row">
      						<textarea name="note" title="备注"></textarea>
      						<div class="note_book_baisc_info_note_div"></div>
      					</div>
      			</div>
      			
      			<div class="note_book_content_save_basic_controgroup">
      				<div class="note_book_content_save_basic_info_button btn btn-success common_btn_small_padding">保存</div>
      			</div>
      			</form>
      		</div>
		</div>
		<div class="note_book_main_info_for_unit_body_container">
			<div class="note_book_note_infos_list_main_container common_grey_background">
				<div class="note_book_create_note_btn common_grey_button_hover">添加笔记页</div>
				<div class="note_book_note_infos_list">
					<div class="note_book_important_note_infos_list" title="重要笔记区域"></div>
					<div class="note_book_general_note_infos_list" title="普通笔记区域"> </div>
				</div>
			</div>
			<div class="note_book_note_content_main_container common_grey_background">
				<div class="note_book_note_note_pages_container_for_merge_mode">
				
				</div>
			</div>
			<div class="note_book_open_zero_note_body">
					您没有打开任何笔记页
			</div>
			
			
		</div>
	</div>
	
	<div class="one_note_book_content_unit">
		<div class="one_note_book_content_unit_header" >
			<div class="one_note_book_content_unit_page_name_and_delete_mark">
				<div class="one_note_book_content_unit_page_name"></div>
				<input class="one_note_book_content_unit_page_name_input" type="text" />
				<div class="one_note_book_content_unit_delete_mark">&times;</div>
			</div>
			<div class="one_note_book_content_unit_page_control_btns">
				<span class="one_note_book_content_unit_page_control_mark_hidden common_blue_font common_hover" note_hidden="true">隐藏</span>
				<span class="one_note_book_content_unit_page_control_mark_show common_blue_font common_hover" note_hidden="false">取消隐藏</span>
				<span class="one_note_book_content_unit_page_control_mark_important common_blue_font common_hover" note_important="true">标为重要</span>
				<span class="one_note_book_content_unit_page_control_mark_general common_blue_font common_hover" note_important="false">标为普通</span>
				<span class="one_note_book_content_unit_page_control_delete_btn common_blue_font common_hover" note_important="true">删除</span>
			</div>
			<div class="one_note_book_content_unit_page_info"><em>鼠标失焦</em>和<em>ctrl+s</em>时触发保存，创建于<span class="page_create_time"></span>，保存于<span class="page_update_time"></span></div>
		</div>
		<div class="one_note_book_content_unit_body">
			<textarea></textarea>
		</div>	
		
		<div class="one_note_book_content_unit_body_show_info"></div>
	</div>
	
		<div class="one_note_book_unit_pattern_and_btns_container">
			<div class="moleskine-wrapper one_note_book_unit_pattern" >
				<div class="moleskine-notebook">
					<div class="notebook-cover">
						<span class="notebook_pre_span"></span>
						<div class="notebook-skin note_book_name"></div>
					</div>
					<div class="notebook-page ruled"></div>
				</div>
			</div>
			<div class="one_note_book_unit_footer">
				<span class="common_blue_font common_hover one_note_see_book_content_btn">查看</span>
				<span class="common_blue_font common_hover one_note_open_book_btn">编辑</span>
				<span class="common_blue_font common_hover one_note_close_book_btn">封存</span>
			</div>
		</div>
		
	<div class="one_memo_item_unit_container">
		<div class="one_memo_item_unit_container_header">
			<span class="one_memo_item_unit_label_name"></span>
			<div class="one_memo_item_unit_content_and_btns_container">
			<div class="one_memo_item_unit_content"></div>
			<div class="one_memo_item_unit_btns_for_item">
				<div class="one_memo_item_unit_btns_for_delete_item common_hover"></div>
				<div class="one_memo_item_unit_btns_for_modify_item common_hover"></div>
			</div>
			</div>
			<div class="one_memo_item_unit_btns_for_opreation">
				<span class="common_blue_font common_hover one_memo_item_unit_switch_footer_btn"></span>
				<span class="common_blue_font common_hover one_memo_item_unit_copy_btn">复制</span>
			</div>
			<span class="one_memo_item_unit_hint"></span>
		</div>
		<div class="one_memo_item_unit_container_footer">
			<div class="one_memo_item_unit_container_footer_note">
				
			</div>
			<div class="one_memo_item_unit_container_footer_note_src_container">
				<div class="one_memo_item_unit_container_footer_note_src_by_put_to_memo_container">
				<span class="one_memo_item_unit_container_footer_src_prefix">来自</span>
				<em><span class="one_memo_item_unit_container_footer_src_book_name"></span></em>
				<span class="one_memo_item_unit_container_footer_src_middle_span">的</span>
				<em><span class="one_memo_item_unit_container_footer_src_note_name"></span></em>
				</div>
				<span class="one_memo_item_unit_container_footer_src_created_by_user">手动添加</span>
			</div>
		</div>
	</div>	
		
		
	<div class="one_note_sm_label_unit_of_manager">
		<span class="one_note_sm_label_unit_tag_name_of_manager"></span>
		<span class="one_note_sm_label_unit_content_of_manager"></span>
		<div class="one_note_sm_label_unit_controgroup_of_manager">
			<span class="one_note_unit_of_manager_hint_of_already_put_to_memo">已存备忘录</span>
			<span class="common_blue_font common_hover one_note_unit_of_manager_put_to_memo_btn">放入备忘录</span>
			<span class="common_blue_font common_hover one_note_unit_of_manager_copy_btn">复制</span>
		</div>
		<span class="one_note_sm_label_unit_hint_of_manager"></span>
	</div>
	<div class="one_note_label_of_manager_introduction">
		<div class="one_note_label_of_manager_introduction_label_name"></div>
		<div class="one_note_label_of_manager_introduction_label_desc"></div>
	</div>
	<div class="one_note_labels_manager_pattern">
		<div class="one_note_labels_manager_introduction">
			<div class="one_note_labels_manager_pattern_container_header">
				<div class="one_note_labels_manager_pattern_container_title">使用说明</div>
				<div class="one_note_labels_manager_pattern_container_switching_btn common_blue_font common_hover"></div>
			</div>
			<div class="one_note_labels_manager_pattern_container_body">
				<div class="one_note_labels_manager_introduction_text_container">
					<p>
						标签是个非常有意思的功能，它可以智能识别您笔记中包含特殊语法的句子，将其转化成标签，以下是相关说明：
					</p>
					<p>
						a.标签管理器只能管理<em>已打开笔记页</em>的内容。
					</p>
					<p>
						b.<em>语法</em>：[标签名][空格]标签内容[换行或者以）或)结尾]   
					</p>
					<p> 
						c.笔记页有编辑模式和取消编辑模式，当在编辑模式时输入包含标签语法的文本，切换到取消编辑模式时，会自动将其转化为对应标签。
					</p>
					<p>
						d.当需要在标签内<em>换行</em>时，使用<em>&r</em>的语法，例如输入文本为 TODO 哈&r哈 时，它转化后的标签中，“哈”字将分别占据一行。
					</p>
					<p>
						e.当需要<em>嵌套</em>标签时，需要<em>显式地在句子后加）或) </em> ，并且，<em>不允许同种标签的嵌套</em>，否则会出现难以预料的结果，例如，TODO 我要吃EM 牛) 系统会正确识别，而TODO 我要吃EM 牛，系统则只能识别EM标签。
					</p>
					<p>
						f.当<em>不希望系统将文本识别为标签</em>  时，只需要在标签前加&即可。例如，TODO 我想到了一个&IDEA 我要吃牛），系统就不会将“我要吃牛”识别为一个IDEA标签。
					</p>
					<p>
						g.语法解析引擎借助了<em>ж</em>和<em>ц</em>两个字符，请不要在笔记中使用它们。
					</p>
					<p>
						出于技术上的难题，标签管理器在<em>拖动、调整窗口大小时，光标不能落在笔记页的编辑器中</em>（这是由于编辑器相当于另一个页面，无法有效监控到里边的光标），请见谅。
					</p>
					<p>
						下面是已支持的标签列表，如果您还想添加其它标签，可以<span class="common_open_new_window common_blut_font common_hover" href="web_info.jsp?container=web_info_connect_with_us">联系我</span>，我会认真考虑每一个用户的意见的：
					</p>
				</div>
				<div class="one_note_labels_manager_introduction_labels_container"></div>
				<div class="one_note_labels_manager_introduction_labels_sample_container">
					<div class="one_note_labels_manager_introduction_labels_container_sample_rlt"></div>
					<input type="text" placeholder="输入包含标签语法的文本，来看看效果吧" class="one_note_labels_manager_introduction_labels_container_sample_input"/>
				</div>				
			</div>
		</div>
		<div class="one_note_labels_manager_labels_management">
			<div class="one_note_labels_manager_pattern_container_header">
				<div class="one_note_labels_manager_pattern_container_title">标签</div>
				<div class="one_note_labels_manager_pattern_container_switching_btn common_blue_font common_hover"></div>
			</div>
			<div class="one_note_labels_manager_pattern_container_body">
				<div class="one_note_labels_fitler">
					<input class="note_labels_search_keys" type="text" placeholder="关键词"/>
					<div class="btn-group dropdown" id="label_list_for_filter">
 						<button type="button"  class="btn btn-primary dropdown-toggle common_btn_smaller_padding" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
 									无
 						</button>
 						<div class="dropdown-menu"></div>
					</div>
					<span>过滤</span>
				</div>
				<div class="one_note_sm_label_units_container">
					
				</div>
				<div class="one_note_sm_label_hint_when_close_all_books">
					您关闭了所有笔记本
				</div>
			</div>
		</div>
		<div class="one_note_labels_manager_others">
			<div class="one_note_labels_manager_pattern_container_header">
				<div class="one_note_labels_manager_pattern_container_title">其它</div>
				<div class="one_note_labels_manager_pattern_container_switching_btn common_blue_font common_hover"></div>
			</div>
			<div class="one_note_labels_manager_pattern_container_body">
				<div class="one_note_labels_btns_container">
					<span class="common_hover common_blue_font call_out_memo_dialog_btn_of_manager">查看备忘录</span>
				</div>
			
				<div class="one_note_labels_manager_of_text_search_container">
					<div class="one_note_labels_manager_of_text_search_container_header">
						<div class="one_note_labels_manager_of_text_search_container_hint">标签管理器可以根据关键词搜索<em>已打开笔记页</em>中的文本</div>
						<input class="note_page_text_search_keys" type="text" placeholder="文本搜索关键词"/>
					</div>
					<div class="one_note_labels_manager_of_text_search_container_body">
						
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="one_note_sm_text_unit_of_manager">
		<div class="one_note_sm_text_content_of_manager"></div>
		<div class="one_note_sm_text_src">
			来自<span></span>
		</div>
	</div>
</div>
</body>
