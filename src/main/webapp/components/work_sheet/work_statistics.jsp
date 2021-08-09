<%@page import="manager.system.career.WorkSheetState"%>
<%@page import="manager.system.career.PlanState"%>
<%@page import="manager.system.career.PlanItemType"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!-- 这个JSP 是由于解决全部工作表 dialog互相重叠 提取出来的，仅仅是为了能让dom结构上 这个dialog 不要被包含在workSheet中 css 和 js 还是先依赖于work_sheet的 -->
<div class="modal fade" id="ws_statistics_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="ws_statistics_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="ws_statistics_dialog_label"><span id="date_for_ws_statistics_dialog_label"></span>的工作统计</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<div id="ws_statistics_dialog_charts_container">
      		<div id="ws_statistics_dialog_bar_chart_container">
      			
      			
      			
      		</div>
      		<div id="ws_statistics_dialog_pie_chart_container">
      			
      			
      			
      		</div>
      	</div>
      	
      	<div id="ws_statistics_dialog_line_and_bar_chart_container">
      		
      	</div>
      	
      	<div id="ws_statistics_dialog_introduction_for_chart">
      		统计仅计算具备结束时间的工作项。
      	</div>

      	<div id="ws_statistics_dialog_text_info_container">
      		 总工作时长 <em id="ws_statistics_dialog_count_all_time"></em>
      	</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>