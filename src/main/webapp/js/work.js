const WORK_NAMESPACE = {
    OPENED_WS_ID : null
}

$(function(){
    //DONE
    openWsContainer();
    //DONE
    $(".work_switch_to_show_plan_sub_container").click(switchToShowPlansContainer);
    //DONE
    $(".work_switch_to_show_ws_sub_container").click(switchToShowWSContainer);
    //DONE
    loadActivePlans();
    //DONE
    $("#work_plan_cards_container").on("click",".work_plan_card_content",switchToSelectPlanCard)
        //DONE
        .on("click",".work_abandon_plan",abandonPlan)
        //DONE
        .on("click",".work_see_plan_detail",seePlanDetail)
        //DONE
        .on("click",".work_edit_plan_button",editPlanDetail)
        //DONE
        .on("click",".work_finish_plan",finishPlan);

    //DONE
    $("#work_create_plan_button,.work_plan_create_plan_in_hint").click(openCreatePlanDialog);

    //DONE
    $("#work_set_start_date_today_for_create_plan").click(changeStartDateWithTodayForCreatePlan);
    //DONE
    $("#work_create_plan_form [name='end_date_null']").click(switchToDisableEndateForCreatePlan);

    //DONE
    $("#work_commit_create_plan_button").click(createPlan);
    //DONE
    $(".work_create_plan_entity [name='name']").blur(function(){
        testCreatePlanFormat(this,(text)=>checkPlanName(text),"1-20个字符");
    });
    //DONE
    $(".work_create_plan_entity [name='start_date']").blur(function(){
        testCreatePlanFormat(this,checkDateFormat,"日期格式非法，建议使用插件来选择日期，如果没有弹出插件，请更新浏览器版本");
    });
    //DONE
    $(".work_create_plan_entity [name='end_date']").blur(function(){
        testCreatePlanFormat(this,checkDateFormat,"日期格式非法，建议使用插件来选择日期，如果没有弹出插件，请更新浏览器版本");
    });

    //DONE
    $(".work_leran_to_use_work_sheet").click(()=>$("#work_getting_start_dialog").modal("show"));

    //DONE
    showWsInfosRecently();
    //DONE
    $("#switch_to_show_ws_infos_recently").click(switchToShowWsInfosRecently);
    //DONE
    $("#work_ws_sub_left_container_footer .work_see_more_ws").click(seeMoreWsInfos);
    //DONE
    $("#work_ws_sub_left_container_header").click(closeWsActualContainer);
    //DONE
    $("#work_open_ws_stat_of_date_range_dialog_btn").click(openWSOfDateRangeDialog);

    //DONE
    $("#work_open_ws_today_button").click(openWorkSheetToday);
    //DONE
    loadWorkSheetInfosRecentlyForFirstLoading();
    //DONE
    $("#work_ws_sub_left_container_body").on("click",".work_ws_date_cotnainer",showWsDetail);
    //DONE
    $("#batch_sync_wss_in_ws_infos_recently").click(batchSyncAllToPlanDept);

    //DONE
    $("#work_open_plan_dept_dialog_btn").click(openPlanDeptDialog);
});

function loadWorkSheetInfosRecently(){
    sendAjax("CareerServlet","c_load_work_sheet_infos_recently",{
        "page":0
    },(data)=>loadWorkSheetInfosRecently_render(data));
}

function batchSyncAllToPlanDept(){
    let wsIds = $("#work_ws_sub_left_container_body").find(".over_finished_date,.warning_date").get().map(e=>parseInt($(e).attr("ws_id")));
    let wsId = $("#work_sheet_main_container").attr("ws_id");
    /*0代表不需要回显*/
    let notOpenWS = wsId == undefined ;
    wsId = notOpenWS ? 0 : wsId;

    confirmInfo("确定同步列表内<em>超期完成</em>或<em>超期</em>的工作表吗？（涉及<em>"+wsIds.length+"</em>天）",()=>{
        sendAjax("CareerServlet","c_sync_all_to_plan_dept_batch",{
            "ws_id" : wsId,
            "ws_ids":wsIds
        },(data)=>{
            if(!notOpenWS){
                loadWorkSheetDetail_render(data);
            }
            loadWorkSheetInfosRecently();
        });
    });
}

//DONE MAIN!!! The Hardest Part
function showWsDetail(){
    //DONE
    openWsActualContainer();
    //DONE
    let isWsToday = new Date(parseInt($(this).attr("ws_date"))).isSameByDate(new Date());    
    $("#work_ws_count_ws_today_alert").toggle(isWsToday);
    $("#work_ws_count_ws_prev_day_alert").toggle(!isWsToday);
    //DONE
    sendAjax("CareerServlet","c_load_work_sheet_count",{
        "date":new Date(parseInt($(this).attr("ws_date"))).valueOf()
    },(data)=>{
        $("#work_ws_count_ws_today_alert em").text(data.rlt);
    })

    $(this).addClass("common_prevent_double_click");
    let wsId = $(this).attr("ws_id");

    WORK_NAMESPACE.OPENED_WS_ID = wsId;
    //DONE
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


//DONE
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
            //DONE
            loadWorkSheetInfosRecently_render(data);
            //DONE
            closeAllPlanCards();
            //DONE
            openWsContainer();
            //DONE
            closePlansContainer();

            //DONE
            $("#work_ws_main_container").get(0).scrollIntoView();

            //DONE 工作表内左侧列表 打开今天的工作表
            $("#work_ws_sub_left_container_body").find(".work_ws_date_cotnainer").filter((i, v) => new Date(parseInt($(v).attr("ws_date"))).isSameByDate(new Date())).click();
        })
    });
}


/** 
 * 当今天已经开了ws后 closePlans openWsContainer 
 * 否则 openPlan
 **/
//DONE
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
//DONE
function openWsActualContainer(){
    $("#work_ws_with_actual_content_container").show();
    $("#work_ws_sub_right_container .work_ws_blank_container").hide();
}
//DONE
function closeWsActualContainer(){
    $("#work_ws_with_actual_content_container").hide();
    $("#work_ws_sub_right_container .work_ws_blank_container").show();

    WORK_NAMESPACE.OPENED_WS_ID = null;
}

//DONE
function loadWorkSheetInfosRecently_render(data,page){
    let pageOfData = page == undefined ? 0 : page;
    /*兼容之前写的不严谨的地方*/
    //React的逻辑 理论上 不需要再传分页了
    pageOfData = isNaN(pageOfData) ? 0 : pageOfData;
    //DONE
    let hasTodayWS = data.some((one)=>{
        let today = new Date();
        return new Date(one.date).isSameByDate(today);
    })
    $("#work_open_ws_today_button").toggle(!hasTodayWS);

    $("#work_ws_sub_left_container_body").empty();
    fillWsDateInfosContainer(data,pageOfData);
}
//DONE
function fillWorkListUnit(ws,$unit){
    $unit.attr({
        "ws_id":ws.id,
        "ws_date":ws.date
    }).find(".work_ws_date_cotnainer_title").text(new Date(ws.date).toChineseDate())
        //DONE
        .end().toggleClass("warning_date",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVERDUE)
        //DONE
        .prop("title",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVERDUE ? "该日工作存在未完成项，请尽快处理或同步进历史欠账":"")
        //DONE
        .toggleClass("over_finished_date",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVER_FINISHED)
        //DONE
        .prop("title",ws.state.dbCode==WS_NAMESPACE.WS_STATE_OF_OVER_FINISHED ? "该日工作超额完成，可同步进历史欠账":"")
}

function refreshWokrListUnitForExternalInvoker(ws){
    let $target = $("#work_ws_sub_left_container_body .work_ws_date_cotnainer[ws_id='"+ws.id+"']");
    if($target.length==0){
        console.error("refresh不存在的WorkListUnit")
        return;
    }
    fillWorkListUnit(ws,$target);
}

//DONE
function fillWsDateInfosContainer(data,pageOfData){
    //DONE
    data.forEach(ws=>{
        let $dateUnit = $("#work_pattern_container").find(".work_ws_date_cotnainer").clone();
        fillWorkListUnit(ws,$dateUnit);
        $("#work_ws_sub_left_container_body").append($dateUnit);
    })
    //DONE
    let wsSheetOfOnPage =  parseInt($("#default_ws_limit_of_one_page").val());
    //DONE
    $("#work_ws_sub_left_container_footer .work_see_more_ws").attr("start_page",parseInt(pageOfData)+1).toggle(wsSheetOfOnPage == data.length);
    $("#work_ws_sub_left_container_footer .work_no_more_ws").toggle(wsSheetOfOnPage > data.length);
    if(wsSheetOfOnPage < data.length){
        throw "this is impossible!"+wsSheetOfOnPage+" vs "+data.length;
    }    
}

/**
 *  TODO 这个是当修改了对应计划后 是否需要刷新对应的工作表？？
 *  细节问题 或许之后可以不管
 */

function refreshCurrentWorkSheetIfOpen(){
    if(WORK_NAMESPACE.OPENED_WS_ID){
        drawWorkSheetDetail(WORK_NAMESPACE.OPENED_WS_ID); 
    }
}

function editPlanDetail(){
    preventDoubleClick(this);
    let id = $(this).parents(".work_plan_card_container").attr("plan_id");
    openPlanDialog(id,true,refreshCurrentWorkSheetIfOpen); 
}

//DONE
function seePlanDetail(){
    preventDoubleClick(this);
    let id = $(this).parents(".work_plan_card_container").attr("plan_id");
    openPlanDialog(id,false,refreshCurrentWorkSheetIfOpen);
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
            /*根据seqWeight 决定顺序*/
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

//DONE
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

//DONE
function createPlan_render(data){
    $("#work_create_plan_dialog").modal("hide");
    loadActivePlans();

    confirmInfo("创建成功，您要直接编辑计划内容吗？",()=>{
        openPlanDialogByDate(data,true,refreshCurrentWorkSheetIfOpen);
    },"直接编辑","以后再说");
}


//DONE
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


//DONE
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
//DONE
function hideWsInfosRecently(){
    $("#work_ws_sub_left_container").hide();
    $("#switch_to_show_ws_infos_recently").text("显示"+$("#work_ws_sub_left_container_header").text().trim()).attr("open",false);
}

//DONE
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