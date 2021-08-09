$(function(){
    showPlanDeptDialogSurplusContainer();
    showPlanDeptDialogCreditContainer();
    hidePlanDeptDialogLogsContainer();

    $("#close_plan_dept_edit_mode_btn").click(closePlanDeptDialogEditMode);
    $("#open_plan_dept_edit_mode_btn").click(openPlanDeptDialogEditMode);

    $("#plan_dept_dialog_surplus_main_container").find(".plan_dept_dialog_switch_container_visibility").click(function(){
        switchToShowPlanDeptDialogContainer(this,showPlanDeptDialogSurplusContainer,hidePlanDeptDialogSurplusContainer)
    })

    $("#plan_dept_dialog_credit_main_container").find(".plan_dept_dialog_switch_container_visibility").click(function(){
        switchToShowPlanDeptDialogContainer(this,showPlanDeptDialogCreditContainer,hidePlanDeptDialogCreditContainer)
    })

    $("#plan_dept_dialog_logs_main_container").find(".plan_dept_dialog_switch_container_visibility").click(function(){
        switchToShowPlanDeptDialogContainer(this,showPlanDeptDialogLogsContainer,hidePlanDeptDialogLogsContainer)
    })    

    $("#plan_dept_dialog").on("input","[name='plan_dept_item_val']",inputOnlyAllowFloat)
        .on("focus","[name='plan_dept_item_name']",getPlanDeptItemNamesForPlanDeptDialog)
        .on("click",".plan_dept_save_modify_item_btn",saveModifyDeptItem);    
    
})

function saveModifyDeptItem(){
    let $container = $(this).parents(".plan_dept_item_container");
    let name = $container.find("[name='plan_dept_item_name']").val();
    if(name.trim().length == 0){
        alertInfo("请填写修改名称");
        return;
    }
    confirmInfo("确定修改吗？",()=>{
        let deptItemId = $container.attr("dept_item_id");
        let val = $container.find("[name='plan_dept_item_val']").val();
        if(val==undefined || val.trim().length==0){
            val = 0;
        }else{
            val = parseFloat(val);
        }
        let isPositive = parseToBool($container.find("[name='plan_dept_item_val']").attr("is_positive"));
        if(isPositive){
            val = (-1)*val;
        }
        sendAjax("CareerServlet","c_save_dept_item",{
            "item_id" : deptItemId,
            "name" : name,
            "val":val
        },fillPlanDeptDialog);
    })
}


function getPlanDeptItemNamesForPlanDeptDialog(){
    sendAjax("CareerServlet","c_load_plan_dept_item_names",{},(data)=>{
        fillAutocompletInput(data,$(this));
    });
}


function openPlanDeptDialogEditMode(){
    $("#plan_dept_dialog").attr("open_edit_mode",true);
}

function closePlanDeptDialogEditMode(){
    $("#plan_dept_dialog").attr("open_edit_mode",false);
}

function mergeDeptItemValueAndType(val,type){
    if(type.dbCode != BASIC_NAMESPACE.WS_ITEM_CAT_TYPE_OF_MINUTES){
        return val.toText()+type.name;
    }

    return val.transferToHoursMesIfPossible();
}

function fillPlanDeptDialog(data){
    let $surplusContainer = $("#plan_dept_dialog_surplus_main_container .plan_dept_dialog_content");
    let $creditContainer = $("#plan_dept_dialog_credit_main_container .plan_dept_dialog_content");

    $surplusContainer.empty();
    $creditContainer.empty();

    data.content.items.forEach(element => {
        let $unit = $("#plan_dept_dialog_pattern_container").find(".plan_dept_item_container").clone();

        $unit.attr(
            "dept_item_id",element.id
        ).find(".plan_dept_item_name").text(element.name).end()
        .find(".plan_dept_modify_item_type").text(element.type.name).end()
        .find("[name='plan_dept_item_name']").autocomplete({
            minLength:0,
            source : []
        }).val(element.name)
        if(element.value > 0){
            

            $unit.find(".plan_dept_item_val_and_type").text(mergeDeptItemValueAndType(element.value,element.type)).end()
                .find("[name='plan_dept_item_val']").val(element.value.toText()).attr("is_positive",false).end()

            $creditContainer.append($unit);
        }else{
            $unit.find(".plan_dept_item_val_and_type").text(mergeDeptItemValueAndType((element.value * -1),element.type)).end()
                .find("[name='plan_dept_item_val']").val((element.value * -1).toText()).attr("is_positive",true).end()

            $surplusContainer.append($unit);
        }
    });

    drawCommonLogs(data.content.logs,$("#plan_dept_dialog_plan_logs_container"))
    $("#plan_dept_dialog_plan_latest_update_time>span").text(new Date(data.dept.updateTime).toSmartString());
}


/*For External Module*/
function openPlanDeptDialog(){
    $("#plan_dept_dialog").modal("show");
 
    closePlanDeptDialogEditMode();

    sendAjax("CareerServlet","c_load_plan_dept",{},fillPlanDeptDialog)
}

function switchToShowPlanDeptDialogContainer(dom,showFunc,hideFunc){
    let open = parseToBool($(dom).attr("open"));
    if(open){
        hideFunc();
    }else{
        showFunc();
    }
}
function showPlanDeptDialogContainer($container){
    $container.find(".plan_dept_dialog_content").show().end()
        .find(".plan_dept_dialog_switch_container_visibility").text("收起").attr("open",true)
        .parents(".plan_dept_dialog_main_container").attr("open_container",true);
}

function hidePlanDeptDialogContainer($container){
    $container.find(".plan_dept_dialog_content").hide().end()
        .find(".plan_dept_dialog_switch_container_visibility").text("展开").attr("open",false)
        .parents(".plan_dept_dialog_main_container").attr("open_container",false);
}

function showPlanDeptDialogSurplusContainer(){
    showPlanDeptDialogContainer($("#plan_dept_dialog_surplus_main_container"));
}

function hidePlanDeptDialogSurplusContainer(){
    hidePlanDeptDialogContainer($("#plan_dept_dialog_surplus_main_container"));
}

function showPlanDeptDialogCreditContainer(){
    showPlanDeptDialogContainer($("#plan_dept_dialog_credit_main_container"));
}

function hidePlanDeptDialogCreditContainer(){
    hidePlanDeptDialogContainer($("#plan_dept_dialog_credit_main_container"));
}

function showPlanDeptDialogLogsContainer(){
    showPlanDeptDialogContainer($("#plan_dept_dialog_logs_main_container"));
}

function hidePlanDeptDialogLogsContainer(){
    hidePlanDeptDialogContainer($("#plan_dept_dialog_logs_main_container"));
}




