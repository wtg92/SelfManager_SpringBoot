<%@page import="manager.system.VerifyUserMethod"%>
<%@page import="manager.system.SM"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
<head>
<jsp:include page="including.jsp" flush="false" />
<link href="${pageContext.request.contextPath}/css/web_info.css?<%= SM.VERSION %>" type="text/css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/js/web_info.js?<%= SM.VERSION %>" type="text/javascript"></script>
<title><%=SM.WEB_TITLE %></title>
</head>
<body>
	<jsp:include page="header.jsp" flush="false" />
	<div class="common_main_container">
		<div id="web_info_sub_main_container" class="common_sub_main_container">
			<div id="web_info_main_invagation_container">
				<div id="web_info_what_project_to_do">
					<span container_id='web_info_what_sm_to_do'><span><%=SM.BRAND_NAME%></span>能做什么？</span>
				</div>
				<div id="web_info_footer_titles_container"></div>
			</div>
			<div id="web_info_main_context_container">
				<div id="web_info_what_sm_to_do">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
					<p>
						我是一个没有成功作品、但创作经验非常丰富的创作者，而比创作经验更丰富的，则是我在创作过程中，与人类贪玩、拖延天性相处的经验。
					</p>
					<p>
						所以我十分理解人会坐在自习室一天却什么都没有做，因为我也是这样的。大学期间，我因为写作，看了N遍《老友记》，N遍《摩登家庭》，N遍《马尔科姆的一家》，N遍《生活大爆炸》,N遍《发展受阻》.....					
					</p>
					<p>
						总之，我因为写作而看了非常多的电视剧以及电影，我一开始还会责怪自己意志不坚定，但渐渐发现这十分正常——谁叫他们拍得那么有意思呢？
					</p>
					<p>
						但当我失业在家后，我担心假如我再这样下去，会有饿死的可能，因此，不得不寻找一种工作方法来尽量规范我工作的时间。
					</p>
      				<a href="<%=SM.BASIC_FILES_FOLDER%>ws_example_3.jpg" data-fancybox data-caption="我在2020.10.31 下午一点时的工作情况" >
    					<img src="<%=SM.BASIC_FILES_FOLDER%>ws_example_3.jpg"  class="right_float_img"/>
  					</a>
  					<p>
						我逐渐采用了一种以分钟为单位而记录工作的工作方法，这让我自觉提高了工作效率。（假如学术界还没有相关著作的话，我觉得我算得上发明了一种工作方法——“天戈工作法”）
					</p>
					<p>
						恰好我又是一个牛逼的程序员，所以写了一个软件来支持这种工作方法，正如右图，就是我在书写当下这篇文章时的工作情况。
					</p>
  					<p>
  						这方便了我（我以前是用Word来登记的，厌倦了计算时间），同时，也能够分享给大家。因为我知道，贪玩与拖延不止会出现在创作中，它会出现在任何一个相对困难的事情之中。
  					</p>
  					<p>
  						正如在课上睡觉会更香一样，在人要做一件困难的事情时，俄罗斯方块都会变得有趣起来。
  					</p>
  					<p>
						而这种工作方法，会让人更有效率地专注于手头上的事情，从而更有可能，达成自己的目标。	  					
  					</p>
  					<p>
  						除此之外，我现在拥有了自己的网站，已经可以发挥很多想象力，未来我会不断增加新的功能，来让自己的生活更加轻松，而我方便了自己，也就会方便任何一个使用本网站的人。
  					</p>
  					<p>
  						这就是这个网站能做的事情，<em>让人生简单</em>。
  					</p>
  					
  					<hr/>
  					
  					<p>
  						在使用了一段时间后，系统对工作表进行了进一步的强化，提供了<em>工作统计</em>功能，<em>云笔记</em>功能。
  					</p>
      				<a href="<%=SM.BASIC_FILES_FOLDER%>ws_one_day_statistics.jpg" data-fancybox data-caption="我在2021.4.30 下午三点时的工作情况" >
    					<img src="<%=SM.BASIC_FILES_FOLDER%>ws_one_day_statistics.jpg" />
  					</a>
      				<a href="<%=SM.BASIC_FILES_FOLDER%>note_book_example1.jpg" data-fancybox data-caption="我在2021.4.30 下午三点时的工作情况" >
    					<img src="<%=SM.BASIC_FILES_FOLDER%>note_book_example1.jpg" />
  					</a>
  					
					</div>
					<div class="footer_web_info_what_sm_to_do">
						编辑于 <span>2021.4.30</span>
					</div>
				</div>
				
				<div id="web_info_what_sm_to_src">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
						<p>
							程序员的圈子里一直流传着一个梗——“我有一个好的想法，现在只差一个程序员了”，我就是这句话里的程序员，也是说出这句话的人。			
						</p>
						<p>
							有趣的是，在编剧圈、小说界也流传着相似的话——“我有一个好的创意，现在只差一个编剧了”、“我有一个好的设定，现在只差一个作家/写手了”。我同样是这些话里的主体与客体。
						</p>
						<p>
							但这一点，好像就是我仅有能瞧得上自己的地方了。
						</p>
						<p>
							在2020年7月20号左右，我提出了离职，离职的原因听起来有些滑稽，我们公司之前要求我们每天墨墨背单词100个，但在那些天里加到了150个，我无法接受，于是决定了离职。
						</p>
						<p>
							我的BOSS也曾说过，假如他知道我因为背单词改到150个就离职，那么就还是改回100个呗，我为什么啥也没说就决定离职了呢。
						</p>
						<p>
							这其实是我跟我BOSS之间一直存在的矛盾，这种矛盾在我BOSS将100个单词要求上调到150个后，终于让我觉得无法调和，因此下定了决心离职。
						</p>
						<p>
							这种矛盾是对于我来说，写作与计算机之间的矛盾——我该如何分配我在这两件事情上的精力？					
						</p>
						<p>
							我的BOSS知道我在业余时间里会进行写作，但他一直希望我能把更多的时间放在计算机上，这能帮上他的忙，同时，他也觉得这对我是最好的选择。
						</p>
						<p>
							他总说我要把握好兴趣与职业的关系，但我觉得，他一直没理解写作对我的重要性，我觉得我身边的好多人都没能理解写作对我的重要性，我希望写作能成为我的职业，而我也不能放弃写作，至少在那时我是那么认为的。	
						</p>
						<p>
							这也是我曾经想过的一个问题，我在编程领域已是高屋建瓴，同时也能在编程中收获非凡的乐趣，而编程显然要比写作赚的钱多（事实上，我至今未因写作赚过一分钱），为何我还执着于写作不放呢？
						</p>
						<p>
							随着我在编程领域有了一定的高度，更准确地说，是在我领略到编程领域的风光之后（这其实也是我大学毕业、选择当程序员的原因之一），我心中渐渐冒出了一个想法：
						</p>
						<p>
							“没错，我是擅长编程，我也能从中得到乐趣，但那又怎样呢？我现在明明应该在创作一个非凡的故事，而不是在写代码。”
						</p>
						<p>
							这种想法在我每次看过一部好剧、或一部好电影之后，都会冒出来，并且愈演愈烈，而我那段时间恰好看了《良医》和《谁先爱上他的》，多么杰出的两部艺术作品！
						</p>
						<p>
							于是在一个晚上，好像是看过了《良医》的某一集后，我看着窗外的夜色，心中突然冒出了一句话——“理想之剑，真地可以划破漆黑长空吗？”
						</p>
						<p>
							这句话促使我创作了<pdf pdf="lxzj_script">《理想之剑》</pdf>这个故事。
						</p>
						<p>
							在这之前，我从没想过会写一个关于理想的故事，事实上，那之前我也一直没认为写作是我的理想，我一直以为理想应该是更大上的东西才对，例如，和所爱的人在一起，或是能和知己好友相住比邻。
						</p>
						<p>
							但创作的奇妙就在于此，当你创作一个东西时，它会促使你去思考、去怀疑、去质问， 在这个过程中，你会越来越了解它，越来越感悟它，最终有了自己独特的理解。
						</p>
						<p>
							而故事的创作即人的创作，它会让你思考自己，从而明白哪些东西对自己更重要，而哪些东西，只是他人的影响才掩盖了它无足轻重的本质。
						</p>
						<p>
							我是通过创作《理想之剑》，才领悟到写作是我的理想之一的。
						</p>
						<p>
							有趣的一点是，在这个故事中，我将主人公设定为一个33岁裸辞的男人，当时我只是想增加主人公的困难才采取这样的设定，但没想到，我后来也裸辞了。
						</p>
						<p>
							这就好像，如果你不相信一件东西，你就没办法真正书写它一样。我裸辞的决定是否受到了创作的影响尚未可知，但在我还工作的那段时间里，创作这个故事确实多有不顺，而在裸辞后，故事的创作就显得水到渠成了。
						</p>
						<a href="<%=SM.BASIC_FILES_FOLDER%>plan_in_writing_phase.jpg" data-fancybox data-caption="我在2020.8.2-2020.8.22 期间的工作情况" >
    						<img src="<%=SM.BASIC_FILES_FOLDER%>plan_in_writing_phase.jpg"  class="right_float_img"/>
  						</a>
						<p>
							总之，8月1号我正式离职，开始了居家创作之路，而我深知我需要找到一种方法，来让自己不至于迷失在完全支配时间的自由里。
						</p>
						<p>
							我第一天采取的是按时间划分工作内容的方式，结果很不理想，我很容易在应该工作的时间去打游戏/看美剧了，这在创作之中太常见了，当没有灵感的时候，你会备受煎熬地做各种无关紧要的小事。
						</p>
						<p>
							第二天我想到了我应当以时间的总额规定我一天的工作，这就是网站工作表模块的雏形，只不过当时我是用word文档来记录的，而在后来的工作中，发现效果着实不错。
						</p>
						<p>
							右图，就是我在创作《理想之剑》期间制定的计划。
						</p>
						<p>
							当时每天的工作我都需要人工计算，虽然并不困难，但是程序员当多了，就会越来越受不了重复性的事情，因此在这期间，我就动起了为其写一个软件的念头，只是那时还不知道什么时候能够付诸行动。
						</p>
						<p>
							我是在8月19号完成了《理想之剑》的创作，但在完成之后，却第一次失去了对故事的信心。
						</p>
						<p>
							我并非不相信我所写的是一个好的故事，而只是不相信它能卖出去，它会被人赏识吗？是否还是如以前一样，让我不得不为其改编成小说才能具有一丁点价值呢？
						</p>
						<p>
							在创作<pdf pdf="rsdqcnh_script">《如水的青春年华》</pdf>这个故事的期间及完成后的一段时间内，我都觉得我写了一个非常好的故事，它会帮我叩开编剧界的大门，而现在，我只觉得它是一个好的故事了。
						</p>
						<p>
							我不忍这个故事石沉大海而将其改编成小说，<pdf pdf="rsdqcnh_novel">《如水的青春年华（小说）》</pdf>，但结果还是没有任何变化，我没能从写作中挣取一分钱，而显然，我还得生活。
						</p>
						<p>
							非常滑稽的是，在我当初决定辞职的时候，我曾告诉自己：“我的下一份工作，无论薪水如何，只要还是在当程序员，我就算失败了”，我那时莫名以为我会凭借《理想之剑》，真地划破漆黑长空，但在完成它的一刻，才惊觉连我自己都不相信它能成功。
						</p>
						<p>
							现在我创业了，我能说自己是在当老板、而不算当程序员，因此不算失败吗？
						</p>
						<p>
							8月20号我去了北京，去看一个一直说要来南京看我的好朋友，为了嘲笑他言而无信，为了散心，也为了想明白接下来的路该怎么走。
						</p>
						<p>
							到了北京，我发现我竟然喜欢上了这个城市，我明明记得小时候来这里的时候黄沙漫天，现在却完全变了模样 ，这算得上意外之喜。
						</p>
						<p>
							我跟我朋友在晚上散步的时候，说我没准会在北京找个工作，我也曾不止一次这么想过，但他指着路上来往骑车的人，说这些都是刚下班的。
						</p>					
						<p>
							我畏惧了，除了杭州，北京是996最严重的地方了吧？
						</p>					
						<p>
							我无法接受996，除了它违法的原因之外，更是由于我觉得至少对于程序员来讲，996是再愚蠢不过的事情了。
						</p>					
						<p>
							心情的好坏会影响程序的好坏，这或许是不写代码的人难以理解的：实现同一个功能，至少会有几十种实现方式的可能，而在这诸多可能之中，或许只有几个，甚至仅仅只有一个（Python的创始人持有这种观点）才是最符合程序本质的，它支撑着软件的健壮。
						</p>					
						<p>
							这是编程的艺术，一个对代码失去了热情、甚至产生了厌倦的人，如何能书写艺术？
						</p>
						<p>
							更别说在软件开发中，程序员要面对无数的选择，在神智清醒的时候，都难以次次做出正确的决定，而每一次错误的决定，都迟早爆发出更严重的后果。
							在疲倦的时候写代码，无异于把命运交给了赌徒，而我甚至在有一丁点犯困的时候，都不敢书写核心的逻辑。
						</p>
						<p>
							我在北京工作的想法因为担忧996而夭折了，而我的编剧之路还像空中楼阁一样缥缈。这时发生了一件事，让我终于放弃了写作，放弃了我的理想。
						</p>
						<p>
							我在8月28号的时候，那是一个星期五，和朋友聊天间，突发奇想我既然来了北京，为什么不去现场投稿呢？网上投稿会石沉大海，但现场投稿，至少会有一个回信吧？
						</p>
						<p>
							我抱着这样的想法精心打印了我的剧本，然后在第二天，跑到了光线传媒的公司，我还记得绕着公司转了三圈，然后还照着路牌拍了两张照片，才鼓起勇气走入了公司的大门。
						</p>
						<p>
							他们公司竟然休息，原来在北京996的，好像只有程序员及其相关的职业，这太搞笑、又太讽刺了。
						</p>
						
						<p>
							回去的时候，我在一条小河边的石台上躺了好久，我回想起我多年的创作生涯，想自己在写作上的得与失，我确实收获了常人难以想象的快乐，但从世俗意义上，
							它就是一个接一个的失败、失败、失败；直到现在，连我自己都不相信我在写作上能有所回报，我只能说我到现在也不后悔，但我真地，真地得放弃了。 
						</p>
						<p>
							我直到晚上回到酒店，还在想着我是否要放弃写作。我想起了我的BOSS，在与他合作的快三年的时间里，我一直觉得他不适合当老板，不适合创业：他比我还聪明一些，这么聪明的人，如果再加上勤奋努力，那么还怎么能明白刚过易折的道理呢？
						</p>
						<p>
							而他做程序员挣的钱明明比他创业挣得多，也轻松得多，他为什么还执迷于创业不放呢？
						</p>
						<p>
							我曾对他说起过这件事，他却始终没有放弃，这在我看来，虽然壮烈却并不明智——但到了此刻，我不禁在想，我对写作的执迷是否也同他对创业的执迷一般，壮烈而又不明智呢？ 
						</p>
						<p>
							我就这样终于放弃了写作，而当我放弃后，我忽然感觉我已没有必要再留在南京，于是回到了青岛准备找工作，同时，也想跟在青岛的同学打够级了。
						</p>
						<p>
							结果我回青岛的第二天，我的一个同学就又离开了青岛，这下也凑不齐打够级的人了；而至于青岛的工作，我又想到了海尔因为员工午休就把人家开除的事，有着这样环境的青岛，能找到合适的工作吗？
						</p>
						<p>
							所以我决定创业了，假如自己开个有双休的公司，应该就能回青岛了吧。
						</p>
						<p>
							而最初，我是希望边工作边创业的，所以在青岛待了两天，我就又回了南京，我那时期望能在南京能找到合适的工作。
						</p>
						<p>
							但显然，我之前能够双休的程序员工作是罕见的，在之后的一周半里，我有过两次面试，第一次感觉不错，但在最后明确表示双休是底线后，也就没有了回应；而第二次，我被一个特别垃圾的程序员面试了，
							我非常理解程序员喜欢装逼，难免有些傲慢（事实上，我觉得这是程序员的职业特质），但当你自己水平很差的时候，这件事就变得很恶心了。
						</p>
						<p>
							我觉得自己被冒犯了，也明白了在南京恐怕还是难以找到合适的工作，而再想找，恐怕得去苏州了，我也厌倦了再去熟悉一个城市，所以索性就全心全意的开发网站了。
						</p>
						<p>
							而在后来独立开发这个网站的过程中，所遇到的困难，无论是设计上的，还是技术上的，也确实出乎了我的预料，这让我知道，我假如按之前设想的边工作边开发，一定是没法完成这件事。因此从这个角度讲，我被一个面试恶心了这件事，也算因祸得福了。
						</p>
						<p>
							以上就是本网站的起源，一个关于失败与放弃理想的故事。
						</p>
						<p> 
							除此之外，我把创作过的作品，都写在了<span class="common_blue_font common_hover go_click_connection_btn">联系方式</span>下，虽然有些可悲，但我还是想自己的作品能被更多人看到。
						</p>
						<p>
							阅读到这里，您应该会知道，我写在首页的话并没有骗人，虽然我失败、贫穷依旧，但我确实是一位编剧、作家、程序员、设计师，我喜欢这样的工作方法，并不仅是因为它是由我发明的。
						</p>
						<p>	
							而我也相信：每一个使用这种工作方法的人，只要他能够<em>诚实</em>地对待自己，对待知识，那么迟早会收获到非同寻常的东西。
						</p>
						<p>
							但愿每个人到故事的结局，都能心想事成。
						</p>												
					</div>
				</div>
				
				<div id="web_info_sm_spirit">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
						<p>关于网站的精神，无关乎我个人，而只是我想借由这个网站传递给大家的。我不知道这个网站能存在多长时间，也不知道我还能脱产开发它多久，但我希望至少能够将网站的精神留传下来。</p>
						<div class="web_spirit_title"><em>Fight</em></div>
						<p>
							我信仰公平，这应该是从我很小的时候看过的一部小说——《高手寂寞》里学到的，小说里讲的是朋友之间的公平；我活到现在，一直将公平视为我的原则，我的底线，我的信仰。
						</p>
						<p>
							而这些年，我在网上接触到了太多不公平的新闻，让我生气，让我愤怒，无论是甚嚣尘上的福报之说，还是因为拒绝加班而被罚款的荒唐闹剧。
						</p>
						<p>
							而更让我生气、更让我愤怒的是，我看着新闻里做这些事、说这些话的人，他们一脸得意洋洋，他们似乎认为他们是在施舍智慧，是在谆谆善诱。
						</p>
						<p>
							For God's Sake,FTMDX!
						</p>
						<p>
							这些让我生气的事情层出不穷，我听说杭州有一家公司用机器学习开发出了一个监控学生走神的软件。	
						</p>
						<p>
							开发这个软件的程序员是没有童年吗？这个公司就这么缺钱吗？假如一个公司需要开发这样的软件才能存活下去，那它活该倒闭。
						</p>
						<p>
							还有编剧的署名权，当你出售一部剧本时，人家问你是要钱还是要名。那当然是都要了，那也理应都要啊。
						</p>
						<p>
							还有小说的作者，凭什么一个网站能拥有一个作者个人作品的所有版权，甚至从法律意义上讲，作者只不过是替网站代写的写手。
						</p>
						<p>
							凭什么？而这些都应是法律的责任，法律理应保证底线，但底线在哪里？
						</p>
						<p>
							而为了能够改变这些现象，我认为关键在于<em>发声</em>，在于<em>Fight</em>。											
						</p>
						<p>
							我认为只有越来越多的人认识到这些现象背后的不公平，并且不止是为了别人、也是为了自己而去发声，去Fight，才会让这种现象有所改变，这不止是为了我们一代人，也是为了下一代人能够生存在一个更公平的环境中，这才是人类传承的意义。
						</p>
						<p>
							当我听说谷歌的程序员为抗议AI武器化的项目而辞职时，我觉得这才是我敬佩的程序员，这才是我敬佩的人，这无关乎他们能拿到多大的package，而在于当他们有能力捍卫自己的底线时，他们去捍卫了，他们去Fight了。
						</p>
						<p>
							而虽然“不作恶”的原则已经从谷歌的行为准则中删除了，但我从来不认为那是对的。我认为“不作恶”是每一个程序员的底线，也是每一个做着承载前人智慧工作人的底线。
						</p>
						<p>
							无论是技术、艺术还是政治，我们都站在了前人的肩膀之上，也就应该为后来的人同样提供一个正确的、公平的、不作恶的肩膀。
						</p>
						<p>
							这是我认为这个网站首要的精神，<em>Fight</em>。
						</p>
						<div class="web_spirit_title"><em>Engage</em></div>
						<p>
							这是我的BOSS教给我的，假如用中文来翻译的话，我认为比较准确的翻译应当是<em>"积极地面对"</em>。
						</p>
						<p>
							在我的编程生涯中，我越来越意识到Engage的重要性。这世上恐怕没有哪一个职业会像程序员一样，时刻处在一团迷雾之中，无论是改一个莫名其妙的BUG，还是抽象一个看似不可能的事物。
						</p>
						<p>
							而解决这些问题都需要Engage，都需要耐下心来，一点点摸索出最正确的路，这需要诚实，需要脚踏实地，而不能逃避，不能在没想清楚的情况下就匆匆动手。
						</p>
						<p>
							写作也是一样，这句台词，这句对白，我真地想清楚它的用处了吗？我是任迸发的创意完全支配了自己，还是用理智挑选出了里边最合理的部分？
						</p>
						<p>
							这些都影响着最后结果的好坏，而显然，人生也是如此。
						</p>
						<p>
							有多少人活了半生后悔恨？有多少人临死之际遗憾？又有多少人终其一生，都在追逐着无关紧要的幻光，而当真正重要的东西失去之后，才追悔莫及？
						</p>
						<p>
							我觉得这都是在于他们没有Engage自己的人生，人生比程序容易逃避得多，也难得多，但我觉得即便如此，人也应当尽力Engage人生的各种困难，正如我在GayHub上为这个项目写下的About——if u can't find a wished job, make one.
						</p>
						<p>
							诚实而又不要逃避地找出解决之道吧。
						</p>
					</div>
					
				</div>
				
				<div id="web_info_i_want_to_donate">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
						<p>
							您的捐款将用来主要用来支撑我的生活费用以及服务器的运维费用，它决定着我是否不得不为生计打算而再去找一份工作。
						</p>
						<p>
							现在网站服务器采用的是阿里云最低档的配置，因此，假如网站卡慢，那并非我程序写得糟糕，而只是因为我没有钱，以及阿里云实在是太贵了。
						</p>
						<p>
							同样也希望您可以帮忙推广本网站，这是一个不错的软件，我已经开始使用它管理我日常的工作了，而只要我还在使用，我就会不断完善它，并且添加新的功能，以期让技术帮助生活更加轻松，而非相反。
						</p>
						<p>
							现在的支付接口只是我的个人账户，这是由于现如今注册公司虽然已无需注册资本，但仍需办公地点，而我没钱支付租金，因此无法注册公司（短信使用的是本人名字也是由于这个原因，可不是我自恋哦）；当有了一定资金后，我会尽快注册一个公司，从而让网站更加规范。
						</p>
						<div class="web_info_zhifu_container">
							<img src="<%=SM.BASIC_FILES_FOLDER%>zhifubao.png" />
							<img src="<%=SM.BASIC_FILES_FOLDER%>weixin.png"  />		
						</div>
						
					</div>
				</div>
				
				<div id="web_info_connect_with_us">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
						<div class="web_info_connect_unit">
							<span>QQ</span>
							<span id="web_info_qq_ahchor">914748956</span>
							<span class="common_copy_font" copy_id="web_info_qq_ahchor">复制</span>
							<span class="web_info_copy_hint"></span>
						</div>
						<div class="web_info_connect_unit">
							<span>邮箱 </span>
							<span id="web_info_email_ahchor">wtg92@126.com</span>
							<span class="common_copy_font" copy_id="web_info_email_ahchor">复制</span>
							<span class="web_info_copy_hint"></span>
						</div>
						<div class="web_info_connect_unit">
							<span>斗鱼直播间</span>
							<span id="web_info_douyu_anchor">9389659</span>
							<span class="common_copy_font" copy_id="web_info_douyu_anchor">复制</span>
							<span class="web_info_copy_hint"></span>
						</div>
						
						
						<div class="web_info_my_work_container">
							<div class="web_info_my_work_title">我的作品列表（由新到旧）</div>
							<div class="web_info_my_work_content"><span class="common_open_new_window" href="https://www.xrzww.com/bookdetail/95702">陆想的史诗（连载中-异术超能-长篇小说）</span></div>
							<div class="web_info_my_work_content"><pdf pdf="howToBeAGoodCoder">如何成为一流程序员？Java，JavaScript（已完成-计算机技术）</pdf>注：积极寻求出版</div>
							<div class="web_info_my_work_content"><pdf pdf="原点（The Essence Of Everything）">原点（The Essence Of Everything）（已完成-哲学）</pdf>注：积极寻求出版</div>
							<div class="web_info_my_work_content"><span class="common_open_new_window" href="https://www.xrzww.com/bookdetail/27200">低俗家庭（未完成-搞笑生活-长篇小说）</span></div>
							<div class="web_info_my_work_content"><span class="common_open_new_window" href="https://www.xrzww.com/bookdetail/19237">换天记（未完成-玄幻-长篇小说）</span></div>
							<div class="web_info_my_work_content"><pdf pdf="理想之剑">理想之剑（已完成-励志-剧本）</pdf></div>
							<div class="web_info_my_work_content"><span class="common_open_new_window" href="https://www.xrzww.com/column/userpage?id=42">如水的青春年华（已完成-爱情-小说）</span></div>
							<div class="web_info_my_work_content"><pdf pdf="如水的青春年华_剧本">如水的青春年华（已完成-爱情-剧本）</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="许氏异闻-新租客">许氏异闻-新租客（已完成-悬疑爱情短片-剧本）</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="许氏异闻-少女的祈祷">许氏异闻-少女的祈祷（已完成-悬疑短片-剧本）</pdf></div>
							<div class="web_info_my_work_content"><span class="common_open_new_window" href="https://www.xrzww.com/column/userpage?id=42">死无对证（已完成-校园悬疑-小说）</span></div>
							<div class="web_info_my_work_content"><pdf pdf="死无对证_剧本">死无对证（已完成-校园悬疑-剧本）</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="最佳损友的爱情故事">最佳损友的爱情故事（已完成-喜剧-剧本）</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="中国设施农业现状、基于五天的实习经历">中国设施农业现状、基于五天的实习经历（学术，大三写的实习报告）</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="男人梦">男人梦（已完成-都市情感-小说）——大三写的，自我感觉是黑历史</pdf></div>
							<div class="web_info_my_work_content"><pdf pdf="转烛">转烛（未完成-玄幻-小说）-高中毕业写的第一本小说，本以为是黑历史，但在回看之下还觉得不错——为什么当时没写下去？</pdf></div>
												
						</div>
						<!-- <div class="web_info_about_work_title">关于作品</div>
						<p>
							其实我还写过两篇小说《转烛劫》和《男人梦》，大家去网上搜索应该能搜得到，但那是我太年轻的时候写的了，我自己都看不下去，就不放在这里了。
						</p>
						<p>
							《许氏异闻》系列是我有段时间非常喜欢看《九号秘事》（但感觉第五季没有之前精彩了），模仿其场景单一的故事特点而创作的。
						</p>
						<p>如果对我的剧本感兴趣，去<span class="common_open_new_window" href="www.tuluoluo.com">土罗罗</span>上直接联系我即可，这是我写剧本非常喜欢用的网站。</p>		 -->				
					</div>
				</div>
				
				
				
				<div id="web_info_system_info_for_phone">
					<div class="web_info_sub_header_title"></div>
					<div class="web_info_sub_header_body">
						<p>
							建议用<em>手机浏览器</em>访问登录。推荐<span class="common_open_new_window" href="https://www.xbext.com/">X浏览器</span>
						</p>
						<p>
							X浏览器的下载二维码。
						</p>
						<p>
							<img src="https://www.xbext.com/img/scan_qrcode.png" />
						</p>
						<h5>背后有趣的事儿</h5>
						<p>
							我是看到了知乎的一篇文章——<span class="common_open_new_window" href="https://zhuanlan.zhihu.com/p/285778671">独立开发者因为广告拦截被优酷起诉100万元</span>，得知了这个浏览器。
						</p>
						<p>
							看完之后，立马下载了X浏览器，以表支持；意外发现X浏览器十分好用，至少，用来使用本网站的服务没有问题。
						</p>
						<p>
							X浏览器的开发者和我一样，都是独立开发者；但开发浏览器，是一件酷得多、也难得多的事情，我既自愧不如，也敬佩不已，只能在这里聊表支持。
						</p>
						<p>
							而一谈到手机浏览器，就不得不提到微信小程序，它实际上，是一种只能依托于微信平台的互联网网站。
							
						</p>
						<p>
							在技术的角度来讲，微信小程序的发明，绝对称得上一个倒退；但从商业的角度讲，它也确实能带来垄断，是一项非凡的创意。
						</p>
						<p>
							但当这两者比较的时候，我更偏爱于技术一些，所以我去餐馆点餐的时候，能不用微信小程序，就不用微信小程序。
						</p>
						<p>
							另外，对于手机端的使用，最合理的做法，应当是为其开发一款专门的APP。
						</p>
						<p>
							但我估计我得需要至少三个月才能完成这件事情，而我现在已经无法拿出三个月的时间来专门处理它了。
						</p>
						<p>
							因此，如果您在用浏览器的使用过程中，遇到什么令你难受的问题，还请包涵，多谢。
						</p>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" flush="false" />
	
	<div id="web_info_pattern_container" class="common_pattern_container">
		<div class="web_info_title_container">
			<div class="web_info_title"></div>
			<div class="web_info_items"></div>
		</div>
	</div>
</body>
</html>