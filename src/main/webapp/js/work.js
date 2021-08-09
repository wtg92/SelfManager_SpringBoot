const WORK_NAMESPACE = {

}

$(function(){

    openWsContainer();
    loadWorkSheetInfosRecentlyForFirstLoading();
    loadActivePlans();
    showWsInfosRecently();

    $(".work_switch_to_show_plan_sub_container").click(switchToShowPlansContainer);
    $(".work_switch_to_show_ws_sub_container").click(switchToShowWSContainer);
    $("#work_open_ws_today_button").click(openWorkSheetToday);

    $("#work_create_plan_button,.work_plan_create_plan_in_hint").click(openCreatePlanDialog);

    $("#work_set_start_date_today_for_create_plan").click(changeStartDateWithTodayForCreatePlan);

    $("#work_create_plan_form [name='end_date_null']").click(switchToDisableEndateForCreatePlan);
    $("#work_commit_create_plan_button").click(createPlan);

    $(".work_create_plan_entity [name='name']").blur(function(){
        testCreatePlanFormat(this,(text)=>checkPlanName(text),"1-20个字符");
    });
    $(".work_create_plan_entity [name='start_date']").blur(function(){
        testCreatePlanFormat(this,checkDateFormat,"日期格式非法，建议使用插件来选择日期，如果没有弹出插件，请更新浏览器版本");
    });
    $(".work_create_plan_entity [name='end_date']").blur(function(){
        testCreatePlanFormat(this,checkDateFormat,"日期格式非法，建议使用插件来选择日期，如果没有弹出插件，请更新浏览器版本");
    });

    $("#switch_to_show_ws_infos_recently").click(switchToShowWsInfosRecently);

    $(".work_leran_to_use_work_sheet").click(()=>$("#work_getting_start_dialog").modal("show"));

    $("#work_plan_cards_container").on("click",".work_plan_card_content",switchToSelectPlanCard)
        .on("click",".work_abandon_plan",abandonPlan)
        .on("click",".work_see_plan_detail",seePlanDetail)
        .on("click",".work_edit_plan_button",editPlanDetail)
        .on("click",".work_finish_plan",finishPlan);

    $("#work_ws_sub_left_container_footer .work_see_more_ws").click(seeMoreWsInfos);

    $("#work_ws_sub_left_container_header").click(closeWsActualContainer);
    $("#work_ws_sub_left_container_body").on("click",".work_ws_date_cotnainer",showWsDetail);

    $("#work_open_plan_dept_dialog_btn").click(openPlanDeptDialog);

    $("#work_open_ws_stat_of_date_range_dialog_btn").click(openWSOfDateRangeDialog);
});

function showWsDetail(){
    openWsActualContainer();
    let isWsToday = new Date(parseInt($(this).attr("ws_date"))).isSameByDate(new Date());    
    $("#work_ws_count_ws_today_alert").toggle(isWsToday);
    $("#work_ws_count_ws_prev_day_alert").toggle(!isWsToday);

    sendAjax("CareerServlet","c_load_work_sheet_count",{
        "date":new Date(parseInt($(this).attr("ws_date"))).getDateStr()
    },(data)=>{
        if(isWsToday){
            /*-1是由于要减去自己*/
            $("#work_ws_count_ws_today_alert em").text(data.rlt-1);
        }else{
            $("#work_ws_count_ws_prev_day_alert em").text(data.rlt);
        }
    })

    $(this).addClass("common_prevent_double_click");
    let wsId = $(this).attr("ws_id");
    drawWorkSheetDetail(wsId,()=>$(this).removeClass("common_prevent_double_click"));    
}


function seeMoreWsInfos(){
    let page = $(this).attr("start_page");
    let loadingClass = "common_prevent_double_click";
    let srcText = $(this).text();
    $(this).text("加载中......").addClass(loadingClass)
    sendAjax("CareerServlet","c_load_work_sheet_infos_recently",{
        "page":page
    },(data)=>{
        $(this).text(srcText).removeClass(loadingClass);
        fillWsDateInfosContainer(data,page);
    })
}



function openWorkSheetToday(){
    let $slectedPlan = $("#work_plan_cards_container .work_plan_card_container[select='true']");
    if($slectedPlan.length == 0){
        $slectedPlan = $("#work_plan_cards_container .work_plan_card_container");
        if($slectedPlan.length != 1) {
            alertInfo("请选择一个计划开启工作表");
            return;
        }
    }
    let planName = $slectedPlan.find(".work_plan_card_name").text();
    confirmInfo("确定以 "+planName+" 为今日计划，开启工作表吗？",()=>{
        let planId = $slectedPlan.attr("plan_id");
        sendAjax("CareerServlet","c_open_work_sheet_today",{
            "plan_id":planId
        },(data)=>{
            loadWorkSheetInfosRecently_render(data);
            closeAllPlanCards();
            confirmInfoSecond("您要直接开始工作吗？", () => {
                openWsContainer();
                closePlansContainer();
                $("#work_ws_main_container").get(0).scrollIntoView();
                $("#work_ws_sub_left_container_body").find(".work_ws_date_cotnainer").filter((i, v) => new Date(parseInt($(v).attr("ws_date"))).isSameByDate(new Date())).click();
            })
        })
    });
}


/** 
 * 当今天已经开了ws后 closePlans openWsContainer 
 * 否则 openPlan
 **/
function loadWorkSheetInfosRecentlyForFirstLoading(){
    closePlansContainer();
    closeWsActualContainer();

    $("#work_ws_sub_left_container_footer>div").hide();

    let $loadingContainer = $("#work_ws_sub_container .common_loading_message");
    let $wsMainContainer = $("#work_work_sheet_main_body");
    $wsMainContainer.hide();
    $loadingContainer.show();

    sendAjax("CareerServlet","c_load_work_sheet_infos_recently",{
        "page":0
    },(data)=>{
        $wsMainContainer.show();
        $loadingContainer.hide();
        loadWorkSheetInfosRecently_render(data);
        let hasTodayWS = data.some((one)=>{
            let today = new Date();
            return new Date(one.date).isSameByDate(today);
        })
        if(!hasTodayWS){
            openPlansContainer();
        }else{
            /*自动点击今天的*/
            $("#work_ws_sub_left_container_body").find(".work_ws_date_cotnainer").filter((i,v)=> new Date(parseInt($(v).attr("ws_date"))).isSameByDate(new Date())).click();
        }
    })
}

function openWsActualContainer(){
    $("#work_ws_with_actual_content_container").show();
    $("#work_ws_sub_right_container .work_ws_blank_container").hide();
}

function closeWsActualContainer(){
    $("#work_ws_with_actual_content_container").hide();
    $("#work_ws_sub_right_container .work_ws_blank_container").show();
}

function loadWorkSheetInfosRecently_render(data,page){
    let pageOfData = page == undefined ? 0 : page;

    let hasTodayWS = data.some((one)=>{
        let today = new Date();
        return new Date(one.date).isSameByDate(today);
    })
    $("#work_open_ws_today_button").toggle(!hasTodayWS);
    $("#work_ws_sub_left_container_body").empty();
    fillWsDateInfosContainer(data,pageOfData);
}
function fillWorkListUnit(ws,$unit){
    $unit.attr({
        "ws_id":ws.id,
        "ws_date":ws.date
    }).find(".work_ws_date_cotnainer_title").text(new Date(ws.date).toChineseDate())
        .end().toggleClass("warning_date",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVERDUE)
        .prop("title",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVERDUE ? "该日工作存在未完成项，请尽快处理或同步进历史欠账":"")
}

function refreshWokrListUnitForExternalInvoker(ws){
    let $target = $("#work_ws_sub_left_container_body .work_ws_date_cotnainer[ws_id='"+ws.id+"']");
    if($target.length==0){
        console.error("refresh不存在的WorkListUnit")
        return;
    }
    fillWorkListUnit(ws,$target);
}

function fillWsDateInfosContainer(data,pageOfData){
    data.forEach(ws=>{
        let $dateUnit = $("#work_pattern_container").find(".work_ws_date_cotnainer").clone();
        fillWorkListUnit(ws,$dateUnit);
        $("#work_ws_sub_left_container_body").append($dateUnit);
    })

    let wsSheetOfOnPage =  parseInt($("#default_ws_limit_of_one_page").val());
    
    $("#work_ws_sub_left_container_footer .work_see_more_ws").attr("start_page",parseInt(pageOfData)+1).toggle(wsSheetOfOnPage == data.length);
    $("#work_ws_sub_left_container_footer .work_no_more_ws").toggle(wsSheetOfOnPage > data.length);
    if(wsSheetOfOnPage < data.length){
        throw "this is impossible!"+wsSheetOfOnPage+" vs "+data.length;
    }    
}

function editPlanDetail(){
    preventDoubleClick(this);
    let id = $(this).parents(".work_plan_card_container").attr("plan_id");
    openPlanDialog(id,true); 
}

function seePlanDetail(){
    preventDoubleClick(this);
    let id = $(this).parents(".work_plan_card_container").attr("plan_id");
    openPlanDialog(id,false);
}

function finishPlan(){
    confirmInfo("确定完成吗？（该计划的状态会变为完成,并且把结束日期修改为今天）",()=>{
        let id = $(this).parents(".work_plan_card_container").attr("plan_id");
        sendAjax("CareerServlet","c_finish_plan",{
            "plan_id" : id
        },loadActivePlans);
    });
}



function abandonPlan(){
    confirmInfo("确定废除吗？（如果存在基于该计划生成的工作表，该计划的状态会变为废除,并且把结束日期修改为今天；否则，该计划会被直接删除）",()=>{
        let id = $(this).parents(".work_plan_card_container").attr("plan_id");
        sendAjax("CareerServlet","c_abandon_plan",{
            "plan_id" : id
        },loadActivePlans);
    });
}


function switchToSelectPlanCard(){
    let $container = $(this).parents(".work_plan_card_container");
    let selected = parseToBool($container.attr("select"));
    if(!selected){
        /*同一时间 只允许选一张卡片*/
        closeAllPlanCards();
        openPlanCard($container);
    }else{
        closePlanCard($container);
    }
}

function closeAllPlanCards(){
    $("#work_plan_cards_container .work_plan_card_container[select='true']").each((i,v)=>{
        closePlanCard(v);
    })
}


function openPlanCard(dom){
    $(dom).attr("select",true);
}

function closePlanCard(dom){
    $(dom).attr("select",false);
}



function loadActivePlans(){
    let $content = $("#work_plan_cards_container .work_plan_cards_content");
    $content.empty();
    let $loadingInfo = $("#work_plan_cards_container .common_loading_message").show();
    sendAjax("CareerServlet","c_load_active_plans",{},(data)=>{
        $loadingInfo.hide();
        $("#work_plan_mes_when_zero_plan").toggle(data.length == 0);
        const NUM_FOR_ONE_ROW = 5;
        $(data.sort((a,b)=>{
            /*根据seqWeight 决定瞬息*/
            return b.seqWeight-a.seqWeight;
        })).each((i,plan)=>{
            let $card = $("#work_pattern_container").find(".work_plan_card_container").clone();
            $card.attr("plan_id",plan.id).toggleClass("work_right_Card",(i+1) % NUM_FOR_ONE_ROW == 0)
                .find(".work_plan_card_name").text(plan.name).prop("title",plan.name).end()
                .find(".work_plan_card_state>span").text(plan.state.name).css(getFontColorAndBackgroudColor(plan.state.color));
            $card.find(".work_plan_card_start_date_content").html(new Date(plan.startDate).toChineseDate()).end()
                .find(".work_plan_card_end_date_content").html(new Date(plan.endDate).toChineseDate("<em title='将一直被系统认为进行中'>至今</em>")).end()
                .find(".work_plan_card_create_time_content").text(new Date(plan.createTime).toChineseDate()).end()

            $content.append($card);
        })
    });
}


function testCreatePlanFormat(inputDom,checkFunc,errorAppendInfo) {
    /*假如清空了就不校验了*/
    let text=  $(inputDom).val();
    let test =text.length == 0 ? true : checkFunc(text);
    let $container = $(inputDom).parents(".work_create_plan_entity");
    $container.attr("test", test);
    let $errorContainer = $container.find(".work_create_plan_error_container");
    $errorContainer.toggle(!test);
    if (!test) {
        $errorContainer.text("格式有误，" + errorAppendInfo);
    } else {
        $errorContainer.text("");
    }
    return test && text.length != 0;
}


function createPlan(){
    let test = $("#work_create_plan_form .work_create_plan_entity").get().every(one=>parseToBool($(one).attr("test")));
    if(!test){
        alertInfo("请根据提示填写好必要信息（如没想好结束日期，请勾选对应按钮）");
        return;
    }

    let buttonText = $("#work_commit_create_plan_button").text();
    $("#work_commit_create_plan_button").text("创建中......").addClass("common_waiting_button");

   let param = $("#work_create_plan_form").serializeArray();

   sendAjax("CareerServlet","c_create_plan",param,createPlan_render,true,()=>{},()=>$("#work_commit_create_plan_button").text(buttonText).removeClass("common_waiting_button"));
}

function createPlan_render(data){
    $("#work_create_plan_dialog").modal("hide");
    loadActivePlans();

    confirmInfo("创建成功，您要直接编辑计划内容吗？",()=>{
        openPlanDialogByDate(data,true,true);
    },"直接编辑","以后再说");
}



function switchToDisableEndateForCreatePlan(){
    let checked = $(this).prop("checked");
    let $container = $(this).parents(".work_create_plan_entity");
    if(!checked){
        $container.removeAttr("test");
    }else{
        $container.find("[name='end_date']").val("").end()
            .find(".work_create_plan_error_container").text("").end()
            .attr("test",true);
    }
    
    $container.toggleClass("end_date_null",checked);
}


function changeStartDateWithTodayForCreatePlan(){
    $("#work_create_plan_form [name='start_date']").val(new Date().getDateStr()).parents(".work_create_plan_entity").attr("test",true);
}



function openCreatePlanDialog(){
    $("#work_create_plan_form").find("[type='text'],[type='date'],textarea").val("");
    let $switchCheckbox = $("#work_end_date_null_for_create_plan");
    if($switchCheckbox.prop("checked")){
        $switchCheckbox.click();
    }
    $("#work_create_plan_dialog").modal("show");
}

function switchToShowWSContainer(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        closeWsContainer();
    }else{
        openWsContainer();
    }   
}
function switchToShowWsInfosRecently(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        hideWsInfosRecently();
    }else{
        showWsInfosRecently();
    }     
}

function hideWsInfosRecently(){
    $("#work_ws_sub_left_container").hide();
    $("#switch_to_show_ws_infos_recently").text("显示"+$("#work_ws_sub_left_container_header").text().trim()).attr("open",false);
}


function showWsInfosRecently(){
    $("#work_ws_sub_left_container").show();
    $("#switch_to_show_ws_infos_recently").text("隐藏"+$("#work_ws_sub_left_container_header").text().trim()).attr("open",true);
}


function closeWsContainer(){
    $("#work_ws_sub_container").hide();
    $(".work_switch_to_show_ws_sub_container").text("展开").attr("open",false);
}

function openWsContainer(){
    $("#work_ws_sub_container").show();
    $(".work_switch_to_show_ws_sub_container").text("收起").attr("open",true);
}


function switchToShowPlansContainer(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        closePlansContainer();
    }else{
        openPlansContainer();
    }
}



function openPlansContainer(){
    $("#work_plan_sub_container").show();
    $(".work_switch_to_show_plan_sub_container").text("收起").attr("open",true);
}


function closePlansContainer(){
    closeAllPlanCards();
    $("#work_plan_sub_container").hide();
    $(".work_switch_to_show_plan_sub_container").text("展开").attr("open",false);
}