<%@page import="manager.logic.career.WorkLogic"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<script src="${pageContext.request.contextPath}/js/work.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/work.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<title><%=SM.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div id="work_sub_main_container" class="common_sub_main_container">
			<div id="work_plan_main_container">
				<div class="work_header_container">
					<span class="work_title">进行中的计划</span>
					<span><span class="work_switch_to_show_plan_sub_container common_blue_font common_hover"></span></span>
				</div>
				<div id="work_plan_sub_container">
					<div id="work_cards_controgroup">
						<span class="btn btn-outline-primary" id="work_open_ws_today_button" title="开始绝对算得上最困难的事情之一，但它让人区别于平庸">开启今日工作表</span>
						<span class="btn btn-outline-primary" id="work_create_plan_button">创建计划</span>
						<span class="btn btn-outline-primary work_leran_to_use_work_sheet" >使用方式</span>
						<span class="btn btn-outline-primary common_go_to_page" href="all_plans.jsp" >查看所有</span>
					</div>
					<div id="work_plan_cards_container">
					
					<div id="work_plan_mes_when_zero_plan" class="alert alert-success fade show" role="alert">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  							<span aria-hidden="true">&times;</span>
						</button>
 						 <h4 class="alert-heading">嗨!</h4>
 						 <p>没有进行中的计划，<em><span class="work_plan_create_plan_in_hint common_hover">创建</span></em>计划。如果困惑，可以<em><span class="work_leran_to_use_work_sheet common_hover">了解</span></em>工作表的使用方式。</p>
					</div>
					
						<div class="common_loading_message">加载中......</div>
						<div class="work_plan_cards_content"></div>
					</div>
				</div>
			</div>
			<div id="work_ws_main_container">
				<input type="hidden" id="default_ws_limit_of_one_page" value="<%=WorkLogic.DEFAULT_WS_LIMITE_OF_ONE_PAGE%>"/>
				<div class="work_header_container">
					<span class="work_title">工作表</span>
					<span><span class="work_switch_to_show_ws_sub_container common_blue_font common_hover"></span></span>
				</div>
				<div id="work_ws_sub_container">
					<div id="work_work_sheet_main_controgroup">
						<span class="btn btn-outline-primary" id="switch_to_show_ws_infos_recently"></span>
						<span class="btn btn-outline-primary" id="batch_sync_wss_in_ws_infos_recently">同步列表内工作表</span>
						<span class="btn btn-outline-primary" id="work_open_ws_stat_of_date_range_dialog_btn">时间范围分析</span>
						<span class="btn btn-outline-primary" id="work_open_plan_dept_dialog_btn">历史欠账</span>
						<span class="btn btn-outline-primary common_go_to_page" href="all_ws.jsp" >看所有</span>
					</div>
					<div class="common_loading_message">加载中......</div>
					<div id="work_work_sheet_main_body" class="common_grey_background">
						<div id="work_ws_sub_left_container">
							<div id="work_ws_sub_left_container_header" class="common_grey_button_hover" >
								近日工作表
							</div>
							<div id="work_ws_sub_left_container_body"></div>
							<div id="work_ws_sub_left_container_footer">
								<div class="work_see_more_ws common_grey_button_hover">看更多......</div>
								<div class="work_no_more_ws" >没有更多了......</div>
							</div>
						</div>
						<div id="work_ws_sub_right_container">
							<div id="work_ws_with_actual_content_container">
							<div id="work_ws_count_ws_today_alert" class="alert alert-success fade show" role="alert">
								<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  									<span aria-hidden="true">&times;</span>
								</button>
 						 		<p>今天已有<em></em>人开启工作表</p>
							</div>
							<div id="work_ws_count_ws_prev_day_alert" class="alert alert-success fade show" role="alert">
								<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  									<span aria-hidden="true">&times;</span>
								</button>
 						 		<p>共有<em></em>个人在这一天开启了工作表</p>
							</div>
							
							
								<div id="work_work_sheet_jsp_container">
									<jsp:include page="components/work_sheet/work_sheet.jsp" flush="false" />
								</div>
							</div>
							<div class="work_ws_blank_container">
								未开启工作表
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="common_module_message">高效的时间管理是为了让人有更多时间来<em>享乐</em>与<em>生活</em></div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	<jsp:include page="components/work_sheet/work_statistics.jsp" flush="false" />
	<jsp:include page="components/ws_stat_of_date_range_dialog/ws_stat_of_date_range_dialog.jsp" flush="false" />
<div id="work_pattern_container" class="common_pattern_container">
	<div class="work_ws_date_cotnainer common_grey_button_hover">
		<div class="work_ws_date_cotnainer_title"></div>
	</div>
	<div class="work_plan_card_container">
		<div class="work_plan_card_content">
			<div class="work_plan_card_name"></div>
			<div class="work_plan_card_state"><span class="common_state_block"></span></div>
			<div class="work_plan_card_start_date">
				<div class="work_plan_card_date_title">开始日期</div>
				<div class="work_plan_card_start_date_content"></div>
			</div>
			<div class="work_plan_card_end_date">
				<div class="work_plan_card_date_title">结束日期</div>
				<div class="work_plan_card_end_date_content"></div>
			</div>
			<div class="work_plan_card_create_time">
				<div class="work_plan_card_date_title">创建时间</div>
				<div class="work_plan_card_create_time_content"></div>
			</div>
		</div>
		<div class="work_plan_card_footer">
			<span class="common_blue_font common_hover work_see_plan_detail">详情</span>
			<span class="common_blue_font common_hover work_edit_plan_button">编辑</span>
			<span class="common_blue_font common_hover work_finish_plan">完成</span>
			<span class="common_blue_font common_hover work_abandon_plan">废除</span>
		</div>
	</div>
</div>
<div class="modal fade" id="work_create_plan_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="work_create_plan_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="work_create_plan_dialog_label">定制专属计划</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<form id="work_create_plan_form">
			<div class="work_create_plan_entity plan_name">
				<div class="work_second_title">名称</div>
				<input placeholder="1-20个字符" name="name" type="text"/>
				<div class="work_create_plan_error_container"></div>
			</div>
			<div class="work_create_plan_entity">
				<div class="work_create_plan_date_header">
					<div class="work_second_title">起始日期</div><span id="work_set_start_date_today_for_create_plan" class="common_blue_font">今天</span>
				</div>
				<input name="start_date" type="date"/>
				<div class="work_create_plan_error_container"></div>
			</div>
			<div class="work_create_plan_entity end_date_container">
				<div class="work_create_plan_date_header">
					<div class="work_second_title">结束日期</div>
					<div class="work_end_date_null_container">
						<input type="checkbox" name="end_date_null" title="在重新设置结束日期前，该计划将一直被系统认为进行中" id="work_end_date_null_for_create_plan">
						<label title="在重新设置结束日期前，该计划将一直被系统认为进行中" for="work_end_date_null_for_create_plan" class="common_blue_font">暂时没想好</label>
					</div>
				</div>
				<input name="end_date" type="date"/>
				<div class="work_create_plan_error_container"></div>
			</div>
			
			<div class="work_create_plan_entity" test="true">
				<div class="work_second_title">备注</div>
				<textarea name="note" rows="" cols=""></textarea>
				<div class="work_create_plan_error_container"></div>
			</div>
      	</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="work_commit_create_plan_button">创建</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="work_getting_start_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="work_getting_start_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="work_getting_start_label">如何使用工作表模块</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<a href="<%=SM.BASIC_FILES_FOLDER%>plan_example1.jpg" data-fancybox data-caption="2021.9.18" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>plan_example1.jpg"  class="right_float_img"/>
  		</a>
      	<p>
  			本模块根据<em>工作表</em>来记录每天的工作，而工作表一定是基于<em>计划</em>的。
  		</p>
  		<p>	
  			如右图，计划是<em>树型结构</em>，第一层确定每天需要达到的分钟/次数，第二层之后确定和前一层的<em>换算关系</em>。
      	</p>	
		<p>
		   当有了计划后，就可以生成一份工作表，然后登记工作了。
		</p>
  		<p>
  			另外，工作表里的计划、是预设计划的<em>副本</em>，因此，你可以随便编辑每日工作表里的计划、而不会影响到预设的计划——这通常适用于计划临时变动的情况。
  		</p>
      	<br/>
		<a href="<%=SM.BASIC_FILES_FOLDER%>plan_example1_ws.jpg" data-fancybox data-caption="2021.9.18" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>plan_example1_ws.jpg" />
  		</a>
  		<br/>
  		<p>
  			当登记完工作之后，系统会根据登记的工作，生成可供分析的报表，无论是每日的工作统计，还是在一段时间内的工作统计。
  		</p>
  		 <p>
  			以上就是对工作表的简单介绍，使用愉快。
  		</p>
		<a href="<%=SM.BASIC_FILES_FOLDER%>plan_example1_report.jpg" data-fancybox data-caption="2021.9.18" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>plan_example1_report.jpg" />
  		</a>
		<a href="<%=SM.BASIC_FILES_FOLDER%>plan_example1_report_by_month.jpg" data-fancybox data-caption="2021.9.18" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>plan_example1_report_by_month.jpg"  class="right_float_img"/>
  		</a>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>

<jsp:include page="components/tag_edit_dialog/tag_edit_dialog.jsp" flush="false" />
<jsp:include page="components/plan_dialog/plan_dialog.jsp" flush="false" />
<jsp:include page="components/plan_dept_dialog/plan_dept_dialog.jsp" flush="false" />
</body>
</html>