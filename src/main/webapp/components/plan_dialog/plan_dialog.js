const PLAN_DIALOG_NAMESPACE = {
    CURRENT_PLAN : {},
    RELOAD_FUNC_FOR_SYNC_PLAN_TAGS:null
};
/**
 * plan item id ==0 means root
 */
$(function(){

    drawCommonIcon("including_modify_mark",$("#plan_dialog_pattern_container .plan_item_modify_mark"));
    drawCommonIcon("including_add_mark",$("#plan_dialog_pattern_container .plan_item_add_mark"));
    drawCommonIcon("including_minus_mark",$("#plan_dialog_pattern_container .plan_item_delete_mark"));
    drawCommonIcon("including_circle_mark",$("#plan_dialog_pattern_container .plan_item_circle_container"));
    drawCommonIcon("including_circle_with_left_line_mark",$("#plan_dialog_pattern_container .plan_item_root_add_mark"));

    drawCommonIcon("including_open_folder_mark",$("#plan_dialog_pattern_container .plan_item_unfold_btn"));
    drawCommonIcon("including_close_folder_mark",$("#plan_dialog_pattern_container .plan_item_fold_btn"));
    drawCommonIcon("including_common_edit_shape",$("#plan_dialog_basic_unit_container_edit_btn"));


    $("#plan_dialog_basic_unit_container_edit_btn").click(openPlanTagEditDialog);

    $("#plan_dialog_basic_info_main_container")
        .find(".plan_dialog_switch_container_visibility").click(function(){
            switchToShowPlanDialogContainer(this,showPlanDialogBasicInfoContainer,hidePlanDialogBasicInfoContainer)
        })

    $("#plan_dialog_items_main_container")
        .find(".plan_dialog_switch_container_visibility").click(function(){
            switchToShowPlanDialogContainer(this,showPlanDialogItemsContainer,hidePlanDialogItemsContainer)
        })

    $("#plan_dialog_logs_main_container")
        .find(".plan_dialog_switch_container_visibility").click(function(){
            switchToShowPlanDialogContainer(this,showPlanDialogLogsContainer,hidePlanDialogLogsContainer)
        })    


    $("#plan_dialog_save_plan_basic_info_button").click(commitBasicInfo);
    $("#plan_dialog_basic_info_sub_container").find("[name='name']").change(checkPlanBasicInfoNameLegal)
        .end().find("[name='start_date']").change(checkPlanBaickInfoStartDateLegal)
        .end().find("[name='end_date']").change(checkPlanBaickInfoEndDateLegal)
        .end().find("[name='seq_weight']").on("input",inputOnlyAllowInt);

    
    $("#plan_dialog_close_edit_mode").click(closePlanDialogEditMode);
    $("#plan_dialog_open_edit_mode").click(openPlanDialogEditMode);

    $("#work_end_date_null_for_save_plan").click(switchPlanBasicInfoEndDateNull);

    $("#plan_dialog_items_control_group_container").find("[name='cat_name']").change(switchToShowItemsValContainer)
    .autocomplete({
        minLength:0,
        source : []
    }).focus(getPlanDeptItemNamesForPlanDialog)
    .on("autocompletechange",switchToShowItemsValContainer).end()
    .find("[name='cat_name_when_modify']")
    .autocomplete({
            minLength:0,
            source : []
    }).focus(getPlanDeptItemNamesForPlanDialog)
        .change(checkCatNameLegalWhenModifying).on("input",syncCatNameWhenModify)
        .on("autocompletechange",syncCatNameWhenModify).end()
        .find("[name='cat_type']").click(switchToShowItemsValContainer).end()
        .find("[name='value_by_minutes']").on("input",inputOnlyAllowInt).end()
        .find("[name='value_by_times']").on("input",inputOnlyAllowInt).end()
        .find("[name='mapping_val_for_differ_type']").on("input",inputOnlyAllowInt).end()
        .find("[name='mapping_val_for_same_type']").on("input",inputOnlyAllowFloat).end()

    $("#plan_dialog_items_father_and_son_relationship_container").on("click",".dropdown-item",switchToShowItemsValContainerThroughFatherRealtion);

    $("#plan_dialog_add_item_button").click(addItemToPlan);
    $("#plan_dialog_save_item_button").click(savePlanItem);

    $("#plan_dialog_items_cards_container").on("click",".plan_item_root_add_mark",addRootItemByClickMark)
        .on("click",".plan_item_add_mark",addItemByClickMark)
        .on("click",".plan_item_delete_mark",deleteItemByClickMark)
        .on("click",".plan_item_modify_mark",modifyItemByClickMark)
        .on("click",".plan_item_fold_btn",foldPlanItemFoldBtn)
        .on("click",".plan_item_unfold_btn",unfoldPlanItemFoldBtn)
        .on("click",".plan_item_save_fold_info",savePlanItemFoldInfo);


    $(".plan_dialog_basic_info_copy_btn").click(() => {
        copyToClipboard($(".plan_dialog_basic_info_decoded_id").text(), $("#plan_dialog"));
        showForAWhile("??????", $(".plan_dialog_basic_info_copy_hint"));
    })
    
    $("#plan_dialog_copy_plan_btn").click(copyPlanItemsById);

    $("#plan_dialog_sync_plan_tags_to_ws_container_btn").click(syncPlanTagsToWS);

})

function syncPlanTagsToWS(){
    confirmInfo("????????????????????????????????????????????????????????????<em>"+$("#plan_dialog_label em").text()+"</em>??????",()=>{
        sendAjaxBySmartParams("CareerServlet","c_sync_plan_tags_to_workSheet",{
            "plan_id":PLAN_DIALOG_NAMESPACE.CURRENT_PLAN.plan.id
        },refreshExternalIfExisted)
    })
}

function refreshExternalIfExisted(){
    if(PLAN_DIALOG_NAMESPACE.RELOAD_FUNC_FOR_SYNC_PLAN_TAGS){
        PLAN_DIALOG_NAMESPACE.RELOAD_FUNC_FOR_SYNC_PLAN_TAGS();
    }
}


function openPlanTagEditDialog(){
    openTagEditDialogForExternalModule((tags,closeDialogFunc,removeDoubleClsFunc)=>{
        sendAjaxBySmartParams("CareerServlet","c_reset_plan_tags",{
            "plan_id":PLAN_DIALOG_NAMESPACE.CURRENT_PLAN.plan.id,
            "tags":tags
        },(data)=>{
            fillPlanDialogByDate(data);
            closeDialogFunc();
        },removeDoubleClsFunc)
    },PLAN_DIALOG_NAMESPACE.CURRENT_PLAN.plan.tags,"c_load_all_plan_tags");
}


function foldPlanItemFoldBtn(){
    let $parentContainer =  getSelfContainerForFoldBtns(this);
    $parentContainer.attr("fold",true);
}

function getSelfContainerForFoldBtns(dom){
    return $(dom).parent().parent().parent().parent(".plan_item_container_first_level,.plan_item_container_unit_level")
}

function unfoldPlanItemFoldBtn(){
    let $parentContainer =  getSelfContainerForFoldBtns(this);
    $parentContainer.attr("fold",false);
}

function savePlanItemFoldInfo(){
    let planId = $("#plan_dialog").attr("plan_id");
    let fold = getSelfContainerForFoldBtns(this).attr("fold");
    let itemId = $(this).parent().siblings(".plan_item_container_footer").attr("item_id");

    $(this).addClass("common_prevent_double_click");

    sendAjaxBySmartParams("CareerServlet","c_save_plan_item_fold",{
        "plan_id":planId,
        "item_id":itemId,
        "fold": fold,
    },()=>{
        showForAWhile("?????????",$(this).siblings(".plan_item_save_fold_hint"),5000,()=>{
            $(this).hide();
        },()=>{
            $(this).show();
        })
    },()=>{
        $(this).removeClass("common_prevent_double_click");
    })
}


function copyPlanItemsById(){
    let planId = $("#plan_dialog").attr("plan_id");
    let templeId = $(".plan_dialog_copy_plan_items_container").find("[name='copy_plan_id']").val();
    if(templeId.trim().length == 0){
        alertInfo("??????????????????????????????ID");
        return;
    }

    confirmInfo("??????????????????????????????,??????????????????",()=>{
        let param = {
            "target_plan_id" :planId,
            "temple_plan_id" : templeId,
        }
        sendAjax("CareerServlet","c_copy_plan_items_by_id",param,(data)=>{
            showForAWhile("????????????",$("#plan_dialog_copy_plan_hint_mes"));
            fillPlanDialogByDate(data);
        });
    })
}



function getPlanDeptItemNamesForPlanDialog(){
    sendAjax("CareerServlet","c_load_plan_dept_item_names",{},(data)=>{
        fillAutocompletInput(data,$(this));
    });
}




function modifyItemByClickMark(){
    let $container = $("#plan_dialog_items_control_group_container");
    let $footer  =  $(this).parent(".plan_item_container_footer");
    let $item = findDropDownItemByItemId($footer.attr("item_id"));
    let fatherId = parseInt($(this).parent(".plan_item_container_footer").attr("father_id"));
    let $fatherItem = findDropDownItemByItemId(fatherId);
    let itemTypeCode= parseInt($item.attr("cat_type"));
    let typeText = $container.find("[name='cat_type']").filter((i,v)=>parseInt($(v).val())==itemTypeCode).parent("label").text();
    let catName = $footer.attr("cat_name");

    $("#plan_dialog_item_id_when_modify_item").val($footer.attr("item_id"));

    /*???cat?????????????????????*/
    $container.find("[cat_name_anchor='true']").attr("cat_name_anchor",false);
    $container.find(".plan_dialog_items_values_container>div").hide();
    openItemControgroupSaveMode();

    $(".plan_dialog_type_when_modifying").text(typeText);
    $(".plan_dialog_subordinate_when_modifying").text($fatherItem.text());
    $("#plan_dialog_items_control_group_container [name='cat_name_when_modify']").val(catName).change().end()
        .find("[name='note']").val($footer.attr("item_note"));


    let isFather = fatherId == 0;
    let isMinutesType = itemTypeCode == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES;
    if(isFather){
        $("#plan_dialog_items_inputs_for_minutes_type").toggle(isMinutesType);
        $("#plan_dialog_items_inputs_for_times_type").toggle(!isMinutesType);
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
        let $targetContainerOfDifferType =  $("#plan_dialog_items_inputs_for_different_type_mapping");
        let timesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? catName : fatherText;
        let minutesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? fatherText : catName;
        $targetContainerOfDifferType.find(".plan_dialog_items_inputs_of_times_tyep").attr("cat_name_anchor",true).text(timesTarget).end()
            .find(".plan_dialog_items_inputs_of_minutes_tyep").text(minutesTarget).end()
            .find("[name='mapping_val_for_differ_type']").val(mappinVal);
        $targetContainerOfDifferType.show();
        return;
    }
    
    let $targetContainerOfSameType = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ?  $("#plan_dialog_items_inputs_for_same_type_mapping_of_times") : $("#plan_dialog_items_inputs_for_same_type_mapping_of_minutes");
    $targetContainerOfSameType.find(".plan_dialog_items_son_title").attr("cat_name_anchor",true).text(catName).end()
        .find("[name='mapping_val_for_same_type']").val(mappinVal).end()
        .find(".plan_dialog_items_father_title").text(fatherText).end().show()
}

function openItemControgroupSaveMode(){
    $("#plan_dialog_items_control_group_container").attr("controgroup_mode","save");
}


function deleteItemByClickMark(){
    confirmInfo("???????????????????????????????????????????????????",()=>{
        let itemId = $(this).parent(".plan_item_container_footer").attr("item_id");
        let planId = $("#plan_dialog").attr("plan_id");
        sendAjax("CareerServlet","c_remove_item_plan",{
            "plan_id" : planId,
            "item_id" :itemId
        },fillPlanDialogByDate);
    })
}


/**???????????? ??????itemId ??????????????????????????????????????? */
function addRootItemByClickMark(){
    let itemId = $(this).parent(".plan_item_container_root_controlgroup").attr("item_id");
    findSubItemAndClick(itemId);
    openItemControgroupAddMode();
}

function openItemControgroupAddMode(){
    $("#plan_dialog_items_control_group_container").attr("controgroup_mode","add");
}


function addItemByClickMark(){
    let itemId = $(this).parent(".plan_item_container_footer").attr("item_id");
    findSubItemAndClick(itemId);
    openItemControgroupAddMode();
}

function findDropDownItemByItemId(itemId){
    return $("#plan_dialog_items_father_and_son_relationship_container .dropdown-item[item_id='"+itemId+"']");
}

function findSubItemAndClick(itemId){
    findDropDownItemByItemId(itemId).click();    
}

function sendSavePlanItemOp(param){
    if(param.val != undefined && param.val.length == 0){
        param.val = 0
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_VAL,()=>{
            sendAjax("CareerServlet","c_save_plan_item",param,(data)=>{
                showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
                fillPlanDialogByDate(data);
            })
        })
        return;
    } 
    if(param.mapping_val !=undefined && param.mapping_val.trim().length == 0){
        param.mapping_val = 0
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_MAPPING_VAL,()=>{
            sendAjax("CareerServlet","c_save_plan_item",param,(data)=>{
                showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
                fillPlanDialogByDate(data);
            })
        })
        return;
    }

    sendAjax("CareerServlet","c_save_plan_item",param,(data)=>{
        showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
        fillPlanDialogByDate(data);
    });
}

function savePlanItem(){
    let $container = $("#plan_dialog_items_control_group_container");
    let catName = $container.find("[name='cat_name_when_modify']").val().trim();
    if(catName.length == 0){
        alertInfo("????????????????????????????????????");
        return;
    }

    preventDoubleClick(this);

    let itemId = $("#plan_dialog_item_id_when_modify_item").val();
    let planId = $("#plan_dialog").attr("plan_id");
    let note = $container.find("[name='note']").val();
    
    let param = {
        "plan_id" :planId,
        "cat_name" : catName,
        "note" : note,
        "item_id" :itemId
    }

    if($("#plan_dialog_items_inputs_for_minutes_type").is(":visible")){
        let val = $container.find("[name='value_by_minutes']").val();
        param.val = val;
        sendSavePlanItemOp(param);
    }else if($("#plan_dialog_items_inputs_for_times_type").is(":visible")){
        let val =$container.find("[name='value_by_times']").val();
        param.val = val;
        sendSavePlanItemOp(param);
    }else if($("#plan_dialog_items_inputs_for_same_type_mapping_of_minutes").is(":visible")){
        let mappingVal = $("#plan_dialog_items_inputs_for_same_type_mapping_of_minutes").find("[name='mapping_val_for_same_type']").val();
        param.mapping_val = mappingVal;
        sendSavePlanItemOp(param);
    }else if($("#plan_dialog_items_inputs_for_same_type_mapping_of_times").is(":visible")){
        let mappingVal = $("#plan_dialog_items_inputs_for_same_type_mapping_of_times").find("[name='mapping_val_for_same_type']").val()
        param.mapping_val = mappingVal;
        sendSavePlanItemOp(param);
    }else{
        if(!$("#plan_dialog_items_inputs_for_different_type_mapping").is(":visible")){
            throw "????????????!";
        }
        let mappingVal = $("#plan_dialog_items_inputs_for_different_type_mapping").find("[name='mapping_val_for_differ_type']").val();
        param.mapping_val = mappingVal;
        sendSavePlanItemOp(param);
    }
}


function addItemToPlan(){
    let $container = $("#plan_dialog_items_control_group_container");
    let $catNameInput = $container.find("[name='cat_name']");
    let catName = $catNameInput.val().trim();
    $(".plan_dialog_items_error_mes.items_header").toggle(catName.length == 0).text(catName.length == 0 ? "??????????????????":"");
    $catNameInput.parents(".plan_dialog_items_unit_container").attr("test",catName.length > 0);
    if(catName.length == 0){
        alertInfo("????????????????????????????????????");
        return;
    }

    preventDoubleClick(this);

    let fatherId = parseInt($("#plan_dialog_items_father_relation_btn").attr("father_id"));
    let isFather = fatherId == 0;
    let planId = $("#plan_dialog").attr("plan_id");
    let note = $container.find("[name='note']").val();
    let catType = $container.find("[name='cat_type']:checked").val();

    if(isFather){
        let usingMnutesType = $container.find("[name='cat_type'][minutes]").prop("checked");
        let val = usingMnutesType ? $container.find("[name='value_by_minutes']").val() : $container.find("[name='value_by_times']").val();
        let param = {
            "plan_id" :planId,
            "cat_name" : catName,
            "val" : val.length == 0 ? 0 : val,
            "note" : note,
            "cat_type" : catType,
            "father_id" : fatherId,
        }

        if(val.length == 0){
            confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_VAL,()=>{
                sendAjax("CareerServlet","c_add_item_to_plan",param,(data)=>{
                    showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
                    fillPlanDialogByDate(data);
                })
            })
            return;
        } 
        sendAjax("CareerServlet","c_add_item_to_plan",param,(data)=>{
            showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
            fillPlanDialogByDate(data);
        });
        return;
    }

    let $fatherDrop = $("#plan_dialog_items_father_relation_btn");
    let sonType = parseInt($container.find("[name='cat_type']:checked").val());
    let fatherType = parseInt($fatherDrop.attr("father_type"));
    let mappingVal;
    if( sonType != fatherType){
        mappingVal = $("#plan_dialog_items_inputs_for_different_type_mapping").find("[name='mapping_val_for_differ_type']").val();
    }else{
        mappingVal = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? $("#plan_dialog_items_inputs_for_same_type_mapping_of_times").find("[name='mapping_val_for_same_type']").val()
                : $("#plan_dialog_items_inputs_for_same_type_mapping_of_minutes").find("[name='mapping_val_for_same_type']").val();
    }
    let param = {
        "plan_id" :planId,
        "cat_name" : catName,
        "mapping_val" : mappingVal.length == 0 ? 0 : mappingVal,
        "note" : note,
        "cat_type" : catType,
        "father_id" : fatherId,
    }
    if(mappingVal.length == 0){
        confirmInfo(BASIC_NAMESPACE.WS_HINT_FOR_NULL_PLAN_ITEM_MAPPING_VAL,()=>{
            sendAjax("CareerServlet","c_add_item_to_plan",param,(data)=>{
                showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
                fillPlanDialogByDate(data);
            })
        })
        return;
    } 
    sendAjax("CareerServlet","c_add_item_to_plan",param,(data)=>{
        showForAWhile("????????????",$("#plan_dialog_save_or_add_item_hint_mes"));
        fillPlanDialogByDate(data);
    });
}


function switchToShowItemsValContainerThroughFatherRealtion(){
    let text = $(this).text();
    let id = $(this).attr("item_id");
    $("#plan_dialog_items_father_relation_btn").attr({
        "father_id":id,
        "father_type" : $(this).attr("cat_type")
    }).text(text);


    switchToShowItemsValContainer();
}


function initItemsValContainer(){
    let $container = $("#plan_dialog_items_control_group_container");
    
    $(".plan_dialog_items_inputs_by_type_container").find("[type='text'],textarea").val("").end();
    $(".plan_dialog_copy_plan_items_container").find("[type='text']").val("").end();

    if($container.find("[name='cat_type']:checked").length == 0){
        $container.find("[name='cat_type']").eq(0).click();
    }

}

function switchToShowItemsValContainer(){
    let $container = $("#plan_dialog_items_control_group_container");

    $(".plan_dialog_items_form_one_row.plan_dialog_items_values_container>div").hide();
    let $fatherDrop = $("#plan_dialog_items_father_relation_btn");
    let isFather = parseInt($fatherDrop.attr("father_id")) == 0;
    
    let $catNameInput = $container.find("[name='cat_name']");
    let catName = $catNameInput.val().trim();
    
    /*?????????????????????????????????????????????????????????*/
    $errorContainer = $(".plan_dialog_items_error_mes.items_header");
    if(catName.length == 0 && !isFather){
        return;
    }else{
        $errorContainer.hide().text("");
        $catNameInput.parents(".plan_dialog_items_unit_container").attr("test",true);
    }

    if(isFather){
        $("#plan_dialog_items_inputs_for_minutes_type").toggle($container.find("[name='cat_type'][minutes]").prop("checked"));
        $("#plan_dialog_items_inputs_for_times_type").toggle($container.find("[name='cat_type'][times]").prop("checked"));

        initItemsValContainer();
        return;
    }

    if($container.find("[name='cat_type']:checked").length == 0){
        alertInfo("??????????????????????????????????????????????????????");
        return;
    }
    
    initItemsValContainer();

    let sonType = parseInt($container.find("[name='cat_type']:checked").val());
    let fatherType = parseInt($fatherDrop.attr("father_type"));
    let fatherText = $fatherDrop.text();
    if( sonType != fatherType){
        let $targetContainerOfDifferType =  $("#plan_dialog_items_inputs_for_different_type_mapping");
        let timesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? catName : fatherText;
        let minutesTarget = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ? fatherText : catName;
        $targetContainerOfDifferType.find(".plan_dialog_items_inputs_of_times_tyep").text(timesTarget).end()
            .find(".plan_dialog_items_inputs_of_minutes_tyep").text(minutesTarget);
        $targetContainerOfDifferType.show();
        return;
    }

    let $targetContainerOfSameType = sonType == BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_TIMES ?  $("#plan_dialog_items_inputs_for_same_type_mapping_of_times") : $("#plan_dialog_items_inputs_for_same_type_mapping_of_minutes");
    $targetContainerOfSameType.find(".plan_dialog_items_son_title").text(catName).end()
        .find(".plan_dialog_items_father_title").text(fatherText).end().show();

}



function switchPlanBasicInfoEndDateNull(){
    let choosed = parseToBool($(this).attr("choosed"));
    let $endDate = $("#plan_dialog_basic_info_sub_container").find("[name='end_date']");
    let $container = $endDate.parents(".plan_dialog_basic_unit_container");
    if(!choosed){
        $endDate.val("");
        $container.attr("test",true);
        $(".plan_dialog_basic_info_form_error_mes.p_date").hide().text("");
    }
    $container.attr("switch_to_null",!choosed);
    $(this).attr("choosed",!choosed)
}


function checkPlanName(text){
    return text.length<21 && text.length>0;
}

function syncCatNameWhenModify(){
    let $anchor = $("#plan_dialog_items_control_group_container [cat_name_anchor='true']");
    $anchor.text($(this).val());
}

function checkCatNameLegalWhenModifying(){
    let $name = $("#plan_dialog_items_control_group_container").find("[name='cat_name_when_modify']");
    let $container = $name.parents(".plan_dialog_items_unit_container");
    let $errorContainer=   $(".plan_dialog_items_error_mes.items_header");
    if(!$(this).is(":visible")){
        $container.attr("test",true)
       $errorContainer.hide().text("");
        return true;
    }
    let text = $name.val().trim();
    if(text.length == 0){
        $container.attr("test",false);
        $errorContainer.show().text("??????????????????");
        return false;
    }else{
        $container.attr("test",true)
        $errorContainer.hide().text("");
        return true;
    } 
}

function checkPlanBasicInfoNameLegal(){
    let $name = $("#plan_dialog_basic_info_sub_container").find("[name='name']");
    let $container = $name.parents(".plan_dialog_basic_unit_container");
    let text = $name.val();
    if(!checkPlanName(text)){
        $container.attr("test",false);
        $(".plan_dialog_basic_info_form_error_mes.p_name").show().text("1-20?????????");
        return false;
    }else{
        $container.attr("test",true)
        $(".plan_dialog_basic_info_form_error_mes.p_name").hide().text("");
        return true;
    }
}

function checkPlanBaickInfoEndDateLegal(){
    let $endDate = $("#plan_dialog_basic_info_sub_container").find("[name='end_date']");
    let $container = $endDate.parents(".plan_dialog_basic_unit_container");
    if(parseToBool($container.attr("switch_to_null"))){
        return true;
    }
    let text = $endDate.val();
    if(text.length == 0){
        /*???????????? ???????????????*/
        return true;
    }

    if(!checkDateFormat(text)){
        $container.attr("test",false);
        $(".plan_dialog_basic_info_form_error_mes.p_date").show().text("????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        return false;
    }else{
        $container.attr("test",true)
        $(".plan_dialog_basic_info_form_error_mes.p_date").hide().text("");
        return true;
    }
}

function checkPlanBaickInfoStartDateLegal(){
    let $startDate = $("#plan_dialog_basic_info_sub_container").find("[name='start_date']");
    let $container = $startDate.parents(".plan_dialog_basic_unit_container");
    let text = $startDate.val();
    if(!checkDateFormat(text)){
        $container.attr("test",false);
        $(".plan_dialog_basic_info_form_error_mes.p_date").show().text("????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        return false;
    }else{
        $container.attr("test",true)
        $(".plan_dialog_basic_info_form_error_mes.p_date").hide().text("");
        return true;
    }
}


function commitBasicInfo(){
    if(!checkPlanBaickInfoEndDateLegal()
        || !checkPlanBaickInfoStartDateLegal()
        || !checkPlanBasicInfoNameLegal()){
        alertInfo("????????????????????????????????????");
        return;
    }

    let param = $("#plan_dialog_basic_info_form").serializeArray();
    param.push({
        "name":"plan_id",
        "value":$("#plan_dialog").attr("plan_id")
    })

    sendAjax("CareerServlet","c_save_plan",param,(data)=>{
        showForAWhile("????????????",$("#plan_dialog_save_basic_hint_mes"));

        let openEditMode = parseToBool($("#plan_dialog").attr("open_edit_mode")) 
        fillPlanDialogByDate(data,openEditMode);

        if($("#work_sub_main_container").length>0){
            /*???????????????*/
            loadActivePlans();
        }

        if($("#all_plans_sub_main_container").length>0){
            /*????????????????????????*/
            loadPlansByStateCircle();
            loadPlanStatistics();
        }

    });
}


function checkDateFormat(text){
    let dateFormat =/^(\d{4})-(\d{2})-(\d{2})$/;
    return dateFormat.test(text);
}

function switchToShowPlanDialogContainer(dom,showFunc,hideFunc){
    let open = parseToBool($(dom).attr("open"));
    if(open){
        hideFunc();
    }else{
        showFunc();
    }
}

/*??????????????? 1.PlanItems?????????  2.?????????????????? */
function drawPlanItemsFromCache(){
    /*??????????????????*/
    let $dropUpContainer  =$("#plan_dialog_items_control_group_container").find(".dropdown-menu");
    $dropUpContainer.empty();
    let cats = [];
    let items = PLAN_DIALOG_NAMESPACE.CURRENT_PLAN.content.items;
    traversePlanItems(items,(item)=>{
        cats.push({
            "id" : item.id,
            "name" : item.name,
            "type" : item.type.dbCode
        });
    })
    cats.push({
        "id" : 0,
        "name" : "???"
    });

    cats.forEach(cat=>{
        let $dropDown = getDropDownItemButton();
        $dropDown.text(cat.name).attr({
            /*catId??????itemId*/
            "item_id":cat.id,
            "cat_type" :cat.type
        });
        $dropUpContainer.append($dropDown);

    })

    /*PlanItems?????????*/
    let $itemsCardContainer = $("#plan_dialog_items_cards_container");
    let $pattern = $("#plan_dialog_pattern_container");
    $itemsCardContainer.empty();

    let $root = $pattern.find(".plan_item_container_root_container").clone();
    $root.find(".plan_item_container_root_controlgroup").attr("item_id",0);
    $itemsCardContainer.append($root);


    /**????????????????????? */
    let $firstLevelContainer
    for(let i=0 ; i<items.length ;i++){
        $firstLevelContainer = $pattern.find(".plan_item_container_first_level").clone();
        $firstLevelContainer.attr({
            "fold" : items[i].fold
        }).find(".plan_item_container_footer").attr({
            "item_id":items[i].id,
            "father_id" : 0,
            "cat_name" : items[i].name,
            "item_val" : items[i].value,
            "item_note" : items[i].note
        });
        if(items[i].note.length>0){
            $firstLevelContainer.find(".plan_item_container_body").prop("title",items[i].note);
        }

        $firstLevelContainer.find(".plan_item_container_body").html(calculateRootPlanItemText(items[i]));

        drawSonPlanItemCards(items[i],$firstLevelContainer);

        $itemsCardContainer.append($firstLevelContainer);
    }
    /*????????????*/
    if($firstLevelContainer != undefined){
        $firstLevelContainer.addClass("last_one");
    }
}




function drawSonPlanItemCards(fatherItem,$container){
    let items  = fatherItem.descendants;
    let fatherId =  $container.find(".plan_item_container_footer").attr("item_id");

    $container.attr("has_next_level",items.length>0);

    let $firstLevelContainer
    for(let i=0 ; i<items.length ;i++){
        $firstLevelContainer = $("#plan_dialog_pattern_container").find(".plan_item_container_unit_level").clone();
        $firstLevelContainer.attr({
            "fold" : items[i].fold
        }).find(".plan_item_container_footer").attr({
            "item_id":items[i].id,
            "father_id" :fatherId,
            "cat_name" : items[i].name,
            "mapping_val" :items[i].mappingValue,
            "item_note" : items[i].note
        });
        if(items[i].note.length>0){
            $firstLevelContainer.find(".plan_item_container_body").prop("title",items[i].note);
        }

        $firstLevelContainer.find(".plan_item_container_body").html(calculateUnitPlanItem(items[i],fatherItem));
        $container.children(".plan_item_container_wrap").append($firstLevelContainer);

        drawSonPlanItemCards(items[i],$firstLevelContainer);
    }
    /*????????????*/
    if($firstLevelContainer != undefined){
        $firstLevelContainer.addClass("last_one");
    }
}






function fillPlanDialogByDate(data,openEditMode){

    PLAN_DIALOG_NAMESPACE.CURRENT_PLAN = cloneObj(data);

    let $planDialog = $("#plan_dialog");
    if(openEditMode == undefined){
        openEditMode = parseToBool($planDialog.attr("open_edit_mode")); 
    }

   
    drawPlanItemsFromCache();

    /*????????????????????? ??? ????????????????????????*/
    openItemControgroupAddMode();
    $("#plan_dialog_items_control_group_container").find(".dropdown-item[item_id='0']").click();


    $(".plan_dialog_basic_info_form_error_mes,.plan_dialog_items_error_mes").hide();

    let title = data.countWS == 0 ? data.plan.name : "??????<em>"+data.countWS+"</em>??????????????????????????????";

    $planDialog.attr({
        "plan_id":data.plan.id
    }).find(".modal-header .modal-title").html(title);

    $("#plan_dialog_basic_info_form").find("[name='name']").val(data.plan.name)
        .end().find(".plan_dialog_basic_info_state").text(data.plan.state.name).css(getFontColorAndBorderColor(data.plan.state.color));

    $("#plan_dialog_basic_info_form").find("[name='start_date']").attr("abs_time_in_millis",data.plan.startDate).end()
        .find("[name='end_date']").attr("abs_time_in_millis",data.plan.endDate).end()
        .find(".plan_dialog_basic_info_decoded_id").text(data.planId).end()
        .find(".allow_others_copy_plan_items").prop("checked",data.allowOthersCopy).end()
        .find(".plan_dialog_basic_allow_others_copy_plan_items_rlt").text(data.allowOthersCopy?"???":"???").attr("p_setting",data.allowOthersCopy).end()
        .find(".plan_dialog_basic_info_sub_title_seq_weight_span").text(data.plan.seqWeight).end()
        .find("[name='seq_weight']").val(data.plan.seqWeight).end()

    fillTextareaVal(data.plan.note,$("#plan_dialog_basic_info_form").find("[name='note'"));

    drawCommonLogs(data.content.logs,$("#plan_dialog_plan_logs_container"))
    $("#plan_dialog_plan_latest_update_time>span").text(new Date(data.plan.updateTime).toSmartString());

    $tagContainer = $(".plan_dialog_basic_unit_container_plan_tags");
    $tagContainer.empty();
    data.plan.tags.forEach(tag=>{
        let $unit = $("#plan_dialog_pattern_container .plan_dialog_tag_unit_container").clone();
        $unit.find(".plan_dialog_tag_unit_container_context").text(tag.name);
        $tagContainer.append($unit);
    })

    if(openEditMode){
        openPlanDialogEditMode();
    }else{
        closePlanDialogEditMode();
    }
    
}

function openPlanDialogEditMode(){

    $("#plan_dialog").attr("open_edit_mode",true);

    let startDate = new Date(parseInt($("#plan_dialog_basic_info_form").find("[name='start_date']").attr("abs_time_in_millis")));
    let endDate = new Date(parseInt($("#plan_dialog_basic_info_form").find("[name='end_date']").attr("abs_time_in_millis")));
    $("#plan_dialog_basic_info_form").find("[name='start_date']").prop("type","date").val(startDate.getDateStr()).end()
        .find("[name='end_date']").prop("type","date").val(endDate.isBlank() ?"":endDate.getDateStr()).end();
    

    $(".plan_dialog_items_form_one_row.plan_dialog_items_values_container>div").hide();    
    $("#plan_dialog_items_main_container").find("[type='text'],textarea").val("");
    
    $("#plan_dialog_close_edit_mode").show();
    $("#plan_dialog_open_edit_mode").hide();
}


function closePlanDialogEditMode(){

    $("#plan_dialog").attr("open_edit_mode",false);

    let startDate = new Date(parseInt($("#plan_dialog_basic_info_form").find("[name='start_date']").attr("abs_time_in_millis")));
    let endDate = new Date(parseInt($("#plan_dialog_basic_info_form").find("[name='end_date']").attr("abs_time_in_millis")));
    $("#plan_dialog_basic_info_form").find("[name='start_date']").prop("type","text").val(startDate.toChineseDate()).end()
        .find("[name='end_date']").prop("type","text").val(endDate.toChineseDate("??????")).end();

    $(".plan_dialog_basic_unit_container").removeAttr("switch_to_null").removeAttr("test");

    $("#plan_dialog_close_edit_mode").hide();
    $("#plan_dialog_open_edit_mode").show();
}




function openPlanDialog(id,openEditMode,reloadFuncForSyncPlanTags){
    
    sendAjax("CareerServlet","c_load_plan",{
        "plan_id" : id
    },(data)=>openPlanDialogByDate(data,openEditMode,reloadFuncForSyncPlanTags));
}


function openPlanDialogByDate(data,openEditMode,reloadFuncForSyncPlanTags){

    PLAN_DIALOG_NAMESPACE.RELOAD_FUNC_FOR_SYNC_PLAN_TAGS = reloadFuncForSyncPlanTags;

    showPlanDialogBasicInfoContainer();
    showPlanDialogItemsContainer();
    hidePlanDialogLogsContainer();
    fillPlanDialogByDate(data,openEditMode);

    $("#plan_dialog").modal('handleUpdate').modal("show");
}

function showPlanDialogContainer($container){
    $container.find(".plan_dialog_content").show().end()
        .find(".plan_dialog_switch_container_visibility").text("??????").attr("open",true)
        .parents(".plan_dialog_main_container").attr("open_container",true);
}

function hidePlanDialogContainer($container){
    $container.find(".plan_dialog_content").hide().end()
        .find(".plan_dialog_switch_container_visibility").text("??????").attr("open",false)
        .parents(".plan_dialog_main_container").attr("open_container",false);
}


function showPlanDialogBasicInfoContainer(){
    showPlanDialogContainer($("#plan_dialog_basic_info_main_container"));
}

function hidePlanDialogBasicInfoContainer(){
    hidePlanDialogContainer($("#plan_dialog_basic_info_main_container"));
}

function showPlanDialogItemsContainer(){
    showPlanDialogContainer($("#plan_dialog_items_main_container"));
}

function hidePlanDialogItemsContainer(){
    hidePlanDialogContainer($("#plan_dialog_items_main_container"));
}

function showPlanDialogLogsContainer(){
    showPlanDialogContainer($("#plan_dialog_logs_main_container"));
}

function hidePlanDialogLogsContainer(){
    hidePlanDialogContainer($("#plan_dialog_logs_main_container"));
}


