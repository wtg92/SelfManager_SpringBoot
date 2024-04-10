/**
 * DONE
 */
$(function(){
    loadPlanStatistics();
    loadPlansByStateCircle();
    //点圆圈
    $(".all_plans_state_unit_container").click(switchToShowPlansByState);

    initAllPlansDataTable();
    $("#all_plans_statics").on("click","tbody tr",openPlanDialogFromDataTable);
})


function openPlanDialogFromDataTable(){
    let planId = $(this).find(".plan_state_span").attr("plan_id");
    openPlanDialog(planId,false);
}

function initAllPlansDataTable(){
    jQuery.extend(jQuery.fn.dataTableExt.oSort,{
        "general-date-sorter-pre":function(dom){
            let dateAbs =  parseInt($(dom).attr("date_abs"));
            if(new Date(dateAbs).isBlank()){
                return new Date().getTime();
            }
            return dateAbs;
        },
        "general-date-sorter-asc":function(a,b){
            return (a<b)? -1 : ((a>b)? 1:0);
        },
        "general-date-sorter-desc":function(a,b){
            return (a<b)? 1 : ((a>b)? -1:0);
        }
    })

    $("#all_plans_statics table").DataTable({
        "oLanguage" : CONFIG.DATATABLE_LANG_CONFIG,
        "columns":[{
            "title":"名称",
        },{
            "title":"状态",
        },{
            "title":"开始日期",
        },{
            "title":"结束日期",
        },{
            "title":"创建日期",
        }],
        "columnDefs":[
            {"sType":"general-date-sorter","aTargets":[2,3,4]}
        ]   
    });
}


function switchToShowPlansByState(){
    let thisClicked = $(this).hasClass("selected_state");
    $(".selected_state").removeClass("selected_state");
    if(!thisClicked){
        $(this).addClass("selected_state");
    }

    loadPlansByStateCircle();
}


function loadPlansByStateCircle(){
    let $hint = $("#all_plans_hint_when_loading");
    $hint.show();

    let state = $(".all_plans_state_unit_container.selected_state") ? $(".all_plans_state_unit_container.selected_state").attr("state_code") : 0;
    sendAjax("CareerServlet","c_load_plans_by_state",{
        state : state
    },(data)=>{
        $hint.hide();

        let linesDate = [];
        data.forEach(plan=>{
            let oneLine = [];
            let $state = $("<span>");
            $state.text(plan.state.name).addClass("common_state_block").addClass("plan_state_span").attr("plan_id",plan.id).css(getFontColorAndBorderColor(plan.state.color));
            
            let $startDate = $("<span>");
            $startDate.attr("date_abs",plan.startDate).text(new Date(plan.startDate).toChineseDate());
            
            let $endDate = $("<span>");
            $endDate.attr("date_abs",plan.endDate).text(new Date(plan.endDate).toChineseDate("至今"));

            let $createTime = $("<span>");
            $createTime.attr("date_abs",plan.createTime).text(new Date(plan.createTime).toChineseDate());

            oneLine.push(plan.name);
            oneLine.push($state.get(0).outerHTML);
            oneLine.push($startDate.get(0).outerHTML);
            oneLine.push($endDate.get(0).outerHTML);
            oneLine.push($createTime.get(0).outerHTML);
            linesDate.push(oneLine);
        })

        let table = $("#all_plans_statics table").DataTable();
        table.clear();
        table.rows.add(linesDate);
        table.draw();
    });

}

function loadPlanStatistics(){
    sendAjax("CareerServlet","c_load_plan_state_statistics",{},(data)=>{
        for(let key in data){
            $(".all_plans_state_unit_container[state_code='"+key+"']").find(".all_plans_unit_count").text(data[key]);
        }
    });
}

