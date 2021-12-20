let WS_NAMESPACE = {
    WS_STATE_OF_OVERDUE : 4 ,
    WS_STATE_OF_ASSUME_FINISHED : 3,
    WS_STATE_OF_ACTIVE:1,
    WS_STATE_OF_OVER_FINISHED:5,
    SAVE_WORK_ITEM_OP_TIMEOUT_ID :"",
    SAVE_WORK_ITEM_OP_TIMEOUT_SECONDS:3,
    WORK_ITEM_MOOD_MIN_VAL:1,
    WORK_ITEM_MOOD_MAX_VAL:5,
    WORK_ITEM_TYPE_OF_GENERAL : 1,
    WORK_ITEM_TYPE_OF_DEPT:2,

    /*在foucs的情况下 最多询问X次 一定push掉修改*/
    MAX_LIMIT_TO_SEND_SAVING_IN_QUEUE:30,
    SAVING_LOCK:false,
    COUNT_SAVING_ASKING:0,
    REMINDER_INTERVAL_MINUTES:1
}

/**为了解耦，这里关于WS.planItems的部分多数是从plan_dialog.js copy过来的 当修改时，应当修改两处地方 */
$(function(){

    drawCommonIcon("inluding_modify_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_modify_mark"));
    drawCommonIcon("inluding_add_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_add_mark"));
    drawCommonIcon("inluding_minus_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_delete_mark"));
    drawCommonIcon("inluding_circle_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_circle_container"));
    drawCommonIcon("inluding_circle_with_left_line_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_root_add_mark"));
    drawCommonIcon("inluding_open_folder_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_unfold_btn"));
    drawCommonIcon("inluding_close_folder_mark",$("#work_sheet_pattern_container .work_sheet_plan_item_fold_btn"));
    drawCommonIcon("inluding_trashcan_mark",$("#work_sheet_pattern_container .work_sheet_work_item_container_delete_button"));
    $("body").on("keydown",monitorHotKeys);

    $(".work_sheet_switch_to_show_ws_note").click(switchToShowWSNote);
    $(".work_sheet_switch_to_show_today_plan_main_container").click(switchToShowTodayPlan);
    $("#work_note_textarea").change(saveWorkSheet).on("focus",lockSaveWorkItem).on("blur",unlockSaveWorkItem);
    $(".work_sheet_main_container_delete_button").click(deleteWorkSheet);
    $(".work_sheet_main_container_assume_finsihed").click(assumeFinished);
    $(".work_sheet_main_container_cancel_assumen_finished").click(cancelAssumeFinished);
    $(".work_sheet_main_container_open_plan_edit_mode").click(openWSPlanEditMode);
    $(".work_sheet_main_container_close_plan_edit_mode").click(closeWSPlanEditMode);
    $(".work_sheet_main_container_switch_to_completion_mode").click(switchWSPlanToCompletioMode);
    $(".work_sheet_main_container_switch_to_plan_mode").click(switchWSPlanToPlanMode);

    $(".work_sheet_logs_switch_container_visibility").click(switchToWSLogsBody);

    $(".work_sheet_main_container_open_work_items_edit_mode").click(openWorkItemsEditMode);
    $(".work_sheet_main_container_close_work_items_edit_mode").click(closeWorkItemsEditMode);
    $(".work_sheet_switch_to_show_work_items_main_body").click(switchToShowWorkItemsBody);

    $("#work_sheet_today_plan_father_and_son_relationship_container").on("click",".dropdown-item",switchToShowWSPlanItemsByFatherRelation);


    $(".work_sheet_main_container_open_ws_statistics").click(openWSStatistics);

    $("#work_sheet_today_plan_control_group_container").find("[name='cat_name']").autocomplete({
        minLength:0,
        source : []
    }).focus(getPlanDeptItemNamesForWSPlan)
    .change(switchToShowWSPlanItemsValContainer)
    .on("autocompletechange",switchToShowWSPlanItemsValContainer).end()
    .find("[name='cat_name_when_modify']")
    .autocomplete({
        minLength:0,
        source : []
    }).focus(getPlanDeptItemNamesForWSPlan)
    .on("autocompletechange",syncWSPlanCatNameWhenModify)
    .change(checkWSPlanNameLegalWhenModifying).on("input",syncWSPlanCatNameWhenModify)
    .end()
    .find("[name='cat_type']").click(switchToShowWSPlanItemsValContainer).end()
    .find("[name='value_by_minutes']").on("input",inputOnlyAllowInt).end()
    .find("[name='value_by_times']").on("input",inputOnlyAllowInt).end()
    .find("[name='mapping_val_for_differ_type']").on("input",inputOnlyAllowInt).end()
    .find("[name='mapping_val_for_same_type']").on("input",inputOnlyAllowFloat).end()


    $("#work_sheet_today_plan_add_item_button").click(addPlanItemToWSPlan);
    $("#work_sheet_today_plan_save_item_button").click(saveWSPlanItem);

    $("#work_sheet_today_plan_items_cards_container").on("click",".work_sheet_plan_item_root_add_mark",addRootWSPlanItemByClickMark)
        .on("click",".work_sheet_plan_item_add_mark",addWSPlanItemClickMark)
        .on("click",".work_sheet_plan_item_delete_mark",deleteWSPlanItemByClickMark)
        .on("click",".work_sheet_plan_item_modify_mark",modifyWSPlanItemByClickMark)
        .on("click",".work_sheet_plan_item_fold_btn",foldWSPlanItemFoldBtn)
        .on("click",".work_sheet_plan_item_unfold_btn",unfoldWSPlanItemFoldBtn)
        .on("click",".work_sheet_plan_item_save_fold_info",saveWSPlanItemFoldInfo)
        .on("click",".work_sheet_plan_item_container_body,.work_sheet_plan_item_completion_body",addItemToWSByClickLabel)
        .on("click",".work_sheet_plan_sync_completion_to_dept",syncToPlanDept);

    $("#work_sheet_work_items_container_main_body_controgroup").on("click",".dropdown-item",addItemToWS);

    $("#work_sheet_work_items_container_main_body_ws_items").on("click",".work_sheet_work_item_container_switch_to_show_note",switchToShowWorkItemNote)
        .on("input","[name='val']",inputOnlyAllowInt)
        .on("focus","textarea,input",lockSaveWorkItem)
        .on("blur","textarea,input",unlockSaveWorkItem)
        .on("change","textarea,input",saveWorkItemByUnit)
        .on("click",".work_sheet_work_item_container_calculate_info_mark",switchToWorkItemValMark)
        .on("click",".work_sheet_work_item_calculate_end_time_by_now",setEndTimeByNow)
        .on("click",".work_sheet_work_item_container_mood_body",changeWorkItemMood)
        .on("click",".work_sheet_work_item_calculate_val_by_end_time_btn",calculateValByEndTime)
        .on("click",".work_sheet_work_item_calculate_end_time_by_val_btn",calculateEndTimeByVal)
        .on("click",".work_sheet_work_item_container_delete_button",deleteItemFromWorkSheet)
        .on("click",".work_sheet_save_workitem_type_btn",saveWorkItemTypeModified)
        .on("click",".work_sheet_revoke_save_workitem_type_btn",function(){
            unlockSaveWorkItem();
            $(this).parents(".work_sheet_work_item_container").attr("siwtch_type_modify_on",false);
        })
        .on("click",".work_sheet_work_item_container_plan_item_type_modify_mark",function(){
            /*如果要修改类型了 就锁一下*/
            lockSaveWorkItem();
            
            $(this).parents(".work_sheet_work_item_container").attr("siwtch_type_modify_on",true);
        });
        

    $(".work_sheet_main_container_show_all_work_items_note").click(showAllWorkItemsNote);
    $(".work_sheet_main_container_hide_all_work_items_note").click(hideAllWorkItemsNote);

    $(".work_sheet_main_container_sync_all_plan_item").click(syncAllToPlanDept);

    $("#work_sheet_open_work_sheet_reminder_btn").click(checkIfEnable);
    $("#ws_warning_work_items_container").on("input","[name='extensition_customize_minutes']",inputOnlyAllowInt)
        .on("click",".ws_warning_work_item_unit_controlgroup_samples [extension_minutates]",extensionWorkItemVal)
        .on("click",".ws_warning_work_item_unit_confirm_extension_minutates",confirmCustomizeExtensionWorkItemVal)


    /* === 每一个整分钟 进行reminder code start===*/
    let now = new Date();
    let copy = cloneObj(now);
    copy.setMilliseconds(0);
    copy.setSeconds(0);
    copy.setMinutes(now.getMinutes()+1);
    setTimeout(()=>{
           reminderMonitoring();
           setInterval(reminderMonitoring,WS_NAMESPACE.REMINDER_INTERVAL_MINUTES*60*1000);
       },copy.getTime()-now.getTime());
    /* === 每一个整分钟 进行reminder code end ===*/

    initReminderBtnByLocalStorage();

    $("#work_sheet_open_work_sheet_reminder_btn").click(function(e){
        let prop = $(this).prop("checked");
        localStorage[CONFIG.DEFAULT_WS_REMINDER_OPEN_KEY]=prop;
    })
});




function initReminderBtnByLocalStorage(){

    let selected;
    
    if(localStorage[CONFIG.DEFAULT_WS_REMINDER_OPEN_KEY]){
        selected = parseToBool(localStorage[CONFIG.DEFAULT_WS_REMINDER_OPEN_KEY]);
    }else{
        selected = true;
    }

    $("#work_sheet_open_work_sheet_reminder_btn").prop("checked",selected);
}




function confirmCustomizeExtensionWorkItemVal(){
    let val = $(this).parents(".ws_warning_work_item_unit_controlgroup_customize").find("[name='extensition_customize_minutes']").val();
    let valInt = parseInt(val);
    if(isNaN(valInt)){
        alertInfo("请填写延长时间");
        return;
    }
    extensionWorkItem(valInt,this);
}

function extensionWorkItem(extensionMinutates,dom){
    let wsId = $(dom).parents(".ws_warning_work_item_unit_container").attr("item_id");
    let $target = $("#work_sheet_work_items_container_main_body_ws_items").find(".work_sheet_work_item_container[item_id='"+wsId+"']");
    $target.find("[name='val']").val(extensionMinutates+parseInt($target.find("[name='val']").val()));

    let $contorgroup = $(dom).parents(".ws_warning_work_item_unit_controlgroup");
    $contorgroup.addClass("common_prevent_double_click");

    pushSaveWorkItemOpToQueue($target);  
    saveChangedWorkItemUnits(null,true,()=>{
        calculateWorkItemsWhetherOvertime();
        $contorgroup.removeClass("common_prevent_double_click");
    });
}


function extensionWorkItemVal(){
    let extensionMinutates = parseInt($(this).attr("extension_minutates"));
    extensionWorkItem(extensionMinutates,this);
}



function checkIfEnable(){
    if($(this).prop("checked")){
        calculateWorkItemsWhetherOvertime();
    }
}

function calculateWorkItemsWhetherOvertime(){
    let baseTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    let now = new Date();
    
    let matchedWorkItems = $("#work_sheet_work_items_container_main_body_ws_items").find(".work_sheet_work_item_container").get().filter(e=>
        parseInt($(e).attr("plan_item_type"))==BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES
        && $(e).find("[name='start_time']").val().trim().length>0 && parseInt($(e).find(".work_sheet_work_item_container_calculate_info_val").text())>0 &&
            $(e).find("[name='end_time']").val().trim().length==0
    ).filter(e=>{
        let startTimeVal = $(e).find("[name='start_time']").val();
        let warningTime =  cloneObj(baseTime);
        warningTime.overrideTimeInput(startTimeVal);
        warningTime.addMinutes(parseInt($(e).find(".work_sheet_work_item_container_calculate_info_val").text()));
        return now.isAfter(warningTime);
    })

    let $container = $("#ws_warning_work_items_container");
    $container.empty();
    if(matchedWorkItems.length == 0){
        $("#ws_warning_work_item_dialog").modal("hide");
        return;
    }

    $("#ws_warning_work_item_dialog").modal("show");

    matchedWorkItems.forEach(e=>{
        let $unit = $("#ws_warning_work_item_dialog_pattern_container").find(".ws_warning_work_item_unit_container").clone();
        let startTimeVal = $(e).find("[name='start_time']").val();
        let startTimeObj =  cloneObj(baseTime);
        startTimeObj.overrideTimeInput(startTimeVal);
        let lastingMinutes = parseInt($(e).find(".work_sheet_work_item_container_calculate_info_val").text());
        let planEndTime = cloneObj(startTimeObj);
        planEndTime.addMinutes(lastingMinutes);

        $unit.attr({
            "item_id":$(e).attr("item_id")
        })
        .find(".warning_work_item_start_time").text(startTimeObj.toSmartString()).end()
        .find(".warning_work_item_name").text($(e).find(".work_sheet_work_item_container_plan_item_context").text()).end()
        .find(".warning_work_item_lasting_time").text(lastingMinutes).end()
        .find(".warning_work_item_overdue_time").text(now.countMinutesDiffer(planEndTime).transferToHoursMesIfPossible()).end()

        $container.append($unit);
    });
}


function reminderMonitoring(){
    let flag = $("#work_sheet_open_work_sheet_reminder_btn").prop("checked");
    if(!flag){
        return;
    }

    calculateWorkItemsWhetherOvertime();
}



function foldWSPlanItemFoldBtn(){
    let $parentContainer =  getSelfWSPlanItemContainerForFoldBtns(this);
    $parentContainer.attr("fold",true);
}

function getSelfWSPlanItemContainerForFoldBtns(dom){
    return $(dom).parent().parent().parent().parent(".work_sheet_plan_item_container_first_level,.work_sheet_plan_item_container_unit_level")
}

function unfoldWSPlanItemFoldBtn(){
    let $parentContainer =  getSelfWSPlanItemContainerForFoldBtns(this);
    $parentContainer.attr("fold",false);
}

function saveWSPlanItemFoldInfo(){
    let wsId = $("#work_sheet_main_container").attr("ws_id");
    let fold = getSelfWSPlanItemContainerForFoldBtns(this).attr("fold");
    let itemId = $(this).parent().siblings(".work_sheet_plan_item_container_footer").attr("item_id");

    $(this).addClass("common_prevent_double_click");

    sendAjaxBySmartParams("CareerServlet","c_save_ws_plan_item_fold",{
        "ws_id":wsId,
        "item_id":itemId,
        "fold": fold,
    },()=>{
        showForAWhile("已保存",$(this).siblings(".work_sheet_plan_item_save_fold_hint"),5000,()=>{
            $(this).hide();
        },()=>{
            $(this).show();
        })
    },()=>{
        $(this).removeClass("common_prevent_double_click");
    })
}


/**
 * 统计心情变化的逻辑是这样的：
 * 取出所有workItem 最早和最晚时间 然后填充这期间每一分钟的心情
 * 假设没有结束时间的，也过滤掉
 * 假设工作项有重叠的话 则取心情的平均值
 * 无工作项时的心情取值为0，有工作项但无心情标识时，心情取值也为0 （和平均值保持一致）
 */
function analyzeMoodsByTime(content){
    let srcOfWorkItem = content.workItems.filter(w => w.item.endTime != 0 || w.item.mood > 0).map((e)=>e.item);
    if(srcOfWorkItem.length == 0)
        return [];

    let minTime = srcOfWorkItem[0].startTime;
    let maxTime = srcOfWorkItem[0].endTime;
    srcOfWorkItem.forEach(item=>{
        if(minTime > item.startTime){
            minTime = item.startTime;
        }
        if(minTime > item.endTime){
            minTime = item.endTime;
        }
        if(maxTime < item.startTime){
            maxTime = item.startTime;
        }
        if(maxTime < item.endTime){
            maxTime = item.endTime;
        }
    })

    let rlt = [];
    let stepOfOneMinute = 60 * 1000;

    let curTime = minTime;
    while(curTime <= maxTime){
        /*求出这一刻的Mood值*/
        let workItemsForThisMinute = srcOfWorkItem.filter(e=>e.startTime <= curTime && e.endTime >= curTime);
        
        let mood;
        
        if(workItemsForThisMinute.length == 0){
            mood = 0;
        }else{
            mood = parseFloat(((workItemsForThisMinute.reduce((acc,cur)=>
                (acc + cur.mood)
            ,0))/workItemsForThisMinute.length).toText());
        }

        rlt.push({
            "time" : curTime,
            "mood" : mood
        });

        curTime += stepOfOneMinute;
    }

    return rlt;
}




function openWSStatistics() {
    $("#ws_statistics_dialog").modal("show");

    let wsId = $("#work_sheet_main_container").attr("ws_id");

    $("#date_for_ws_statistics_dialog_label").text($(".work_sheet_main_container_header_date_title").text());

    sendAjax("CareerServlet","c_load_work_sheet",{
        "ws_id" : wsId
    },(data)=>{
        rlt = mergeWorkItemsExceptUndone(data.content);



        let pieSource = rlt.map(e=>{
            return {
                "name":e.pItem.item.name,
                "value" : e.costMinutes
            }
        });

        let barSource = rlt.map(e=>{
            return [e.pItem.item.name,e.costMinutes]
        }).sort((a,b)=>{
            if (a[1] < b[1]) {
                return 1;
            } else if (a[1] > b[1]) {
                return -1;
            } else {
                return 0;
            }
        });

        drawCommonBarChart("ws_statistics_dialog_bar_chart_container",barSource,"总耗时/min",rlt.length);
       

        drawCommonPieChart("ws_statistics_dialog_pie_chart_container",pieSource,50,value=>value.transferToHoursMesIfPossible());

        let countAllMinutes = rlt.reduce((accum,current)=>{
            return accum+current.costMinutes;           
        },0)

        $("#ws_statistics_dialog_count_all_time").text(countAllMinutes.transferToHoursMesIfPossible());

        let moodsStatisticsDataSource = analyzeMoodsByTime(data.content); 

        getOrInitEcharts("ws_statistics_dialog_line_and_bar_chart_container").setOption({

            visualMap: {
                show: false,
                type: 'continuous',
                seriesIndex: 0,
                min: 0,
                max: 5
            },

            title: {
                left: 'center',
                text: '心情变化统计'
            },

            tooltip: {
                trigger: 'axis'
            },

            xAxis: {
                data: moodsStatisticsDataSource.map(e=>new Date(e.time).toHoursAndMinutesOnly())
            },
            yAxis: {
                name:"心情平均值"
            },
            grid: {
                bottom: '60%'
            },
            series: {
                type: 'line',
                showSymbol: false,
                data: moodsStatisticsDataSource.map(e=>e.mood)
            }
        })


    })
}




function monitorHotKeys(e){
    /*ctrl+q 切换工作表 编辑*/
    if(e.keyCode == 81 && withCtrl(e)){
        e.preventDefault();
        $("#work_sheet_work_items_container_header").find(".work_sheet_main_container_open_work_items_edit_mode:visible,.work_sheet_main_container_close_work_items_edit_mode:visible").click().focus();
    }
}

function getPlanDeptItemNamesForWSPlan(){
    sendAjax("CareerServlet","c_load_plan_dept_item_names",{},(data)=>{
        fillAutocompletInput(data,$(this));
    });
}


function lockSaveWorkItem(){
    WS_NAMESPACE.SAVING_LOCK = true;
}

function unlockSaveWorkItem(){
    WS_NAMESPACE.SAVING_LOCK = false;
}



function deleteItemFromWorkSheet(){
    confirmInfo("确定要删除吗？",()=>{
        let $workItem = $(this).parents(".work_sheet_work_item_container");
        let wsId = $("#work_sheet_main_container").attr("ws_id");
        
        $workItem.remove();

        sendAjax("CareerServlet","c_remove_item_from_work_sheet",{
            "ws_id" : wsId,
            "item_id" :$workItem.attr("item_id")
        },loadWorkSheetDetail_render);
    })
}

function calculateEndTimeByVal(){
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    let isMinutesType  =  parseInt($workItem.attr("plan_item_type")) == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;

    if(!isMinutesType){
        console.error("??? 不该出现的");
        return;
    }
    let val = parseInt($workItem.find("[name='val']").val());
    let startTimeVal = $workItem.find("[name='start_time']").val();
    let startTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    startTime.overrideTimeInput(startTimeVal);
    startTime.addMinutes(val);
    $workItem.find("[name='end_time']").val(startTime.toStandardHoursAndMinutesOnly());

    pushSaveWorkItemOpToQueue($workItem);
}


/**假如是次数 默认为1 分钟则由结束时间-开始时间 */
/**这个动作基本算是一项的结束，直接保存修改*/
function calculateValByEndTime(){
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    let isMinutesType  =  parseInt($workItem.attr("plan_item_type")) == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;

    let origin = $workItem.find("[name='val']").val().trim().length == 0 ? 0 :  parseInt($workItem.find("[name='val']").val());

    if(!isMinutesType){
        if(origin != 1){
            $workItem.find("[name='val']").val(1);
            pushSaveWorkItemOpToQueue($workItem);  
            saveChangedWorkItemUnits(); 
        }
        return;
    }

    let startTimeVal = $workItem.find("[name='start_time']").val();
    let startTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    startTime.overrideTimeInput(startTimeVal);

    let endTimeVal =  $workItem.find("[name='end_time']").val();
    let endTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    endTime.overrideTimeInput(endTimeVal);

  
    let minutesDiffer = calculateMinutesForWorkItem(startTime,endTime);
    
    if(origin != minutesDiffer){
        $workItem.find("[name='val']").val(minutesDiffer);
        pushSaveWorkItemOpToQueue($workItem);  
        saveChangedWorkItemUnits();
    }
}





function setEndTimeByNow(){
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    $workItem.find("[name='end_time']").val(new Date().toStandardHoursAndMinutesOnly());
    pushSaveWorkItemOpToQueue($workItem);  
}


function changeWorkItemMood(){
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    pushSaveWorkItemOpToQueue($workItem);  

    let origin = parseInt($(this).attr("mood"));
    if(origin+1>WS_NAMESPACE.WORK_ITEM_MOOD_MAX_VAL){
        origin = 0
    }else{
        origin++;
    }
    $(this).parent(".work_sheet_work_item_container_mood").empty().append(calculateMoodSpan(origin));
}

function switchToWorkItemValMark(){
    let origin = parseToBool($(this).attr("for_add"));
    $(this).attr("for_add",!origin).text(caclulateWorkItemMarkByForAdd(!origin));
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    pushSaveWorkItemOpToQueue($workItem);  
}


function saveWorkItemByUnit(){
    let $workItem = $(this).parents(".work_sheet_work_item_container");
    pushSaveWorkItemOpToQueue($workItem);   
}



function showAllWorkItemsNote(){
    $("#work_sheet_work_items_container_main_body_ws_items").find(".work_sheet_work_item_container").each((i,v)=>{
        showWorkItemNote($(v));
    })
}

function hideAllWorkItemsNote(){
    $("#work_sheet_work_items_container_main_body_ws_items").find(".work_sheet_work_item_container").each((i,v)=>{
        hideWorkItemNote($(v));
    })
}

function addItemToWSByClickLabel(){
    let planItemId = $(this).parent(".work_sheet_plan_item_container_sub_container").children(".work_sheet_plan_item_container_footer").attr("item_id");
    $("#work_sheet_work_items_container_main_body_controgroup .dropdown-item[item_id='"+planItemId+"']").click();
}


/**
 * 主要是传一些默认值给后台，比较特殊的是start_time 是这天的date+当时的time
 */
function addItemToWS(){
    if($(this).hasClass("common_prevent_double_click")){
        return;
    }

    $(this).addClass("common_prevent_double_click");
    let planItemId  = $(this).attr("item_id");
    let wsId = $("#work_sheet_main_container").attr("ws_id");
    let date = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    date.overrideTime(new Date());
    sendAjax("CareerServlet","c_add_item_to_ws",{
        "ws_id" : wsId,
        "plan_item_id" :planItemId,
        "note": getDefaultWorkItemNote(),
        "val" : 0,
        "mood":0,
        "for_add":false,
        "start_time":date.toString()
    },(data)=>{
        $(this).removeClass("common_prevent_double_click");
        loadWorkSheetDetail_render(data)
    });
}

function getDefaultWorkItemNote(){
    return "目标 \r\n开始 \r\n结束 "
}


function modifyWSPlanItemByClickMark(){
    let $container = $("#work_sheet_today_plan_control_group_container");
    let $footer  =  $(this).parent(".work_sheet_plan_item_container_footer");
    let $item = findWSPlanDropDownItemByItemId($footer.attr("item_id"));
    let fatherId = parseInt($footer.attr("father_id"));
    let $fatherItem = findWSPlanDropDownItemByItemId(fatherId);
    let itemTypeCode= parseInt($item.attr("cat_type"));
    let typeText = $container.find("[name='cat_type']").filter((i,v)=>parseInt($(v).val())==itemTypeCode).parent("label").text();
    let catName = $footer.attr("cat_name");

    $("#work_sheet_item_id_when_modify_item").val($footer.attr("item_id"));

    /*把cat名锚点属性清空*/
    $container.find("[cat_name_anchor='true']").attr("cat_name_anchor",false);
    $container.find(".plan_dialog_items_values_container>div").hide();
    openWSPlanItemControlgroupSaveMode();

    $(".work_sheet_today_plan_type_when_modifying").text(typeText);
    $(".work_sheet_today_plan_subordinate_when_modifying").text($fatherItem.text());

    $("#work_sheet_today_plan_control_group_container [name='cat_name_when_modify']").val(catName).change().end()
        .find("[name='note']").val($footer.attr("item_note"));


    let isFather = fatherId == 0;
    let isMinutesType = itemTypeCode == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;
    if(isFather){
        $("#work_sheet_today_plan_items_inputs_for_minutes_type").toggle(isMinutesType);
        $("#work_sheet_today_plan_items_inputs_for_times_type").toggle(!isMinutesType);
        let val = $footer.attr("item_val");
        if(isMinutesType){
            $container.find("[name='value_by_minutes']").val(val);
        }else{
            $container.find("[name='value_by_times']").val(val);
        }
        return;
    }

    let sonType = itemTypeCode;
    let fatherType = parseInt($fatherItem.attr("cat_type"));
    let fatherText = $fatherItem.text();
    let mappinVal = $footer.attr("mapping_val");
    if( sonType != fatherType){
        let $targetContainerOfDifferType =  $("#work_sheet_today_plan_items_inputs_for_different_type_mapping");
        let timesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? catName : fatherText;
        let minutesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? fatherText : catName;
        $targetContainerOfDifferType.find(".work_sheet_today_plan_items_inputs_of_times_type").attr("cat_name_anchor",true).text(timesTarget).end()
            .find(".work_sheet_today_plan_items_inputs_of_minutes_type").text(minutesTarget).end()
            .find("[name='mapping_val_for_differ_type']").val(mappinVal);
        $targetContainerOfDifferType.show();
        return;
    }
    
    let $targetContainerOfSameType = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ?  $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times") : $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes");
    $targetContainerOfSameType.find(".work_sheet_today_plan_items_son_title").attr("cat_name_anchor",true).text(catName).end()
        .find("[name='mapping_val_for_same_type']").val(mappinVal).end()
        .find(".work_sheet_today_plan_items_father_title").text(fatherText).end().show()
}

function deleteWSPlanItemByClickMark(){
    confirmInfo("确定要删除吗（会将子项一并删除）？",()=>{
        let itemId = $(this).parent(".work_sheet_plan_item_container_footer").attr("item_id");
        let wsId = $("#work_sheet_main_container").attr("ws_id");
        sendAjax("CareerServlet","c_remove_item_from_ws_plan",{
            "ws_id" : wsId,
            "item_id" :itemId
        },loadWorkSheetDetail_render);
    })
}


function findSubWSPlanAndClick(itemId){
    findWSPlanDropDownItemByItemId(itemId).click();  
}

function findWSPlanDropDownItemByItemId(itemId){
    return $("#work_sheet_today_plan_father_and_son_relationship_container .dropdown-item[item_id='"+itemId+"']");
}


function addRootWSPlanItemByClickMark(){
    let itemId = $(this).parent(".work_sheet_plan_item_container_root_controlgroup").attr("item_id");
    findSubWSPlanAndClick(itemId);
    openWSPlanItemControlgroupAddMode();
}
function addWSPlanItemClickMark(){
    let itemId = $(this).parent(".work_sheet_plan_item_container_footer").attr("item_id");
    findSubWSPlanAndClick(itemId);
    openWSPlanItemControlgroupAddMode();
}
function syncAllToPlanDept(){
    confirmInfo("确定同步所有盈余或赊欠的计划项吗（根据计划项名与类型和欠账对应，且仅以<em>根节点</em>同步）",()=>{
        saveChangedWorkItemUnits(()=>{
            let wsId = $("#work_sheet_main_container").attr("ws_id");
            sendAjax("CareerServlet","c_sync_all_to_plan_dept",{
                "ws_id" : wsId,
            },(data)=>{
                loadWorkSheetDetail_render(data);
                reloadForExternalModule();
            });
        },false);
    })
}


function syncToPlanDept(){
    confirmInfo("确定要同步进历史欠账吗（根据计划项名与类型和欠账对应）",()=>{
        saveChangedWorkItemUnits(()=>{
            let wsId = $("#work_sheet_main_container").attr("ws_id");
            let itemId = $(this).parent(".work_sheet_plan_item_container_footer").attr("item_id");
            sendAjax("CareerServlet","c_sync_to_plan_dept",{
                "ws_id" : wsId,
                "plan_item_id" :itemId,
            },(data)=>{
                loadWorkSheetDetail_render(data);
                reloadForExternalModule();
            });
        },false);
    })
}

function reloadForExternalModule(){
    if($("#work_sub_main_container").length>0){
        /*工作表模块*/
        loadWorkSheetInfosRecently();
    }

    if($("#all_ws_sub_main_container").length>0){
        /*查看全部工作表模块*/
        loadWStatistics();
        loadWSByStateCircle();
    }
}


function addPlanItemToWSPlan(){
    let $container = $("#work_sheet_today_plan_control_group_container");
    let $catNameInput = $container.find("[name='cat_name']");
    let catName = $catNameInput.val().trim();

    $(".work_sheet_today_plan_items_error_mes.items_header").toggle(catName.length == 0).text(catName.length == 0 ? "名称不能为空":"");

    $catNameInput.parents(".work_sheet_today_plan_unit_container").attr("test",catName.length > 0);
    if(catName.length == 0){
        alertInfo("请按照提示填写好必要信息");
        return;
    }

    preventDoubleClick(this);

    let fatherId = parseInt($("#work_sheet_today_plan_father_relation_btn").attr("father_id"));
    let isFather = fatherId == 0;

    let wsId = $("#work_sheet_main_container").attr("ws_id");

    let note = $container.find("[name='note']").val();
    let catType = $container.find("[name='cat_type']:checked").val();

    if(isFather){
        let usingMnutesType = $container.find("[name='cat_type'][minutes]").prop("checked");
        let val = usingMnutesType ? $container.find("[name='value_by_minutes']").val() : $container.find("[name='value_by_times']").val();
        let param = {
            "ws_id" :wsId,
            "cat_name" : catName,
            "val" : val.length == 0 ? 0 : val,
            "note" : note,
            "cat_type" : catType,
            "father_id" : fatherId,
        }

        if(val.length == 0){
            confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_VAL,()=>{
                sendAjax("CareerServlet","c_add_item_to_ws_plan",param,(data)=>{
                    showForAWhile("添加成功",$("#work_sheet_today_plan_save_or_add_item_hint_mes"));
                    loadWorkSheetDetail_render(data);
                })
            })
            return;
        } 
        sendAjax("CareerServlet","c_add_item_to_ws_plan",param,(data)=>{
            showForAWhile("添加成功",$("#work_sheet_today_plan_save_or_add_item_hint_mes"));
            loadWorkSheetDetail_render(data);
        })
        return;
    }

    let $fatherDrop = $("#work_sheet_today_plan_father_relation_btn");
    let sonType = parseInt($container.find("[name='cat_type']:checked").val());
    let fatherType = parseInt($fatherDrop.attr("father_type"));
    let mappingVal;
    if( sonType != fatherType){
        mappingVal = $("#work_sheet_today_plan_items_inputs_for_different_type_mapping").find("[name='mapping_val_for_differ_type']").val();
    }else{
        mappingVal = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times").find("[name='mapping_val_for_same_type']").val()
                : $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes").find("[name='mapping_val_for_same_type']").val();
    }
    let param = {
        "ws_id" :wsId,
        "cat_name" : catName,
        "mapping_val" : mappingVal.length == 0 ? 0 : mappingVal,
        "note" : note,
        "cat_type" : catType,
        "father_id" : fatherId,
    }
    if(mappingVal.length == 0){
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_MAPPING_VAL,()=>{
            sendAjax("CareerServlet","c_add_item_to_ws_plan",param,(data)=>{
                showForAWhile("添加成功",$("#work_sheet_today_plan_save_or_add_item_hint_mes"));
                loadWorkSheetDetail_render(data);
            })
        })
        return;
    } 
    sendAjax("CareerServlet","c_add_item_to_ws_plan",param,(data)=>{
        showForAWhile("添加成功",$("#work_sheet_today_plan_save_or_add_item_hint_mes"));
        loadWorkSheetDetail_render(data);
    })
}


function saveWSPlanItem(){
    let $container = $("#work_sheet_today_plan_control_group_container");
    let catName = $container.find("[name='cat_name_when_modify']").val().trim();
    if(catName.length == 0){
        alertInfo("请按照提示填写好必要信息");
        return;
    }

    preventDoubleClick(this);

    let itemId = $("#work_sheet_item_id_when_modify_item").val();
    let wsId = $("#work_sheet_main_container").attr("ws_id");
    let note = $container.find("[name='note']").val();
    
    let param = {
        "ws_id" :wsId,
        "cat_name" : catName,
        "note" : note,
        "item_id" :itemId
    }

    if($("#work_sheet_today_plan_items_inputs_for_minutes_type").is(":visible")){
        let val = $container.find("[name='value_by_minutes']").val();
        param.val = val;
        sendSaveWSPlanItem(param);
    }else if($("#work_sheet_today_plan_items_inputs_for_times_type").is(":visible")){
        let val =$container.find("[name='value_by_times']").val();
        param.val = val;
        sendSaveWSPlanItem(param);
    }else if($("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes").is(":visible")){
        let mappingVal = $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes").find("[name='mapping_val_for_same_type']").val();
        param.mapping_val = mappingVal;
        sendSaveWSPlanItem(param);
    }else if($("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times").is(":visible")){
        let mappingVal = $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times").find("[name='mapping_val_for_same_type']").val()
        param.mapping_val = mappingVal;
        sendSaveWSPlanItem(param);
    }else{
        if(!$("#work_sheet_today_plan_items_inputs_for_different_type_mapping").is(":visible")){
            throw "缺乏配置!";
        }
        let mappingVal = $("#work_sheet_today_plan_items_inputs_for_different_type_mapping").find("[name='mapping_val_for_differ_type']").val();
        param.mapping_val = mappingVal;
        sendSaveWSPlanItem(param);
    }
}

function sendSaveWSPlanItem(param){
    let $hintContainer = $("#work_sheet_today_plan_save_or_add_item_hint_mes");
    if(param.val != undefined && param.val.length == 0){
        param.val = 0
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_VAL,()=>{
            sendAjax("CareerServlet","c_save_ws_plan_item",param,(data)=>{
                showForAWhile("保存成功",$hintContainer);
                loadWorkSheetDetail_render(data);
            })
        })
        return;
    } 
    if(param.mapping_val !=undefined && param.mapping_val.trim().length == 0){
        param.mapping_val = 0
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_MAPPING_VAL,()=>{
            sendAjax("CareerServlet","c_save_ws_plan_item",param,(data)=>{
                showForAWhile("保存成功",$hintContainer);
                loadWorkSheetDetail_render(data);
            })
        })
        return;
    }

    sendAjax("CareerServlet","c_save_ws_plan_item",param,(data)=>{
        showForAWhile("保存成功",$hintContainer);
        loadWorkSheetDetail_render(data);
    });
}




function checkWSPlanNameLegalWhenModifying(){
    let $name = $("#work_sheet_today_plan_control_group_container").find("[name='cat_name_when_modify']");
    let $container = $name.parents(".work_sheet_today_plan_unit_container");
    let $errorContainer=   $(".work_sheet_today_plan_items_error_mes.items_header");
    if(!$(this).is(":visible")){
        $container.attr("test",true)
       $errorContainer.hide().text("");
        return true;
    }
    let text = $name.val().trim();
    if(text.length == 0){
        $container.attr("test",false);
        $errorContainer.show().text("名称不能为空");
        return false;
    }else{
        $container.attr("test",true)
        $errorContainer.hide().text("");
        return true;
    } 
}


function syncWSPlanCatNameWhenModify(){
    let $anchor = $("#work_sheet_today_plan_control_group_container [cat_name_anchor='true']");
    $anchor.text($(this).val());
}


function switchToShowWSPlanItemsByFatherRelation(){
    let text = $(this).text();
    let id = $(this).attr("item_id");
    $("#work_sheet_today_plan_father_relation_btn").attr({
        "father_id":id,
        "father_type" : $(this).attr("cat_type")
    }).text(text);

    switchToShowWSPlanItemsValContainer();
}

function switchToShowWSPlanItemsValContainer(isFinal){
    let $container = $("#work_sheet_today_plan_control_group_container");

    $(".work_sheet_today_plan_items_for_one_row.work_sheet_today_plan_items_values_container>div").hide();

    let $fatherDrop = $("#work_sheet_today_plan_father_relation_btn");
    let isFather = parseInt($fatherDrop.attr("father_id")) == 0;

    let $catNameInput = $container.find("[name='cat_name']");
    let catName = $catNameInput.val().trim();
    
    /*当从属关系非无时，需要校验名称不能为空*/
    $errorContainer = $(".work_sheet_today_plan_items_error_mes.items_header");
    if(catName.length == 0 && !isFather){
        return;
    }else{
        $errorContainer.hide().text("");
        $catNameInput.parents(".work_sheet_today_plan_unit_container").attr("test",true);
    }

    if(isFather){
        $("#work_sheet_today_plan_items_inputs_for_minutes_type").toggle($container.find("[name='cat_type'][minutes]").prop("checked"));
        $("#work_sheet_today_plan_items_inputs_for_times_type").toggle($container.find("[name='cat_type'][times]").prop("checked"));

        initWSPlanItemsValContainer();
        return;
    }
    let $catType = $container.find("[name='cat_type']:checked");
    let sonType
    if($catType.length != 0){
        sonType = parseInt($catType.val());
    }else{
        let $active = $container.find(".work_sheet_today_plan_type_chooser").find("label.active");
        if($active.length == 0){
            alertInfo("出现罕见的添加临时计划项错误，请刷新页面重试");
            return;
        }
        sonType = parseInt($active.find("[name='cat_type']").val());
    }
    
    initWSPlanItemsValContainer();


    let fatherType = parseInt($fatherDrop.attr("father_type"));
    let fatherText = $fatherDrop.text();
    if( sonType != fatherType){
        let $targetContainerOfDifferType =  $("#work_sheet_today_plan_items_inputs_for_different_type_mapping");
        let timesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? catName : fatherText;
        let minutesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? fatherText : catName;
        $targetContainerOfDifferType.find(".work_sheet_today_plan_items_inputs_of_times_type").text(timesTarget).end()
            .find(".work_sheet_today_plan_items_inputs_of_minutes_type").text(minutesTarget);
        $targetContainerOfDifferType.show();
        return;
    }

    let $targetContainerOfSameType = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ?  $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_times") : $("#work_sheet_today_plan_items_inputs_for_same_type_mapping_of_minutes");
    $targetContainerOfSameType.find(".work_sheet_today_plan_items_son_title").text(catName).end()
        .find(".work_sheet_today_plan_items_father_title").text(fatherText).end().show();

}

function initWSPlanItemsValContainer(){
    let $container = $("#work_sheet_today_plan_control_group_container");
    $(".work_sheet_today_plan_items_inputs_by_type_container").find("[type='text'],textarea").val("").end();
    if($container.find("[name='cat_type']:checked").length == 0){
        $container.find("[name='cat_type']").eq(0).click();
    }
}

function switchWSPlanToCompletioMode(){
    $("#work_sheet_container_with_header_and_content").attr("plan_mode","completion");
    $(".work_sheet_main_container_today_plan_title").text($(".work_sheet_main_container_switch_to_completion_mode>span").text());
}


function switchWSPlanToPlanMode(){
    $("#work_sheet_container_with_header_and_content").attr("plan_mode","plan");
    $(".work_sheet_main_container_today_plan_title").text($(".work_sheet_main_container_switch_to_plan_mode>span").text());
}


function openWSPlanItemControlgroupSaveMode(){
    $("#work_sheet_today_play_main_container").attr("controlgroup_mode","save");
}

function openWSPlanItemControlgroupAddMode(){
    $("#work_sheet_today_play_main_container").attr("controlgroup_mode","add");
}

function openWorkItemsEditModeWithoutConfirm(){
    $("#work_sheet_main_container").attr("open_ws_edit_mode",true);

    $(".work_sheet_main_container_close_work_items_edit_mode").show();
    $(".work_sheet_main_container_open_work_items_edit_mode").hide();
}

function openWorkItemsEditMode(){
    let date = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    if(date.isToday()){
        openWorkItemsEditModeWithoutConfirm()
        return;
    }

    confirmInfo("往者不可谏，确定要编辑过往的工作项吗？",openWorkItemsEditModeWithoutConfirm);
}

function closeWorkItemsEditMode(){
    $("#work_sheet_main_container").attr("open_ws_edit_mode",false);

    $(".work_sheet_main_container_close_work_items_edit_mode").hide();
    $(".work_sheet_main_container_open_work_items_edit_mode").show();
}


function closeWSPlanEditMode(){
    $("#work_sheet_container_with_header_and_content").attr("open_edit_mode",false);
    $(".work_sheet_main_container_close_plan_edit_mode").hide();
    $(".work_sheet_main_container_open_plan_edit_mode").show();
}


function openWSPlanEditMode(){
    $("#work_sheet_container_with_header_and_content").attr("open_edit_mode",true);
    $(".work_sheet_main_container_close_plan_edit_mode").show();
    $(".work_sheet_main_container_open_plan_edit_mode").hide();
}



function cancelAssumeFinished(){
    let text =$(this).text();
    confirmInfo("确定"+text+"吗？（系统会根据完成情况重新计算状态）",()=>{
        let wsId = $("#work_sheet_main_container").attr("ws_id");
        sendAjax("CareerServlet","c_cancel_assume_work_sheet_finished",{
            "ws_id" : wsId,
        },(data)=>{
            loadWorkSheetDetail_render(data);
            reloadForExternalModule();
        }) 
    })
}

function assumeFinished(){
    let text =$(this).text();
    confirmInfo("确定"+text+"吗？（系统不会再监控该工作表的完成情况，但仍可编辑）",()=>{
        let wsId = $("#work_sheet_main_container").attr("ws_id");
        sendAjax("CareerServlet","c_assume_work_sheet_finished",{
            "ws_id" : wsId,
        },(data)=>{
            loadWorkSheetDetail_render(data);
            reloadForExternalModule();
        }) 
    })
}


function deleteWorkSheet(){
    confirmInfo("确定删除吗？（系统会清空相关数据并且不可恢复，建议只在更换计划时使用）",()=>{
        let wsId = $("#work_sheet_main_container").attr("ws_id");
        sendAjax("CareerServlet","c_delete_work_sheet",{
            "ws_id" : wsId,
        },(data)=>{
            alertInfo("删除成功");
            reloadForExternalModule();
            if($("#work_sub_main_container").length>0){
                /*工作表模块*/
                closeWsActualContainer();
            }
        }) 
    })
}


function saveWorkSheet(){
    let note = $(this).val();
    let wsId = $("#work_sheet_main_container").attr("ws_id");
    sendAjax("CareerServlet","c_save_work_sheet",{
        "ws_id" : wsId,
        "note" : note
    },(data)=>{
        showForAWhile("已保存",$("#work_save_ws_rlt_container"));
    })
}
function switchToShowTodayPlan(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        hideTodayPlan();
    }else{
        showTodayPlan();
    }    
}

function hideWorkItemNote($container){
    $container.find(".work_sheet_work_item_container_note").addClass("hide_work_item_note").end()
        .find(".work_sheet_work_item_container_note_container").addClass("hide_work_item_note").end()
        .find(".work_sheet_work_item_container_switch_to_show_note").text("显示备注").attr("open",false);
}

function showWorkItemNote($container){
    $container.find(".work_sheet_work_item_container_note").removeClass("hide_work_item_note").end()
        .find(".work_sheet_work_item_container_note_container").removeClass("hide_work_item_note").end()
        .find(".work_sheet_work_item_container_switch_to_show_note").text("隐藏备注").attr("open",true);
}


function switchToShowWorkItemNote(){
    let open = parseToBool($(this).attr("open"));
    let $container = $(this).parents(".work_sheet_work_item_container");
    if(open){
        hideWorkItemNote($container);
    }else{
        showWorkItemNote($container);
    }   
}

function switchToWSLogsBody(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        hideWSLogsBody();
    }else{
        showWSLogsBody();
    }
}

function hideWSLogsBody(){
    $(".work_sheet_logs_content").hide();
    $(".work_sheet_logs_switch_container_visibility").text("展开").attr("open",false);
}

function showWSLogsBody(){
    $(".work_sheet_logs_content").show();
    $(".work_sheet_logs_switch_container_visibility").text("收起").attr("open",true);
}


function switchToShowWorkItemsBody(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        hideWorkItemsBody();
    }else{
        showWorkItemsBody();
    }
}
function hideWorkItemsBody(){
    $("#work_sheet_work_items_container_main_body").hide();
    $(".work_sheet_switch_to_show_work_items_main_body").text("展开").attr("open",false);
}

function showWorkItemsBody(){
    $("#work_sheet_work_items_container_main_body").show();
    $(".work_sheet_switch_to_show_work_items_main_body").text("收起").attr("open",true);
}



function hideTodayPlan(){
    $("#work_sheet_today_play_main_container").hide();
    $(".work_sheet_switch_to_show_today_plan_main_container").text("展开").attr("open",false);
}

function showTodayPlan(){
    $("#work_sheet_today_play_main_container").show();
    $(".work_sheet_switch_to_show_today_plan_main_container").text("收起").attr("open",true);
}


function switchToShowWSNote(){
    let open = parseToBool($(this).attr("open"));
    if(open){
        hideWSNote();
    }else{
        showWSNote();
    }   
}

function hideWSNote(){
    $("#work_sheet_main_container_for_basic_info").hide();
    $(".work_sheet_switch_to_show_ws_note").text("展开").attr("open",false);
}

function showWSNote(){
    $("#work_sheet_main_container_for_basic_info").show();
    $(".work_sheet_switch_to_show_ws_note").text("收起").attr("open",true);
}



function drawWorkSheetDetail(wsId,successFunc){

    showWSNote();
    showTodayPlan();
    showWorkItemsBody();
    closeWSPlanEditMode();
    switchWSPlanToCompletioMode();
    hideWSLogsBody();
    /*切换时删掉提示信息*/
    $("#work_sheet_main_container .common_hint_message").text("");

    $("#work_sheet_work_items_container_main_body_ws_items").empty();

    sendAjax("CareerServlet","c_load_work_sheet",{
        "ws_id" : wsId
    },(data)=>{
        loadWorkSheetDetail_render(data);
        /*work items 假如是今天且状态为进行中 自动打开编辑 否则 自动关闭*/
        if(new Date(data.ws.date).isToday() && data.ws.state.dbCode == WS_NAMESPACE.WS_STATE_OF_ACTIVE){
            openWorkItemsEditModeWithoutConfirm();
        }else{
            closeWorkItemsEditMode();
        }

        if(successFunc!=undefined){
            successFunc();
        }
    })
}
/**
 * 关于WorkItem的保存的调度方式
 * 当任意一条workItem修改后，会有一个x秒后的回调，它会先检验是否已经存在一个x秒后的ajax，假如存在，则刷新，并且标注当前workItem是需要更新的
 * 当准备发出更新op时，会检查所有被标记的workItem 在所有的workItem都成功更新完毕后，更新一整张workSheet
 * 
 * */
function pushSaveWorkItemOpToQueue($ws){
    $ws.attr("to_save",true);
    calculateWorkItemUnitButtonVisible($ws);
    clearTimeout(WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_ID);

    WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_ID = setTimeout(saveChangedWorkItemUnits,WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_SECONDS*1000);
}

/** 
 * 由于WorkItem是根据页面上已存在的信息延时保存的，因此在一切重新加载前 都应把已改过的保存信息保存上
 * 
 * 它会检验一下Saving 是否上锁，在上锁的情况下 会进行自旋 到最大值 仍不能save掉,则此时无论如何都会save
 * @returns 返回是否发了OP 假如为true 那么dom元素的更新 应当由该函数接管 而不是loadWorkSheetDetail_render
 * 
*/
function saveChangedWorkItemUnits(successFunc,reloadWorkSheetAfterSave,suncFuncAfterReload){
    clearTimeout(WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_ID);

    let wsId = $("#work_sheet_main_container").attr("ws_id");
    let $forSave = $("#work_sheet_work_items_container_main_body_ws_items .work_sheet_work_item_container[to_save='"+true+"']");
    let count = 0;

    if($forSave.length == 0){
        if(successFunc != undefined){
            successFunc();
        }
        return false;
    }

    if(WS_NAMESPACE.SAVING_LOCK && WS_NAMESPACE.COUNT_SAVING_ASKING<WS_NAMESPACE.MAX_LIMIT_TO_SEND_SAVING_IN_QUEUE){
        WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_ID = setTimeout(saveChangedWorkItemUnits,WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_SECONDS*1000);
        WS_NAMESPACE.COUNT_SAVING_ASKING++; 
        return true;
    }
    try{
        let $workItemsDom =  $("#work_sheet_work_items_container_main_body_ws_items .work_sheet_work_item_container");
        $workItemsDom.addClass("ws_items_saving")
        $forSave.each((i,v)=>{
            let param = parseWorkItemSaveParam($(v));
            param.push({
                "name":"ws_id",
                "value":wsId
            });
            sendAjax("CareerServlet","c_save_work_item",param,()=>{
                count ++;
                $(v).attr("to_save",false);
                if(count != $forSave.length){
                    /*还有未保存的，等全部保存完 再进下一步骤*/
                    return;
                }
                if(successFunc != undefined){
                    successFunc();
                }
                if (reloadWorkSheetAfterSave == undefined || reloadWorkSheetAfterSave) {
                    /*表明所有都保存成功 重新加载一次workSheet*/
                    sendAjaxBySmartParams("CareerServlet", "c_load_work_sheet", {
                        "ws_id": wsId,
                    }, (data) => {
                        loadWorkSheetDetail_render(data);
                        if(suncFuncAfterReload){
                            suncFuncAfterReload();
                        }
                    },()=>{
                        $workItemsDom.removeClass("ws_items_saving");
                    });
                }
            })
        })
        return true;
    }finally{
        WS_NAMESPACE.COUNT_SAVING_ASKING = 0;
    }
}

function saveWorkItemTypeModified(){
    let $btn = $(this);
    $btn.addClass("common_prevent_double_click");

    let $container = $btn.parents(".work_sheet_work_item_container");
    
    let planItemId =  $container.find(".dropdown-selected").attr("item_id");
    if(planItemId == undefined){
        $container.attr("siwtch_type_modify_on",false);
        return;
    }

    saveChangedWorkItemUnits(()=>{
        sendAjax("CareerServlet","c_save_work_item_plan_item_id",{
            "ws_id":$("#work_sheet_main_container").attr("ws_id"),
            "work_item_id":$(this).parents(".work_sheet_work_item_container").attr("item_id"),
            "plan_item_id":planItemId
        },loadWorkSheetDetail_render)
    },false);
}



function parseWorkItemSaveParam($ws){
    let param = [];
    param.push({
        "name":"mood",
        "value" : $ws.find(".work_sheet_work_item_container_mood_body").attr("mood")
    })
    param.push({
        "name":"for_add",
        "value" : $ws.find(".work_sheet_work_item_container_calculate_info_mark").attr("for_add")
    })
    param.push({
        "name":"work_item_id",
        "value" : $ws.attr("item_id")
    })

    let startTimeInput = $ws.find("[name='start_time']").val();
    let startTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    startTime.overrideTimeInput(startTimeInput);
    
    param.push({
        "name":"start_time",
        "value" : startTime.toString()
    })

    let endTimeInput = $ws.find("[name='end_time']").val();
    let endTime = new Date(parseInt($("#work_sheet_main_container").attr("abs_date")));
    endTime.overrideTimeInput(endTimeInput);
    param.push({
        "name":"end_time",
        "value" : endTime.toString()
    })

    param.push({
        "name":"val",
        "value" : $ws.find("[name='val']").val()
    })
    
    param.push({
        "name":"note",
        "value" : $ws.find("[name='note']").val()
    })

    return param;
}



function drawWorkItems(wsItems,basePlanItems){

    let $container = $("#work_sheet_work_items_container_main_body_ws_items");
    $container.empty();

    //工作项初始化 那么之前所谓Lock也没意义了 因此尝试解锁一下
    unlockSaveWorkItem();

    wsItems.forEach(item=>{
        let planItem = getPlanItemOfWorkItem(item,basePlanItems);

        let remaining  = item.remainingValAtStart;
        let differ = item.item.forAdd ?(remaining+item.item.value):(remaining-item.item.value);
        
        let isMinutes = planItem.item.type.dbCode == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;

        if(item.item.type.dbCode == WS_NAMESPACE.WORK_ITEM_TYPE_OF_DEPT){
            let $ws = $("#work_sheet_pattern_container .work_sheet_work_item_of_dept_container").clone();
            let time = new Date(item.item.startTime);


            $ws.attr({
                "plan_item_type":planItem.item.type.dbCode,
                "item_id":item.item.id
            }).find(".work_sheet_work_item_of_dept_time").text(time.toSmartString()).end()
                .find(".work_sheet_work_item_of_dept_plan_item").html("<em>"+planItem.item.name+"</em>").end()
                .find(".work_sheet_work_item_container_calculate_info_start").text(remaining.toText()).prop("title",isMinutes?remaining.transferToHoursMesIfPossible():"").end()
                .find(".work_sheet_work_item_of_dept_calculate_info_mark").attr("for_add",item.item.forAdd).text(caclulateWorkItemMarkByForAdd(item.item.forAdd)).end()          
                .find(".work_sheet_work_item_of_dept_calculate_info_val").text(item.item.value.toText()).prop("title",isMinutes?item.item.value.transferToHoursMesIfPossible():"").end()
                .find(".work_sheet_work_item_container_calculate_info_rlt").text(differ.toText()).prop("title",isMinutes?differ.transferToHoursMesIfPossible():"").end()
                .find(".work_sheet_work_item_container_calculate_info_type_name").text(planItem.item.type.name).end()

            $container.append($ws);
            return true;
        }


        let $ws = $("#work_sheet_pattern_container .work_sheet_work_item_container").clone();
        let liObjs =  $("#work_sheet_work_items_container_main_body").find(".dropdown-menu").find(".dropdown-item").get().map(v=>{
            return {
                "item_id":$(v).attr("item_id"),
                "cat_type":$(v).attr("cat_type"),
                "val":$(v).text(),
                "selected" : parseInt($(v).attr("item_id")) == planItem.item.id
            }
        });

        let startTime = new Date(item.item.startTime);
        let endTime = new Date(item.item.endTime);


        $ws.find(".work_sheet_work_item_container_change_item_type_container").dropdown({
            readOnly: true,
            attrs:["item_id","cat_type"],
            liObjs:liObjs,
            searchInputContext: '按关键词搜索',
            choice: function(targetLi,$showText) {
              $showText.attr({
                  "item_id": targetLi.item_id
              })
            }
          });
        

        $ws.attr({
            "plan_item_type":planItem.item.type.dbCode,
            "item_id":item.item.id,
            "siwtch_type_modify_on":false
        }).find(".work_sheet_work_item_container_change_item_type_container").find("select").prop("placeholder",planItem.item.name)
        .end().prop("value","")
        .end().find(".work_sheet_work_item_container_start_time_span").text(startTime.toHoursAndMinutesOnly("<em>无</em>")).end()
            .find("[name='start_time']").val(startTime.toStandardHoursAndMinutesOnly()).end()
            .find(".work_sheet_work_item_container_calculate_info_start").text(remaining.toText()).prop("title",isMinutes?remaining.transferToHoursMesIfPossible():"").end()
            .find(".work_sheet_work_item_container_calculate_info_mark").attr("for_add",item.item.forAdd).text(caclulateWorkItemMarkByForAdd(item.item.forAdd)).end()          
            .find(".work_sheet_work_item_container_calculate_info_val").text(item.item.value.toText()).prop("title",isMinutes?item.item.value.transferToHoursMesIfPossible():"").end()
            .find("[name='val']").val(item.item.value).prop("title",isMinutes?item.item.value.transferToHoursMesIfPossible():"").end()
            .find(".work_sheet_work_item_container_calculate_info_rlt").text(differ.toText()).prop("title",isMinutes?differ.transferToHoursMesIfPossible():"").end()
            .find(".work_sheet_work_item_container_calculate_info_type_name").text(planItem.item.type.name).end()
            .find(".work_sheet_work_item_container_end_time_span").html(endTime.toHoursAndMinutesOnly("<em>进行中</em>")).end()
            .find("[name='end_time']").val(endTime.toStandardHoursAndMinutesOnly("")).end()
            .find(".work_sheet_work_item_container_mood").append(calculateMoodSpan(item.item.mood)).end()
            .find(".work_sheet_work_item_container_plan_item_context").html("<em>"+planItem.item.name+"</em>").end()
            .find(".work_sheet_work_item_container_note_body").html(item.item.note.replaceAll("\n","<br/>"));

        if(isPC()){
            $ws.find(".work_sheet_work_item_unit_container_for_phone").remove();
        }else{
            $ws.find(".work_sheet_work_item_container_plan_item").remove();
        }

        calculateWorkItemUnitButtonVisible($ws);
        
        /*进行中的workItem 自动打开备注 否则会关闭*/
        if(new Date(item.item.endTime).isBlank()){
            showWorkItemNote($ws);
        }else{
            hideWorkItemNote($ws);
        }

        $container.append($ws);

        fillTextareaVal(item.item.note,$ws.find(".work_sheet_work_item_container_note"));
    })
}
/** 
 * 假如是0 则不设置背景色
 * 1 2 3 4 5
 */
function calculateMoodSpan(val){
    let $span = $("<span>");
    $span.addClass("work_sheet_work_item_container_mood_body").attr({
        "mood":val,
    }).text(val);
    if(val == 0){
        $span.css({
            "background-color":"#e7e7f8",
            "color" : "#e7e7f8"
        });
        return $span;
    }
    let startColor = "rgb(100, 125, 51)";
    let endColor = "rgb(40, 180, 69)";
    let thisColor = calculateColorByRange(startColor,endColor,WS_NAMESPACE.WORK_ITEM_MOOD_MIN_VAL,WS_NAMESPACE.WORK_ITEM_MOOD_MAX_VAL,val);
    $span.css(getFontColorAndBackgroudColor(thisColor));
    return $span;
}

/**
 * 结束时间的计算按钮 必须是要以time为type 且 val有值>0
 * 持续值得计算按钮 必须要开始时间&结束时间有值 假如是次数 则默认一次
 * */
function calculateWorkItemUnitButtonVisible($ws){
    let isMinutesType  =  parseInt($ws.attr("plan_item_type")) == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;
    let endTimeIsNull = $ws.find("[name='end_time']").val().trim().length == 0;
    let startTimeIsNull = $ws.find("[name='start_time']").val().trim().length == 0;
    let valIsNullOrZero = $ws.find("[name='val']").val().trim().length == 0 || parseInt($ws.find("[name='val']").val().trim()) == 0;

    $ws.find(".work_sheet_work_item_calculate_val_by_end_time_btn").toggleClass("hide_work_item_unit",endTimeIsNull||startTimeIsNull).end()
        .find(".work_sheet_work_item_calculate_end_time_by_val_btn").toggleClass("hide_work_item_unit",!isMinutesType || valIsNullOrZero).end()
}

function caclulateWorkItemMarkByForAdd(forAdd){
    return forAdd ? "+" : "-";
}


function loadWorkSheetDetail_render(data) {
    /**加载时，代表着要刷新容器了,那么检查一下有没有需要save的 假如有的话 就重新save一下 */
    if(saveChangedWorkItemUnits()){
        return ;
    }

    /*
    * 更新前需要根据Id监控哪些workItem打开/关闭了备注 当重新更新完数据后 要保持原样
    */
    let idsForOpened = [];
    let idsForClosed = [];
    $("#work_sheet_work_items_container_main_body_ws_items .work_sheet_work_item_container").each((i, v) => {
        let open = parseToBool($(v).find(".work_sheet_work_item_container_switch_to_show_note").attr("open"));
        if (open) {
            idsForOpened.push($(v).attr("item_id"));
        } else {
            idsForClosed.push($(v).attr("item_id"));
        }
    });

    /*同步工作表模块 左侧工作列表 该日工作存在未完成项，请尽快处理 信息*/
    if($("#work_sub_main_container").length>0){
        /*工作表模块*/
        refreshWokrListUnitForExternalInvoker(data.ws);
    }


    try{
        let $container = $("#work_sheet_main_container");
        $container.attr({
            "ws_id":data.ws.id,
            "abs_date" : data.ws.date
        }).find(".work_sheet_main_container_header_date_title").text(new Date(data.ws.date).toChineseDate()).end()
            .find(".work_sheet_main_container_header_base_plan>em").text(data.basePlanName).end()
            
            .find(".work_sheet_main_container_header_mood").empty().append(calculateMoodSpan(data.mood.toFixed(1))).end()
            .find(".work_sheet_main_container_header_state>span").text(data.ws.state.name).css(getFontColorAndBackgroudColor(data.ws.state.color))
        
        if(!$("#work_note_textarea").is(":focus")){
            /*假设focus了 说明用户正在编辑textarea 就不必打扰了，反正等到它编辑完毕还要保存*/
            fillTextareaVal(data.ws.note,$("#work_note_textarea"));
        }


        $(".work_sheet_main_container_cancel_assumen_finished").toggle(data.ws.state.dbCode == WS_NAMESPACE.WS_STATE_OF_ASSUME_FINISHED);
        $(".work_sheet_main_container_assume_finsihed").toggle(data.ws.state.dbCode == WS_NAMESPACE.WS_STATE_OF_OVERDUE
                                                                || data.ws.state.dbCode == WS_NAMESPACE.WS_STATE_OF_ACTIVE
                                                                || data.ws.state.dbCode == WS_NAMESPACE.WS_STATE_OF_OVER_FINISHED);
    
        $("#work_sheet_work_items_container_main_body_save_hint").find(".save_hint_seconds").text(WS_NAMESPACE.SAVE_WORK_ITEM_OP_TIMEOUT_SECONDS).end()
                .find(".save_hint_update_time").text(new Date(data.ws.updateTime).toSmartString());
    
    
        drawWSPlanAndDropDown(data.content.planItems);
        drawWorkItems(data.content.workItems,data.content.planItems);
    
        /*下拉框默认选择 无 默认启动增加模式*/
        openWSPlanItemControlgroupAddMode();
        $("#work_sheet_today_plan_control_group_container").find(".dropdown-item[item_id='0']").click();
    
        drawCommonLogs(data.content.logs,$("#work_sheet_logs_container"))
        $("#work_sheet_latest_update_time>span").text(new Date(data.ws.updateTime).toSmartString());

        /*初始化items 部分 TODO 还应该清空选项*/
        $(".work_sheet_today_plan_items_for_one_row.work_sheet_today_plan_items_values_container>div").hide();    
        $("#work_sheet_today_plan_control_group_container").find("[type='text'],textarea").val("");

    }finally{
        $("#work_sheet_work_items_container_main_body_ws_items .work_sheet_work_item_container").each((i, v) => {
            if (idsForOpened.indexOf($(v).attr("item_id")) != -1) {
                showWorkItemNote($(v))
            }
            if (idsForClosed.indexOf($(v).attr("item_id")) != -1) {
                hideWorkItemNote($(v))
            }
        })
    }



    

}



function drawWSPlanAndDropDown(planItems){

   /*从属的上拉框*/
   let $dropDownContainerForPlanItem  =$("#work_sheet_today_plan_control_group_container").find(".dropdown-menu");
   let $dropDownContaienrForWorkItem = $("#work_sheet_work_items_container_main_body").find(".dropdown-menu");

   $dropDownContainerForPlanItem.empty();
   $dropDownContaienrForWorkItem.empty();

   let cats = [];
   traverseWSPlanItems(planItems,(item)=>{
       cats.push({
           "id" : item.item.id,
           "name" : item.item.name,
           "type" : item.item.type.dbCode
       });
   })
   cats.push({
       "id" : 0,
       "name" : "无"
   });

   cats.forEach(cat=>{
       let $dropDownForPlan = getDropDownItemButton();
       let $dropDownForWorkItem = getDropDownItemButton();
       $dropDownForWorkItem.text(cat.name).attr({
        /*catId就是itemId*/
        "item_id":cat.id,
        "cat_type" :cat.type
        });
       $dropDownForPlan.text(cat.name).attr({
           /*catId就是itemId*/
           "item_id":cat.id,
           "cat_type" :cat.type
       });
       $dropDownContainerForPlanItem.append($dropDownForPlan);
       if(cat.id!=0){
           /*ws不需要无*/
            $dropDownContaienrForWorkItem.append($dropDownForWorkItem);
       }
   })

   /*PlanItems的条目*/
   let $itemsCardContainer = $("#work_sheet_today_plan_items_cards_container");
   $itemsCardContainer.empty();

   let $pattern = $("#work_sheet_pattern_container");
   let $root = $pattern.find(".work_sheet_plan_item_container_root_container").clone();
   $root.find(".work_sheet_plan_item_container_root_controlgroup").attr("item_id",0);
   $itemsCardContainer.append($root);

   /**第一层特殊处理 */
   let $firstLevelContainer
   for(let i=0 ; i<planItems.length ;i++){
       $firstLevelContainer = $pattern.find(".work_sheet_plan_item_container_first_level").clone();
       $firstLevelContainer.attr({
        "fold" : planItems[i].item.fold
        }).find(".work_sheet_plan_item_container_footer").attr({
           "item_id":planItems[i].item.id,
           "father_id" : 0,
           "cat_name" : planItems[i].item.name,
           "item_val" : planItems[i].item.value,
           "item_note" : planItems[i].item.note
       });
       if(planItems[i].item.note.length>0){
           $firstLevelContainer.find(".work_sheet_plan_item_container_body").prop("title",planItems[i].item.note);
       }

       $firstLevelContainer.find(".work_sheet_plan_item_container_body").html(calculateRootPlanItemText(planItems[i].item))

       fillCompetionInfo($firstLevelContainer,planItems[i]);

       drawSonWSPlanItemCards(planItems[i],$firstLevelContainer);

       $itemsCardContainer.append($firstLevelContainer);
   }
   /*最后一个*/
   if($firstLevelContainer != undefined){
       $firstLevelContainer.addClass("last_one");
   }

   $(".work_sheet_main_container_sync_all_plan_item")
        .toggleClass("hide_ws_btn",$("#work_sheet_today_plan_items_cards_container .work_sheet_plan_sync_completion_to_dept:not(.hide_ws_btn)").length == 0);
}


function fillCompetionInfo($contianer,plan){
    let remaining  = plan.remainingValForCur;
    let sum = parseFloat((parseFloat(remaining)+ parseFloat(plan.sumValForWorkItems)));
    
    sum = parseInt(sum) == sum ? parseInt(sum) :sum;

    let mathMark = plan.sumValForWorkItems < 0 ? "+" : "-";


    let html;
    let absSumVal = Math.abs(plan.sumValForWorkItems);
    if(plan.item.type.dbCode == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES){
        
        html = "<span title='以该项计算得出的差值'>"+plan.item.name+"  "+"<span title='"+plan.sumValForWorkItems.transferToHoursMesIfPossible() +"'>"+plan.sumValForWorkItems+"</span>"+plan.item.type.name+"<little>"
        +"<span title='"+sum.transferToHoursMesIfPossible() +"'>"+sum.toText()+"</span> "+mathMark+" "
        +"<span title='"+absSumVal.transferToHoursMesIfPossible() +"'>"+absSumVal+"</span> = "
        +"<span title='"+remaining.transferToHoursMesIfPossible() +"'>"+remaining.toText()+"</span></little></span>";
    }else{
        html = "<span title='以该项计算得出的差值'>"+plan.item.name+"  "+plan.sumValForWorkItems+""+plan.item.type.name+"<little>"+sum.toText()+" "+mathMark+" "+ Math.abs(plan.sumValForWorkItems)+" = "
        +remaining.toText()+"</little></span>";
    }

    $contianer.find(".work_sheet_plan_item_completion_body").html(html).end()
        .find(".work_sheet_plan_sync_completion_to_dept").toggleClass("hide_ws_btn",remaining == 0);
}





function drawSonWSPlanItemCards(fatherItem,$container){
    let items  = fatherItem.descendants;
    let fatherId =  $container.find(".work_sheet_plan_item_container_footer").attr("item_id");
    
    $container.attr("has_next_level",items.length>0);
    
    let $unitLevelContainer
    for(let i=0 ; i<items.length ;i++){
        $unitLevelContainer = $("#work_sheet_pattern_container").find(".work_sheet_plan_item_container_unit_level").clone();
        $unitLevelContainer.attr({
            "fold" : items[i].item.fold
        }).find(".work_sheet_plan_item_container_footer").attr({
            "item_id":items[i].item.id,
            "father_id" :fatherId,
            "cat_name" : items[i].item.name,
            "mapping_val" :items[i].item.mappingValue,
            "item_note" : items[i].item.note
        });
        if(items[i].item.note.length>0){
            $unitLevelContainer.find(".work_sheet_plan_item_container_body").prop("title",items[i].item.note);
        }

        $unitLevelContainer.find(".work_sheet_plan_item_container_body").html(calculateUnitPlanItem(items[i].item,fatherItem.item));

        fillCompetionInfo($unitLevelContainer,items[i]);
                
        $container.children(".work_sheet_plan_item_container_wrap").append($unitLevelContainer);

        drawSonWSPlanItemCards(items[i],$unitLevelContainer);
    }
    /*最后一个*/
    if($unitLevelContainer != undefined){
        $unitLevelContainer.addClass("last_one");
    }
}