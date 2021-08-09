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
 						 <p>您还没有进行中的计划，快来<em><span class="work_plan_create_plan_in_hint common_hover">创建</span></em>一个吧。如果有些困惑，也可以先<em><span class="work_leran_to_use_work_sheet common_hover">简单了解</span></em>工作表的使用方式。</p>
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
						<span class="btn btn-outline-primary" id="work_open_ws_stat_of_date_range_dialog_btn">时间范围分析</span>
						<span class="btn btn-outline-primary" id="work_open_plan_dept_dialog_btn">历史欠账</span>
						<span class="btn btn-outline-primary common_go_to_page" href="all_ws.jsp" >查看所有</span>
					</div>
					<div class="common_loading_message">加载中......</div>
					<div id="work_work_sheet_main_body" class="common_grey_background">
						<div id="work_ws_sub_left_container">
							<div id="work_ws_sub_left_container_header" class="common_grey_button_hover" >
								近日工作表
							</div>
							<div id="work_ws_sub_left_container_body"></div>
							<div id="work_ws_sub_left_container_footer">
								<div class="work_see_more_ws common_grey_button_hover">查看更多......</div>
								<div class="work_no_more_ws" >没有更多了......</div>
							</div>
						</div>
						<div id="work_ws_sub_right_container">
							<div id="work_ws_with_actual_content_container">
							<div id="work_ws_count_ws_today_alert" class="alert alert-success fade show" role="alert">
								<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  									<span aria-hidden="true">&times;</span>
								</button>
 						 		<p>到现在为止，已经有<em></em>个人和您一样，开启了今天的工作表</p>
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
								您没有开启任何工作表
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="common_module_message">高效的时间管理是为了让人有更多时间来<em>享乐与生活</em></div>
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
        <h5 class="modal-title" id="work_create_plan_dialog_label">定制您的专属计划</h5>
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
        <button type="button" class="btn btn-secondary" id="work_commit_create_plan_button">创建</button>
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
      	<a href="<%=SM.BASIC_FILES_FOLDER%>start_business.jpg" data-fancybox data-caption="我在2020.9.1-2020.10.31期间的计划" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>start_business.jpg"  class="right_float_img"/>
  		</a>
      	<p>
  			我认为完成一个困难事情的最有效的方法，是制定一个漂亮的计划并且诚实地执行它。
  		</p>
  		<p>	
  			工作表模块就是基于这个理念设计的，<em>它每一天的工作，一定是基于一个计划</em>之上的。
      	</p>	
      	<p> 
      		右图，就是我在创业期间为自己制定的计划，它能够很好说明工作表的使用方式：
      	</p>
		<p>
			a.它采取了一种<em>树结构</em>，这让人尽可能表达心目中的计划。
		</p>
		<p>
			b.它将计划项分为<em>次数</em>和<em>分钟</em>两种类型，并可以随心所欲地设置换算方法。
		</p>
		<a href="<%=SM.BASIC_FILES_FOLDER%>ws_example_2.jpg" data-fancybox data-caption="我在2020.10.28 7点左右的工作表" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>ws_example_2.jpg"  class="right_float_img"/>
  		</a>
		<p>
		   当有了计划后，就可以开始一天的工作了，右图，就是我首次把系统部署到服务器当天、晚上7点左右的工作情况。
		</p>
		<p>
		   这天，我由于终于用起来系统的激动，在部署成功（早上8:21）后，其实没怎么工作，以至于到了晚上7点，我才只不过写了67分钟的代码，处理了199分钟服务器相关的事情，而此时我离计划的目标还剩下93.8分钟。
		</p>
  		<p>
  		   不久，我的朋友找我去下棋......最终导致这一天，我只工作了这么长时间——并没将计划完成。
  		</p>
  		<p>
  			而对于<em>未完成的计划项</em>，它们的去处有两个：
  		</p>
  		<p>
  			a.将这天的工作表<em>假定完成</em>（这代表着系统将认为这天的工作完成）。
  		</p>
  		<p>
  		    b.以名称为标识<em>与历史欠账同步</em>(同步意味着未完成的作为赊欠，多完成的作为盈余)。
  		</p>
  		<p>
  		   	我选择了后者，期待未来某天能够再努力一点，把欠账的计划项都消掉。
  		</p>
  		<p>
  			另外多提一点的是，<em>每日工作表的计划同样可以编辑，它实际上是工作表基于的计划的一份复制</em>，如果有临时的变动，完全可以修改当日工作表的计划，它只影响当日的工作表计算。
  		</p>
  		<p>
  			以上就是对工作表的简单介绍，祝您使用愉快。
  		</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>


<jsp:include page="components/plan_dialog/plan_dialog.jsp" flush="false" />
<jsp:include page="components/plan_dept_dialog/plan_dept_dialog.jsp" flush="false" />
</body>
</html>