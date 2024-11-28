<%@page import="manager.system.career.BookStyle"%>
<%@page import="manager.service.work.WorkLogic"%>
<%@page import="manager.system.Gender"%>
<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<script src="${pageContext.request.contextPath}/js/notes.js?<%= SM.VERSION %>" type="text/javascript" ></script>
<link  href="${pageContext.request.contextPath}/css/notes.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<meta charset="utf-8" />
<title><%=SM.WEB_TITLE%></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div id="notes_sub_main_container" class="common_sub_main_container">
			<div id="note_books_main_container">
				<div class="note_books_header_container">
					<span class="notes_title"></span>
					<span><span class="note_books_switch_to_show_note_books_content_container common_blue_font common_hover"></span></span>
				</div>
				<div id="note_books_content_container">
					<div id="note_books_cards_controgroup">
						<span class="btn btn-outline-primary" id="notes_create_note_book_btn">创建笔记本</span>
						<span class="btn btn-outline-primary notes_leran_to_use_notes_btn">使用方式</span>
						<span class="btn btn-outline-primary" id="notes_call_out_memo_dialog_btn">备忘录</span>
						<span class="btn btn-outline-primary" id="notes_show_closed_books_container_btn">显示封存的笔记本</span>
						<span class="btn btn-outline-primary" id="notes_hide_closed_books_container_btn">隐藏封存的笔记本</span>
					</div>
					<div id="note_books_mes_when_zero_plan" class="alert alert-success fade show" role="alert">
						<button type="button" class="close" data-dismiss="alert" aria-label="Close">
  							<span aria-hidden="true">&times;</span>
						</button>
 						 <h4 class="alert-heading">嗨!</h4>
 						 <p>您还没有开启的笔记本，快来<em><span class="notes_create_note_book_btn_in_hint common_hover">创建</span></em>一个吧。如果有些困惑，也可以先<em><span class="notes_leran_to_use_notes_btn common_hover">简单了解</span></em>笔记的使用方式</p>
					</div>
					<div id="note_books_cards_main_container">
						<div class="common_loading_message">加载中......</div>
						<div id="note_book_cards_sub_container"></div>
						<div id="notes_closed_note_books_container">
							<table></table>
						</div>
					</div>
				</div>
			</div>
			<div id="note_books_detail_main_container" class="common_grey_background">
				<jsp:include page="components/note_book_content/note_book_content.jsp" flush="false" />
			</div>
			<div class="common_module_message">并非只有学习和工作需要记录笔记；日记、随笔……或许仅仅记录下一段心情，便足以让时光回味。</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	
<div class="modal fade" id="notes_getting_start_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="notes_getting_start_dialog_label" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="notes_getting_start_dialog_label">如何使用笔记模块</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">

      	<p>
  			笔记模块顾名思义，它可以让您创建笔记本来记录笔记；而它除了能提供物理意义上笔记本的功能之外，还能提供只有当它信息化后，才能实现的有趣功能——这篇介绍，就是介绍其中的一个功能，标签。
  		</p>
  		<p>	
  			在介绍之前，我想说明一点，下面只是我通过个人使用的例子来尽快让您明白它的使用方式，当明白之后，您完全可以发挥自己的想象力，用更舒服的方式使用它。
      	</p>	
      	<p> 
      		毕竟我最初设计它的原因（也是设计这个模块的原因），只不过是我厌倦了Word文档里，每次都要手动把“TODO”四个字母改成“DONE”。
      	</p>
      	
      	<a href="<%=SM.BASIC_FILES_FOLDER%>note_example_1.jpg" data-fancybox data-caption="我在写这篇使用说明时的当日笔记，2020.11.23" >
    		<img src="<%=SM.BASIC_FILES_FOLDER%>note_example_1.jpg"  class="right_float_img"/>
  		</a>
		<p> 
			在我工作的过程中，我常常需要把一些阶段性的目的记录下来，并且在完成它之后，再消掉它。（PS：我觉得这是一种非常好的完成事情的方式，它能够分解一个庞大的事情，从而让人不至于迷失在过程之中）
		</p>
		<p>
			为此，我设计了一种标签语法（以标签名开头 以)或）或换行结尾）、和两个标签（TODO，DONE）来完成这件事情。（PS:对于标签使用更详细的介绍，您可以在创建笔记本之后，点开标签管理器查看）
		</p>
		<p>
			当我开始做某件事情的时候，我会留下 TODO 某件事情 的信息；当事情完成后，我会在标签管理器或者在笔记的查看模式下，点掉它，它就自动变成了DONE。就这么简单。
		</p>
		<p>
			介绍讲完了，至于其它有趣的功能，当您使用起来后，随便点点，没准会有意外之喜。
		</p>
		<p>
			祝您使用愉快。
		</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>	





<div id="notes_pattern_container" class="common_pattern_container">
	<div class="notes_closed_books_controlgroup">
		<span class="notes_show_book_note_btn common_blue_font common_hover" data-container="body" data-toggle="popover" data-placement="top">查看备注</span>
		<span class="notes_cancel_book_closed_state_btn common_blue_font common_hover">解除封存</span>
		<span class="notes_delete_closed_book_btn common_blue_font common_hover">彻底删除</span>
	</div>
</div>

<div class="modal fade" id="notes_create_note_book_dialog" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="notes_create_note_book_dialog_label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="notes_create_note_book_dialog_label">创建笔记本</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      	<form id="notes_create_note_book_form">
			<div class="notes_create_note_book_entity note_book_name">
				<div class="notes_create_note_book_second_title">名称</div>
				<input placeholder="1-20个字符" name="name" type="text" autocomplete="off"/>
				<div class="notes_create_note_book_error_container"></div>
			</div>
			<div class="notes_create_note_book_entity choose_style" test="true">
				<div class="notes_create_note_book_second_title">样式风格</div>
				<div class="notes_create_note_book_style_contaier">
					<%
						for(BookStyle style:BookStyle.values()){
							if(style==BookStyle.UNDECIDED){
								continue;
							}
							
							%>
								<div class="notes_create_note_book_style_unit" main_color="<%=style.getMainColor()%>" sub_color="<%=style.getSubColor()%>" >
									<div class="notes_create_note_book_style_sample"></div>
									<input name="style" type="radio" value="<%=style.getDbCode()%>"/>
								</div>
							<% 
						}
					%>
				</div>
			</div>
			<div class="notes_create_note_book_entity" test="true">
				<div class="notes_create_note_book_second_title">备注</div>
				<textarea name="note"></textarea>
				<div class="notes_create_note_book_error_container"></div>
			</div>
      	</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="notes_commit_create_note_book_button">创建</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>